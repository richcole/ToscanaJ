/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.BinaryRelation;
import net.sourceforge.toscanaj.model.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.Context;

import java.util.Collection;
import java.util.HashSet;

public class ContextImplementation implements Context {
    private Collection objects = new HashSet();
    private Collection attributes = new HashSet();
    private BinaryRelationImplementation relation = new BinaryRelationImplementation();

    public ContextImplementation() {
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
}
