/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import com.mockobjects.sql.MockConnection;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.ArrayList;
import java.util.List;

public class DatabaseConnectedConceptTest extends ConceptTest {
    public DatabaseConnectedConceptTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(DatabaseConnectedConceptTest.class);
    }

    static class MockDBConnection extends DatabaseConnection {
        public MockDBConnection() throws DatabaseException {
            super(new EventBroker(), new MockConnection());
        }

        public List executeQuery(List fields, String tableName, String whereClause) throws DatabaseException {
           return new ArrayList();
        }
    }

    protected DatabaseConnectedConcept makeConceptWithEmptyContingentAndExtent() {
        try {
            DatabaseConnection.setConnection(new MockDBConnection());
            DatabaseConnectedConcept databaseConnectedConcept = new DatabaseConnectedConcept();
            databaseConnectedConcept.setObjectClause("clause");
            //should be set, otherwise query will no be executed
            return databaseConnectedConcept;
        } catch (Exception e) {
            return null;
        }
    }
}
