package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JOptionPane;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jdom.Element;

public class DatabaseViewerManager
{
    private static Component parentComponent = null;
    private static List viewerRegistry = new LinkedList();
    private DatabaseViewer viewer = null;
    private String screenName = null;
    private Element template = null;
    private Dictionary parameters = new Hashtable();
    private DatabaseInfo databaseInfo;
    private DBConnection dbConnection;
    public DatabaseViewerManager(Element viewerDefinition, DatabaseInfo databaseInfo, DBConnection connection)
    {
        screenName = viewerDefinition.getAttributeValue("name");
        template = viewerDefinition.getChild("template");
        List parameterElems = viewerDefinition.getChildren("parameter");
        Iterator it = parameterElems.iterator();
        while(it.hasNext())
        {
            Element parameterElem = (Element) it.next();
            this.parameters.put(parameterElem.getAttributeValue("name"), parameterElem.getAttributeValue("value"));
        }
        this.databaseInfo = databaseInfo;
        this.dbConnection = connection;
        // register the viewer object as last step, after all info has been set (which is used by the viewer)
        try
        {
            Class viewerClass = Class.forName(viewerDefinition.getAttributeValue("class"));
            viewer = (DatabaseViewer)viewerClass.newInstance();
            viewer.initialize(this); 
        }
        catch( ClassNotFoundException e )
        {
            e.printStackTrace();
            /// @todo do something more useful like reporting the error in the UI and we have to boil out here
        }
        catch( InstantiationException e )
        {
            e.printStackTrace();
            /// @todo do something more useful like reporting the error in the UI and we have to boil out here
        }
        catch( IllegalAccessException e )
        {
            e.printStackTrace();
            /// @todo do something more useful like reporting the error in the UI and we have to boil out here
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
    public Element getTemplate()
    {
        return this.template;
    }
    public String getTemplateString()
    {
        if( this.template == null )
        {
            return null;
        }
        return this.template.getText();
    }
    public Dictionary getParameters()
    {
        return this.parameters;
    }
    public DatabaseInfo getDatabaseInfo()
    {
        return this.databaseInfo;
    }
    public DBConnection getConnection()
    {
        return this.dbConnection;
    }
}
