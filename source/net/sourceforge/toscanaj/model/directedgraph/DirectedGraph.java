/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.directedgraph;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class DirectedGraph {
    private Set nodes = new HashSet();

    public void addNode(Node node) {
        nodes.add(node);
        for (Iterator iterator = node.getInboundNodes().iterator(); iterator.hasNext();) {
            Node curNode = (Node) iterator.next();
            if(! nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
        for (Iterator iterator = node.getOutboundNodes().iterator(); iterator.hasNext();) {
            Node curNode = (Node) iterator.next();
            if(! nodes.contains(curNode)) {
                addNode(curNode);
            }
        }
    }

    public void removeNode(Node node, boolean keepConnections) {
        nodes.remove(node);
        if(keepConnections) {
            Set inboundNodes = node.getInboundNodes();
            Set outboundNodes = node.getOutboundNodes();
            for (Iterator iterator = inboundNodes.iterator(); iterator.hasNext();) {
                Node fromNode = (Node) iterator.next();
                for (Iterator iterator2 = outboundNodes.iterator(); iterator2.hasNext();) {
                    Node toNode = (Node) iterator2.next();
                    fromNode.connectTo(toNode);
                }
            }
        }
    }

    public Set getNodes() {
        return nodes;
    }

    public Set getSources() {
        Set retVal = new HashSet();
        for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
            Node node = (Node) iterator.next();
            if(node.getInboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set getSinks() {
        Set retVal = new HashSet();
        for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
            Node node = (Node) iterator.next();
            if(node.getOutboundNodes().isEmpty()) {
                retVal.add(node);
            }
        }
        return retVal;
    }

    public Set getMaximalPaths() {
        /** @todo this algorithm is pretty much brute force since it reiterates the whole set all over again in each
                  turn, we could do better, but we don't care at the moment.
            @todo we assume an acyclic graph here, otherwise we will get stuck
         */
        Set paths = new HashSet();
        Set sources = getSources();
        for (Iterator iterator = sources.iterator(); iterator.hasNext();) {
            Node source = (Node) iterator.next();
            Vector path = new Vector();
            path.add(source);
            paths.add(path);
        }
        calculateMaximalPaths(paths);
        return paths;
    }

    protected void calculateMaximalPaths(Set paths) {
        boolean changed = false;
        Set newPaths = new HashSet();
        for (Iterator iterator = paths.iterator(); iterator.hasNext();) {
            Vector path = (Vector) iterator.next();
            Node lastNode = (Node) path.get(path.size()-1);
            Set outboundNodes = lastNode.getOutboundNodes();
            for (Iterator iterator2 = outboundNodes.iterator(); iterator2.hasNext();) {
                Node node = (Node) iterator2.next();
                if(iterator2.hasNext()) {
                    // copy path for all but last branch
                    Vector newPath = new Vector();
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
        if(changed) {
            calculateMaximalPaths(paths);
        }
    }
}
