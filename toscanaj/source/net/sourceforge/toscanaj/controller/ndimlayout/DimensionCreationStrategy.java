/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;

import java.util.Vector;

public interface DimensionCreationStrategy {
    <T> Vector<Dimension<T>> calculateDimensions(Lattice<?,T> lattice);
}
