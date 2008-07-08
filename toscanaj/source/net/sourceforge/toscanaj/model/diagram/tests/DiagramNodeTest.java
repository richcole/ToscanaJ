/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram.tests;

import java.awt.geom.Point2D;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;

public class DiagramNodeTest extends TestCase {
    final static Class THIS = DiagramNodeTest.class;

    public DiagramNodeTest(final String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testReadWriteFromXML() {

        final Point2D position = new Point2D.Double(10, 10);
        final DiagramNode node = new DiagramNode(new SimpleLineDiagram(), "Id",
                position, new ConceptImplementation(), new LabelInfo(),
                new LabelInfo(), null);

        final Element xmlDesc = node.toXML();

        try {
            final DiagramNode other = new DiagramNode(new SimpleLineDiagram(),
                    xmlDesc);
            assertEquals(node, other);
        } catch (final XMLSyntaxError error) {
            fail(error.toString());
        }

    }

    public void testEquals() {
        final Point2D position = new Point2D.Double(0, 0);
        final String identifier = "Id";
        DiagramNode node = new DiagramNode(new SimpleLineDiagram(), identifier,
                position, new ConceptImplementation(), new LabelInfo(),
                new LabelInfo(), null);

        final DiagramNode node2 = new DiagramNode(new SimpleLineDiagram(),
                identifier, position, new ConceptImplementation(),
                new LabelInfo(), new LabelInfo(), null);

        assertEquals(node, node2);

        assertEquals(false, node.equals(new Object()));
        assertEquals(false, node.equals(null));

        node.setPosition(new Point2D.Double(10, 0));
        assertEquals(false, node.equals(node2));

        node = new DiagramNode(new SimpleLineDiagram(), "Id2", position,
                new ConceptImplementation(), new LabelInfo(), new LabelInfo(),
                null);
        assertEquals(false, node.equals(node2));

        final LabelInfo info = new LabelInfo();
        info.setOffset(new Point2D.Double(0, 10));

        node = new DiagramNode(new SimpleLineDiagram(), identifier, position,
                new ConceptImplementation(), info, new LabelInfo(), null);
        assertEquals(false, node.equals(node2));

    }

}
