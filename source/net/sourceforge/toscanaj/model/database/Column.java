/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;

public class Column implements XMLizable {

    private String name;
    private int type;
    private Table table;

    public static final String COLUMN_ELEMENT_NAME = "column";
    public static final String COLUMN_NAME_ATTRIBUTE_NAME = "name";
    public static final String COLUMN_TYPE_ATTRIBUTE_NAME = "type";

    public Column(String name, int type, Table table) {
        this.table = table;
        this.name = name;
        this.type = type;
    }

    public Column(Element elem, Table table) throws XMLSyntaxError {
        this.table = table;
        readXML(elem);
    }

    public Element toXML() {
        Element retVal = new Element(COLUMN_ELEMENT_NAME);
        retVal.setAttribute(COLUMN_NAME_ATTRIBUTE_NAME, name);
        retVal.setAttribute(COLUMN_TYPE_ATTRIBUTE_NAME, String.valueOf(type));
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(COLUMN_ELEMENT_NAME, elem);
        name = XMLHelper.getAttribute(elem, COLUMN_NAME_ATTRIBUTE_NAME).getValue();
        type = XMLHelper.getIntAttribute(elem, COLUMN_TYPE_ATTRIBUTE_NAME);
    }

    public String getDisplayName() {
        return this.name;
    }
    
    public int getType() {
        return this.type;
    }

    public Table getTable() {
        return this.table;
    }

    public String getSqlExpression() {
        return getQuotedIdentifier(this.name);
    }

	/**
	 * @todo outsource this into some central place. It is copied and pasted
	 * from DumpSqlScript. A good idea might be implementing an interface for
	 * SQL expressions, which allows access to this stuff.
	 */
    private static String getQuotedIdentifier(String identifier) {
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
}
