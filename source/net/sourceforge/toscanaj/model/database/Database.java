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
    public DatabaseInfo info;
    public DBConnection connection;
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
        info = new DatabaseInfo("","","","");
    }

    /**
     * Write this part of the model to the database node.
     */
    public void writeXML(Element elem) {

        Element databaseElem = XML_Helper.insertElement("database", elem);

        info.writeXML(databaseElem);
        //	m_database_info.writeXML(databaseElement);
        //	m_connecion.writeXML(databaseElement);
        //	m_scheme.writeXML(databaseElement);
    };

    /**
     * Read the database model from the element elem
     */
    public void readXML(Element elem) throws XML_SyntaxError {

        Element databaseElem = XML_Helper.mustbe("database", elem);
        if (info == null) {
            info = new DatabaseInfo(databaseElem);
        }
        else {
            info.readXML(databaseElem);
        }
        //	m_database_info.readXML(databaseElement);
        //	m_connecion.readXML(databaseElement);
        //	m_scheme.readXML(databaseElement);
        notifyObservers(true);
    };

    public void setInfo(DatabaseInfo info) {
        this.info = info;
        this.setChanged();
        this.notifyObservers();
    }

    public DatabaseInfo getInfo() {
        return info;
    }

    /**
     * Connect to the database by creating a database connection.
     */
    public void connect() throws Exception {
        try {
            Class.forName(info.driver);
        } catch (ClassNotFoundException e) {
            throw new Exception(
                "Could not load class \"" +
                    info.driver + "\" as database driver.");
        }

        connection = new DBConnection(getInfo().url, getInfo().user, getInfo().password);
    };
};
