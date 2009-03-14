/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import java.awt.Component;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tockit.plugin.PluginClassLoader;

/**
 * @todo the actual storing of the available viewers should not be part of this
 *       class, but should be in the ConceptualSchema instead, this class should
 *       just have a reference to a list.
 * 
 * @todo create some less db-specific abstraction
 * 
 * @todo external templates are automatically inlined when loading from and
 *       saving into XML, fix that
 */
public class DatabaseViewerManager implements XMLizable {
    private final static Preferences preferences = Preferences
            .userNodeForPackage(DatabaseViewerManager.class);

    // / @todo look for a better way to get the parent
    private static Component parentComponent = null;
    private static List<DatabaseViewerManager> objectViewerRegistry = new LinkedList<DatabaseViewerManager>();
    private static List<DatabaseViewerManager> objectListViewerRegistry = new LinkedList<DatabaseViewerManager>();
    private static List<DatabaseViewerManager> attributeViewerRegistry = new LinkedList<DatabaseViewerManager>();
    private DatabaseViewer viewer = null;
    private String screenName = null;
    private String tableName = null;
    private String keyName = null;
    private Element originalTemplateElement = null;
    private Element templateElement = null;
    private final Dictionary<String, String> parameters = new Hashtable<String, String>();
    private final DatabaseInfo databaseInfo;
    private final DatabaseConnection dbConnection;
    private static URL baseURL;
    private static final String OBJECT_VIEW_ELEMENT_NAME = "objectView";
    private static final String OBJECT_LIST_VIEW_ELEMENT_NAME = "objectListView";
    private static final String ATTTRIBUTE_VIEW_ELEMENT_NAME = "attributeView";
    private static final String CLASS_ATTRIBUTE_NAME = "class";
    private static final String SCREEN_NAME_ATTRIBUTE_NAME = "name";
    private static final String TEMPLATE_ELEMENT_NAME = "template";
    private static final String TABLE_ELEMENT_NAME = "table";
    private static final String KEY_ELEMENT_NAME = "key";
    private static final String PARAMETER_ELEMENT_NAME = "parameter";
    private static final String PARAMETER_NAME_ATTRIBUTE_NAME = "name";
    private static final String PARAMETER_VALUE_ATTRIBUTE_NAME = "value";

    private static final String PLUGIN_LOCATION = preferences.get(
            "pluginDirectory", "plugins");

    public DatabaseViewerManager(final Element viewerDefinition,
            final DatabaseInfo databaseInfo, final DatabaseConnection connection)
            throws DatabaseViewerException {
        this.databaseInfo = databaseInfo;
        this.dbConnection = connection;
        try {
            readXML(viewerDefinition);
        } catch (final XMLSyntaxError xmlSyntaxError) {
            throw new DatabaseViewerException(
                    "XML Syntax error in viewer definition.", xmlSyntaxError);
        }
        registerViewer(
                viewerDefinition.getAttributeValue(CLASS_ATTRIBUTE_NAME),
                viewerDefinition.getName());
    }

    private void registerViewer(final String className, final String viewerType)
            throws DatabaseViewerException {
        if (className == null) {
            throw new DatabaseViewerException(
                    "Could not find class attribute on <" + viewerType + ">");
        }
        Class viewerClass;
        try {
            final PluginClassLoader loader = new PluginClassLoader(new File(
                    PLUGIN_LOCATION));
            viewerClass = loader.findClass(className);
        } catch (final Exception e) { // if anything fails with the plugin
            // loader, fall back to normal class
            // loader
            try {
                viewerClass = Class.forName(className);
            } catch (final ClassNotFoundException e1) {
                throw new DatabaseViewerException("Could not find class '"
                        + className + "' -- possible cause: missing plugin");
            }
        }
        try {
            this.viewer = (DatabaseViewer) viewerClass.newInstance();
            this.viewer.initialize(this);
        } catch (final InstantiationException e) {
            throw new DatabaseViewerException("Could not instantiate class '"
                    + className + "'");
        } catch (final IllegalAccessException e) {
            throw new DatabaseViewerException("Could not access class '"
                    + className + "'");
        }
        if (viewerType.equals(OBJECT_VIEW_ELEMENT_NAME)) {
            objectViewerRegistry.add(this);
        } else if (viewerType.equals(OBJECT_LIST_VIEW_ELEMENT_NAME)) {
            objectListViewerRegistry.add(this);
        } else if (viewerType.equals(ATTTRIBUTE_VIEW_ELEMENT_NAME)) {
            attributeViewerRegistry.add(this);
        } else {
            throw new DatabaseViewerException("Unknown viewer type: <"
                    + viewerType + ">");
        }
    }

