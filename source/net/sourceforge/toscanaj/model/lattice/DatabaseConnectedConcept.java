package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.ObjectListQuery;
import net.sourceforge.toscanaj.model.ObjectNumberQuery;
import net.sourceforge.toscanaj.model.Query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implements a concept whose objects are stored in a relational database.
 */
public class DatabaseConnectedConcept extends AbstractConceptImplementation {
    /**
     * Stores the database connection we use for querying the objects.
     */
    private DBConnection connection;

    /**
     * Stores the information on how to use the Database connected.
     */
    private DatabaseInfo dbInfo;

    /**
     * Stores the attributes as Java Objects.
     */
    private List attributeContingent = new LinkedList();

    /**
     * Stores the object names as Java Strings, fetched on only demand.
     */
    private List objects = null;

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
     * @see filterClauses
     */
    private String objectClause = null;

    /**
     * Stores all clauses used to filter into the current concept.
     *
     * @see objectClause
     */
    private List filterClauses = new LinkedList();

    /**
     * The constructor always needs the DB connection and the information how
     * to use it.
     */
    public DatabaseConnectedConcept(DatabaseInfo dbInfo, DBConnection connection) {
        this.dbInfo = dbInfo;
        this.connection = connection;
    }

    /**
     * Changes the information about the database connection.
     */
    public void setDatabase(DatabaseInfo dbInfo, DBConnection connection) {
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
        if(clause == null) {
            this.objects = new LinkedList(); // empty list
            this.numObjects = 0; // no objects
        }
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
        if( numObjects == -1 ) {
            if( objects != null ) {
                numObjects = objects.size();
            }
            else {
                // we don't know the answer yet, ask DB
                try {
                    String query = this.dbInfo.getCountQuery() + " WHERE " + this.objectClause;
                    Iterator iter = this.filterClauses.iterator();
                    while (iter.hasNext()) {
                        Object item = iter.next();
                        query += " AND " + item;
                    }
                    query += ";";
                    this.numObjects = this.connection.queryNumber(query,1);
                }
                catch (DatabaseException e) {
                    /// @TODO Find something useful to do here.
                    if(e.getOriginal()!=null) {
                        System.err.println(e.getMessage());
                        e.getOriginal().printStackTrace();
                    }
                    else {
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
        if( this.objects == null ) {
            objects = new LinkedList();
            if( this.objectClause != null ) {
                try {
                    String query = this.dbInfo.getQuery() + " WHERE " + this.objectClause;
                    Iterator iter = this.filterClauses.iterator();
                    while (iter.hasNext()) {
                        Object item = iter.next();
                        query += " AND " + item;
                    }
                    query += ";";
                    objects = this.connection.queryColumn(query,1);
                }
                catch (DatabaseException e) {
                    /// @TODO Find something useful to do here.
                    if(e.getOriginal()!=null) {
                        System.err.println(e.getMessage());
                        e.getOriginal().printStackTrace();
                    }
                    else {
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
        if( query instanceof ObjectListQuery ) {
            /// @todo implement this
            throw new RuntimeException("NYI");
        }
        else if( query instanceof ObjectNumberQuery ) {
            /// @todo implement this
            throw new RuntimeException("NYI");
        }
        else if( query instanceof DatabaseInfo.DatabaseQuery ) {
            DatabaseInfo.DatabaseQuery dbQuery = (DatabaseInfo.DatabaseQuery) query;
            List retVal = new LinkedList();
            if( this.objectClause != null ) {
                try {
                    String whereClause = " WHERE (";
                    if(contingentOnly) {
                        // use only the local clause (we assume there is one)
                        whereClause += this.objectClause;
                    }
                    else {
                        // aggregate all clauses from the downset
                        Iterator iter = this.ideal.iterator();
                        boolean first = true;
                        while (iter.hasNext()) {
                            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) iter.next();
                            if(concept.objectClause == null) {
                                continue;
                            }
                            if(first) {
                                first = false;
                            }
                            else {
                                whereClause += " OR ";
                            }
                            whereClause += concept.objectClause;
                        }
                    }
                    whereClause += ") ";
                    Iterator iter = this.filterClauses.iterator();
                    while (iter.hasNext()) {
                        Object item = iter.next();
                        whereClause += " AND " + item;
                    }
                    whereClause += ";";
                    retVal = this.connection.executeQuery(dbQuery, whereClause);
                }
                catch (DatabaseException e) {
                    /// @TODO Find something useful to do here.
                    if(e.getOriginal()!=null) {
                        System.err.println(e.getMessage());
                        e.getOriginal().printStackTrace();
                    }
                    else {
                        e.printStackTrace(System.err);
                    }
                }
            }
            return retVal;
        }
        else {
            throw new RuntimeException("Unknown Query type");
        }
    }

    /**
     * Implements Concept.filterByExtent(Concept).
     *
     * The other concept is assumed to be a DatabaseConnectedConcept.
     */
    public Concept filterByExtent(Concept other) {
        DatabaseConnectedConcept retVal = new DatabaseConnectedConcept(this.dbInfo, this.connection);
        retVal.attributeContingent.addAll(this.attributeContingent);
        if(other == null) {
            retVal.setObjectClause(this.objectClause);
        }
        else {
            if(this.objectClause == null) {
                retVal.setObjectClause(null);
            }
            else {
                DatabaseConnectedConcept otherDB = (DatabaseConnectedConcept) other;
                retVal.objectClause = this.objectClause;
                retVal.filterClauses.addAll(otherDB.filterClauses);
                String newFilterClause = "(";
                boolean first = true;
                Iterator it = otherDB.ideal.iterator();
                while(it.hasNext()) {
                    DatabaseConnectedConcept cur = (DatabaseConnectedConcept) it.next();
                    if(cur.objectClause == null) {
                        continue;
                    }
                    if(!first) {
                        newFilterClause = newFilterClause + " OR " ;
                    }
                    else {
                        first = false;
                    }
                    newFilterClause = newFilterClause + cur.objectClause;
                }
                newFilterClause += ")";
                if(!first) { // don't do anything if we are still waiting for the first (i.e. we have none)
                    retVal.filterClauses.add(newFilterClause);
                }
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
        if(other == null) {
            retVal.setObjectClause(this.objectClause);
        }
        else {
            DatabaseConnectedConcept otherDB = (DatabaseConnectedConcept) other;
            if( (this.objectClause == null) || (otherDB.objectClause == null) ) {
                retVal.setObjectClause(null);
            }
            else {
                retVal.setObjectClause(this.objectClause);
                retVal.filterClauses.addAll(otherDB.filterClauses);
                retVal.filterClauses.add(otherDB.objectClause);
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
            DatabaseConnectedConcept item = (DatabaseConnectedConcept)iter.next();
            if(item.objectClause == null) {
                continue;
            }
            if(first) {
                clause += item.objectClause;
                first = false;
            }
            else {
                clause += " OR " + item.objectClause;
            }
        }
        clause += ")";
        retVal.setObjectClause(clause);
        retVal.filterClauses.addAll(this.filterClauses);
        return retVal;
    }
}