/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import org.jdom.Element;

import java.util.Iterator;
import java.util.Vector;

public class DistinctListQuery extends Query {
    private DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "distinctListQuery";

    public DistinctListQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public DistinctListQuery(DatabaseInfo info, Element element) {
        super(element);
        this.info = info;
    }

    protected String getElementName() {
        return QUERY_ELEMENT_NAME;
    }

    public String getQueryHead() {
        return "SELECT DISTINCT " + getFieldList() + " FROM " + 
                info.getTable().getSqlExpression() + " ";
    }

    public String getOrderClause() {
        return "ORDER BY " + getFieldList();
    }

    private String getFieldList() {
        String retVal = "";
        Iterator it = fieldList.iterator();
        while (it.hasNext()) {
            QueryField field = (QueryField) it.next();
            retVal += field.getQueryPart();
            if (it.hasNext()) {
                retVal += ", ";
            }
        }
        return retVal;
    }

    public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values, Vector referenceValues) {
        String displayString = this.formatResults(values, 0);
        DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(whereClause, displayString);
        String specialWhereClause = whereClause;
        Iterator it = fieldList.iterator();
        Iterator it2 = values.iterator();
        while (it.hasNext() && it2.hasNext()) {
            QueryField field = (QueryField) it.next();
            String value = (String) it2.next();
            specialWhereClause += " AND (" + field.getQueryPart() + "='" + value + "')";
        }
        retVal.setSpecialWhereClause(specialWhereClause);
        return retVal;
    }

	public boolean doesNeedReferenceValues() {
		return false;
	}
}