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

public class AggregateQuery extends Query {
    private DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "aggregateQuery";

    public static final AggregateQuery COUNT_QUERY = new AggregateQuery(null, "Count", "fake");
	public static final AggregateQuery PERCENT_QUERY = new AggregateQuery(null, "Distribution of Objects", "fake");
	public static final AggregateQuery DEVIATION_QUERY = new AggregateQuery(null, "Deviation from Expected Values", "fake");

    public AggregateQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public AggregateQuery(DatabaseInfo info, Element element) throws XMLSyntaxError {
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
        Vector valuesToUse;
        if(this.doesNeedReferenceValues()) {
			///@todo this is all a bit brute force -> be smarter
			valuesToUse = new Vector(values.size());
			Iterator valIt = values.iterator();
			Iterator refIt = referenceValues.iterator();
			Iterator fieldIt = this.fieldList.iterator();

			// skip the first extra field, putting the value straight into the results
			valuesToUse.add(valIt.next());
			refIt.next();
			
			while(valIt.hasNext() && refIt.hasNext()) {
				String value = valIt.next().toString();
				String refVal = refIt.next().toString();
				QueryField field = (QueryField) fieldIt.next();
				if(field.isRelative()) {
					valuesToUse.add(new Double(Double.parseDouble(value) / Double.parseDouble(refVal)));
				} else {
					valuesToUse.add(value);
				}
			}
        } else {
        	valuesToUse = values;
        }
        String displayString = this.formatResults(valuesToUse, 1);
        DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(whereClause, displayString);
        return retVal;
    }

    public boolean doesNeedReferenceValues() {
    	for (Iterator iter = this.fieldList.iterator(); iter.hasNext();) {
            QueryField field = (QueryField) iter.next();
            if(field.isRelative()) {
            	return true;
            }
        }
        return false;
    }
}
