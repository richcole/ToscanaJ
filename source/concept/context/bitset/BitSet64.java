package concept.context.bitset;


/**
 * BitSet64.java
 *
 *
 * Created: Fri Jan 07 19:01:45 2000
 *
 * @author
 * @version
 * based on java BitSet64; rewriten cause of all private members in BitSet64
 */


import concept.context.Fragment;
import concept.context.ModifiableSet;
import concept.context.Set;

import java.io.Serializable;


public class BitSet64 extends BasicBitSet implements Cloneable, Serializable {
    /*
 * BitSets are packed into arrays of "units."  Currently a unit is a long,
 * which consists of 64 bits, requiring 6 address bits.  The choice of unit
 * is determined purely by performance concerns.
 */
    private final static int ADDRESS_BITS_PER_UNIT = 6;
    private final static int BITS_PER_UNIT = 1 << ADDRESS_BITS_PER_UNIT;
    private final static int BIT_INDEX_MASK = BITS_PER_UNIT - 1;

    /**
     * The bits in this BitSet64.  The ith bit is stored in bits[i/64] at
     * bit position i % 64 (where bit position 0 refers to the least
     * significant bit and 63 refers to the most significant bit).
     *
     * @serial
     */
    private long bits;  // this should be called bits[]


    /**
     * Creates a new bit set. All bits are initially <code>false</code>.
     */
    public BitSet64() {
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
    public BitSet64(int nbits) {
        /* nbits can't be negative; size 0 is OK */
        super(nbits);
        checkSize(nbits);
        bits = 0;
    }


    public BitSet64(BitSet64 other) {
        super(other.size);
        bits = other.bits;
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

        BitSet64 set = (BitSet64) s;
        bits &= set.bits;
        size = Math.min(size, set.size);
    }

    /**
     * Clears all of the bits in this <code>BitSet64</code> whose corresponding
     * bit is set in the specified <code>BitSet64</code>.
     *
     * @param     s the <code>BitSet64</code> with which to mask this
     *            <code>BitSet64</code>.
     * @since     JDK1.2
     */
    public void andNot(Fragment s) {
        BitSet64 set = (BitSet64) s;
        bits &= ~set.bits;
    }

    /**
     * Insert the method's description here.
     * Creation date: (17.10.00 0:17:36)
     * @param s concept.context.Set
     */
    public void append(Set s) {
        BitSet64 set = (BitSet64) s;
        if (0 == set.size) {
            return;
        }
        if (0 == size) {
            copy(set);
            return;
        }
        int newSize = size + set.size;
        checkSize(newSize);
        int shiftCount = (size & BIT_INDEX_MASK);

        long startMaskInThis = ~(0xFFFFFFFFFFFFFFFFL << shiftCount);
        long startMaskInOther = (0xFFFFFFFFFFFFFFFFL >>> shiftCount);

        bits = (bits & startMaskInThis) | ((set.bits & startMaskInOther) << shiftCount);

        size = newSize;
    }

    /**
     * Given a bit index, return a bits that masks that bit in its bits.
     */
    private static long bit(int bitIndex) {
        return 1L << (bitIndex & BIT_INDEX_MASK);
    }
//--------------------------------------------------

    /**
     * Describe <code>clearExtraWords</code> method here.
     *
     * @param posFrom an <code>int</code> - describes position,
     *  from which clear words in set
     */

    public void clearSet() {
        bits = 0;
    }

    public ModifiableSet makeModifiableSetCopy() {
        return new BitSet64(this);
    }

    public int compare(Set other) {
        if (other == null || !(other instanceof BitSet64))
            return NOT_COMPARABLE;
        if (this == other)
            return EQUAL;

        BitSet64 _other = (BitSet64) other;

        if (bits == _other.bits) {
            return Set.EQUAL;
        }
        long temp = bits & _other.bits;
        if (temp == bits) {
            return Set.SUBSET;
        } else if (temp == _other.bits) {
            return Set.SUPERSET;
        } else {
            return Set.NOT_COMPARABLE;
        }
    }

    public void copy(Fragment s) {
        BitSet64 set = (BitSet64) s;
        if (set != this) {
            bits = set.bits;
            size = set.size;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (22.10.00 22:58:19)
     * @param minSize int
     */
    void ensureSize(int minSize) {
        if (size < minSize) {
            size = minSize;
        }
    }

    /**
     * Removes the bits specified from set .
     * Value of the specified bit becomes the value of right bit
     * @param     bitIndex   the index of the bit to be cleared.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void exclude(int bitIndex) {
        checkIndexAndSize(bitIndex);
        long mask;
        int indexInMask = bitIndex & BIT_INDEX_MASK;
        if (indexInMask != 0) {
            mask = 0xffffffffffffffffL >>> (BITS_PER_UNIT - indexInMask);
        } else {
            mask = 0L;
        }
        //*DBG*/ System.out.println("mask "+Long.toHexString(mask));
        long tempVal = bits & mask;
        bits >>>= 1;
        bits &= ~mask;
        bits |= tempVal;

        --size;
    }

    public void fillByOne(int num) {
        checkSize(num);
        if (0 == num) {
            clearSet();
            return;
        }
        bits = 0;
        final int bitTill = num - 1;
        for (long i = bit(bitTill); i != 0; i >>>= 1) {
            bits |= i;
        }
        ensureSize(num);
    }

    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:43:47)
     * @return int
     */
    public int firstIn() {
        if (0 == size) {
            return -1;
        }
        int i = 0;
        long mask = 0x1L;
        while (i != size) {
            if ((bits & mask) != 0) {
                return i;
            }
            mask <<= 1;
            i++;
        }
        return -1;
    }

    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:43:47)
     * @return int
     */
    public int firstOut() {
        final int bound = size;
        for (int i = 0; i < bound; i++) {
            if (!in(i)) {
                return i;
            }
        }
        return -1;
    }

