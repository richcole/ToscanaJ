package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class TypedSize {

	private double value;
	private String type;

	public TypedSize(double value, String type) {
		this.value = value;
		this.type = type;
	}
		
	/**
	 * Returns the type.
	 * @return String
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns the value.
	 * @return double
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
