/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import net.sourceforge.toscanaj.model.directedgraph.Node;
import net.sourceforge.toscanaj.model.cernato.ValueGroup;

public class PartialOrderNode extends Node {
    private ValueGroup valueGroup;

    public PartialOrderNode(ValueGroup valueGroup) {
        this.valueGroup = valueGroup;
    }

    public ValueGroup getValueGroup() {
        return valueGroup;
    }
}
