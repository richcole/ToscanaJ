/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales.tests;

import junit.framework.*;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.view.scales.*;

public class ScaleGeneratorPaneTest extends TestCase {
    public ScaleGeneratorPaneTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(ScaleGeneratorPaneTest.class);
    }

    public void testUpdateOfConceptualSchema() {
        final EventBroker broker = new EventBroker();
        ConceptualSchema schema = new ConceptualSchema(broker);
        DatabaseConnection databaseConnection = new DatabaseConnection(broker);
        ScaleGeneratorPanel view =
                new ScaleGeneratorPanel(null, schema,
                        new MockTableColumnPairsSelectionSource(),
                        databaseConnection,
                        broker);
        assertSame(schema, view.getConceptualSchema());
        ConceptualSchema otherSchema = new ConceptualSchema(broker);
        broker.processEvent(new NewConceptualSchemaEvent(this, otherSchema));
        assertSame(otherSchema, view.getConceptualSchema());

    }

    private class MockTableColumnPairsSelectionSource implements TableColumnPairsSelectionSource {
        public TableColumnPair[] getSelectedTableColumnPairs() {
            return new TableColumnPair[0];
        }
    }
}
