/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.model.DatabaseInfo;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * This class facilitates connection to and communication with a database
 * via JDBC.
 */
public class DBConnection
{
    /**
     * The JDBC database connection we use.
     */
    private Connection con;

    /**
     * If set to something else than null we will print log entries into this
     * stream.
     */
    static private final PrintStream logger;

    /**
     * Initializes the logger from the system configuration.
     */
    static {
        String log = ConfigurationManager.fetchString("DBConnection", "logger", "");
        PrintStream result = null; // we need indirection since the compiler doesn't grok it otherwise
        if(log.length() == 0) {
            // keep the null
        }
        else if(log.equals("-")) {
            result = System.out;
        }
        else {
            try {
                result = new PrintStream(new FileOutputStream(log));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        logger = result;
    }

    /**
     *  This constructor takes the data source as driver/url combination, an
     *  account name and a password.
     *
     * @TODO Throw exceptions instead of just printing them.
     */
    public DBConnection(String url, String account, String password) throws DatabaseException {
        this(getConnection(url, account, password));
    }

    public DBConnection(Connection connection){
        con = connection;
    }

    private static Connection getConnection(String url, String account, String password) throws DatabaseException {
        try {
            Driver driver = DriverManager.getDriver(url);
            if(driver == null) {
                throw new DatabaseException("Could not locate JDBC Driver class for the url:\n" + url);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Error locating JDBC Driver class for the url:\n" + url, e);
        }

        Connection connection = null;

        // connect to the DB
        try {
            connection= DriverManager.getConnection(url, account, password);
            printLogMessage("Created new DB connection to " + url);
        }
        catch (SQLException se) {
            throw new DatabaseException("An error occured connecting to the database", se);
        }
        return connection;
    }

    /**
     * Loads an SQL script into the database.
     */
    public void executeScript(URL sqlURL) throws DatabaseException {
        String sqlCommand = "";
        try {
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(sqlURL.openStream()));
            String inputLine;
            while( (inputLine = in.readLine()) != null ) {
                sqlCommand += inputLine;
            }
        }
        catch( Exception e ) {
            throw new DatabaseException("Could not read SQL script.", e);
        }
        // submit the SQL
        Statement stmt;
        try {
            stmt = con.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Submitting script: " + sqlURL.toString());
            stmt.execute(sqlCommand);
            printLogMessage(System.currentTimeMillis() + ": done.");
        }
        catch( SQLException se ) {
            throw new DatabaseException("An error occured while processing the DB script.", se);
        }
    }
    
    /**
     * Retrieves a specific column from a query as a list of strings.
     */
    public List queryColumn(String statement, int column) throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        List result = new LinkedList();

        // submit the query
        try {
            stmt = con.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing statement: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            while(resultSet.next()) {
                result.add(resultSet.getString(column));
            }
        }
        catch( SQLException se ) {
            throw new DatabaseException("An error occured while querying the database.", se);
        }
        finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(stmt!=null) {
                    stmt.close();
                }
            }
            catch(SQLException e) {
            }
        }
        return result;
    }

    /**
     * Executes the given query and returns a list of pairs of keys and formatted Strings.
     *
     * The query is given as Query object given the head of the final query plus
     * the WHERE clause for specifying the object set used. The results are pairs
     * of the key value (a string) and the result of the defined query formatted by
     * DatabaseInfo.DatabaseQuery.formatResults(ResultSet) given as Vector(2).
     *
     * If the query is an aggregate, the key value is an empty string.
     */
    public List executeQuery(DatabaseInfo.DatabaseQuery query, String whereClause) throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        List result = new LinkedList();

        String statement = query.getQueryHead() + whereClause;

        // submit the query
        try {
            stmt = con.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing query: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            while(resultSet.next()) {
                Vector item = new Vector(2);
                item.add(0,resultSet.getString(1));
                item.add(1,query.formatResults(resultSet));
                result.add(item);
            }
        }
        catch( SQLException se ) {
            throw new DatabaseException("An error occured while querying the database.", se);
        }
        finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(stmt != null) {
                    stmt.close();
                }
            }
            catch(SQLException e) {
            }
        }
        // here comes a nasty hack: if we have an aggregate on nothing, we don't want to show it
        // since the first column of the results does contain count(*) for AggregateQueries we can
        // figure out which aggregates are called on nothing (others might return zero although being
        // useful, e.g. an average). So if the count is zero, we remove the entry.
        /// @todo This is so ugly it really needs to be changed
        if( query instanceof DatabaseInfo.AggregateQuery ) {
            Vector firstRow = (Vector)result.get(0);
            if(firstRow.get(0).equals("0")) {
                result.clear();
            }
        }
        return result;
    }

    /**
     * Expects a list of field names and a where clause and returns all matches.
     *
     * The return value is a list (matching rows) of vectors (fields).
     */
    public List executeQuery(List fields, String tableName, String whereClause) throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        List result = new LinkedList();

        String statement = "SELECT ";
        Iterator it = fields.iterator();
        while(it.hasNext()) 
        {
            String field = (String) it.next();
            statement += field;
            if(it.hasNext())
            {
                statement += ", ";
            }
        }
        statement += " FROM " + tableName + " " + whereClause;

        // submit the query
        try {
            stmt = con.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing query: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            int numberColumns = resultSet.getMetaData().getColumnCount();
            while(resultSet.next()) {
                Vector item = new Vector(numberColumns);
                for( int i = 0; i < numberColumns; i++ )
                {
                    item.add(i,resultSet.getString(i+1));
                }
                result.add(item);
            }
        }
        catch( SQLException se ) {
            throw new DatabaseException("An error occured while querying the database.", se);
        }
        finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(stmt != null) {
                    stmt.close();
                }
            }
            catch(SQLException e) {
            }
        }
        return result;
    }

    /**
     * Retrieves the first value of the given column as integer.
     */
    public int queryNumber(String statement, int column) throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        int result;

        // submit the query
        try {
            stmt = con.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing statement: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            resultSet.next();
            result = resultSet.getInt(column);
        }
        catch( SQLException se ) {
            throw new DatabaseException("An error occured while querying the database.", se);
        }
        finally {
            try {
                if(resultSet != null) {
                    resultSet.close();
                }
                if(stmt != null) {
                    stmt.close();
                }
            }
            catch(SQLException e) {
            }
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
    private static void printLogMessage(String message) {
        if(logger != null) {
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
    public static void main (String [] args) throws DatabaseException {
        if( args.length != 1 ) {
            System.err.println(
                        "Usage: DBConnection [JDBC database url]" );
            System.exit( 1 );
        }

        DBConnection test = new DBConnection( args[0], "", "" );

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

