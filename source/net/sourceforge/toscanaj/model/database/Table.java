/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.events.TableChangedEvent;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;
import org.tockit.events.EventBroker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table implements XMLizable {

    private String name;

    private Column key;
    private EventBroker broker;
    private List columns;
    public static final String TABLE_ELEMENT_NAME = "table";
    public static final String TABLE_NAME_ATTRIBUTE_NAME = "name";

    public Table(EventBroker broker, String name) {
        this.columns = new ArrayList();
        this.broker = broker;
        this.name = name;
    }

    public Table(EventBroker broker, Element elem) throws XMLSyntaxError {
        this(broker, "");
        readXML(elem);
    }

    public Element toXML() {
        Element retVal = new Element(TABLE_ELEMENT_NAME);
        retVal.setAttribute(TABLE_NAME_ATTRIBUTE_NAME, name);
        if (key != null) {
            retVal.addContent(key.toXML());
        }
        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
            Column column = (Column) iterator.next();
            retVal.addContent(column.toXML());
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(TABLE_ELEMENT_NAME, elem);
        name = XMLHelper.getAttribute(elem, TABLE_NAME_ATTRIBUTE_NAME).getValue();
        List columnElems = elem.getChildren(Column.COLUMN_ELEMENT_NAME);
        for (Iterator iterator = columnElems.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            columns.add(new Column(element, this));
        }
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String getName() {
    	DatabaseConnection connection = DatabaseConnection.getConnection();
    	if( connection != null && connection.getDatabaseType() == DatabaseInfo.ACCESS_FILE) {
			return "[" + name + "]";
    	}
        return name;
    }
    
    public String getPlainName() {
    	return this.name;
    }

    public Column getKey() {
        return key;
    }

    public String getKeyName() {
        return key.getName();
    }

    public List getColumns() {
        return columns;
    }

    public void setKey(Column key) {
        this.key = key;
        broker.processEvent(new TableChangedEvent(this, this));
    }

    public void setName(String name) {
        this.name = name;
    }

}
