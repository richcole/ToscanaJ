/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;

public class NumericalValue implements AttributeValue {
    private double value;

    public NumericalValue(double value) {
        this.value = value;
    }

    public String getDisplayString() {
        return String.valueOf(value);
    }

    public double getValue() {
        return value;
    }
}
