/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;

public class CernatoTable implements ManyValuedContext {
    private List objects = new ArrayList();
    private List properties = new ArrayList();
    private Hashtable relation = new Hashtable();

    public CernatoTable() {
    }

    public void add(FCAObject object) {
        objects.add(object);
        relation.put(object, new Hashtable());
    }

    public Collection getObjects() {
        return objects;
    }

    public void add(ManyValuedAttribute attribute) {
        properties.add(attribute);
    }

    public Collection getAttributes() {
        return properties;
    }

    public void setRelationship(FCAObject object, ManyValuedAttribute attribute, AttributeValue value) {
        Hashtable row = (Hashtable) relation.get(object);
        row.put(attribute, value);
    }

    public AttributeValue getRelationship(FCAObject object, ManyValuedAttribute attribute) {
        Hashtable row = (Hashtable) relation.get(object);
        return (AttributeValue) row.get(attribute);
    }	
    
	public void updateObject(String objName, int index){
		CernatoObject obj = (CernatoObject) objects.get(index);
		obj.setName(objName);
		objects.set(index,obj);
	}
}
