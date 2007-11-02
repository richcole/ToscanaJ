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
    private Set<N> nodes = new HashSet<N>();

    public void addNode(N node) {
        nodes.add(node);
        for (Iterator<N> iterator = node.getInboundNodes().iterator(); iterator.hasNext();) {
            N curNode = iterator.next();
            if (!nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
        for (Iterator<N> iterator = node.getOutboundNodes().iterator(); iterator.hasNext();) {
            N curNode = iterator.next();
            if (!nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
    }

    public void removeNode(N node, boolean keepConnections) {
        nodes.remove(node);
        if (keepConnections) {
            Set<N> inboundNodes = node.getInboundNodes();
            Set<N> outboundNodes = node.getOutboundNodes();
            for (Iterator<N> iterator = inboundNodes.iterator(); iterator.hasNext();) {
                N fromNode = iterator.next();
                for (Iterator<N> iterator2 = outboundNodes.iterator(); iterator2.hasNext();) {
                    N toNode = iterator2.next();
                    fromNode.connectTo(toNode);
                }
            }
        }
    }

    public Set<N> getNodes() {
        return nodes;
    }

    public Set<N> getSources() {
        Set<N> retVal = new HashSet<N>();
        for (Iterator<N> iterator = nodes.iterator(); iterator.hasNext();) {
            N node = iterator.next();
            if (node.getInboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set<N> getSinks() {
        Set<N> retVal = new HashSet<N>();
        for (Iterator<N> iterator = nodes.iterator(); iterator.hasNext();) {
            N node = iterator.next();
            if (node.getOutboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set<Vector<N>> getMaximalPaths() {
        /** @todo this algorithm is pretty much brute force since it reiterates the whole set all over again in each
         turn, we could do better, but we don't care at the moment.
         @todo we assume an acyclic graph here, otherwise we will get stuck
         */
        Set<Vector<N>> paths = new HashSet<Vector<N>>();
        Set<N> sources = getSources();
        for (Iterator<N> iterator = sources.iterator(); iterator.hasNext();) {
            N source = iterator.next();
            Vector<N> path = new Vector<N>();
            path.add(source);
            paths.add(path);
        }
        calculateMaximalPaths(paths);
        return paths;
    }

    protected void calculateMaximalPaths(Set<Vector<N>> paths) {
        boolean changed = false;
        Set<Vector<N>> newPaths = new HashSet<Vector<N>>();
        for (Iterator<Vector<N>> iterator = paths.iterator(); iterator.hasNext();) {
            Vector<N> path = iterator.next();
            N lastNode = path.get(path.size() - 1);
            Set<N> outboundNodes = lastNode.getOutboundNodes();
            for (Iterator<N> iterator2 = outboundNodes.iterator(); iterator2.hasNext();) {
                N node = iterator2.next();
                if (iterator2.hasNext()) {
                    // copy path for all but last branch
                    Vector<N> newPath = new Vector<N>();
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
