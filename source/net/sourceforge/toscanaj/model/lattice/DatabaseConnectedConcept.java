/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.*;
import net.sourceforge.toscanaj.model.database.DatabaseQuery;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import java.util.*;

import org.jdom.Element;

/**
 * Implements a concept whose objects are stored in a relational database.
 */
public class DatabaseConnectedConcept extends AbstractConceptImplementation {
    /**
     * Stores the information on how to use the Database connected.
     */
    private DatabaseInfo dbInfo;

    /**
     * Stores the attributes as Java Objects.
     */
    private Set attributeContingent = new HashSet();

    /**
     * Stores the object names as Java Strings, fetched on only demand.
     */
    private Set objects = null;

    /**
     * Stores the number of objects in the contingent.
     *
     * This is set to -1 first (unknown), it will be either explicitely queried when the
     * size is needed before anyone asks for the names, or it will be set to the
     * size of the objects list when we created the list.
     */
    private int numObjects = -1;

    /**
     * Stores the where clause for finding the object contingent in the database.
     *
     * This is only the part that comes from the current diagram, the rest is
     * stored in a separate list to avoid duplication.
     *
     * @see #filterClauses
     */
    private String objectClause = null;

    /**
     * Stores all clauses used to filter into the current concept.
     *
     * @see #objectClause
     *
     * @todo This should be removed later.
     */
    private Set filterClauses = new HashSet();
    private static final String OBJECT_ELEMENT_NAME = "object";

