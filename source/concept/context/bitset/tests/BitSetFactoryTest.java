package concept.context.bitset.tests;

import concept.context.BinaryRelation;
import concept.context.Set;
import concept.context.bitset.BitSetFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * BitSetFactoryTest.java
 *
 *
 * Created: Mon Jul 24 23:10:15 2000
 *
 * @author Sergey Yevtushenko
 * @version
 */


public class BitSetFactoryTest extends TestCase {

    private BitSetFactory factory;

    public BitSetFactoryTest(String name) {
        super(name);
    }

    protected void setUp() {
        factory = new BitSetFactory();
    }

    public static Test suite() {
        return new TestSuite(BitSetFactoryTest.class);
    }

    public void testRelation() {
        try {
            factory.createRelation(-1, 2);
            fail("Created relation with negative size");
        } catch (IndexOutOfBoundsException exc) {
        }

        try {
            factory.createRelation(2, -1);
            fail("Created set with negative size");
        } catch (IndexOutOfBoundsException exc) {
        }
        try {
            factory.createRelation(0, 1);
        } catch (IndexOutOfBoundsException exc) {
            assertTrue(true);
        }

        try {
            factory.createRelation(1, 0);
        } catch (IndexOutOfBoundsException exc) {
            assertTrue(true);
        }

        BinaryRelation tmp = factory.createRelation(1, 2);
        assertNotNull(tmp);

    }

    public void testSet() {
        try {
            factory.createSet(-1);
            fail("Created set with negative size");
        } catch (IndexOutOfBoundsException exc) {

        }
        Set temp = factory.createSet(0);
        assertNotNull("Failed to create set with zero length", temp);

        temp = factory.createSet(20);
        assertNotNull("Failed to create set with nonzero length", temp);
    }


}