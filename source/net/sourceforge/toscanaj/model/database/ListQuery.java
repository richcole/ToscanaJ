/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;

import java.util.Iterator;
import java.util.Vector;

public class ListQuery extends Query {
    private DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "listQuery";

    public static final ListQuery KEY_LIST_QUERY = new ListQuery(null, "List", "fake");

    public ListQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public ListQuery(DatabaseInfo info, Element element) throws XMLSyntaxError {
        super(element);
        this.info = info;
    }

    protected String getElementName() {
        return QUERY_ELEMENT_NAME;
    }

    public String getQueryHead() {
        String retValue = "SELECT ";
        retValue += info.getKey().getSqlExpression() + ", ";
        Iterator it = fieldList.iterator();
        while (it.hasNext()) {
            QueryField field = (QueryField) it.next();
            retValue += field.getQueryPart();
            if (it.hasNext()) {
                retValue += ", ";
            }
        }
        retValue += " FROM " + info.getTable().getSqlExpression() + " ";
        return retValue;
    }

    public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values, Vector referenceValues) {
        String displayString = this.formatResults(values, 1);
        DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(whereClause, displayString);
        retVal.setKey(values.get(0));
        return retVal;
    }
}
