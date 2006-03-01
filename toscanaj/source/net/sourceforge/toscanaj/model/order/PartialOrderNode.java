/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.order;

import net.sourceforge.toscanaj.model.directedgraph.Node;

public class PartialOrderNode extends Node {
    private Ordered data;

    public PartialOrderNode(Ordered data) {
        this.data = data;
    }

    public Ordered getData() {
        return data;
    }
}