    public static void setParentComponent(final Component parentComponent) {
        DatabaseViewerManager.parentComponent = parentComponent;
    }

    public static Frame getParentWindow() {
        return JOptionPane.getFrameForComponent(parentComponent);
    }

    public static void showObject(final int viewerID,
            final DatabaseRetrievedObject object)
            throws DatabaseViewerException {
        if (object.hasKey()) {
            if (objectViewerRegistry.size() == 0) {
                return;
            }
            final DatabaseViewerManager manager = objectViewerRegistry
                    .get(viewerID);
            final DatabaseViewer viewer = manager.viewer;
            viewer.showView("WHERE " + manager.getKeyName() + " = '"
                    + object.getKey() + "'");
        } else if (object.hasSpecialWhereClause()) {
            if (objectListViewerRegistry.size() == 0) {
                return;
            }
            final DatabaseViewerManager manager = objectListViewerRegistry
                    .get(viewerID);
            final DatabaseViewer viewer = manager.viewer;
            viewer.showView(object.getSpecialWhereClause());
        } else {
            showObjectList(viewerID, object);
        }
    }

    public static void showObject(final String viewName,
            final DatabaseRetrievedObject object)
            throws DatabaseViewerException {
        if (object.hasKey()) {
            for (int i = 0; i < objectViewerRegistry.size(); i++) {
                final DatabaseViewerManager manager = objectViewerRegistry
                        .get(i);
                if (manager.screenName.equals(viewName)) {
                    final DatabaseViewer viewer = manager.viewer;
                    viewer.showView("WHERE " + manager.getKeyName() + " = '"
                            + object.getKey() + "'");
                }
            }
        } else if (object.hasSpecialWhereClause()) {
            for (int i = 0; i < objectListViewerRegistry.size(); i++) {
                final DatabaseViewerManager manager = objectListViewerRegistry
                        .get(i);
                if (manager.screenName.equals(viewName)) {
                    final DatabaseViewer viewer = manager.viewer;
                    viewer.showView(object.getSpecialWhereClause());
                }
            }
        } else {
            showObjectList(viewName, object);
        }
    }

    public static void showObjectList(final int viewerID,
            final DatabaseRetrievedObject object)
            throws DatabaseViewerException {
        if (objectListViewerRegistry.size() == 0) {
            return;
        }
        final DatabaseViewerManager manager = objectListViewerRegistry
                .get(viewerID);
        manager.viewer.showView(object.getQueryWhereClause());
    }

    public static void showObjectList(final String viewName,
            final DatabaseRetrievedObject object)
            throws DatabaseViewerException {
        for (int i = 0; i < objectListViewerRegistry.size(); i++) {
            final DatabaseViewerManager manager = objectListViewerRegistry
                    .get(i);
            if (manager.screenName.equals(viewName)) {
                manager.viewer.showView(object.getQueryWhereClause());
            }
        }
    }

    public static void showAttribute(final int viewerID, final String attribute)
            throws DatabaseViewerException {
        final DatabaseViewerManager manager = attributeViewerRegistry
                .get(viewerID);
        final DatabaseViewer viewer = manager.viewer;
        viewer.showView("WHERE " + manager.getKeyName() + " = '" + attribute
                + "'");
    }

    public static void showAttribute(final String viewName,
            final String attribute) throws DatabaseViewerException {
        for (int i = 0; i < attributeViewerRegistry.size(); i++) {
            final DatabaseViewerManager manager = attributeViewerRegistry
                    .get(i);
            if (manager.screenName.equals(viewName)) {
                final DatabaseViewer viewer = manager.viewer;
                viewer.showView("WHERE " + manager.getKeyName() + " = '"
                        + attribute + "'");
            }
        }
    }

