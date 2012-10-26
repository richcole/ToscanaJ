/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.database.DatabaseInfo.Type;
import net.sourceforge.toscanaj.model.events.DatabaseModifiedEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.plugin.DatabaseDriverLoader;
import org.tockit.plugin.DatabaseDriverLoader.Error;

/**
 * This class facilitates connection to and communication with a database via
 * JDBC.
 * 
 * @todo there is some chance for more reuse in the different queries, the
 *       logging/querying/exception handling code could be unified in one method
 */
public class DatabaseConnection implements EventBrokerListener<Object> {
    private static final String DEFAULT_DATABASE_DRIVER_LOCATION = "dbdrivers";
    private static final Preferences preferences = Preferences
    .userNodeForPackage(DatabaseConnection.class);

    /**
     * The JDBC database connection we use.
     */
    private Connection jdbcConnection = null;
    private final EventBroker<Object> broker;

    static private DatabaseConnection singleton = null;

    private final static Logger logger = Logger
    .getLogger(DatabaseConnection.class.getName());

    private Type type;

    private long lastStatementStartTime;

    static public void initialize(final EventBroker<Object> eventBroker) {
        singleton = new DatabaseConnection(eventBroker);
    }

    static public DatabaseConnection getConnection() {
        return singleton;
    }

    static public void setConnection(final DatabaseConnection connection) {
        singleton = connection;
    }

    /**
     * This constructor takes the data source as driver/url combination, an
     * account name and a password.
     * 
     * @todo Throw exceptions instead of just printing them.
     */
    public DatabaseConnection(final EventBroker<Object> broker,
            final String url, final String driver, final String account,
            final String password) throws DatabaseException {
        this.broker = broker;
        connect(url, driver, account, password);
    }

    public DatabaseConnection(final EventBroker<Object> broker,
            final Connection connection) {
        this.broker = broker;
        this.jdbcConnection = connection;
    }

    /**
     * Create a connection that isn't connected.
     */
    public DatabaseConnection(final EventBroker<Object> broker) {
        this.broker = broker;
        broker.subscribe(this, DatabaseConnectEvent.class, Object.class);
    }

    public void connect(final DatabaseInfo info) throws DatabaseException {
        if (this.isConnected()) {
            disconnect();
        }
        connect(info.getURL(), info.getDriverClass(), info.getUserName(), info
                .getPassword());
    }

    public void disconnect() throws DatabaseException {
        if (this.jdbcConnection == null) {
            throw new DatabaseException(
            "Disconnect requested but we are not connected.");
        }
        try {
            this.jdbcConnection.close();
            this.jdbcConnection = null;
            logger.fine("Disconnected");
        } catch (final SQLException e) {
            throw new DatabaseException(
                    "Could not disconnect from the database.", e);
        }
    }

    public boolean isConnected() {
        return this.jdbcConnection != null;
    }

    public void connect(final String url, final String driverName,
            final String account, final String password)
    throws DatabaseException {
        this.jdbcConnection = getConnection(url, driverName, account, password);
        // / @todo we probably could just ask JDBC if it is Access
        this.type = DatabaseInfo.getType(url, driverName);
        this.broker.processEvent(new DatabaseConnectedEvent(this, this));
    }

    private static Connection getConnection(final String url,
            final String driverName, final String account, final String password)
    throws DatabaseException {
        if ((url == null) || (url.equals(""))) {
            throw new DatabaseException(
            "No URL given for connecting to the database");
        }
        if ((driverName == null) || (driverName.equals(""))) {
            throw new DatabaseException(
            "No driver given for connecting to the database");
        }
        try {
            Class.forName(driverName);
        } catch (final ClassNotFoundException e) {
            final String dbDriverLocation = preferences.get("driverDirectory",
                    DEFAULT_DATABASE_DRIVER_LOCATION);
            final DatabaseDriverLoader.Error[] errors = DatabaseDriverLoader
            .loadDrivers(new File(dbDriverLocation));
            for (final Error error : errors) {
                logger.log(Level.WARNING,
                        "Error when loading database drivers", error
                        .getException());
            }
        }

        Connection connection;
        try {
            final Properties connectionProperties = new Properties();
            connectionProperties.setProperty("user", account);
            connectionProperties.setProperty("password", password);
            // hsqldb does not remove old tables since version 1.7.2 unless we tell
            // it explicitly to shut down after disconnect (requires 1.8.0+)
            if (DatabaseInfo.getType(url, driverName) == DatabaseInfo.EMBEDDED) {
                connectionProperties.setProperty("shutdown", "true");
            }
            connection = DriverManager.getConnection(url, connectionProperties);
            logger.fine("Created new DB connection to " + url);
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured connecting to the database", se);
        }
        return connection;
    }

