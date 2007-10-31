/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class contains information how to connect to a database.
 */
public class DatabaseInfo implements XMLizable {
    /// @todo yet another hack that should go away after ConceptInterpreter is done
    static public URL baseURL;
    /**
     * The source where the database can be found.
     *
     * This is a JDBC url.
     */
    private String sourceURL = null;

    /**
     * The table (or view) queried in the database.
     */
    private Table table = null;

    /**
     * The key used for the object names.
     */
    private Column objectKey = null;

    private String userName = null;

    private String password = null;

    private URL embeddedSQLLocation = null;
    private String embeddedSQLPath = null;

    private String driverClass = null;

    public static class Type {
        private String name;
        protected Type(String name) {
            this.name = name;
        }
        @Override
		public String toString() {
            return name;
        }
    }

    public static final Type UNDEFINED = new Type("UNDEFINED");
    public static final Type EMBEDDED = new Type("EMBEDDED");
    public static final Type JDBC = new Type("JDBC");
    public static final Type ODBC = new Type("ODBC");
    public static final Type ACCESS_FILE = new Type("ACCESS_FILE");
    public static final Type EXCEL_FILE = new Type("ACCESS_FILE");

    private static final String ODBC_PREFIX = "jdbc:odbc:";
    private static final String ACCESS_FILE_URL_PREFIX =
        "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb); DBQ=";
    private static final String ACCESS_FILE_URL_END =
        ";UserCommitSync=Yes;Threads=3;SafeTransactions=0;PageTimeout=5;"
            + "MaxScanRows=8;MaxBufferSize=2048;DriverId=281";
    private static final String EXCEL_FILE_URL_PREFIX =
        "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=";
    private static final String EXCEL_FILE_URL_END =
        ";DriverID=22;READONLY=true";

    private static final String JDBC_ODBC_BRIDGE_DRIVER =
        "sun.jdbc.odbc.JdbcOdbcDriver";

    public static final String DATABASE_CONNECTION_ELEMENT_NAME =
        "databaseConnection";
    private static final String EMBEDDED_SOURCE_ELEMENT_NAME = "embed";
    private static final String URL_SOURCE_ELEMENT_NAME = "url";
    private static final String DRIVER_CLASS_ATTRIBUTE_NAME = "driver";
    private static final String USERNAME_ATTRIBUTE_NAME = "user";
    private static final String PASSWORD_ATTRIBUTE_NAME = "password";
    private static final String EMBEDDED_URL_ATTRIBUTE_NAME = "url";
	private static final String TABLE_ELEMENT_NAME = "table";
	private static final String OBJECT_KEY_ELEMENT_NAME = "key";

    public static DatabaseInfo getEmbeddedDatabaseInfo() {
        DatabaseInfo info = new DatabaseInfo();
        info.setUrl("jdbc:hsqldb:.");
        info.setDriverClass("org.hsqldb.jdbcDriver");
        info.setUserName("sa");
        info.setPassword("");
        return info;
    }

    /**
     * Creates a new Query that will query a list.
     */
    public Query createListQuery(
        String name,
        String header,
        boolean isDistinct) {
        if (isDistinct) {
            return new DistinctListQuery(this, name, header);
        } else {
            return new ListQuery(this, name, header);
        }
    }

    /**
     * Creates a new Query that will query a single number as aggregate.
     */
    public Query createAggregateQuery(String name, String header) {
        return new AggregateQuery(this, name, header);
    }

    /**
     * Creates an empty instance.
     */
	public DatabaseInfo() {
		// nothing to initialize here
	}

	public DatabaseInfo(DatabaseInfo other) {
		this.sourceURL = other.sourceURL;
		this.table = other.table;
		this.objectKey = other.objectKey;
		this.userName = other.userName;
		this.password = other.password;
		this.embeddedSQLLocation = other.embeddedSQLLocation;
		this.embeddedSQLPath = other.embeddedSQLPath;
		this.driverClass = other.driverClass;
	}

