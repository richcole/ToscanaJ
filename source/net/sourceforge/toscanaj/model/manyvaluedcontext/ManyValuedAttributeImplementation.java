/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;


public class ManyValuedAttributeImplementation implements WritableManyValuedAttribute {
    private AttributeType type;
    private String name;

    public ManyValuedAttributeImplementation(AttributeType type, String name) {
        this.type = type;
        this.name = name;
    }

    public AttributeType getType() {
        return type;
    }
    
    public void setType(AttributeType type){
    	this.type = type;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
    	return getName();
    }

	public void setName(String name) {
		this.name = name;
	}
}