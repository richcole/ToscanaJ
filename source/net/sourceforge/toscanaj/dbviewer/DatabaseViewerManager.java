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
 * @todo drop distinction between the one element viewers and the multi-element viewers.
 *      Implementing multi-element viewers can still easily done by supplying a framework
 *      giving a dialog which shows a one-element view and adds controls for changing the
 *      item (first/prev/next/last/number) if needed.
 *
 * @todo the actual storing of the available viewers should not be part of this class, but
 *      should be in the ConceptualSchema instead, this class should just have a reference
 *      to a list.
 *
 * @todo create some less db-specific abstraction
 */
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
    private DatabaseConnection dbConnection;
    private URL baseURL;

    public DatabaseViewerManager(Element viewerDefinition, DatabaseInfo databaseInfo, DatabaseConnection connection, URL baseURL)
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
