/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @todo supply a framework
 *      giving a dialog which shows a one-element view and adds controls for changing the
 *      item (first/prev/next/last/number) if needed.
 *
 * @todo the actual storing of the available viewers should not be part of this class, but
 *      should be in the ConceptualSchema instead, this class should just have a reference
 *      to a list.
 *
 * @todo create some less db-specific abstraction
 *
 * @todo external templates are automatically inlined when loading from and saving into XML,
 *       fix that
 */
public class DatabaseViewerManager implements XMLizable {
    /// @todo look for a better way to get the parent
    private static Component parentComponent = null;
    private static List objectViewerRegistry = new LinkedList();
    private static List objectListViewerRegistry = new LinkedList();
    private static List attributeViewerRegistry = new LinkedList();
    private DatabaseViewer viewer = null;
    private String screenName = null;
    private String tableName = null;
    private String keyName = null;
    private Element originalTemplateElement = null;
    private Element templateElement = null;
    private Dictionary parameters = new Hashtable();
    private DatabaseInfo databaseInfo;
    private DatabaseConnection dbConnection;
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

    public DatabaseViewerManager(Element viewerDefinition, DatabaseInfo databaseInfo, DatabaseConnection connection)
            throws DatabaseViewerInitializationException {
        this.databaseInfo = databaseInfo;
        this.dbConnection = connection;
        try {
            readXML(viewerDefinition);
        } catch (XMLSyntaxError xmlSyntaxError) {
            throw new DatabaseViewerInitializationException("XML Syntax error in viewer definition.", xmlSyntaxError);
        }
        registerViewer(viewerDefinition.getAttributeValue(CLASS_ATTRIBUTE_NAME), viewerDefinition.getName());
    }

    private void registerViewer(String className, String viewerType) throws DatabaseViewerInitializationException {
        if (className == null) {
            throw new DatabaseViewerInitializationException("Could not find class attribute on <" +
                    viewerType + ">");
        }
        try {
            Class viewerClass = Class.forName(className);
            viewer = (DatabaseViewer) viewerClass.newInstance();
            viewer.initialize(this);
        } catch (ClassNotFoundException e) {
            throw new DatabaseViewerInitializationException("Could not find class '" + className + "'");
        } catch (InstantiationException e) {
            throw new DatabaseViewerInitializationException("Could not instantiate class '" + className + "'");
        } catch (IllegalAccessException e) {
            throw new DatabaseViewerInitializationException("Could not access class '" + className + "'");
        }
        if (viewerType.equals(OBJECT_VIEW_ELEMENT_NAME)) {
            objectViewerRegistry.add(this);
        } else if (viewerType.equals(OBJECT_LIST_VIEW_ELEMENT_NAME)) {
            objectListViewerRegistry.add(this);
        } else if (viewerType.equals(ATTTRIBUTE_VIEW_ELEMENT_NAME)) {
            attributeViewerRegistry.add(this);
        } else {
            throw new DatabaseViewerInitializationException("Unknown viewer type: <" + viewerType + ">");
        }
    }

    public static void setParentComponent(Component parentComponent) {
        DatabaseViewerManager.parentComponent = parentComponent;
    }

    public static Frame getParentWindow() {
        return JOptionPane.getFrameForComponent(parentComponent);
    }

    public static void showObject(int viewerID, DatabaseRetrievedObject object) {
        if (object.hasKey()) {
            DatabaseViewerManager manager = (DatabaseViewerManager) objectViewerRegistry.get(viewerID);
            DatabaseViewer viewer = manager.viewer;
            viewer.showView("WHERE " + manager.getKeyName() + " = '" + object.getKey() + "'");
        } else if (object.hasSpecialWhereClause()) {
            DatabaseViewerManager manager = (DatabaseViewerManager) objectListViewerRegistry.get(viewerID);
            DatabaseViewer viewer = manager.viewer;
            viewer.showView(object.getSpecialWhereClause());
        } else {
            showObjectList(viewerID, object);
        }
    }

