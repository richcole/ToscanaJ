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

public class DistributionQuery extends Query {
    private DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "distributionQuery";

    public static final DistributionQuery PERCENT_QUERY = new DistributionQuery(null, "Percent", "fake");

    public DistributionQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public DistributionQuery(DatabaseInfo info, Element element) throws XMLSyntaxError {
        super(element);
        this.info = info;
    }

    protected String getElementName() {
        return QUERY_ELEMENT_NAME;
    }

    public String getQueryHead() {
        // this gives an additional column replacing the key (used only in lists)
        String retValue = "SELECT count(*),";
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

	/**
	 * This method is not yet implemented since we don't support general distribution queries 
	 * yet. The class exists solely to define a consistent frame for the percent query, adding
	 * proper support for distribution queries is more tricky: we will need to somehow query for
	 * the same values on the top element and the current element, which requires knowledge of
	 * the top. Do we want to do this? Do we want to keep the percent version like this?
	 * 
	 * @todo implement/change/whatever 
	 */
    public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values) {
    	throw new RuntimeException("Not yet implemented");
    }
}
