package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.dbviewer.DatabaseReportGeneratorManager;

import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * A LabelView for displaying the objects.
 *
 * This and the AttributeLabelView are used to distinguish between labels above
 * and below the nodes and the default display type (list or number).
 *
 * @see AttributeLabelView
 */
public class ObjectLabelView extends LabelView {
    /**
     * Sets the default value for showing contingent or extent.
     */
    static private boolean defaultShowContingentOnly = true;

    /**
     * Sets the default query used for new labels.
     */
    static private Query defaultQuery = null;

    /**
     * Stores the query we currently use.
     */
    private Query query = null;

    private List queryKeyValues = null;
    private List queryDisplayStrings = null;
    
    private JPopupMenu popupMenu = null;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView( DiagramView diagramView, LabelInfo label ) {
        super(diagramView,label);
        setDisplayType(defaultShowContingentOnly);
        setQuery(defaultQuery);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    public void draw(Graphics2D graphics) {
        if(this.labelInfo.getNode().getConcept().isRealised()) {
            super.draw(graphics);
        }
    }

    /**
     * Overwritten to reset the query cache.
     */
    public void setDisplayType(boolean contingentOnly) {
        super.setDisplayType(contingentOnly);
        doQuery();
        if( this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES ) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        }
        else {
            this.displayLines = this.getNumberOfEntries();
        }
        update(this);
    }

    /**
     * Sets the default display type for new labels.
     */
    static public void setDefaultDisplayType(boolean contingentOnly) {
        ObjectLabelView.defaultShowContingentOnly = contingentOnly;
    }

    /**
     * Sets the default query for new labels.
     */
    static public void setDefaultQuery(Query query) {
        ObjectLabelView.defaultQuery = query;
    }

    /**
     * Returns LabelView.BELOW
     */
    protected int getPlacement() {
        return LabelView.BELOW;
    }

    /**
     */
    public void setQuery(Query query) {
        this.query = query;
        doQuery();
        if( this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES ) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        }
        else {
            this.displayLines = this.getNumberOfEntries();
        }
        update(this);
    }

    protected int getNumberOfEntries() {
        if(this.query == null) {
            return 0;
        }
        return this.queryDisplayStrings.size();
    }

    protected Iterator getEntryIterator() {
        return this.queryDisplayStrings.iterator();
    }

    protected void doQuery() {
        if(query != null) {
            List queryResult = this.labelInfo.getNode().getConcept().executeQuery(
                                               this.query, this.showOnlyContingent);
            this.queryKeyValues = new LinkedList();
            this.queryDisplayStrings = new LinkedList();
            Iterator it = queryResult.iterator();
            while( it.hasNext() ) {
                Vector cur = (Vector)it.next();
                this.queryKeyValues.add(cur.elementAt(0));
                this.queryDisplayStrings.add(cur.elementAt(1));
            }
        }
    }
    
    public void doubleClicked(Point2D pos) {
        if(pos.getX() > this.rect.getMaxX() - this.scrollbarWidth) {
            // a doubleClick on the scrollbar
            return;
        }
        /// @todo Get rid of RTTI here.
        if(this.query instanceof net.sourceforge.toscanaj.model.DatabaseInfo.ListQuery) {
            if(DatabaseViewerManager.getNumberOfViews() == 0)
            {
                return;
            }
            int lineHit = (int)((pos.getY()-this.rect.getY())/this.lineHeight);
            int itemHit = lineHit + this.firstItem;
            DatabaseViewerManager.showObject(0,this.queryKeyValues.get(itemHit).toString());
        }
        if(this.query instanceof net.sourceforge.toscanaj.model.DatabaseInfo.AggregateQuery) {
            if(DatabaseReportGeneratorManager.getNumberOfReports() == 0)
            {
                return;
            }
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) this.labelInfo.getNode().getConcept();
            DatabaseReportGeneratorManager.showReport(0,concept.constructWhereClause(this.showOnlyContingent));
        }
        return;
    }
    
    public void openPopupMenu(MouseEvent event, Point2D pos) {
        int itemHit = getItemAtPosition(pos);
        List viewNames;
        if(this.query instanceof net.sourceforge.toscanaj.model.DatabaseInfo.ListQuery)
        {
            viewNames = DatabaseViewerManager.getViewNames();
        }
        else
        { // no views for aggregates
            viewNames = new LinkedList();
        }
        List reportNames = DatabaseReportGeneratorManager.getReportNames();
        if( viewNames.size() + reportNames.size() == 0 )
        { // nothing to display
            return;
        }
        popupMenu = new JPopupMenu();
        JMenuItem menuItem;
        if( viewNames.size() != 0 )
        {
            final String objectKey = this.queryKeyValues.get(itemHit).toString();
            Iterator it = viewNames.iterator();
            while(it.hasNext())
            {
                final String viewName = (String) it.next();
                menuItem= new JMenuItem(viewName);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DatabaseViewerManager.showObject(viewName, objectKey);    
                    }
                });
                popupMenu.add(menuItem);
            }
        }
        if( reportNames.size() != 0 )
        {
            if( viewNames.size() != 0 )
            {
                popupMenu.addSeparator();
            }
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) this.labelInfo.getNode().getConcept();
            final String whereClause = concept.constructWhereClause(this.showOnlyContingent);
            Iterator it = reportNames.iterator();
            while(it.hasNext())
            {
                final String reportName = (String) it.next();
                menuItem= new JMenuItem(reportName);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DatabaseReportGeneratorManager.showReport(reportName, whereClause);    
                    }
                });
                popupMenu.add(menuItem);
            }
        }
        popupMenu.show(this.diagramView,event.getX(),event.getY());
    }
}