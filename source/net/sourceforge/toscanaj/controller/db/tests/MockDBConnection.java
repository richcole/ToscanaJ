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
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import org.tockit.events.EventBroker;

import com.mockobjects.sql.MockConnection;

public class MockDBConnection extends DatabaseConnection {
    public int queryNumberCallCounter = 0;

    public MockDBConnection() throws DatabaseException {
        super(new EventBroker(), new MockConnection());
    }
    public List executeQuery(List fields, String tableName, String whereClause)
        throws DatabaseException {
        return new ArrayList();
    }
    
    public int queryNumber(String statement, int column) throws DatabaseException {
    	this.queryNumberCallCounter ++;
        return 0;
    }
}