    public static void showObject(String viewName, DatabaseRetrievedObject object) {
        if (object.hasKey()) {
            for (int i = 0; i < objectViewerRegistry.size(); i++) {
                DatabaseViewerManager manager = (DatabaseViewerManager) objectViewerRegistry.get(i);
                if (manager.screenName.equals(viewName)) {
                    DatabaseViewer viewer = manager.viewer;
                    viewer.showView("WHERE " + manager.getKeyName() + " = '" + object.getKey() + "'");
                }
            }
        } else if (object.hasSpecialWhereClause()) {
            for (int i = 0; i < objectListViewerRegistry.size(); i++) {
                DatabaseViewerManager manager = (DatabaseViewerManager) objectListViewerRegistry.get(i);
                if (manager.screenName.equals(viewName)) {
                    DatabaseViewer viewer = manager.viewer;
                    viewer.showView(object.getSpecialWhereClause());
                }
            }
        } else {
            showObjectList(viewName, object);
        }
    }

    public static void showObjectList(int viewerID, DatabaseRetrievedObject object) {
        DatabaseViewerManager manager = (DatabaseViewerManager) objectListViewerRegistry.get(viewerID);
        manager.viewer.showView(object.getQueryWhereClause());
    }

    public static void showObjectList(String viewName, DatabaseRetrievedObject object) {
        for (int i = 0; i < objectListViewerRegistry.size(); i++) {
            DatabaseViewerManager manager = (DatabaseViewerManager) objectListViewerRegistry.get(i);
            if (manager.screenName.equals(viewName)) {
                manager.viewer.showView(object.getQueryWhereClause());
            }
        }
    }

    public static void showAttribute(int viewerID, String attribute) {
            DatabaseViewerManager manager = (DatabaseViewerManager) attributeViewerRegistry.get(viewerID);
            DatabaseViewer viewer = manager.viewer;
            viewer.showView("WHERE " + manager.getKeyName() + " = '" + attribute + "'");
    }

    public static void showAttribute(String viewName, String attribute) {
        for (int i = 0; i < attributeViewerRegistry.size(); i++) {
            DatabaseViewerManager manager = (DatabaseViewerManager) attributeViewerRegistry.get(i);
            if (manager.screenName.equals(viewName)) {
                DatabaseViewer viewer = manager.viewer;
                viewer.showView("WHERE " + manager.getKeyName() + " = '" + attribute + "'");
            }
        }
    }

    public Element getTemplate() {
        if (templateElement != null) {
            String url = this.templateElement.getAttributeValue("url");
            if (url != null) {
                insertXML(templateElement);
            }
        }
        return this.templateElement;
    }

    public String getTemplateString() {
        if (this.templateElement != null) {
            String url = this.templateElement.getAttributeValue("url");
            if (url != null) {
                loadText(templateElement);
            }
        }
        return this.templateElement.getText();
    }

    protected void insertXML(Element elem) {
        String urlAttr = elem.getAttributeValue("url");
        if (urlAttr != null) {
            try {
                URL url = new URL(this.baseURL, urlAttr);
                DOMBuilder builder =
                        new DOMBuilder("org.jdom.adapters.XercesDOMAdapter");
                org.jdom.Document doc = builder.build(url);
                Element root = doc.getRootElement();
                root.detach();
                elem.addContent(root);
                // remove "url" so we don't insert again
                elem.removeAttribute("url");
            } catch (Exception e) {
                /// @todo handle exceptions, give feedback
                e.printStackTrace();
            }
        }
        return;
    }

    protected void loadText(Element elem) {
        String urlAttr = elem.getAttributeValue("url");
        if (urlAttr != null) {
            String result = "";
            try {
                URL url = new URL(this.baseURL, urlAttr);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = reader.readLine();
                while (line != null) {
                    result += line + "\n";
                    line = reader.readLine();
                }
                elem.addContent(result);
                // remove "url" so we don't insert again
                elem.removeAttribute("url");
            } catch (Exception e) {
                /// @todo handle exceptions, give feedback
                e.printStackTrace();
            }
        }
    }

    public Dictionary getParameters() {
        return this.parameters;
    }

    public String getTableName() {
        if (this.tableName != null) {
            return this.tableName;
        } else {
            return this.databaseInfo.getTableName();
        }
    }

    public String getKeyName() {
        if (this.keyName != null) {
            return this.keyName;
        } else {
            return this.databaseInfo.getKey();
        }
    }

    public DatabaseConnection getConnection() {
        return this.dbConnection;
    }

