/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;

public class TableTest extends TestCase {
    final static Class THIS = TableTest.class;

    public TableTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testAddColumns() {
        Table table = new Table(new EventBroker(), "Table");
        table.addColumn(new Column("Second", java.sql.Types.INTEGER, table));

    }


}