    public DatabaseInfo(Element element) throws XMLSyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(DATABASE_CONNECTION_ELEMENT_NAME);
        if (embeddedSQLPath != null) {
            Element embedElem = new Element(EMBEDDED_SOURCE_ELEMENT_NAME);
            embedElem.setAttribute(
                EMBEDDED_URL_ATTRIBUTE_NAME,
                embeddedSQLPath);
            retVal.addContent(embedElem);
        } else {
            Element urlElem = new Element(URL_SOURCE_ELEMENT_NAME);
            urlElem.addContent(sourceURL);
            urlElem.setAttribute(DRIVER_CLASS_ATTRIBUTE_NAME, driverClass);
            urlElem.setAttribute(USERNAME_ATTRIBUTE_NAME, getUserName());
            urlElem.setAttribute(PASSWORD_ATTRIBUTE_NAME, getPassword());
            retVal.addContent(urlElem);
        }
        retVal.addContent(this.table.toXML());
        retVal.addContent(this.objectKey.toXML(OBJECT_KEY_ELEMENT_NAME));
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, DATABASE_CONNECTION_ELEMENT_NAME);
        if (XMLHelper.contains(elem, EMBEDDED_SOURCE_ELEMENT_NAME)) {
            Element embedElem = elem.getChild(EMBEDDED_SOURCE_ELEMENT_NAME);
            setEmbeddedSQLLocation(
                XMLHelper
                    .getAttribute(embedElem, EMBEDDED_URL_ATTRIBUTE_NAME)
                    .getValue());
            setUrl("jdbc:hsqldb:.");
            setDriverClass("org.hsqldb.jdbcDriver");
            setUserName("sa");
            setPassword("");
        } else {
            Element urlElement =
                XMLHelper.getMandatoryChild(elem, URL_SOURCE_ELEMENT_NAME);
            sourceURL = urlElement.getTextNormalize();
            driverClass =
                XMLHelper
                    .getAttribute(urlElement, DRIVER_CLASS_ATTRIBUTE_NAME)
                    .getValue();
            userName =
                XMLHelper
                    .getAttribute(urlElement, USERNAME_ATTRIBUTE_NAME)
                    .getValue();
            password =
                XMLHelper
                    .getAttribute(urlElement, PASSWORD_ATTRIBUTE_NAME)
                    .getValue();
        }
        this.table = new Table(XMLHelper.getMandatoryChild(elem, TABLE_ELEMENT_NAME));
        this.objectKey = new Column(XMLHelper.getMandatoryChild(elem, OBJECT_KEY_ELEMENT_NAME), this.table);
    }

    /**
     * Returns the JDBC url for connecting to the database.
     */
    public String getURL() {
        return this.sourceURL;
    }

    /**
     * Sets the given URL as DB connecion point.
     */
    public void setUrl(String url) {
        this.sourceURL = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        if (this.userName == null) {
            return "";
        }
        return this.userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        if (this.password == null) {
            return "";
        }
        return this.password;
    }

    /**
     * Sets the database table we want to query.
     */
    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return this.table;
    }

    /**
     * Sets the key we use in queries.
     */
    public void setKey(Column key) {
        this.objectKey = key;
    }

    public Column getKey() {
        return this.objectKey;
    }

    public void setEmbeddedSQLLocation(String relativePath) {
        if (relativePath == null) {
            this.embeddedSQLLocation = null;
            this.embeddedSQLPath = null;
        } else {
            this.embeddedSQLLocation = resolveLocation(relativePath);
            this.embeddedSQLPath = relativePath;
        }
    }

    public void setEmbeddedSQLLocation(URL url) {
        this.embeddedSQLLocation = url;
        if (url == null) {
            this.embeddedSQLPath = null;
        } else {
            this.embeddedSQLPath = url.getPath();
        }
    }

    private URL resolveLocation(String relativePath) {
        try {
            return new URL(baseURL, relativePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException(
                "Could not create URL for database: " + relativePath);
        }
    }

    public URL getEmbeddedSQLLocation() {
        return this.embeddedSQLLocation;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    /**
     * Debugging info.
     */
    @Override
	public String toString() {
        String result = "DatabaseInfo\n";

        result += "\t"
            + "url: "
            + this.sourceURL
            + "\n"
            + "\t"
            + "key/table: "
            + this.objectKey
            + "/"
            + this.table;

        return result;
    }

    public Type getType() {
        return getType(this.getURL(), this.getDriverClass());
    }

    public static Type getType(String url, String driverClass) {
        if (url == null) {
            return UNDEFINED;
        }
        if (url.equals(getEmbeddedDatabaseInfo().getURL())) {
            return EMBEDDED;
        }
        if (driverClass.equals(JDBC_ODBC_BRIDGE_DRIVER)) {
            if (url.indexOf(';') == -1) {
                // a semicolon is not allowed in DSN names, only in file URLs
                return ODBC;
            } else if (url.startsWith(ACCESS_FILE_URL_PREFIX)) {
                return ACCESS_FILE;
            } else if (url.startsWith(EXCEL_FILE_URL_PREFIX)) {
                return EXCEL_FILE;
            } else {
                throw new IllegalStateException("Undefined JDBC URL: " + url);
            }
        }
        return JDBC;
    }

    public void setAccessFileInfo(String fileLocation, String userName,
                                  String password) {
        this.driverClass = JDBC_ODBC_BRIDGE_DRIVER;
        this.sourceURL =
            ACCESS_FILE_URL_PREFIX + fileLocation + ACCESS_FILE_URL_END;
        this.userName = userName;
        this.password = password;
        this.embeddedSQLLocation = null;
        this.embeddedSQLPath = null;
    }

    public String getAccessFileUrl() {
        int start = ACCESS_FILE_URL_PREFIX.length();
        int end = getURL().length() - ACCESS_FILE_URL_END.length();
        return getURL().substring(start, end);
    }

    public void setExcelFileInfo(String fileLocation, String userName,
                                  String password) {
        this.driverClass = JDBC_ODBC_BRIDGE_DRIVER;
        this.sourceURL =
            EXCEL_FILE_URL_PREFIX + fileLocation + EXCEL_FILE_URL_END;
        this.userName = userName;
        this.password = password;
        this.embeddedSQLLocation = null;
        this.embeddedSQLPath = null;
    }

    public String getExcelFileUrl() {
        int start = EXCEL_FILE_URL_PREFIX.length();
        int end = getURL().length() - EXCEL_FILE_URL_END.length();
        return getURL().substring(start, end);
    }

    public void setOdbcDataSource(
        String dsn,
        String userName,
        String password) {
        this.driverClass = JDBC_ODBC_BRIDGE_DRIVER;
        this.sourceURL = ODBC_PREFIX + dsn;
        this.userName = userName;
        this.password = password;
        this.embeddedSQLLocation = null;
        this.embeddedSQLPath = null;
    }

    public String getOdbcDataSourceName() {
        return getURL().substring(ODBC_PREFIX.length());
    }
}
