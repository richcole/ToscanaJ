/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Hashtable;


public class ManyValuedContextImplementation implements WritableManyValuedContext {
    private Set objects = new LinkedHashSet();
    private Set properties = new LinkedHashSet();
    private Hashtable relation = new Hashtable();
	private Set types = new LinkedHashSet();

    public ManyValuedContextImplementation() {
    }

    public void add(FCAObject object) {
        objects.add(object);
        relation.put(object, new Hashtable());
    }

    public Set getObjects() {
        return objects;
    }

    public void add(ManyValuedAttribute attribute) {
        properties.add(attribute);
    }

    public Set getAttributes() {
        return properties;
    }
    
    public Set getTypes(){
    	return types;
    }

    public void setRelationship(FCAObject object, ManyValuedAttribute attribute, AttributeValue value) {
        Hashtable row = (Hashtable) relation.get(object);
        row.put(attribute, value);
    }

    public AttributeValue getRelationship(FCAObject object, ManyValuedAttribute attribute) {
        Hashtable row = (Hashtable) relation.get(object);
        return (AttributeValue) row.get(attribute);
    }	
}
