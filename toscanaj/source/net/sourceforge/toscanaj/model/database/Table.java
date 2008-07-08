/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.toscanaj.model.events.TableChangedEvent;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.events.EventBroker;

public class Table implements XMLizable {

    private static final String SUPPRESS_ESCAPING_ATTRIBUTE_NAME = "suppressEscaping";
    private boolean suppressEscaping = false;
    public static final String STANDARD_SQL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_";
    public static final String STANDARD_SQL_FIRST_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String name;

    private Column key;
    private final EventBroker broker;
    private final List<Column> columns;
    public static final String TABLE_ELEMENT_NAME = "table";
    public static final String TABLE_NAME_ATTRIBUTE_NAME = "name";

    public Table(final EventBroker broker, final String name,
            final boolean suppressEscaping) {
        this.columns = new ArrayList<Column>();
        this.broker = broker;
        this.name = name;
        this.suppressEscaping = suppressEscaping;
    }

    public Table(final String name, final boolean suppressEscaping) {
        this(null, name, suppressEscaping);
    }

    public Table(final EventBroker broker, final Element elem)
            throws XMLSyntaxError {
        this(broker, "", false);
        readXML(elem);
    }

    public Table(final Element elem) throws XMLSyntaxError {
        this(null, "", false);
        readXML(elem);
    }

    public Element toXML() {
        final Element retVal = new Element(TABLE_ELEMENT_NAME);
        retVal.setAttribute(TABLE_NAME_ATTRIBUTE_NAME, this.name);
        // @todo either remove this completely or start using it
        // for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
        // Column column = (Column) iterator.next();
        // retVal.addContent(column.toXML());
        // }
        if (this.suppressEscaping) {
            retVal.setAttribute(SUPPRESS_ESCAPING_ATTRIBUTE_NAME, "true");
        }
        return retVal;
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, TABLE_ELEMENT_NAME);
        this.name = elem.getAttributeValue(TABLE_NAME_ATTRIBUTE_NAME);
        if (this.name == null) {
            this.name = elem.getText();
        }
        final List<Element> columnElems = elem
                .getChildren(Column.COLUMN_ELEMENT_NAME);
        for (final Element element : columnElems) {
            this.columns.add(new Column(element, this));
        }
        final String attributeValue = elem
                .getAttributeValue(SUPPRESS_ESCAPING_ATTRIBUTE_NAME);
        if ("true".equalsIgnoreCase(attributeValue)
                || "yes".equalsIgnoreCase(attributeValue)
                || "on".equalsIgnoreCase(attributeValue)) {
            this.suppressEscaping = true;
        }
    }

    public void addColumn(final Column column) {
        columns.add(column);
    }

    public String getDisplayName() {
        return this.name;
    }

    public Column getKey() {
        return key;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setKey(final Column key) {
        this.key = key;
        if (broker != null) {
            broker.processEvent(new TableChangedEvent(this, this));
        }
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSqlExpression() {
        if (this.suppressEscaping) {
            return this.name;
        } else {
            return getQuotedIdentifier(this.name);
        }
    }

    /**
     * @todo outsource this into some central place. It is copied and pasted
     *       from DumpSqlScript. A good idea might be implementing an interface
     *       for SQL expressions, which allows access to this stuff.
     */
    public static String getQuotedIdentifier(final String identifier) {
        // / @todo we might need something like the following lines for
        // supporting Access
        // DatabaseConnection connection = DatabaseConnection.getConnection();
        // if( connection != null && connection.getDatabaseType() ==
        // DatabaseInfo.ACCESS_FILE) {
        // return "[" + name + "]";
        // }
        if (!quotingIsNeeded(identifier)) {
            return identifier;
        }
        String retVal = "\"";
        for (int i = 0; i < identifier.length(); i++) {
            final char curChar = identifier.charAt(i);
            if (curChar == '\"') {
                retVal += "\"\"";
            } else {
                retVal += curChar;
            }
        }
        retVal += "\"";
        return retVal;
    }

    public static boolean quotingIsNeeded(final String identifier) {
        for (int i = 0; i < identifier.length(); i++) {
            final char curChar = identifier.charAt(i);
            if (i == 0) {
                if (STANDARD_SQL_FIRST_CHAR.indexOf(curChar) == -1) {
                    return true;
                }
            } else {
                if (STANDARD_SQL_CHARS.indexOf(curChar) == -1) {
                    return true;
                }
            }
        }
        return false;
    }
}
