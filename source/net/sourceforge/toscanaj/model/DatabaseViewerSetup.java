package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jdom.Element;

public class DatabaseViewerSetup
{
    private static List viewerRegistry = new LinkedList();
    private Element template = null;
    private Hashtable parameters = new Hashtable();
    private DatabaseInfo databaseInfo;
    private DBConnection dbConnection;
    public DatabaseViewerSetup(Element viewerDefinition, DatabaseInfo databaseInfo, DBConnection connection)
    {
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
        viewerRegistry.add(this);
    }
    public static DatabaseViewerSetup getViewerSetup(int i)
    {
        return (DatabaseViewerSetup) viewerRegistry.get(i);
    }
    public Element getTemplate()
    {
        return this.template;
    }
    public String getTemplateString()
    {
        return this.template.getText();
    }
    public Hashtable getParameters()
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