    public int hashCode() {
        long h = 1234;
        h ^= bits;

        return (int) ((h >> 32) ^ h);
    }

    public boolean in(int bitIndex) {
        checkIndex(bitIndex);
        if (bitIndex >= size) {
            return false;
        }
        return (bits & bit(bitIndex)) != 0;
    }

    public boolean intersects(Fragment s) {
        if (s instanceof BitSet64) {
            BitSet64 set = (BitSet64) s;
            if ((bits & set.bits) != 0) {
                return true;
            }
        }
        return false;
    }
//-------------------------------------------------------------------------
    /*********************************************************
     * very stupid implementation of isEmpty. Later will be changed
     **********************************************************/
    public boolean isEmpty() {
        return 0 == bits;
    }

    /**
     * Returns the "logical size" of this <code>BitSet64</code>: the index of
     * the highest set bit in the <code>BitSet64</code> plus one.
     *
     * @return  the logical size of this <code>BitSet64</code>.
     * @since   JDK1.2
     */
    public int length() {
        if (size == 0)
            return 0;

        int highestBit = 0;
        long highestUnit = bits;
        do {
            highestUnit = highestUnit >>> 1;
            highestBit++;
        } while (highestUnit > 0);
        return highestBit;
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

        long mask = bit(i);
        final long lastUnit = bits;
        while (i < size) {
            if ((lastUnit & mask) != 0) {
                return i;
            }
            mask <<= 1;
            i++;
        }
        return -1;
    }


    public void or(Set s) {
        if (this == s)
            return;
        BitSet64 set = (BitSet64) s;
        bits |= set.bits;
        ensureSize(set.size);
    }

    /**
     * Sets the bit specified by the index to <code>true</code>.
     *
     * @param     bitIndex   a bit index.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void put(int bitIndex) {
        checkIndex(bitIndex);
        bits |= bit(bitIndex);
        ensureSize(bitIndex + 1);
    }

    /**
     * Sets the bit specified by the index to <code>false</code>.
     *
     * @param     bitIndex   the index of the bit to be cleared.
     * @exception IndexOutOfBoundsException if the specified index is negative.
     * @since     JDK1.0
     */
    public void remove(int bitIndex) {
        checkIndexAndSize(bitIndex);
        bits &= ~bit(bitIndex);
    }

