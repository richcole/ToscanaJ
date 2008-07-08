/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.order.Ordered;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

public class PartialOrderOperations {
    static public <D extends Ordered<D>> DirectedGraph<PartialOrderNode<D>> createGraphFromOrder(
            final D[] order) {
        final DirectedGraph<PartialOrderNode<D>> graph = new DirectedGraph<PartialOrderNode<D>>();
        for (int i = 0; i < order.length; i++) {
            final D item = order[i];
            final PartialOrderNode<D> node = new PartialOrderNode<D>(item);
            final Set<PartialOrderNode<D>> nodes = graph.getNodes();
            final Set<PartialOrderNode<D>> largerNodes = new HashSet<PartialOrderNode<D>>();
            final Set<PartialOrderNode<D>> smallerNodes = new HashSet<PartialOrderNode<D>>();
            for (final PartialOrderNode<D> otherNode : nodes) {
                final D otherItem = otherNode.getData();
                if (otherItem.isEqual(item)) {
                    for (final PartialOrderNode<D> inbNode : otherNode
                            .getInboundNodes()) {
                        inbNode.connectTo(node);
                    }
                    for (final PartialOrderNode<D> outbNode : otherNode
                            .getOutboundNodes()) {
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
            final Set<PartialOrderNode<D>> nonNeighbours = new HashSet<PartialOrderNode<D>>();
            smallerLoop: for (final PartialOrderNode<D> smallerNode : smallerNodes) {
                for (final PartialOrderNode<D> inbNode : smallerNode
                        .getInboundNodes()) {
                    if (smallerNodes.contains(inbNode)) {
                        nonNeighbours.add(smallerNode);
                        continue smallerLoop;
                    }
                }
            }
            smallerNodes.removeAll(nonNeighbours);
            nonNeighbours.clear();
            largerLoop: for (final PartialOrderNode<D> largerNode : largerNodes) {
                for (final PartialOrderNode<D> outbNode : largerNode
                        .getOutboundNodes()) {
                    if (largerNodes.contains(outbNode)) {
                        nonNeighbours.add(largerNode);
                        continue largerLoop;
                    }
                }
            }
            largerNodes.removeAll(nonNeighbours);
            for (final PartialOrderNode<D> lowerNeighbour : smallerNodes) {
                node.connectTo(lowerNeighbour);
                for (final PartialOrderNode<D> upperNeighbour : largerNodes) {
                    upperNeighbour.disconnectFrom(lowerNeighbour);
                }
            }
            for (final PartialOrderNode<D> upperNeighbour : largerNodes) {
                upperNeighbour.connectTo(node);
            }
            graph.addNode(node);
        }
        return graph;
    }
}
