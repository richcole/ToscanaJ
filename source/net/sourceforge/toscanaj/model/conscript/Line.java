package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class Line {
	private Point from;
	private Point to;
	private PointFormat format;
	
	public Line(Point from, Point to, PointFormat format) {
		this.from = from;
		this.to = to;
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
	 * Returns the from.
	 * @return Point
	 */
	public Point getFrom() {
		return from;
	}

	/**
	 * Returns the to.
	 * @return Point
	 */
	public Point getTo() {
		return to;
	}

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	public void setFormat(PointFormat format) {
		this.format = format;
	}

	/**
	 * Sets the from.
	 * @param from The from to set
	 */
	public void setFrom(Point from) {
		this.from = from;
	}

	/**
	 * Sets the to.
	 * @param to The to to set
	 */
	public void setTo(Point to) {
		this.to = to;
	}

}
