/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.util.CollectionFactory;

import java.util.List;

public class OrdinalScaleEditorDialogTest extends TestCase {
    public OrdinalScaleEditorDialogTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(OrdinalScaleEditorDialogTest.class);
    }

    private List makeListOfDoubles(double[] expValuesArr) {
        List expValues = CollectionFactory.createDefaultList();
        for (int i = 0; i < expValuesArr.length; i++) {
            expValues.add(new Double(expValuesArr[i]));
        }
        return expValues;
    }
}
