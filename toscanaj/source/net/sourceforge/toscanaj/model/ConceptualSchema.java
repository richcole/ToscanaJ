/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerException;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.DistinctListQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContextImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * This is the main interface for the data structures.
 * 
 * The class encapsulates (directly or indirectly) the whole data model used in
 * the program. Instances are created by parsing a CSX file with the CSXParser
 * class.
 * 
 * @todo write test cases, e.g. for testing if the dirty flag is handled
 *       properly
 */
public class ConceptualSchema implements XMLizable, DiagramCollection,
        EventBrokerListener {
    /**
     * The database information.
     */
    private DatabaseInfo databaseInfo = null;

    /**
     * The event broker for administering the conceptual scheme events.
     */
    EventBroker eventBroker;

    private final List<Query> queries = new ArrayList<Query>();

    /**
     * List of tables and views in the database
     */
    private DatabaseSchema databaseSchema;

    /**
     * The list of diagrams.
     */
    private Vector<Diagram2D> diagrams;

    /**
     * The XML (XHTML) describing the schema (or null if not found).
     */
    private Element description = null;

    private ConceptInterpreter conceptInterpreter;

    private static final String CONCEPTUAL_SCHEMA_ELEMENT_NAME = "conceptualSchema";
    private static final String VERSION_ATTRIBUTE_NAME = "version";
    private static final String VERSION_ATTRIBUTE_VALUE = "TJ1.0";
    private static final String DESCRIPTION_ELEMENT_NAME = "description";
    private static final String VIEWS_ELEMENT_NAME = "views";
    private static final String QUERIES_ELEMENT_NAME = "queries";

    private WritableManyValuedContext manyValuedContext;
    private boolean dataSaved = true;
    private URL location;

    /**
     * Creates an empty schema.
     */
    public ConceptualSchema(final EventBroker broker) {
        this.eventBroker = broker;
        reset();
        this.conceptInterpreter = new DirectConceptInterpreter();
        eventBroker.subscribe(this, DiagramChangedEvent.class, Object.class);
        eventBroker.processEvent(new NewConceptualSchemaEvent(this));
    }

    public ConceptualSchema(final EventBroker eventBroker, final Element element)
            throws XMLSyntaxError {
        this.eventBroker = eventBroker;
        reset();
        readXML(element);
        eventBroker.subscribe(this, DiagramChangedEvent.class, Object.class);
        eventBroker.processEvent(new NewConceptualSchemaEvent(this));
    }

    public Element toXML() {
        final Element retVal = new Element(CONCEPTUAL_SCHEMA_ELEMENT_NAME);
        retVal.setAttribute(VERSION_ATTRIBUTE_NAME, VERSION_ATTRIBUTE_VALUE);
        if (description != null) {
            retVal.addContent(description.detach());
        }
        if (this.conceptInterpreter instanceof XMLizable) {
            final XMLizable xmlConceptInterpreter = (XMLizable) this.conceptInterpreter;
            retVal.addContent(xmlConceptInterpreter.toXML());
        }
        if (this.manyValuedContext instanceof XMLizable) {
            final XMLizable xmlManyValuedContext = (XMLizable) this.manyValuedContext;
            retVal.addContent(xmlManyValuedContext.toXML());
        }
        if (databaseInfo != null) {
            retVal.addContent(databaseInfo.toXML());
        }
        // / @todo reintroduce when we start really using it properly
        // if (databaseSchema != null) {
        // retVal.addContent(databaseSchema.toXML());
        // }
        if (DatabaseViewerManager.getNumberOfObjectListViews() != 0
                || DatabaseViewerManager.getNumberOfObjectViews() != 0) {
            final Element viewsElem = new Element(VIEWS_ELEMENT_NAME);
            DatabaseViewerManager.listsToXML(viewsElem);
            if (viewsElem.getChildren().size() != 0) {
                retVal.addContent(viewsElem);
            }
        }
        if (this.queries.size() != 0) {
            final Element queriesElement = new Element(QUERIES_ELEMENT_NAME);
            for (final Query query : queries) {
                if (query != ListQuery.KEY_LIST_QUERY
                        && query != AggregateQuery.COUNT_QUERY
                        && query != AggregateQuery.PERCENT_QUERY) {
                    queriesElement.addContent(query.toXML());
                }
            }
            retVal.addContent(queriesElement);
        }
        for (int i = 0; i < diagrams.size(); i++) {
            final Diagram2D d = diagrams.elementAt(i);
            retVal.addContent(d.toXML());
        }
        return retVal;
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, CONCEPTUAL_SCHEMA_ELEMENT_NAME);
        final Element descriptionChild = elem
                .getChild(DESCRIPTION_ELEMENT_NAME);
        if (descriptionChild != null) {
            description = (Element) descriptionChild.clone();
        } else {
            description = null;
        }
        if (XMLHelper
                .contains(
                        elem,
                        ManyValuedContextImplementation.MANY_VALUED_CONTEXT_ELEMENT_NAME)) {
            this.manyValuedContext = new ManyValuedContextImplementation(
                    elem
                            .getChild(ManyValuedContextImplementation.MANY_VALUED_CONTEXT_ELEMENT_NAME));
        }
        if (XMLHelper.contains(elem,
                DatabaseInfo.DATABASE_CONNECTION_ELEMENT_NAME)) {
            databaseInfo = new DatabaseInfo(elem
                    .getChild(DatabaseInfo.DATABASE_CONNECTION_ELEMENT_NAME));
            if (XMLHelper.contains(elem,
                    DatabaseSchema.DATABASE_SCHEMA_ELEMENT_NAME)) {
                databaseSchema = new DatabaseSchema(eventBroker, elem
                        .getChild(DatabaseSchema.DATABASE_SCHEMA_ELEMENT_NAME));
            } else {
                databaseSchema = new DatabaseSchema(eventBroker);
            }
        }
        if (XMLHelper.contains(elem,
                ConceptInterpreter.CONCEPT_INTERPRETER_ELEMENT_NAME)) {
            final Element conceptInterpreterElem = elem
                    .getChild(ConceptInterpreter.CONCEPT_INTERPRETER_ELEMENT_NAME);
            final String className = XMLHelper.getAttribute(
                    conceptInterpreterElem,
                    ConceptInterpreter.CONCEPT_INTERPRETER_CLASS_ATTRIBUTE)
                    .getValue();
            try {
                final Class ciClass = Class.forName(className);
                final Constructor constructor = ciClass
                        .getConstructor(new Class[] { Element.class });
                this.conceptInterpreter = (ConceptInterpreter) constructor
                        .newInstance(new Object[] { conceptInterpreterElem });
            } catch (final ClassNotFoundException e) {
                throw new XMLSyntaxError(
                        "Could not find concept interpreter class", e);
            } catch (final SecurityException e) {
                throw new XMLSyntaxError(
                        "Could not access concept interpreter class", e);
            } catch (final NoSuchMethodException e) {
                throw new XMLSyntaxError(
                        "Could not find concept interpreter constructor", e);
            } catch (final IllegalArgumentException e) {
                throw new XMLSyntaxError(
                        "Could not initialize concept interpreter", e);
            } catch (final InstantiationException e) {
                throw new XMLSyntaxError(
                        "Could not initialize concept interpreter", e);
            } catch (final IllegalAccessException e) {
                throw new XMLSyntaxError(
                        "Could not access concept interpreter constructor", e);
            } catch (final InvocationTargetException e) {
                throw new XMLSyntaxError(
                        "Could not initialize concept interpreter", e);
            }
        } else {
            if (this.databaseInfo != null) {
                this.conceptInterpreter = new DatabaseConnectedConceptInterpreter(
                        this.databaseInfo);
            } else {
                this.conceptInterpreter = new DirectConceptInterpreter();
            }
        }
        // / @todo change this once DatabaseViewers are on the schema itself
        DatabaseViewerManager.resetRegistry();
        final Element viewsElem = elem.getChild(VIEWS_ELEMENT_NAME);
        if (viewsElem != null) {
            try {
                DatabaseViewerManager.listsReadXML(viewsElem, databaseInfo,
                        DatabaseConnection.getConnection());
            } catch (final DatabaseViewerException e) {
                throw new XMLSyntaxError(
                        "Could not initialize database viewer.", e);
            }
        }
        final Element queriesElem = elem.getChild(QUERIES_ELEMENT_NAME);
        if (queriesElem != null && queriesElem.getChildren().size() != 0) {
            final String dropDefaultsAttribute = queriesElem
                    .getAttributeValue("dropDefaults");
            if (dropDefaultsAttribute == null
                    || dropDefaultsAttribute.equals("false")) {
                addDefaultQueries();
            }
            for (final Iterator<Element> iterator = queriesElem.getChildren()
                    .iterator(); iterator.hasNext();) {
                final Element queryElem = iterator.next();
                if (queryElem.getName().equals(
                        AggregateQuery.QUERY_ELEMENT_NAME)) {
                    this.queries
                            .add(new AggregateQuery(databaseInfo, queryElem));
                } else if (queryElem.getName().equals(
                        ListQuery.QUERY_ELEMENT_NAME)) {
                    this.queries.add(new ListQuery(databaseInfo, queryElem));
                } else if (queryElem.getName().equals(
                        DistinctListQuery.QUERY_ELEMENT_NAME)) {
                    this.queries.add(new DistinctListQuery(databaseInfo,
                            queryElem));
                }
            }
        } else {
            addDefaultQueries();
        }
        final List<Element> diagramElems = elem
                .getChildren(Diagram2D.DIAGRAM_ELEMENT_NAME);
        for (final Element element : diagramElems) {
            SimpleLineDiagram diagram;
            if (element.getChild("projectionBase") != null) {
                diagram = new NDimDiagram(element);
            } else {
                diagram = new SimpleLineDiagram(element);
            }
            addDiagram(diagram);
        }
        this.dataSaved = true;
    }

    private void addDefaultQueries() {
        queries.add(AggregateQuery.COUNT_QUERY);
        queries.add(ListQuery.KEY_LIST_QUERY);
        queries.add(AggregateQuery.PERCENT_QUERY);
    }

    /**
     * Deletes all schema content, rendering the schema empty.
     */
    protected void reset() {
        databaseInfo = null;
        diagrams = new Vector<Diagram2D>();
    }

    /**
     * Returns the database information stored.
     * 
     * The return value is null if no database is defined in the schema.
     * 
     * @todo DatabaseInfo should be immutable, then we don't need the defensive
     *       copy anymore
     */
    public DatabaseInfo getDatabaseInfo() {
        if (databaseInfo == null) {
            return null;
        }
        return new DatabaseInfo(databaseInfo);
    }

    /**
     * Sets the database information for the schema.
     * 
     * @todo DatabaseInfo should be immutable, then we don't need the defensive
     *       copy anymore
     */
    public void setDatabaseInfo(final DatabaseInfo databaseInfo) {
        if (databaseInfo == null) {
            this.databaseInfo = null;
        } else {
            this.databaseInfo = new DatabaseInfo(databaseInfo);
        }
        markDataDirty();
        eventBroker.processEvent(new DatabaseInfoChangedEvent(this,
                databaseInfo));
    }

    private void markDataDirty() {
        this.dataSaved = false;
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
    public Diagram2D getDiagram(final int number) {
        return diagrams.get(number);
    }

    /**
     * Returns a diagram from the list using the diagram title as key.
     */
    public Diagram2D getDiagram(final String title) {
        Diagram2D retVal = null;
        final Iterator<Diagram2D> it = this.diagrams.iterator();
        while (it.hasNext()) {
            final Diagram2D cur = it.next();
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
    public void addDiagram(final Diagram2D diagram) {
        diagrams.add(diagram);
        if (diagram instanceof WriteableDiagram2D) {
            final WriteableDiagram2D wd2d = (WriteableDiagram2D) diagram;
            wd2d.setEventBroker(this.eventBroker);
        }
        markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this));
    }

    public void removeDiagram(final int diagramIndex) {
        diagrams.remove(diagramIndex);
        markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this));
    }

    public void removeDiagram(final Diagram2D diagram) {
        diagrams.remove(diagram);
        markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this));
    }

    public void exchangeDiagrams(final int from, final int to) {
        final Diagram2D indexDiagram = diagrams.get(from);
        final Diagram2D diagram = diagrams.get(to);
        diagrams.setElementAt(indexDiagram, to);
        diagrams.setElementAt(diagram, from);
        markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this));
    }

    public void replaceDiagram(final Diagram2D existingDiagram,
            final Diagram2D newDiagram) {
        final int index = this.diagrams.indexOf(existingDiagram);
        if (index == -1) {
            throw new IllegalArgumentException("No such diagram to replace");
        }
        this.diagrams.set(index, newDiagram);
        markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this));
    }

    public void setDescription(final Element description) {
        if (this.description != description) {
            markDataDirty();
        }
        if (description != null) {
            this.description = (Element) description.clone();
        } else {
            this.description = null;
        }
    }

    public Element getDescription() {
        return this.description;
    }

    public boolean hasDiagramDescription() {
        final Iterator<Diagram2D> it = this.diagrams.iterator();
        while (it.hasNext()) {
            final Diagram2D diagram = it.next();
            if (diagram.getDescription() != null) {
                return true;
            }
        }
        return false;
    }

    public DatabaseSchema getDatabaseSchema() {
        return databaseSchema;
    }

    public void setDatabaseSchema(final DatabaseSchema schema) {
        this.databaseSchema = schema;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void addQuery(final Query query) {
        markDataDirty();
        this.queries.add(query);
    }

    public void setManyValuedContext(final WritableManyValuedContext context) {
        markDataDirty();
        this.manyValuedContext = context;
    }

    public WritableManyValuedContext getManyValuedContext() {
        return this.manyValuedContext;
    }

    public void dataSaved() {
        this.dataSaved = true;
    }

    public boolean isDataSaved() {
        return this.dataSaved;
    }

    public void processEvent(final Event e) {
        final DiagramChangedEvent dce = (DiagramChangedEvent) e;
        final Iterator<Diagram2D> it = this.diagrams.iterator();
        while (it.hasNext()) {
            final Diagram2D diag = it.next();
            if (diag == dce.getDiagram()) {
                this.dataSaved = false;
            }
        }
    }

    /**
     * returns an iterator of Diagram2D objects
     */
    public Iterator<Diagram2D> getDiagramsIterator() {
        return this.diagrams.iterator();
    }

    public ConceptInterpreter getConceptInterpreter() {
        return conceptInterpreter;
    }

    public void setConceptInterpreter(final ConceptInterpreter interpreter) {
        this.conceptInterpreter = interpreter;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(final URL location) {
        this.location = location;
    }
}
