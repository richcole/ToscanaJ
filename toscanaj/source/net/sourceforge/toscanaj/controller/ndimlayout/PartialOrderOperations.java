/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.order.Ordered;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

public class PartialOrderOperations {
    static public<D extends Ordered<D>> DirectedGraph<PartialOrderNode<D>> createGraphFromOrder(D[] order) {
        DirectedGraph<PartialOrderNode<D>> graph = new DirectedGraph<PartialOrderNode<D>>();
        for (int i = 0; i < order.length; i++) {
            D item = order[i];
            PartialOrderNode<D> node = new PartialOrderNode<D>(item);
            Set<PartialOrderNode<D>> nodes = graph.getNodes();
            Set<PartialOrderNode<D>> largerNodes = new HashSet<PartialOrderNode<D>>();
            Set<PartialOrderNode<D>> smallerNodes = new HashSet<PartialOrderNode<D>>();
            for (Iterator<PartialOrderNode<D>> iterator = nodes.iterator(); iterator.hasNext();) {
            	PartialOrderNode<D> otherNode = iterator.next();
                D otherItem = otherNode.getData();
                if (otherItem.isEqual(item)) {
                    for (Iterator<PartialOrderNode<D>> iterator2 = otherNode.getInboundNodes().iterator(); iterator2.hasNext();) {
                    	PartialOrderNode<D> inbNode = iterator2.next();
                        inbNode.connectTo(node);
                    }
                    for (Iterator<PartialOrderNode<D>> iterator2 = otherNode.getOutboundNodes().iterator(); iterator2.hasNext();) {
                    	PartialOrderNode<D> outbNode = iterator2.next();
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
            Set<PartialOrderNode<D>> nonNeighbours = new HashSet<PartialOrderNode<D>>();
            smallerLoop: for (Iterator<PartialOrderNode<D>> iterator = smallerNodes.iterator(); iterator.hasNext();) {
            	PartialOrderNode<D> smallerNode = iterator.next();
                for (Iterator<PartialOrderNode<D>> iterator2 = smallerNode.getInboundNodes().iterator(); iterator2.hasNext();) {
                	PartialOrderNode<D> inbNode = iterator2.next();
                    if (smallerNodes.contains(inbNode)) {
                        nonNeighbours.add(smallerNode);
                        continue smallerLoop;
                    }
                }
            }
            smallerNodes.removeAll(nonNeighbours);
            nonNeighbours.clear();
            largerLoop: for (Iterator<PartialOrderNode<D>> iterator = largerNodes.iterator(); iterator.hasNext();) {
            	PartialOrderNode<D> largerNode = iterator.next();
                for (Iterator<PartialOrderNode<D>> iterator2 = largerNode.getOutboundNodes().iterator(); iterator2.hasNext();) {
                	PartialOrderNode<D> outbNode = iterator2.next();
                    if (largerNodes.contains(outbNode)) {
                        nonNeighbours.add(largerNode);
                        continue largerLoop;
                    }
                }
            }
            largerNodes.removeAll(nonNeighbours);
            for (Iterator<PartialOrderNode<D>> iterator = smallerNodes.iterator(); iterator.hasNext();) {
            	PartialOrderNode<D> lowerNeighbour = iterator.next();
                node.connectTo(lowerNeighbour);
                for (Iterator<PartialOrderNode<D>> iterator2 = largerNodes.iterator(); iterator2.hasNext();) {
                	PartialOrderNode<D> upperNeighbour = iterator2.next();
                    upperNeighbour.disconnectFrom(lowerNeighbour);
                }
            }
            for (Iterator<PartialOrderNode<D>> iterator = largerNodes.iterator(); iterator.hasNext();) {
            	PartialOrderNode<D> upperNeighbour = iterator.next();
                upperNeighbour.connectTo(node);
            }
            graph.addNode(node);
        }
        return graph;
    }
}
