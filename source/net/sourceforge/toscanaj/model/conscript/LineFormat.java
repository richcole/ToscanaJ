package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class LineFormat {
	public static final class LineStyle{
		protected LineStyle() { };
	}
	public static final LineStyle SOLID = new LineStyle();
	public static final LineStyle DASHED = new LineStyle();
	public static final LineStyle DOTTED = new LineStyle();
	
	private LineStyle style;
	private TypedSize width;
	private String colorName;
	
	public LineFormat(LineStyle style, TypedSize width, String colorName) {
		this.style = style;
		this.width = width;
		this.colorName = colorName;
	}
	
	/**
	 * Returns the colorName.
	 * @return String
	 */
	public String getColorName() {
		return colorName;
	}

	/**
	 * Returns the style.
	 * @return LineStyle
	 */
	public LineStyle getStyle() {
		return style;
	}

	/**
	 * Returns the width.
	 * @return TypedSize
	 */
	public TypedSize getWidth() {
		return width;
	}

	/**
	 * Sets the colorName.
	 * @param colorName The colorName to set
	 */
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	/**
	 * Sets the style.
	 * @param style The style to set
	 */
	public void setStyle(LineStyle style) {
		this.style = style;
	}

	/**
	 * Sets the width.
	 * @param width The width to set
	 */
	public void setWidth(TypedSize width) {
		this.width = width;
	}
}
