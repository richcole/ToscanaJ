/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import org.jdom.Element;
import util.CollectionFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.net.URL;

/**
 * This is the main interface for the data structures.
 *
 * The class encapsulates (directly or indirectly) the whole data model used
 * in the program. Instances are created by parsing a CSX file with the
 * CSXParser class.
 */
public class ConceptualSchema implements XML_Serializable, DiagramCollection {
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
    private static final String CONCEPTUAL_SCHEMA_ELEMENT_NAME = "conceptualSchema";
    private static final String VERSION_ATTRIBUTE_NAME = "version";
    private static final String VERSION_ATTRIBUTE_VALUE = "TJ0.6";
    private static final String DESCRIPTION_ELEMENT_NAME = "description";

    /**
     * Creates an empty schema.
     */
    public ConceptualSchema(EventBroker broker) {
        this.eventBroker = broker;
        this.dbScheme = new DatabaseSchema(broker);
        reset();
        eventBroker.processEvent(new NewConceptualSchemaEvent(this, this));
    }

    public ConceptualSchema(EventBroker eventBroker, Element element) throws XML_SyntaxError {
        this.eventBroker = eventBroker;
        this.dbScheme = new DatabaseSchema(eventBroker);
        reset();
        readXML(element);
        eventBroker.processEvent(new NewConceptualSchemaEvent(this, this));
    }

    public Element toXML() {
        Element retVal = new Element(CONCEPTUAL_SCHEMA_ELEMENT_NAME);
        retVal.setAttribute(VERSION_ATTRIBUTE_NAME, VERSION_ATTRIBUTE_VALUE);
        Element descriptionElement= new Element(DESCRIPTION_ELEMENT_NAME);
        retVal.addContent(description);
        retVal.addContent(databaseInfo.toXML());
        retVal.addContent(dbScheme.toXML());
        for (int i = 0; i < diagrams.size(); i++) {
            Diagram2D d = (Diagram2D) diagrams.elementAt(i);
            retVal.addContent(d.toXML());
        }
        return retVal;
    }

    public void readXML(Element elem) throws XML_SyntaxError {
        XML_Helper.checkName(CONCEPTUAL_SCHEMA_ELEMENT_NAME, elem);
        description = elem.getChild(DESCRIPTION_ELEMENT_NAME);
        databaseInfo = new DatabaseInfo(
                XML_Helper.mustbe(DatabaseInfo.DATABASE_CONNECTION_ELEMENT_NAME, elem)
        );
        if(XML_Helper.contains(elem, DatabaseSchema.DATABASE_SCHEMA_ELEMENT_NAME)){
            dbScheme = new DatabaseSchema(eventBroker, elem.getChild(DatabaseSchema.DATABASE_SCHEMA_ELEMENT_NAME));
        } else {
            dbScheme = new DatabaseSchema(eventBroker);
        }
        List diagramElems = elem.getChildren(Diagram2D.DIAGRAM_ELEMENT_NAME);
        for (Iterator iterator = diagramElems.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            SimpleLineDiagram diagram = new SimpleLineDiagram(element);
            diagrams.add(diagram);
            for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
                DiagramNode node = (DiagramNode) diagram.getNode(i);
                DatabaseConnectedConcept concept = (DatabaseConnectedConcept) node.getConcept();
                concept.setDatabase(databaseInfo);
            }
        }
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