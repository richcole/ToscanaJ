package net.sourceforge.toscanaj.dbviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.JDBCCategoryDataset;

public class JFreeChartViewer implements DatabaseViewer {
    private DatabaseViewerManager manager;
    private String queryFront;
    private String windowTitle;
    private Color bgColor;
    private Font font;
    private int type;
    
    private static final int HORIZONTAL_BARCHART = 0;
    private static final int VERTICAL_BARCHART = 1;
    private static final int HORIZONTAL_LINECHART = 2;
    private static final int VERTICAL_LINECHART = 3;

    public void initialize(DatabaseViewerManager manager) throws DatabaseViewerInitializationException {
        this.manager = manager;
        this.queryFront = "SELECT ";
        Element template = manager.getTemplate();
        
        String typeTag = template.getChildText("type");
        if("HorizontalBarchart".equalsIgnoreCase(typeTag)) {
            this.type = HORIZONTAL_BARCHART;
        } else if("VerticalBarchart".equalsIgnoreCase(typeTag)) {
            this.type = VERTICAL_BARCHART;
        } else if("HorizontalLineChart".equalsIgnoreCase(typeTag)) {
            this.type = HORIZONTAL_LINECHART;
        } else if("VerticalLineChart".equalsIgnoreCase(typeTag)) {
            this.type = VERTICAL_LINECHART;
        } else {
            throw new DatabaseViewerInitializationException("Unknown chart type");
        }
        
        this.queryFront += template.getChild("domain").getText();
        List range = template.getChildren("range");
        for (Iterator iter = range.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            this.queryFront += ", " + element.getText();
        }
        this.queryFront += " FROM " + manager.getTableName() + " ";
        
        Element bgColorElem = template.getChild("bgcolor");
        if(bgColorElem != null) {
            this.bgColor = Color.decode(bgColorElem.getText());
        }
        
        Element fontElem = template.getChild("font");
        if(fontElem != null) {
            String family = fontElem.getAttributeValue("family");
            int size = Integer.parseInt(fontElem.getAttributeValue("size"));
            this.font = new Font(family, Font.PLAIN, size);
        }
    }

    public void showView(String whereClause) {
        JFreeChart chart = null;
        if(this.type == HORIZONTAL_BARCHART) {
            JDBCCategoryDataset dataset = new JDBCCategoryDataset(this.manager.getConnection().getJdbcConnection(),
                                                                 this.queryFront + whereClause);
            chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, true, true, false);
        } else if(this.type == VERTICAL_BARCHART) {
            JDBCCategoryDataset dataset = new JDBCCategoryDataset(this.manager.getConnection().getJdbcConnection(),
                                                                 this.queryFront + whereClause);
            chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
        } else if(this.type == HORIZONTAL_LINECHART) {
            JDBCCategoryDataset dataset = new JDBCCategoryDataset(this.manager.getConnection().getJdbcConnection(),
                                                                 this.queryFront + whereClause);
            chart = ChartFactory.createLineChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, true, true, false);
        } else if(this.type == VERTICAL_LINECHART) {
            JDBCCategoryDataset dataset = new JDBCCategoryDataset(this.manager.getConnection().getJdbcConnection(),
                                                                 this.queryFront + whereClause);
            chart = ChartFactory.createLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
        }
        if(this.font != null) {
            chart.getCategoryPlot().getDomainAxis().setTickLabelFont(this.font);
            chart.getCategoryPlot().getRenderer().setItemLabelFont(this.font);
            chart.getCategoryPlot().getRangeAxis().setTickLabelFont(this.font);
        }
        if(this.bgColor != null) {
            chart.setBackgroundPaint(this.bgColor);
        }

        ChartFrame frame = new ChartFrame(this.windowTitle, chart);
        frame.getChartPanel().setPreferredSize(new Dimension(500, 270));
        frame.pack();
        frame.show();        
    }
}
