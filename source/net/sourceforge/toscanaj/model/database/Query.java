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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public abstract class Query implements XMLizable {
    private static final String QUERY_FIELD_IS_RELATIVE_ATTRIBUTE_NAME = "relative";
    private static final String QUERY_NAME_ATTRIBUTE_NAME = "name";
    private static final String QUERY_HEAD_ATTRIBUTE_NAME = "head";
    private static final String QUERY_FIELD_ELEMENT_NAME = "queryField";
    private static final String QUERY_FIELD_NAME_ATTRIBUTE_NAME = "name";
    private static final String QUERY_FIELD_SEPARATOR_ATTRIBUTE_NAME = "separator";
    private static final String QUERY_FIELD_FORMAT_ATTRIBUTE_NAME = "format";

    protected class QueryField {
        private String name;
        private String format;
        private String separator;
        private String queryPart;
        private boolean isRelative;

        public QueryField(String name, String format, String separator, String queryPart, boolean isRelative) {
            this.name = name;
            this.format = format;
            this.separator = separator;
            this.queryPart = queryPart;
            this.isRelative = isRelative;
        }

        public String getName() {
            return name;
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
    protected List fieldList = new ArrayList();

    public Query(String name, String header) {
        this.name = name;
        this.header = header;
    }

    public Query(Element element) throws XMLSyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(getElementName());
        retVal.setAttribute(QUERY_NAME_ATTRIBUTE_NAME, this.name);
        XMLHelper.addOptionalAttribute(retVal, QUERY_HEAD_ATTRIBUTE_NAME, this.header);
        for (Iterator iterator = fieldList.iterator(); iterator.hasNext();) {
            QueryField queryField = (QueryField) iterator.next();
            Element elem = new Element(QUERY_FIELD_ELEMENT_NAME);
            XMLHelper.addOptionalAttribute(elem, QUERY_FIELD_NAME_ATTRIBUTE_NAME, queryField.getName());
            XMLHelper.addOptionalAttribute(elem, QUERY_FIELD_SEPARATOR_ATTRIBUTE_NAME, queryField.getSeparator());
            XMLHelper.addOptionalAttribute(elem, QUERY_FIELD_FORMAT_ATTRIBUTE_NAME, queryField.getFormat());
            elem.setText(queryField.getQueryPart());
            retVal.addContent(elem);
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        this.name = elem.getAttributeValue(QUERY_FIELD_NAME_ATTRIBUTE_NAME);
        this.header = elem.getAttributeValue(QUERY_HEAD_ATTRIBUTE_NAME);
        for (Iterator iterator = elem.getChildren(QUERY_FIELD_ELEMENT_NAME).iterator(); iterator.hasNext();) {
            Element queryFieldElement = (Element) iterator.next();
            QueryField field = new QueryField(
                    queryFieldElement.getAttributeValue(QUERY_FIELD_NAME_ATTRIBUTE_NAME),
                    queryFieldElement.getAttributeValue(QUERY_FIELD_FORMAT_ATTRIBUTE_NAME),
                    queryFieldElement.getAttributeValue(QUERY_FIELD_SEPARATOR_ATTRIBUTE_NAME),
                    queryFieldElement.getText(),
                    "true".equals(queryFieldElement.getAttributeValue(QUERY_FIELD_IS_RELATIVE_ATTRIBUTE_NAME))
            );
            this.fieldList.add(field);
        }
    }

    public String getName() {
        return this.name;
    }

    public void insertQueryColumn(String columnName, String columnFormat,
                                  String separator, String queryPart, boolean isRelative) {
        QueryField field = new QueryField(columnName, columnFormat, separator, queryPart, isRelative);
        fieldList.add(field);
    }

    /**
     * Formats a row of a result set for this query.
     *
     * The input is a ResultSet which is supposed to point to an existing
     * row. Column one is supposed to be the first column of the query
     * definition and so on.
     *
     * The return value is a String which returns a formatted version of the
     * row
     */
    public String formatResults(Vector values, int startPosition) {
        String rowRes = new String();
        if (header != null) {
            rowRes += header;
        }
        Iterator colDefIt = this.fieldList.iterator();
        // skip key, start with 1
        int i = startPosition;
        while (colDefIt.hasNext()) {
            QueryField field = (QueryField) colDefIt.next();
            String value = values.get(i).toString();
            i++;
            if (field.getFormat() != null) {
                DecimalFormat format = new DecimalFormat(field.getFormat());
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
	 * @param whereClause       The SQL WHERE clause to query.
	 * @param values            The query results to turn into objects
	 * @param referenceValues   The reference values that can be used for relative results, usually the same values for the top node 
	 */
    abstract public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values, Vector referenceValues);
    
    abstract public boolean doesNeedReferenceValues();
}
