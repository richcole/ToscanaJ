package net.sourceforge.toscanaj.model.conscript;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class StringFormat {

	public static final class HorizontalAlign {
		protected HorizontalAlign() {};
	}
	public static final class VerticalAlign {
		protected VerticalAlign(){};
	}
	public static final class FontStyle{
		protected FontStyle() {};
	}
	
	public static final HorizontalAlign LEFT = new HorizontalAlign();
	public static final HorizontalAlign H_CENTER= new HorizontalAlign();
	public static final HorizontalAlign RIGHT= new HorizontalAlign();

	public static final VerticalAlign BOTTOM = new VerticalAlign();
	public static final VerticalAlign V_CENTER= new VerticalAlign();
	public static final VerticalAlign TOP= new VerticalAlign();

	public static final FontStyle BOLD = new FontStyle();
	public static final FontStyle CURSIVE = new FontStyle();
	public static final FontStyle OUTLINED = new FontStyle();
	public static final FontStyle SHADOWED = new FontStyle();
	public static final FontStyle UNDERLINED = new FontStyle();

	private String fontFamily;
	private FontStyle fontStyle;
	private String colorName;
	private TypedSize fontSize;
	private Point2D offset;
	private HorizontalAlign horizontalAlign;
	private VerticalAlign verticalAlign;
	private Rectangle clipbox;	
	
	public StringFormat( String fontFamily, FontStyle fontStyle, String colorName,
						  TypedSize fontSize,Point2D offset, 
						  HorizontalAlign horizontalAlign,
						  VerticalAlign verticalAlign, Rectangle clipbox ) {
		this.fontFamily = fontFamily;
		this.fontStyle = fontStyle;
		this.colorName = colorName;
		this.fontSize = fontSize;
		this.offset = offset;
		this.horizontalAlign = horizontalAlign;
		this.verticalAlign = verticalAlign;
		this.clipbox = clipbox;
		
	}

	/**
	 * Returns the clipbox.
	 * @return Rectangle
	 */
	public Rectangle getClipbox() {
		return clipbox;
	}

	/**
	 * Returns the colorName.
	 * @return String
	 */
	public String getColorName() {
		return colorName;
	}

	/**
	 * Returns the fontFamily.
	 * @return String
	 */
	public String getFontFamily() {
		return fontFamily;
	}

	/**
	 * Returns the fontSize.
	 * @return TypedSize
	 */
	public TypedSize getFontSize() {
		return fontSize;
	}

	/**
	 * Returns the fontStyle.
	 * @return FontStyle
	 */
	public FontStyle getFontStyle() {
		return fontStyle;
	}

	/**
	 * Returns the horizontalAlign.
	 * @return HorizontalAlign
	 */
	public HorizontalAlign getHorizontalAlign() {
		return horizontalAlign;
	}

	/**
	 * Returns the offset.
	 * @return Point2D
	 */
	public Point2D getOffset() {
		return offset;
	}

	/**
	 * Returns the verticalAlign.
	 * @return VerticalAlign
	 */
	public VerticalAlign getVerticalAlign() {
		return verticalAlign;
	}

	/**
	 * Sets the clipbox.
	 * @param clipbox The clipbox to set
	 */
	public void setClipbox(Rectangle clipbox) {
		this.clipbox = clipbox;
	}

	/**
	 * Sets the colorName.
	 * @param colorName The colorName to set
	 */
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	/**
	 * Sets the fontFamily.
	 * @param fontFamily The fontFamily to set
	 */
	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	/**
	 * Sets the fontSize.
	 * @param fontSize The fontSize to set
	 */
	public void setFontSize(TypedSize fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Sets the fontStyle.
	 * @param fontStyle The fontStyle to set
	 */
	public void setFontStyle(FontStyle fontStyle) {
		this.fontStyle = fontStyle;
	}

	/**
	 * Sets the horizontalAlign.
	 * @param horizontalAlign The horizontalAlign to set
	 */
	public void setHorizontalAlign(HorizontalAlign horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
	}

	/**
	 * Sets the offset.
	 * @param offset The offset to set
	 */
	public void setOffset(Point2D offset) {
		this.offset = offset;
	}

	/**
	 * Sets the verticalAlign.
	 * @param verticalAlign The verticalAlign to set
	 */
	public void setVerticalAlign(VerticalAlign verticalAlign) {
		this.verticalAlign = verticalAlign;
	}

}
