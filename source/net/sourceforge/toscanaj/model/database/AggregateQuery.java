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

import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

public class AggregateQuery extends Query {
    private DatabaseInfo info;
    public static final String QUERY_ELEMENT_NAME = "aggregateQuery";

    public static final AggregateQuery CountQuery = new AggregateQuery(null, "Count", "fake");

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
        retValue += " FROM " + info.getTableName() + " ";
        return retValue;
    }

    public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values) {
        if (values.get(0).toString().equals("0")) {
            return null;
        }
        String displayString = this.formatResults(values, 1);
        DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(whereClause, displayString);
        return retVal;
    }
}