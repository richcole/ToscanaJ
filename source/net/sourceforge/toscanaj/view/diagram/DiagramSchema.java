/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

import java.awt.*;

/**
 * DiagramSchema will hold the palette colors, line widths and similar information
 * for the DiagramView.
 *
 * This class uses a Singleton design pattern.
 *
 * @todo drop singleton, attach object to DiagramView
 */

public class DiagramSchema {
    /**
     * Used when the gradient should use the extent size of a concept as measure.
     */
    public static int GRADIENT_TYPE_EXTENT = 1;

    /**
     * Used when the gradient should use the contingent size of a concept as measure.
     */
    public static int GRADIENT_TYPE_CONTINGENT = 2;

    /**
     * Used when the gradient should compare to the number of objects in the diagram.
     */
    public static int GRADIENT_REFERENCE_DIAGRAM = 1;

    /**
     * Used when the gradient should compare to the number of objects in the conceptual
     * schema.
     */
    public static int GRADIENT_REFERENCE_SCHEMA = 2;

    /**
     * The amount of fade out for unselected nodes.
     */
    private float fadeOut = 0.7F;

    /**
     * Holds diagram canvas foreground color
     */
    private Color foreground = Color.black;

    /**
     * Holds diagram canvas background color.
     */
    private Color background = new Color(204, 204, 204);

    /**
     * Holds background color for the nested diagrams.
     */
    private Color nestedDiagramNodeColor = Color.white;

    /**
     * The color used for the top of the gradient.
     */
    private Color topColor = new Color(0, 0, 150);

    /**
     * The color used for the bottom of the gradient.
     */
    private Color bottomColor = new Color(255, 255, 150);

    /**
     * The color for the circles around the nodes.
     */
    private Color circleColor = new Color(0, 0, 0);

    /**
     * The color for lines between nodes.
     */
    private Color lineColor = new Color(0, 0, 0);

    /**
     * The color for the circles around the node with the selected concept.
     */
    private Color circleSelectionColor = new Color(255, 0, 0);

    /**
     * The color for the circles around the nodes in the ideal of the selected.
     * concept.
     */
    private Color circleIdealColor = new Color(0, 0, 0);

    /**
     * The color for the circles around the nodes in the filter of the selected.
     * concept.
     */
    private Color circleFilterColor = new Color(0, 0, 0);

    /**
     * The gradient type set.
     *
     * @see #setGradientType(int)
     */
    private int gradientType = GRADIENT_TYPE_EXTENT;

    /**
     * The gradient reference set.
     *
     * @see #setGradientReference(int)
     */
    private int gradientReference = GRADIENT_REFERENCE_SCHEMA;

    private int selectionLineWidth = 3;

    private String labelFontName = "SansSerif";

    private int labelFontSize = 10;

    private int margin = 20;

    private float notRealizedNodeSizeReductionFactor = 3;

    /**
     * Default constructor.
     */
    public DiagramSchema() {
        background = ConfigurationManager.fetchColor("diagramSchema", "backgroundColor", background);
        topColor = ConfigurationManager.fetchColor("diagramSchema", "topColor", topColor);
        bottomColor = ConfigurationManager.fetchColor("diagramSchema", "bottomColor", bottomColor);
        foreground = ConfigurationManager.fetchColor("diagramSchema", "foregroundColor", foreground);
        nestedDiagramNodeColor = ConfigurationManager.fetchColor("diagramSchema", "nestedDiagramNodeColor", nestedDiagramNodeColor);
        circleColor = ConfigurationManager.fetchColor("diagramSchema", "circleColor", circleColor);
        lineColor = ConfigurationManager.fetchColor("diagramSchema", "lineColor", lineColor);
        circleSelectionColor = ConfigurationManager.fetchColor("diagramSchema", "circleSelectionColor", circleSelectionColor);
        circleIdealColor = ConfigurationManager.fetchColor("diagramSchema", "circleIdealColor", circleIdealColor);
        circleFilterColor = ConfigurationManager.fetchColor("diagramSchema", "circleFilterColor", circleFilterColor);
        fadeOut = ConfigurationManager.fetchFloat("diagramSchema", "fadeOutValue", fadeOut);
        margin = ConfigurationManager.fetchInt("diagramSchema", "margin", margin);
        notRealizedNodeSizeReductionFactor = ConfigurationManager.fetchFloat("diagramSchema","notRealizedNodeSizeReductionFactor",notRealizedNodeSizeReductionFactor);
        String propVal = ConfigurationManager.fetchString("diagramSchema", "gradientType", "extent");
        propVal = propVal.toLowerCase();
        if (propVal.equals("extent")) {
            gradientType = GRADIENT_TYPE_EXTENT;
        } else if (propVal.equals("contingent")) {
            gradientType = GRADIENT_TYPE_CONTINGENT;
        } else {
            System.err.println("Caught unknown gradient type for DiagramSchema: " + propVal);
            System.err.println("-- using default");
        }
        propVal = ConfigurationManager.fetchString("diagramSchema", "gradientReference", "diagram");
        propVal = propVal.toLowerCase();
        if (propVal.equals("diagram")) {
            gradientReference = GRADIENT_REFERENCE_DIAGRAM;
        } else if (propVal.equals("schema")) {
            gradientReference = GRADIENT_REFERENCE_SCHEMA;
        } else {
            System.err.println("Caught unknown gradient reference for DiagramSchema: " + propVal);
            System.err.println("-- using default");
        }
        selectionLineWidth = ConfigurationManager.fetchInt("diagramSchema", "selectionLineWidth",
                selectionLineWidth);
        labelFontName = ConfigurationManager.fetchString("diagramSchema", "labelFontName", labelFontName);
        labelFontSize = ConfigurationManager.fetchInt("diagramSchema", "labelFontSize", labelFontSize);
    }

