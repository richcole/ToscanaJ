package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JOptionPane;

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jdom.Element;
import org.jdom.input.DOMBuilder;

public class DatabaseViewerManager
{
    private static Component parentComponent = null;
    private static List viewerRegistry = new LinkedList();
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
            throws DatabaseViewerInitializationException
    {
        screenName = viewerDefinition.getAttributeValue("name");
        template = viewerDefinition.getChild("template");
        tableName = viewerDefinition.getChildText("table");
        keyName = viewerDefinition.getChildText("key");
        List parameterElems = viewerDefinition.getChildren("parameter");
        Iterator it = parameterElems.iterator();
        while(it.hasNext())
        {
            Element parameterElem = (Element) it.next();
            this.parameters.put(parameterElem.getAttributeValue("name"), parameterElem.getAttributeValue("value"));
        }
        this.databaseInfo = databaseInfo;
        this.dbConnection = connection;
        this.baseURL = baseURL;
        // register the viewer object as last step, after all info has been set (which is used by the viewer)
        String className = viewerDefinition.getAttributeValue("class");
        if( className == null )
        {
            throw new DatabaseViewerInitializationException("Could not find class attribute on <viewer>");
        }
        try
        {
            Class viewerClass = Class.forName(className);
            viewer = (DatabaseViewer)viewerClass.newInstance();
            viewer.initialize(this); 
        }
        catch( ClassNotFoundException e )
        {
            throw new DatabaseViewerInitializationException("Could not find class '" + className + "'");
        }
        catch( InstantiationException e )
        {
            throw new DatabaseViewerInitializationException("Could not instantiate class '" + className + "'");
        }
        catch( IllegalAccessException e )
        {
            throw new DatabaseViewerInitializationException("Could not access class '" + className + "'");
        }
        viewerRegistry.add(this);
    }
    public static void setParentComponent(Component parentComponent)
    {
        DatabaseViewerManager.parentComponent = parentComponent;
    }
    public static Frame getParentWindow()
    {
        return JOptionPane.getFrameForComponent( parentComponent );
    }        
    public static void showObject(int viewerID, String objectKey)
    {
        DatabaseViewerManager manager = (DatabaseViewerManager) viewerRegistry.get(viewerID);
        manager.viewer.showObject(objectKey);
    }
    public static void showObject(String viewName, String objectKey)
    {
        for(int i = 0; i<viewerRegistry.size(); i++)
        {
            DatabaseViewerManager manager = (DatabaseViewerManager) viewerRegistry.get(i);
            if(manager.screenName.equals(viewName))
            {
                manager.viewer.showObject(objectKey);
            }
        }
    }
    public Element getTemplate()
    {
        String url = this.template.getAttributeValue("url");
        if( url != null ) {
            return insertXML(template);
        }
        else {
            return this.template;
        }
    }
    public String getTemplateString()
    {
        if( this.template == null )
        {
            return null;
        }
        else {
            String url = this.template.getAttributeValue("url");
            if( url != null ) {
                return loadText(template);
            }
            else {
                return this.template.getText();
            }
        }
    }
    protected Element insertXML(Element elem)
    {
        String urlAttr = elem.getAttributeValue("url");
        if( urlAttr == null ) {
            return elem;
        }
        try
        {
            URL url = new URL(this.baseURL, urlAttr);
            DOMBuilder builder =
                            new DOMBuilder( "org.jdom.adapters.XercesDOMAdapter" );
            org.jdom.Document doc = builder.build( url );
            Element root = doc.getRootElement();
            root.detach();
            elem.addContent(root);
        }
        catch(Exception e) {
            /// @todo handle exceptions, give feedback
            e.printStackTrace();
        }
        return elem;
        /// @todo implement
    }
    protected String loadText(Element elem) {
        String urlAttr = elem.getAttributeValue("url");
        if( urlAttr == null ) {
            return null;
        }
        /// @todo implement
        return "not yet implemented";
    }
    public Dictionary getParameters()
    {
        return this.parameters;
    }
    public String getTableName()
    {
        if( this.tableName != null ) {
            return this.tableName;
        }
        else {
            return this.databaseInfo.getTableName();
        }
    }
    public String getKeyName()
    {
        if( this.keyName != null ) {
            return this.keyName;
        }
        else {
            return this.databaseInfo.getKey();
        }
    }
    public DBConnection getConnection()
    {
        return this.dbConnection;
    }
    public static List getViewNames()
    {
        List retVal = new LinkedList();
        Iterator it = viewerRegistry.iterator();
        while(it.hasNext())
        {
            DatabaseViewerManager manager = (DatabaseViewerManager) it.next();
            retVal.add(manager.screenName);
        }
        return retVal;
    }
    public static int getNumberOfViews()
    {
        return viewerRegistry.size();
    }
    public static void resetRegistry()
    {
        viewerRegistry.clear();
    }
}
