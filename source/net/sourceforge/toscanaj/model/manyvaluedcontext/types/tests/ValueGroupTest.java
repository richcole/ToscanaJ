/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.*;

public class ValueGroupTest extends TestCase {
    final static Class THIS = ValueGroupTest.class;

    public ValueGroupTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testPartialOrder() {
        NumericalType numType1 = new NumericalType("numtype1");
        NumericalType numType2 = new NumericalType("numtype2");
        TextualType textType1 = new TextualType("texttype1");
        TextualType textType2 = new TextualType("texttype2");

        NumericalValueGroup numGroup1 = new NumericalValueGroup(numType1, "num1", "num1", 0, true, 3, true);
        NumericalValueGroup numGroup2 = new NumericalValueGroup(numType1, "num2", "num2", 0, true, 3, false);
        NumericalValueGroup numGroup3 = new NumericalValueGroup(numType1, "num3", "num3", 0, false, 3, false);
        NumericalValueGroup numGroup4 = new NumericalValueGroup(numType1, "num4", "num4", 1, true, 2, true);
        NumericalValueGroup numGroup5 = new NumericalValueGroup(numType2, "num5", "num5", 1, true, 2, true);
        NumericalValueGroup numGroup6 = new NumericalValueGroup(numType1, "num6", "num6", 0, false, 3, true);

        assertEquals(numGroup1.isSuperSetOf(numGroup1), true);
        assertEquals(numGroup1.isSuperSetOf(numGroup2), true);
        assertEquals(numGroup1.isSuperSetOf(numGroup3), true);
        assertEquals(numGroup1.isSuperSetOf(numGroup4), true);
        assertEquals(numGroup1.isSuperSetOf(numGroup5), false);
        assertEquals(numGroup1.isSuperSetOf(numGroup6), true);

        assertEquals(numGroup2.isSuperSetOf(numGroup1), false);
        assertEquals(numGroup2.isSuperSetOf(numGroup2), true);
        assertEquals(numGroup2.isSuperSetOf(numGroup3), true);
        assertEquals(numGroup2.isSuperSetOf(numGroup4), true);
        assertEquals(numGroup2.isSuperSetOf(numGroup5), false);
        assertEquals(numGroup2.isSuperSetOf(numGroup6), false);

        assertEquals(numGroup3.isSuperSetOf(numGroup1), false);
        assertEquals(numGroup3.isSuperSetOf(numGroup2), false);
        assertEquals(numGroup3.isSuperSetOf(numGroup3), true);
        assertEquals(numGroup3.isSuperSetOf(numGroup4), true);
        assertEquals(numGroup3.isSuperSetOf(numGroup5), false);
        assertEquals(numGroup3.isSuperSetOf(numGroup6), false);

        assertEquals(numGroup4.isSuperSetOf(numGroup1), false);
        assertEquals(numGroup4.isSuperSetOf(numGroup2), false);
        assertEquals(numGroup4.isSuperSetOf(numGroup3), false);
        assertEquals(numGroup4.isSuperSetOf(numGroup4), true);
        assertEquals(numGroup4.isSuperSetOf(numGroup5), false);
        assertEquals(numGroup4.isSuperSetOf(numGroup6), false);

        assertEquals(numGroup5.isSuperSetOf(numGroup1), false);
        assertEquals(numGroup5.isSuperSetOf(numGroup2), false);
        assertEquals(numGroup5.isSuperSetOf(numGroup3), false);
        assertEquals(numGroup5.isSuperSetOf(numGroup4), false);
        assertEquals(numGroup5.isSuperSetOf(numGroup5), true);
        assertEquals(numGroup5.isSuperSetOf(numGroup6), false);

        assertEquals(numGroup6.isSuperSetOf(numGroup1), false);
        assertEquals(numGroup6.isSuperSetOf(numGroup2), false);
        assertEquals(numGroup6.isSuperSetOf(numGroup3), true);
        assertEquals(numGroup6.isSuperSetOf(numGroup4), true);
        assertEquals(numGroup6.isSuperSetOf(numGroup5), false);
        assertEquals(numGroup6.isSuperSetOf(numGroup6), true);

        TextualValueGroup textGroup1 = new TextualValueGroup(textType1, "text1", "text1");
        textGroup1.addValue(new TextualValue("one"));
        textGroup1.addValue(new TextualValue("two"));
        TextualValueGroup textGroup2 = new TextualValueGroup(textType1, "text2", "text2");
        textGroup2.addValue(new TextualValue("one"));
        TextualValueGroup textGroup3 = new TextualValueGroup(textType1, "text3", "text3");
        textGroup3.addValue(new TextualValue("two"));
        TextualValueGroup textGroup4 = new TextualValueGroup(textType1, "text4", "text4");
        TextualValueGroup textGroup5 = new TextualValueGroup(textType2, "text5", "text5");
        textGroup5.addValue(new TextualValue("two"));

        assertEquals(textGroup1.isSuperSetOf(textGroup1), true);
        assertEquals(textGroup1.isSuperSetOf(textGroup2), true);
        assertEquals(textGroup1.isSuperSetOf(textGroup3), true);
        assertEquals(textGroup1.isSuperSetOf(textGroup4), true);
        assertEquals(textGroup1.isSuperSetOf(textGroup5), false);

        assertEquals(textGroup2.isSuperSetOf(textGroup1), false);
        assertEquals(textGroup2.isSuperSetOf(textGroup2), true);
        assertEquals(textGroup2.isSuperSetOf(textGroup3), false);
        assertEquals(textGroup2.isSuperSetOf(textGroup4), true);
        assertEquals(textGroup2.isSuperSetOf(textGroup5), false);

        assertEquals(textGroup3.isSuperSetOf(textGroup1), false);
        assertEquals(textGroup3.isSuperSetOf(textGroup2), false);
        assertEquals(textGroup3.isSuperSetOf(textGroup3), true);
        assertEquals(textGroup3.isSuperSetOf(textGroup4), true);
        assertEquals(textGroup3.isSuperSetOf(textGroup5), false);

        assertEquals(textGroup4.isSuperSetOf(textGroup1), false);
        assertEquals(textGroup4.isSuperSetOf(textGroup2), false);
        assertEquals(textGroup4.isSuperSetOf(textGroup3), false);
        assertEquals(textGroup4.isSuperSetOf(textGroup4), true);
        assertEquals(textGroup4.isSuperSetOf(textGroup5), false);

        assertEquals(textGroup5.isSuperSetOf(textGroup1), false);
        assertEquals(textGroup5.isSuperSetOf(textGroup2), false);
        assertEquals(textGroup5.isSuperSetOf(textGroup3), false);
        assertEquals(textGroup5.isSuperSetOf(textGroup4), false);
        assertEquals(textGroup5.isSuperSetOf(textGroup5), true);
    }
}
