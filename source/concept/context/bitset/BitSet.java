package concept.context.bitset;


/**
 * BitSet.java
 *
 *
 * Created: Fri Jan 07 19:01:45 2000
 *
 * @author
 * @version
 * based on java BitSet; rewriten cause of all private members in BitSet
 */


import concept.context.Fragment;
import concept.context.ModifiableSet;
import concept.context.Set;

import java.io.Serializable;

import util.Assert;


public class BitSet extends BasicBitSet implements Cloneable, Serializable {

    /*
 * BitSets are packed into arrays of "units."  Currently a unit is a long,
 * which consists of 64 bits, requiring 6 address bits.  The choice of unit
 * is determined purely by performance concerns.
 */
    private final static int ADDRESS_BITS_PER_UNIT = 6;
    private final static int BITS_PER_UNIT = 1 << ADDRESS_BITS_PER_UNIT;
    private final static int BIT_INDEX_MASK = BITS_PER_UNIT - 1;

    /**
     * The bits in this BitSet.  The ith bit is stored in bits[i/64] at
     * bit position i % 64 (where bit position 0 refers to the least
     * significant bit and 63 refers to the most significant bit).
     *
     * @serial
     */
    private long[] unit;  // this should be called unit[]

    /* use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 7997698588986878753L;

    /**
     * Creates a new bit set. All bits are initially <code>false</code>.
     */
    public BitSet() {
        this(BITS_PER_UNIT);
    }

    /**
     * Creates a bit set whose initial size is large enough to explicitly
     * represent bits with indices in the range <code>0</code> through
     * <code>nbits-1</code>. All bits are initially <code>false</code>.
     *
     * @param     nbits   the initial size of the bit set.
     * @exception NegativeArraySizeException if the specified initial size
     *               is negative.
     */
    public BitSet(int nbits) {
        /* nbits can't be negative; size 0 is OK */

        super(nbits);

        unit = new long[unitsNeeded(nbits)];
    }

    public BitSet(BitSet other) {
        super(other.size);
        unit = new long[other.unit.length];
        System.arraycopy(other.unit, 0, unit, 0, other.unit.length);
    }

    private int unitsNeeded(int nbits) {
        return (unitIndex(nbits - 1) + 1);
    }

    /**
     * Performs a logical <b>AND</b> of this target bit set with the
     * argument bit set. This bit set is modified so that each bit in it
     * has the value <code>true</code> if and only if it both initially
     * had the value <code>true</code> and the corresponding bit in the
     * bit set argument also had the value <code>true</code>.
     *
     * @param   set   a bit set.
     */
    public void and(Fragment s) {
        if (this == s)
            return;
        BitSet set = (BitSet) s;
        //*DBG*/util.Assert.isTrue(this.size ==set.size);
        //*DBG*/util.Assert.isTrue(this.unit.length==set.unit.length);
        for (int i = 0; i < unit.length; i++)
            unit[i] &= set.unit[i];
    }

