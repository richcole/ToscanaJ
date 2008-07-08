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
    private final Set<N> outboundNodes = new HashSet<N>();
    private final Set<N> inboundNodes = new HashSet<N>();

    protected void addOutboundNode(final N node) {
        outboundNodes.add(node);
    }

    protected void addInboundNode(final N node) {
        inboundNodes.add(node);
    }

    protected void removeOutboundNode(final N node) {
        outboundNodes.remove(node);
    }

    protected void removeInboundNode(final N node) {
        inboundNodes.remove(node);
    }

    @SuppressWarnings("unchecked")
    public void connectTo(final N node) {
        this.addOutboundNode(node);
        node.addInboundNode((N) this);
    }

    @SuppressWarnings("unchecked")
    public void disconnectFrom(final N node) {
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
