/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.tests.*;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

public class DatabaseConnectedConceptTest extends ConceptTest {
    public DatabaseConnectedConceptTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(DatabaseConnectedConceptTest.class);
    }

    protected ConceptImplementation makeConceptWithEmptyContingentAndExtent() {
        try {
            DatabaseConnection.setConnection(new MockDBConnection());
            ConceptImplementation databaseConnectedConcept = new ConceptImplementation();
            databaseConnectedConcept.addObject(new FCAElementImplementation("clause"));
            //should be set, otherwise query will no be executed
            return databaseConnectedConcept;
        } catch (Exception e) {
            return null;
        }
    }
}
