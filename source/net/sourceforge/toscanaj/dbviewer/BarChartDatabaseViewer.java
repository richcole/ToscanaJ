/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import org.jdom.Element;

public class BarChartDatabaseViewer extends PagingDatabaseViewer {
    private JPanel barChartPanel;
    private ArrayList columnDefSQL, columnDefDisplay, panels;
    private ArrayList columnDefLineCol, columnDefMinCol, columnDefMaxCol;


    protected Component getPanel() throws DatabaseViewerInitializationException {

        columnDefSQL = new ArrayList();
        columnDefDisplay = new ArrayList();
        columnDefLineCol = new ArrayList();
        columnDefMaxCol = new ArrayList();
        columnDefMinCol = new ArrayList();

        panels = new ArrayList();


        DatabaseViewerManager viewerManager = getManager();
        Element template = viewerManager.getTemplate();
        List columnElements = template.getChildren("column");
        for (Iterator iterator = columnElements.iterator(); iterator.hasNext();) {
            Element columnElement = (Element) iterator.next();
            if (columnElement.getAttributeValue("sqlname") == null) {
                throw new DatabaseViewerInitializationException();
            } else {
                columnDefSQL.add(columnElement.getAttributeValue("sqlname"));
            }
            if (columnElement.getAttributeValue("displayname") == null) {
                throw new DatabaseViewerInitializationException();
            } else {
                columnDefDisplay.add(columnElement.getAttributeValue("displayname"));
            }
            if (columnElement.getAttributeValue("linecolor") == null) {
                columnDefLineCol.add(new Color(255, 255, 255));
            } else {
                try {
                    columnDefLineCol.add(Color.decode(columnElement.getAttributeValue("linecolor")));
                } catch (Exception e) {
                    System.err.println("Invalid linecolor code for " + columnElement.getAttributeValue("displayname") + ". Using Default setting.");
                    columnDefLineCol.add(new Color(255, 255, 255));
                }
            }
            if (columnElement.getAttributeValue("maxcolor") == null) {
                columnDefMaxCol.add(new Color(255, 0, 255));
            } else {
                try {
                    columnDefMaxCol.add(Color.decode(columnElement.getAttributeValue("maxcolor")));
                } catch (Exception e) {
                    System.err.println("Invalid maxcolor code for " + columnElement.getAttributeValue("displayname") + ". Using Default setting.");
                    columnDefMaxCol.add(new Color(255, 0, 255));
                }
            }
            if (columnElement.getAttributeValue("mincolor") == null) {
                columnDefMinCol.add(new Color(0, 255, 0));
            } else {
                try {
                    columnDefMinCol.add(Color.decode(columnElement.getAttributeValue("mincolor")));
                } catch (Exception e) {
                    System.err.println("Invalid mincolor code for " + columnElement.getAttributeValue("displayname") + ". Using Default setting.");
                    columnDefMinCol.add(new Color(0, 255, 0));
                }
            }
        }

        BarContainer tmpBC;

        barChartPanel = new JPanel();
        barChartPanel.setLayout(new BoxLayout(barChartPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < columnDefSQL.size(); i++) {
            tmpBC = new BarContainer();
            tmpBC.setColors((Color) columnDefMinCol.get(i), (Color) columnDefMaxCol.get(i), (Color) columnDefLineCol.get(i));
            panels.add(tmpBC);
            barChartPanel.add(tmpBC);
        }

        return barChartPanel;
    }

    protected void showItem(String keyValue) {
        DatabaseViewerManager viewerManager = getManager();
        List tmpList;

        String tmpS = "";
        try {
            for (int i = 0; i < columnDefSQL.size(); i++) {

                tmpS = (String) columnDefSQL.get(i);
                tmpList = viewerManager.getConnection().executeQuery(
                        "SELECT min(" + tmpS + "), max(" + tmpS + "), avg(" + tmpS + ") " +
                        "FROM " + viewerManager.getTableName() + ";");
                ((BarContainer) panels.get(i)).setInfo(
                        Float.parseFloat((String) ((Vector) (((viewerManager.getConnection().executeQuery(
                                "SELECT " + tmpS +
                        " FROM " + viewerManager.getTableName() +
                        " WHERE " + viewerManager.getKeyName() + "='" + keyValue + "';")).get(0)))).firstElement()),
                        Float.parseFloat((String) ((Vector) tmpList.get(0)).elementAt(0)),
                        Float.parseFloat((String) ((Vector) tmpList.get(0)).elementAt(1)),
                        Float.parseFloat((String) ((Vector) (((viewerManager.getConnection().executeQuery(
                                "SELECT sum(" + tmpS +
                        ") FROM " + viewerManager.getTableName() + ";")).get(0)))).firstElement()),
                        (String) columnDefDisplay.get(i));
                tmpList = (viewerManager.getConnection().executeQuery(
                        "SELECT " + tmpS +
                        " FROM " + viewerManager.getTableName() + ";"));
                ((BarContainer) panels.get(i)).setVector(tmpList);

            }

        } catch (DatabaseException e) {
            System.out.println(e);
        }

    }

    private class BarContainer extends JPanel {
        private JLabel minLabel;
        private JLabel maxLabel;
        private JLabel whatLabel;
        private JPanel labelPanel;
        private PrettyPanel prettyPanel;

        public void setWhat(String str) {
            whatLabel.setText(str);
        }

        public BarContainer() {
            labelPanel = new JPanel();
            minLabel = new JLabel("min");
            maxLabel = new JLabel("max");
            whatLabel = new JLabel();
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
            labelPanel.add(minLabel);
            labelPanel.add(Box.createHorizontalGlue());
            labelPanel.add(whatLabel);
            labelPanel.add(Box.createHorizontalGlue());
            labelPanel.add(maxLabel);

            prettyPanel = new PrettyPanel();

            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            labelPanel.setBorder(BorderFactory.createBevelBorder(1));
            prettyPanel.setBorder(BorderFactory.createBevelBorder(1));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(labelPanel);
            this.add(prettyPanel);

        }

        public void setInfo(float cur, float min, float max, float sum, String what) {
            //ADD EXTRA stuff like where to draw this slop!
            minLabel.setText(min + "");
            maxLabel.setText(max + "");
            whatLabel.setText(what);
            prettyPanel.setInfo(cur, min, max, sum);
            repaint();

        }

        public void setVector(List theList) {
            prettyPanel.setVector(theList);
        }

        public void setColors(Color min, Color max, Color line) {
            prettyPanel.setColors(min, max, line);
        }

        private class PrettyPanel extends JPanel {
            private float theCur;
            private float theMax;
            private float theMin;
            private float theSum;
            private List data;
            private Color lineCol;
            private Color maxCol;
            private Color minCol;
            private boolean drawHover = false;

            private int hoverPos = -1;

            public PrettyPanel() {
                super();
                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseMoved(MouseEvent evt) {
                        hoverPos = evt.getX();
                        repaint();
                    }
                }
                );
                addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent evt) {
                        hoverPos = evt.getX();
                        drawHover = !drawHover;
                        repaint();
                    }
                }
                );

            }

            public void setInfo(float cur, float min, float max, float sum) {
                theCur = cur;
                theMax = max;
                theMin = min;
                theSum = sum;
                repaint();
            }

            public void setVector(List theList) {
                data = theList;
            }

            public void paint(Graphics g) {
                super.paint(g);

                Graphics2D g2 = (Graphics2D) g;
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                float intensity = 0;
                float acumm = 0;
                System.out.println(data.size() + "");
                for (int i = 0; i < data.size(); i++) {
                    float current = Float.parseFloat((String) ((Vector) data.get(i)).elementAt(0));
                    intensity += current / theSum;
                    current *= getWidth() / theSum;
                    acumm += current;

                    g2.setColor(new Color(
                            (int) ((minCol.getRed() * intensity) + (maxCol.getRed() * (1 - intensity))),
                            (int) ((minCol.getGreen() * intensity) + (maxCol.getGreen() * (1 - intensity))),
                            (int) ((minCol.getBlue() * intensity) + (maxCol.getBlue() * (1 - intensity)))
                    ));
                    g2.fillRect((int) (acumm - current), 0, (int) acumm, this.getHeight() - 1);
                    g2.setColor(new Color(lineCol.getRed(), lineCol.getGreen(), lineCol.getBlue(), 127));
                    g2.drawLine((int) (acumm - current), 10, (int) (acumm - current), (this.getHeight()) - 10);
                }
                System.out.println("acumm = " + acumm);
                System.out.println("intensity = " + intensity);


                int cutpoint = (int) ((theCur - theMin) * ((this.getWidth() - 1) / (theMax - theMin)));
                g2.setColor(Color.white);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(cutpoint, 11, cutpoint, (this.getHeight()) - 11);
                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(cutpoint, 10, cutpoint, (this.getHeight()) - 10);

                Font font2Use = new Font(getFont().getFontName(), Font.PLAIN, getFont().getSize() + 4);
                g2.setFont(font2Use);
                TextLayout text = new TextLayout("" + theCur, font2Use, new FontRenderContext(null, true, true));
                Rectangle2D bounds = text.getBounds();

                if (cutpoint <= getWidth() / 2) {
                    g2.setColor(Color.white);
                    g2.fillRect(cutpoint + 3, (getHeight() / 2) - (int) bounds.getHeight() - 3, (int) bounds.getWidth() + 6, (int) bounds.getHeight() + 4);
                    g2.setColor(Color.black);
                    g2.drawRect(cutpoint + 3, (getHeight() / 2) - (int) bounds.getHeight() - 3, (int) bounds.getWidth() + 6, (int) bounds.getHeight() + 4);
                    g2.drawString("" + theCur, cutpoint + 6, getHeight() / 2);
                } else {
                    g2.setColor(Color.white);
                    g2.fillRect((int) (cutpoint - bounds.getWidth() - 9), (getHeight() / 2) - (int) bounds.getHeight() - 3, (int) bounds.getWidth() + 6, (int) bounds.getHeight() + 4);
                    g2.setColor(Color.black);
                    g2.drawRect((int) (cutpoint - bounds.getWidth() - 9), (getHeight() / 2) - (int) bounds.getHeight() - 3, (int) bounds.getWidth() + 6, (int) bounds.getHeight() + 4);
                    g2.drawString("" + theCur, (int) (cutpoint - bounds.getWidth() - 6), getHeight() / 2);
                }

                int hoverVal = (int) ((hoverPos / ((this.getWidth() - 1) / (theMax - theMin))) + theMin);

                text = new TextLayout("" + hoverVal, font2Use, new FontRenderContext(null, true, true));
                bounds = text.getBounds();

                if (drawHover && hoverPos > bounds.getWidth() / 2 && hoverPos < this.getWidth() - (bounds.getWidth() / 2)) {
                    g2.setColor(Color.white);
                    g2.fillRect((int) (hoverPos - 3 - bounds.getWidth() / 2), (getHeight() / 2) + 3, (int) bounds.getWidth() + 6, (int) bounds.getHeight() + 4);
                    g2.setColor(Color.black);
                    g2.drawRect((int) (hoverPos - 3 - bounds.getWidth() / 2), (getHeight() / 2) + 3, (int) bounds.getWidth() + 6, (int) bounds.getHeight() + 4);
                    g2.drawString("" + hoverVal, (int) (hoverPos - bounds.getWidth() / 2), (getHeight() / 2) + (int) bounds.getHeight() + 6);
                }


            }

            public void setColors(Color min, Color max, Color line) {
                minCol = min;
                maxCol = max;
                lineCol = line;
            }

        }
    }

}
