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

public class ManyValuedContext {
    private List objects = new ArrayList();
    private List properties = new ArrayList();
    private Hashtable relation = new Hashtable();

    public ManyValuedContext() {
    }

    public void add(FCAObject object) {
        objects.add(object);
        relation.put(object, new Hashtable());
    }

    public Collection getObjects() {
        return objects;
    }

    public void add(Property property) {
        properties.add(property);
    }

    public Collection getProperties() {
        return properties;
    }

    public void setRelationship(FCAObject object, Property property, Value value) {
        Hashtable row = (Hashtable) relation.get(object);
        row.put(property, value);
    }

    public Object getRelationship(FCAObject object, Property property) {
        Hashtable row = (Hashtable) relation.get(object);
        return row.get(object);
    }
}
