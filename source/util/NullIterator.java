package util;

/**
 * Insert the type's description here.
 * Creation date: (18.12.00 23:19:13)
 * @author:
 */
public class NullIterator implements java.util.Iterator {
    private final static NullIterator nullIterator = new NullIterator();

    /**
     * NullIterator constructor comment.
     */
    private NullIterator() {
        super();
    }

    /**
     * hasNext method comment.
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * Insert the method's description here.
     * Creation date: (18.12.00 23:20:54)
     * @return util.NullIterator
     */
    public static NullIterator makeNull() {
        return nullIterator;
    }

    /**
     * next method comment.
     */
    public Object next() {
        return null;
    }

    /**
     * remove method comment.
     */
    public void remove() {
    }
}