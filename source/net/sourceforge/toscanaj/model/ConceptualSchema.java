/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerInitializationException;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramChangedEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This is the main interface for the data structures.
 *
 * The class encapsulates (directly or indirectly) the whole data model used
 * in the program. Instances are created by parsing a CSX file with the
 * CSXParser class.
 * 
 * @todo write test cases, e.g. for testing if the dirty flag is handled
 * properly
 */
public class ConceptualSchema implements XMLizable, DiagramCollection, EventBrokerListener {
    /**
     * The database information.
     */
    private DatabaseInfo databaseInfo = null;

    /**
     * The event broker for administering the conceptual scheme events.
     */
    EventBroker eventBroker;

    private List queries = new ArrayList();

    /**
     * List of tables and views in the database
     */
    private DatabaseSchema databaseSchema;

    /**
     * The list of diagrams.
     */
    private Vector diagrams;

    /**
     * The XML (XHTML) describing the schema (or null if not found).
     */
    private Element description = null;
   
    private static final String CONCEPTUAL_SCHEMA_ELEMENT_NAME = "conceptualSchema";
    private static final String VERSION_ATTRIBUTE_NAME = "version";
    private static final String VERSION_ATTRIBUTE_VALUE = "TJ1.0";
    private static final String DESCRIPTION_ELEMENT_NAME = "description";
    private static final String VIEWS_ELEMENT_NAME = "views";
    private static final String QUERIES_ELEMENT_NAME = "queries";
    
    private WritableManyValuedContext manyValuedContext;
	private boolean dataSaved = true;

    /**
     * Creates an empty schema.
     */
    public ConceptualSchema(EventBroker broker) {
        this.eventBroker = broker;
        reset();
        eventBroker.subscribe(this, DiagramChangedEvent.class, Object.class);
        eventBroker.processEvent(new NewConceptualSchemaEvent(this, this));
    }

    public ConceptualSchema(EventBroker eventBroker, Element element) throws XMLSyntaxError {
        this.eventBroker = eventBroker;
        reset();
        readXML(element);
		eventBroker.subscribe(this, DiagramChangedEvent.class, Object.class);
        eventBroker.processEvent(new NewConceptualSchemaEvent(this, this));
    }

