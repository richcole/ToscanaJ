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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
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
public class DiagramSchema implements Comparable {
    public static ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(DiagramSchema.class);

    private static ArrayList schemas = new ArrayList();

    private static DiagramSchema currentSchema;
    
    /**
     * The static block makes sure the preferences are set.
     * 
     * Otherwise editing them will not work.
     */
    static {
        try {
            DiagramSchema defaultSchema = null;
            if(preferences.getInt("margin", -1) != -1) {
                // this must be an old-style configuration, just read the default
                // and forget the rest -- will result in possibly different default
                defaultSchema = readFromPreferences(preferences);
                defaultSchema.name = "Default";
                ExtendedPreferences.removeBranch(preferences);
                // recreate the main node
                preferences = ExtendedPreferences.userNodeForClass(DiagramSchema.class);
            }
            String[] children = preferences.childrenNames();
            String currentSchemaName = preferences.get("currentSchema", "Default");
            if(children.length != 0) {
                for (int i = 0; i < children.length; i++) {
                    String schemaName = children[i];
                    DiagramSchema schema = readFromPreferences(preferences.node(schemaName));
                    schemas.add(schema);
                    if(schemaName.equals(currentSchemaName)) {
                        schema.setAsCurrent();
                    }
                }
                if(currentSchema == null) {
                    // shouldn't happen
                    currentSchema = getDefaultSchema();
                }
            } else {
                if(defaultSchema == null) {
                    defaultSchema = getDefaultSchema();
                }
                schemas.add(defaultSchema);
                defaultSchema.setAsCurrent();
                schemas.add(getGrayscaleSchema());
                schemas.add(getBlackNodeSchema());
                schemas.add(getWhiteNodeSchema());
                // store another copy of the default as user defined
                DiagramSchema userSchema = getDefaultSchema();
                userSchema.name = "User Defined";
                schemas.add(userSchema);
                storeAll();
            }
        } catch (BackingStoreException e) {
        }
    }
    
    public static Collection getSchemas() {
        return Collections.unmodifiableCollection(schemas);
    }
    
    private String name;

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

    private float defaultLineWidth;

    private float selectionLineWidth;

    private float nodeStrokeWidth;

    private int margin;

    private float notRealizedNodeSizeReductionFactor;
    
    private ConceptInterpreter.IntervalType nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
    
    private Font labelFont;
    
    private ArrowStyle[] arrowStyles;

    private DiagramSchema() {
    }
    
