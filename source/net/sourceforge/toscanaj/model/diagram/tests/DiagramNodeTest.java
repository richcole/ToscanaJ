/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.lattice.DummyConcept;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;

import java.awt.geom.Point2D;

import org.jdom.Element;
import util.StringUtil;

public class DiagramNodeTest extends TestCase {
    final static Class THIS = DiagramNodeTest.class;

    public DiagramNodeTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testReadWriteFromXML() {

        Point2D position = new Point2D.Double(10, 10);
        DiagramNode node = new DiagramNode("Id", position,new DummyConcept(),
                new LabelInfo(), new LabelInfo(), null);

        Element xmlDesc = node.toXML();

        try {
            DiagramNode other = new DiagramNode(xmlDesc);
            assertEquals(node, other);
        } catch (XML_SyntaxError error) {
            fail(StringUtil.stackTraceToString(error));
        }

    }

    public void testEquals(){
        Point2D position = new Point2D.Double(0, 0);
        DiagramNode node = new DiagramNode("Id", position,new DummyConcept(),
                new LabelInfo(), new LabelInfo(), null);


        DiagramNode node2 = new DiagramNode("Id", position,new DummyConcept(),
                new LabelInfo(), new LabelInfo(), null);

        assertEquals(node, node2);

        assertEquals(false, node.equals(new Object()));
        assertEquals(false, node.equals(null));

        node.setPosition(new Point2D.Double(10, 0));
        assertEquals(false, node.equals(node2));

        node = new DiagramNode("Id2", position,new DummyConcept(),
                new LabelInfo(), new LabelInfo(), null);
        assertEquals(false, node.equals(node2));



    }


}
