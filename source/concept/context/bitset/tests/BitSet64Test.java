package concept.context.bitset.tests;

import com.mockobjects.util.TestCaseMo;
import concept.context.ModifiableSet;
import concept.context.Set;
import concept.context.bitset.BitSet;
import concept.context.bitset.BitSet64;
import concept.context.tests.SetBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BitSet64Test extends TestCaseMo {
    private static final Class THIS = BitSet64Test.class;

    private ModifiableSet twoWordSet;
    private ModifiableSet emptySet;
    private ModifiableSet secondWord;
    private ModifiableSet firstWord;
    private ModifiableSet temp;

    public BitSet64Test(String name) {
        super(name);
    }


    public static void main(String[] args) {
        start(new String[]{THIS.getName()});
    }


    protected void setUp() {
        twoWordSet = new BitSet64(64);
        twoWordSet.fillByOne(64);
        emptySet = new BitSet64(0);

        firstWord = new BitSet64(64);
        for (int i = 0; i < 32; i++)
            firstWord.put(i);
        secondWord = new BitSet64(64);
        for (int i = 32; i < 64; i++)
            secondWord.put(i);

        temp = new BitSet64(64);
    }


    public static Test suite() {
        return new TestSuite(THIS);
    }


    public void testAnd() {
        emptySet.and(firstWord);
        assertTrue(emptySet.isEmpty());

        emptySet.and(secondWord);
        assertTrue(emptySet.isEmpty());

        twoWordSet.and(firstWord);
        assertEquals(twoWordSet, firstWord);

        twoWordSet.and(secondWord);
        assertEquals(twoWordSet, temp);

        secondWord.and(firstWord);
        assertEquals(secondWord, temp);

        firstWord.and(emptySet);
        assertEquals(firstWord, emptySet);
    }


    public void testAndNot() {
        firstWord.andNot(twoWordSet);
        assertEquals(temp, firstWord);
        temp.fillByOne(32);
        twoWordSet.andNot(secondWord);
        assertEquals(temp, twoWordSet);

        temp.copy(secondWord);
        secondWord.andNot(emptySet);
        assertEquals(temp, secondWord);
        emptySet.andNot(secondWord);
        assertTrue(emptySet.isEmpty());
    }


    /**
     * Insert the method's description here.
     * Creation date: (20.10.00 21:00:07)
     */
    public void testAppend() {
        //test append to empty word
        //test append to empty word
        assertEquals(0, emptySet.size());
        testAppendTwoSets(emptySet, emptySet);
        testAppendTwoSets(firstWord, emptySet);
        emptySet = new BitSet64(0);
        testAppendTwoSets(emptySet, firstWord);
    }


    /**
     * Insert the method's description here.
     * Creation date: (21.10.00 8:05:10)
     * @param one concept.context.Set
     * @param two concept.context.Set
     */
    void testAppendTwoSets(Set one, Set two) {
        ModifiableSet res = (ModifiableSet) one.clone();
        res.append(two);
        assertEquals(one.size() + two.size(), res.size());
        for (int i = one.size(); --i >= 0;) {
            assertEquals(res.in(i), one.in(i));
        }
        int bound = one.size();
        for (int i = two.size(); --i >= 0;) {
            assertEquals(res.in(bound + i), two.in(i));
        }
    }


    public void testClearSet() {
        assertTrue(emptySet.isEmpty());
        utilClearSet(twoWordSet);
        utilClearSet(firstWord);
        utilClearSet(secondWord);
    }


    public void testClone() {
        utilTestClone(twoWordSet);
        utilTestClone(emptySet);
        utilTestClone(firstWord);
        utilTestClone(secondWord);
    }


    public void testCompare() {
        assertEquals(Set.EQUAL, emptySet.compare(emptySet));
        assertEquals(Set.SUBSET, emptySet.compare(firstWord));
        assertEquals(Set.SUBSET, emptySet.compare(secondWord));
        assertEquals(Set.SUBSET, emptySet.compare(twoWordSet));
        assertEquals(Set.SUBSET, firstWord.compare(twoWordSet));
        assertEquals(Set.SUPERSET, twoWordSet.compare(firstWord));
        twoWordSet.remove(33);
        assertEquals(Set.NOT_COMPARABLE, twoWordSet.compare(secondWord));
        assertEquals(Set.NOT_COMPARABLE, secondWord.compare(twoWordSet));

    }


    public void testCopySet() {
        temp.copy(twoWordSet);
        assertEquals(temp, twoWordSet);
        temp.copy(emptySet);
        assertEquals(temp, emptySet);
        temp.copy(firstWord);
        assertEquals(temp, firstWord);
        temp.copy(secondWord);
        assertEquals(temp, secondWord);
    }


    /**
     * Insert the method's description here.
     * Creation date: (20.10.00 21:00:22)
     */
    public void testCreationWithMinusArg() {
        try {
            temp = new BitSet(-1);
            fail("created set with negative size");
        } catch (IndexOutOfBoundsException ex) {
            assertTrue(true);
        }
    }


    public void testElementCount() {
        assertEquals(emptySet.elementCount(), 0);
        utilTestElemCount(emptySet);
        assertEquals(twoWordSet.elementCount(), 64);
        utilTestElemCount(twoWordSet);
        assertEquals(firstWord.elementCount(), 32);
        utilTestElemCount(firstWord);
        assertEquals(secondWord.elementCount(), 32);
        utilTestElemCount(secondWord);
    }


    public void testEmpty() {
        assertEquals(false, twoWordSet.isEmpty());
        assertEquals(true, emptySet.isEmpty());
        assertEquals(false, firstWord.isEmpty());
        assertEquals(false, secondWord.isEmpty());
    }


    public void testEquals() {
        assertEquals(emptySet, emptySet);
        assertEquals(twoWordSet, twoWordSet);
        assertEquals(firstWord, firstWord);
        assertEquals(secondWord, secondWord);

        assertEquals(false, emptySet.equals(twoWordSet));
        assertEquals(false, emptySet.equals(firstWord));
        assertEquals(false, emptySet.equals(secondWord));

        assertEquals(false, twoWordSet.equals(emptySet));
        assertEquals(false, twoWordSet.equals(firstWord));
        assertEquals(false, twoWordSet.equals(secondWord));

        assertEquals(false, firstWord.equals(emptySet));
        assertEquals(false, firstWord.equals(twoWordSet));
        assertEquals(false, firstWord.equals(secondWord));

        assertEquals(false, secondWord.equals(emptySet));
        assertEquals(false, secondWord.equals(twoWordSet));
        assertEquals(false, secondWord.equals(firstWord));
    }


    public void testExclude() {
        for (int i = 63; i >= 32; i--) {
            secondWord.exclude(i);
            firstWord.exclude(i);
            twoWordSet.exclude(i);
        }
        assertEquals(32, firstWord.size());
        assertEquals(32, secondWord.size());

        assertEquals(twoWordSet, firstWord);
        assertEquals(false, firstWord.isEmpty());
        for (int i = 31; i >= 0; i--) {
            secondWord.exclude(i);
        }
        assertEquals(0, secondWord.size());
        for (int i = 31; i >= 0; i--) {
            firstWord.exclude(i);
        }
        assertEquals(emptySet, firstWord);
        assertEquals(0, firstWord.size());
    }


    public void testFillByOne() {
        emptySet = new BitSet64(64);
        temp.fillByOne(0);
        assertEquals(emptySet, temp);
        temp.fillByOne(64);
        assertEquals(64, temp.length());

        temp.fillByOne(32);
        assertEquals(firstWord, temp);

        try {
            temp.fillByOne(-1);
            fail("IndexOutOfBoundsException was not thrown");
        } catch (IndexOutOfBoundsException ex) {
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:44:36)
     */
    public void testFirstInAndOut() {
        assertEquals(-1, emptySet.firstIn());
        assertEquals(-1, emptySet.firstOut());

        assertEquals(0, firstWord.firstIn());
        assertEquals(32, firstWord.firstOut());

        assertEquals(32, secondWord.firstIn());
        assertEquals(0, secondWord.firstOut());

        twoWordSet = new BitSet64(64);
        assertEquals(-1, twoWordSet.firstIn());
        twoWordSet.put(63);
        assertEquals(63, twoWordSet.firstIn());
        twoWordSet.put(32);
        assertEquals(32, twoWordSet.firstIn());

    }


    public void testIn() {
        for (int i = 0; i < 64; i++) {
            assertTrue(twoWordSet.in(i));
            assertEquals(false, emptySet.in(i));
        }
        for (int i = 0; i < 32; i++) {
            assertTrue(firstWord.in(i));
            assertEquals(false, secondWord.in(i));
        }
        for (int i = 33; i < 64; i++) {
            assertEquals(false, firstWord.in(i));
            assertEquals(true, secondWord.in(i));
        }
    }


    public void testIntersects() {
        utilTestIntersects(emptySet, emptySet, false);
        utilTestIntersects(twoWordSet, twoWordSet, true);
        utilTestIntersects(firstWord, firstWord, true);
        utilTestIntersects(secondWord, secondWord, true);

        utilTestIntersects(firstWord, secondWord, false);
        utilTestIntersects(firstWord, twoWordSet, true);
        utilTestIntersects(secondWord, twoWordSet, true);
    }


    /**
     * Insert the method's description here.
     * Creation date: (26.07.01 8:35:06)
     */
    public void testIsSubsetOf() {
        assertEquals(true, emptySet.isSubsetOf(emptySet));
        assertEquals(true, emptySet.isSubsetOf(firstWord));
        assertEquals(true, emptySet.isSubsetOf(secondWord));
        assertEquals(true, emptySet.isSubsetOf(twoWordSet));
        assertEquals(true, firstWord.isSubsetOf(twoWordSet));
        assertEquals(false, twoWordSet.isSubsetOf(firstWord));
        twoWordSet.remove(33);
        assertEquals(false, twoWordSet.isSubsetOf(secondWord));
        assertEquals(false, secondWord.isSubsetOf(twoWordSet));

    }


    public void testLength() {
        assertEquals(64, twoWordSet.length());
        assertEquals(0, emptySet.length());
        assertEquals(32, firstWord.length());
        assertEquals(64, secondWord.length());
    }


    /**
     * Insert the method's description here.
     * Creation date: (10.07.01 8:17:29)
     */
    public void testLexCompare() {
/*	assertEquals(1, secondWord.lexCompareGanter(firstWord));
	assertEquals(0, secondWord.lexCompareGanter(secondWord));
	assertEquals(-1, firstWord.lexCompareGanter(secondWord));
	assertEquals(1, secondWord.lexCompareGanter(emptySet));*/

        assertEquals(1, SetBuilder.makeSet(new int[]{0, 1, 0}).lexCompareGanter(SetBuilder.makeSet(new int[]{0, 0, 1})));
        assertEquals(-1, SetBuilder.makeSet(new int[]{0, 0, 1}).lexCompareGanter(SetBuilder.makeSet(new int[]{0, 1, 0})));
        assertEquals(1, SetBuilder.makeSet(new int[]{0, 1, 1}).lexCompareGanter(SetBuilder.makeSet(new int[]{0, 1, 0})));
        assertEquals(1, SetBuilder.makeSet(new int[]{0, 0, 1}).lexCompareGanter(SetBuilder.makeSet(new int[]{0, 0, 0})));

        assertEquals(1, SetBuilder.makeSet(new int[]{1, 0, 1, 1, 0}).lexCompareGanter(SetBuilder.makeSet(new int[]{1, 0, 0, 1, 1})));
    }


    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:44:36)
     */
    public void testNextIn() {
        assertEquals(-1, emptySet.nextIn(-1));
        assertEquals(-1, emptySet.nextIn(0));
        assertEquals(-1, emptySet.nextIn(2));

        assertEquals(-1, firstWord.nextIn(-1));
        assertEquals(1, firstWord.nextIn(0));
        assertEquals(31, firstWord.nextIn(30));
        assertEquals(-1, firstWord.nextIn(31));

        twoWordSet = new BitSet(64);
        twoWordSet.put(63);
        assertEquals(63, twoWordSet.nextIn(0));
        twoWordSet.put(32);
        assertEquals(32, twoWordSet.nextIn(0));
        assertEquals(63, twoWordSet.nextIn(32));

    }


    /**
     * Insert the method's description here.
     * Creation date: (09.03.01 22:44:36)
     */
    public void testNextOut() {
        assertEquals(-1, emptySet.nextOut(-1));
        assertEquals(-1, emptySet.nextOut(0));
        assertEquals(-1, emptySet.nextOut(2));

        assertEquals(-1, firstWord.nextOut(-1));
        assertEquals(32, firstWord.nextOut(0));

        assertEquals(-1, secondWord.nextOut(-1));
        assertEquals(1, secondWord.nextOut(0));
        assertEquals(2, secondWord.nextOut(1));
        assertEquals(-1, secondWord.nextOut(62));
        assertEquals(-1, secondWord.nextOut(63));


    }


    public void testOr() {
        temp.or(twoWordSet);
        assertEquals(twoWordSet, temp);

        emptySet.or(firstWord);
        assertEquals(firstWord, emptySet);

        firstWord.or(secondWord);
        assertEquals(64, twoWordSet.elementCount());
    }


    public void testOut() {
        for (int i = 0; i < 64; i++) {
            assertEquals(false, twoWordSet.out(i));
            assertEquals(true, emptySet.out(i));
        }
        for (int i = 0; i < 32; i++) {
            assertEquals(false, firstWord.out(i));
            assertEquals(true, secondWord.out(i));
        }
        for (int i = 33; i < 64; i++) {
            assertEquals(true, firstWord.out(i));
            assertEquals(false, secondWord.out(i));
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (13.07.01 8:47:41)
     */
    public void testOutUpperBound() {
        //specially more then size of long;
        temp = new BitSet64(64);
        assertEquals(64, temp.outUpperBound());
        for (int j = temp.size(); --j >= 0;) {
            temp.put(j);
            assertEquals(j, temp.outUpperBound());
        }
    }


    public void testPutAndRemove() {
        emptySet = new BitSet64(64);
        temp.remove(63);
        assertEquals(temp, emptySet);
        temp.put(63);
        assertTrue(temp.in(63));
        temp.remove(63);
        assertEquals(temp, emptySet);
        twoWordSet.put(32);
        assertTrue(twoWordSet.in(32));
    }


    /**
     * Insert the method's description here.
     * Creation date: (22.07.01 0:06:16)
     */
    public void testResize() {
        emptySet.resize(32);
        assertEquals(32, emptySet.size());
        try {
            emptySet.resize(65);
            fail("Should throw");
        } catch (IndexOutOfBoundsException ex) {
            assertTrue("All is ok", true);
        }
    }


    /**
     * Insert the method's description here.
     * Creation date: (20.10.00 21:00:22)
     */
    public void testSize() {
        assertEquals(64, firstWord.size());
        assertEquals(64, secondWord.size());
        temp.copy(firstWord);
        assertEquals(64, temp.size());
        assertEquals(64, twoWordSet.size());
    }


    protected void utilClearSet(ModifiableSet toTest) {
        toTest.clearSet();
        assertEquals(temp, toTest);
        assertTrue(toTest.isEmpty());
    }


    protected void utilTestClone(ModifiableSet toTest) {
        temp = (BitSet64) toTest.clone();
        assertEquals(temp, toTest);
        assertEquals(temp.size(), toTest.size());
        if (!toTest.isEmpty()) {
            toTest.clearSet();
            assertEquals(false, temp.equals(toTest));
            assertEquals(temp.size(), toTest.size());
        } // end of if ()

    }


    protected void utilTestElemCount(ModifiableSet toTest) {
        int elBefore = toTest.elementCount();
        if (!toTest.in(1)) {
            toTest.put(1);
            assertEquals(elBefore + 1, toTest.elementCount());
        } else {
            toTest.remove(1);
            assertEquals(elBefore - 1, toTest.elementCount());
        }
        elBefore = toTest.elementCount();
        if (!toTest.in(63)) {
            toTest.put(63);
            assertEquals(elBefore + 1, toTest.elementCount());
        } else {
            toTest.remove(63);
            assertEquals(elBefore - 1, toTest.elementCount());
        }
    }

    public void testLengthSizeInteraction(){
        temp = new BitSet64(3);
        temp.fillByOne(3);
        assertEquals(3, temp.size());
        assertEquals(3, temp.length());
        temp.resize(2);
        assertEquals(2, temp.size());
        assertEquals(2, temp.length());
    }

    protected void utilTestIntersects(Set one, Set two, boolean expected) {
        assertEquals(expected, one.intersects(two));
        assertEquals(expected, two.intersects(one));
    }
}