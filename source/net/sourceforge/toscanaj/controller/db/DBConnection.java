package net.sourceforge.toscanaj.controller.db;

import java.io.PrintStream;
import java.util.*;
import java.sql.*;

/**
 * This class facilitates connection to and communication with a database
 * via JDBC.
 *
 * Currently it is hard-coded to use the JDBC-ODBC bridge from sun, this might
 * change later.
 */
public class DBConnection
{
    // URL constructed from the source, account an password info
    private String     dbURL;
    private Connection con;

    // set the diver name for the JDBC-ODBC Bridge driver
    private static final String JDBC_ODBC_BRIDGE = "sun.jdbc.odbc.JdbcOdbcDriver";

    /**
     * If set to something else than null we will print log entries into this
     * stream.
     */
    static private final PrintStream logger = null;

    /**
     * This constructor takes the data source only.
     *
     * This is a convenience function, it calls the full initialisation
     * constructor with empty username and password.
     */
    public DBConnection(String source)
    {
        this( source, "", "" );
    }

    /**
     *  This constructor takes the data source, an account name and a
     *  password.
     *
     * @TODO Throw exceptions instead of just printing them.
     */
    public DBConnection( String source, String account,	String password) {
        try {
            Class.forName(JDBC_ODBC_BRIDGE);
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println( "Could not locate JDBC Driver class: " +
                                cnfe.getMessage());
        }

        dbURL = "jdbc:odbc:" + source;

        // connect to the DB
        try {
            con = DriverManager.getConnection(dbURL, account, password);
        }
        catch (SQLException se) {
            System.out.println("An error occured connecting to the DB: " +
                               se.getMessage());
        }
        printLogMessage("Created new DB connection to " + dbURL);
    }

    /**
     * Execute a query and return the results.
     */
    public ResultSet query(String statement)
    {
        ResultSet result = null;

        // submit a query
        try {
            Statement stmt = con.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Exectuting statement: " + statement);
            result = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
        }
        catch( SQLException se ) {
            System.err.println("An error occured while querying the DB: " +
                       se.getMessage());
        }

        return result;
    }

    /**
     * Returns a String vector containing all available database names -- nyi.
     *
     * This class is not yet implemented since I still didn't find the right
     * methods to do this. The ODBC function is called SQLDataSources but it
     * seems the concept is not available in JDBC.
     *
     * One way to solve this would be implementing a native method for usage
     * with JNI -- but this is not a good way since we loose platform
     * independence in doing this.
     */
    public Vector getDatabaseNames() {
        return new Vector();
    }

    /**
     * Returns a String vector containing the table names for the
     * current database.
     */
    public Vector getTableNames() {
        Vector result = new Vector();
        final String[] tableTypes = {"TABLE"};

        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getTables( null, null, null, tableTypes);
            while( rs.next() )
            {
                result.add( rs.getString( 3 ) );
            }
        }
        catch( SQLException ex ) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                           + ex.getMessage ());
                System.err.println("SQLState:  "
                           + ex.getSQLState ());
                System.err.println("ErrorCode: "
                           + ex.getErrorCode ());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Returns a String vector containing the view names for the
     * current database.
     */
    public Vector getViewNames() {
        Vector result = new Vector();
        final String[] viewTypes = {"VIEW"};

        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getTables( null, null, null, viewTypes);
            while( rs.next() ) {
                result.add( rs.getString( 3 ) );
            }
        }
        catch( SQLException ex ) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                           + ex.getMessage ());
                System.err.println("SQLState:  "
                           + ex.getSQLState ());
                System.err.println("ErrorCode: "
                           + ex.getErrorCode ());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Returns a String vector containing the column names for the
     * specified table.
     *
     * The parameter view can be either a table or a view.
     */
    public Vector getColumnNames( String table ) {
        Vector result = new Vector();

        try {
            DatabaseMetaData dmd = con.getMetaData();
            ResultSet rs = dmd.getColumns( null, null, table, null);
            while( rs.next() ) {
                result.add( rs.getString( 4 ) );
            }
        }
        catch( SQLException ex ) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                           + ex.getMessage ());
                System.err.println("SQLState:  "
                           + ex.getSQLState ());
                System.err.println("ErrorCode: "
                           + ex.getErrorCode ());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Return a String vector containing the contents of the given column.
     *
     * The parameter view can be either a table or a view.
     *
     * The method "getString" can retrieve any of the basic
     * SQL types (however you cannot retrieve the new SQL3 datatypes
     * with it)
     */
    public Vector getColumn( String column, String table )
    {
        Vector result = new Vector();

        try {
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT [" + column +
                                 "] FROM [" + table + "]");

            while( resultSet.next() ) {
            String value = resultSet.getString( 1 );
                    result.add( value );
            }
        }
        catch( SQLException ex ) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                           + ex.getMessage ());
                System.err.println("SQLState:  "
                           + ex.getSQLState ());
                System.err.println("ErrorCode: "
                           + ex.getErrorCode ());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Puts debug output to the logger, if it is not null.
     */
    private void printLogMessage(String message) {
        if(this.logger != null) {
            logger.println(message);
        }
    }

    /**
     * Main method for testing the class.
     *
     * The main method expects exactly one argument, which is the name of an
     * ODBC source. This source is queried for all tables/view, each of them
     * is printed to stdout with each column and each entry, so be cautious on
     * which kind of DB you use the function ;-)
     */
    public static void main (String [] args) {
        if( args.length != 1 ) {
            System.err.println(
                        "Usage: DBConnection [ODBC database source name]" );
            System.exit( 1 );
        }

        DBConnection test = new DBConnection( args[0] );

        // print the tables
        System.out.println("The tables:\n-----------");

        // get the list of tables
        Vector tables = test.getTableNames();

        // print out each table
        for( int i = 0; i < tables.size(); i++ )
        {
            System.out.println( "========== " + tables.get(i) + " ==========" );
            Vector columns = test.getColumnNames( (String)tables.get(i) );
            // by printing each column
            for( int j = 0; j < columns.size(); j++ )
            {
                System.out.println( "----- " + columns.get(j) + " -----" );
                // and querying the contents
                System.out.println( test.getColumn( (String)columns.get(j),
                                                    (String)tables.get(i) ) );
            }
        }

        // print the views
        System.out.println("The views:\n-----------");

        // get the list of views
        Vector views = test.getViewNames();

        // print out each view
        for( int i = 0; i < views.size(); i++ )
        {
            System.out.println( "========== " + views.get(i) + " ==========" );
            Vector columns = test.getColumnNames( (String)views.get(i) );
            // by printing each column
            for( int j = 0; j < columns.size(); j++ )
            {
                System.out.println( "----- " + columns.get(j) + " -----" );
                // and querying the contents
                System.out.println( test.getColumn( (String)columns.get(j),
                                                    (String)views.get(i) ) );
            }
        }
    }
}

