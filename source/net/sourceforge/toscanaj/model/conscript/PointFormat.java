package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class PointFormat {
	
	public static final class FillStyle{
		protected FillStyle() { };
	}
	
	public static final FillStyle EMPTY = new FillStyle();
	public static final FillStyle FULL = new FillStyle();
	public static final FillStyle LOWER = new FillStyle();
	public static final FillStyle UPPER = new FillStyle();
	
	private TypedSize radius;
	private LineFormat outlineFormat;
	private FillStyle fillStyle;
	private String fillColorName;
	
	public PointFormat(TypedSize radius, LineFormat outlineFormat, FillStyle fillStyle, String fillColorName) {
		this.radius = radius;
		this.outlineFormat = outlineFormat;
		this.fillStyle = fillStyle;
		this.fillColorName = fillColorName;
	}
	
	/**
	 * Returns the fillColorName.
	 * @return String
	 */
	public String getFillColorName() {
		return fillColorName;
	}

	/**
	 * Returns the fillStyle.
	 * @return FillStyle
	 */
	public FillStyle getFillStyle() {
		return fillStyle;
	}

	/**
	 * Returns the outlineFormat.
	 * @return LineFormat
	 */
	public LineFormat getOutlineFormat() {
		return outlineFormat;
	}

	/**
	 * Returns the radius.
	 * @return TypedSize
	 */
	public TypedSize getRadius() {
		return radius;
	}

	/**
	 * Sets the fillColorName.
	 * @param fillColorName The fillColorName to set
	 */
	public void setFillColorName(String fillColorName) {
		this.fillColorName = fillColorName;
	}

	/**
	 * Sets the fillStyle.
	 * @param fillStyle The fillStyle to set
	 */
	public void setFillStyle(FillStyle fillStyle) {
		this.fillStyle = fillStyle;
	}

	/**
	 * Sets the outlineFormat.
	 * @param outlineFormat The outlineFormat to set
	 */
	public void setOutlineFormat(LineFormat outlineFormat) {
		this.outlineFormat = outlineFormat;
	}

	/**
	 * Sets the radius.
	 * @param radius The radius to set
	 */
	public void setRadius(TypedSize radius) {
		this.radius = radius;
	}

}
