/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato.tests;

import net.sourceforge.toscanaj.model.cernato.*;
import net.sourceforge.toscanaj.controller.cernato.LayoutOperations;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.util.Vector;

public class LayoutOperationsTest extends TestCase {
    final static Class THIS = LayoutOperationsTest.class;
    static CernatoModel model = new CernatoModel();

    static {
        NumericalType numType1 = new NumericalType("numtype1");

        NumericalValueGroup numGroup1 = new NumericalValueGroup(numType1, "num1", "num1", 0, true, 3, true);
        NumericalValueGroup numGroup2 = new NumericalValueGroup(numType1, "num2", "num2", 0, true, 3, false);
        NumericalValueGroup numGroup3 = new NumericalValueGroup(numType1, "num3", "num3", 0, false, 3, false);
        NumericalValueGroup numGroup4 = new NumericalValueGroup(numType1, "num4", "num4", 1, true, 2, true);
        NumericalValueGroup numGroup5 = new NumericalValueGroup(numType1, "num5", "num5", 1, true, 2, true);
        NumericalValueGroup numGroup6 = new NumericalValueGroup(numType1, "num6", "num6", 0, false, 3, true);
        NumericalValueGroup numGroup7 = new NumericalValueGroup(numType1, "num7", "num7", 3, true, 5, true);
        NumericalValueGroup numGroup8 = new NumericalValueGroup(numType1, "num8", "num8", 3, true, 3, true);

        TextualType textType1 = new TextualType("texttype1");
        TextualType textType2 = new TextualType("texttype2");

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

        model.getTypes().add(numType1);
        model.getTypes().add(textType1);
        model.getTypes().add(textType2);

        ManyValuedContext context = model.getContext();

        Property property1 = new Property(numType1, "numProp1");
        Property property2 = new Property(numType1, "numProp2");
        Property property3 = new Property(textType1, "textProp1");
        Property property4 = new Property(textType1, "textProp2");
        Property property5 = new Property(textType2, "textProp3");
        Property property6 = new Property(textType2, "textProp4");

        context.add(property1);
        context.add(property2);
        context.add(property3);
        context.add(property4);
        context.add(property5);
        context.add(property6);

        FCAObject object1 = new FCAObject("object1");
        FCAObject object2 = new FCAObject("object2");
        FCAObject object3 = new FCAObject("object3");
        FCAObject object4 = new FCAObject("object4");
        FCAObject object5 = new FCAObject("object5");
        FCAObject object6 = new FCAObject("object6");
        FCAObject object7 = new FCAObject("object7");

        context.add(object1);
        context.add(object2);
        context.add(object3);
        context.add(object4);
        context.add(object5);
        context.add(object6);
        context.add(object7);

        context.setRelationship(object1, property1, new NumericalValue(0));
        context.setRelationship(object1, property2, new NumericalValue(1));
        context.setRelationship(object1, property3, new TextualValue("one"));
        context.setRelationship(object1, property4, new TextualValue("two"));
        context.setRelationship(object1, property5, new TextualValue("one"));
        context.setRelationship(object1, property6, new TextualValue("two"));

        context.setRelationship(object2, property1, new NumericalValue(0.5));
        context.setRelationship(object2, property2, new NumericalValue(1.5));
        context.setRelationship(object2, property3, new TextualValue("two"));
        context.setRelationship(object2, property4, new TextualValue("two"));
        context.setRelationship(object2, property5, new TextualValue("one"));
        context.setRelationship(object2, property6, new TextualValue("two"));

        context.setRelationship(object3, property1, new NumericalValue(1));
        context.setRelationship(object3, property2, new NumericalValue(2));
        context.setRelationship(object3, property3, new TextualValue("one"));
        context.setRelationship(object3, property4, new TextualValue("two"));
        context.setRelationship(object3, property5, new TextualValue("two"));
        context.setRelationship(object3, property6, new TextualValue("two"));

        context.setRelationship(object4, property1, new NumericalValue(1.5));
        context.setRelationship(object4, property2, new NumericalValue(2.5));
        context.setRelationship(object4, property3, new TextualValue("one"));
        context.setRelationship(object4, property4, new TextualValue("one"));
        context.setRelationship(object4, property5, new TextualValue("one"));
        context.setRelationship(object4, property6, new TextualValue("two"));

        context.setRelationship(object5, property1, new NumericalValue(2.5));
        context.setRelationship(object5, property2, new NumericalValue(0.5));
        context.setRelationship(object5, property3, new TextualValue("one"));
        context.setRelationship(object5, property4, new TextualValue("two"));
        context.setRelationship(object5, property5, new TextualValue("one"));
        context.setRelationship(object5, property6, new TextualValue("one"));

        context.setRelationship(object6, property1, new NumericalValue(1));
        context.setRelationship(object6, property2, new NumericalValue(0));
        context.setRelationship(object6, property3, new TextualValue("one"));
        context.setRelationship(object6, property4, new TextualValue("one"));
        context.setRelationship(object6, property5, new TextualValue("two"));
        context.setRelationship(object6, property6, new TextualValue("two"));

        context.setRelationship(object7, property1, new NumericalValue(1));
        context.setRelationship(object7, property2, new NumericalValue(1));
        context.setRelationship(object7, property3, new TextualValue("two"));
        context.setRelationship(object7, property4, new TextualValue("one"));
        context.setRelationship(object7, property5, new TextualValue("one"));
        context.setRelationship(object7, property6, new TextualValue("two"));

        Vector views = model.getViews();

        View view1 = new View("view2");
        view1.addCriterion(new Criterion(property1, numGroup1));
        view1.addCriterion(new Criterion(property1, numGroup2));
        view1.addCriterion(new Criterion(property1, numGroup3));
        view1.addCriterion(new Criterion(property1, numGroup4));
        view1.addCriterion(new Criterion(property1, numGroup5));
        view1.addCriterion(new Criterion(property1, numGroup6));
        view1.addCriterion(new Criterion(property1, numGroup7));
        view1.addCriterion(new Criterion(property1, numGroup8));

        View view2 = new View("view2");
        view2.addCriterion(new Criterion(property1, numGroup1));
        view2.addCriterion(new Criterion(property1, numGroup2));
        view2.addCriterion(new Criterion(property1, numGroup3));
        view2.addCriterion(new Criterion(property1, numGroup4));
        view2.addCriterion(new Criterion(property2, numGroup1));
        view2.addCriterion(new Criterion(property2, numGroup2));
        view2.addCriterion(new Criterion(property2, numGroup3));
        view2.addCriterion(new Criterion(property2, numGroup4));

        View view3 = new View("view3");
        view3.addCriterion(new Criterion(property3, textGroup1));
        view3.addCriterion(new Criterion(property3, textGroup2));
        view3.addCriterion(new Criterion(property3, textGroup3));
        view3.addCriterion(new Criterion(property3, textGroup4));
        view3.addCriterion(new Criterion(property4, textGroup1));
        view3.addCriterion(new Criterion(property4, textGroup2));
        view3.addCriterion(new Criterion(property4, textGroup3));
        view3.addCriterion(new Criterion(property4, textGroup4));
        view3.addCriterion(new Criterion(property5, textGroup5));
        view3.addCriterion(new Criterion(property6, textGroup5));

        views.add(view1);
        views.add(view2);
        views.add(view3);
    }

    public LayoutOperationsTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testDimensionCalculation() {
        Vector dimensions = LayoutOperations.calculateDimensions(model);
        assertEquals(18, dimensions.size());
    }
}
