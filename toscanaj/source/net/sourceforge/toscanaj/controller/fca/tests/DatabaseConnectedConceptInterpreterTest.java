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
import net.sourceforge.toscanaj.controller.db.tests.MockDBConnection;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DatabaseConnectedConceptInterpreterTest extends TestCase {
	final static Class<DatabaseConnectedConceptInterpreterTest> THIS = DatabaseConnectedConceptInterpreterTest.class;

	public DatabaseConnectedConceptInterpreterTest(String s) {
		super(s);
	}

	public static Test suite() {
		return new TestSuite(THIS);
	}

	public void testSizesCaches() {
		DatabaseConnectedConceptInterpreter<FCAElementImplementation, FCAElementImplementation> interpreter;
		interpreter = initializeInterpreter();

		DiagramHistory history1 = new DiagramHistory();
		ConceptInterpretationContext<FCAElementImplementation, FCAElementImplementation> context1 = new ConceptInterpretationContext<FCAElementImplementation, FCAElementImplementation>(
				history1, new EventBroker());
		ConceptInterpretationContext<FCAElementImplementation, FCAElementImplementation> context2 = new ConceptInterpretationContext<FCAElementImplementation, FCAElementImplementation>(
				history1, new EventBroker());
		context2.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
		DiagramHistory history2 = new DiagramHistory();
		ConceptInterpretationContext<FCAElementImplementation, FCAElementImplementation> context3 = new ConceptInterpretationContext<FCAElementImplementation, FCAElementImplementation>(
				history2, new EventBroker());

		ConceptImplementation<FCAElementImplementation, FCAElementImplementation> concept1 = new ConceptImplementation<FCAElementImplementation, FCAElementImplementation>();
		concept1.addObject(new FCAElementImplementation(
				"dummy clause to get queries to be created"));
		ConceptImplementation<FCAElementImplementation, FCAElementImplementation> concept2 = new ConceptImplementation<FCAElementImplementation, FCAElementImplementation>();
		concept2.addObject(new FCAElementImplementation(
				"dummy clause to get queries to be created"));
		ConceptImplementation<FCAElementImplementation, FCAElementImplementation> concept3 = new ConceptImplementation<FCAElementImplementation, FCAElementImplementation>();
		concept3.addObject(new FCAElementImplementation(
				"dummy clause to get queries to be created"));
		ConceptImplementation<FCAElementImplementation, FCAElementImplementation> concept4 = new ConceptImplementation<FCAElementImplementation, FCAElementImplementation>();
		concept4.addObject(new FCAElementImplementation(
				"dummy clause to get queries to be created"));

		MockDBConnection dbConnection = (MockDBConnection) DatabaseConnection
				.getConnection();

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
		interpreter.getObjectCount(concept4, context1
				.createNestedContext(concept1));
		checkAssertion(dbConnection.queryNumberCallCounter == 7);

		history2
				.addDiagram(new SimpleLineDiagram<FCAElementImplementation, FCAElementImplementation>());

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
		interpreter.getObjectCount(concept4, context1
				.createNestedContext(concept1));
		checkAssertion(dbConnection.queryNumberCallCounter == 10);
	}

	protected DatabaseConnectedConceptInterpreter<FCAElementImplementation, FCAElementImplementation> initializeInterpreter() {
		DatabaseConnection databaseConnection = new MockDBConnection();
		DatabaseInfo dbInfo = new DatabaseInfo();
		Table testTable = new Table("testing", false);
		dbInfo.setTable(testTable);
		dbInfo.setKey(new Column("testing", Types.VARCHAR, testTable));
		DatabaseConnection.setConnection(databaseConnection);
		DatabaseConnectedConceptInterpreter<FCAElementImplementation, FCAElementImplementation> interpreter = new DatabaseConnectedConceptInterpreter<FCAElementImplementation, FCAElementImplementation>(
				dbInfo);
		return interpreter;
	}

	protected void checkAssertion(boolean assertion) {
		if (!assertion) {
			throw new RuntimeException("Assertion failed in test");
		}
	}
}
