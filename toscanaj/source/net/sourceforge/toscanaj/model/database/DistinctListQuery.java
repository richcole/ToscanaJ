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

public class DistinctListQuery extends Query {
    private final DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "distinctListQuery";

    public DistinctListQuery(final DatabaseInfo info, final String name,
            final String header) {
        super(name, header);
        this.info = info;
    }

    public DistinctListQuery(final DatabaseInfo info, final Element element) {
        super(element);
        this.info = info;
    }

    @Override
    protected String getElementName() {
        return QUERY_ELEMENT_NAME;
    }

    @Override
    public String getQueryHead() {
        return "SELECT DISTINCT " + getFieldList() + " FROM "
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
        final String displayString = this.formatResults(values, 0);
        final DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(
                whereClause, displayString);
        String specialWhereClause = whereClause;
        final Iterator<QueryField> it = fieldList.iterator();
        for (final String value : values) {
            final QueryField field = it.next();
            specialWhereClause += " AND (" + field.getQueryPart() + "='"
                    + value + "')";
        }
        retVal.setSpecialWhereClause(specialWhereClause);
        return retVal;
    }

    @Override
    public boolean doesNeedReferenceValues() {
        return false;
    }
}
