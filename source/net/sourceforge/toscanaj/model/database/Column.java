/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 5:55:51 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model.database;

import org.jdom.*;

import java.sql.Types;

import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.XML_Helper;

public class Column implements XML_Serializable {

    private String name;
    private int type;
    private Table table;

    public static final String COLUMN_ELEMENT_NAME = "column";
    public static final String COLUMN_NAME_ATTRIBUTE_NAME = "name";
    public static final String COLUMN_TYPE_ATTRIBUTE_NAME = "type";

    public Column(String name, int type, net.sourceforge.toscanaj.model.database.Table table) {
        this.table = table;
        this.name = name;
        this.type = type;
    }

    public Column(Element elem, net.sourceforge.toscanaj.model.database.Table table) throws XML_SyntaxError {
        this.table = table;
        readXML(elem);
    }

    public Element toXML() {
        Element retVal = new Element(COLUMN_ELEMENT_NAME);
        retVal.setAttribute(COLUMN_NAME_ATTRIBUTE_NAME, name);
        retVal.setAttribute(COLUMN_TYPE_ATTRIBUTE_NAME, String.valueOf(type));
        return retVal;
    }

    public void readXML(Element elem) throws XML_SyntaxError {
        XML_Helper.checkName(COLUMN_ELEMENT_NAME, elem);
        name = XML_Helper.getAttribute(elem, COLUMN_NAME_ATTRIBUTE_NAME).getValue();
        type = XML_Helper.getIntAttribute(elem, COLUMN_TYPE_ATTRIBUTE_NAME);
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public net.sourceforge.toscanaj.model.database.Table getTable() {
        return table;
    }
}