    public Element getTemplate() {
        if (this.templateElement != null) {
            final String url = this.templateElement.getAttributeValue("url");
            if (url != null) {
                insertXML(this.templateElement);
            }
        }
        return (Element) this.templateElement.clone();
    }

    public String getTemplateString() {
        if (this.templateElement != null) {
            final String url = this.templateElement.getAttributeValue("url");
            if (url != null) {
                loadText(this.templateElement);
            }
        }
        return this.templateElement.getText();
    }

    protected void insertXML(final Element elem) {
        final String urlAttr = elem.getAttributeValue("url");
        if (urlAttr != null) {
            try {
                final URL url = new URL(DatabaseViewerManager.baseURL, urlAttr);
                final SAXBuilder parser = new SAXBuilder();
                final Document doc = parser.build(url);
                final Element root = doc.getRootElement();
                root.detach();
                elem.addContent(root);
                // remove "url" so we don't insert again
                elem.removeAttribute("url");
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Could not insert XML/XHTML template for database viewer from file",
                        e);
            }
        }
        return;
    }

    protected void loadText(final Element elem) {
        final String urlAttr = elem.getAttributeValue("url");
        if (urlAttr != null) {
            String result = "";
            try {
                final URL url = new URL(DatabaseViewerManager.baseURL, urlAttr);
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = reader.readLine();
                while (line != null) {
                    result += line + "\n";
                    line = reader.readLine();
                }
                elem.addContent(result);
                // remove "url" so we don't insert again
                elem.removeAttribute("url");
            } catch (final Exception e) {
                throw new RuntimeException(
                        "Could not insert text template for database viewer from file",
                        e);
            }
        }
    }

    public Dictionary<String, String> getParameters() {
        return this.parameters;
    }

    public String getTableName() {
        if (this.tableName != null) {
            return this.tableName;
        } else {
            return this.databaseInfo.getTable().getSqlExpression();
        }
    }

    public String getKeyName() {
        if (this.keyName != null) {
            return this.keyName;
        } else {
            return this.databaseInfo.getKey().getSqlExpression();
        }
    }

    public DatabaseConnection getConnection() {
        return this.dbConnection;
    }

    public static List<String> getObjectViewNames(
            final DatabaseRetrievedObject object) {
        final List<String> retVal = new LinkedList<String>();
        if (object.hasKey()) {
            final Iterator<DatabaseViewerManager> it = objectViewerRegistry
                    .iterator();
            while (it.hasNext()) {
                final DatabaseViewerManager manager = it.next();
                retVal.add(manager.screenName);
            }
        }
        if (object.hasSpecialWhereClause()) {
            final Iterator<DatabaseViewerManager> it = objectListViewerRegistry
                    .iterator();
            while (it.hasNext()) {
                final DatabaseViewerManager manager = it.next();
                retVal.add(manager.screenName);
            }
        }
        return retVal;
    }

    public static List<String> getObjectListViewNames() {
        final List<String> retVal = new LinkedList<String>();
        final Iterator<DatabaseViewerManager> it = objectListViewerRegistry
                .iterator();
        while (it.hasNext()) {
            final DatabaseViewerManager manager = it.next();
            retVal.add(manager.screenName);
        }
        return retVal;
    }

    public static List<String> getAttributeViewNames() {
        final List<String> retVal = new LinkedList<String>();
        final Iterator<DatabaseViewerManager> it = attributeViewerRegistry
                .iterator();
        while (it.hasNext()) {
            final DatabaseViewerManager manager = it.next();
            retVal.add(manager.screenName);
        }
        return retVal;
    }

    public static int getNumberOfObjectViews() {
        return objectViewerRegistry.size();
    }

    public static int getNumberOfObjectListViews() {
        return objectListViewerRegistry.size();
    }

    public static int getNumberOfAttributeViews() {
        return attributeViewerRegistry.size();
    }

    public static void resetRegistry() {
        objectViewerRegistry.clear();
        objectListViewerRegistry.clear();
    }

    public static void listsToXML(final Element parentElem) {
        for (final DatabaseViewerManager databaseViewerManager : objectViewerRegistry) {
            parentElem.addContent(databaseViewerManager.toXML());
        }
        for (final DatabaseViewerManager databaseViewerManager : objectListViewerRegistry) {
            parentElem.addContent(databaseViewerManager.toXML());
        }
    }

    public static void listsReadXML(final Element parentElem,
            final DatabaseInfo databaseInfo, final DatabaseConnection connection)
            throws DatabaseViewerException {
        for (final Iterator<Element> iterator = parentElem.getChildren(
                OBJECT_VIEW_ELEMENT_NAME).iterator(); iterator.hasNext();) {
            final Element element = iterator.next();
            new DatabaseViewerManager(element, databaseInfo, connection);
        }
        for (final Iterator<Element> iterator = parentElem.getChildren(
                OBJECT_LIST_VIEW_ELEMENT_NAME).iterator(); iterator.hasNext();) {
            final Element element = iterator.next();
            new DatabaseViewerManager(element, databaseInfo, connection);
        }
    }

    public Element toXML() {
        Element retVal;
        if (objectViewerRegistry.contains(this)) {
            retVal = new Element(OBJECT_VIEW_ELEMENT_NAME);
        } else if (objectListViewerRegistry.contains(this)) {
            retVal = new Element(OBJECT_LIST_VIEW_ELEMENT_NAME);
        } else {
            throw new RuntimeException("Totally unexpected situation");
        }
        retVal.setAttribute(SCREEN_NAME_ATTRIBUTE_NAME, this.screenName);
        retVal.setAttribute(CLASS_ATTRIBUTE_NAME, this.viewer.getClass()
                .getName());
        if (this.originalTemplateElement != null) {
            retVal.addContent((Element) this.originalTemplateElement.clone());
        }
        if (this.tableName != null) {
            final Element tableElem = new Element(TABLE_ELEMENT_NAME);
            tableElem.addContent(this.tableName);
            retVal.addContent(tableElem);
        }
        if (this.keyName != null) {
            final Element keyElem = new Element(KEY_ELEMENT_NAME);
            keyElem.addContent(this.keyName);
            retVal.addContent(keyElem);
        }
        final Enumeration<String> parKeys = this.parameters.keys();
        while (parKeys.hasMoreElements()) {
            final String key = parKeys.nextElement();
            final Element parElem = new Element(PARAMETER_ELEMENT_NAME);
            parElem.setAttribute(PARAMETER_NAME_ATTRIBUTE_NAME, key);
            parElem.setAttribute(PARAMETER_VALUE_ATTRIBUTE_NAME,
                    this.parameters.get(key));
            retVal.addContent(parElem);
        }
        return retVal;
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        if (elem == null) {
            throw new XMLSyntaxError("No element for "
                    + this.getClass().getName() + "given.");
        }
        this.screenName = elem.getAttributeValue(SCREEN_NAME_ATTRIBUTE_NAME);
        this.originalTemplateElement = elem.getChild(TEMPLATE_ELEMENT_NAME);
        if (this.originalTemplateElement != null) {
            this.templateElement = (Element) this.originalTemplateElement
                    .clone();
        }
        this.tableName = elem.getChildText(TABLE_ELEMENT_NAME);
        this.keyName = elem.getChildText(KEY_ELEMENT_NAME);
        final List<Element> parameterElems = elem
                .getChildren(PARAMETER_ELEMENT_NAME);
        final Iterator<Element> it = parameterElems.iterator();
        while (it.hasNext()) {
            final Element parameterElem = it.next();
            this.parameters.put(parameterElem
                    .getAttributeValue(PARAMETER_NAME_ATTRIBUTE_NAME),
                    parameterElem
                            .getAttributeValue(PARAMETER_VALUE_ATTRIBUTE_NAME));
        }
    }

    public static void setBaseURL(final URL baseURL) {
        DatabaseViewerManager.baseURL = baseURL;
    }

    public static URL getBaseURL() {
        return baseURL;
    }
}
