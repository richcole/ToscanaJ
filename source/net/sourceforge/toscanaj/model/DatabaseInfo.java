package net.sourceforge.toscanaj.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    private String userName = null;

    private String password = null;

    public static abstract class DatabaseQuery extends Query {
        public class Column {
            String name;
            String format;
            String separator;
            String queryPart;
        };

        public String header;
        public List columnList = new LinkedList();

        public DatabaseQuery(String name, String header) {
            super(name);
            this.header = header;
        }

        public void insertQueryColumn(String columnName, String columnFormat,
            String separator, String queryPart)
        {
            Column col = new Column();
            col.name = columnName;
            col.format = columnFormat;
            col.separator = separator;
            col.queryPart = queryPart;
            columnList.add(col);
        };

        /**
         * Formats a row of a result set for this query.
         *
         * The input is a ResultSet which is supposed to point to an existing
         * row. Column one is supposed to be the first column of the query
         * definition and so on.
         *
         * The return value is a String which returns a formatted version of the
         * row
         */
        public String formatResults(ResultSet results) throws SQLException {
            String rowRes = new String();
            if(header != null) {
                rowRes += header;
            }
            Iterator colDefIt = this.columnList.iterator();
            // skip key, start with 1
            int i = 1;
            while(colDefIt.hasNext()) {
                Column col = (Column)colDefIt.next();
                i++;
                if(col.format != null) {
                    DecimalFormat format = new DecimalFormat(col.format);
                    rowRes += format.format(results.getDouble(i));
                }
                else {
                    rowRes += results.getString(i);
                }
                if(col.separator != null) {
                    rowRes += col.separator;
                }
            }
            return rowRes;
        }

        abstract public String getQueryHead();
    };

    protected class ListQuery extends DatabaseQuery {
        public boolean isDistinct;

        public ListQuery(String name, String header, boolean isDistinct) {
            super(name,header);
            this.isDistinct = isDistinct;
        }

        public String getQueryHead() {
            String retValue = "SELECT ";
            if ( isDistinct ) {
                retValue += "DISTINCT ";
            }
            retValue += objectKey + ", ";
            Iterator it = columnList.iterator();
            while(it.hasNext()) {
                Column col = (Column) it.next();
                retValue += col.queryPart;
                if(it.hasNext()) {
                    retValue += ", ";
                }
            }
            retValue += " FROM " + table + " ";
            return retValue;
        };
    };

    protected class AggregateQuery extends DatabaseQuery {

        public AggregateQuery(String name, String header) {
            super(name, header);
        }

        public String getQueryHead() {
            // this gives an additional column replacing the key (used only in lists)
            String retValue = "SELECT count(*),";
            Iterator it = columnList.iterator();
            while(it.hasNext()) {
                Column col = (Column) it.next();
                retValue += col.queryPart;
                if(it.hasNext()) {
                    retValue += ", ";
                }
            }
            retValue += " FROM " + table + " ";
            return retValue;
        };
    };

    /**
     * Creates a new Query that will query a list.
     */
    public DatabaseQuery createListQuery(String name, String header, boolean isDistinct) {
        return new ListQuery(name, header, isDistinct);
    };

    /**
     * Creates a new Query that will query a single number as aggregate.
     */
    public DatabaseQuery createAggregateQuery(String name, String header) {
        return new AggregateQuery(name, header);
    };

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
     *
     * @deprecated
     */
    public String getQuery() {
        return "SELECT " + this.objectKey + " FROM " + this.table + " ";
    }

    /**
     * Returns the query string used for counting the objects.
     *
     * This should be always of the form "SELECT count(*) FROM x" where x is the
     * table used. The where clauses will be added at the end.
     *
     * @deprecated
     */
    public String getCountQuery() {
        return "SELECT count(*) FROM " + this.table + " ";
    }

    /**
     * Sets the given URL as DB connecion point.
     */
    public void setUrl( String url ) {
        this.source = url;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return this.userName;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the database table we want to query.
     *
     * This can be a view, too.
     */
    public void setTable( String table ) {
        this.table = table;
    }

    /**
     * Sets the key we use in queries.
     */
    public void setKey( String key ) {
        this.objectKey = key;
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