/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.util.gradients.*;
import net.sourceforge.toscanaj.view.temporal.ArrowStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * DiagramSchema will hold the palette colors, line widths and similar information
 * for the DiagramView.
 *
 * This class uses a Singleton design pattern.
 */
public class DiagramSchema {
    public static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(DiagramSchema.class);

    /**
     * The static block makes sure the preferences are set.
     * 
     * Otherwise editing them will not work.
     */
    static {
        DiagramSchema schema = getCurrentSchema();
        schema.setAsDefault();
    }

    /**
     * The amount of fade out for unselected nodes.
     */
    private float fadeOut;

    /**
     * Holds diagram canvas foreground color
     */
    private Color foreground;

    /**
     * Holds diagram canvas background color.
     */
    private Color background;

    /**
     * Holds background color for the nested diagrams.
     */
    private Color nestedDiagramNodeColor;

    /**
     * Holds color for the not realised nodes.
     * 
     * If set to null, the normal node color will be used.
     */
    private Color notRealisedDiagramNodeColor;

    /**
     * The color used for the top of the gradient.
     */
    private Color topColor;

    /**
     * The color used for the bottom of the gradient.
     */
    private Color bottomColor;

	private Gradient gradient;

    /**
     * The color for the circles around the nodes.
     */
    private Color circleColor;

    /**
     * The color for lines between nodes.
     */
    private Color lineColor;

    /**
     * The color for the circles around the node with the selected concept.
     */
    private Color circleSelectionColor;

    /**
     * The color for the circles around the nodes in the ideal of the selected.
     * concept.
     */
    private Color circleIdealColor;

    /**
     * The color for the circles around the nodes in the filter of the selected.
     * concept.
     */
    private Color circleFilterColor;

    /**
     * The gradient type set.
     *
     * @see #setGradientType(int)
     */
    private ConceptInterpreter.IntervalType gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;

    private int selectionLineWidth;

    private int margin;

    private float notRealizedNodeSizeReductionFactor;
    
    private ConceptInterpreter.IntervalType nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
    
    private Font labelFont;
    
    private ArrowStyle[] arrowStyles;

    private static DiagramSchema currentSchema;
    
    private DiagramSchema() {
    }
    
    public static DiagramSchema getCurrentSchema() {
        if(currentSchema == null) {
            currentSchema = new DiagramSchema();
    		currentSchema.background = preferences.getColor("backgroundColor", null);
    		currentSchema.topColor = preferences.getColor("topColor", new Color(0, 0, 150));
    		currentSchema.bottomColor = preferences.getColor("bottomColor", new Color(255, 255, 150));
    		currentSchema.gradient = currentSchema.getDefaultGradient();
    		currentSchema.foreground = preferences.getColor("foregroundColor", new Color(0, 0, 0));
    		currentSchema.nestedDiagramNodeColor = preferences.getColor("nestedDiagramNodeColor", new Color(255, 255, 255));
    		currentSchema.notRealisedDiagramNodeColor = preferences.getColor("notRealisedDiagramNodeColor", null);
    		currentSchema.circleColor = preferences.getColor("circleColor", new Color(0, 0, 0));
    		currentSchema.lineColor = preferences.getColor("lineColor", new Color(0, 0, 0));
    		currentSchema.circleSelectionColor = preferences.getColor("circleSelectionColor", new Color(255, 0, 0));
    		currentSchema.circleIdealColor = preferences.getColor("circleIdealColor", new Color(0, 0, 0));
    		currentSchema.circleFilterColor = preferences.getColor("circleFilterColor", new Color(0, 0, 0));
    		currentSchema.fadeOut = preferences.getFloat("fadeOutValue", 0.7F);
    		currentSchema.margin = preferences.getInt("margin", 20);
    		currentSchema.notRealizedNodeSizeReductionFactor = preferences.getFloat("notRealizedNodeSizeReductionFactor", 3);
    		String propVal = preferences.get("gradientType", "extent");
    		propVal = propVal.toLowerCase();
    		if (propVal.equals("extent")) {
    			currentSchema.gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
    		} else if (propVal.equals("contingent")) {
    			currentSchema.gradientType = ConceptInterpreter.INTERVAL_TYPE_CONTINGENT;
    		} else {
    			System.err.println("Caught unknown gradient type for DiagramSchema: " + propVal);
    			System.err.println("-- using default");
    		}
    		currentSchema.selectionLineWidth = preferences.getInt("selectionLineWidth", 3);
    		String labelFontName = preferences.get("labelFontName", "SansSerif");
    		int labelFontSize = preferences.getInt("labelFontSize", 10);
    		propVal = preferences.get("scaleNodeSize", "none");
    		propVal = propVal.toLowerCase();
    		currentSchema.labelFont = new Font(labelFontName, Font.PLAIN, labelFontSize);
    		if (propVal.equals("contingent")) {
    			currentSchema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_CONTINGENT;
    		} else if (propVal.equals("extent")) {
    			currentSchema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
    		} else if (propVal.equals("none")) {
    			currentSchema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
    		} else {
    			System.err.println("Caught unknown node size scaling value for DiagramSchema: " + propVal);
    			System.err.println("-- using default");
    		}
            List arrowStylesList = new ArrayList();
            int i = 0;
            try {
                while(preferences.nodeExists("arrowStyle-" + i)) {
                    arrowStylesList.add(new ArrowStyle(preferences.node("arrowStyle-" + i)));
                    i++;
                }
            } catch (BackingStoreException e) {
                // make sure to revert to defaults -- otherwise we might not have enough
                arrowStylesList.clear();
            }
            if(arrowStylesList.size() != 0) {
                currentSchema.arrowStyles = (ArrowStyle[]) arrowStylesList.toArray(new ArrowStyle[arrowStylesList.size()]);
            } else {
                currentSchema.arrowStyles = new ArrowStyle[] {
                        new ArrowStyle(Color.RED),
                        new ArrowStyle(Color.BLUE),
                        new ArrowStyle(Color.GREEN),
                        new ArrowStyle(Color.CYAN),
                        new ArrowStyle(Color.GRAY),
                        new ArrowStyle(Color.MAGENTA),
                        new ArrowStyle(Color.ORANGE),
                        new ArrowStyle(Color.PINK),
                        new ArrowStyle(Color.BLACK),
                        new ArrowStyle(Color.YELLOW)
                };
            }
        }
    	return currentSchema;
    }

