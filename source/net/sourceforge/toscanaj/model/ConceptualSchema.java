/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import org.jdom.Element;
import util.CollectionFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This is the main interface for the data structures.
 *
 * The class encapsulates (directly or indirectly) the whole data model used
 * in the program. Instances are created by parsing a CSX file with the
 * CSXParser class.
 */
public class ConceptualSchema implements DiagramCollection {
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
        eventBroker.processEvent(new NewConceptualSchemaEvent(this, this));
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
    public Diagram2D getDiagram(int number) {
        return (Diagram2D) diagrams.get(number);
    }

    /**
     * Returns a diagram from the list using the diagram title as key.
     */
    public Diagram2D getDiagram(String title) {
        Diagram2D retVal = null;
        Iterator it = this.diagrams.iterator();
        while (it.hasNext()) {
            Diagram2D cur = (Diagram2D) it.next();
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
    public void addDiagram(Diagram2D diagram) {
        diagrams.add(diagram);
        eventBroker.processEvent(new DiagramListChangeEvent(this, this));
    }

    public void removeDiagram(int diagramIndex) {
        diagrams.remove(diagramIndex);
        eventBroker.processEvent(new DiagramListChangeEvent(this, this));
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