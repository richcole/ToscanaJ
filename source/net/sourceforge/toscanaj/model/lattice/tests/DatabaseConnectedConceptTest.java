/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import com.mockobjects.sql.MockConnection;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;
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

    static class MockDBConnection extends DBConnection {
        public MockDBConnection() throws DatabaseException {
            super(new MockConnection());
        }

        public List executeQuery(DatabaseInfo.DatabaseQuery query, String whereClause) throws DatabaseException {
            List list = new ArrayList();
            return list;
        }
    }

    protected Concept makeConceptWithEmptyContingentAndExtent() {
        try {
            DatabaseConnectedConcept databaseConnectedConcept = new DatabaseConnectedConcept(new DatabaseInfo(),
                    new MockDBConnection());
            databaseConnectedConcept.setObjectClause("clause");
            //should be set, otherwise query will no be executed
            return databaseConnectedConcept;
        } catch (Exception e) {
            return null;
        }
    }
}
