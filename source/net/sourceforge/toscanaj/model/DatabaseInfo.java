package net.sourceforge.toscanaj.model;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * This class contains information how to connect to a database.
 */
public class DatabaseInfo
{
    /**
     * The source where the database can be found.
     *
     * This is a JDBC url.
     */
    private String source = null;

    /**
     * The table (or view) queried in the database.
     */
    private String table = null;

    /**
     * The key used for the object names.
     */
    private String objectKey = null;

    /**
     * The list of additional queries available as view option.
     */
    private Hashtable specialQueries = new Hashtable();

    /**
     * The list of format used for the results of the additional queries.
     */
    private Hashtable specialQueryFormats = new Hashtable();

    /**
     * Creates an empty instance.
     *
     * Type is set to TYPE_UNDEFINED, the strings are all null.
     */
    public DatabaseInfo() {
    }

    /**
     * Returns the source where the database can be found.
     *
     * If type is TYPE_DSN this is the name of a data source that should be
     * defined in the system. If type is TYPE_FILE it is a filename.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Returns the query string used for getting the objects.
     *
     * This should be always of the form "SELECT x FROM y" where x is the key
     * and y the table used. The where clauses will be added at the end.
     */
    public String getQuery() {
        return "SELECT [" + this.objectKey + "] FROM [" + this.table + "] ";
    }

    /**
     * Returns the query string used for counting the objects.
     *
     * This should be always of the form "SELECT count(x) FROM y" where x is the key
     * and y the table used. The where clauses will be added at the end.
     */
    public String getCountQuery() {
        return "SELECT count(" + " [" + this.objectKey + "] ) FROM [" + this.table + "] ";
    }

    /**
     * Returns all names of additional queries.
     */
    public Iterator getSpecialQueryNames() {
        return this.specialQueries.keySet().iterator();
    }

    /**
     * Returns a complete query for a given name.
     */
    public String getSpecialQuery(String name) {
        return "SELECT " + this.specialQueries.get(name) + " FROM [" + this.table + "] ";
    }

    /**
     * Returns the format for the results of a special query with the given name.
     */
    public String getSpecialQueryFormat(String name) {
        return (String)this.specialQueryFormats.get(name);
    }

    /**
     * Sets the given URL as DB connecion point.
     */
    public void setUrl( String url ) {
        this.source = url;
    }

    /**
     * Sets the query string to the given SQL subcommand.
     *
     * This should be always of the form "SELECT x FROM y" where x is the key
     * and y the table used. The where clauses will be added at the end.
     */
    public void setQuery( String sql ) {
        this.table = "not yet";
        this.objectKey = "implemented";
        /// @TODO Implement something that calculates table and key from the query string
    }

    /**
     * Sets the query string to use the given table/key combination.
     *
     * Both table and key name can be given with the square brackets often used
     * in SQL, but they don't have to.
     */
    public void setQuery( String table, String key ) {
        this.table = table;
        while(this.table.charAt(0) == '[') {
            this.table = this.table.substring(1);
        }
        while(this.table.charAt(this.table.length()-1) == ']') {
            this.table = this.table.substring(0,this.table.length()-1);
        }
        this.objectKey = key;
        while(this.objectKey.charAt(0) == '[') {
            this.objectKey = this.objectKey.substring(1);
        }
        while(this.objectKey.charAt(this.objectKey.length()-1) == ']') {
            this.objectKey = this.objectKey.substring(0,this.objectKey.length()-1);
        }
    }

    /**
     * Adds a special query to the list of available queries.
     *
     * Special queries are given as SQL expressions like "AVG(price)" and are
     * accessed using a unique name which is also used as menu entry for this
     * query in the view menu.
     *
     * The format parameter can be null if the results should be presented as
     * strings, if the format is not null, it will be used to format the result
     * as a double, using an instance of the DecimalFormat class.
     *
     * @see java.text.DecimalFormat
     */
    public void addSpecialQuery(String name, String query, String format) {
        this.specialQueries.put(name, query);
        this.specialQueryFormats.put(name, format);
    }

    /**
     * Debugging info.
     */
    public String toString() {
        String result = "DatabaseInfo\n";

        result += "\t" + "url: " + this.source + "\n" +
                  "\t" + "key/table: " + this.objectKey + "/" +this.table;

        return result;
    }
}