/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.List;

public class DatabaseViewerManager {
    private static Component parentComponent = null;
    private static List objectViewerRegistry = new LinkedList();
    private static List objectListViewerRegistry = new LinkedList();
    private DatabaseViewer viewer = null;
    private String screenName = null;
    private String tableName = null;
    private String keyName = null;
    private Element template = null;
    private Dictionary parameters = new Hashtable();
    private DatabaseInfo databaseInfo;
    private DBConnection dbConnection;
    private URL baseURL;

    public DatabaseViewerManager(Element viewerDefinition, DatabaseInfo databaseInfo, DBConnection connection, URL baseURL)
            throws DatabaseViewerInitializationException {
        screenName = viewerDefinition.getAttributeValue("name");
        template = viewerDefinition.getChild("template");
        tableName = viewerDefinition.getChildText("table");
        keyName = viewerDefinition.getChildText("key");
        List parameterElems = viewerDefinition.getChildren("parameter");
        Iterator it = parameterElems.iterator();
        while (it.hasNext()) {
            Element parameterElem = (Element) it.next();
            this.parameters.put(parameterElem.getAttributeValue("name"), parameterElem.getAttributeValue("value"));
        }
        this.databaseInfo = databaseInfo;
        this.dbConnection = connection;
        this.baseURL = baseURL;
        // register the viewer object as last step, after all info has been set (which is used by the viewer)
        String className = viewerDefinition.getAttributeValue("class");
        if (className == null) {
            throw new DatabaseViewerInitializationException("Could not find class attribute on <" +
                    viewerDefinition.getName() + ">");
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
        if (viewerDefinition.getName().equals("objectView")) {
            objectViewerRegistry.add(this);
        } else if (viewerDefinition.getName().equals("objectListView")) {
            objectListViewerRegistry.add(this);
        } else {
            throw new DatabaseViewerInitializationException("Unknown viewer type: <" + viewerDefinition.getName() + ">");
        }
    }

    public static void setParentComponent(Component parentComponent) {
        DatabaseViewerManager.parentComponent = parentComponent;
    }

    public static Frame getParentWindow() {
        return JOptionPane.getFrameForComponent(parentComponent);
    }

    public static void showObject(int viewerID, String objectKey) {
        DatabaseViewerManager manager = (DatabaseViewerManager) objectViewerRegistry.get(viewerID);
        DatabaseViewer viewer = manager.viewer;
        viewer.showView("WHERE " + manager.getKeyName() + " = '" + objectKey + "'");
    }

    public static void showObject(String viewName, String objectKey) {
        for (int i = 0; i < objectViewerRegistry.size(); i++) {
            DatabaseViewerManager manager = (DatabaseViewerManager) objectViewerRegistry.get(i);
            if (manager.screenName.equals(viewName)) {
                DatabaseViewer viewer = manager.viewer;
                viewer.showView("WHERE " + manager.getKeyName() + " = '" + objectKey + "'");
            }
        }
    }

    public static void showObjectList(int viewerID, String whereClause) {
        DatabaseViewerManager manager = (DatabaseViewerManager) objectListViewerRegistry.get(viewerID);
        manager.viewer.showView(whereClause);
    }

    public static void showObjectList(String viewName, String whereClause) {
        for (int i = 0; i < objectListViewerRegistry.size(); i++) {
            DatabaseViewerManager manager = (DatabaseViewerManager) objectListViewerRegistry.get(i);
            if (manager.screenName.equals(viewName)) {
                manager.viewer.showView(whereClause);
            }
        }
    }

    public Element getTemplate() {
        String url = this.template.getAttributeValue("url");
        if (url != null) {
            return insertXML(template);
        } else {
            return this.template;
        }
    }

    public String getTemplateString() {
        if (this.template == null) {
            return null;
        } else {
            String url = this.template.getAttributeValue("url");
            if (url != null) {
                return loadText(template);
            } else {
                return this.template.getText();
            }
        }
    }

    protected Element insertXML(Element elem) {
        String urlAttr = elem.getAttributeValue("url");
        if (urlAttr == null) {
            return elem;
        }
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
        return elem;
    }

    protected String loadText(Element elem) {
        String urlAttr = elem.getAttributeValue("url");
        if (urlAttr == null) {
            return null;
        }
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
        return result;
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

    public DBConnection getConnection() {
        return this.dbConnection;
    }

    public static List getObjectViewNames() {
        List retVal = new LinkedList();
        Iterator it = objectViewerRegistry.iterator();
        while (it.hasNext()) {
            DatabaseViewerManager manager = (DatabaseViewerManager) it.next();
            retVal.add(manager.screenName);
        }
        return retVal;
    }

    public static List getObjectListViewNames() {
        List retVal = new LinkedList();
        Iterator it = objectListViewerRegistry.iterator();
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

    public static void resetRegistry() {
        objectViewerRegistry.clear();
        objectListViewerRegistry.clear();
    }
}
