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

public class Node<N extends Node<N>> {
    private Set<N> outboundNodes = new HashSet<N>();
    private Set<N> inboundNodes = new HashSet<N>();

    protected void addOutboundNode(N node) {
        outboundNodes.add(node);
    }

    protected void addInboundNode(N node) {
        inboundNodes.add(node);
    }

    protected void removeOutboundNode(N node) {
        outboundNodes.remove(node);
    }

    protected void removeInboundNode(N node) {
        inboundNodes.remove(node);
    }

    @SuppressWarnings("unchecked")
	public void connectTo(N node) {
        this.addOutboundNode(node);
        node.addInboundNode((N) this);
    }

    @SuppressWarnings("unchecked")
	public void disconnectFrom(N node) {
        this.removeOutboundNode(node);
        node.removeInboundNode((N) this);
    }

    public Set<N> getOutboundNodes() {
        return outboundNodes;
    }

    public Set<N> getInboundNodes() {
        return inboundNodes;
    }
}
