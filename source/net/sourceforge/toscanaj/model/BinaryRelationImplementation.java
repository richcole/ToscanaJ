/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

public class BinaryRelationImplementation implements BinaryRelation {
    private Hashtable rows = new Hashtable();

    public boolean contains(Object domainObject, Object rangeObject) {
        Collection objectAttributes = (Collection) rows.get(domainObject);
        if (objectAttributes == null) {
            return false;
        }
        return objectAttributes.contains(rangeObject);
    }

    public void insert(Object domainObject, Object rangeObject) {
        Collection objectAttributes = (Collection) rows.get(domainObject);
        if (objectAttributes == null) {
            objectAttributes = new HashSet();
            rows.put(domainObject, objectAttributes);
        }
        objectAttributes.add(rangeObject);
    }

    public void remove(Object domainObject, Object rangeObject) {
        Collection objectAttributes = (Collection) rows.get(domainObject);
        if (objectAttributes == null) {
            return; // nothing to remove
        }
        objectAttributes.remove(rangeObject);
    }
}
