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
    private String table = null;

    /**
     * The key used for the object names.
     */
    private String objectKey = null;

    private String userName = null;

    private String password = null;

    private URL embeddedSQLLocation = null;
    private String embeddedSQLPath = null;

    private String driverClass = null;
    
    public static class Type{};
    
    public static final Type UNDEFINED = new Type();
    public static final Type EMBEDDED = new Type();
    public static final Type JDBC = new Type();
    public static final Type ODBC = new Type();
    public static final Type ACCESS_FILE = new Type();

    private static final String ODBC_PREFIX = "jdbc:odbc:";
    private static final String ACCESS_FILE_URL_PREFIX =
                "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb); DBQ=";
    private static final String ACCESS_FILE_URL_END =
                ";UserCommitSync=Yes;Threads=3;SafeTransactions=0;PageTimeout=5;" +
                "MaxScanRows=8;MaxBufferSize=2048;DriverId=281";

    private static final String JDBC_ODBC_BRIDGE_DRIVER =
                "sun.jdbc.odbc.JdbcOdbcDriver";

    private static final String TABLE_ELEMENT_NAME = "table";
    public static final String DATABASE_CONNECTION_ELEMENT_NAME = "databaseConnection";
    private static final String EMBEDDED_SOURCE_ELEMENT_NAME = "embed";
    private static final String URL_SOURCE_ELEMENT_NAME = "url";
    private static final String DRIVER_CLASS_ATTRIBUTE_NAME = "driver";
    private static final String USERNAME_ATTRIBUTE_NAME = "user";
    private static final String PASSWORD_ATTRIBUTE_NAME = "password";
    private static final String EMBEDDED_URL_ATTRIBUTE_NAME = "url";
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
    public Query createListQuery(String name, String header, boolean isDistinct) {
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
    }

    public DatabaseInfo(Element element) throws XMLSyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(DATABASE_CONNECTION_ELEMENT_NAME);
        if (embeddedSQLPath != null) {
            Element embedElem = new Element(EMBEDDED_SOURCE_ELEMENT_NAME);
            embedElem.setAttribute(EMBEDDED_URL_ATTRIBUTE_NAME, embeddedSQLPath);
            retVal.addContent(embedElem);
        } else {
            Element urlElem = new Element(URL_SOURCE_ELEMENT_NAME);
            urlElem.addContent(sourceURL);
            urlElem.setAttribute(DRIVER_CLASS_ATTRIBUTE_NAME, driverClass);
            urlElem.setAttribute(USERNAME_ATTRIBUTE_NAME, getUserName());
            urlElem.setAttribute(PASSWORD_ATTRIBUTE_NAME, getPassword());
            retVal.addContent(urlElem);
        }
        Element tableElem = new Element(TABLE_ELEMENT_NAME);
        tableElem.addContent(table);
        retVal.addContent(tableElem);
        Element keyElem = new Element(OBJECT_KEY_ELEMENT_NAME);
        keyElem.addContent(objectKey);
        retVal.addContent(keyElem);
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(DATABASE_CONNECTION_ELEMENT_NAME, elem);
        if (XMLHelper.contains(elem, EMBEDDED_SOURCE_ELEMENT_NAME)) {
            Element embedElem = elem.getChild(EMBEDDED_SOURCE_ELEMENT_NAME);
            setEmbeddedSQLLocation(XMLHelper.getAttribute(embedElem, EMBEDDED_URL_ATTRIBUTE_NAME).getValue());
            setUrl("jdbc:hsqldb:.");
            setDriverClass("org.hsqldb.jdbcDriver");
            setUserName("sa");
            setPassword("");
        } else {
            Element urlElement = XMLHelper.mustbe(URL_SOURCE_ELEMENT_NAME, elem);
            sourceURL = urlElement.getTextNormalize();
            driverClass = XMLHelper.getAttribute(urlElement, DRIVER_CLASS_ATTRIBUTE_NAME).getValue();
            userName = XMLHelper.getAttribute(urlElement, USERNAME_ATTRIBUTE_NAME).getValue();
            password = XMLHelper.getAttribute(urlElement, PASSWORD_ATTRIBUTE_NAME).getValue();
        }
        table = XMLHelper.mustbe(TABLE_ELEMENT_NAME, elem).getText();
        objectKey = XMLHelper.mustbe(OBJECT_KEY_ELEMENT_NAME, elem).getText();
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
    	if(this.userName == null) {
    		return "";
    	}
        return this.userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        if(this.password == null) {
            return "";
        }
        return this.password;
    }

    /**
     * Sets the database table we want to query.
     *
     * This can be a view, too.
     */
    public void setTableName(String table) {
        this.table = table;
    }

    public String getSQLTableName() {
    	if( getType() == ACCESS_FILE ) {
	    	return "\"" + this.table + "\"";
    	} else {
    		return this.table;
    	}
    }

    public String getDisplayTableName() {
        return this.table;
    }

    /**
     * Sets the key we use in queries.
     */
    public void setKey(String key) {
        this.objectKey = key;
    }

    public String getKey() {
        return this.objectKey;
    }

    public void setEmbeddedSQLLocation(String relativePath) {
        if(relativePath == null) {
            this.embeddedSQLLocation = null;
            this.embeddedSQLPath = null;
        } else {
            this.embeddedSQLLocation = resolveLocation(relativePath);
            this.embeddedSQLPath = relativePath;
        }
    }

    public void setEmbeddedSQLLocation(URL url) {
        this.embeddedSQLLocation = url;
        if(url == null) {
        	this.embeddedSQLPath = null;
        } else {
        	this.embeddedSQLPath = url.getPath();
        }
    }

    private URL resolveLocation(String relativePath) {
        try {
            return new URL(baseURL, relativePath);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not create URL for database: " + relativePath);
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
    public String toString() {
        String result = "DatabaseInfo\n";

        result += "\t" + "url: " + this.sourceURL + "\n" +
                "\t" + "key/table: " + this.objectKey + "/" + this.table;

        return result;
    }

	public Type getType() {
		if(getURL() == null) {
			return UNDEFINED;
		}
	    if(getURL().equals(getEmbeddedDatabaseInfo().getURL())) {
	    	return EMBEDDED;
	    }
		if(getDriverClass().equals(JDBC_ODBC_BRIDGE_DRIVER)) {
	        if(getURL().indexOf(';') == -1){ // a semicolon is not allowed in DSN names
	        	return ODBC;
	        }
	        else { // but always in the access file URLs
	        	return ACCESS_FILE;
	        }
	    }
    	return JDBC;
	}
	
	public void setAccessFileInfo(String fileLocation, String userName, String password) {
		this.driverClass = JDBC_ODBC_BRIDGE_DRIVER;
        this.sourceURL = ACCESS_FILE_URL_PREFIX + fileLocation + ACCESS_FILE_URL_END;
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
    
    public void setOdbcDataSource(String dsn, String userName, String password) {
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
