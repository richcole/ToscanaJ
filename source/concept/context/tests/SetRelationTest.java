package concept.context.tests;

import com.mockobjects.util.TestCaseMo;
import concept.context.BinaryRelation;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test case for SetRelationTest
 */

public class SetRelationTest extends TestCaseMo {
    private static final Class THIS = SetRelationTest.class;

    public SetRelationTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }


    public void testEquals() {
        BinaryRelation rel = SetBuilder.makeRelation(new int[][]{{1, 0, 0},
                                                                 {0, 1, 0}});
        BinaryRelation relEquals = SetBuilder.makeRelation(new int[][]{{1, 0, 0},
                                                                       {0, 1, 0}});

        BinaryRelation sameSizeNotEquals = SetBuilder.makeRelation(new int[][]{{1, 0, 1},
                                                                               {0, 1, 0}});

        BinaryRelation relNotEquals = SetBuilder.makeRelation(new int[][]{{1, 0, 0},
                                                                          {0, 1, 0},
                                                                          {1, 0, 0},
                                                                          {0, 1, 0}});

        assertEquals(false, rel.equals(null));
        assertEquals(false, rel.equals(relNotEquals));
        assertEquals(false, relNotEquals.equals(rel));
        assertEquals(true, rel.equals(rel));
        assertEquals(true, rel.equals(relEquals));
        assertEquals(true, relEquals.equals(rel));
        assertEquals(false, rel.equals(sameSizeNotEquals));
        assertEquals(false, sameSizeNotEquals.equals(rel));

    }

    public void testSetDimension() {
        SetBuilder.makeRelation(new int[0][0]);
    }
}