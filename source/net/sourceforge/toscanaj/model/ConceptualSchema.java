package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

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
    private boolean useDatabase;

    /**
     * The database information.
     */
    private DatabaseInfo databaseInfo;

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
    public ConceptualSchema()
    {
        useDatabase = false;
        databaseInfo = null;
        diagrams = new Vector();
        hasDiagramDescription = false;
    }

    /**
     * Returns true if a database should be used.
     */
    public boolean usesDatabase()
    {
        return useDatabase;
    }

    /**
     * Sets the flag if a database should be used.
     */
    public void setUseDatabase( boolean flag )
    {
        useDatabase = flag;
    }

    /**
     * Returns the database information stored.
     *
     * The return value is null if no database is defined in the schema.
     */
    public DatabaseInfo getDatabaseInfo()
    {
        return databaseInfo;
    }

    /**
     * Sets the database information for the schema.
     */
    public void setDatabaseInfo( DatabaseInfo databaseInfo ) throws DatabaseException {
        this.databaseInfo = databaseInfo;
        if(databaseInfo == null) {
            return;
        }
        DBConnection conn = new DBConnection(this.databaseInfo.getSource(), this.databaseInfo.getUserName(),
                                             this.databaseInfo.getPassword());
        // update all concepts
        Iterator diagIt = this.diagrams.iterator();
        while(diagIt.hasNext()) {
            SimpleLineDiagram cur = (SimpleLineDiagram)diagIt.next();
            for(int i=0; i < cur.getNumberOfNodes(); i++) {
                Concept con = cur.getNode(i).getConcept();
                if(con instanceof DatabaseConnectedConcept) {
                    ((DatabaseConnectedConcept) con).setDatabase(this.databaseInfo,conn);
                }
            }
        }
    }

    /**
     * Returns the number of diagrams available.
     */
    public int getNumberOfDiagrams()
    {
        return diagrams.size();
    }

    /**
     * Returns a diagram from the list using the index.
     */
    public SimpleLineDiagram getDiagram( int number )
    {
        return (SimpleLineDiagram)diagrams.get( number );
    }

    /**
     * Returns a diagram from the list using the diagram title as key.
     */
    public SimpleLineDiagram getDiagram( String title ) {
        SimpleLineDiagram retVal = null;
        Iterator it = this.diagrams.iterator();
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
        diagrams.add( diagram );
    }
    
    public void setDescription( Element description )
    {
        this.description = description;
    }
    
    public Element getDescription()
    {
        return this.description;
    }
    
    public void setHasDiagramDescription(boolean flag)
    {
        this.hasDiagramDescription = flag;
    }
    
    public boolean hasDiagramDescription()
    {
        return this.hasDiagramDescription;
    }
}