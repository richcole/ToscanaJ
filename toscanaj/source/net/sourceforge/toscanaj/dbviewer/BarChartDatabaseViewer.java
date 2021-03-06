/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import org.jdom.Element;

public class BarChartDatabaseViewer extends PagingDatabaseViewer {
    private class BarChartPanel implements PageViewPanel {
        private List<Serializable> columnDefSQL, columnDefDisplay, panels;
        private List<Color> columnDefLineCol, columnDefMinCol, columnDefMaxCol;

        public Component getComponent() throws DatabaseViewerException {

            this.columnDefSQL = new ArrayList<Serializable>();
            this.columnDefDisplay = new ArrayList<Serializable>();
            this.columnDefLineCol = new ArrayList<Color>();
            this.columnDefMaxCol = new ArrayList<Color>();
            this.columnDefMinCol = new ArrayList<Color>();

            this.panels = new ArrayList<Serializable>();

            final DatabaseViewerManager viewerManager = getManager();
            final Element template = viewerManager.getTemplate();
            final List<Element> columnElements = template.getChildren("column");
            for (final Element columnElement : columnElements) {
                if (columnElement.getAttributeValue("sqlname") == null) {
                    throw new DatabaseViewerException();
                } else {
                    this.columnDefSQL.add(columnElement
                            .getAttributeValue("sqlname"));
                }
                if (columnElement.getAttributeValue("displayname") == null) {
                    throw new DatabaseViewerException();
                } else {
                    this.columnDefDisplay.add(columnElement
                            .getAttributeValue("displayname"));
                }
                if (columnElement.getAttributeValue("linecolor") == null) {
                    this.columnDefLineCol.add(new Color(255, 255, 255));
                } else {
                    try {
                        this.columnDefLineCol.add(Color.decode(columnElement
                                .getAttributeValue("linecolor")));
                    } catch (final Exception e) {
                        System.err.println("Invalid linecolor code for "
                                + columnElement
                                        .getAttributeValue("displayname")
                                + ". Using Default setting.");
                        this.columnDefLineCol.add(new Color(255, 255, 255));
                    }
                }
                if (columnElement.getAttributeValue("maxcolor") == null) {
                    this.columnDefMaxCol.add(new Color(255, 0, 255));
                } else {
                    try {
                        this.columnDefMaxCol.add(Color.decode(columnElement
                                .getAttributeValue("maxcolor")));
                    } catch (final Exception e) {
                        System.err.println("Invalid maxcolor code for "
                                + columnElement
                                        .getAttributeValue("displayname")
                                + ". Using Default setting.");
                        this.columnDefMaxCol.add(new Color(255, 0, 255));
                    }
                }
                if (columnElement.getAttributeValue("mincolor") == null) {
                    this.columnDefMinCol.add(new Color(0, 255, 0));
                } else {
                    try {
                        this.columnDefMinCol.add(Color.decode(columnElement
                                .getAttributeValue("mincolor")));
                    } catch (final Exception e) {
                        System.err.println("Invalid mincolor code for "
                                + columnElement
                                        .getAttributeValue("displayname")
                                + ". Using Default setting.");
                        this.columnDefMinCol.add(new Color(0, 255, 0));
                    }
                }
            }

            BarContainer tmpBC;

            final JPanel barChartPanel = new JPanel();
            barChartPanel.setLayout(new BoxLayout(barChartPanel,
                    BoxLayout.Y_AXIS));
            for (int i = 0; i < this.columnDefSQL.size(); i++) {
                tmpBC = new BarContainer();
                tmpBC.setColors(this.columnDefMinCol.get(i),
                        this.columnDefMaxCol.get(i), this.columnDefLineCol
                                .get(i));
                this.panels.add(tmpBC);
                barChartPanel.add(tmpBC);
            }

            return barChartPanel;
        }

        public void showItem(final String keyValue)
                throws DatabaseViewerException {
            final DatabaseViewerManager viewerManager = getManager();
            List<String[]> tmpList;

            try {
                for (int i = 0; i < this.columnDefSQL.size(); i++) {

                    String tmpS = (String) this.columnDefSQL.get(i);
                    tmpList = (viewerManager.getConnection()
                            .executeQuery("SELECT " + tmpS + " FROM "
                                    + viewerManager.getTableName()
                                    + " ORDER BY " + tmpS + ";"));

                    final float min = Float.parseFloat((tmpList.get(0))[0]);
                    final float max = Float.parseFloat((tmpList.get(tmpList
                            .size() - 1))[0]);
                    ((BarContainer) this.panels.get(i)).setList(tmpList);
                    tmpList = (viewerManager.getConnection()
                            .executeQuery("SELECT " + tmpS + " FROM "
                                    + viewerManager.getTableName() + " WHERE "
                                    + viewerManager.getKeyName() + " = '"
                                    + keyValue + "';"));

                    final float cur = Float.parseFloat((tmpList.get(0))[0]);
                    ((BarContainer) this.panels.get(i)).setInfo(cur, min, max,
                            (String) this.columnDefDisplay.get(i));

                }

            } catch (final DatabaseException e) {
                throw new DatabaseViewerException("Could not query database", e);
            }

        }
    }

