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
import java.util.prefs.Preferences;

import org.tockit.swing.preferences.ExtendedPreferences;


public class ArrowStyle {
    // setting some defaults
	private Color color = Color.WHITE;
    private BasicStroke stroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private double headWidth = 14;
    private double headLength = 20;
    private double relativeLength = 0.75;
    private float borderWidth = 0.2f;
    
    public ArrowStyle(Color color, BasicStroke stroke, 
                      double headWidth, double headLength,
                      double relativeLength, float borderWidth) {
        this.color = color;
        this.stroke = stroke;
        this.headWidth = headWidth;
        this.headLength = headLength;
        this.relativeLength = relativeLength;
        this.borderWidth = borderWidth;
    }
    
    public ArrowStyle(Color color) {
        this.color = color;
    }
    
    public ArrowStyle(ArrowStyle style) {
        this.color = style.color;
        this.stroke = style.stroke;
        this.headWidth = style.headWidth;
        this.headLength = style.headLength;
        this.relativeLength = style.relativeLength;
        this.borderWidth = style.borderWidth;
    }

    public void copyValues(ArrowStyle style) {
        this.setColor(style.getColor());
        this.setStroke(style.getStroke());
        this.setHeadLength(style.getHeadLength());
        this.setHeadWidth(style.getHeadWidth());
        this.setRelativeLength(style.relativeLength);
        this.setBorderWidth(style.borderWidth);
    }
    
    public ArrowStyle(Preferences preferences) {
        ExtendedPreferences prefs = new ExtendedPreferences(preferences);
        this.color = prefs.getColor("color", this.color);
        float lineWidth = prefs.getFloat("stroke-width", this.stroke.getLineWidth());
        int endCap = prefs.getInt("stroke-cap", this.stroke.getEndCap());
        int lineJoin = prefs.getInt("stroke-join", this.stroke.getLineJoin());
        float miterLimit = prefs.getFloat("stroke-miterLimit", this.stroke.getMiterLimit());
        float[] dashArray = parseFloatArray(
                                prefs.get("stroke-dash", serializeFloatArray(this.stroke.getDashArray())));
        float dashPhase = prefs.getFloat("stroke-dashPhase", this.stroke.getDashPhase());
        this.stroke = new BasicStroke(lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase);
        this.headWidth = prefs.getDouble("headWidth", this.headWidth);
        this.headLength = prefs.getDouble("headLength", this.headLength);
        this.relativeLength = prefs.getDouble("relativeLength", this.relativeLength);
        this.borderWidth = prefs.getFloat("borderWidth", this.borderWidth);
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

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void writeToPreferences(Preferences preferences) {
        ExtendedPreferences prefs = new ExtendedPreferences(preferences);
        prefs.putColor("color", this.color);
        prefs.putFloat("stroke-width", this.stroke.getLineWidth());
        prefs.putInt("stroke-cap", this.stroke.getEndCap());
        prefs.putInt("stroke-join", this.stroke.getLineJoin());
        prefs.putFloat("stroke-miterLimit", this.stroke.getMiterLimit());
        prefs.put("stroke-dash", serializeFloatArray(this.stroke.getDashArray()));
        prefs.putFloat("stroke-dashPhase", this.stroke.getDashPhase());
        prefs.putDouble("headWidth", this.headWidth);
        prefs.putDouble("headLength", this.headLength);
        prefs.putDouble("relativeLength", this.relativeLength);
        prefs.putFloat("borderWidth", this.borderWidth);
    }
}