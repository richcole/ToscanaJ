package net.sourceforge.toscanaj.view.diagram;

import java.awt.Color;

/**
 * DiagramSchema will hold the palette colors and pixcel width for CanvasItems.
 *
 * This class uses a Singleton design pattern.
 */

public class DiagramSchema {
    /**
     * create and store DiagramSchema
     */
    private static DiagramSchema diagramSchema = new DiagramSchema();

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
    private Color topColor =  new Color(0,0,150);

    /**
     * The colort used for the bottom of the gradient.
     */
    private Color bottomColor = new Color(255,255,150);

    /**
     * The Color for the circles around the nodes.
     */
    private Color circleColor = new Color(0,0,0);

    /**
     * The Color for the circles around the node with the selected concept.
     */
    private Color circleSelectionColor = new Color(255,0,0);

    /**
     * The Color for the circles around the nodes in the ideal of the selected.
     * concept.
     */
    private Color circleIdealColor = new Color(0,0,0);

    /**
     * The Color for the circles around the nodes in the filter of the selected.
     * concept.
     */
    private Color circleFilterColor = new Color(0,0,0);

    /**
     * Default constructor.
     */
    private DiagramSchema() {
    }

    /**
     * Get DiagramSchema instance.
     */
    public static DiagramSchema getDiagramSchema() {
        return diagramSchema;
    }

    /**
     * Returns foreground color.
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * Returns background color.
     */
    public Color getBackground() {
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
     * Returns color for the circles around the nodes.
     */
    public Color getCircleColor() {
        return circleColor;
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
     * Set the circle color to new color.
     */
    public void setCircleColor(Color circleColor) {
        this.circleColor = circleColor;
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
}