/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.tests;

import java.sql.Types;

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.tests.MockDBConnection;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DatabaseConnectedConceptInterpreterTest extends TestCase {
    final static Class THIS = DatabaseConnectedConceptInterpreterTest.class;

    public DatabaseConnectedConceptInterpreterTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testSizesCaches() {
        DatabaseConnectedConceptInterpreter interpreter;
        try {
            interpreter = initializeInterpreter();
        } catch (DatabaseException e) {
            throw new RuntimeException("couldn't initialise interpreter");
        }

        DiagramHistory history1 = new DiagramHistory();
        ConceptInterpretationContext context1 = new ConceptInterpretationContext(history1, new EventBroker());
        ConceptInterpretationContext context2 = new ConceptInterpretationContext(history1, new EventBroker());
        context2.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
        DiagramHistory history2 = new DiagramHistory();
        ConceptInterpretationContext context3 = new ConceptInterpretationContext(history2, new EventBroker());

        ConceptImplementation concept1 = new ConceptImplementation();
        concept1.addObject("dummy clause to get queries to be created");
        ConceptImplementation concept2 = new ConceptImplementation();
        concept2.addObject("dummy clause to get queries to be created");
        ConceptImplementation concept3 = new ConceptImplementation();
        concept3.addObject("dummy clause to get queries to be created");
        ConceptImplementation concept4 = new ConceptImplementation();
        concept4.addObject("dummy clause to get queries to be created");

        MockDBConnection dbConnection = (MockDBConnection) DatabaseConnection.getConnection();

        interpreter.getObjectCount(concept1, context1);
        checkAssertion(dbConnection.queryNumberCallCounter == 1);
        interpreter.getObjectCount(concept1, context2);
        checkAssertion(dbConnection.queryNumberCallCounter == 1);
        interpreter.getObjectCount(concept1, context3);
        checkAssertion(dbConnection.queryNumberCallCounter == 2);
        interpreter.getObjectCount(concept2, context1);
        checkAssertion(dbConnection.queryNumberCallCounter == 3);
        interpreter.getObjectCount(concept2, context3);
        checkAssertion(dbConnection.queryNumberCallCounter == 4);
        interpreter.getObjectCount(concept3, context2);
        checkAssertion(dbConnection.queryNumberCallCounter == 5);
        interpreter.getObjectCount(concept3, context3);
        checkAssertion(dbConnection.queryNumberCallCounter == 6);
        interpreter.getObjectCount(concept4, context1.createNestedContext(concept1));
        checkAssertion(dbConnection.queryNumberCallCounter == 7);

		history2.addDiagram(new SimpleLineDiagram());

		interpreter.getObjectCount(concept1, context1);
        checkAssertion(dbConnection.queryNumberCallCounter == 7);
        interpreter.getObjectCount(concept1, context2);
        checkAssertion(dbConnection.queryNumberCallCounter == 7);
        interpreter.getObjectCount(concept1, context3);
        checkAssertion(dbConnection.queryNumberCallCounter == 8);
        interpreter.getObjectCount(concept2, context1);
        checkAssertion(dbConnection.queryNumberCallCounter == 8);
        interpreter.getObjectCount(concept2, context3);
        checkAssertion(dbConnection.queryNumberCallCounter == 9);
        interpreter.getObjectCount(concept3, context2);
        checkAssertion(dbConnection.queryNumberCallCounter == 9);
        interpreter.getObjectCount(concept3, context3);
        checkAssertion(dbConnection.queryNumberCallCounter == 10);
        interpreter.getObjectCount(concept4, context1.createNestedContext(concept1));
        checkAssertion(dbConnection.queryNumberCallCounter == 10);
    }

    protected DatabaseConnectedConceptInterpreter initializeInterpreter()
        throws DatabaseException {
        DatabaseConnection databaseConnection = new MockDBConnection();
        DatabaseInfo dbInfo = new DatabaseInfo();
        Table testTable = new Table("testing", false);
        dbInfo.setTable(testTable);
        dbInfo.setKey(new Column("testing",Types.VARCHAR,testTable));
        DatabaseConnection.setConnection(databaseConnection);
        DatabaseConnectedConceptInterpreter interpreter = new DatabaseConnectedConceptInterpreter(dbInfo);
        return interpreter;
    }
    
    protected void checkAssertion(boolean assertion) {
    	if(!assertion) {
    		throw new RuntimeException("Assertion failed in test");
    	}
    }
}
