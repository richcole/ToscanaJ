package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.Model;
import org.jdom.Element;

import net.sourceforge.toscanaj.model.database.Database;
// import net.sourceforge.toscanaj.model.ScaleSet;

import org.jdom.*;
import javax.swing.JOptionPane;

public class AnacondaModel extends Model implements XML_Serializable
{
    private Database database;
    private ViewListModel modelViewList = new ViewListModel();
    //    public ScaleSet m_scaleSet;

    public ViewListModel getModelViewList() {
        return modelViewList;
    }

    /**
     * Construct a new anaconda model using the XML definition
     */
    public AnacondaModel(Element elem) throws XML_SyntaxError {
        readXML(elem);
    }

    /**
     * Construct an empty anaconda model.
     */
    public AnacondaModel() {
        database = new Database();
    }

    /**
     * Write this part of the model to the database node.
     */
    public void writeXML(Element elem) {
        // assume that elem refers to an anaconda element.
        // Element anacondaElem = XML_Helper.insertElement("anaconda", elem);
        database.writeXML(elem);
        //	m_scaleSet.writeXML(databaseElement);
    };

    /**
     * Read the model from the database, if a failure occurs the
     * model won't have changed.
     */
    public void readXML(Element elem) throws XML_SyntaxError {
        try {
            if ( ! elem.getName().equals("anaconda") ) {
                throw new XML_SyntaxError("Expected element anaconda as outer element,"
                    + " instead found '" + elem.getName() + "'.");
            }
            if (database == null) {
                database = new Database(elem);
            }
            else {
                database.readXML(elem);
            }

            database = database;
            // updateAllViews();
        }
        catch (XML_SyntaxError ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failure to read the file:" + ex.getMessage(),
                    "File format error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        notifyObservers(true);
    };

    public Database getDatabase() {
        return database;
    };
};
