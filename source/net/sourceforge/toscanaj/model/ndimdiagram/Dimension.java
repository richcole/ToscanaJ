/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.ndimdiagram;

import net.sourceforge.toscanaj.model.context.Attribute;

import java.util.Vector;

public class Dimension {
    private Vector attributes;

    public Dimension(Vector attributes) {
        this.attributes = attributes;
    }

    public boolean contains(Attribute attribute) {
        return attributes.contains(attribute);
    }
}
