/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

public class SQLTypeInfo {
	
	private int typeInt;
	private String typeName;
	
	public SQLTypeInfo (int typeInt, String typeName) {
		this.typeInt = typeInt;
		this.typeName = typeName;		
	}
	
	public int getTypeInt() {
		return typeInt;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public String toString() {
		String str = "SQLTypeInfo: SQL type int = " + typeInt + ", name = " + typeName;
		return str;
	}

}
