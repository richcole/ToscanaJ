package concept.context.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

    public AllTests(String name) {
        super(name);
    }


    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(ContextTest.suite());
        suite.addTest(ContextObjectTest.suite());
        suite.addTest(ContextListenerSupportTest.suite());
        suite.addTest(BinaryRelationUtilsTest.suite());
        suite.addTest(SetRelationTest.suite());

        suite.addTest(concept.context.bitset.tests.AllTests.suite());


        return suite;
    }
}