package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.MemoryMappedConcept;

import java.util.*;

/**
 * This is the main interface for the data structures.
 *
 * The class encapsulates (directly or indirectly) the whole data model used
 * in the program. Instances are created by parsing a CSX file with the
 * CSXParser class.
 */
public class ConceptualSchema {

    /**
     * Flag is set when schema uses database.
     *
     * If a schema uses a database all object names should be interpreted as
     * WHERE-clauses of SQL queries.
     *
     * The method should return a DatabaseInfo object that contains all
     * information needed to connect to the database and the first part of the
     * query strings. If this is not given, the information has to be asked
     * from the user.
     */
    private boolean _useDatabase;

    /**
     * The database information.
     */
    private DatabaseInfo _databaseInfo;

    /**
     * The list of diagrams.
     */
    private Vector _diagrams;

    /**
     * Creates an empty schema.
     */
    public ConceptualSchema()
    {
        _useDatabase = false;
        _databaseInfo = null;
        _diagrams = new Vector();
    }

    /**
     * Returns true if a database should be used.
     */
    public boolean usesDatabase()
    {
        return _useDatabase;
    }

    /**
     * Sets the flag if a database should be used.
     */
    public void setUseDatabase( boolean flag )
    {
        _useDatabase = flag;
    }

    /**
     * Returns the database information stored.
     *
     * The return value is null if no database is defined in the schema.
     */
    public DatabaseInfo getDatabaseInfo()
    {
        return _databaseInfo;
    }

    /**
     * Sets the database information for the schema.
     */
    public void setDatabaseInformation( DatabaseInfo databaseInfo )
    {
        _databaseInfo = databaseInfo;
    }

    /**
     * Returns the number of diagrams available.
     */
    public int getNumberOfDiagrams()
    {
        return _diagrams.size();
    }

    /**
     * Returns a diagram from the list using the index.
     */
    public SimpleLineDiagram getDiagram( int number )
    {
        return (SimpleLineDiagram)_diagrams.get( number );
    }

    /**
     * Returns a diagram from the list using the diagram title as key.
     */
    public SimpleLineDiagram getDiagram( String title ) {
        SimpleLineDiagram retVal = null;
        Iterator it = this._diagrams.iterator();
        while( it.hasNext() ) {
            SimpleLineDiagram cur = (SimpleLineDiagram) it.next();
            if( cur.getTitle().equals( title ) ) {
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
    public void addDiagram( SimpleLineDiagram diagram )
    {
        _diagrams.add( diagram );
    }

    /**
     * Creates a concept directly from the input data (i.e. for a non-nested
     * diagram).
     *
     * If the concept could not be created an NoSuchElementException
     * will be thrown.
     */
    public Concept getConcept( int diagramNumber, int conceptNumber ) {
        if( this._useDatabase ) {
            /// @TODO add DB version
            // just to get things going
            return null;
        }
        else {
            SimpleLineDiagram diag = (SimpleLineDiagram) this._diagrams.get( diagramNumber );
            if( diag == null ) {
                throw new NoSuchElementException("Invalid diagram number");
            }
            if( conceptNumber > diag.getNumberOfNodes() - 1 ) {
                throw new NoSuchElementException("Invalid concept number");
            }
            return new MemoryMappedConcept();
        }
    }
}