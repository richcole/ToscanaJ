/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.temporal;

import org.tockit.swing.preferences.ExtendedPreferences;

import java.awt.*;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

public class ArrowStyle {
    public enum LabelUse {
        NEVER("Never", false, false, false),
        ONLY_FIRST("Only on the first", true, false, false),
        ONLY_LAST("Only on the last", false, false, true),
        FIRST_AND_LAST("On the first and last", true, false, true),
        ALWAYS("Always", true, true, true);

        private final String label;
        private final boolean showFirst;
        private final boolean showAll;
        private final boolean showLast;

        LabelUse(String label, boolean showFirst, boolean showAll, boolean showLast) {
            this.label = label;
            this.showFirst = showFirst;
            this.showAll = showAll;
            this.showLast = showLast;
        }

        public String getLabel() {
            return label;
        }

        public boolean shouldShow(boolean isFirst, boolean isLast) {
            return showAll ||
                    (isFirst && showFirst) ||
                    (isLast && showLast);
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final LabelUse DEFAULT_LABEL_USE = LabelUse.ONLY_FIRST;

    // setting some defaults
    private Color color = Color.WHITE;
    private BasicStroke stroke = new BasicStroke(4, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER);
    private double headWidth = 14;
    private double headLength = 20;
    private float borderWidth = 0.2f;
    private LabelUse labelUse = DEFAULT_LABEL_USE;

    public ArrowStyle(final Color color, final BasicStroke stroke,
            final double headWidth, final double headLength,
            final float borderWidth, LabelUse labelUse) {
        this.color = color;
        this.stroke = stroke;
        this.headWidth = headWidth;
        this.headLength = headLength;
        this.borderWidth = borderWidth;
        this.labelUse = labelUse;
    }

    public ArrowStyle(final Color color) {
        this.color = color;
    }

    public ArrowStyle(final ArrowStyle style) {
        this.color = style.color;
        this.stroke = style.stroke;
        this.headWidth = style.headWidth;
        this.headLength = style.headLength;
        this.borderWidth = style.borderWidth;
        this.labelUse = style.labelUse;
    }

    public void copyValues(final ArrowStyle style) {
        this.setColor(style.getColor());
        this.setStroke(style.getStroke());
        this.setHeadLength(style.getHeadLength());
        this.setHeadWidth(style.getHeadWidth());
        this.setBorderWidth(style.borderWidth);
        this.setLabelUse(style.labelUse);
    }

    @SuppressWarnings("MagicConstant")
    public ArrowStyle(final Preferences preferences) {
        final ExtendedPreferences prefs = new ExtendedPreferences(preferences);
        this.color = prefs.getColor("color", this.color);
        final float lineWidth = prefs.getFloat("stroke-width", this.stroke
                .getLineWidth());
        final int endCap = prefs.getInt("stroke-cap", this.stroke.getEndCap());
        final int lineJoin = prefs.getInt("stroke-join", this.stroke
                .getLineJoin());
        final float miterLimit = prefs.getFloat("stroke-miterLimit",
                this.stroke.getMiterLimit());
        final float[] dashArray = parseFloatArray(prefs.get("stroke-dash",
                serializeFloatArray(this.stroke.getDashArray())));
        final float dashPhase = prefs.getFloat("stroke-dashPhase", this.stroke
                .getDashPhase());
        this.stroke = new BasicStroke(lineWidth, endCap, lineJoin, miterLimit,
                dashArray, dashPhase);
        this.headWidth = prefs.getDouble("headWidth", this.headWidth);
        this.headLength = prefs.getDouble("headLength", this.headLength);
        this.borderWidth = prefs.getFloat("borderWidth", this.borderWidth);
        this.labelUse = LabelUse.valueOf(prefs.get("labelUse", DEFAULT_LABEL_USE.name()));
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

    public LabelUse getLabelUse() {
        return labelUse;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    public void setHeadLength(final double headLength) {
        this.headLength = headLength;
    }

    public void setHeadWidth(final double headWidth) {
        this.headWidth = headWidth;
    }

    public void setStroke(final BasicStroke stroke) {
        this.stroke = stroke;
    }

    public void setLabelUse(LabelUse labelUse) {
        this.labelUse = labelUse;
    }

    private String serializeFloatArray(final float[] array) {
        if (array == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            final float f = array[i];
            if (i != 0) {
                builder.append(";");
            }
            builder.append(f);
        }
        return builder.toString();
    }

    private float[] parseFloatArray(final String string) {
        if (string == null || string.equals("")) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(string, ";");
        final float[] array = new float[tokenizer.countTokens()];
        for (int i = 0; i < array.length; i++) {
            array[i] = Float.parseFloat(tokenizer.nextToken());
        }
        return array;
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(final float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void writeToPreferences(final Preferences preferences) {
        final ExtendedPreferences prefs = new ExtendedPreferences(preferences);
        prefs.putColor("color", this.color);
        prefs.putFloat("stroke-width", this.stroke.getLineWidth());
        prefs.putInt("stroke-cap", this.stroke.getEndCap());
        prefs.putInt("stroke-join", this.stroke.getLineJoin());
        prefs.putFloat("stroke-miterLimit", this.stroke.getMiterLimit());
        prefs.put("stroke-dash",
                serializeFloatArray(this.stroke.getDashArray()));
        prefs.putFloat("stroke-dashPhase", this.stroke.getDashPhase());
        prefs.putDouble("headWidth", this.headWidth);
        prefs.putDouble("headLength", this.headLength);
        prefs.putFloat("borderWidth", this.borderWidth);
        prefs.put("labelUse", this.labelUse.name());
    }
}