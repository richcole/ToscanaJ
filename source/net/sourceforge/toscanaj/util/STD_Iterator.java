/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;


public class STD_Iterator implements Iterator {
    Iterator it;

    public STD_Iterator(Iterator a_it) {
        it = (Iterator) a_it.clone();
    }

    public STD_Iterator(java.util.Set set) {
        it = new JavaUtilSetIterator(set);
    }

    public STD_Iterator(java.util.Collection collection) {
        it = new JavaUtilCollectionIterator(collection);
    }

    public boolean isMember(Object x) {
        return it.isMember(x);
    }

    public void reset() {
        it.reset();
    }

    public void next() {
        it.next();
    }

    public Object val()  {
        return it.val();
    }

    public int count() throws Exception {
        return it.count();
    }

    public boolean atEnd() {
        return it.atEnd();
    }

    public Object clone() {
        return new STD_Iterator(it);
    }

    public void nextGTE(Comparable o)  {
        it.nextGTE(o);
    };
}

