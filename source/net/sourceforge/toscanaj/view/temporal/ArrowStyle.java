/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package net.sourceforge.toscanaj.view.temporal;

import java.awt.Color;
import java.awt.Stroke;


public class ArrowStyle {
    private Color color;
    private Stroke stroke;
    private double headWidth;
    private double headLength;
    private double relativeLength;
    
    public ArrowStyle(Color color, Stroke stroke, 
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

    public Color getColor() {
        return this.color;
    }

    public double getHeadLength() {
        return this.headLength;
    }

    public double getHeadWidth() {
        return this.headWidth;
    }

    public Stroke getStroke() {
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

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
}