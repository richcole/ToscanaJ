/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.List;
import java.util.ArrayList;

public class NumericalValueGroup implements ValueGroup {
    private NumericalType type;
    private String name;
    private double min;
    private boolean minIncluded;
    private double max;
    private boolean maxIncluded;

    public NumericalValueGroup(NumericalType type, String name,
                               double min, boolean minIncluded, double max, boolean maxIncluded) {
        this.type = type;
        this.name = name;
        this.min = min;
        this.minIncluded = minIncluded;
        this.max = max;
        this.maxIncluded = maxIncluded;
        type.addValueGroup(this, name);
    }

    public String getName() {
        return name;
    }

    public boolean containsValue(Value value) {
        if(!(value instanceof NumericalValue)) {
            return false;
        }
        NumericalValue numVal = (NumericalValue) value;
        double number = numVal.getValue();
        if(number < min) {
            return false;
        }
        if(number == min && !minIncluded) {
            return false;
        }
        if(number > max) {
            return false;
        }
        if(number == max && !maxIncluded) {
            return false;
        }
        return true;
    }

    public boolean isSuperSetOf(ValueGroup otherGroup) {
        if(! (otherGroup instanceof NumericalValueGroup)) {
            return false;
        }
        NumericalValueGroup otherNVGroup = (NumericalValueGroup) otherGroup;
        if(otherNVGroup.type != type) {
            return false;
        }
        if(otherNVGroup.min < min) {
            return false;
        }
        if(otherNVGroup.min == min && !minIncluded && otherNVGroup.minIncluded) {
            return false;
        }
        if(otherNVGroup.max > max) {
            return false;
        }
        if(otherNVGroup.max == max && !maxIncluded && otherNVGroup.maxIncluded) {
            return false;
        }
        return true;
    }
}
