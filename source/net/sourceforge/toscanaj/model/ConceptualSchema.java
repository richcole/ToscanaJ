/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import org.jdom.Element;

import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.net.URL;

import util.CollectionFactory;

/**
 * This is the main interface for the data structures.
 *
 * The class encapsulates (directly or indirectly) the whole data model used
 * in the program. Instances are created by parsing a CSX file with the
 * CSXParser class.
 */
public class ConceptualSchema {
    /**
     * The database information.
     */
    private DatabaseInfo databaseInfo;

    /**
     * The event broker for administering the conceptual scheme events.
     */
    EventBroker eventBroker;

    /**
     * List of scales
     */
    private List scales;

    /**
     * List of tables and views in the database
     */
    private DatabaseSchema dbScheme;

    /**
     * The list of diagrams.
     */
    private Vector diagrams;

    /**
     * The XML (XHTML) describing the schema (or null if not found).
     */
    private Element description = null;

    /**
     * True if the schema contains at least one diagram with description.
     */
    private boolean hasDiagramDescription = false;

    /**
     * Creates an empty schema.
     */
    public ConceptualSchema(EventBroker broker) {
        this.eventBroker = broker;
        this.dbScheme = new DatabaseSchema(broker);
        reset();
    }

    /**
     * Deletes all schema content, rendering the schema empty.
     */
    public void reset() {
        databaseInfo = new DatabaseInfo();
        diagrams = new Vector();
        hasDiagramDescription = false;
        scales = CollectionFactory.createDefaultList();
    }

    /**
     * Turns this schema into a copy of the other.
     *
     * @todo get rid of the exception
     */

    /* remove:
     public void copyContents(ConceptualSchema otherSchema)
        throws DatabaseException
    {
        reset();
        setDatabaseInfo(otherSchema.getDatabaseInfo());
        diagrams.addAll(otherSchema.diagrams);
        setDescription(otherSchema.description);
        scales.addAll(otherSchema.scales);
    }
    */

    /**
     * Returns the database information stored.
     *
     * The return value is null if no database is defined in the schema.
     */
    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    /**
     * Sets the database information for the schema.
     */
    public void setDatabaseInfo(DatabaseInfo databaseInfo) throws DatabaseException {

        this.databaseInfo = databaseInfo;
        eventBroker.processEvent(new DatabaseInfoChangedEvent(this, this, databaseInfo));

/* remove:
        if (databaseInfo == null) {
            return;
        }
        DatabaseConnection conn = new DatabaseConnection(null,
                this.databaseInfo.getURL(),
                this.databaseInfo.getUserName(),
                this.databaseInfo.getPassword()
        );
        // update all concepts
        Iterator diagIt = this.diagrams.iterator();
        while (diagIt.hasNext()) {
            SimpleLineDiagram cur = (SimpleLineDiagram) diagIt.next();
            for (int i = 0; i < cur.getNumberOfNodes(); i++) {
                Concept con = cur.getNode(i).getConcept();
                if (con instanceof DatabaseConnectedConcept) {
                    ((DatabaseConnectedConcept) con).setDatabase(this.databaseInfo, conn);
                }
            }
        }
        */
    }

    /**
     * Returns the number of diagrams available.
     */
    public int getNumberOfDiagrams() {
        return diagrams.size();
    }

    /**
     * Returns a diagram from the list using the index.
     */
    public SimpleLineDiagram getDiagram(int number) {
        return (SimpleLineDiagram) diagrams.get(number);
    }

    /**
     * Returns a diagram from the list using the diagram title as key.
     */
    public SimpleLineDiagram getDiagram(String title) {
        SimpleLineDiagram retVal = null;
        Iterator it = this.diagrams.iterator();
        while (it.hasNext()) {
            SimpleLineDiagram cur = (SimpleLineDiagram) it.next();
            if (cur.getTitle().equals(title)) {
                retVal = cur;
                break;
            }
        }
        return retVal;
    }

    /**
     * Adds a diagram to the schema.
     *
     * The new diagram will be the last one.
     */
    public void addDiagram(SimpleLineDiagram diagram) {
        diagrams.add(diagram);
        eventBroker.processEvent(new DiagramListChangeEvent(this,this));
    }

    public void removeDiagram(int diagramIndex) {
        diagrams.remove(diagramIndex);
        eventBroker.processEvent(new DiagramListChangeEvent(this,this));
    }

    public void setDescription(Element description) {
        this.description = description;
    }

    public Element getDescription() {
        return this.description;
    }

    public void setHasDiagramDescription(boolean flag) {
        this.hasDiagramDescription = flag;
    }

    public boolean hasDiagramDescription() {
        return this.hasDiagramDescription;
    }

    public DatabaseSchema getDbScheme() {
        return dbScheme;
    }
}