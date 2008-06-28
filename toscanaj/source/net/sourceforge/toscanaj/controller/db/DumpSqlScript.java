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

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.view.database.SQLTypeMapper;

public class DumpSqlScript {
	public static final void dumpSqlScript(DatabaseConnection connection, OutputStream outStream) throws DatabaseException {
		PrintStream out = new PrintStream(outStream);
		if(!connection.isConnected()) {
			throw new RuntimeException("Database not connected");
		}
		Iterator<String> tableNamesIt = connection.getTableNames().iterator();
		while (tableNamesIt.hasNext()) {
            String tableName = tableNamesIt.next();
            out.println("CREATE TABLE " + Table.getQuotedIdentifier(tableName) + " (");
            Table table = new Table(new EventBroker(), tableName, false);
            
            List<Column> columns = connection.getColumns(table);
            Iterator<Column> columnIt = columns.iterator();
            while (columnIt.hasNext()) {
                Column column = columnIt.next();
                String typeName = SQLTypeMapper.getSQLName(column.getType());
                out.print("  " + column.getSqlExpression() + " " + typeName);
                if(columnIt.hasNext()) {
                	out.println(",");
                } else {
                	out.println();
                }                
            }
		    out.println(");");
		    out.println();
		    
			Iterator<String[]> rowIt = connection.executeQuery("SELECT * FROM " + Table.getQuotedIdentifier(tableName) + ";").iterator();
			while (rowIt.hasNext()) {
                String[] rowResults = rowIt.next();
                out.print("INSERT INTO " + Table.getQuotedIdentifier(tableName) + " VALUES (");
                
                for (int i = 0; i < rowResults.length; i++) {
                    if(i != 0) {
                    	out.print(",");
                    }					
					String result = rowResults[i];
                    if(result == null) {
                    	out.print("NULL");
                    } else {
                        try {
                            Double.parseDouble(result);
                            // Double parses, so we assume that must be a number
                            out.print(result);
                        } catch(NumberFormatException e) {
                            // doesn't seem to be a number, better put it in quotes
                            out.print(getQuotedValueString(result));
                        }
                    }
				}
                out.println(");");
            }		    

            if(tableNamesIt.hasNext()) {
				out.println();
				out.println();
            }
        }
	}
	
	private static String getQuotedValueString(String value) {
		String retVal = "'";
		for(int i = 0; i < value.length(); i++) {
			char curChar = value.charAt(i);
			if(curChar == '\'') {
				retVal += "\'\'";
			} else {
				retVal += curChar;
			}
		}
		retVal += "'";
		return retVal;
	}
}
