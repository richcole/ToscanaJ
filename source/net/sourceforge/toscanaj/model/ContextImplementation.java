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
}
