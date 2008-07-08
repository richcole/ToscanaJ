/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.Concept;

public class FixedValueIntervalSource<O, A> implements
        NormedIntervalSource<O, A> {
    private final double value;

    public FixedValueIntervalSource(final double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException("Value must be in [0,1]");
        }
        this.value = value;
    }

    public double getValue(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        return this.value;
    }

}
