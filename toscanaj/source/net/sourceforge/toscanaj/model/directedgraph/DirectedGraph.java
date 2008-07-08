/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.directedgraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class DirectedGraph<N extends Node<N>> {
    private final Set<N> nodes = new HashSet<N>();

    public void addNode(final N node) {
        nodes.add(node);
        for (final N curNode : node.getInboundNodes()) {
            if (!nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
        for (final N curNode : node.getOutboundNodes()) {
            if (!nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
    }

    public void removeNode(final N node, final boolean keepConnections) {
        nodes.remove(node);
        if (keepConnections) {
            final Set<N> inboundNodes = node.getInboundNodes();
            final Set<N> outboundNodes = node.getOutboundNodes();
            for (final N fromNode : inboundNodes) {
                for (final N toNode : outboundNodes) {
                    fromNode.connectTo(toNode);
                }
            }
        }
    }

    public Set<N> getNodes() {
        return nodes;
    }

    public Set<N> getSources() {
        final Set<N> retVal = new HashSet<N>();
        for (final N node : nodes) {
            if (node.getInboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set<N> getSinks() {
        final Set<N> retVal = new HashSet<N>();
        for (final N node : nodes) {
            if (node.getOutboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set<Vector<N>> getMaximalPaths() {
        /**
         * @todo this algorithm is pretty much brute force since it reiterates
         *       the whole set all over again in each turn, we could do better,
         *       but we don't care at the moment.
         * @todo we assume an acyclic graph here, otherwise we will get stuck
         */
        final Set<Vector<N>> paths = new HashSet<Vector<N>>();
        final Set<N> sources = getSources();
        for (final N source : sources) {
            final Vector<N> path = new Vector<N>();
            path.add(source);
            paths.add(path);
        }
        calculateMaximalPaths(paths);
        return paths;
    }

    protected void calculateMaximalPaths(final Set<Vector<N>> paths) {
        boolean changed = false;
        final Set<Vector<N>> newPaths = new HashSet<Vector<N>>();
        for (final Vector<N> path : paths) {
            final N lastNode = path.get(path.size() - 1);
            final Set<N> outboundNodes = lastNode.getOutboundNodes();
            for (final Iterator<N> iterator2 = outboundNodes.iterator(); iterator2
                    .hasNext();) {
                final N node = iterator2.next();
                if (iterator2.hasNext()) {
                    // copy path for all but last branch
                    final Vector<N> newPath = new Vector<N>();
                    newPath.addAll(path);
                    newPath.add(node);
                    newPaths.add(newPath);
                } else {
                    path.add(node);
                }
                changed = true;
            }
        }
        paths.addAll(newPaths);
        if (changed) {
            calculateMaximalPaths(paths);
        }
    }
}
