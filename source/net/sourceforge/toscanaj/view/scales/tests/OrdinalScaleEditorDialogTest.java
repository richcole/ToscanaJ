/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.view.scales.OrdinalScaleEditorDialog;
import util.CollectionFactory;

import java.util.List;

public class OrdinalScaleEditorDialogTest extends TestCase {
    public OrdinalScaleEditorDialogTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(OrdinalScaleEditorDialogTest.class);
    }

    public void testAddDivider() {
        OrdinalScaleEditorDialog dialog = new OrdinalScaleEditorDialog(null,
                new Column("Column", java.sql.Types.INTEGER));
        dialog.addDelimiter(1.);

        List expValues = makeListOfDoubles(new double[]{1.});
        assertEquals(expValues, dialog.getDividers());
        dialog.addDelimiter(0.);
        expValues = makeListOfDoubles(new double[]{0., 1.});
        assertEquals(expValues, dialog.getDividers());


        dialog.addDelimiter(2.);
        assertEquals(makeListOfDoubles(new double[]{0., 1., 2.}), dialog.getDividers());

        dialog.addDelimiter(1.);
        assertEquals(makeListOfDoubles(new double[]{0., 1., 2}), dialog.getDividers());

        dialog.removeDivider(0);
        assertEquals(makeListOfDoubles(new double[]{1., 2}), dialog.getDividers());


        dialog.removeAllDividers();
        assertEquals(makeListOfDoubles(new double[0]), dialog.getDividers());

    }

    private List makeListOfDoubles(double[] expValuesArr) {
        List expValues = CollectionFactory.createDefaultList();
        for (int i = 0; i < expValuesArr.length; i++) {
            expValues.add(new Double(expValuesArr[i]));
        }
        return expValues;
    }
}
