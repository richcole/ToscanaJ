/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;

public class Property implements ManyValuedAttribute {
    private AttributeType type;
    private String name;

    public Property(AttributeType type, String name) {
        this.type = type;
        this.name = name;
    }

    public AttributeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
    	return getName();
    }
}
