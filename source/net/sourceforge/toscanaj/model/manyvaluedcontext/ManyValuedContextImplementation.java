/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;

import net.sourceforge.toscanaj.model.context.*;


public class ManyValuedContextImplementation implements WritableManyValuedContext {
    private ListSet objects = new ListSetImplementation();
    private ListSet properties = new ListSetImplementation();
    private Hashtable relation = new Hashtable();
	private ListSet types = new ListSetImplementation();

    public ManyValuedContextImplementation() {
    }

    public void add(FCAObject object) {
        this.objects.add(object);
        this.relation.put(object, new Hashtable());
    }

    public void remove(FCAObject object) {
        this.objects.remove(object);
        this.relation.remove(object);
    }

    public ListSet getObjects() {
        return ListSetImplementation.unmodifiableListSet(this.objects);
    }

    public void add(ManyValuedAttribute attribute) {
        this.properties.add(attribute);
    }

    public void remove(ManyValuedAttribute attribute) {
        this.properties.remove(attribute);
    }

    public ListSet getAttributes() {
        return ListSetImplementation.unmodifiableListSet(this.properties);
    }
    
    public void add(AttributeType type) {
        this.types.add(type);
    }

    public void remove(AttributeType type) {
        this.types.remove(type);
    }

    public ListSet getTypes(){
    	return ListSetImplementation.unmodifiableListSet(this.types);
    }

    public void setRelationship(FCAObject object, ManyValuedAttribute attribute, AttributeValue value) {
        Hashtable row = (Hashtable) this.relation.get(object);
        row.put(attribute, value);
    }

    public AttributeValue getRelationship(FCAObject object, ManyValuedAttribute attribute) {
        Hashtable row = (Hashtable) this.relation.get(object);
        return (AttributeValue) row.get(attribute);
    }
    
    public void update(){
    	Hashtable newRelation = new Hashtable();
    	Set entries = this.relation.entrySet();
    	Set checkObjectNames = new HashSet();
    	for (Iterator iter = entries.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			FCAObject object = (FCAObject) entry.getKey();
			if (checkObjectNames.contains(object.getData())) {
				throw new IllegalStateException("Object appears twice in object set of many-valued context.");
			}
			checkObjectNames.add(object.getData());
			Hashtable propTable = (Hashtable) entry.getValue();
			Hashtable newPropTable = new Hashtable();
			Set propEntries = propTable.entrySet();
			Set checkPropertieNames = new HashSet();
			for (Iterator iter2 = propEntries.iterator(); iter2.hasNext();) {
				Entry propEntry = (Entry) iter2.next();
				ManyValuedAttribute prop = (ManyValuedAttribute) propEntry.getKey();
				if (checkPropertieNames.contains(prop.getName())){
					throw new IllegalStateException("Attribute appears twice in attribute set of many-valued context.");
				}
				checkPropertieNames.add(prop.getName());
				Object value = propEntry.getValue();
				newPropTable.put(prop,value);
			}
			newRelation.put(object,newPropTable);
		}
    	this.relation = newRelation;
    }
}
