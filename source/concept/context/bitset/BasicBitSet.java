/*
 * User: sergey
 * Date: Jan 16, 2002
 * Time: 8:21:05 PM
 */
package concept.context.bitset;

import concept.context.Fragment;
import concept.context.ModifiableFragment;
import concept.context.ModifiableSet;

public abstract class BasicBitSet implements ModifiableSet, Cloneable {
    protected int size;

    public BasicBitSet(int nbits) {
        if (nbits < 0)
            throw new IndexOutOfBoundsException(Integer.toString(nbits));
        this.size = nbits;
    }


    public int elementCount() {
        int ret = 0;
        for (int j = length(); --j >= 0;) {
            if (in(j))
                ret++;
        }
        return ret;
    }

    public int nextOut(int prev) {
        if (prev < 0) {
            return -1;
        }
        for (int i = prev + 1; i < size; i++) {
            if (!in(i)) {
                return i;
            }
        }
        return -1;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(this.getClass().isInstance(obj)))
            return false;
        if (this == obj)
            return true;
        return isEquals((Fragment) obj);
    }

    public ModifiableFragment makeModifiableFragment() {
        return makeModifiableSetCopy();
    }

    public Object clone() {
        return makeModifiableSetCopy();
    }

    public int size() {
        return size;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(3 * size() + 2);
        String separator = "";
        buffer.append('{');
        for (int i = 0; i < size(); i++) {
            buffer.append(separator);
            buffer.append(in(i) ? '1' : '0');
            separator = ", ";
        }
        buffer.append('}');
        return buffer.toString();
    }


    public boolean isSupersetOf(Fragment other) {
        return other.isSubsetOf(this);
    }

}
