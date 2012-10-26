/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.view.database.SQLTypeMapper;

import org.tockit.events.EventBroker;

public class DumpSqlScript {
    public static void dumpSqlScript(final DatabaseConnection connection,
            final OutputStream outStream) throws DatabaseException {
        final PrintStream out = new PrintStream(outStream);
        if (!connection.isConnected()) {
            throw new RuntimeException("Database not connected");
        }
        final Iterator<String> tableNamesIt = connection.getTableNames()
                .iterator();
        while (tableNamesIt.hasNext()) {
            final String tableName = tableNamesIt.next();
            out.println("CREATE TABLE " + Table.getQuotedIdentifier(tableName)
                    + " (");
            final Table table = new Table(new EventBroker(), tableName, false);

            final List<Column> columns = connection.getColumns(table);
            final Iterator<Column> columnIt = columns.iterator();
            while (columnIt.hasNext()) {
                final Column column = columnIt.next();
                final String typeName = SQLTypeMapper.getSQLName(column
                        .getType());
                out.print("  " + column.getSqlExpression() + " " + typeName);
                if (columnIt.hasNext()) {
                    out.println(",");
                } else {
                    out.println();
                }
            }
            out.println(");");
            out.println();

            for (String[] rowResults : connection.executeQuery(
                    "SELECT * FROM " + Table.getQuotedIdentifier(tableName)
                            + ";")) {
                out.print("INSERT INTO " + Table.getQuotedIdentifier(tableName)
                        + " VALUES (");

                for (int i = 0; i < rowResults.length; i++) {
                    if (i != 0) {
                        out.print(",");
                    }
                    final String result = rowResults[i];
                    if (result == null) {
                        out.print("NULL");
                    } else {
                        try {
                            Double.parseDouble(result);
                            // Double parses, so we assume that must be a number
                            out.print(result);
                        } catch (final NumberFormatException e) {
                            // doesn't seem to be a number, better put it in
                            // quotes
                            out.print(getQuotedValueString(result));
                        }
                    }
                }
                out.println(");");
            }

            if (tableNamesIt.hasNext()) {
                out.println();
                out.println();
            }
        }
    }

    private static String getQuotedValueString(final String value) {
        String retVal = "'";
        for (int i = 0; i < value.length(); i++) {
            final char curChar = value.charAt(i);
            if (curChar == '\'') {
                retVal += "\'\'";
            } else {
                retVal += curChar;
            }
        }
        retVal += "'";
        return retVal;
    }
}
