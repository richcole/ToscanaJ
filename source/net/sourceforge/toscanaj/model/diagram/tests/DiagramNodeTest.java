/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;

import java.awt.geom.Point2D;

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
        DiagramNode node = new DiagramNode("Id", position, new ConceptImplementation(),
                new LabelInfo(), new LabelInfo(), null);

        Element xmlDesc = node.toXML();

        try {
            DiagramNode other = new DiagramNode(xmlDesc);
            assertEquals(node, other);
        } catch (XMLSyntaxError error) {
            fail(error.toString());
        }

    }

    public void testEquals() {
        Point2D position = new Point2D.Double(0, 0);
        final String identifier = "Id";
        DiagramNode node = new DiagramNode(identifier, position, new ConceptImplementation(),
                new LabelInfo(), new LabelInfo(), null);


        DiagramNode node2 = new DiagramNode(identifier, position, new ConceptImplementation(),
                new LabelInfo(), new LabelInfo(), null);

        assertEquals(node, node2);

        assertEquals(false, node.equals(new Object()));
        assertEquals(false, node.equals(null));

        node.setPosition(new Point2D.Double(10, 0));
        assertEquals(false, node.equals(node2));

        node = new DiagramNode("Id2", position, new ConceptImplementation(),
                new LabelInfo(), new LabelInfo(), null);
        assertEquals(false, node.equals(node2));

        LabelInfo info = new LabelInfo();
        info.setOffset(new Point2D.Double(0, 10));

        node = new DiagramNode(identifier, position, new ConceptImplementation(), info, new LabelInfo(), null);
        assertEquals(false, node.equals(node2));

    }


}
