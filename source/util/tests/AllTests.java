package util.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    public AllTests(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(StringUtilTest.suite());
        suite.addTest(FileNameManglerTest.suite());
        suite.addTest(IntArrayWrapperTest.suite());
        suite.addTest(ReverseListIteratorTest.suite());
        return suite;
    }
}