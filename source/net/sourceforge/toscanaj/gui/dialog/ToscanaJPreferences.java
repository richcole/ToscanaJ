/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;

import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.gui.ToscanaJMainPanel;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.view.diagram.LineView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.swing.dialogs.GenericDialog;
import org.tockit.swing.preferences.ConfigurationEntry;
import org.tockit.swing.preferences.ConfigurationSection;
import org.tockit.swing.preferences.ConfigurationSubsection;
import org.tockit.swing.preferences.ConfigurationType;
import org.tockit.swing.preferences.ExtendedPreferences;
import org.tockit.swing.preferences.PreferencePanel;


public class ToscanaJPreferences {
    private final static ExtendedPreferences DIAGRAM_SCHEMA_NODE = 
                                new ExtendedPreferences(DiagramSchema.preferences.node("User Defined"));
    
    private static final ConfigurationSection DIAGRAM_COLORS_SECTION = new ConfigurationSection(
            new ConfigurationSubsection[] {
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "topColor", ConfigurationType.COLOR, "Top"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "bottomColor", ConfigurationType.COLOR, "Bottom"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "nestedDiagramNodeColor", ConfigurationType.COLOR, "Large Node"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "notRealisedDiagramNodeColor", ConfigurationType.COLOR, "Empty Node "),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "circleColor", ConfigurationType.COLOR, "Circle")
                }, 
                "Node Colors"),
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "circleSelectionColor", ConfigurationType.COLOR, "Selection"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "circleIdealColor", ConfigurationType.COLOR, "Downset"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "circleFilterColor", ConfigurationType.COLOR, "Upset")
                }, 
                "Highlight Colors"),
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "backgroundColor", ConfigurationType.COLOR, "Background"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "lineColor", ConfigurationType.COLOR, "Line")
                }, 
                "Other Colors")
            },
            "Diagram Colors"
    );
    
    private static final ConfigurationSection DIAGRAM_OTHER_OPTIONS_SECTION = new ConfigurationSection(
            new ConfigurationSubsection[] {
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "labelFontName", ConfigurationType.FONT_FAMILY, "Family"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "labelFontSize", ConfigurationType.INTEGER, "Size")
                }, 
                "Label Font"),
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "gradientType", ConfigurationType.createEnumType(new String[] {"extent", "contingent"}), "Gradient type"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "scaleNodeSize", ConfigurationType.createEnumType(new String[] {"none","extent", "contingent"}), "Node size scaling")
                }, 
                "Gradient/Scaling"),
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "margin", ConfigurationType.INTEGER, "Margins"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "notRealizedNodeSizeReductionFactor", ConfigurationType.DOUBLE, "Reduction for non-realized"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "nodeStrokeWidth", ConfigurationType.DOUBLE, "Node stroke width"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "defaultLineWidth", ConfigurationType.DOUBLE, "Default line width"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "selectionLineWidth", ConfigurationType.DOUBLE, "Selection line width"),
                    new ConfigurationEntry(DIAGRAM_SCHEMA_NODE, "fadeOutValue", ConfigurationType.DOUBLE, "Fade out")
                }, 
                "Other Values")
            },
            "Diagram Options"
    );

    private final static ExtendedPreferences LINE_VIEW_NODE = ExtendedPreferences.userNodeForClass(LineView.class);
    
    private static final ConfigurationSection LINE_VIEW_SECTION = new ConfigurationSection(
            new ConfigurationSubsection[] {
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(LINE_VIEW_NODE, "showExtentRatioLabels", ConfigurationType.BOOLEAN, "Show Labels"),
                    new ConfigurationEntry(LINE_VIEW_NODE, "lineWidth", ConfigurationType.BOOLEAN, "Adjust Width"),
                    new ConfigurationEntry(LINE_VIEW_NODE, "nonRealizedConceptGrouping", ConfigurationType.createEnumType(new String[] {"no","coloredLines","clouds"}), "Group Equivalence Classes")
                }, 
                "General Options"),
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(LINE_VIEW_NODE, "labelFormat", ConfigurationType.STRING, "Label Format"),
                    new ConfigurationEntry(LINE_VIEW_NODE, "showExtentRatioColor", ConfigurationType.COLOR, "Label Color"),
                    new ConfigurationEntry(LINE_VIEW_NODE, "showExtentRatioFillColor", ConfigurationType.COLOR, "Second Label Color"),
                    new ConfigurationEntry(LINE_VIEW_NODE, "labelFontSize", ConfigurationType.DOUBLE, "Font Size")
                }, 
                "Label Settings")
            },
            "Line Labels"
    );
    
    private final static ExtendedPreferences MAINPANEL_NODE = ExtendedPreferences.userNodeForClass(ToscanaJMainPanel.class);
    private final static ExtendedPreferences NODE_VIEW_NODE = ExtendedPreferences.userNodeForClass(NodeView.class);
    private final static ExtendedPreferences DATABASE_CONNECTED_CONCEPT_INTERPRETER_NODE = ExtendedPreferences.userNodeForClass(DatabaseConnectedConceptInterpreter.class);
    
    private static final ConfigurationSection MAINPANEL_SECTION = new ConfigurationSection(
            new ConfigurationSubsection[] {
                new ConfigurationSubsection(new ConfigurationEntry[] {
                    new ConfigurationEntry(MAINPANEL_NODE, "offerGradientOptions", ConfigurationType.BOOLEAN, "Gradient"),
                    new ConfigurationEntry(MAINPANEL_NODE, "offerNodeSizeScalingOptions", ConfigurationType.BOOLEAN, "Node Scaling"),
                    new ConfigurationEntry(MAINPANEL_NODE, "offerOrthogonalityGradient", ConfigurationType.BOOLEAN, "Orthogonality")
                }, 
                "Extra Menu Options"),
                new ConfigurationSubsection(new ConfigurationEntry[] {
                        new ConfigurationEntry(MAINPANEL_NODE, "showDiagramPreview", ConfigurationType.BOOLEAN, "Diagram Preview (needs restart)"),
                        new ConfigurationEntry(NODE_VIEW_NODE, "displayCoordinates", ConfigurationType.BOOLEAN, "Show Node Vectors (debug option)"),
                        new ConfigurationEntry(DATABASE_CONNECTED_CONCEPT_INTERPRETER_NODE,
                                "useOrderBy", ConfigurationType.BOOLEAN, "Sort results from database")
                }, 
                "Miscellaneous")
            },
            "Extra Features"
    );
    
    private static final ConfigurationSection[] SECTIONS = new ConfigurationSection[] {
		DIAGRAM_COLORS_SECTION, DIAGRAM_OTHER_OPTIONS_SECTION, LINE_VIEW_SECTION, MAINPANEL_SECTION
    };
    
    public static boolean showPreferences(Component parent) {
        PreferencePanel panel = new PreferencePanel(SECTIONS, parent);
        boolean okClicked = GenericDialog.showDialog(parent, "Preferences", panel);
        if(okClicked) {
            panel.applyChanges();
            Collection schemas = DiagramSchema.getSchemas();
            for (Iterator iter = schemas.iterator(); iter.hasNext(); ) {
                DiagramSchema schema = (DiagramSchema) iter.next();
                if(schema.getName().equals("User Defined")) {
                    schema.setAsCurrent();
                    schema.restore(DIAGRAM_SCHEMA_NODE);
                }
            }
        }
        return okClicked;
    }
    
    public static final void removeSettings() throws BackingStoreException {
        for (int i = 0; i < SECTIONS.length; i++) {
            ConfigurationSection section = SECTIONS[i];
            for (int j = 0; j < section.getSubsections().length; j++) {
                ConfigurationSubsection subsection = section.getSubsections()[j];
                for (int k = 0; k < subsection.getEntries().length; k++) {
                    ConfigurationEntry entry = subsection.getEntries()[k];
                    ExtendedPreferences.removeBranch(entry.getNode());
                }
            }
        }
    }
}
