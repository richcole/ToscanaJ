/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.directedgraph.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.directedgraph.Node;

public class DirectedGraphTest extends TestCase {
    final static Class THIS = DirectedGraphTest.class;

    public DirectedGraphTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testGraph() {
        DirectedGraph graph = new DirectedGraph();
        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();
        Node node4 = new Node();
        Node node5 = new Node();
        Node node6 = new Node();
        Node node7 = new Node();
        Node node8 = new Node();
        Node node9 = new Node();
        Node node10 = new Node();

        node1.connectTo(node3);
        node2.connectTo(node3);
        node3.connectTo(node4);
        node3.connectTo(node6);
        node5.connectTo(node6);
        node6.connectTo(node7);
        node6.connectTo(node8);
        node6.connectTo(node9);
        node8.connectTo(node10);

        graph.addNode(node1);

        assertEquals(10, graph.getNodes().size());
        assertEquals(3, graph.getSources().size());
        assertEquals(4, graph.getSinks().size());
        assertEquals(11, graph.getMaximalPaths().size());
    }
}