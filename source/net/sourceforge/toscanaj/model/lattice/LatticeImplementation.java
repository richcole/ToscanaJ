/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LatticeImplementation implements Lattice {
    private Set concepts = new HashSet();

    public LatticeImplementation() {
    }

    public void addConcept(Concept concept) {
        concepts.add(concept);
    }

    public void removeConcept(Concept concept) {
        concepts.remove(concept);
    }

    public Concept[] getConcepts() {
        Concept[] retVal = new Concept[concepts.size()];
        concepts.toArray(retVal);
        return retVal;
    }

    public Concept getTop() {
        for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
            Concept concept = (Concept) iterator.next();
            if (concept.isTop()) {
                return concept;
            }
        }
        return null;
    }

    public Concept getBottom() {
        for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
            Concept concept = (Concept) iterator.next();
            if (concept.isBottom()) {
                return concept;
            }
        }
        return null;
    }
}