    public static List getObjectViewNames(DatabaseRetrievedObject object) {
        List retVal = new LinkedList();
        if (object.hasKey()) {
            Iterator it = objectViewerRegistry.iterator();
            while (it.hasNext()) {
                DatabaseViewerManager manager = (DatabaseViewerManager) it.next();
                retVal.add(manager.screenName);
            }
        }
        if (object.hasSpecialWhereClause()) {
            Iterator it = objectListViewerRegistry.iterator();
            while (it.hasNext()) {
                DatabaseViewerManager manager = (DatabaseViewerManager) it.next();
                retVal.add(manager.screenName);
            }
        }
        return retVal;
    }

    public static List getObjectListViewNames(DatabaseRetrievedObject object) {
        List retVal = new LinkedList();
        Iterator it = objectListViewerRegistry.iterator();
        while (it.hasNext()) {
            DatabaseViewerManager manager = (DatabaseViewerManager) it.next();
            retVal.add(manager.screenName);
        }
        return retVal;
    }

    public static List getAttributeViewNames() {
        List retVal = new LinkedList();
        Iterator it = attributeViewerRegistry.iterator();
        while (it.hasNext()) {
            DatabaseViewerManager manager = (DatabaseViewerManager) it.next();
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

    public static void listsToXML(Element parentElem) {
        for (Iterator iterator = objectViewerRegistry.iterator(); iterator.hasNext();) {
            DatabaseViewerManager databaseViewerManager = (DatabaseViewerManager) iterator.next();
            parentElem.addContent(databaseViewerManager.toXML());
        }
        for (Iterator iterator = objectListViewerRegistry.iterator(); iterator.hasNext();) {
            DatabaseViewerManager databaseViewerManager = (DatabaseViewerManager) iterator.next();
            parentElem.addContent(databaseViewerManager.toXML());
        }
    }

    public static void listsReadXML(Element parentElem, DatabaseInfo databaseInfo, DatabaseConnection connection)
            throws DatabaseViewerInitializationException {
        for (Iterator iterator = parentElem.getChildren(OBJECT_VIEW_ELEMENT_NAME).iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            new DatabaseViewerManager(element, databaseInfo, connection);
        }
        for (Iterator iterator = parentElem.getChildren(OBJECT_LIST_VIEW_ELEMENT_NAME).iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
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
        retVal.setAttribute(SCREEN_NAME_ATTRIBUTE_NAME, screenName);
        retVal.setAttribute(CLASS_ATTRIBUTE_NAME, this.viewer.getClass().getName());
        if (originalTemplateElement != null) {
            retVal.addContent((Element) originalTemplateElement.clone());
        }
        if (tableName != null) {
            Element tableElem = new Element(TABLE_ELEMENT_NAME);
            tableElem.addContent(tableName);
            retVal.addContent(tableElem);
        }
        if (keyName != null) {
            Element keyElem = new Element(KEY_ELEMENT_NAME);
            keyElem.addContent(keyName);
            retVal.addContent(keyElem);
        }
        Enumeration parKeys = parameters.keys();
        while (parKeys.hasMoreElements()) {
            String key = (String) parKeys.nextElement();
            Element parElem = new Element(PARAMETER_ELEMENT_NAME);
            parElem.setAttribute(PARAMETER_NAME_ATTRIBUTE_NAME, key);
            parElem.setAttribute(PARAMETER_VALUE_ATTRIBUTE_NAME, (String) parameters.get(key));
            retVal.addContent(parElem);
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        screenName = elem.getAttributeValue(SCREEN_NAME_ATTRIBUTE_NAME);
        originalTemplateElement = elem.getChild(TEMPLATE_ELEMENT_NAME);
        if (originalTemplateElement != null) {
            templateElement = (Element) originalTemplateElement.clone();
        }
        tableName = elem.getChildText(TABLE_ELEMENT_NAME);
        keyName = elem.getChildText(KEY_ELEMENT_NAME);
        List parameterElems = elem.getChildren(PARAMETER_ELEMENT_NAME);
        Iterator it = parameterElems.iterator();
        while (it.hasNext()) {
            Element parameterElem = (Element) it.next();
            this.parameters.put(parameterElem.getAttributeValue(PARAMETER_NAME_ATTRIBUTE_NAME),
                    parameterElem.getAttributeValue(PARAMETER_VALUE_ATTRIBUTE_NAME));
        }
    }

    public static void setBaseURL(URL baseURL) {
        DatabaseViewerManager.baseURL = baseURL;
    }
}
