/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import java.util.*;

import org.jdom.Element;

/**
 * Implements a concept whose objects are stored in a relational database.
 */
public class DatabaseConnectedConcept extends AbstractConceptImplementation {
    /**
     * Stores the database connection we use for querying the objects.
     */
    private DatabaseConnection connection;

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
     */
    private Set filterClauses = new HashSet();
    private static final String OBJECT_ELEMENT_NAME = "object";

    /**
     * The constructor always needs the DB connection and the information how
     * to use it.
     */
    public DatabaseConnectedConcept(DatabaseInfo dbInfo, DatabaseConnection connection) {
        this.dbInfo = dbInfo;
        this.connection = connection;
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
    public void setDatabase(DatabaseInfo dbInfo, DatabaseConnection connection) {
        this.dbInfo = dbInfo;
        this.connection = connection;
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
                    String query = this.dbInfo.getCountQuery() + " WHERE " + this.objectClause;
                    Iterator iter = this.filterClauses.iterator();
                    while (iter.hasNext()) {
                        Object item = iter.next();
                        query += " AND " + item;
                    }
                    query += ";";
                    this.numObjects = this.connection.queryNumber(query, 1);
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
        // fetch object names if we don't have them -- they will be stored once
        // we have queried them
        if (this.objects == null) {
            objects = new HashSet();
            if (this.objectClause != null) {
                try {
                    String query = this.dbInfo.getQuery() + " WHERE " + this.objectClause;
                    Iterator iter = this.filterClauses.iterator();
                    while (iter.hasNext()) {
                        Object item = iter.next();
                        query += " AND " + item;
                    }
                    query += ";";
                    objects.addAll(this.connection.queryColumn(query, 1));
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
        return objects.iterator();
    }

    /**
     * Implements Concept.executeQuery(Query, boolean).
     */
    public List executeQuery(Query query, boolean contingentOnly) {
        if (query instanceof DatabaseInfo.DatabaseQuery) {
            return executeDatabaseQuery((DatabaseInfo.DatabaseQuery) query, contingentOnly);
        } else {
            throw new RuntimeException("Unknown Query type");
        }
    }

    private List executeDatabaseQuery(DatabaseInfo.DatabaseQuery dbQuery, boolean contingentOnly) {
        List retVal = new ArrayList();
        // do a query only if there will be something to query
        // either: there is a contingent in this concept or we query extent and we
        // have subconcepts (at least one should have a contingent, otherwise this
        // concept shouldn't exist)
        if (this.objectClause != null || (!contingentOnly && this.ideal.size() != 1)) {
            String whereClause = constructWhereClause(contingentOnly);
            if (whereClause != null) {
                try {
                    retVal = this.connection.executeQuery(dbQuery, whereClause);
                } catch (DatabaseException e) {
                    handleDBException(e);
                }
            }
        }
        return retVal;
    }

    private void handleDBException(DatabaseException e) {
        /// @TODO Find something useful to do here.
        if (e.getOriginal() != null) {
            System.err.println(e.getMessage());
            e.getOriginal().printStackTrace();
        } else {
            e.printStackTrace(System.err);
        }
    }

    public String constructWhereClause(boolean contingentOnly) {
        boolean first = true;
        String whereClause = "WHERE ";
        if (contingentOnly) {
            if (this.objectClause != null) {
                whereClause += this.objectClause;
                first = false;
            }
        } else {
            // aggregate all clauses from the downset
            Iterator iter = this.ideal.iterator();
            while (iter.hasNext()) {
                DatabaseConnectedConcept concept = (DatabaseConnectedConcept) iter.next();
                if (concept.objectClause == null) {
                    continue;
                }
                if (first) {
                    first = false;
                    whereClause += " (";
                } else {
                    whereClause += " OR ";
                }
                whereClause += concept.objectClause;
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

    /**
     * Implements Concept.filterByExtent(Concept).
     *
     * The other concept is assumed to be a DatabaseConnectedConcept.
     */
    public Concept filterByExtent(Concept other) {
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo, this.connection);
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
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo, this.connection);
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
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo, this.connection);
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
}