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

public class DirectedGraph {
    private Set<Node> nodes = new HashSet<Node>();

    public void addNode(Node node) {
        nodes.add(node);
        for (Iterator<Node> iterator = node.getInboundNodes().iterator(); iterator.hasNext();) {
            Node curNode = iterator.next();
            if (!nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
        for (Iterator<Node> iterator = node.getOutboundNodes().iterator(); iterator.hasNext();) {
            Node curNode = iterator.next();
            if (!nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
    }

    public void removeNode(Node node, boolean keepConnections) {
        nodes.remove(node);
        if (keepConnections) {
            Set<Node> inboundNodes = node.getInboundNodes();
            Set<Node> outboundNodes = node.getOutboundNodes();
            for (Iterator<Node> iterator = inboundNodes.iterator(); iterator.hasNext();) {
                Node fromNode = iterator.next();
                for (Iterator<Node> iterator2 = outboundNodes.iterator(); iterator2.hasNext();) {
                    Node toNode = iterator2.next();
                    fromNode.connectTo(toNode);
                }
            }
        }
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Node> getSources() {
        Set<Node> retVal = new HashSet<Node>();
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
            Node node = iterator.next();
            if (node.getInboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set<Node> getSinks() {
        Set<Node> retVal = new HashSet<Node>();
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext();) {
            Node node = iterator.next();
            if (node.getOutboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set<Vector<Node>> getMaximalPaths() {
        /** @todo this algorithm is pretty much brute force since it reiterates the whole set all over again in each
         turn, we could do better, but we don't care at the moment.
         @todo we assume an acyclic graph here, otherwise we will get stuck
         */
        Set<Vector<Node>> paths = new HashSet<Vector<Node>>();
        Set<Node> sources = getSources();
        for (Iterator<Node> iterator = sources.iterator(); iterator.hasNext();) {
            Node source = iterator.next();
            Vector<Node> path = new Vector<Node>();
            path.add(source);
            paths.add(path);
        }
        calculateMaximalPaths(paths);
        return paths;
    }

    protected void calculateMaximalPaths(Set<Vector<Node>> paths) {
        boolean changed = false;
        Set<Vector<Node>> newPaths = new HashSet<Vector<Node>>();
        for (Iterator<Vector<Node>> iterator = paths.iterator(); iterator.hasNext();) {
            Vector<Node> path = iterator.next();
            Node lastNode = path.get(path.size() - 1);
            Set<Node> outboundNodes = lastNode.getOutboundNodes();
            for (Iterator<Node> iterator2 = outboundNodes.iterator(); iterator2.hasNext();) {
                Node node = iterator2.next();
                if (iterator2.hasNext()) {
                    // copy path for all but last branch
                    Vector<Node> newPath = new Vector<Node>();
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
