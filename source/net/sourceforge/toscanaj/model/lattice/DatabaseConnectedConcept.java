package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;

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
    private List attributes = new LinkedList();

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
     */
    private String objectClause = null;

    /**
     * The constructor always needs the DB connection and the information how
     * to use it.
     */
    public DatabaseConnectedConcept(DatabaseInfo dbInfo, DBConnection connection) {
        this.connection = connection;
        this.dbInfo = dbInfo;
    }

    /**
     * Sets the where-clause for finding the object contingent.
     */
    public void setObjectClause(String clause) {
        this.objectClause = clause;
    }

    /**
     * Adds an attribute to the attribute contingent.
     */
    public void addAttribute(Object attribute) {
        this.attributes.add(attribute);
    }

    /**
     * Implements AbstractConceptImplementation.getAttributeContingentSize().
     */
    public int getAttributeContingentSize() {
        return this.attributes.size();
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
                    String query = this.dbInfo.getCountQuery() + " WHERE " + this.objectClause + ";";
                    ResultSet result = connection.query(query);
                    result.next();
                    this.numObjects = Integer.parseInt(result.getString(1));
                }
                catch (SQLException e) {
                    e.printStackTrace(System.err);
                    /// @TODO Find something useful to do here.
                }
            }
        }
        return this.numObjects;
    }

    /**
     * Implements AbstractConceptImplementation.getAttributeContingentIterator().
     */
    public Iterator getAttributeContingentIterator() {
        return this.attributes.iterator();
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
                    String query = this.dbInfo.getQuery() + " WHERE " + this.objectClause + ";";
                    ResultSet result = connection.query(query);
                    if( result != null ) {
                        while( result.next() ) {
                            objects.add(result.getString(1));
                        }
                    }
                    else {
                        System.err.println("Could not resolve query: " + query);
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace(System.err);
                    /// @TODO Find something useful to do here.
                }
            }
        }
        return objects.iterator();
    }

    /**
     * Implements Concept.filterByExtent(Concept).
     */
    public Concept filterByExtent(Concept other) {
        /// @TODO Implement this properly
        return this;
    }

    /**
     * Implements Concept.filterByContingent(Concept).
     */
    public Concept filterByContingent(Concept other) {
        /// @TODO Implement this properly
        return this;
    }
}