/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.database.DatabaseAggregateQuery;
import net.sourceforge.toscanaj.model.database.DatabaseListQuery;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
    public ObjectLabelView(DiagramView diagramView, LabelInfo label, ConceptInterpreter conceptInterpreter) {
        super(diagramView, label, conceptInterpreter);
        setDisplayType(defaultShowContingentOnly);
        setQuery(defaultQuery);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    public void draw(Graphics2D graphics) {
        if (this.labelInfo.getNode().getConcept().isRealised()) {
            super.draw(graphics);
        }
    }

    /**
     * Overwritten to reset the query cache.
     */
    public void setDisplayType(boolean contingentOnly) {
        super.setDisplayType(contingentOnly);
        doQuery();
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
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
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
            this.displayLines = this.getNumberOfEntries();
        }
        this.firstItem = 0;
        update(this);
    }

    protected int getNumberOfEntries() {
        if (this.query == null) {
            return 0;
        }
        return this.queryDisplayStrings.size();
    }

    protected Iterator getEntryIterator() {
        return this.queryDisplayStrings.iterator();
    }

    protected void doQuery() {
        if (query != null) {
            List queryResult = this.labelInfo.getNode().getConcept().executeQuery(
                    this.query, this.showOnlyContingent);
            this.queryKeyValues = new LinkedList();
            this.queryDisplayStrings = new LinkedList();
            Iterator it = queryResult.iterator();
            while (it.hasNext()) {
                Vector cur = (Vector) it.next();
                this.queryKeyValues.add(cur.elementAt(0));
                this.queryDisplayStrings.add(cur.elementAt(1));
            }
        }
    }

    public void doubleClicked(Point2D pos) {
        if (pos.getX() > this.rect.getMaxX() - this.scrollbarWidth) {
            // a doubleClick on the scrollbar
            return;
        }
        /// @todo Get rid of RTTI here.
        if (this.query instanceof DatabaseListQuery) {
            if (DatabaseViewerManager.getNumberOfObjectViews() == 0) {
                return;
            }
            int lineHit = (int) ((pos.getY() - this.rect.getY()) / this.lineHeight);
            int itemHit = lineHit + this.firstItem;
            DatabaseViewerManager.showObject(0, this.queryKeyValues.get(itemHit).toString());
        }
        if (this.query instanceof DatabaseAggregateQuery) {
            if (DatabaseViewerManager.getNumberOfObjectListViews() == 0) {
                return;
            }
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) this.labelInfo.getNode().getConcept();
            DatabaseViewerManager.showObjectList(0, concept.constructWhereClause(this.showOnlyContingent));
        }
        return;
    }

    public void openPopupMenu(Point2D canvasPosition, Point2D screenPosition) {
        int itemHit = getItemAtPosition(canvasPosition);
        // find available queries
        List queries = Query.getQueries();
        // find available object views if list is displayed
        List objectViewNames;
        if (this.query instanceof DatabaseListQuery) {
            objectViewNames = DatabaseViewerManager.getObjectViewNames();
        } else { // no views for aggregates
            objectViewNames = new LinkedList();
        }
        // find available object list views
        List objectListViewNames = DatabaseViewerManager.getObjectListViewNames();
        if (queries.size() + objectViewNames.size() + objectListViewNames.size() == 0) { // nothing to display
            return;
        }
        // create the menu
        popupMenu = new JPopupMenu();
        JMenuItem menuItem;
        if (queries.size() != 0) {
            JMenu queryMenu = new JMenu("Change label");
            Iterator it = queries.iterator();
            while (it.hasNext()) {
                final Query query = (Query) it.next();
                menuItem = new JMenuItem(query.getName());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setQuery(query);
                    }
                });
                queryMenu.add(menuItem);
            }
            popupMenu.add(queryMenu);
        }
        if (objectViewNames.size() != 0) {
            JMenu objectViewMenu = new JMenu("View object");
            final String objectKey = this.queryKeyValues.get(itemHit).toString();
            Iterator it = objectViewNames.iterator();
            while (it.hasNext()) {
                final String objectViewName = (String) it.next();
                menuItem = new JMenuItem(objectViewName);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DatabaseViewerManager.showObject(objectViewName, objectKey);
                    }
                });
                objectViewMenu.add(menuItem);
            }
            popupMenu.add(objectViewMenu);
        }
        if (objectListViewNames.size() != 0) {
            JMenu objectListViewMenu = new JMenu("View summary");
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) this.labelInfo.getNode().getConcept();
            final String whereClause = concept.constructWhereClause(this.showOnlyContingent);
            Iterator it = objectListViewNames.iterator();
            while (it.hasNext()) {
                final String objectListViewName = (String) it.next();
                menuItem = new JMenuItem(objectListViewName);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        DatabaseViewerManager.showObjectList(objectListViewName, whereClause);
                    }
                });
                objectListViewMenu.add(menuItem);
            }
            popupMenu.add(objectListViewMenu);
        }
        popupMenu.show(this.diagramView, (int)screenPosition.getX(), (int)screenPosition.getY());
    }
}