package concept.context.tests;

import com.mockobjects.util.TestCaseMo;
import concept.context.BinaryRelation;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test case for BinaryRelationUtilsTest
 */

public class BinaryRelationUtilsTest extends TestCaseMo {
    private static final Class THIS = BinaryRelationUtilsTest.class;

    public BinaryRelationUtilsTest(String name) {
        super(name);
    }


    /**
     * Insert the method's description here.
     * Creation date: (04.08.01 8:36:57)
     */
    protected void doTestLexSort(int[][] relToSort, int[][] sortedRel) {
        BinaryRelation toSort = SetBuilder.makeRelation(relToSort);
        BinaryRelation sorted = SetBuilder.makeRelation(sortedRel);
        assertEquals(sorted, concept.context.BinaryRelationUtils.lexSort(toSort));
    }


    public static void main(String[] args) {
        start(new String[]{THIS.getName()});
    }


    public static Test suite() {
        return new TestSuite(THIS);
    }


    public void testLexSort() {
        int[][] relToSort = new int[][]{
            {1, 0, 0, 1},
            {1, 1, 0, 0},
            {0, 0, 0, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 1}
        };

        int[][] sortedRel = new int[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 1, 1, 0},
            {1, 0, 0, 1},
            {1, 1, 0, 0}
        };

        doTestLexSort(relToSort, sortedRel);
    }


    public void testOneColSort() {
        int[][] relToSort = new int[][]{
            {1},
            {1},
            {0},
            {0},
            {0}
        };

        int[][] sortedRel = new int[][]{
            {0},
            {0},
            {0},
            {1},
            {1}
        };

        doTestLexSort(relToSort, sortedRel);
    }
}