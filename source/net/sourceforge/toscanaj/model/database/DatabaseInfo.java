package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.model.Model;
import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.XML_Helper;

import org.jdom.*;

public class DatabaseInfo extends Model implements XML_Serializable {

    public String url = new String();
    public String driver = new String();
    public String user = new String();
    public String password = new String();

    // public DBConnection m_connection;
    // public DBScheme     m_scheme;

    /**
     * Construct a new database element by reading the model from
     * from the dom.
     */
    public DatabaseInfo(Element elem) throws XML_SyntaxError {
        readXML(elem);
    }

    public DatabaseInfo(
            String a_url,
            String a_driver,
            String a_user,
            String a_password)
    {
        url = a_url;
        driver = a_driver;
        user = a_user;
        password = a_password;
    }

    /**
     * Write this part of the model to the database node.
     */
    public void writeXML(Element elem) {
        Element urlElem = new Element("url");
        urlElem.setAttribute("driver",   driver);
        urlElem.setAttribute("user",     user);
        urlElem.setAttribute("password", password);
        elem.addContent(urlElem);
        urlElem.addContent(url);
        notifyObservers();
    }

    /**
     * Write this part of the model to the database node.
     */
    public void readXML(Element elem) throws XML_SyntaxError {
        Element urlElem = XML_Helper.mustbe("url", elem);
        url      = urlElem.getText();
        driver   = urlElem.getAttributeValue("driver");
        user     = urlElem.getAttributeValue("user");
        password = urlElem.getAttributeValue("password");
        notifyObservers(true);
    }
}