    /**
     * Returns default foreground color.
     */
    public Color getForegroundColor() {
        return foreground;
    }

    /**
     * Returns background color.
     */
    public Color getBackgroundColor() {
        return background;
    }

    /**
     * Returns the top gradient color.
     */
    public Color getTopColor() {
        return topColor;
    }

    /**
     * Returns the bottem gradient color.
     */
    public Color getBottomColor() {
        return bottomColor;
    }

    /**
     * Returns a color on the gradient.
     *
     * The parameter has to be between 0 and 1 (otherwise an
     * IllegalArgumentException will be thrown). The color returned will be the
     * bottom color if the number is zero, the top color if it is one, some
     * interpolation otherwise.
     */
    public Color getGradientColor(double position) {
        if (position < 0 || position > 1) {
            throw new IllegalArgumentException("Gradient position not in [0,1]");
        }
        return new Color((int) (topColor.getRed() * position + bottomColor.getRed() * (1 - position)),
                (int) (topColor.getGreen() * position + bottomColor.getGreen() * (1 - position)),
                (int) (topColor.getBlue() * position + bottomColor.getBlue() * (1 - position)),
                (int) (topColor.getAlpha() * position + bottomColor.getAlpha() * (1 - position)));
    }

    /**
     * Returns color for the circles around the nodes.
     */
    public Color getCircleColor() {
        return circleColor;
    }

    /**
     * Returns color for the lines between nodes.
     */
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * Returns color for the circles around the node with the selected concept.
     */
    public Color getCircleSelectionColor() {
        return circleSelectionColor;
    }

    /**
     * Returns color for the circles around the nodes in the ideal of the selected.
     */
    public Color getCircleIdealColor() {
        return circleIdealColor;
    }

    /**
     * Returns color for the circles around the nodes in the filter of the selected
     * concept.
     */
    public Color getCircleFilterColor() {
        return circleFilterColor;
    }

    /**
     * Returns background color for the nested diagrams.
     */
    public Color getNestedDiagramNodeColor() {
        return nestedDiagramNodeColor;
    }

    /**
     * Returns the type of information that should be used to create the diagram.
     *
     * This can be either GRADIENT_TYPE_EXTENT or GRADIENT_TYPE_CONTINGENT.
     */
    public int getGradientType() {
        return this.gradientType;
    }

    /**
     * Returns the reference point for creating the gradient.
     *
     * This can be either GRADIENT_REFERENCE_DIAGRAM to compare extent/contingent
     * to the number of objects in the diagram or GRADIENT_REFERENCE_SCHEMA to
     * compare it to the full set of objects in the schema.
     */
    public int getGradientReference() {
        return this.gradientReference;
    }

    public int getSelectionLineWidth() {
        return this.selectionLineWidth;
    }

    public void setSelectionLineWidth(int lineWidth) {
        this.selectionLineWidth = lineWidth;
    }

    /**
     * Set the circle color to new color.
     */
    public void setCircleColor(Color circleColor) {
        this.circleColor = circleColor;
    }

    /**
     * Set the line color to new color.
     */
    public void setLineColor(Color LineColor) {
        this.lineColor = lineColor;
    }

    /**
     * Set the top color to new color.
     */
    public void setTopColor(Color topColor) {
        this.topColor = topColor;
    }

    /**
     * Set the bottom color to new color.
     */
    public void setBottomColor(Color bottomColor) {
        this.bottomColor = bottomColor;
    }

    /**
     * Sets the type of information that should be used to create the diagram.
     *
     * This can be either GRADIENT_TYPE_EXTENT or GRADIENT_TYPE_CONTINGENT.
     *
     * @throws IllegalArgumentException  If argument is not one of the two allowed values.
     */
    public void setGradientType(int gradientType) {
        if (gradientType != GRADIENT_TYPE_EXTENT && gradientType != GRADIENT_TYPE_CONTINGENT) {
            throw new IllegalArgumentException("Unknown value for gradient type");
        }
        this.gradientType = gradientType;
    }

    /**
     * Sets the reference point for creating the gradient.
     *
     * This can be either GRADIENT_REFERENCE_DIAGRAM to compare extent/contingent
     * to the number of objects in the diagram or GRADIENT_REFERENCE_SCHEMA to
     * compare it to the full set of objects in the schema.
     */
    public void setGradientReference(int gradientReference) {
        if (gradientReference != GRADIENT_REFERENCE_DIAGRAM &&
                gradientReference != GRADIENT_REFERENCE_SCHEMA) {
            throw new IllegalArgumentException("Unknown value for gradient type");
        }
        this.gradientReference = gradientReference;
    }

    public String getLabelFontName() {
        return this.labelFontName;
    }

    public int getLabelFontSize() {
        return this.labelFontSize;
    }

    /**
     * Fades a color into the background.
     *
     * This is typically used for highlighting the rest of the diagram by using
     * the returned colors for the non-highlighted part.
     */
    public Color fadeOut(Color original) {
        return new Color((int) (original.getRed() * (1 - fadeOut) + 255 * fadeOut),
                (int) (original.getGreen() * (1 - fadeOut) + 255 * fadeOut),
                (int) (original.getBlue() * (1 - fadeOut) + 255 * fadeOut),
                (int) (original.getAlpha() * (1 - fadeOut) + 255 * fadeOut));
    }

    public int getMargin() {
        return margin;
    }

    public double getNotRealizedNodeSizeReductionFactor() {
        return notRealizedNodeSizeReductionFactor;
    }
}