    /**
     * The constructor always needs the DB connection and the information how
     * to use it.
     */
    public DatabaseConnectedConcept(DatabaseInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public DatabaseConnectedConcept(Element element) throws XMLSyntaxError{
           readXML(element);
    }


    /// @todo this is DB specific, but it should be changed to be more generic
    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(CONCEPT_ELEMENT_NAME, elem);
        Element objectContingentElem = XMLHelper.mustbe(OBJECT_CONTINGENT_ELEMENT_NAME, elem);
        List objects = objectContingentElem.getChildren(OBJECT_ELEMENT_NAME);
        if(objects.size() > 1) {
            throw new XMLSyntaxError("Only one object clause allowed in this version");
        }
        if(objects.size() == 1) {
            Element objElem = (Element) objects.get(0);
            this.objectClause = objElem.getText();
        }
        else {
            this.objectClause = null;
        }
        Element attributeContingentElem = XMLHelper.mustbe(ATTRIBUTE_CONTINGENT_ELEMENT_NAME, elem);
        List attributes = attributeContingentElem.getChildren(ATTRIBUTE_ELEMENT_NAME);
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            Element attrElem = (Element) iterator.next();
            addAttribute(new Attribute(attrElem.getText(),null));
        }
    }


    protected void fillObjectContingentElement(Element objectContingentElem) {
        if (objectClause != null) {
            Element objectElem = new Element(OBJECT_ELEMENT_NAME);
            objectElem.addContent(objectClause);
            objectContingentElem.addContent(objectElem);
        }
    }

    /**
     * Changes the information about the database connection.
     */
    public void setDatabase(DatabaseInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    /**
     * Sets the where-clause for finding the object contingent.
     */
    public void setObjectClause(String clause) {
        this.objectClause = clause;
        // if we get a null here (no clause for this one), we can initialize without
        // asking the DB
        if (clause == null) {
            this.objects = new HashSet(); // empty list
            this.numObjects = 0; // no objects
        }
    }

    /**
     * True if the concept has an object clause defining the contingent.
     */
    public boolean hasObjectClause() {
        return this.objectClause != null;
    }

    public String getObjectClause() {
        return this.objectClause;
    }

    /**
     * Adds an attribute to the attribute contingent.
     */
    public void addAttribute(Object attribute) {
        this.attributeContingent.add(attribute);
    }

    /**
     * Implements AbstractConceptImplementation.getAttributeContingentSize().
     */
    public int getAttributeContingentSize() {
        return this.attributeContingent.size();
    }

    /**
     * Implements AbstractConceptImplementation.getObjectContingentSize().
     */
    public int getObjectContingentSize() {
        if (numObjects == -1) {
            if (objects != null) {
                numObjects = objects.size();
            } else {
                // we don't know the answer yet, ask DB
                try {
                    this.numObjects = DatabaseConnection.getConnection().queryNumber(calculateContingentQuery(), 1);
                } catch (DatabaseException e) {
                    /// @TODO Find something useful to do here.
                    if (e.getOriginal() != null) {
                        System.err.println(e.getMessage());
                        e.getOriginal().printStackTrace();
                    } else {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        return this.numObjects;
    }

    private String calculateContingentQuery() {
        String query = this.dbInfo.getCountQuery() + " WHERE " + this.objectClause;
        Iterator iter = this.filterClauses.iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            query += " AND " + item;
        }
        query += ";";
        return query;
    }

    /**
     * Implements AbstractConceptImplementation.getAttributeContingentIterator().
     */
    public Iterator getAttributeContingentIterator() {
        return this.attributeContingent.iterator();
    }

    /**
     * Implements AbstractConceptImplementation.getObjectContingentIterator().
     */
    public Iterator getObjectContingentIterator() {
        return getObjectContingent().iterator();
    }

    private Collection getObjectContingent() {
        // fetch object names if we don't have them -- they will be stored once
        // we have queried them
        if (this.objects == null) {
            objects = new HashSet();
            if (this.objectClause != null) {
                try {
                    objects.addAll(DatabaseConnection.getConnection().queryColumn(calculateContingentQuery(), 1));
                } catch (DatabaseException e) {
                    /// @TODO Find something useful to do here.
                    if (e.getOriginal() != null) {
                        System.err.println(e.getMessage());
                        e.getOriginal().printStackTrace();
                    } else {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }
        return objects;
    }

    /**
     * Implements Concept.filterByExtent(Concept).
     *
     * The other concept is assumed to be a DatabaseConnectedConcept.
     */
    public Concept filterByExtent(Concept other) {
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo);
        retVal.attributeContingent.addAll(this.attributeContingent);
        if (other == null) {
            retVal.setObjectClause(this.objectClause);
        } else {
            if (this.objectClause == null) {
                retVal.setObjectClause(null);
            } else {
                retVal.setObjectClause(this.objectClause);
            }
            DatabaseConnectedConcept otherDB = (DatabaseConnectedConcept) other;
            retVal.filterClauses.addAll(otherDB.filterClauses);
            String newFilterClause = "(";
            boolean first = true;
            Iterator it = otherDB.ideal.iterator();
            while (it.hasNext()) {
                DatabaseConnectedConcept cur = (DatabaseConnectedConcept) it.next();
                if (cur.objectClause == null) {
                    continue;
                }
                if (!first) {
                    newFilterClause = newFilterClause + " OR ";
                } else {
                    first = false;
                }
                newFilterClause = newFilterClause + cur.objectClause;
            }
            newFilterClause += ")";
            if (!first) { // don't do anything if we are still waiting for the first (i.e. we have none)
                retVal.filterClauses.add(newFilterClause);
            }
        }
        return retVal;
    }

    /**
     * Implements Concept.filterByContingent(Concept).
     */
    public Concept filterByContingent(Concept other) {
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo);
        retVal.attributeContingent.addAll(this.attributeContingent);
        if (other == null) {
            retVal.setObjectClause(this.objectClause);
        } else {
            DatabaseConnectedConcept otherDB = (DatabaseConnectedConcept) other;
            if (otherDB.objectClause == null) {
                retVal.setObjectClause(null);
            } else {
                retVal.setObjectClause(this.objectClause);
                retVal.filterClauses.add(otherDB.objectClause);
                retVal.filterClauses.addAll(otherDB.filterClauses);
            }
        }
        return retVal;
    }

    /**
     * Implements Concept.getCollapsedConcept().
     */
    public Concept getCollapsedConcept() {
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo);
        retVal.attributeContingent.addAll(this.attributeContingent);
        String clause = "(";
        boolean first = true;
        Iterator iter = this.ideal.iterator();
        while (iter.hasNext()) {
            DatabaseConnectedConcept item = (DatabaseConnectedConcept) iter.next();
            if (item.objectClause == null) {
                continue;
            }
            if (first) {
                clause += item.objectClause;
                first = false;
            } else {
                clause += " OR " + item.objectClause;
            }
        }
        clause += ")";
        retVal.setObjectClause(clause);
        retVal.filterClauses.addAll(this.filterClauses);
        return retVal;
    }

    public String constructWhereClause(boolean contingentOnly) {
        boolean first = true;
        String whereClause = "WHERE ";
        if (contingentOnly) {
            if (this.hasObjectClause()) {
                whereClause += this.getObjectClause();
                first = false;
            }
        } else {
            // aggregate all clauses from the downset
            Iterator iter = this.ideal.iterator();
            while (iter.hasNext()) {
                DatabaseConnectedConcept otherConcept = (DatabaseConnectedConcept) iter.next();
                if (!otherConcept.hasObjectClause()) {
                    continue;
                }
                if (first) {
                    first = false;
                    whereClause += " (";
                } else {
                    whereClause += " OR ";
                }
                whereClause += otherConcept.getObjectClause();
            }
            if (!first) {
                whereClause += ") ";
            }
        }

        Iterator iter = this.filterClauses.iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            if (first) {
                first = false;
            } else {
                whereClause += " AND ";
            }
            whereClause += item;
        }
        if (first) {
            return null; // no clause at all
        }
        whereClause += ";";
        return whereClause;
    }
}