/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util;


class JavaUtilSetIterator implements Iterator {
    private java.util.Set m_set;
    private java.util.Iterator m_curr;
    private Object m_curr_object;
    private boolean m_at_end;

    private JavaUtilSetIterator(JavaUtilSetIterator a_it) {
        m_set = a_it.m_set;
        reset();

        // move to the same position as the iterator we are
        // copying
        while (!atEnd() && a_it.m_curr.equals(m_curr)) {
            a_it.next();
        }
    }

    public Object clone() {
        return new JavaUtilSetIterator(this);
    }

    public JavaUtilSetIterator(java.util.Set set) {
        m_set = set;
        reset();
    }

    public boolean isMember(Object x) {
        java.util.Iterator curr = m_set.iterator();
        while (curr.hasNext()) {
            if (x.equals(curr.next())) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        m_curr = m_set.iterator();
        if (m_curr.hasNext()) {
            m_curr_object = m_curr.next();
            m_at_end = false;
        } else {
            m_at_end = true;
        }
    }

    public Object val() {
        if (m_at_end == true) {
            throw new Error("At end of sequence");
        }
        return m_curr_object;
    }

    public void next() {
        if (m_at_end == true) {
            throw new Error("At end of sequence");
        } else {
            if (m_curr.hasNext()) {
                m_curr_object = m_curr.next();
                m_at_end = false;
            } else {
                m_at_end = true;
            }
        }
    }

    public int count() throws Exception {
        Iterator it = (Iterator) clone();
        int count = 0;
        for (it.reset(); !it.atEnd(); it.next()) {
            ++count;
        }
        return count;
    }

    public boolean atEnd() {
        return m_at_end;
    }

    public void nextGTE(Comparable o) {
        while (!atEnd() && ((Comparable) val()).compareTo(o) < 0) {
            next();
        }
    }
}

