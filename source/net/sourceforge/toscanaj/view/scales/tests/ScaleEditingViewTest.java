/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.view.scales.ScaleEditingView;

public class ScaleEditingViewTest extends TestCase {
    public ScaleEditingViewTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(ScaleEditingViewTest.class);
    }

    public void testScaleListUpdateOnChangeOfConceptualScheme(){
        final EventBroker broker = new EventBroker();
        ConceptualSchema schema = new ConceptualSchema(broker);
        ScaleEditingView view = new ScaleEditingView(null, schema, broker);
        assertEquals(0, view.getScalesListModel().getSize());
        final SimpleLineDiagram diagram = new SimpleLineDiagram();
        diagram.setTitle("One");
        schema.addDiagram(diagram);
        assertEquals(1, view.getScalesListModel().getSize());


    }
}
