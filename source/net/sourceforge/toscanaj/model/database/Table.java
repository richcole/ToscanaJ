/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

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

    private static final String SUPPRESS_ESCAPING_ATTRIBUTE_NAME = "suppressEscaping";
    private boolean suppressEscaping = false;
    public static final String STANDARD_SQL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_";
    public static final String STANDARD_SQL_FIRST_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String name;

    private Column key;
    private EventBroker broker;
    private List columns;
    public static final String TABLE_ELEMENT_NAME = "table";
    public static final String TABLE_NAME_ATTRIBUTE_NAME = "name";

    public Table(EventBroker broker, String name, boolean suppressEscaping) {
        this.columns = new ArrayList();
        this.broker = broker;
        this.name = name;
        this.suppressEscaping = suppressEscaping;
    }

    public Table(String name, boolean suppressEscaping) {
        this(null, name, suppressEscaping);
    }

	public Table(EventBroker broker, Element elem) throws XMLSyntaxError {
		this(broker, "", false);
		readXML(elem);
	}

	public Table(Element elem) throws XMLSyntaxError {
		this(null, "", false);
		readXML(elem);
	}

    public Element toXML() {
        Element retVal = new Element(TABLE_ELEMENT_NAME);
        retVal.setAttribute(TABLE_NAME_ATTRIBUTE_NAME, this.name);
        // @todo either remove this completely or start using it
//        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
//            Column column = (Column) iterator.next();
//            retVal.addContent(column.toXML());
//        }
        if(this.suppressEscaping) {
        	retVal.setAttribute(SUPPRESS_ESCAPING_ATTRIBUTE_NAME, "true");
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(TABLE_ELEMENT_NAME, elem);
        this.name = elem.getAttributeValue(TABLE_NAME_ATTRIBUTE_NAME);
        if(this.name == null) {
        	this.name = elem.getText();
        }
        List columnElems = elem.getChildren(Column.COLUMN_ELEMENT_NAME);
        for (Iterator iterator = columnElems.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            this.columns.add(new Column(element, this));
        }
        String attributeValue = elem.getAttributeValue(SUPPRESS_ESCAPING_ATTRIBUTE_NAME);
        if("true".equalsIgnoreCase(attributeValue) ||
                  "yes".equalsIgnoreCase(attributeValue) ||
                  "on".equalsIgnoreCase(attributeValue)) {
        	this.suppressEscaping = true;
        }
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String getDisplayName() {
    	return this.name;
    }

    public Column getKey() {
        return key;
    }

    public List getColumns() {
        return columns;
    }

    public void setKey(Column key) {
        this.key = key;
        if(broker != null) {
        	broker.processEvent(new TableChangedEvent(this, this));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSqlExpression() {
    	if(this.suppressEscaping) {
    		return this.name;
    	} else {
			return getQuotedIdentifier(this.name);
    	}
    }

    /**
     * @todo outsource this into some central place. It is copied and pasted
     * from DumpSqlScript. A good idea might be implementing an interface for
     * SQL expressions, which allows access to this stuff.
     */
    public static String getQuotedIdentifier(String identifier) {
    	/// @todo we might need something like the following lines for supporting Access
//        DatabaseConnection connection = DatabaseConnection.getConnection();
//        if( connection != null && connection.getDatabaseType() == DatabaseInfo.ACCESS_FILE) {
//            return "[" + name + "]";
//        }
		if(!quotingIsNeeded(identifier)) {
			return identifier;
		}
        String retVal = "\"";
        for(int i = 0; i < identifier.length(); i++) {
            char curChar = identifier.charAt(i);
            if(curChar == '\"') {
                retVal += "\"\"";
            } else {
                retVal += curChar;
            }
        }
        retVal += "\"";
        return retVal;
    }

    public static boolean quotingIsNeeded(String identifier) {
		for(int i = 0; i < identifier.length(); i++) {
			char curChar = identifier.charAt(i);
            if(i == 0) {
                if(STANDARD_SQL_FIRST_CHAR.indexOf(curChar) == -1) {
                    return true;
                }
            } else {
                if(STANDARD_SQL_CHARS.indexOf(curChar) == -1) {
                    return true;
                }
            }
		}
        return false;
    }
}
