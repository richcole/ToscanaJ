package net.sourceforge.toscanaj.model;

/**
 * This class contains information how to connect to a database.
 */
public class DatabaseInfo
{
    /**
     * If this constant is used, the type of the database has not yet been
     * defined.
     */
    public static final int TYPE_UNDEFINED = 0;

    /**
     * Use this constant to define/query a connection using a Data Source Name
     * (DSN).
     */
    public static final int TYPE_DSN = 1;

    /**
     * Use this constant to define/query a connection using a path to access
     * a file.
     */
    public static final int TYPE_FILE = 2;

    /**
     * The type of the database -- either TYPE_DSN or TYPE_FILE.
     */
    private int _type = TYPE_UNDEFINED;

    /**
     * The source where the database can be found.
     *
     * If type is TYPE_DSN this is the name of a data source that should be
     * defined in the system. Otherwise it is a filename.
     */
    private String _source = null;

    /**
     * The table (or view) queried in the database.
     */
    private String _table = null;

    /**
     * The key used for the object names.
     */
    private String _key = null;

    /**
     * Creates an empty instance.
     *
     * Type is set to TYPE_UNDEFINED, the strings are all null.
     */
    public DatabaseInfo()
    {
    }

    /**
     * Returns the type of the database.
     *
     *  This is TYPE_DSN for DSN access, TYPE_FILE for file access or
     *  TYPE_UNDEFINED if the type is not yet known.
     */
    public int getType()
    {
        return _type;
    }

    /**
     * Returns the source where the database can be found.
     *
     * If type is TYPE_DSN this is the name of a data source that should be
     * defined in the system. If type is TYPE_FILE it is a filename.
     */
    public String getSource()
    {
        return _source;
    }

    /**
     * Returns the query string used for getting the objects.
     *
     * This should be always of the form "SELECT x FROM y" where x is the key
     * and y the table used. The where clauses will be added at the end.
     */
    public String getQuery()
    {
        return "SELECT [" + _key + "] FROM [" + _table + "] ";
    }

    /**
     * Returns the query string used for counting the objects.
     *
     * This should be always of the form "SELECT count(x) FROM y" where x is the key
     * and y the table used. The where clauses will be added at the end.
     */
    public String getCountQuery()
    {
        return "SELECT count(" + " [" + _key + "] ) FROM [" + _table + "] ";
    }

    /**
     * Sets the database to use DSN access to the given DSN.
     */
    public void setDSN( String dsn )
    {
        _type = TYPE_DSN;
        _source = dsn;
    }

    /**
     * Sets the database to use file access to the given file.
     */
    public void setDatabaseFile( String file )
    {
        _type = TYPE_FILE;
        _source = file;
    }

    /**
     * Sets the query string to the given SQL subcommand.
     *
     * This should be always of the form "SELECT x FROM y" where x is the key
     * and y the table used. The where clauses will be added at the end.
     */
    public void setQuery( String sql )
    {
        _table = "not yet";
        _key = "implemented";
        /// @TODO Implement something that calculates table and key from the query string
    }

    /**
     * Sets the query string to use the given table/key combination.
     */
    public void setQuery( String table, String key )
    {
        _table = table;
        _key = key;
    }

    /**
     * Prints contents as String.
     */
    public String toString()
    {
        String result = "DatabaseInfo";

        if( _type == TYPE_DSN )
        {
            result += "(DSN): " + _source + "\n" +
                      "\t" + "key/table: " + _key + "/" +_table;
        }
        else if( _type == TYPE_FILE )
        {
            result += "(File): " + _source + "\n" +
                      "\t" + "key/table: " + _key + "/" +_table;
        }
        else if( _type == TYPE_UNDEFINED )
        {
            result += "(undefined)";
        }
        else
        {
            result += "(unknown)";
        }

        return result;
    }
}