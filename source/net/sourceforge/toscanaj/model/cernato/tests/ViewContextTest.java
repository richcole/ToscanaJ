/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato.tests;

import junit.framework.*;
import net.sourceforge.toscanaj.model.cernato.*;
import net.sourceforge.toscanaj.model.Context;

import java.util.Collection;
import java.util.Iterator;

public class ViewContextTest extends TestCase {
    final static Class THIS = ViewContextTest.class;

    public ViewContextTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testScaling() {
        checkView(TestData.View1);
        checkView(TestData.View2);
        checkView(TestData.View3);
    }

    private void checkView(View view) {
        Context scaledContext = new ViewContext(TestData.Model, view);
        Collection objects = scaledContext.getObjects();
        Collection attributes = scaledContext.getAttributes();
        for (Iterator it1 = objects.iterator(); it1.hasNext();) {
            FCAObject object = (FCAObject) it1.next();
            for (Iterator it2 = attributes.iterator(); it2.hasNext();) {
                Criterion criterion = (Criterion) it2.next();
                assertEquals(object.getName() + " x " + criterion.getProperty().getName() + ":" + criterion.getValueGroup().getName(),
                             TestData.isInScaledRelation(object, criterion),
                             scaledContext.getRelation().contains(object, criterion));
            }
        }
    }
}
