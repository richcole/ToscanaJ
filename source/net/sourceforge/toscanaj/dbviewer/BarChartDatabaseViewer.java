/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import org.jdom.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BarChartDatabaseViewer extends PagingDatabaseViewer {
	private class BarChartPanel implements PageViewPanel {
		private List columnDefSQL, columnDefDisplay, panels;
		private List columnDefLineCol, columnDefMinCol, columnDefMaxCol;

	    public Component getComponent() throws DatabaseViewerInitializationException {
	
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
	
	        JPanel barChartPanel = new JPanel();
	        barChartPanel.setLayout(new BoxLayout(barChartPanel, BoxLayout.Y_AXIS));
	        for (int i = 0; i < columnDefSQL.size(); i++) {
	            tmpBC = new BarContainer();
	            tmpBC.setColors((Color) columnDefMinCol.get(i), (Color) columnDefMaxCol.get(i), (Color) columnDefLineCol.get(i));
	            panels.add(tmpBC);
	            barChartPanel.add(tmpBC);
	        }
	
	        return barChartPanel;
	    }
	
	    public void showItem(String keyValue) {
			DatabaseViewerManager viewerManager = getManager();
	        List tmpList;
	
	        String tmpS = "";
	        try {
	            for (int i = 0; i < columnDefSQL.size(); i++) {
	
	                tmpS = (String) columnDefSQL.get(i);
	                tmpList = (viewerManager.getConnection().executeQuery(
	                        "SELECT " + tmpS +
	                        " FROM " + viewerManager.getTableName() +
	                        " ORDER BY " + tmpS + ";"));
	
	                float min = Float.parseFloat((String) ((Vector) (tmpList.get(0))).elementAt(0));
	                float max = Float.parseFloat((String) ((Vector) (tmpList.get(tmpList.size() - 1))).elementAt(0));
	                ((BarContainer) panels.get(i)).setList(tmpList);
	                tmpList = (viewerManager.getConnection().executeQuery(
	                        "SELECT " + tmpS +
	                        " FROM " + viewerManager.getTableName() +
	                        " WHERE " + viewerManager.getKeyName() + " = '" + keyValue + "';"));
	
	                float cur = Float.parseFloat((String) ((Vector) (tmpList.get(0))).elementAt(0));
	                ((BarContainer) panels.get(i)).setInfo(cur, min, max, (String) columnDefDisplay.get(i));
	
	
	            }
	
	        } catch (DatabaseException e) {
	            throw new RuntimeException("Could not query database", e);
	        }
	
	    }
	}
	
    private class BarContainer extends JPanel {
        private JLabel minLabel;
        private JLabel maxLabel;
        private JLabel sqlColLabel;
        private JPanel labelPanel;
        private PrettyPanel prettyPanel;

        public BarContainer() {
            labelPanel = new JPanel();
            minLabel = new JLabel("min");
            maxLabel = new JLabel("max");
            sqlColLabel = new JLabel();
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
            labelPanel.add(minLabel);
            labelPanel.add(Box.createHorizontalGlue());
            labelPanel.add(sqlColLabel);
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

        public void setInfo(float cur, float min, float max, String what) {
            minLabel.setText(min + "");
            maxLabel.setText(max + "");
            sqlColLabel.setText(what);
            prettyPanel.setInfo(cur, min, max);
            repaint();

        }

        public void setList(List theList) {
            prettyPanel.setList(theList);
        }

        public void setColors(Color min, Color max, Color line) {
            prettyPanel.setColors(min, max, line);
        }

        private class PrettyPanel extends JPanel {
            private float theCur;
            private float theMax;
            private float theMin;
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

            public void setInfo(float cur, float min, float max) {
                theCur = cur;
                theMax = max;
                theMin = min;
                repaint();
            }

            public void setList(List theList) {
                data = theList;
            }

            public void paint(Graphics g) {
                final int Y_OFFSET = 10; //vert space from edge of panel before lines appear
                final int LINE_OUTER_SIZE = 3; //outer line size for line indicating 'current'
                final int LINE_INNER_SIZE = 1; //inner line size for line indicating 'current'

                super.paint(g);

                Graphics2D g2 = (Graphics2D) g;
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                float dist = theMax - theMin;
                float vrel, vrelold;
                for (int i = 1; i < data.size(); i++) {
                    vrel = Float.parseFloat((String) ((Vector) data.get(i)).elementAt(0)) - theMin;
                    vrelold = Float.parseFloat((String) ((Vector) data.get(i - 1)).elementAt(0)) - theMin;


                    g2.setColor(new Color(
                            (int) ((minCol.getRed() * (1 - (vrel / dist))) + (maxCol.getRed() * (vrel / dist))),
                            (int) ((minCol.getGreen() * (1 - (vrel / dist))) + (maxCol.getGreen() * (vrel / dist))),
                            (int) ((minCol.getBlue() * (1 - (vrel / dist))) + (maxCol.getBlue() * (vrel / dist)))
                    ));

                    g2.fillRect((int) ((vrelold / dist) * this.getWidth()), 0, ((int) ((vrel / dist) * this.getWidth()) - (int) ((vrelold / dist) * this.getWidth())), this.getHeight() - 1);
                    g2.setColor(new Color(lineCol.getRed(), lineCol.getGreen(), lineCol.getBlue(), 127));
                    if (i > 1) g2.drawLine((int) ((vrelold / dist) * this.getWidth()), Y_OFFSET, (int) ((vrelold / dist) * this.getWidth()), (this.getHeight()) - Y_OFFSET);
                }


                int curpoint = (int) ((theCur - theMin) * ((this.getWidth() - 1) / (dist)));
                g2.setColor(Color.white);

                g2.setStroke(new BasicStroke(LINE_OUTER_SIZE));
                g2.drawLine(curpoint, Y_OFFSET, curpoint, (this.getHeight()) - Y_OFFSET);
                g2.setColor(Color.black);

                g2.setStroke(new BasicStroke(LINE_INNER_SIZE));
                g2.drawLine(curpoint, Y_OFFSET, curpoint, (this.getHeight()) - Y_OFFSET);

                Font font2Use = new Font(getFont().getFontName(), Font.PLAIN, getFont().getSize() + 4);
                g2.setFont(font2Use);
                TextLayout text = new TextLayout("" + theCur, font2Use, new FontRenderContext(null, true, true));
                Rectangle2D bounds = text.getBounds();

                final int TEXT_BOX_BUFFER_SIZE = 3; //set the space around rendered text

                if (curpoint <= getWidth() / 2) {
                    g2.setColor(Color.white);
                    g2.fillRect(curpoint + TEXT_BOX_BUFFER_SIZE, (getHeight() / 2) - 1 - (int) bounds.getHeight() - TEXT_BOX_BUFFER_SIZE, (int) bounds.getWidth() + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.setColor(Color.black);
                    g2.drawRect(curpoint + TEXT_BOX_BUFFER_SIZE, (getHeight() / 2) - 1 - (int) bounds.getHeight() - TEXT_BOX_BUFFER_SIZE, (int) bounds.getWidth() + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.drawString("" + theCur, curpoint + TEXT_BOX_BUFFER_SIZE * 2, getHeight() / 2);
                } else {
                    g2.setColor(Color.white);
                    g2.fillRect((int) (curpoint - bounds.getWidth() - TEXT_BOX_BUFFER_SIZE * 3), (getHeight() / 2) - 1 - (int) bounds.getHeight() - TEXT_BOX_BUFFER_SIZE, (int) bounds.getWidth() + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.setColor(Color.black);
                    g2.drawRect((int) (curpoint - bounds.getWidth() - TEXT_BOX_BUFFER_SIZE * 3), (getHeight() / 2) - 1 - (int) bounds.getHeight() - TEXT_BOX_BUFFER_SIZE, (int) bounds.getWidth() + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.drawString("" + theCur, (int) (curpoint - bounds.getWidth() - TEXT_BOX_BUFFER_SIZE * 2), getHeight() / 2);
                }

                int hoverVal = (int) ((hoverPos / ((this.getWidth() - 1) / (theMax - theMin))) + theMin);

                text = new TextLayout("" + hoverVal, font2Use, new FontRenderContext(null, true, true));
                bounds = text.getBounds();

                if (drawHover && hoverPos > bounds.getWidth() / 2 && hoverPos < this.getWidth() - (bounds.getWidth() / 2)) {
                    g2.setColor(Color.white);
                    g2.fillRect((int) (hoverPos - TEXT_BOX_BUFFER_SIZE - bounds.getWidth() / 2), (getHeight() / 2) - 1 + TEXT_BOX_BUFFER_SIZE, (int) bounds.getWidth() + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.setColor(Color.black);
                    g2.drawRect((int) (hoverPos - TEXT_BOX_BUFFER_SIZE - bounds.getWidth() / 2), (getHeight() / 2) - 1 + TEXT_BOX_BUFFER_SIZE, (int) bounds.getWidth() + TEXT_BOX_BUFFER_SIZE * 2, (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                    g2.drawString("" + hoverVal, (int) (hoverPos - bounds.getWidth() / 2), (getHeight() / 2) + (int) bounds.getHeight() + TEXT_BOX_BUFFER_SIZE * 2);
                }
            }

            public void setColors(Color min, Color max, Color line) {
                minCol = min;
                maxCol = max;
                lineCol = line;
            }
        }
    }

	protected PageViewPanel createPanel() {
		return new BarChartPanel();
	}
}
