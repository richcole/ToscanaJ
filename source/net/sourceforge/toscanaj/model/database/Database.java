package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.model.Model;
import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.XML_Helper;

import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.controller.db.DBConnection;
// import net.sourceforge.toscanaj.model.Scheme;

import org.jdom.*;

public class Database extends Model implements XML_Serializable
{
    public DatabaseInfo m_info;
    public DBConnection m_connection;
    // public DBScheme     m_scheme;

    /**
     * Construct a new database element by reading the model from
     * from the dom.
     */
    public Database(Element elem) throws XML_SyntaxError {
        readXML(elem);
    }

    /**
     * Construct an empty database.
     */
    public Database() {
        m_info = new DatabaseInfo("","","","");
    }

    /**
     * Write this part of the model to the database node.
     */
    public void writeXML(Element elem) {

        Element databaseElem = XML_Helper.insertElement("database", elem);

        m_info.writeXML(databaseElem);
        //	m_database_info.writeXML(databaseElement);
        //	m_connecion.writeXML(databaseElement);
        //	m_scheme.writeXML(databaseElement);
    };

    /**
     * Read the database model from the element elem
     */
    public void readXML(Element elem) throws XML_SyntaxError {

        Element databaseElem = XML_Helper.mustbe("database", elem);
        if (m_info == null) {
            m_info = new DatabaseInfo(databaseElem);
        }
        else {
            m_info.readXML(databaseElem);
        }
        //	m_database_info.readXML(databaseElement);
        //	m_connecion.readXML(databaseElement);
        //	m_scheme.readXML(databaseElement);
        notifyObservers(true);
    };

    public void setInfo(DatabaseInfo info) {
        m_info = info;
        this.setChanged();
        this.notifyObservers();
    }

    public DatabaseInfo getInfo() {
        return m_info;
    }

    /**
     * Connect to the database by creating a database connection.
     */
    public void connect() throws Exception {
        m_connection = new DBConnection(getInfo().url, getInfo().user, getInfo().password);
    };
};
