/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.directedgraph;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private Set outboundNodes = new HashSet();
    private Set inboundNodes = new HashSet();

    protected void addOutboundNode(Node node) {
        outboundNodes.add(node);
    }

    protected void addInboundNode(Node node) {
        inboundNodes.add(node);
    }

    protected void removeOutboundNode(Node node) {
        outboundNodes.remove(node);
    }

    protected void removeInboundNode(Node node) {
        inboundNodes.remove(node);
    }

    public void connectTo(Node node) {
        this.addOutboundNode(node);
        node.addInboundNode(this);
    }

    public void disconnectFrom(Node node) {
        this.removeOutboundNode(node);
        node.removeInboundNode(this);
    }

    public Set getOutboundNodes() {
        return outboundNodes;
    }

    public Set getInboundNodes() {
        return inboundNodes;
    }
}