    public Color getForegroundColor() {
        return foreground;
    }

    /**
     * Returns background color.
     */
    public Color getBackgroundColor() {
        return background;
    }

	public Gradient getGradient() {
		return this.gradient;
	}
	
	public void setGradient(Gradient gradient) {
		this.gradient = gradient;
	}

	/**
	 * Returns a gradient to be used for the node colors.
	 */
	public Gradient getDefaultGradient() {
        return new LinearGradient(this.bottomColor, this.topColor);
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
     */
    public ConceptInterpreter.IntervalType getGradientType() {
        return this.gradientType;
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
    public void setLineColor(Color lineColor) {
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
     */
    public void setGradientType(ConceptInterpreter.IntervalType gradientType) {
        this.gradientType = gradientType;
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
    
    public ConceptInterpreter.IntervalType getNodeSizeScalingType() {
        return nodeSizeScalingType;
    }
    
    public void setNodeSizeScalingType(ConceptInterpreter.IntervalType nodeSizeScalingType) {
        this.nodeSizeScalingType = nodeSizeScalingType;
    }

    public Color getNotRealisedNodeColor(Color nodeColor) {
        if(this.notRealisedDiagramNodeColor == null) {
        	return nodeColor;
        } else {
        	return this.notRealisedDiagramNodeColor;
        }
    }

	public Font getLabelFont() {
		return this.labelFont;
	}
    
    /**
     * Stores the schema in the preferences.
     */
    public void setAsDefault() {
        DiagramSchema.currentSchema = this;
        writeToPreferences(DiagramSchema.preferences);
    }

    private void writeToPreferences(ExtendedPreferences prefs) {
        prefs.putColor("backgroundColor", this.background);
        prefs.putColor("topColor", this.topColor);
        prefs.putColor("bottomColor", this.bottomColor);
        prefs.putColor("foregroundColor", this.foreground);
        prefs.putColor("nestedDiagramNodeColor", this.nestedDiagramNodeColor);
        prefs.putColor("notRealisedDiagramNodeColor", this.notRealisedDiagramNodeColor);
        prefs.putColor("circleColor", this.circleColor);
        prefs.putColor("lineColor", this.lineColor);
        prefs.putColor("circleSelectionColor", this.circleSelectionColor);
        prefs.putColor("circleIdealColor", this.circleIdealColor);
        prefs.putColor("circleFilterColor", this.circleFilterColor);
        prefs.putFloat("fadeOutValue", this.fadeOut);
        prefs.putInt("margin", this.margin);
        prefs.putFloat("notRealizedNodeSizeReductionFactor", this.notRealizedNodeSizeReductionFactor);
        if(this.gradientType == ConceptInterpreter.INTERVAL_TYPE_EXTENT) {
            prefs.put("gradientType", "extent");
        } else if(this.gradientType == ConceptInterpreter.INTERVAL_TYPE_CONTINGENT) {
            prefs.put("gradientType", "contingent");
        } else {
            throw new RuntimeException("Unknown gradient type");
        }
        prefs.putInt("selectionLineWidth", this.selectionLineWidth);
        prefs.put("labelFontName", this.labelFont.getFamily());
        prefs.putInt("labelFontSize", this.labelFont.getSize());
        if(this.nodeSizeScalingType == ConceptInterpreter.INTERVAL_TYPE_CONTINGENT) {
            prefs.put("scaleNodeSize", "contingent");
        } else if(this.nodeSizeScalingType == ConceptInterpreter.INTERVAL_TYPE_EXTENT) {
            prefs.put("scaleNodeSize", "extent");
        } else if(this.nodeSizeScalingType == ConceptInterpreter.INTERVAL_TYPE_FIXED) {
            prefs.put("scaleNodeSize", "none");
        } else {
            throw new RuntimeException("Unknown node scaling type");
        }
        
        for (int i = 0; i < this.arrowStyles.length; i++) {
            ArrowStyle style = this.arrowStyles[i];
            Preferences stylePrefs = prefs.node("arrowStyle-" + i);
            style.writeToPreferences(stylePrefs);
        }
    }

    public ArrowStyle[] getArrowStyles() {
        return this.arrowStyles;
    }
}