    /**
     * Loads an SQL script into the database.
     */
    public void executeScript(final URL sqlURL) throws DatabaseException {
        // we have to concatenate a couple of lines sometimes since the SQL
        // commands don't have to be on one line
        logger.fine(System.currentTimeMillis() + ": Submitting script: "
                + sqlURL.toString());
        String sqlCommand = "";
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(
                    sqlURL.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sqlCommand = (sqlCommand + inputLine).trim();
                if ((sqlCommand.length() != 0)
                        && sqlCommand.charAt(sqlCommand.length() - 1) == ';') {
                    executeSQLAsString(sqlCommand);
                    sqlCommand = "";
                }
            }
        } catch (final Exception e) {
            throw new DatabaseException("Could not read SQL script from URL '"
                    + sqlURL.toString() + "'.", e);
        }
        logger.fine(System.currentTimeMillis() + ": done.");
        this.broker.processEvent(new DatabaseModifiedEvent(this, this));
    }

    public void executeSQLAsString(final String sqlCommand)
    throws DatabaseException {
        Statement stmt;
        try {
            stmt = this.jdbcConnection.createStatement();
            stmt.execute(sqlCommand);
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured while processing the DB script.", se);
        }
    }

    /**
     * Retrieves a specific column from a query as a list of strings.
     */
    public List<String> queryColumn(final String statement, final int column)
    throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        final List<String> result = new ArrayList<String>();

        // submit the query
        try {
            stmt = this.jdbcConnection.createStatement();
            logStatementStart(statement);
            resultSet = stmt.executeQuery(statement);
            logStatementEnd();
            while (resultSet.next()) {
                result.add(resultSet.getString(column));
            }
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured while querying the database.\nThe statement \""
                    + statement + "\" failed.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                // can't do anything here
            }
        }
        return result;
    }

    /**
     * Expects a list of field names and a where clause and returns all matches.
     * 
     * The return value is a list (matching rows) of string arrays (fields).
     */
    public List<String[]> executeQuery(final List<String> fields,
            final String tableName, final String whereClause)
            throws DatabaseException {
        String statement = "SELECT ";
        final Iterator<String> it = fields.iterator();
        while (it.hasNext()) {
            final String field = it.next();
            statement += field;
            if (it.hasNext()) {
                statement += ", ";
            }
        }
        statement += " FROM " + tableName + " " + whereClause;

        return executeQuery(statement);
    }

    public List<String[]> executeQuery(final String statement)
    throws DatabaseException {
        final List<String[]> result = new ArrayList<String[]>();
        ResultSet resultSet = null;
        Statement stmt = null;
        // submit the query
        try {
            stmt = this.jdbcConnection.createStatement();
            logStatementStart(statement);
            resultSet = stmt.executeQuery(statement);
            logStatementEnd();
            final int numberColumns = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                final String[] item = new String[numberColumns];
                for (int i = 0; i < numberColumns; i++) {
                    item[i] = resultSet.getString(i + 1);
                }
                result.add(item);
            }
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured while querying the database.\nThe statement \""
                    + statement + "\" failed.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                // can't do anything here
            }
        }
        return result;
    }

    public int executeUpdate(final String statement) throws DatabaseException {
        int result;
        Statement stmt = null;
        // submit the query
        try {
            stmt = this.jdbcConnection.createStatement();
            logStatementStart(statement);
            result = stmt.executeUpdate(statement);
            logStatementEnd();
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured while querying the database.\nThe statement \""
                    + statement + "\" failed.", se);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                // can't do anything here
            }
        }
        return result;
    }

    /**
     * Retrieves the first value of the given column as integer.
     */
    public int queryInt(final String statement, final int column)
    throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        int result;

        // submit the query
        try {
            stmt = this.jdbcConnection.createStatement();
            logStatementStart(statement);
            resultSet = stmt.executeQuery(statement);
            logStatementEnd();
            resultSet.next();
            result = resultSet.getInt(column);
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured while querying the database.\nThe statement \""
                    + statement + "\" failed.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                // nothing we can do here
            }
        }

        return result;
    }

    /**
     * Retrieves the first value of the given column as double.
     */
    public double queryDouble(final String statement, final int column)
    throws DatabaseException {
        ResultSet resultSet = null;
        Statement stmt = null;
        double result;

        // submit the query
        try {
            stmt = this.jdbcConnection.createStatement();
            logStatementStart(statement);
            resultSet = stmt.executeQuery(statement);
            logStatementEnd();
            resultSet.next();
            result = resultSet.getDouble(column);
        } catch (final SQLException se) {
            throw new DatabaseException(
                    "An error occured while querying the database.\nThe statement \""
                    + statement + "\" failed.", se);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                // nothing we can do here
            }
        }

        return result;
    }

    /**
     * Returns a list containing all available database names -- nyi.
     * 
     * This class is not yet implemented since I still didn't find the right
     * methods to do this. The ODBC function is called SQLDataSources but it
     * seems the concept is not available in JDBC.
     * 
     * One way to solve this would be implementing a native method for usage
     * with JNI -- but this is not a good way since we loose platform
     * independence in doing this.
     */
    public List<String> getDatabaseNames() {
        return new ArrayList<String>();
    }

    /**
     * Returns a list containing the table names for the current database.
     */
    public List<String> getTableNames() {
        final List<String> result = new ArrayList<String>();
        final String[] tableTypes = { "TABLE" };

        try {
            final DatabaseMetaData dmd = this.jdbcConnection.getMetaData();
            final ResultSet rs = dmd.getTables(null, null, "%", tableTypes);
            while (rs.next()) {
                result.add(rs.getString(3));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   " + ex.getMessage());
                System.err.println("SQLState:  " + ex.getSQLState());
                System.err.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Returns a String list containing the view names for the current database.
     */
    public List<String> getViewNames() {
        final List<String> result = new ArrayList<String>();
        final String[] viewTypes = { "VIEW" };

        try {
            final DatabaseMetaData dmd = this.jdbcConnection.getMetaData();
            final ResultSet rs = dmd.getTables(null, null, null, viewTypes);
            while (rs.next()) {
                result.add(rs.getString(3));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   " + ex.getMessage());
                System.err.println("SQLState:  " + ex.getSQLState());
                System.err.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Returns a list of column objects.
     * 
     * The parameter view can be either a table or a view.
     */
    public List<Column> getColumns(final Table table) {
        final List<Column> result = new ArrayList<Column>();

        try {
            final DatabaseMetaData dmd = this.jdbcConnection.getMetaData();
            final ResultSet rs = dmd.getColumns(null, null, table
                    .getDisplayName(), null);
            while (rs.next()) {
                result.add(new Column(rs.getString(4), rs.getInt(5), table));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   " + ex.getMessage());
                System.err.println("SQLState:  " + ex.getSQLState());
                System.err.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Returns a list of column names.
     * 
     * The input parameter can be the name of either a table or a view.
     */
    public List<String> getColumns(final String table) {
        final List<String> result = new ArrayList<String>();

        try {
            final DatabaseMetaData dmd = this.jdbcConnection.getMetaData();
            final ResultSet rs = dmd.getColumns(null, null, table, null);
            while (rs.next()) {
                result.add(rs.getString(4));
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   " + ex.getMessage());
                System.err.println("SQLState:  " + ex.getSQLState());
                System.err.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    /**
     * Returns a list containing the contents of the given column.
     * 
     * The parameter view can be either a table or a view.
     * 
     * The method "getString" can retrieve any of the basic SQL types (however
     * you cannot retrieve the new SQL3 datatypes with it)
     */
    public List<String> getColumn(final String column, final String table) {
        final List<String> result = new ArrayList<String>();

        try {
            final Statement stmt = this.jdbcConnection.createStatement();
            final ResultSet resultSet = stmt.executeQuery("SELECT [" + column
                    + "] FROM [" + table + "]");

            while (resultSet.next()) {
                final String value = resultSet.getString(1);
                result.add(value);
            }
        } catch (SQLException ex) {
            System.err.println("\n--- SQLException caught ---\n");
            while (ex != null) {
                System.err.println("Message:   " + ex.getMessage());
                System.err.println("SQLState:  " + ex.getSQLState());
                System.err.println("ErrorCode: " + ex.getErrorCode());
                ex = ex.getNextException();
                System.err.println();
            }
        }

        return result;
    }

    public void processEvent(final Event<?> e) {
        if (e instanceof DatabaseConnectEvent) {
            final DatabaseConnectEvent event = (DatabaseConnectEvent) e;
            try {
                connect(event.getInfo());
            } catch (final DatabaseException ex) {
                ErrorDialog.showError(null, ex, "Unable to connect to database"
                        + ex.getMessage(), "Database Connection failed");
            }
        }
    }

    /**
     * Main method for testing the class.
     * 
     * The main method expects exactly one argument, which is the name of an
     * ODBC source. This source is queried for all tables/view, each of them is
     * printed to stdout with each column and each entry, so be cautious on
     * which kind of DB you use the function ;-)
     */
    public static void main(final String[] args) throws DatabaseException {
        if (args.length != 2) {
            System.err
            .println("Usage: DatabaseConnection [JDBC driver class] [JDBC database url]");
            System.exit(1);
        }

        final DatabaseConnection test = new DatabaseConnection(
                new EventBroker<Object>(), args[1], args[0], "", "");

        // print the tables
        System.out.println("The tables:\n-----------");

        // get the list of tables
        final List<String> tables = test.getTableNames();

        // print out each table
        for (String table : tables) {
            System.out.println("========== " + table + " ==========");
            final List<Column> columns = test.getColumns(new Table(
                    new EventBroker<Object>(), table, false));
            // by printing each column
            for (final Column column : columns) {
                System.out.println("----- " + column.getDisplayName()
                        + " -----");
                // and querying the contents
                System.out.println(test.getColumn(column.getDisplayName(),
                        table));
            }
        }

        // print the views
        System.out.println("The views:\n-----------");

        // get the list of views
        final List<String> views = test.getViewNames();

        // print out each view
        for (int i = 0; i < views.size(); i++) {
            System.out.println("========== " + views.get(i) + " ==========");
            final List<Column> columns = test.getColumns(new Table(
                    new EventBroker<Object>(), views.get(i), false));
            // by printing each column
            for (final Column column : columns) {
                System.out.println("----- " + column.getDisplayName()
                        + " -----");
                // and querying the contents
                System.out.println(test.getColumn(column.getDisplayName(),
                        views.get(i)));
            }
        }
    }

    public DatabaseInfo.Type getDatabaseType() {
        return this.type;
    }

    private void logStatementStart(final String statement) {
        this.lastStatementStartTime = System.currentTimeMillis();
        logger.fine(this.lastStatementStartTime + ": Executing statement: "
                + statement);
    }

    private void logStatementEnd() {
        final long statementStopTime = System.currentTimeMillis();
        logger.fine(statementStopTime + ": done ("
                + (statementStopTime - this.lastStatementStartTime) + " ms).");
    }

    public DatabaseMetaData getDatabaseMetaData() throws DatabaseException {
        try {
            return this.jdbcConnection.getMetaData();
        } catch (final SQLException e) {
            throw new DatabaseException("Could not get Database Metadata", e);
        }
    }

    /**
     * Returns a Collection of SQLTypeInfo objects.
     */
    public Collection<SQLTypeInfo> getDatabaseSupportedTypeNames()
    throws DatabaseException {
        final Collection<SQLTypeInfo> result = new ArrayList<SQLTypeInfo>();
        try {
            final DatabaseMetaData dbMetadata = getDatabaseMetaData();

            final ResultSet typeInfo = dbMetadata.getTypeInfo();

            while (typeInfo.next()) {
                // according to java documentation the first column should
                // contain TYPE_NAME and the second column should contain
                // DATA_TYPE.
                result.add(new SQLTypeInfo(typeInfo.getInt(2), typeInfo.getString(1)));
            }
            return result;
        } catch (final SQLException e) {
            throw new DatabaseException(
                    "Error retrieving types supported by database", e);
        }

    }

    public Connection getJdbcConnection() {
        return this.jdbcConnection;
    }
}
