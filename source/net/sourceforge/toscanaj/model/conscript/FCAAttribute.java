package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class FCAAttribute {
	private long number;
	private String identifier;
	private FormattedString description;
	
	public FCAAttribute(long number, String identifier, FormattedString description) {
		this.number = number;
		this.identifier = identifier;
		this.description = description;
	}
	
	/**
	 * Returns the description.
	 * @return FormattedString
	 */
	public FormattedString getDescription() {
		return description;
	}

	/**
	 * Returns the identifier.
	 * @return String
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the number.
	 * @return long
	 */
	public long getNumber() {
		return number;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(FormattedString description) {
		this.description = description;
	}

	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Sets the number.
	 * @param number The number to set
	 */
	public void setNumber(long number) {
		this.number = number;
	}

}