    //------------------------------------------------
    /**************************************************
     *  compares two sets in lexicographical order
     *  1 - this set is greater then other
     *  0 - sets are equal
     *  -1 - this set is less than other
     **************************************************/
    public int lexCompareGanter(Set _other) {
        if (!(_other instanceof BitSet64)) {
            throw new IllegalArgumentException("Bad argument in lex compare");
        }
        BitSet64 other = (BitSet64) _other;
        final int compSize = Math.max(this.size, other.size);

        long mask = 0x1L;
        long xorMask = bits ^ other.bits;
        for (int i = 0; i < compSize; i++) {
            if ((mask & xorMask) != 0) {
                return ((mask & bits) != 0) ? 1: -1;
            }
            mask <<= 1;
/*			if(in(i)){
				if(!other.in(i)){
					return 1;
				}
			}else{
				if(other.in(i)){
					return -1;
				}
			}*/
        }
        return 0;
    }


    /**
     * Insert the method's description here.
     * Creation date: (13.07.01 9:17:07)
     * @return boolean
     * @param index int
     */
    public boolean out(int bitIndex) {
        checkIndex(bitIndex);
        return (bits & bit(bitIndex)) == 0;
    }


    /**
     * Insert the method's description here.
     * Creation date: (13.07.01 8:41:40)
     * @return int
     */
    public int outUpperBound() {
        int highestBit = size - 1;

        long highestUnit = bits;
        long bitMask = bit(highestBit);
        while (bitMask != 0) {
            if (0 == (highestUnit & bitMask)) {
                return highestBit + 1;
            }
            highestBit--;
            bitMask >>>= 1;
        }
        return highestBit + 1;
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.07.01 22:06:28)
     * @param bitIndex the index of the bit to be cleared.
     */
    protected static void checkIndex(int bitIndex) {
        if (bitIndex < 0) {
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        }
        if (bitIndex > 63) {
            throw new IndexOutOfBoundsException(Integer.toString(bitIndex));
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.07.01 22:19:32)
     * @param bitIndex the index of the bit to be cleared.
     */
    protected void checkIndexAndSize(int bitIndex) {
        checkIndex(bitIndex);
        checkIndexToSize(bitIndex);
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.07.01 22:11:30)
     * @param bitIndex the index of the bit to be cleared.
     */
    protected void checkIndexToSize(int bitIndex) {
        if (bitIndex >= size) {
            throw new IllegalArgumentException(Integer.toString(bitIndex));
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.07.01 21:59:03)
     * @param newSize size of bitset
     */
    protected static void checkSize(int newSize) {
        if (newSize < 0)
            throw new IndexOutOfBoundsException(Integer.toString(newSize));
        if (newSize > 64) {
            throw new IndexOutOfBoundsException("Illegal size for BitSet64 " + Integer.toString(newSize));
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (22.07.01 0:05:28)
     * @param newSize int
     */
    public void resize(int newSize) {
        checkSize(newSize);
        if (size != newSize) {
            size = newSize;
        }
        for(int i=newSize; i<BITS_PER_UNIT;i++){
            bits&= ~bit(i);
        }

    }


    /**
     * Insert the method's description here.
     * Creation date: (05.08.01 17:09:54)
     */
    public boolean isEquals(Fragment obj) {
        BitSet64 set = (BitSet64) obj;
        if (bits != set.bits)
            return false;
        if (size != set.size)
            return false;

        return true;
    }


    /**
     * Insert the method's description here.
     * Creation date: (26.07.01 8:25:24)
     * @return boolean
     * @param s concept.context.Set
     */
    public boolean isSubsetOf(Fragment other) {
        if (other == null || !(other instanceof BitSet64))
            return false;
        BitSet64 s = (BitSet64) other;
        return (bits == (bits & s.bits));
    }


}