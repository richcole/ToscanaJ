/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import net.sourceforge.toscanaj.model.order.Ordered;

public class Criterion implements Ordered<Criterion> {
    private ManyValuedAttribute property;
    private ScaleColumn valueGroup;

    public Criterion(ManyValuedAttribute property, ScaleColumn valueGroup) {
        this.valueGroup = valueGroup;
        this.property = property;
    }

    public ManyValuedAttribute getProperty() {
        return property;
    }

    public ScaleColumn getValueGroup() {
        return valueGroup;
    }

    public String getDisplayString() {
        return property.getName() + ":" + valueGroup.getName();
    }

    @Override
	public String toString() {
        return getDisplayString();
    }

    public boolean isLesserThan(Criterion other) {
        return (this.property == other.property) && this.valueGroup.isLesserThan(other.valueGroup);
    }

    public boolean isEqual(Criterion other) {
        return (this.property == other.property) && this.valueGroup.isEqual(other.valueGroup);
    }
}
