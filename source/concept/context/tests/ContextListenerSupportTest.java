package concept.context.tests;

import com.mockobjects.util.TestCaseMo;
import concept.context.ContextListenerSupport;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test case for ContextTest
 */

public class ContextListenerSupportTest extends TestCaseMo {
    private static final Class THIS = ContextListenerSupportTest.class;

    public ContextListenerSupportTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        start(new String[]{THIS.getName()});
    }


    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testMarkStructureChange() {
        ContextListenerSupport contextListenerSupport = new ContextListenerSupport(null);
        assertEquals(false, contextListenerSupport.hasStructureChangePostponed());
        contextListenerSupport.madePostponedStructureChange();
        assertEquals(true, contextListenerSupport.hasStructureChangePostponed());
        contextListenerSupport.fireContextStructureChanged();
        assertEquals(false, contextListenerSupport.hasStructureChangePostponed());

    }
}