    private class BarContainer extends JPanel {
        private final JLabel minLabel;
        private final JLabel maxLabel;
        private final JLabel sqlColLabel;
        private final JPanel labelPanel;
        private final PrettyPanel prettyPanel;

        public BarContainer() {
            this.labelPanel = new JPanel();
            this.minLabel = new JLabel("min");
            this.maxLabel = new JLabel("max");
            this.sqlColLabel = new JLabel();
            this.labelPanel.setLayout(new BoxLayout(this.labelPanel,
                    BoxLayout.X_AXIS));
            this.labelPanel.add(this.minLabel);
            this.labelPanel.add(Box.createHorizontalGlue());
            this.labelPanel.add(this.sqlColLabel);
            this.labelPanel.add(Box.createHorizontalGlue());
            this.labelPanel.add(this.maxLabel);

            this.prettyPanel = new PrettyPanel();

            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.labelPanel.setBorder(BorderFactory.createBevelBorder(1));
            this.prettyPanel.setBorder(BorderFactory.createBevelBorder(1));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(this.labelPanel);
            this.add(this.prettyPanel);

        }

        public void setInfo(final float cur, final float min, final float max,
                final String what) {
            this.minLabel.setText(min + "");
            this.maxLabel.setText(max + "");
            this.sqlColLabel.setText(what);
            this.prettyPanel.setInfo(cur, min, max);
            repaint();

        }

        public void setList(final List<String[]> theList) {
            this.prettyPanel.setList(theList);
        }

        public void setColors(final Color min, final Color max, final Color line) {
            this.prettyPanel.setColors(min, max, line);
        }

        private class PrettyPanel extends JPanel {
            private float theCur;
            private float theMax;
            private float theMin;
            private List<String[]> data;
            private Color lineCol;
            private Color maxCol;
            private Color minCol;
            private boolean drawHover = false;

            private int hoverPos = -1;

