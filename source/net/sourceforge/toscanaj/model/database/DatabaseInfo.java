/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import org.jdom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;

import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.XML_Helper;

/**
 * This class contains information how to connect to a database.
 */
public class DatabaseInfo implements XML_Serializable {
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
    public DatabaseQuery createListQuery(String name, String header, boolean isDistinct) {
        return new DatabaseListQuery(this, name, header, isDistinct);
    }

    /**
     * Creates a new Query that will query a single number as aggregate.
     */
    public DatabaseQuery createAggregateQuery(String name, String header) {
        return new DatabaseAggregateQuery(this, name, header);
    }

    /**
     * Creates an empty instance.
     *
     * Type is set to TYPE_UNDEFINED, the strings are all null.
     */
    public DatabaseInfo() {
    }

    public DatabaseInfo(Element element) throws XML_SyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(DATABASE_CONNECTION_ELEMENT_NAME);
        if( embeddedSQLPath != null ) {
            Element embedElem = new Element(EMBEDDED_SOURCE_ELEMENT_NAME);
            embedElem.setAttribute(EMBEDDED_URL_ATTRIBUTE_NAME,embeddedSQLPath);
            retVal.addContent(embedElem);
        } else {
            Element urlElem = new Element(URL_SOURCE_ELEMENT_NAME);
            urlElem.addContent(sourceURL);
            urlElem.setAttribute(DRIVER_CLASS_ATTRIBUTE_NAME, driverClass);
            urlElem.setAttribute(USERNAME_ATTRIBUTE_NAME, userName);
            urlElem.setAttribute(PASSWORD_ATTRIBUTE_NAME, password);
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

    public void readXML(Element elem) throws XML_SyntaxError {
        XML_Helper.checkName(DATABASE_CONNECTION_ELEMENT_NAME, elem);
        if (XML_Helper.contains(elem, EMBEDDED_SOURCE_ELEMENT_NAME)) {
            Element embedElem = elem.getChild(EMBEDDED_SOURCE_ELEMENT_NAME);
            setEmbeddedSQLLocation(XML_Helper.getAttribute(embedElem,EMBEDDED_URL_ATTRIBUTE_NAME).getValue());
            setUrl("jdbc:hsqldb:.");
            setDriverClass("org.hsqldb.jdbcDriver");
            setUserName("sa");
            setPassword("");
        } else {
            Element urlElement=XML_Helper.mustbe(URL_SOURCE_ELEMENT_NAME,elem);
            sourceURL=urlElement.getText();
            driverClass=XML_Helper.getAttribute(urlElement, DRIVER_CLASS_ATTRIBUTE_NAME).getValue();
            userName=XML_Helper.getAttribute(urlElement, USERNAME_ATTRIBUTE_NAME).getValue();
            password=XML_Helper.getAttribute(urlElement, PASSWORD_ATTRIBUTE_NAME).getValue();;
        }
        table=XML_Helper.mustbe(TABLE_ELEMENT_NAME, elem).getText();
        objectKey= XML_Helper.mustbe(OBJECT_KEY_ELEMENT_NAME,elem).getText();
    }

    /**
     * Returns the JDBC url for connecting to the database.
     */
    public String getURL() {
        return this.sourceURL;
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
    public void setUrl(String url) {
        this.sourceURL = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setPassword(String password) {
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
    public void setTableName(String table) {
        this.table = table;
    }

    public String getTableName() {
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
        this.embeddedSQLLocation = resolveLocation(relativePath);
        this.embeddedSQLPath = relativePath;
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
}