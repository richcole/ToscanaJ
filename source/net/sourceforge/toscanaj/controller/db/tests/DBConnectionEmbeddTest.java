/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db.tests;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.events.tests.EventBrokerTest;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;

import java.net.URL;

public class DBConnectionEmbeddTest extends TestCase {

    public static Test suite() {
        return new TestSuite(DBConnectionEmbeddTest.class);
    }

    public DBConnectionEmbeddTest(String testName) {
        super(testName);
    };

    static String SQLCommand =
        "create table student (name VARCHAR(20), age INTEGER, height INTEGER);";

    public void testDBConnectionEmbedd1() {

        try {
            DatabaseInfo info = DatabaseInfo.getEmbeddedDatabaseInfo();

            DatabaseConnection connection = new DatabaseConnection(new EventBroker());

            connection.connect(info);
            connection.executeSQLAsString(SQLCommand, "EmbedDBConnectionTestSQL");

            assertEquals(1, connection.getTableNames().size());

        } catch (DatabaseException e) {
            fail("DB Exception: " + e.getMessage());
        }

    }

}
