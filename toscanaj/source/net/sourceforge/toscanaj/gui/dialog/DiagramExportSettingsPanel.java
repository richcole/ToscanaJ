/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tockit.canvas.imagewriter.DiagramExportSettings;

/**
 * A panel for setting the export options for diagram pictures.
 * 
 * This panel asks the user for the graphic format and size to use when
 * exporting a diagram. It is meant to be used as accessory for a file dialog.
 * 
 * @todo check if this should be Tockit code
 */
public class DiagramExportSettingsPanel extends JComponent implements
        ActionListener {
    /**
     * The data used.
     */
    private final DiagramExportSettings diagramSettings;

    private JLabel widthLabel;
    private JLabel heightLabel;
    private JTextField widthField;
    private JTextField heightField;
    private JCheckBox saveToFileCheckBox;
    private JCheckBox copyToClipboardCheckBox;
    private JCheckBox useScrSizeCheckBox;

    private JCheckBox forceColorCheckBox;

    private JButton backgroundColorButton;

    public DiagramExportSettingsPanel(final DiagramExportSettings settings) {
        super();
        setPreferredSize(new Dimension(150, 200));
        this.diagramSettings = settings;
        buildPanel();
    }

    private void buildPanel() {
        setLayout(new GridBagLayout());
        add(createModePanel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(createHistoryPanel(), new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(createBackgroundPanel(), new GridBagConstraints(1, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
    }

    private JPanel createModePanel() {
        this.useScrSizeCheckBox = new JCheckBox("Use Screen Size",
                this.diagramSettings.usesAutoMode());
        this.useScrSizeCheckBox.addActionListener(this);

        this.widthLabel = new JLabel();
        this.widthLabel.setText("Width:");
        this.widthLabel.setEnabled(!this.diagramSettings.usesAutoMode());
        this.widthField = new JTextField();
        this.widthField.setText(String.valueOf(this.diagramSettings
                .getImageWidth()));
        this.widthField.setEnabled(false);
        this.widthField.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.widthField.setEnabled(!this.diagramSettings.usesAutoMode());

        this.heightLabel = new JLabel();
        this.heightLabel.setText("Height:");
        this.heightLabel.setEnabled(!this.diagramSettings.usesAutoMode());
        this.heightField = new JTextField();
        this.heightField.setText(String.valueOf(this.diagramSettings
                .getImageHeight()));
        this.heightField.setEnabled(false);
        this.heightField.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.heightField.setEnabled(!this.diagramSettings.usesAutoMode());

        // add the items of the same classification to a JPanel
        final JPanel modePanel = new JPanel(new GridBagLayout());
        // modePanel.setBorder(BorderFactory.createTitledBorder("Size"));

        modePanel.add(this.useScrSizeCheckBox, new GridBagConstraints(0, 0, 1,
                1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        modePanel.add(this.widthLabel, new GridBagConstraints(0, 1, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        modePanel.add(this.widthField, new GridBagConstraints(0, 1, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 50, 5, 5), 0, 0));

        modePanel.add(this.heightLabel, new GridBagConstraints(0, 2, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        modePanel.add(this.heightField, new GridBagConstraints(0, 2, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 50, 5, 5), 0, 0));
        modePanel.add(new JPanel(), new GridBagConstraints(1, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        return modePanel;
    }

    private JPanel createHistoryPanel() {
        final JPanel historyPanel = new JPanel(new GridBagLayout());
        // historyPanel.setBorder(BorderFactory.createTitledBorder(
        // "History Export"));
        this.saveToFileCheckBox = new JCheckBox("Save history to file",
                this.diagramSettings.getSaveCommentsToFile());
        this.copyToClipboardCheckBox = new JCheckBox("History to clipboard",
                this.diagramSettings.getSaveCommentToClipboard());
        historyPanel.add(this.saveToFileCheckBox, new GridBagConstraints(0, 0,
                1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        historyPanel.add(this.copyToClipboardCheckBox, new GridBagConstraints(
                0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        return historyPanel;
    }

    private JPanel createBackgroundPanel() {
        final JPanel backgroundPanel = new JPanel(new GridBagLayout());
        final JComponent parent = this;
        // backgroundPanel.setBorder(BorderFactory.createTitledBorder(
        // "Background options"));
        this.forceColorCheckBox = new JCheckBox("Force Background",
                this.diagramSettings.forceColorIsSet());
        this.backgroundColorButton = new JButton("");
        final Dimension buttonSize = new Dimension(20, 20);
        this.backgroundColorButton.setMinimumSize(buttonSize);
        this.backgroundColorButton.setPreferredSize(buttonSize);
        this.backgroundColorButton.setBackground(this.diagramSettings
                .getBackgroundColor());
        this.backgroundColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final Color newColor = JColorChooser.showDialog(parent,
                        "Select background color",
                        DiagramExportSettingsPanel.this.backgroundColorButton
                                .getBackground());
                DiagramExportSettingsPanel.this.backgroundColorButton
                        .setBackground(newColor);
            }
        });
        backgroundPanel.add(this.forceColorCheckBox, new GridBagConstraints(0,
                0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        backgroundPanel.add(new JLabel("Color: "), new GridBagConstraints(0, 1,
                1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(0, 15, 0, 5), 0, 0));
        backgroundPanel.add(this.backgroundColorButton, new GridBagConstraints(
                1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        return backgroundPanel;
    }

    public void actionPerformed(final ActionEvent a) {
        final Object source = a.getSource();
        if ((source == this.useScrSizeCheckBox)) {
            this.widthField.setEnabled(!this.useScrSizeCheckBox.isSelected());
            this.heightField.setEnabled(!this.useScrSizeCheckBox.isSelected());
            this.widthLabel.setEnabled(!this.useScrSizeCheckBox.isSelected());
            this.heightLabel.setEnabled(!this.useScrSizeCheckBox.isSelected());
        }
    }

    public void saveSettings() {
        this.diagramSettings.setImageSize(Integer.parseInt(this.widthField
                .getText()), Integer.parseInt(this.heightField.getText()));
        this.diagramSettings.setAutoMode(this.useScrSizeCheckBox.isSelected());
        this.diagramSettings.setSaveCommentsToFile(this.saveToFileCheckBox
                .isSelected());
        this.diagramSettings
                .setSaveCommentToClipboard(this.copyToClipboardCheckBox
                        .isSelected());
        this.diagramSettings
                .setForceColor(this.forceColorCheckBox.isSelected());
        this.diagramSettings.setBackgroundColor(this.backgroundColorButton
                .getBackground());
    }
}
