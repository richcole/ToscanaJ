/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import javax.swing.*;

import org.tockit.canvas.imagewriter.DiagramExportSettings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel for setting the export options for diagram pictures.
 *
 * This panel asks the user for the graphic format and size to use when exporting
 * a diagram. It is meant to be used as accessory for a file dialog.
 *
 * @todo check if this should be Tockit code
 */
public class DiagramExportSettingsPanel extends JComponent implements ActionListener {
    /**
     * The data used.
     */
    private DiagramExportSettings diagramSettings;

    private JLabel widthLabel;
	private JLabel heightLabel;
    private JTextField widthField;
    private JTextField heightField;
    private JCheckBox saveToFileCheckBox;
    private JCheckBox copyToClipboardCheckBox; 
	private JCheckBox useScrSizeCheckBox;

    private JCheckBox forceColorCheckBox;

    private JButton backgroundColorButton; 

    public DiagramExportSettingsPanel(DiagramExportSettings settings) {
        super();
        setPreferredSize(new Dimension(150,200));
        this.diagramSettings = settings;
		buildPanel();
    }

	private void buildPanel() {
		setLayout(new GridBagLayout());
		add(createModePanel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
					   , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        add(createHistoryPanel(), new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        add(createBackgroundPanel(), new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
    }

	private JPanel createModePanel() {
		useScrSizeCheckBox = new JCheckBox("Use Screen Size",this.diagramSettings.usesAutoMode());
		useScrSizeCheckBox.addActionListener(this);				

		widthLabel = new JLabel();
		widthLabel.setText("Width:");
		widthLabel.setEnabled(!this.diagramSettings.usesAutoMode());
		widthField = new JTextField();
		widthField.setText(String.valueOf(this.diagramSettings.getImageWidth()));
		widthField.setEnabled(false);
		widthField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		widthField.setEnabled(!this.diagramSettings.usesAutoMode());
		
		heightLabel = new JLabel();
		heightLabel.setText("Height:");
		heightLabel.setEnabled(!this.diagramSettings.usesAutoMode());
		heightField = new JTextField();
		heightField.setText(String.valueOf(this.diagramSettings.getImageHeight()));
		heightField.setEnabled(false);
		heightField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		heightField.setEnabled(!this.diagramSettings.usesAutoMode());
		
		//add the items of the same classification to a JPanel
		JPanel modePanel = new JPanel(new GridBagLayout());
//		modePanel.setBorder(BorderFactory.createTitledBorder("Size"));
		
		modePanel.add(useScrSizeCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0,0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

		modePanel.add(widthLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
		 	GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		
		modePanel.add(widthField, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
		  	GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 50, 5, 5), 0, 0));
		
		modePanel.add(heightLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
		  	GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		
		modePanel.add(heightField, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
		 	GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 50, 5, 5), 0, 0));		
		modePanel.add(new JPanel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
		GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		
		return modePanel;
	}

	private JPanel createHistoryPanel() {
		JPanel historyPanel = new JPanel(new GridBagLayout());
//		historyPanel.setBorder(BorderFactory.createTitledBorder("History Export"));
		saveToFileCheckBox = new JCheckBox("Save history to file", this.diagramSettings.getSaveCommentsToFile());
		copyToClipboardCheckBox = new JCheckBox("History to clipboard", this.diagramSettings.getSaveCommentToClipboard());
		historyPanel.add(saveToFileCheckBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		historyPanel.add(copyToClipboardCheckBox, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		return historyPanel;
	}

	private JPanel createBackgroundPanel() {
		JPanel backgroundPanel = new JPanel(new GridBagLayout());
        final JComponent parent = this;
//		backgroundPanel.setBorder(BorderFactory.createTitledBorder("Background options"));
		this.forceColorCheckBox = new JCheckBox("Force Background", this.diagramSettings.forceColorIsSet());
		this.backgroundColorButton = new JButton("");
        Dimension buttonSize = new Dimension(20,20);
        this.backgroundColorButton.setMinimumSize(buttonSize);
        this.backgroundColorButton.setPreferredSize(buttonSize);
        this.backgroundColorButton.setBackground(this.diagramSettings.getBackgroundColor());
        this.backgroundColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                                parent, "Select background color", backgroundColorButton.getBackground());
                backgroundColorButton.setBackground(newColor);
            }
        });
		backgroundPanel.add(this.forceColorCheckBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        backgroundPanel.add(new JLabel("Color: "), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 15, 0, 5), 0, 0));
        backgroundPanel.add(this.backgroundColorButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        return backgroundPanel;
	}

    public void actionPerformed(ActionEvent a) {
        Object source = a.getSource();
        if ((source == useScrSizeCheckBox)) {
            this.widthField.setEnabled(!useScrSizeCheckBox.isSelected());
            this.heightField.setEnabled(!useScrSizeCheckBox.isSelected());
            this.widthLabel.setEnabled(!useScrSizeCheckBox.isSelected());
			this.heightLabel.setEnabled(!useScrSizeCheckBox.isSelected());
        }
    }
    
	public void saveSettings(){
	   this.diagramSettings.setImageSize(Integer.parseInt(this.widthField.getText()),
			   Integer.parseInt(this.heightField.getText()));
	   this.diagramSettings.setAutoMode(this.useScrSizeCheckBox.isSelected());
	   this.diagramSettings.setSaveCommentsToFile(this.saveToFileCheckBox.isSelected());
	   this.diagramSettings.setSaveCommentToClipboard(this.copyToClipboardCheckBox.isSelected());
       this.diagramSettings.setForceColor(this.forceColorCheckBox.isSelected());
       this.diagramSettings.setBackgroundColor(this.backgroundColorButton.getBackground());
	}
}
