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
import java.util.Vector;

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.view.database.SQLTypeMapper;

/***
 * @todo add main method to use this from command line by supplying the DB
 *       connection  parameters (driver, URL, username, password) 
 */
public class DumpSqlScript {
	public static final void dumpSqlScript(DatabaseConnection connection, OutputStream outStream) throws DatabaseException {
		PrintStream out = new PrintStream(outStream);
		if(!connection.isConnected()) {
			throw new RuntimeException("Database not connected");
		}
		Iterator tableNamesIt = connection.getTableNames().iterator();
		while (tableNamesIt.hasNext()) {
            String tableName = (String) tableNamesIt.next();
            out.println("CREATE TABLE " + tableName + "(");
            Table table = new Table(new EventBroker(), tableName);
            
            Vector columns = connection.getColumns(table);
            Iterator columnIt = columns.iterator();
            while (columnIt.hasNext()) {
                Column column = (Column) columnIt.next();
                String typeName = SQLTypeMapper.getSQLName(column.getType());
                out.print("  " + column.getName() + " " + typeName);
                if(columnIt.hasNext()) {
                	out.println(",");
                } else {
                	out.println();
                }                
            }
		    out.println(");");
		    out.println();
		    
			Iterator rowIt = connection.executeQuery("SELECT * FROM " + tableName + ";").iterator();
			while (rowIt.hasNext()) {
                Vector rowResults = (Vector) rowIt.next();
                out.print("INSERT INTO " + tableName + " VALUES (");
                
                Iterator resultIt = rowResults.iterator();
                while (resultIt.hasNext()) {
                    String result = (String) resultIt.next();
                    if(result == null) {
                    	out.print("NULL");
                    } else {
                        try {
                            Double.parseDouble(result);
                            // Double parses, so we assume that must be a number
                            out.print(result);
                        } catch(NumberFormatException e) {
                            // doesn't seem to be a number, better put it in quotes
                            out.print("'" + getEscapedString(result) + "'");
                        }
                    }
                    if(resultIt.hasNext()) {
                    	out.print(",");
                    }
                }
                out.println(");");
            }		    
        }
	}
	
    private static String getEscapedString(String result) {
        String retVal = "";
        for(int i = 0; i < result.length(); i++) {
        	char curChar = result.charAt(i);
            if(curChar == '\'') {
        		retVal += "\'\'";
        	} else {
        		retVal += curChar;
        	}
        }
        return retVal;
    }
}
