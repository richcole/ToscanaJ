package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class Concept {
	private Point point;
	private String identifier;
	private FormattedString description;
	
	public Concept(Point point, String identifier, FormattedString description){
		this.point = point;
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
	 * Returns the point.
	 * @return Point
	 */
	public Point getPoint() {
		return point;
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
	 * Sets the point.
	 * @param point The point to set
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

}
