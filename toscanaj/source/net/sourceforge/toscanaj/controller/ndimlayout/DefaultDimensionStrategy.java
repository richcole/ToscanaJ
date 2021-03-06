/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;

/**
 * This dimension strategy ensure the usual type of attribute-additivity.
 * 
 * One attribute of each meet-irreducible concept gets assigned a dimension,
 * which results in an attribute-additive line diagram based on attributes of
 * meet-irreducibles. This is the usual layout used e.g. by Anaconda, except
 * that the manipulation always works in a controlled way: moving the
 * meet-irreducible elements moves the ideals (as with the ideal manipulator in
 * our tools or Anaconda), while moving other nodes splits the movement onto the
 * dimensions involved (I think that can be expressed as moving the ideals of
 * all meet-irreducibles in the interval between the concept and the join of all
 * of its parents -- alternatively just think of it in the same way all the
 * other n-dim stuff works).
 */
public class DefaultDimensionStrategy<T> implements
        DimensionCreationStrategy<T> {
    public <O> List<Dimension<T>> calculateDimensions(
            final Lattice<O, T> lattice) {
        final List<Dimension<T>> dimensions = new ArrayList<Dimension<T>>();
        final Concept<O, T>[] concepts = lattice.getConcepts();
        for (final Concept<O, T> concept : concepts) {
            if (concept.isMeetIrreducible()) {
                final Iterator<T> attrContIt = concept
                        .getAttributeContingentIterator();
                final List<T> attrCont = new ArrayList<T>();
                attrCont.add(attrContIt.next());
                dimensions.add(new Dimension<T>(attrCont));
            }
        }
        return dimensions;
    }
}
