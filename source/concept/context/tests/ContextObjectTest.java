package concept.context.tests;

import com.mockobjects.ExpectationCounter;
import com.mockobjects.util.TestCaseMo;
import concept.context.ContextObject;
import concept.context.DefaultContextObjectListener;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.beans.PropertyChangeEvent;

/**
 * JUnit test case for ContextTest
 */

public class ContextObjectTest extends TestCaseMo {
    private static final Class THIS = ContextObjectTest.class;

    public ContextObjectTest(String name) {
        super(name);
    }


    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testContextObjectNameListener() {
        ContextObject attribute = new ContextObject("One");
        final ExpectationCounter counter = new ExpectationCounter("Expected calls");
        DefaultContextObjectListener mockListener = new DefaultContextObjectListener() {
            public void nameChanged(PropertyChangeEvent evt) {
                counter.inc();
            }
        };
        counter.setExpected(1);
        attribute.setContextObjectListener(mockListener);
        attribute.setName("Two");
        counter.verify();
        counter.setExpected(0);
        attribute.setName("Two");
        counter.verify();
    }

    public void testEquals(){
        final String firstName = "One";
        final String secondName = "Two";
        ContextObject attr1 = new ContextObject(firstName, false);
        ContextObject attr2 = new ContextObject(firstName, false);
        assertEquals(attr1, attr2);



        assertEquals(false, attr1.equals(new Object()));

        assertEquals(false, attr1.equals(null));

        attr2.setName(secondName);
        assertEquals(false, attr1.equals(attr2));

        ContextObject obj = new ContextObject(firstName, true);
        assertEquals(false, attr1.equals(obj));


        attr2 = new ContextObject(new String(firstName), false);
        assertEquals(attr1, attr2);
    }

}