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
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import org.jdom.Element;

public class BarChartDatabaseViewer extends PagingDatabaseViewer {
    private JPanel barChartPanel;
    private ArrayList columnDefSQL, columnDefDisplay, panels;


    protected Component getPanel() throws DatabaseViewerInitializationException {

        columnDefSQL = new ArrayList();
        columnDefDisplay = new ArrayList();
        panels = new ArrayList();


        DatabaseViewerManager viewerManager = getManager();
        Element template = viewerManager.getTemplate();
        List columnElements = template.getChildren("column");
        for (Iterator iterator = columnElements.iterator(); iterator.hasNext();) {
            Element columnElement = (Element) iterator.next();
            columnDefSQL.add(columnElement.getAttributeValue("sqlname"));
            columnDefDisplay.add(columnElement.getAttributeValue("displayname"));
        }

        BarContainer tmpBC;

        barChartPanel = new JPanel();
        barChartPanel.setLayout(new BoxLayout(barChartPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < columnDefSQL.size(); i++) {
            tmpBC = new BarContainer();
            panels.add(tmpBC);
            barChartPanel.add(tmpBC);
        }

        return barChartPanel;
    }

    private void getData(String keyValue) {

    }

    protected void showItem(String keyValue) {
        DatabaseViewerManager viewerManager = getManager();
        List tmpList;
        Vector tmpVector;

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
                        Float.parseFloat((String) ((Vector) tmpList.get(0)).elementAt(2)),
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

        public void setInfo(float cur, float min, float max, float avg, float sum, String what) {
            //ADD EXTRA stuff like where to draw this slop!
            minLabel.setText(min + "");
            maxLabel.setText(max + "");
            whatLabel.setText(what);
            prettyPanel.setInfo(cur, min, max, avg, sum);
            repaint();

        }

        public void setVector(List theList) {
            prettyPanel.setVector(theList);
        }

        private class PrettyPanel extends JPanel {
            private float theAvg;
            private float theCur;
            private float theMax;
            private float theMin;
            private float theSum;
            private List data;

            public void setInfo(float cur, float min, float max, float avg, float sum) {
                theCur = cur;
                theAvg = avg;
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
                //int cutpoint = (int) ((theAvg - theMin) * ((this.getWidth() - 1) / (theMax - theMin)));

                Graphics2D g2 = (Graphics2D) g;
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHints(qualityHints);

                int intencity = 0; float acumm = 0;
                System.out.println(data.size()+"");
                for (int i = 0; i < data.size(); i++) {
                    float current = Float.parseFloat((String)((Vector)data.get(i)).elementAt(0));
                    intencity += current * 255 / theSum;
                    current *= getWidth()/theSum;
                    acumm += current;

                    g2.setColor(new Color(intencity,0,0));//new GradientPaint(0, 0, Color.white, cutpoint, 0, Color.cyan));
                    g2.fillRect((int)(acumm - current), 0, (int)acumm, this.getHeight() - 1);
                    g2.setColor(new Color(255,255,255,127));
                    g2.drawLine((int)(acumm - current), 10,(int)(acumm - current), (this.getHeight()) - 10);
                }
                System.out.println("acumm = " + acumm);
                System.out.println("intencity = " + intencity);



                int cutpoint = (int) ((theCur - theMin) * ((this.getWidth() - 1) / (theMax - theMin)));
                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(cutpoint, 10, cutpoint, (this.getHeight()) - 10);
                Font font2Use = new Font(getFont().getFontName(), Font.PLAIN, getFont().getSize() + 4);
                g2.setFont(font2Use);
                if (cutpoint <= getWidth() / 2) {
                    g2.drawString("" + theCur, cutpoint + 3, getHeight() / 2);
                } else {
                    TextLayout text = new TextLayout("" + theCur, font2Use, new FontRenderContext(null, true, true));
                    Rectangle2D bounds = text.getBounds();
                    g2.drawString("" + theCur, (int) (cutpoint - bounds.getWidth() - 3), getHeight() / 2);
                }


            }

        }
    }

}