    public Element toXML() {
        Element retVal = new Element(CONCEPTUAL_SCHEMA_ELEMENT_NAME);
        retVal.setAttribute(VERSION_ATTRIBUTE_NAME, VERSION_ATTRIBUTE_VALUE);
        if (description != null) {
            retVal.addContent(description.detach());
        }
        if (databaseInfo != null) {
            retVal.addContent(databaseInfo.toXML());
        }
        /// @todo reintroduce when we start really using it properly
//        if (databaseSchema != null) {
//            retVal.addContent(databaseSchema.toXML());
//        }
        if (DatabaseViewerManager.getNumberOfObjectListViews() != 0 ||
                DatabaseViewerManager.getNumberOfObjectViews() != 0) {
            Element viewsElem = new Element(VIEWS_ELEMENT_NAME);
            DatabaseViewerManager.listsToXML(viewsElem);
            if(viewsElem.getChildren().size() != 0) {
            	retVal.addContent(viewsElem);
            }
        }
        if (this.queries.size() != 0) {
	        Element queriesElement = new Element(QUERIES_ELEMENT_NAME);
	        for (Iterator iterator = queries.iterator(); iterator.hasNext();) {
	            Query query = (Query) iterator.next();
	            if(query != ListQuery.KEY_LIST_QUERY && 
	            				query != AggregateQuery.COUNT_QUERY &&
	            				query != AggregateQuery.PERCENT_QUERY ) {
	            	queriesElement.addContent(query.toXML());
	            }
	        }
	        retVal.addContent(queriesElement);
        }
        for (int i = 0; i < diagrams.size(); i++) {
            Diagram2D d = (Diagram2D) diagrams.elementAt(i);
            retVal.addContent(d.toXML());
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(CONCEPTUAL_SCHEMA_ELEMENT_NAME, elem);
        Element descriptionChild = elem.getChild(DESCRIPTION_ELEMENT_NAME);
        if (descriptionChild != null) {
            description = (Element) descriptionChild.clone();
        } else {
            description = null;
        }
        if (XMLHelper.contains(elem, DatabaseInfo.DATABASE_CONNECTION_ELEMENT_NAME)) {
            databaseInfo = new DatabaseInfo(elem.getChild(DatabaseInfo.DATABASE_CONNECTION_ELEMENT_NAME));
            if (XMLHelper.contains(elem, DatabaseSchema.DATABASE_SCHEMA_ELEMENT_NAME)) {
                databaseSchema = new DatabaseSchema(eventBroker, elem.getChild(DatabaseSchema.DATABASE_SCHEMA_ELEMENT_NAME));
            } else {
                databaseSchema = new DatabaseSchema(eventBroker);
            }
        }
        /// @todo change this once DatabaseViewers are on the schema itself
		DatabaseViewerManager.resetRegistry();
        Element viewsElem = elem.getChild(VIEWS_ELEMENT_NAME);
        if (viewsElem != null) {
            try {
                DatabaseViewerManager.listsReadXML(viewsElem, databaseInfo, DatabaseConnection.getConnection());
            } catch (DatabaseViewerInitializationException e) {
                throw new XMLSyntaxError("Could not initialize database viewer.", e);
            }
        }
        Element queriesElem = elem.getChild(QUERIES_ELEMENT_NAME);
        if (queriesElem != null && queriesElem.getChildren().size() != 0) {
			String dropDefaultsAttribute = queriesElem.getAttributeValue("dropDefaults");
            if(dropDefaultsAttribute == null || dropDefaultsAttribute.equals("false")) { 
				addDefaultQueries();
			}
            for (Iterator iterator = queriesElem.getChildren().iterator(); iterator.hasNext();) {
                Element queryElem = (Element) iterator.next();
                if (queryElem.getName().equals(AggregateQuery.QUERY_ELEMENT_NAME)) {
                    this.queries.add(new AggregateQuery(databaseInfo, queryElem));
                } else if (queryElem.getName().equals(ListQuery.QUERY_ELEMENT_NAME)) {
                    this.queries.add(new ListQuery(databaseInfo, queryElem));
                } else if (queryElem.getName().equals(DistinctListQuery.QUERY_ELEMENT_NAME)) {
                    this.queries.add(new DistinctListQuery(databaseInfo, queryElem));
                }
            }
        } else {
            addDefaultQueries();
        }
        List diagramElems = elem.getChildren(Diagram2D.DIAGRAM_ELEMENT_NAME);
        for (Iterator iterator = diagramElems.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            SimpleLineDiagram diagram;
            if(element.getChild("projectionBase") != null) {
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
        diagrams = new Vector();
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
    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
		markDataDirty();
        eventBroker.processEvent(new DatabaseInfoChangedEvent(this, this, databaseInfo));
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
        if(diagram instanceof WriteableDiagram2D) {
			WriteableDiagram2D wd2d = (WriteableDiagram2D) diagram;
			wd2d.setEventBroker(this.eventBroker);        	
        }
		markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this, this));
    }

    public void removeDiagram(int diagramIndex) {
        diagrams.remove(diagramIndex);
		markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this, this));
    }

	public void removeDiagram(Diagram2D diagram) {
        diagrams.remove(diagram);
		markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this, this));
    }
    
    public void exchangeDiagrams(int from, int to){
		Diagram2D indexDiagram = (Diagram2D)diagrams.get( from );
		Diagram2D diagram = (Diagram2D)diagrams.get( to );
		diagrams.setElementAt(indexDiagram,to);
		diagrams.setElementAt(diagram,from);  
		markDataDirty();
		eventBroker.processEvent(new DiagramListChangeEvent(this, this));	
    }
    
    public void replaceDiagram(Diagram2D existingDiagram, Diagram2D newDiagram){
    	int index = this.diagrams.indexOf(existingDiagram);
    	if(index == -1) {
    		throw new IllegalArgumentException("No such diagram to replace");
    	}
    	this.diagrams.set(index, newDiagram);
		markDataDirty();
        eventBroker.processEvent(new DiagramListChangeEvent(this, this));
    }

    public void setDescription(Element description) {
    	if(this.description != description) {
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
		Iterator it = this.diagrams.iterator();
		while (it.hasNext()) {
			Diagram2D diagram = (Diagram2D) it.next();
			if(diagram.getDescription() != null) {
				return true;
			}
		}		
        return false;
    }

    public DatabaseSchema getDatabaseSchema() {
        return databaseSchema;
    }

    public void setDatabaseSchema(DatabaseSchema schema) {
        this.databaseSchema = schema;
    }

    public List getQueries() {
        return queries;
    }

    public void addQuery(Query query) {
		markDataDirty();
        this.queries.add(query);
    }

    public void setManyValuedContext(WritableManyValuedContext context) {
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

	public void processEvent(Event e) {
		DiagramChangedEvent dce = (DiagramChangedEvent) e;
		Iterator it = this.diagrams.iterator();
		while (it.hasNext()) {
			Diagram2D diag = (Diagram2D) it.next();
			if(diag == dce.getDiagram()) {
				this.dataSaved = false;
			}
		}		
	}
	
	/**
	 * returns an iterator of Diagram2D objects
	 */
	public Iterator getDiagramsIterator () {
		return this.diagrams.iterator();
	}
}
