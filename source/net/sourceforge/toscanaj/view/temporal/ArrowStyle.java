/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package net.sourceforge.toscanaj.view.temporal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.tockit.util.ColorStringConverter;

import net.sourceforge.toscanaj.util.xmlize.XMLizable;


public class ArrowStyle implements XMLizable {
    private static final double DEFAULT_ARROW_RELATIVE_LENGTH = 0.75;
	private static final int DEFAULT_ARROW_HEADLENGTH = 20;
	private static final int DEFAULT_ARROW_HEADWIDTH = 14;
	private static final int DEFAULT_ARROW_WIDTH = 4;
	private Color color;
    private BasicStroke stroke;
    private double headWidth;
    private double headLength;
    private double relativeLength;
    
    public ArrowStyle(Color color, BasicStroke stroke, 
                      double headWidth, double headLength,
                      double relativeLength) {
        this.color = color;
        this.stroke = stroke;
        this.headWidth = headWidth;
        this.headLength = headLength;
        this.relativeLength = relativeLength;
    }
    
    public ArrowStyle(ArrowStyle style) {
        this.color = style.color;
        this.stroke = style.stroke;
        this.headWidth = style.headWidth;
        this.headLength = style.headLength;
        this.relativeLength = style.relativeLength;
    }

    public ArrowStyle(Element element) {
        readXML(element);
    }
    
    public static ArrowStyle createDefaultArrowStyle(Color color){
    	BasicStroke stroke = new BasicStroke(DEFAULT_ARROW_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		return new ArrowStyle(color, stroke, DEFAULT_ARROW_HEADWIDTH, DEFAULT_ARROW_HEADLENGTH, DEFAULT_ARROW_RELATIVE_LENGTH); 
    }

    public Color getColor() {
        return this.color;
    }

    public double getHeadLength() {
        return this.headLength;
    }

    public double getHeadWidth() {
        return this.headWidth;
    }

    public BasicStroke getStroke() {
        return this.stroke;
    }
    
    public double getRelativeLength() {
        return this.relativeLength;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }

    public void setHeadLength(double headLength) {
        this.headLength = headLength;
    }

    public void setHeadWidth(double headWidth) {
        this.headWidth = headWidth;
    }

    public void setRelativeLength(double relativeLength) {
        this.relativeLength = relativeLength;
    }

    public void setStroke(BasicStroke stroke) {
        this.stroke = stroke;
    }

    public Element toXML() {
        Element result = new Element("arrowStyle");
        result.setAttribute("color", ColorStringConverter.colorToString(this.color));
        result.setAttribute("stroke-width", "" + stroke.getLineWidth());
        result.setAttribute("stroke-cap", "" + stroke.getEndCap());
        result.setAttribute("stroke-join", "" + stroke.getLineJoin());
        result.setAttribute("stroke-miterLimit", "" + stroke.getMiterLimit());
        result.setAttribute("stroke-dash", "" + serializeFloatArray(stroke.getDashArray()));
        result.setAttribute("stroke-dashPhase", "" + stroke.getDashPhase());
        result.setAttribute("headWidth", "" + this.headWidth);
        result.setAttribute("headLength", "" + this.headLength);
        result.setAttribute("relativeLength", "" + this.relativeLength);
        return result;
    }

    public void readXML(Element elem) {
        String colorValue = elem.getAttributeValue("color");
        this.color = ColorStringConverter.stringToColor(colorValue);
        float width = Float.parseFloat(elem.getAttributeValue("stroke-width"));
        int cap = Integer.parseInt(elem.getAttributeValue("stroke-cap"));
        int join = Integer.parseInt(elem.getAttributeValue("stroke-join"));
        float miterLimit = Float.parseFloat(elem.getAttributeValue("stroke-miterLimit"));
        float[] dash = parseFloatArray(elem.getAttributeValue("stroke-dash"));
        float dashPhase = Float.parseFloat(elem.getAttributeValue("stroke-dashPhase"));
        this.stroke = new BasicStroke(width, cap, join, miterLimit, dash, dashPhase);
        this.headWidth = Double.parseDouble(elem.getAttributeValue("headWidth"));
        this.headLength =Double.parseDouble(elem.getAttributeValue("headLength"));
        this.relativeLength = Double.parseDouble(elem.getAttributeValue("relativeLength"));
    }

    private String serializeFloatArray(float[] array) {
        if(array == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            float f = array[i];
            if(i != 0) {
                buffer.append(";");
            }
            buffer.append(f);
        }
        return buffer.toString();
    }

    private float[] parseFloatArray(String string) {
        if(string == null || string.equals("")) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(string, ";");
        float[] array = new float[tokenizer.countTokens()];
        for (int i = 0; i < array.length; i++) {
            array[i] = Float.parseFloat(tokenizer.nextToken());
        }
        return array;
    }
}