/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.Collection;
import java.util.Hashtable;

public abstract class TypeImplementation implements Type {
    protected String name;
    protected Hashtable valueGroups = new Hashtable();

    public TypeImplementation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ValueGroup getValueGroup(String id) {
        return (ValueGroup) valueGroups.get(id);
    }

    public Collection getValueGroups() {
        return valueGroups.values();
    }
}
