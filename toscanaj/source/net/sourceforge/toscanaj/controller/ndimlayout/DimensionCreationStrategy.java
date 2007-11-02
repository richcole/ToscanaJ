/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.util.List;

import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;

/**
 * Models a strategy to map attributes into conceptual dimension.
 * 
 * A conceptual dimension is a set of attributes that belong together, such
 * as all single-valued attributes from an ordinal scale. This information
 * can then be used to layout the diagram in a way that an attribute-additive
 * diagram uses the same vector for each attribute of a dimension.
 * 
 * Note that this class does not consider the objects as relevant, the matching
 * type parameter is completely free.
 * 
 * @param <A> The type of attributes to consider, which allows specific strategies
 *            to operate only on certain attributes.
 */
public interface DimensionCreationStrategy<A> {
    <O> List<Dimension<A>> calculateDimensions(Lattice<O,A> lattice);
}
