/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @todo hide access to collections and relation by playing middle man.
 */
public class ContextImplementation implements Context {
    private Collection objects = new ArrayList();
    private Collection attributes = new ArrayList();
    private BinaryRelationImplementation relation = new BinaryRelationImplementation();
    private String name = null;

    public ContextImplementation() {
    }

    public ContextImplementation(String name) {
    	this.name = name;
    }

    public Collection getObjects() {
        return objects;
    }

    public Collection getAttributes() {
        return attributes;
    }

    public BinaryRelation getRelation() {
        return relation;
    }
    
    public BinaryRelationImplementation getRelationImplementation() {
    	return this.relation;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Context combine(Context other, String title) {
		ContextImplementation context = new ContextImplementation(title);
		ArrayList objects = (ArrayList) context.getObjects();
		ArrayList attributes = (ArrayList) context.getAttributes();
		BinaryRelationImplementation relation = context.getRelationImplementation();
		
		Iterator objIt = this.getObjects().iterator();
		while (objIt.hasNext()) {
			Object object = (Object) objIt.next();
			objects.add(object);
		}
		objIt = other.getObjects().iterator();
		while (objIt.hasNext()) {
			Object object = (Object) objIt.next();
			objects.add(object);
		}
		Iterator attrIt = this.getAttributes().iterator();
		while (attrIt.hasNext()) {
			Object attribute = (Object) attrIt.next();
			attributes.add(attribute);
		}
		attrIt = other.getAttributes().iterator();
		while (attrIt.hasNext()) {
			Object attribute = (Object) attrIt.next();
			attributes.add(attribute);
		}
		objIt = objects.iterator();
		while (objIt.hasNext()) {
			Object object = (Object) objIt.next();
			attrIt = attributes.iterator();
			while (attrIt.hasNext()) {
				Object attribute = (Object) attrIt.next();
				if(this.getRelation().contains(object,attribute) ||
				   other.getRelation().contains(object,attribute)) {
					relation.insert(object,attribute);
				}
			}
		}
		return context;
	}
}
