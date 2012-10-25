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

public class AggregateQuery extends Query {
    private final DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "aggregateQuery";

    public static final AggregateQuery COUNT_QUERY = new AggregateQuery(null,
            "Count", "fake");
    public static final AggregateQuery PERCENT_QUERY = new AggregateQuery(null,
            "Distribution of Objects", "fake");

    public AggregateQuery(final DatabaseInfo info, final String name,
            final String header) {
        super(name, header);
        this.info = info;
    }

    public AggregateQuery(final DatabaseInfo info, final Element element) {
        super(element);
        this.info = info;
    }

    @Override
    protected String getElementName() {
        return QUERY_ELEMENT_NAME;
    }

    @Override
    public String getQueryHead() {
        // this gives an additional column replacing the key (used only in
        // lists)
        String retValue = "SELECT count(*),";
        final Iterator<QueryField> it = fieldList.iterator();
        while (it.hasNext()) {
            final QueryField field = it.next();
            retValue += field.getQueryPart();
            if (it.hasNext()) {
                retValue += ", ";
            }
        }
        retValue += " FROM " + info.getTable().getSqlExpression() + " ";
        return retValue;
    }

    @Override
    public String getOrderClause() {
        return "";
    }

    @Override
    public DatabaseRetrievedObject createDatabaseRetrievedObject(
            final String whereClause, final String[] values,
            final String[] referenceValues) {
        assert referenceValues == null
                || values.length == referenceValues.length : "We must have the same number of reference values as values if reference values are used.";
        if (values[0].equals("0")) {
            return null;
        }
        Object[] valuesToUse;
        if (this.doesNeedReferenceValues()) {
            assert referenceValues != null : "Missing reference values for query that requires them";
            // /@todo this is all a bit brute force -> be smarter
            valuesToUse = new Object[values.length];

            // skip the first extra field, putting the value straight into the
            // results
            valuesToUse[0] = values[0];

            final Iterator<QueryField> fieldIt = this.fieldList.iterator();
            for (int i = 1; i < values.length; i++) {
                final String value = values[i];
                final String refVal = referenceValues[i];
                final QueryField field = fieldIt.next();
                if (field.isRelative()) {
                    valuesToUse[i] = new Double(Double.parseDouble(value)
                            / Double.parseDouble(refVal));
                } else {
                    valuesToUse[i] = value;
                }
            }
        } else {
            valuesToUse = values;
        }
        final String displayString = this.formatResults(valuesToUse, 1);
        return new DatabaseRetrievedObject(whereClause, displayString);
    }

    @Override
    public boolean doesNeedReferenceValues() {
        for (final QueryField field : this.fieldList) {
            if (field.isRelative()) {
                return true;
            }
        }
        return false;
    }
}
