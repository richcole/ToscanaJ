package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class DatabaseDefinition {
	private String name;
	private String table;
	private String primaryKey;
	
	public DatabaseDefinition(String name, String table, String primaryKey) {
		this.name = name;
		this.table = table;
		this.primaryKey = primaryKey;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the primaryKey.
	 * @return String
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Returns the table.
	 * @return String
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the primaryKey.
	 * @param primaryKey The primaryKey to set
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Sets the table.
	 * @param table The table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
}
