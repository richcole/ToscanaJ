package net.sourceforge.toscanaj.util;

import net.sourceforge.toscanaj.util.Iterator;
import net.sourceforge.toscanaj.util.JavaUtilSetIterator;
import net.sourceforge.toscanaj.util.JavaUtilCollectionIterator;
import java.util.Set;

public class STD_Iterator implements Iterator 
{
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

    public void next() throws PreconditionFailedException {
	it.next();
    }

    public Object val() throws PreconditionFailedException {
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

    public void nextGTE(Comparable o) throws PreconditionFailedException {
	it.nextGTE(o);
    };
}