    private static DiagramSchema getWhiteNodeSchema() {
        DiagramSchema schema = new DiagramSchema();
        schema.name = "White Nodes";
        schema.background = null;
        schema.topColor = new Color(255,255,255);
        schema.bottomColor = new Color(255, 255, 255);
        schema.gradient = schema.getDefaultGradient();
        schema.foreground = new Color(0, 0, 0);
        schema.nestedDiagramNodeColor = new Color(255, 255, 255);
        schema.notRealisedDiagramNodeColor = null;
        schema.circleColor = new Color(0, 0, 0);
        schema.lineColor = new Color(0, 0, 0);
        schema.circleSelectionColor = new Color(128, 128, 128);
        schema.circleIdealColor = new Color(0, 0, 0);
        schema.circleFilterColor = new Color(0, 0, 0);
        schema.fadeOut = 0.7F;
        schema.margin = 20;
        schema.notRealizedNodeSizeReductionFactor = 3;
        schema.gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
        schema.defaultLineWidth = 1;
        schema.selectionLineWidth = 3;
        schema.labelFont = new Font("SansSerif", Font.PLAIN, 10);
        schema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
        /** @todo adjust arrow styles **/
        schema.arrowStyles = new ArrowStyle[] {
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
        return schema;
    }

    private static DiagramSchema getBlackNodeSchema() {
        DiagramSchema schema = new DiagramSchema();
        schema.name = "Black Nodes";
        schema.background = null;
        schema.topColor = new Color(0, 0, 0);
        schema.bottomColor = new Color(0, 0, 0);
        schema.gradient = schema.getDefaultGradient();
        schema.foreground = new Color(0, 0, 0);
        schema.nestedDiagramNodeColor = new Color(255, 255, 255);
        schema.notRealisedDiagramNodeColor = null;
        schema.circleColor = new Color(0, 0, 0);
        schema.lineColor = new Color(0, 0, 0);
        schema.circleSelectionColor = new Color(128,128,128);
        schema.circleIdealColor = new Color(0, 0, 0);
        schema.circleFilterColor = new Color(0, 0, 0);
        schema.fadeOut = 0.7F;
        schema.margin = 20;
        schema.notRealizedNodeSizeReductionFactor = 3;
        schema.gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
        schema.defaultLineWidth = 1;
        schema.selectionLineWidth = 3;
        schema.labelFont = new Font("SansSerif", Font.PLAIN, 10);
        schema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
        /** @todo adjust arrow styles **/
        schema.arrowStyles = new ArrowStyle[] {
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
        return schema;
    }

    private static DiagramSchema getGrayscaleSchema() {
        DiagramSchema schema = new DiagramSchema();
        schema.name = "Grayscale";
        schema.background = null;
        schema.topColor = new Color(0, 0, 0);
        schema.bottomColor = new Color(255, 255, 255);
        schema.gradient = schema.getDefaultGradient();
        schema.foreground = new Color(0, 0, 0);
        schema.nestedDiagramNodeColor = new Color(255, 255, 255);
        schema.notRealisedDiagramNodeColor = null;
        schema.circleColor = new Color(0, 0, 0);
        schema.lineColor = new Color(0, 0, 0);
        schema.circleSelectionColor = new Color(128,128,128);
        schema.circleIdealColor = new Color(0, 0, 0);
        schema.circleFilterColor = new Color(0, 0, 0);
        schema.fadeOut = 0.7F;
        schema.margin = 20;
        schema.notRealizedNodeSizeReductionFactor = 3;
        schema.gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
        schema.defaultLineWidth = 1;
        schema.selectionLineWidth = 3;
        schema.labelFont = new Font("SansSerif", Font.PLAIN, 10);
        schema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
        /** @todo adjust arrow styles **/
        schema.arrowStyles = new ArrowStyle[] {
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
        return schema;
    }

    /**
     * @todo the default values are duplicated in the restore(ExtendedPreferences) method -- remove
     */
    private static DiagramSchema getDefaultSchema() {
        DiagramSchema schema = new DiagramSchema();
        schema.name = "Default";
        schema.background = null;
        schema.topColor = new Color(0, 0, 150);
        schema.bottomColor = new Color(255, 255, 150);
        schema.gradient = schema.getDefaultGradient();
        schema.foreground = new Color(0, 0, 0);
        schema.nestedDiagramNodeColor = new Color(255, 255, 255);
        schema.notRealisedDiagramNodeColor = null;
        schema.circleColor = new Color(0, 0, 0);
        schema.lineColor = new Color(0, 0, 0);
        schema.circleSelectionColor = new Color(255, 0, 0);
        schema.circleIdealColor = new Color(0, 0, 0);
        schema.circleFilterColor = new Color(0, 0, 0);
        schema.fadeOut = 0.7F;
        schema.margin = 20;
        schema.notRealizedNodeSizeReductionFactor = 3;
        schema.gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
        schema.defaultLineWidth = 1;
        schema.selectionLineWidth = 3;
        schema.nodeStrokeWidth = 1;
        schema.labelFont = new Font("SansSerif", Font.PLAIN, 10);
        schema.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
        schema.arrowStyles = new ArrowStyle[] {
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
        return schema;
    }

    public static DiagramSchema getCurrentSchema() {
    	return currentSchema;
    }

    private static DiagramSchema readFromPreferences(Preferences prefs) {
        ExtendedPreferences extPrefs = new ExtendedPreferences(prefs);
        DiagramSchema schema = new DiagramSchema();
        schema.name = prefs.name();
        schema.restore(extPrefs);
        return schema;
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

    public float getDefaultLineWidth() {
        return this.defaultLineWidth;
    }

    public void setDefaultLineWidth(float lineWidth) {
        this.defaultLineWidth = lineWidth;
    }

    public float getSelectionLineWidth() {
        return this.selectionLineWidth;
    }

    public void setSelectionLineWidth(float lineWidth) {
        this.selectionLineWidth = lineWidth;
    }

    public float getNodeStrokeWidth() {
        return nodeStrokeWidth;
    }

    public void setNodeStrokeWidth(float nodeStrokeWidth) {
        this.nodeStrokeWidth = nodeStrokeWidth;
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
    public void store() {
        writeToPreferences(DiagramSchema.preferences.node(this.name));
    }

    public void setAsCurrent() {
        DiagramSchema.currentSchema = this;
        preferences.put("currentSchema", this.name);
    }
    
    private void writeToPreferences(Preferences prefs) {
        ExtendedPreferences extPrefs = new ExtendedPreferences(prefs);
        extPrefs.putColor("backgroundColor", this.background);
        extPrefs.putColor("topColor", this.topColor);
        extPrefs.putColor("bottomColor", this.bottomColor);
        extPrefs.putColor("foregroundColor", this.foreground);
        extPrefs.putColor("nestedDiagramNodeColor", this.nestedDiagramNodeColor);
        extPrefs.putColor("notRealisedDiagramNodeColor", this.notRealisedDiagramNodeColor);
        extPrefs.putColor("circleColor", this.circleColor);
        extPrefs.putColor("lineColor", this.lineColor);
        extPrefs.putColor("circleSelectionColor", this.circleSelectionColor);
        extPrefs.putColor("circleIdealColor", this.circleIdealColor);
        extPrefs.putColor("circleFilterColor", this.circleFilterColor);
        extPrefs.putFloat("fadeOutValue", this.fadeOut);
        extPrefs.putInt("margin", this.margin);
        extPrefs.putFloat("notRealizedNodeSizeReductionFactor", this.notRealizedNodeSizeReductionFactor);
        if(this.gradientType == ConceptInterpreter.INTERVAL_TYPE_EXTENT) {
            extPrefs.put("gradientType", "extent");
        } else if(this.gradientType == ConceptInterpreter.INTERVAL_TYPE_CONTINGENT) {
            extPrefs.put("gradientType", "contingent");
        } else {
            throw new RuntimeException("Unknown gradient type");
        }
        extPrefs.putFloat("defaultLineWidth", this.defaultLineWidth);
        extPrefs.putFloat("selectionLineWidth", this.selectionLineWidth);
        extPrefs.putFloat("nodeStrokeWidth", this.nodeStrokeWidth);
        extPrefs.put("labelFontName", this.labelFont.getFamily());
        extPrefs.putInt("labelFontSize", this.labelFont.getSize());
        if(this.nodeSizeScalingType == ConceptInterpreter.INTERVAL_TYPE_CONTINGENT) {
            extPrefs.put("scaleNodeSize", "contingent");
        } else if(this.nodeSizeScalingType == ConceptInterpreter.INTERVAL_TYPE_EXTENT) {
            extPrefs.put("scaleNodeSize", "extent");
        } else if(this.nodeSizeScalingType == ConceptInterpreter.INTERVAL_TYPE_FIXED) {
            extPrefs.put("scaleNodeSize", "none");
        } else {
            throw new RuntimeException("Unknown node scaling type");
        }
        
        for (int i = 0; i < this.arrowStyles.length; i++) {
            ArrowStyle style = this.arrowStyles[i];
            Preferences stylePrefs = extPrefs.node("arrowStyle-" + i);
            style.writeToPreferences(stylePrefs);
        }
    }

    public ArrowStyle[] getArrowStyles() {
        return this.arrowStyles;
    }
    
    public static void storeAll() {
        for (Iterator iter = schemas.iterator(); iter.hasNext(); ) {
            DiagramSchema schema = (DiagramSchema) iter.next();
            schema.store();
        }
    }

    public String getName() {
        return this.name;
    }

    // we store the schemas in alphabetical order by their name
    public int compareTo(Object o) {
        return this.name.compareTo(((DiagramSchema)o).name);
    }

    public void restore(ExtendedPreferences extPrefs) {
        this.background = extPrefs.getColor("backgroundColor", null);
        this.topColor = extPrefs.getColor("topColor", new Color(0, 0, 150));
        this.bottomColor = extPrefs.getColor("bottomColor", new Color(255, 255, 150));
        this.gradient = this.getDefaultGradient();
        this.foreground = extPrefs.getColor("foregroundColor", new Color(0, 0, 0));
        this.nestedDiagramNodeColor = extPrefs.getColor("nestedDiagramNodeColor", new Color(255, 255, 255));
        this.notRealisedDiagramNodeColor = extPrefs.getColor("notRealisedDiagramNodeColor", null);
        this.circleColor = extPrefs.getColor("circleColor", new Color(0, 0, 0));
        this.lineColor = extPrefs.getColor("lineColor", new Color(0, 0, 0));
        this.circleSelectionColor = extPrefs.getColor("circleSelectionColor", new Color(255, 0, 0));
        this.circleIdealColor = extPrefs.getColor("circleIdealColor", new Color(0, 0, 0));
        this.circleFilterColor = extPrefs.getColor("circleFilterColor", new Color(0, 0, 0));
        this.fadeOut = extPrefs.getFloat("fadeOutValue", 0.7F);
        this.margin = extPrefs.getInt("margin", 20);
        this.notRealizedNodeSizeReductionFactor = extPrefs.getFloat("notRealizedNodeSizeReductionFactor", 3);
        String propVal = extPrefs.get("gradientType", "extent");
        propVal = propVal.toLowerCase();
        if (propVal.equals("extent")) {
            this.gradientType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
        } else if (propVal.equals("contingent")) {
            this.gradientType = ConceptInterpreter.INTERVAL_TYPE_CONTINGENT;
        } else {
            System.err.println("Caught unknown gradient type for DiagramSchema: " + propVal);
            System.err.println("-- using default");
        }
        this.defaultLineWidth = extPrefs.getFloat("defaultLineWidth", 1);
        this.selectionLineWidth = extPrefs.getFloat("selectionLineWidth", 3);
        this.nodeStrokeWidth = extPrefs.getFloat("nodeStrokeWidth", 1);
        String labelFontName = extPrefs.get("labelFontName", "SansSerif");
        int labelFontSize = extPrefs.getInt("labelFontSize", 10);
        propVal = extPrefs.get("scaleNodeSize", "none");
        propVal = propVal.toLowerCase();
        this.labelFont = new Font(labelFontName, Font.PLAIN, labelFontSize);
        if (propVal.equals("contingent")) {
            this.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_CONTINGENT;
        } else if (propVal.equals("extent")) {
            this.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_EXTENT;
        } else if (propVal.equals("none")) {
            this.nodeSizeScalingType = ConceptInterpreter.INTERVAL_TYPE_FIXED;
        } else {
            System.err.println("Caught unknown node size scaling value for DiagramSchema: " + propVal);
            System.err.println("-- using default");
        }
        List arrowStylesList = new ArrayList();
        int i = 0;
        try {
            while(extPrefs.nodeExists("arrowStyle-" + i)) {
                arrowStylesList.add(new ArrowStyle(extPrefs.node("arrowStyle-" + i)));
                i++;
            }
        } catch (BackingStoreException e) {
            // make sure to revert to defaults -- otherwise we might not have enough
            arrowStylesList.clear();
        }
        if(arrowStylesList.size() != 0) {
            this.arrowStyles = (ArrowStyle[]) arrowStylesList.toArray(new ArrowStyle[arrowStylesList.size()]);
        } else {
            this.arrowStyles = new ArrowStyle[] {
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
}
