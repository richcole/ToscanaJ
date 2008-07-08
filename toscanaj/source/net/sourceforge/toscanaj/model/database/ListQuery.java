/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.Iterator;

import org.jdom.Element;

public class ListQuery extends Query {
    private final DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "listQuery";

    public static final ListQuery KEY_LIST_QUERY = new ListQuery(null, "List",
            "fake");

    public ListQuery(final DatabaseInfo info, final String name,
            final String header) {
        super(name, header);
        this.info = info;
    }

    public ListQuery(final DatabaseInfo info, final Element element) {
        super(element);
        this.info = info;
    }

    @Override
    protected String getElementName() {
        return QUERY_ELEMENT_NAME;
    }

    @Override
    public String getQueryHead() {
        return "SELECT " + info.getKey().getSqlExpression() + ", "
                + getFieldList() + " FROM "
                + info.getTable().getSqlExpression() + " ";
    }

    @Override
    public String getOrderClause() {
        return "ORDER BY " + getFieldList();
    }

    private String getFieldList() {
        String retVal = "";
        final Iterator<QueryField> it = fieldList.iterator();
        while (it.hasNext()) {
            final QueryField field = it.next();
            retVal += field.getQueryPart();
            if (it.hasNext()) {
                retVal += ", ";
            }
        }
        return retVal;
    }

    @Override
    public DatabaseRetrievedObject createDatabaseRetrievedObject(
            final String whereClause, final String[] values,
            final String[] referenceValues) {
        assert referenceValues == null
                || values.length == referenceValues.length : "We must have the same number of reference values as values if reference values are used.";
        final String displayString = this.formatResults(values, 1);
        final DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(
                whereClause, displayString);
        retVal.setKey(values[0]);
        return retVal;
    }

    @Override
    public boolean doesNeedReferenceValues() {
        return false;
    }
}
