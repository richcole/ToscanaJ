package net.sourceforge.toscanaj.util;

import net.sourceforge.toscanaj.util.Iterator;
import net.sourceforge.toscanaj.util.PreconditionFailedException;
import java.util.Set;

class JavaUtilCollectionIterator
  implements 
    net.sourceforge.toscanaj.util.Iterator 
{
    private java.util.Collection m_set;
    private java.util.Iterator   m_curr;
    private Object               m_curr_object;
    private boolean              m_at_end;

    private JavaUtilCollectionIterator(JavaUtilCollectionIterator a_it) 
	throws PreconditionFailedException 
    {
	m_set = a_it.m_set;
	reset();

	// move to the same position as the iterator we are
	// copying
	while( ! atEnd() && a_it.m_curr.equals(m_curr) ) {
	    a_it.next();
	}
    }

    public Object clone() {
	try {
	    return new JavaUtilCollectionIterator(this);
	}
	catch (PreconditionFailedException e) {
	    throw new UnknownError("Precondition Failed: " +
	      e.toString());
	}
    }

    public JavaUtilCollectionIterator(java.util.Collection set) {
	m_set = set;
	reset();
    }

    public boolean isMember(Object x) {
	java.util.Iterator curr = m_set.iterator();
	while( curr.hasNext() ) {
	    if ( x.equals( curr.next() ) ) {
		return true;
	    }
	}
	return false;
    }

    public void reset() {
	m_curr       = m_set.iterator();
	if ( m_curr.hasNext() ) {
	    m_curr_object = m_curr.next(); 
	    m_at_end = false;
	}
	else {
	    m_at_end = true;
	}
    }

    public Object val() throws PreconditionFailedException {
	if ( m_at_end == true ) {
	    throw new PreconditionFailedException("At end of sequence");
	}
	return m_curr_object;
    }

    public void next() throws PreconditionFailedException {
	if ( m_at_end == true ) {
	    throw new PreconditionFailedException("At end of sequence");
	}
	else {
	    if ( m_curr.hasNext() ) {
		m_curr_object = m_curr.next();
		m_at_end = false;
	    }
	    else {
		m_at_end = true;
	    }
	}
    }

    public int count() throws Exception {
	Iterator it = (Iterator) clone();
	int       count = 0;
	for(it.reset(); ! it.atEnd(); it.next()) {
	    ++count;
	}
	return count;
    }

    public boolean atEnd() {
	return m_at_end;
    }

    public void nextGTE(Comparable o) throws PreconditionFailedException {
	while( ! atEnd() && ((Comparable)val()).compareTo(o) < 0 ) {
	    next();
	}
    }
}
