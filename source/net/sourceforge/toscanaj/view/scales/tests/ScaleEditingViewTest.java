/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.view.scales.ScaleEditingView;
import org.tockit.events.EventBroker;

public class ScaleEditingViewTest extends TestCase {
    public ScaleEditingViewTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(ScaleEditingViewTest.class);
    }

    public void testScaleListUpdateOnChangeOfConceptualScheme() {
        final EventBroker broker = new EventBroker();
        ConceptualSchema schema = new ConceptualSchema(broker);
        ScaleEditingView view = new ScaleEditingView(
                null,
                schema,
                broker,
                new DatabaseConnection(broker)
        );
        assertEquals(0, view.getScalesListModel().getSize());
        final SimpleLineDiagram diagram = new SimpleLineDiagram();
        diagram.setTitle("One");
        schema.addDiagram(diagram);
        assertEquals(1, view.getScalesListModel().getSize());


    }
}
