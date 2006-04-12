/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.order.Ordered;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PartialOrderOperations {
    static public DirectedGraph createGraphFromOrder(Ordered[] order) {
        DirectedGraph graph = new DirectedGraph();
        for (int i = 0; i < order.length; i++) {
            Ordered item = order[i];
            PartialOrderNode node = new PartialOrderNode(item);
            Set nodes = graph.getNodes();
            Set largerNodes = new HashSet();
            Set smallerNodes = new HashSet();
            for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
                PartialOrderNode otherNode = (PartialOrderNode) iterator.next();
                Ordered otherItem = otherNode.getData();
                if (otherItem.isEqual(item)) {
                    for (Iterator iterator2 = otherNode.getInboundNodes().iterator(); iterator2.hasNext();) {
                        PartialOrderNode inbNode = (PartialOrderNode) iterator2.next();
                        inbNode.connectTo(node);
                    }
                    for (Iterator iterator2 = otherNode.getOutboundNodes().iterator(); iterator2.hasNext();) {
                        PartialOrderNode outbNode = (PartialOrderNode) iterator2.next();
                        node.connectTo(outbNode);
                    }
                    largerNodes.clear();
                    smallerNodes.clear();
                    break;
                } else if (item.isLesserThan(otherItem)) {
                    largerNodes.add(otherNode);
                } else if (otherItem.isLesserThan(item)) {
                    smallerNodes.add(otherNode);
                }
            }
            Set nonNeighbours = new HashSet();
            smallerLoop: for (Iterator iterator = smallerNodes.iterator(); iterator.hasNext();) {
                PartialOrderNode smallerNode = (PartialOrderNode) iterator.next();
                for (Iterator iterator2 = smallerNode.getInboundNodes().iterator(); iterator2.hasNext();) {
                    PartialOrderNode inbNode = (PartialOrderNode) iterator2.next();
                    if (smallerNodes.contains(inbNode)) {
                        nonNeighbours.add(smallerNode);
                        continue smallerLoop;
                    }
                }
            }
            smallerNodes.removeAll(nonNeighbours);
            nonNeighbours.clear();
            largerLoop: for (Iterator iterator = largerNodes.iterator(); iterator.hasNext();) {
                PartialOrderNode largerNode = (PartialOrderNode) iterator.next();
                for (Iterator iterator2 = largerNode.getOutboundNodes().iterator(); iterator2.hasNext();) {
                    PartialOrderNode outbNode = (PartialOrderNode) iterator2.next();
                    if (largerNodes.contains(outbNode)) {
                        nonNeighbours.add(largerNode);
                        continue largerLoop;
                    }
                }
            }
            largerNodes.removeAll(nonNeighbours);
            for (Iterator iterator = smallerNodes.iterator(); iterator.hasNext();) {
                PartialOrderNode lowerNeighbour = (PartialOrderNode) iterator.next();
                node.connectTo(lowerNeighbour);
                for (Iterator iterator2 = largerNodes.iterator(); iterator2.hasNext();) {
                    PartialOrderNode upperNeighbour = (PartialOrderNode) iterator2.next();
                    upperNeighbour.disconnectFrom(lowerNeighbour);
                }
            }
            for (Iterator iterator = largerNodes.iterator(); iterator.hasNext();) {
                PartialOrderNode upperNeighbour = (PartialOrderNode) iterator.next();
                upperNeighbour.connectTo(node);
            }
            graph.addNode(node);
        }
        return graph;
    }
}