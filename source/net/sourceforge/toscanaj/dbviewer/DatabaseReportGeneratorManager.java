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

/**
 * @todo this is the same stuff as the viewer manager -- reuse!
 */
public class DatabaseReportGeneratorManager
{
    private static Component parentComponent = null;
    private static List generatorRegistry = new LinkedList();
    private DatabaseReportGenerator reportGenerator = null;
    private String screenName = null;
    private Element template = null;
    private Dictionary parameters = new Hashtable();
    private DatabaseInfo databaseInfo;
    private DBConnection dbConnection;
    public DatabaseReportGeneratorManager(Element reportDefinition, DatabaseInfo databaseInfo, DBConnection connection)
            throws DatabaseViewerInitializationException
    {
        screenName = reportDefinition.getAttributeValue("name");
        template = reportDefinition.getChild("template");
        List parameterElems = reportDefinition.getChildren("parameter");
        Iterator it = parameterElems.iterator();
        while(it.hasNext())
        {
            Element parameterElem = (Element) it.next();
            this.parameters.put(parameterElem.getAttributeValue("name"), parameterElem.getAttributeValue("value"));
        }
        this.databaseInfo = databaseInfo;
        this.dbConnection = connection;
        // register the object as last step, after all info has been set (which is used by the generator)
        String className = reportDefinition.getAttributeValue("class");
        if( className == null )
        {
            throw new DatabaseViewerInitializationException("Could not find class attribute on <report>");
        }
        try
        {
            Class reportGeneratorClass = Class.forName(className);
            reportGenerator = (DatabaseReportGenerator)reportGeneratorClass.newInstance();
            reportGenerator.initialize(this); 
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
        generatorRegistry.add(this);
    }
    public static void setParentComponent(Component parentComponent)
    {
        DatabaseReportGeneratorManager.parentComponent = parentComponent;
    }
    public static Frame getParentWindow()
    {
        return JOptionPane.getFrameForComponent( parentComponent );
    }        
    public static void showReport(int generatorID, String whereClause)
    {
        DatabaseReportGeneratorManager manager = (DatabaseReportGeneratorManager) generatorRegistry.get(generatorID);
        manager.reportGenerator.showReport(whereClause);
    }
    public static void showReport(String reportName, String whereClause)
    {
        for(int i = 0; i<generatorRegistry.size(); i++)
        {
            DatabaseReportGeneratorManager manager = (DatabaseReportGeneratorManager) generatorRegistry.get(i);
            if(manager.screenName.equals(reportName))
            {
                manager.reportGenerator.showReport(whereClause);
            }
        }
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
    public static List getReportNames()
    {
        List retVal = new LinkedList();
        Iterator it = generatorRegistry.iterator();
        while(it.hasNext())
        {
            DatabaseReportGeneratorManager manager = (DatabaseReportGeneratorManager) it.next();
            retVal.add(manager.screenName);
        }
        return retVal;
    }
    public static int getNumberOfReports()
    {
        return generatorRegistry.size();
    }
    public static void resetRegistry()
    {
        generatorRegistry.clear();
    }
}
