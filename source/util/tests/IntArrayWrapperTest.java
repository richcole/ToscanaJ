/*
 * Created by IntelliJ IDEA.
 * User: sergey
 * Date: Oct 20, 2001
 * Time: 5:03:19 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package util.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.IntArrayWrapper;


public class IntArrayWrapperTest extends TestCase {
    private static final Class THIS = IntArrayWrapperTest.class;

    public IntArrayWrapperTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }


    public void testEquals() {
        int[] arrayOne = new int[]{0};
        int[] arrayTwo = new int[]{0};

        IntArrayWrapper one = new IntArrayWrapper(arrayOne);
        IntArrayWrapper two = new IntArrayWrapper(arrayTwo);

        assertEquals(true, one.equals(two));

        one = new IntArrayWrapper(new int[0]);
        two = new IntArrayWrapper(new int[0]);
        assertEquals(one, two);

    }

    public void testEqualsWithNull() {
        IntArrayWrapper one = new IntArrayWrapper(new int[0]);

        assertEquals(false, one.equals(null));
    }

    public void testZeroLengthHashCode() {
        int[] one = new int[0];
        int[] two = new int[0];

        doTestHashCodeEquals(one, two, true);
    }


    public void testEqualsHashCode() {
        int[] one = new int[]{0, 1, 2};
        int[] two = new int[]{0, 1, 2};

        doTestHashCodeEquals(one, two, true);
    }

    public void testNotEqualsHashCode() {
        int[] one = new int[]{0, 1, 2};
        int[] two = new int[]{2, 1, 0};

        doTestHashCodeEquals(one, two, false);
    }


    private void doTestHashCodeEquals(int[] one, int[] two, boolean expRes) {
        IntArrayWrapper firstWrapper = new IntArrayWrapper(one);
        IntArrayWrapper secondWrapper = new IntArrayWrapper(two);
        assertEquals(expRes, firstWrapper.hashCode() == secondWrapper.hashCode());
    }
}
