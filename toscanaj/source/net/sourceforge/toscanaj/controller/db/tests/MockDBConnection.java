/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db.tests;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

import org.tockit.events.EventBroker;

import com.mockobjects.sql.MockConnection;

public class MockDBConnection extends DatabaseConnection {
    public int queryNumberCallCounter = 0;

    public MockDBConnection() {
        super(new EventBroker(), new MockConnection());
    }
    
    @Override
	public List<String[]> executeQuery(List<String> fields, String tableName, String whereClause) {
        return new ArrayList<String[]>();
    }
    
    @Override
	public int queryInt(String statement, int column) {
    	this.queryNumberCallCounter ++;
        return 0;
    }
}