/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import java.util.HashSet;
import java.util.Set;

public class LatticeImplementation<O, A> implements Lattice<O, A> {
    private final Set<Concept<O, A>> concepts = new HashSet<Concept<O, A>>();

    public LatticeImplementation() {
        // nothing to do here
    }

    public void addConcept(final Concept<O, A> concept) {
        concepts.add(concept);
    }

    public void removeConcept(final Concept<O, A> concept) {
        concepts.remove(concept);
    }

    @SuppressWarnings("unchecked")
    public Concept<O, A>[] getConcepts() {
        final Concept<O, A>[] retVal = new Concept[concepts.size()];
        concepts.toArray(retVal);
        return retVal;
    }

    public Concept<O, A> getTop() {
        for (final Concept<O, A> concept : concepts) {
            if (concept.isTop()) {
                return concept;
            }
        }
        return null;
    }

    public Concept<O, A> getBottom() {
        for (final Concept<O, A> concept : concepts) {
            if (concept.isBottom()) {
                return concept;
            }
        }
        return null;
    }
}
