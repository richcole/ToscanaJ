/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;

public abstract class Query implements XMLizable {
    private static final String QUERY_FIELD_IS_RELATIVE_ATTRIBUTE_NAME = "relative";
    private static final String QUERY_NAME_ATTRIBUTE_NAME = "name";
    private static final String QUERY_HEAD_ATTRIBUTE_NAME = "head";
    private static final String QUERY_FIELD_ELEMENT_NAME = "queryField";
    private static final String QUERY_FIELD_NAME_ATTRIBUTE_NAME = "name";
    private static final String QUERY_FIELD_SEPARATOR_ATTRIBUTE_NAME = "separator";
    private static final String QUERY_FIELD_FORMAT_ATTRIBUTE_NAME = "format";

    protected class QueryField {
        private final String fieldName;
        private final String format;
        private final String separator;
        private final String queryPart;
        private final boolean isRelative;

        public QueryField(final String name, final String format,
                final String separator, final String queryPart,
                final boolean isRelative) {
            this.fieldName = name;
            this.format = format;
            this.separator = separator;
            this.queryPart = queryPart;
            this.isRelative = isRelative;
        }

        public String getName() {
            return fieldName;
        }

        public String getFormat() {
            return format;
        }

        public String getSeparator() {
            return separator;
        }

        public String getQueryPart() {
            return queryPart;
        }

        public boolean isRelative() {
            return this.isRelative;
        }
    }

    private String name;

    private String header;
    protected List<QueryField> fieldList = new ArrayList<QueryField>();

    public Query(final String name, final String header) {
        this.name = name;
        this.header = header;
    }

    public Query(final Element element) {
        readXML(element);
    }

    public Element toXML() {
        final Element retVal = new Element(getElementName());
        retVal.setAttribute(QUERY_NAME_ATTRIBUTE_NAME, this.name);
        XMLHelper.addOptionalAttribute(retVal, QUERY_HEAD_ATTRIBUTE_NAME,
                this.header);
        for (final QueryField queryField : fieldList) {
            final Element elem = new Element(QUERY_FIELD_ELEMENT_NAME);
            XMLHelper.addOptionalAttribute(elem,
                    QUERY_FIELD_NAME_ATTRIBUTE_NAME, queryField.getName());
            XMLHelper.addOptionalAttribute(elem,
                    QUERY_FIELD_SEPARATOR_ATTRIBUTE_NAME, queryField
                            .getSeparator());
            XMLHelper.addOptionalAttribute(elem,
                    QUERY_FIELD_FORMAT_ATTRIBUTE_NAME, queryField.getFormat());
            elem.setText(queryField.getQueryPart());
            retVal.addContent(elem);
        }
        return retVal;
    }

    public void readXML(final Element elem) {
        this.name = elem.getAttributeValue(QUERY_FIELD_NAME_ATTRIBUTE_NAME);
        this.header = elem.getAttributeValue(QUERY_HEAD_ATTRIBUTE_NAME);
        for (final Iterator<Element> iterator = elem.getChildren(
                QUERY_FIELD_ELEMENT_NAME).iterator(); iterator.hasNext();) {
            final Element queryFieldElement = iterator.next();
            final QueryField field = new QueryField(
                    queryFieldElement
                            .getAttributeValue(QUERY_FIELD_NAME_ATTRIBUTE_NAME),
                    queryFieldElement
                            .getAttributeValue(QUERY_FIELD_FORMAT_ATTRIBUTE_NAME),
                    queryFieldElement
                            .getAttributeValue(QUERY_FIELD_SEPARATOR_ATTRIBUTE_NAME),
                    queryFieldElement.getText(),
                    "true"
                            .equals(queryFieldElement
                                    .getAttributeValue(QUERY_FIELD_IS_RELATIVE_ATTRIBUTE_NAME)));
            this.fieldList.add(field);
        }
    }

    public String getName() {
        return this.name;
    }

    public void insertQueryColumn(final String columnName,
            final String columnFormat, final String separator,
            final String queryPart, final boolean isRelative) {
        final QueryField field = new QueryField(columnName, columnFormat,
                separator, queryPart, isRelative);
        fieldList.add(field);
    }

    /**
     * Formats a row of a result set for this query.
     * 
     * The input is a ResultSet which is supposed to point to an existing row.
     * Column one is supposed to be the first column of the query definition and
     * so on.
     * 
     * The return value is a String which returns a formatted version of the row
     */
    public String formatResults(final Object[] values, final int startPosition) {
        String rowRes = new String();
        if (header != null) {
            rowRes += header;
        }
        final Iterator<QueryField> colDefIt = this.fieldList.iterator();
        // skip key, start with 1
        int i = startPosition;
        while (colDefIt.hasNext()) {
            final QueryField field = colDefIt.next();
            final String value = values[i].toString();
            i++;
            if (field.getFormat() != null) {
                final DecimalFormat format = new DecimalFormat(field
                        .getFormat());
                rowRes += format.format(Double.parseDouble(value));
            } else {
                rowRes += value;
            }
            if (field.getSeparator() != null) {
                rowRes += field.getSeparator();
            }
        }
        return rowRes;
    }

    abstract protected String getElementName();

    abstract public String getQueryHead();

    /**
     * Returns an SQL clause to order the results if possible.
     * 
     * This is a clause of the form "ORDER BY [field1],...,[fieldN]", which is
     * typically used only for list queries, all other query implementations
     * should return an empty string.
     */
    abstract public String getOrderClause();

    /**
     * TODO the way reference values are handled is not typesafe: fix.
     * 
     * @param whereClause
     *            The SQL WHERE clause to query. Not null.
     * @param values
     *            The query results to turn into objects. Not null.
     * @param referenceValues
     *            The reference values that can be used for relative results,
     *            usually the same values for the top node. May be null. Must be
     *            the same length as values if supplied.
     */
    abstract public DatabaseRetrievedObject createDatabaseRetrievedObject(
            String whereClause, String[] values, String[] referenceValues);

    abstract public boolean doesNeedReferenceValues();
}