            public PrettyPanel() {
                super();
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(final MouseEvent evt) {
                        PrettyPanel.this.hoverPos = evt.getX();
                        repaint();
                    }
                });
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(final MouseEvent evt) {
                        PrettyPanel.this.hoverPos = evt.getX();
                        PrettyPanel.this.drawHover = !PrettyPanel.this.drawHover;
                        repaint();
                    }
                });

            }

            public void setInfo(final float cur, final float min,
                    final float max) {
                this.theCur = cur;
                this.theMax = max;
                this.theMin = min;
                repaint();
            }

            public void setList(final List<String[]> theList) {
                this.data = theList;
            }

            @Override
            public void paint(final Graphics g) {
                final int Y_OFFSET = 10; // vert space from edge of panel before
                // lines appear
                final int LINE_OUTER_SIZE = 3; // outer line size for line
                // indicating 'current'
                final int LINE_INNER_SIZE = 1; // inner line size for line
                // indicating 'current'

                super.paint(g);

                final Graphics2D g2 = (Graphics2D) g;
                final RenderingHints qualityHints = new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                final float dist = this.theMax - this.theMin;
                float vrel, vrelold;
                for (int i = 1; i < this.data.size(); i++) {
                    vrel = Float.parseFloat(this.data.get(i)[0]) - this.theMin;
                    vrelold = Float.parseFloat(this.data.get(i - 1)[0])
                            - this.theMin;

                    g2.setColor(new Color(
                                    (int) ((this.minCol.getRed() * (1 - (vrel / dist))) + (this.maxCol
                                            .getRed() * (vrel / dist))),
                                    (int) ((this.minCol.getGreen() * (1 - (vrel / dist))) + (this.maxCol
                                            .getGreen() * (vrel / dist))),
                                    (int) ((this.minCol.getBlue() * (1 - (vrel / dist))) + (this.maxCol
                                            .getBlue() * (vrel / dist)))));

                    g2.fillRect(
                                    (int) ((vrelold / dist) * this.getWidth()),
                                    0,
                                    ((int) ((vrel / dist) * this.getWidth()) - (int) ((vrelold / dist) * this
                                            .getWidth())), this.getHeight() - 1);
                    g2.setColor(new Color(this.lineCol.getRed(), this.lineCol
                            .getGreen(), this.lineCol.getBlue(), 127));
                    if (i > 1) {
                        g2.drawLine((int) ((vrelold / dist) * this.getWidth()),
                                Y_OFFSET, (int) ((vrelold / dist) * this
                                        .getWidth()), (this.getHeight())
                                        - Y_OFFSET);
                    }
                }

                final int curpoint = (int) ((this.theCur - this.theMin) * ((this.getWidth() - 1) / (dist)));
                g2.setColor(Color.white);

                g2.setStroke(new BasicStroke(LINE_OUTER_SIZE));
                g2.drawLine(curpoint, Y_OFFSET, curpoint, (this.getHeight())
                        - Y_OFFSET);
                g2.setColor(Color.black);

                g2.setStroke(new BasicStroke(LINE_INNER_SIZE));
                g2.drawLine(curpoint, Y_OFFSET, curpoint, (this.getHeight())
                        - Y_OFFSET);

                final Font font2Use = new Font(getFont().getFontName(),
                        Font.PLAIN, getFont().getSize() + 4);
                g2.setFont(font2Use);
                TextLayout text = new TextLayout("" + this.theCur, font2Use,
                        new FontRenderContext(null, true, true));
                Rectangle2D bounds = text.getBounds();

                final int TEXT_BOX_BUFFER_SIZE = 3; // set the space around
                // rendered text

                if (curpoint <= getWidth() / 2) {
                    g2.setColor(Color.white);
                    g2.fillRect(curpoint + TEXT_BOX_BUFFER_SIZE,
                            (getHeight() / 2) - 1 - (int) bounds.getHeight()
                                    - TEXT_BOX_BUFFER_SIZE, (int) bounds
                                    .getWidth()
                                    + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds
                                    .getHeight()
                                    + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.setColor(Color.black);
                    g2.drawRect(curpoint + TEXT_BOX_BUFFER_SIZE,
                            (getHeight() / 2) - 1 - (int) bounds.getHeight()
                                    - TEXT_BOX_BUFFER_SIZE, (int) bounds
                                    .getWidth()
                                    + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds
                                    .getHeight()
                                    + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.drawString("" + this.theCur, curpoint
                            + TEXT_BOX_BUFFER_SIZE * 2, getHeight() / 2);
                } else {
                    g2.setColor(Color.white);
                    g2.fillRect(
                                    (int) (curpoint - bounds.getWidth() - TEXT_BOX_BUFFER_SIZE * 3),
                                    (getHeight() / 2) - 1
                                            - (int) bounds.getHeight()
                                            - TEXT_BOX_BUFFER_SIZE,
                                    (int) bounds.getWidth()
                                            + TEXT_BOX_BUFFER_SIZE * 2,
                                    (int) bounds.getHeight()
                                            + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.setColor(Color.black);
                    g2.drawRect(
                                    (int) (curpoint - bounds.getWidth() - TEXT_BOX_BUFFER_SIZE * 3),
                                    (getHeight() / 2) - 1
                                            - (int) bounds.getHeight()
                                            - TEXT_BOX_BUFFER_SIZE,
                                    (int) bounds.getWidth()
                                            + TEXT_BOX_BUFFER_SIZE * 2,
                                    (int) bounds.getHeight()
                                            + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.drawString("" + this.theCur, (int) (curpoint
                            - bounds.getWidth() - TEXT_BOX_BUFFER_SIZE * 2),
                            getHeight() / 2);
                }

                final int hoverVal = (int) ((this.hoverPos / ((this.getWidth() - 1) / (this.theMax - this.theMin))) + this.theMin);

                text = new TextLayout("" + hoverVal, font2Use,
                        new FontRenderContext(null, true, true));
                bounds = text.getBounds();

                if (this.drawHover
                        && this.hoverPos > bounds.getWidth() / 2
                        && this.hoverPos < this.getWidth()
                                - (bounds.getWidth() / 2)) {
                    g2.setColor(Color.white);
                    g2.fillRect(
                                    (int) (this.hoverPos - TEXT_BOX_BUFFER_SIZE - bounds
                                            .getWidth() / 2), (getHeight() / 2)
                                            - 1 + TEXT_BOX_BUFFER_SIZE,
                                    (int) bounds.getWidth()
                                            + TEXT_BOX_BUFFER_SIZE * 2,
                                    (int) bounds.getHeight()
                                            + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.setColor(Color.black);
                    g2.drawRect(
                                    (int) (this.hoverPos - TEXT_BOX_BUFFER_SIZE - bounds
                                            .getWidth() / 2), (getHeight() / 2)
                                            - 1 + TEXT_BOX_BUFFER_SIZE,
                                    (int) bounds.getWidth()
                                            + TEXT_BOX_BUFFER_SIZE * 2,
                                    (int) bounds.getHeight()
                                            + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.drawString("" + hoverVal, (int) (this.hoverPos - bounds
                            .getWidth() / 2), (getHeight() / 2)
                            + (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE
                            * 2);
                }
            }

            public void setColors(final Color min, final Color max,
                    final Color line) {
                this.minCol = min;
                this.maxCol = max;
                this.lineCol = line;
            }
        }
    }

    @Override
    protected PageViewPanel createPanel() {
        return new BarChartPanel();
    }
}
