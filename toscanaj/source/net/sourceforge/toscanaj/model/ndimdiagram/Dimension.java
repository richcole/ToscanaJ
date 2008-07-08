/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.ndimdiagram;

import java.util.List;

/**
 * A dimension of a conceptual structure.
 * 
 * A dimension is a set of attributes that form an implication chain in the
 * conceptual structure.
 * 
 * @param <R> The underlying type of the attributes.
 */
public class Dimension<R> {
    private List<R> attributes;

    public Dimension(List<R> attributes) {
        this.attributes = attributes;
    }

    public boolean contains(R attribute) {
        return attributes.contains(attribute);
    }
}
