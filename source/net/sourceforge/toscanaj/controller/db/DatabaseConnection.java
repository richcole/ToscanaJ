/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import org.tockit.events.*;
import org.tockit.events.EventListener;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.events.DatabaseModifiedEvent;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * This class facilitates connection to and communication with a database
 * via JDBC.
 */
public class DatabaseConnection implements EventListener {
    /**
     * The JDBC database connection we use.
     */
    private Connection jdbcConnection = null;
    private EventBroker broker;

    static private DatabaseConnection singleton = null;

    /**
     * If set to something else than null we will print log entries into this
     * stream.
     */
    static private final PrintStream logger;

    /**
     * Initializes the logger from the system configuration.
     */
    static {
        String log = ConfigurationManager.fetchString("DatabaseConnection", "logger", "");
        PrintStream result = null; // we need indirection since the compiler doesn't grok it otherwise
        if (log.length() == 0) {
            // keep the null
        } else if (log.equals("-")) {
            result = System.out;
        } else {
            try {
                result = new PrintStream(new FileOutputStream(log));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger = result;
    }

    static public void initialize(EventBroker eventBroker) {
        singleton = new DatabaseConnection(eventBroker);
    }

    static public DatabaseConnection getConnection() {
        return singleton;
    }

    static public void setConnection(DatabaseConnection connection) {
        singleton = connection;
    }

    /**
     *  This constructor takes the data source as driver/url combination, an
     *  account name and a password.
     *
     * @todo Throw exceptions instead of just printing them.
     */
    public DatabaseConnection(EventBroker broker, String url, String driver, String account, String password)
                throws DatabaseException {
        this.broker = broker;
        connect(url, driver, account, password);
    }

    public DatabaseConnection(EventBroker broker, Connection connection) {
        this.broker = broker;
        jdbcConnection = connection;
    }

    /**
     *  Create a connection that isn't connected.
     */
    public DatabaseConnection(EventBroker broker) {
        this.broker = broker;
        broker.subscribe(this, DatabaseConnectEvent.class, Object.class);
    }

    public void connect(DatabaseInfo info) throws DatabaseException {
        if (this.isConnected()) {
            disconnect();
        }
        connect(info.getURL(), info.getDriverClass(), info.getUserName(), info.getPassword());
    }

    public void disconnect() throws DatabaseException {
        try {
            jdbcConnection.close();
            jdbcConnection = null;
        } catch (SQLException e) {
            throw new DatabaseException("Could not disconnect from the database.", e);
        }
    }

    public boolean isConnected() {
        return jdbcConnection != null;
    }

    public void connect(String url, String driverName, String account, String password) throws DatabaseException {
        jdbcConnection = getConnection(url, driverName, account, password);
        broker.processEvent(new DatabaseConnectedEvent(this, this));
    }

    private static Connection getConnection(String url, String driverName, String account, String password) throws DatabaseException {
        if ((url == null) || (url.equals(""))) {
            throw new DatabaseException("No URL given for connecting to the database");
        }
        if ((driverName == null) || (driverName.equals(""))) {
            throw new DatabaseException("No driver given for connecting to the database");
        }
        try {
            Class.forName(driverName);
            Driver driver = DriverManager.getDriver(url);
            if (driver == null) {
                throw new DatabaseException("Could not locate JDBC Driver class for the url:\n" + url);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error locating JDBC Driver class for the url:\n" + url, e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("The class for '" + url + "' couldn't be loaded", e);
        }

        Connection connection = null;

        // connect to the DB
        try {
            connection = DriverManager.getConnection(url, account, password);
            printLogMessage("Created new DB connection to " + url);
        } catch (SQLException se) {
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
            while ((inputLine = in.readLine()) != null) {
                sqlCommand += inputLine;
            }
        } catch (Exception e) {
            throw new DatabaseException("Could not read SQL script from URL '" + sqlURL.toString() + "'.", e);
        }
        // submit the SQL
        executeSQLAsString(sqlCommand, sqlURL.toString());
        this.broker.processEvent(new DatabaseModifiedEvent(this, this));
    }

    public void executeSQLAsString(String sqlCommand, String descr) throws DatabaseException {
        Statement stmt;
        try {
            stmt = jdbcConnection.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Submitting script: " + descr);
            stmt.execute(sqlCommand);
            printLogMessage(System.currentTimeMillis() + ": done.");
        } catch (SQLException se) {
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
            stmt = jdbcConnection.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing statement: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            while (resultSet.next()) {
                result.add(resultSet.getString(column));
            }
        } catch (SQLException se) {
            throw new DatabaseException("An error occured while querying the database.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
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
        String statement = "SELECT ";
        Iterator it = fields.iterator();
        while (it.hasNext()) {
            String field = (String) it.next();
            statement += field;
            if (it.hasNext()) {
                statement += ", ";
            }
        }
        statement += " FROM " + tableName + " " + whereClause;

        return executeQuery(statement);
    }

    public List executeQuery(String statement) throws DatabaseException {
        List result = new LinkedList();
        ResultSet resultSet = null;
        Statement stmt = null;
        // submit the query
        try {
            stmt = jdbcConnection.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing query: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            int numberColumns = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Vector item = new Vector(numberColumns);
                for (int i = 0; i < numberColumns; i++) {
                    item.add(i, resultSet.getString(i + 1));
                }
                result.add(item);
            }
        } catch (SQLException se) {
            throw new DatabaseException("An error occured while querying the database.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
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
            stmt = jdbcConnection.createStatement();
            printLogMessage(System.currentTimeMillis() + ": Executing statement: " + statement);
            resultSet = stmt.executeQuery(statement);
            printLogMessage(System.currentTimeMillis() + ": done.");
            resultSet.next();
            result = resultSet.getInt(column);
        } catch (SQLException se) {
            throw new DatabaseException("An error occured while querying the database.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
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
            DatabaseMetaData dmd = jdbcConnection.getMetaData();
            ResultSet rs = dmd.getTables(null, null, "%", tableTypes);
            while (rs.next()) {
                result.add(rs.getString(3));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                        + ex.getMessage());
                System.err.println("SQLState:  "
                        + ex.getSQLState());
                System.err.println("ErrorCode: "
                        + ex.getErrorCode());
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
            DatabaseMetaData dmd = jdbcConnection.getMetaData();
            ResultSet rs = dmd.getTables(null, null, null, viewTypes);
            while (rs.next()) {
                result.add(rs.getString(3));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                        + ex.getMessage());
                System.err.println("SQLState:  "
                        + ex.getSQLState());
                System.err.println("ErrorCode: "
                        + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    int translateSQLType(String sqlType) {
        return Types.INTEGER;
    };

    /**
     * Returns a list of column objects.
     *
     * The parameter view can be either a table or a view.
     */
    public Vector getColumns(Table table) {
        Vector result = new Vector();

        try {
            DatabaseMetaData dmd = jdbcConnection.getMetaData();
            ResultSet rs = dmd.getColumns(null, null, table.getName(), null);
            while (rs.next()) {
                result.add(new Column(
                        rs.getString(4),
                        rs.getInt(5), table)
                );
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                        + ex.getMessage());
                System.err.println("SQLState:  "
                        + ex.getSQLState());
                System.err.println("ErrorCode: "
                        + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    public Vector getColumns(String table, String column) {
        Vector result = new Vector();

        try {
            DatabaseMetaData dmd = jdbcConnection.getMetaData();
            ResultSet rs = dmd.getColumns(null, null, table, null);
            while (rs.next()) {
                result.add(rs.getString(4));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                        + ex.getMessage());
                System.err.println("SQLState:  "
                        + ex.getSQLState());
                System.err.println("ErrorCode: "
                        + ex.getErrorCode());
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
    public Vector getColumn(String column, String table) {
        Vector result = new Vector();

        try {
            Statement stmt = jdbcConnection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT [" + column +
                    "] FROM [" + table + "]");

            while (resultSet.next()) {
                String value = resultSet.getString(1);
                result.add(value);
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   "
                        + ex.getMessage());
                System.err.println("SQLState:  "
                        + ex.getSQLState());
                System.err.println("ErrorCode: "
                        + ex.getErrorCode());
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
        if (logger != null) {
            logger.println(message);
        }
    }

    public void processEvent(Event e) {
        if (e instanceof DatabaseConnectEvent) {
            DatabaseConnectEvent event = (DatabaseConnectEvent) e;
            try {
                connect(event.getInfo());
            } catch (DatabaseException ex) {
                ErrorDialog.showError(
                        null,
                        ex,
                        "Unable to connect to database" + ex.getMessage(),
                        "Database Connection failed");
            }
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
    public static void main(String[] args) throws DatabaseException {
        if (args.length != 1) {
            System.err.println(
                    "Usage: DatabaseConnection [JDBC database url]");
            System.exit(1);
        }

        DatabaseConnection test = new DatabaseConnection(new EventBroker(), args[0], "", "", "");

        // print the tables
        System.out.println("The tables:\n-----------");

        // get the list of tables
        Vector tables = test.getTableNames();

        // print out each table
        for (int i = 0; i < tables.size(); i++) {
            System.out.println("========== " + tables.get(i) + " ==========");
            Vector columns = test.getColumns(
                    new Table(
                            new EventBroker(),
                            (String) tables.get(i))
            );
            // by printing each column
            for (int j = 0; j < columns.size(); j++) {
                Column column = (Column) columns.get(j);
                System.out.println("----- " + column.getName() + " -----");
                // and querying the contents
                System.out.println(test.getColumn((String) columns.get(j),
                        (String) tables.get(i)));
            }
        }

        // print the views
        System.out.println("The views:\n-----------");

        // get the list of views
        Vector views = test.getViewNames();

        // print out each view
        for (int i = 0; i < views.size(); i++) {
            System.out.println("========== " + views.get(i) + " ==========");
            Vector columns = test.getColumns(
                    new Table(
                            new EventBroker(),
                            (String) views.get(i))
            );
            // by printing each column
            for (int j = 0; j < columns.size(); j++) {
                Column column = (Column) columns.get(j);
                System.out.println("----- " + column.getName() + " -----");
                // and querying the contents
                System.out.println(test.getColumn(column.getName(),
                        (String) views.get(i)));
            }
        }
    }
}

