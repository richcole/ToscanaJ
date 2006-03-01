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
        return toXML(COLUMN_ELEMENT_NAME);
    }

    public Element toXML(String elementName) {
        Element retVal = new Element(elementName);
        retVal.setAttribute(COLUMN_NAME_ATTRIBUTE_NAME, name);
        // @todo either remove this completely or start using it
        // retVal.setAttribute(COLUMN_TYPE_ATTRIBUTE_NAME, String.valueOf(type));
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
		this.name = elem.getAttributeValue(COLUMN_NAME_ATTRIBUTE_NAME);
		if(this.name == null) {
			this.name = elem.getText();
			if(this.name == null) {
				throw new XMLSyntaxError("No name given for column");
			}
		}
		if(elem.getAttribute(COLUMN_TYPE_ATTRIBUTE_NAME) != null) {
			this.type = XMLHelper.getIntAttribute(elem, COLUMN_TYPE_ATTRIBUTE_NAME);
		}
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
        return Table.getQuotedIdentifier(this.name);
    }
}