    /**
     * Clears all of the bits in this <code>BitSet</code> whose corresponding
     * bit is set in the specified <code>BitSet</code>.
     *
     * @param     s the <code>BitSet</code> with which to mask this
     *            <code>BitSet</code>.
     * @since     JDK1.2
     */
    public void andNot(Fragment s) {
        BitSet set = (BitSet) s;
        //*DBG*/util.Assert.isTrue(this.size ==set.size);
        //*DBG*/util.Assert.isTrue(this.unit.length==set.unit.length);

        for (int i = 0; i < unit.length; i++) {
            unit[i] &= ~set.unit[i];
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (17.10.00 0:17:36)
     * @param s concept.context.Set
     */
    public void append(Set s) {
        if (0 == s.size()) {
            return;
        }
        if (0 == size()) {
            copy(s);
            return;
        }
        int newSize = size + s.size();
        int unitsRequired = unitIndex(newSize - 1) + 1;
        if (unit.length < unitsRequired) {
            resizeSetAndCopyOldValues(unitsRequired);
        }
        BitSet set = (BitSet) s;
        int startUnitIndex = unitIndex(size);
        int shiftCount = (size & BIT_INDEX_MASK);
        if (0 == shiftCount) {
            for (int i = unitIndex(set.size - 1); i >= 0; i--) {
                unit[startUnitIndex + i] = set.unit[i];
            }
        } else {

            long startMaskInThis = ~(0xFFFFFFFFFFFFFFFFL << shiftCount);
            long startMaskInOther = (0xFFFFFFFFFFFFFFFFL >>> shiftCount);
            long endMaskInOther = ~startMaskInOther;

            long prev = unit[startUnitIndex] & startMaskInThis;
            //calculate number of units needed to represent other size
            int unitsCount = unitIndex(set.size - 1);
            int secondPart = BITS_PER_UNIT - shiftCount;
            //for each long in second set
            //form new value from end of previous and begin of current
            for (int i = 0; i <= unitsCount; i++) {
                unit[startUnitIndex + i] = prev | ((set.unit[i] & startMaskInOther) << shiftCount);
                prev = ((set.unit[i] & endMaskInOther) >>> secondPart);
            }
            //add last value, if any
            if (((set.size - secondPart) & BIT_INDEX_MASK) != 0) {
                unit[startUnitIndex + unitsCount + 1] = prev;
            }
            ;

        }
        size = newSize;
    }

    /**
     * Given a bit index, return a unit that masks that bit in its unit.
     */
    private static long bit(int bitIndex) {
        return 1L << (bitIndex & BIT_INDEX_MASK);
    }

    public void clearSet() {
        for (int i = unit.length; --i >= 0;) {
            unit[i] = 0L;
        }
    }


    public ModifiableSet makeModifiableSetCopy() {
        return new BitSet(this);
    }

    public int compare(Set other) {
        if (other == null || !(other instanceof BitSet))
            return NOT_COMPARABLE;
        if (this == other)
            return EQUAL;

        BitSet _other = (BitSet) other;
        util.Assert.isTrue(this.size() == other.size());
        //*DBG*/util.Assert.isTrue(this.size ==_other.size);
        //*DBG*/util.Assert.isTrue(this.unit.length==_other.unit.length);
        int ret = Set.EQUAL;

        // Check units in use by both BitSets
        for (int i = unit.length; --i >= 0;) {
            if (unit[i] != _other.unit[i]) {
                long temp = unit[i] & _other.unit[i];
                if (temp == unit[i]) {
                    switch (ret) {
                        case Set.EQUAL:
                            ret = Set.SUBSET;
                            break;
                        case Set.SUBSET:
                            break;
                        default:
                            return Set.NOT_COMPARABLE;
                    }
                } else if (temp == _other.unit[i]) {
                    switch (ret) {
                        case Set.EQUAL:
                            ret = Set.SUPERSET;
                            break;
                        case Set.SUPERSET:
                            break;
                        default:
                            return Set.NOT_COMPARABLE;
                    }
                } else {
                    return Set.NOT_COMPARABLE;
                }
            }
        }
        return ret;
    }

    public void copy(Fragment s) {
        BitSet set = (BitSet) s;
        if (set != this) {
            if (unit.length != set.unit.length) {
                unit = new long[set.unit.length];
            }
            System.arraycopy(set.unit, 0, unit, 0, set.unit.length);
            size = set.size;
        }
    }
//-------------------------------------------------------------------------

    /**
     * Ensures that the BitSet can hold enough units.
     * @param	unitsRequired the minimum acceptable number of units.
     */
    private void ensureCapacity(int unitsRequired) {
        if (unit.length < unitsRequired) {
            /* Allocate larger of doubled size or required size */
            resizeSetAndCopyOldValues(unitsRequired);
        }
    }

    private void resizeSetAndCopyOldValues(int unitsRequired) {
        util.Assert.isTrue(unitsRequired > 0);
        long newBits[] = new long[unitsRequired];
        System.arraycopy(unit, 0, newBits, 0, Math.min(unit.length, unitsRequired));
        unit = newBits;
    }


    /**
     * Removes the bits specified from set .
     * Value of the specified bit becomes the value of right bit
     * @param     bitIndex   the index of the bit to be cleared.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void exclude(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        }
        int unitIndex = unitIndex(bitIndex);
        if (unitIndex >= unit.length) {
            return;
        }
        long mask;
        int indexInMask = bitIndex & BIT_INDEX_MASK;
        if (indexInMask != 0) {
            mask = 0xffffffffffffffffL >>> (BITS_PER_UNIT - indexInMask);
        } else {
            mask = 0L;
        }
        //*DBG*/ System.out.println("mask "+Long.toHexString(mask));
        long tempVal = unit[unitIndex] & mask;
        unit[unitIndex] >>>= 1;
        unit[unitIndex] &= ~mask;
        unit[unitIndex] |= tempVal;
        for (int l = unitIndex + 1; l < unit.length; l++) {
            if ((unit[l] & 0x1L) != 0) {
                unit[l - 1] |= 0x8000000000000000L;
            }
            unit[l] >>>= 1;
        }
        if (bitIndex < size) {
            --size;
            if (unitIndex(size - 1) < unit.length - 1) {
                int newSize = unitIndex(size - 1) + 1;
                long newBits[] = new long[newSize];
                System.arraycopy(unit, 0, newBits, 0, newSize);
                unit = newBits;
            }
        }
    }

    public void fillByOne(int num) {
        if (num < 0)
            throw new IndexOutOfBoundsException(Integer.toString(num));
        if (0 == num) {
            clearSet();
            return;
        }
        final int bitTill = num - 1;
        final int unitPos = unitIndex(bitTill);
        for (int i = 0; i < unitPos; i++) {
            unit[i] = ~0L;
        }
        for (int i = unitPos; i < unit.length; i++) {
            unit[i] = 0L;
        }
        for (long i = bit(bitTill); i != 0; i >>>= 1) {
            unit[unitPos] |= i;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:43:47)
     * @return int
     */
    public int firstIn() {
        if (size == 0) {
            return NOT_IN_SET;
        }

        int i = 0;
        int upperBound = unit.length - 1;
        for (int k = 0; k < upperBound; k++) {
            final long unit = this.unit[k];
            long mask = 0x1L;
            while (mask != 0) {
                if ((unit & mask) != 0) {
                    return i;
                }
                mask <<= 1;
                i++;
            }
        }
        final long lastUnit = unit[upperBound];
        long mask = 0x1L;
        while (i != size) {
            if ((lastUnit & mask) != 0) {
                return i;
            }
            mask <<= 1;
            i++;
        }
        return NOT_IN_SET;
    }

    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:43:47)
     * @return int
     */
    public int firstOut() {
        int bound = size();
        for (int i = 0; i < bound; i++) {
            if (!in(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a hash code value for this bit set. The has code
     * depends only on which bits have been set within this
     * <code>BitSet</code>. The algorithm used to compute it may
     * be described as follows.<p>
     * Suppose the bits in the <code>BitSet</code> were to be stored
     * in an array of <code>long</code> integers called, say,
     * <code>bits</code>, in such a manner that bit <code>k</code> is
     * set in the <code>BitSet</code> (for nonnegative values of
     * <code>k</code>) if and only if the expression
     * <pre>((k&gt;&gt;6) &lt; bits.length) && ((bits[k&gt;&gt;6] & (1L &lt;&lt; (bit & 0x3F))) != 0)</pre>
     * is true. Then the following definition of the <code>hashCode</code>
     * method would be a correct implementation of the actual algorithm:
     * <pre>
     * public synchronized int hashCode() {
     *      long h = 1234;
     *      for (int i = bits.length; --i &gt;= 0; ) {
     *           h ^= bits[i] * (i + 1);
     *      }
     *      return (int)((h &gt;&gt; 32) ^ h);
     * }</pre>
     * Note that the hash code values change if the set of bits is altered.
     * <p>Overrides the <code>hashCode</code> method of <code>Object</code>.
     *
     * @return  a hash code value for this bit set.
     */
    public int hashCode() {
        long h = 1234;
        for (int i = unit.length; --i >= 0;)
            h ^= unit[i] * (i + 1);

        return (int) ((h >> 32) ^ h);
    }

    /**
     * Returns the value of the bit with the specified index. The value
     * is <code>true</code> if the bit with the index <code>bitIndex</code>
     * is currently set in this <code>BitSet</code>; otherwise, the result
     * is <code>false</code>.
     *
     * @param     bitIndex   the bit index.
     * @return    the value of the bit with the specified index.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     */
    public boolean in(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        util.Assert.isTrue(bitIndex < size, "Size is " + size + " index =" + bitIndex);
        int unitIndex = unitIndex(bitIndex);
        if (unitIndex >= unit.length)
            return false;
        return ((unit[unitIndex] & bit(bitIndex)) != 0);
    }

    public boolean intersects(Fragment s) {
        if (s instanceof BitSet) {
            BitSet set = (BitSet) s;
            for (int i = 0; i < unit.length; i++) {
                if ((unit[i] & set.unit[i]) != 0) {
                    return true;
                }
            }
        }
        return false;
    }
//-------------------------------------------------------------------------
    /*********************************************************
     * very stupid implementation of isEmpty. Later will be changed
     **********************************************************/
    public boolean isEmpty() {
        return length() == 0;
    }

    /**
     * Returns the "logical size" of this <code>BitSet</code>: the index of
     * the highest set bit in the <code>BitSet</code> plus one.
     *
     * @return  the logical size of this <code>BitSet</code>.
     * @since   JDK1.2
     */
    public int length() {
        int k = unit.length;
        while (--k >= 0) {
            if (unit[k] != 0) {
                int highestBit = (k) * BITS_PER_UNIT;
                long highestUnit = unit[k];
                do {
                    highestUnit = highestUnit >>> 1;
                    highestBit++;
                } while (highestUnit > 0);
                return highestBit;
            }
        }
        return 0;
    }

    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:43:47)
     * @return int
     * @param prev int
     */
    public int nextIn(int prev) {
        if (prev < 0) {
            return -1;
        }

        int i = prev + 1;
        if (i >= size) {
            return -1;
        }
        int startUnit = unitIndex(i);
        if (startUnit >= unit.length) {
            return -1;
        }

        int endUnit = unit.length - 1;
        long mask = bit(i);
        for (int k = startUnit; k < endUnit; k++) {
            long currUnit = unit[k];
            while (mask != 0) {
                if ((currUnit & mask) != 0) {
                    return i;
                }
                mask <<= 1;
                i++;
            }
            mask = 0x1L;
        }
        //*DBG*/util.Assert.isTrue(unitIndex(size-1)==(unit.length-1));
        long lastUnit = unit[endUnit];
        while (i < size) {
            if ((lastUnit & mask) != 0) {
                return i;
            }
            mask <<= 1;
            i++;
        }

        return -1;
    }

    /**
     * Performs a logical <b>OR</b> of this bit set with the bit set
     * argument. This bit set is modified so that a bit in it has the
     * value <code>true</code> if and only if it either already had the
     * value <code>true</code> or the corresponding bit in the bit set
     * argument has the value <code>true</code>.
     *
     * @param   set   a bit set.
     */
    public void or(Set s) {
        if (this == s)
            return;
        BitSet set = (BitSet) s;
        //*DBG*/util.Assert.isTrue(this.size ==set.size);
        //*DBG*/util.Assert.isTrue(this.unit.length==set.unit.length);

        for (int i = 0; i < unit.length; i++)
            unit[i] |= set.unit[i];
    }

    /**
     * Sets the bit specified by the index to <code>true</code>.
     *
     * @param     bitIndex   a bit index.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void put(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        int unitIndex = unitIndex(bitIndex);
        unit[unitIndex] |= bit(bitIndex);
    }

    /**
     * Sets the bit specified by the index to <code>false</code>.
     *
     * @param     bitIndex   the index of the bit to be cleared.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void remove(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        int unitIndex = unitIndex(bitIndex);
        unit[unitIndex] &= ~bit(bitIndex);
    }

    /**
     * Given a bit index return unit index containing it.
     */
    private static int unitIndex(int bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_UNIT;
    }

    //------------------------------------------------
    /**************************************************
     *  compares two sets in lexicographical order
     *  1 - this set is greater then other
     *  0 - sets are equal
     *  -1 - this set is less than other
     **************************************************/
    public int lexCompareGanter(Set _other) {
        if (!(_other instanceof BitSet)) {
            throw new IllegalArgumentException("Bad argument in lex compare");
        }
        BitSet other = (BitSet) _other;

        int upperBound = unit.length - 1;
        for (int k = 0; k < upperBound; k++) {
            long mask = 0x1L;
            long xorMask = unit[k] ^ other.unit[k];
            while (mask != 0) {
                if ((mask & xorMask) != 0) {
                    return ((mask & unit[k]) != 0) ? 1: -1;
                }
                mask <<= 1;
            }
        }
        long mask = 0x1L;
        long xorMask = unit[upperBound] ^ other.unit[upperBound];
        int i = upperBound * BITS_PER_UNIT;
        while (i < size) {
            if ((mask & xorMask) != 0) {
                return ((mask & unit[upperBound]) != 0) ? 1: -1;
            }
            mask <<= 1;
            i++;
        }
        return 0;
    }


    /**
     * Insert the method's description here.
     * Creation date: (22.07.01 0:05:18)
     * @param newSize int
     */
    public void resize(int newSize) {
        if (size != newSize) {
            final int newUnitsNeeded = unitsNeeded(newSize);
            util.Assert.isTrue(newUnitsNeeded >= 1);
            if (newUnitsNeeded != unit.length) {
                resizeSetAndCopyOldValues(newUnitsNeeded);
            }
            util.Assert.isTrue(newUnitsNeeded == unit.length);

            clearBitsInLastUnitFromSetIndexTillEnd(newSize);

            size = newSize;
        }

    }

    private void clearBitsInLastUnitFromSetIndexTillEnd(int startIndex) {
        final int newSizeUpperEstimate = unit.length * BITS_PER_UNIT;
        final int lastIndex = unit.length - 1;

        for (int i = startIndex; i < newSizeUpperEstimate; i++) {
            unit[lastIndex] &= ~bit(i);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (26.07.01 8:25:13)
     * @return boolean
     * @param s concept.context.Set
     */
    public boolean isSubsetOf(Fragment other) {
        if (other == null || !(other instanceof BitSet))
            return false;
        if (this == other)
            return true;

        BitSet set = (BitSet) other;
        //*DBG*/util.Assert.isTrue(this.size ==set.size);
        //*DBG*/util.Assert.isTrue(this.unit.length==set.unit.length);

        // Check units in use by both BitSets
        for (int i = 0; i < unit.length; i++) {
            if (unit[i] != (unit[i] & set.unit[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Insert the method's description here.
     * Creation date: (13.07.01 9:17:07)
     * @return boolean
     * @param index int
     */
    public boolean out(int bitIndex) {
        if (bitIndex < 0)
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        Assert.isTrue(bitIndex<size(), "index bigger than size");
        int unitIndex = unitIndex(bitIndex);
        if (unitIndex >= unit.length)
            return true;
        return ((unit[unitIndex] & bit(bitIndex)) == 0);
    }


    /**
     * Insert the method's description here.
     * Creation date: (13.07.01 8:41:40)
     * @return int
     */
    public int outUpperBound() {
        int highestBit = size - 1;
        int currUnit = unit.length;
        while (--currUnit >= 0) {
            long highestUnit = unit[currUnit];
            long bitMask = bit(highestBit);
            while (bitMask != 0) {
                if (0 == (highestUnit & bitMask)) {
                    return highestBit + 1;
                }
                highestBit--;
                bitMask >>>= 1;
            }
        }
        ;
        return highestBit + 1;
    }

    public boolean isEquals(Fragment obj) {
        BitSet set = (BitSet) obj;
        if (this.size != set.size) {
            return false;
        }

        for (int i = unit.length; --i >= 0;) {
            if (unit[i] != set.unit[i])
                return false;
        }

        return true;
    }

}