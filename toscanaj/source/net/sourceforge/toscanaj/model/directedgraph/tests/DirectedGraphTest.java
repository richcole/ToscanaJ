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
    final static Class<DirectedGraphTest> THIS = DirectedGraphTest.class;
    
    private static class TestNode extends Node<TestNode> {
    	// nothing to do but binding type parameter
    }

    public DirectedGraphTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testGraph() {
        DirectedGraph<TestNode> graph = new DirectedGraph<TestNode>();
        TestNode node1 = new TestNode();
        TestNode node2 = new TestNode();
        TestNode node3 = new TestNode();
        TestNode node4 = new TestNode();
        TestNode node5 = new TestNode();
        TestNode node6 = new TestNode();
        TestNode node7 = new TestNode();
        TestNode node8 = new TestNode();
        TestNode node9 = new TestNode();
        TestNode node10 = new TestNode();

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