package util.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.ReverseListIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReverseListIteratorTest extends TestCase {
    private static final Class THIS = ReverseListIteratorTest.class;

    public ReverseListIteratorTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testReverseListIteratorOnEmptyList() {
        List list = new ArrayList();
        ReverseListIterator iter = new ReverseListIterator(list);
        assertEquals(false, iter.hasNext());
    }

    public void testOnOneElementList() {
        List list = new ArrayList();
        list.add("One");
        ReverseListIterator iter = new ReverseListIterator(list);
        assertEquals(true, iter.hasNext());
        assertEquals("One", iter.next());
    }

    public void testOnTwoElementList() {
        Object[] values = {"One", "Two"};
        List list = Arrays.asList(values);
        ReverseListIterator iter = new ReverseListIterator(list);
        assertEquals("Two", iter.next());
        assertEquals("One", iter.next());
    }

    public void testRemove() {
        Object[] values = {"One", "Two", "Three"};
        List list = Arrays.asList(values);
        ReverseListIterator iter = new ReverseListIterator(list);
        try {
            iter.next();
            iter.remove();
            fail("Expect UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }
}