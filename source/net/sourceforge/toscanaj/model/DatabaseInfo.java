package net.sourceforge.toscanaj.model;

import java.util.Hashtable;
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

    public abstract class DatabaseQuery extends Query {
        public class Column {
            String name;
            String format;
            String separator;
            String queryPart;
        };

        public String header;
        public List columnList = new LinkedList();

        public DatabaseQuery(String name) {
            super(name);
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

        abstract public String getQueryHead();
    };

    protected class ListQuery extends DatabaseQuery {
        public boolean isDistinct;

        public ListQuery(String name) {
            super(name);
        }

        public String getQueryHead() {
            String   retValue = "SELECT ";
            if ( isDistinct ) {
                retValue += "DISTINCT ";
            }
            Iterator it = columnList.iterator();
            while(it.hasNext()) {
                Column col = (Column) it.next();
                retValue += col.queryPart;
                if(it.hasNext()) {
                    retValue += ", ";
                }
            }
            retValue += " FROM [" + table + "]";
            return retValue;
        };
    };

    protected class AggregateQuery extends DatabaseQuery {

        public AggregateQuery(String name) {
            super(name);
        }

        public String getQueryHead() {
            String   retValue = "SELECT ";
            Iterator it = columnList.iterator();
            while(it.hasNext()) {
                Column col = (Column) it.next();
                retValue += col.queryPart;
                if(it.hasNext()) {
                    retValue += ", ";
                }
            }
            retValue += " FROM [" + table + "]";
            return retValue;
        };
    };

    /**
     * Creates a new Query that will query a list.
     */
    public DatabaseQuery createListQuery(String name, boolean isDistinct) {
        ListQuery query = new ListQuery(name);
        query.isDistinct = isDistinct;
        return query;
    };

    /**
     * Creates a new Query that will query a single number as aggregate.
     */
    public DatabaseQuery createAggregateQuery(String name) {
        return new AggregateQuery(name);
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
        return "SELECT [" + this.objectKey + "] FROM [" + this.table + "] ";
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
        return "SELECT count(*) FROM [" + this.table + "] ";
    }

    /**
     * Sets the given URL as DB connecion point.
     */
    public void setUrl( String url ) {
        this.source = url;
    }

    /**
     * Sets the database table we want to query.
     *
     * This can be a view, too.
     */
    public void setTable( String table ) {
        this.table = table;
        while(this.table.charAt(0) == '[') {
            this.table = this.table.substring(1);
        }
        while(this.table.charAt(this.table.length()-1) == ']') {
            this.table = this.table.substring(0,this.table.length()-1);
        }
    }

    /**
     * Sets the key we use in queries.
     */
    public void setKey( String key ) {
        this.objectKey = key;
        while(this.objectKey.charAt(0) == '[') {
            this.objectKey = this.objectKey.substring(1);
        }
        while(this.objectKey.charAt(this.objectKey.length()-1) == ']') {
            this.objectKey = this.objectKey.substring(0,this.objectKey.length()-1);
        }
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