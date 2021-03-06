/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.Concept;

/**
 * Models anything returning [0,1] for a concept in a certain context.
 * 
 * Typically an instance of a class implementing this interface will know about
 * a concept interpreter.
 * 
 * @param <O>
 *            The formal objects under consideration.
 * @param <A>
 *            The attributes under consideration.
 */
public interface NormedIntervalSource<O, A> {
    double getValue(Concept<O, A> concept,
            ConceptInterpretationContext<O, A> context);
}
