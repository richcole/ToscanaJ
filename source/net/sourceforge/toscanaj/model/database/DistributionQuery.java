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

    public static final DistributionQuery PERCENT_QUERY = new DistributionQuery(null, "Distribution of Objects", "fake");

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

    public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values, Vector referenceValues) {
		if (values.get(0).toString().equals("0")) {
			return null;
		}
		///@todo this is all a bit brute force -> be smarter
		Vector relativeValues = new Vector(values.size());
		Iterator valIt = values.iterator();
		Iterator refIt = referenceValues.iterator();
		while(valIt.hasNext() && refIt.hasNext()) {
			String value = valIt.next().toString();
			String refVal = refIt.next().toString();
			relativeValues.add(new Double(Double.parseDouble(value) / Double.parseDouble(refVal)));
		}
		String displayString = this.formatResults(relativeValues, 1);
		DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(whereClause, displayString);
		return retVal;
    }
}
