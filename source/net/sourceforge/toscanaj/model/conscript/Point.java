package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class Point {
	private long number;
	private double x;
	private double y;
	private FormattedString label;
	private PointFormat format;
	
	public Point(long number, double x, double y, FormattedString label, PointFormat format) {
		this.number = number;
		this.x = x;
		this.y = y;
		this.label = label;
		this.format = format;
	}
		
	/**
	 * Returns the format.
	 * @return PointFormat
	 */
	public PointFormat getFormat() {
		return format;
	}

	/**
	 * Returns the label.
	 * @return FormattedString
	 */
	public FormattedString getLabel() {
		return label;
	}

	/**
	 * Returns the number.
	 * @return long
	 */
	public long getNumber() {
		return number;
	}

	/**
	 * Returns the x.
	 * @return double
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y.
	 * @return double
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	public void setFormat(PointFormat format) {
		this.format = format;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	public void setLabel(FormattedString label) {
		this.label = label;
	}

	/**
	 * Sets the number.
	 * @param number The number to set
	 */
	public void setNumber(long number) {
		this.number = number;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
}
