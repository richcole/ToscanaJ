package concept.context.bitset.tests;

import com.mockobjects.util.SuiteBuilder;
import com.mockobjects.util.TestCaseMo;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test case for AllTestControllerDB
 */

public class AllTests extends TestCaseMo {
    private static final Class THIS = AllTests.class;

    public AllTests(String name) {
        super(name);
    }


    public static void addBitSet64Test(TestSuite suite) {
        suite.addTest(BitSet64Test.suite());
    }


    public static void addBitSetFactoryTest(TestSuite suite) {
        suite.addTest(BitSetFactoryTest.suite());
    }


    public static void addBitSetTest(TestSuite suite) {
        suite.addTest(BitSetTest.suite());
    }


    public static void main(String[] args) {
        start(new String[]{THIS.getName()});
    }


    public static Test suite() {
        return SuiteBuilder.buildTest(THIS);
    }
}