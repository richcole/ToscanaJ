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
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.tests.MockDBConnection;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.util.CollectionFactory;
import net.sourceforge.toscanaj.view.scales.OrdinalScaleEditorDialog;
import org.tockit.events.EventBroker;

import java.util.List;

public class OrdinalScaleEditorDialogTest extends TestCase {
    public OrdinalScaleEditorDialogTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(OrdinalScaleEditorDialogTest.class);
    }

    public void testAddDivider() throws DatabaseException {
        Table table = new Table(new EventBroker(), "Table");
        Column column = new Column("Column", java.sql.Types.DOUBLE, table);
        table.addColumn(column);
        OrdinalScaleEditorDialog dialog = new OrdinalScaleEditorDialog(
                null, new DatabaseSchema(new EventBroker()), new MockDBConnection()
        );
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
