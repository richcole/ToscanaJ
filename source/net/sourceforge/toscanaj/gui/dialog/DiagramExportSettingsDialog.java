/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

/**
 * A dialog for setting the export options for diagram pictures.
 *
 * This dialogs asks the user for the graphic format and size to use when exporting
 * a diagram.
 *
 * Use the static method showDialog() to get a modal dialog. If this returns
 * true the user clicked ok (otherwise cancel) and the information can be
 * retrieved with the methods getFormat(), getWidth() and getHeight().
 */
public class DiagramExportSettingsDialog extends JDialog implements ActionListener {
    /**
     * The instance used.
     */
    static private DiagramExportSettingsDialog dialog = null;

    /**
     * The data used.
     */
    static private DiagramExportSettings diagramSettings = null;

    private JRadioButton manual;
    private JRadioButton auto;
    private JLabel formatLabel;
    private JLabel widthLabel;
	private JLabel heightLabel;
    private JComboBox formatSelector;
    private JTextField widthField;
    private JTextField heightField;
    private JCheckBox saveToFileCheckBox;
    private JCheckBox copyToClipboardCheckBox; 

    /**
     * No public instances of this dialog.
     */
    private DiagramExportSettingsDialog(Frame frame, DiagramExportSettings settings) {
        super(frame, "Export Diagram Setup", true);
        JLabel modeLabel = new JLabel();
        modeLabel.setText("Mode:");

        ButtonGroup manualOrAuto = new ButtonGroup();

        auto = new JRadioButton("Auto");
        auto.addActionListener(this);
        auto.setSelected(settings.usesAutoMode());
        manualOrAuto.add(auto);
        manual = new JRadioButton("Manual");
        manual.addActionListener(this);
        manual.setSelected(!settings.usesAutoMode());
        manualOrAuto.add(manual);

        formatLabel = new JLabel();
        formatLabel.setText("Format:");
		formatLabel.setEnabled(!settings.usesAutoMode());
        formatSelector = new JComboBox();
        Iterator it = GraphicFormatRegistry.getIterator();
        while (it.hasNext()) {
            formatSelector.addItem(it.next());
        }
        formatSelector.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        GraphicFormat graphicFormat = settings.getGraphicFormat();
        if(graphicFormat != null) {
			formatSelector.setSelectedItem(graphicFormat);
        }
        formatSelector.setEnabled(!settings.usesAutoMode());
       
        widthLabel = new JLabel();
        widthLabel.setText("Width:");
		widthLabel.setEnabled(!settings.usesAutoMode());
        widthField = new JTextField();
        widthField.setText(String.valueOf(settings.getImageWidth()));
        widthField.setEnabled(false);
        widthField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        widthField.setEnabled(!settings.usesAutoMode());

        heightLabel = new JLabel();
        heightLabel.setText("Height:");
		heightLabel.setEnabled(!settings.usesAutoMode());
        heightField = new JTextField();
        heightField.setText(String.valueOf(settings.getImageHeight()));
        heightField.setEnabled(false);
        heightField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        heightField.setEnabled(!settings.usesAutoMode());
		
		//add the items of the same classification to a JPanel
		JPanel modePanel = new JPanel(new GridBagLayout());
		modePanel.setBorder(BorderFactory.createEtchedBorder());
		modePanel.add(auto, new GridBagConstraints(0, 0, 1, 1, 0.0,0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		modePanel.add(manual, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		modePanel.add(formatLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 20), 0, 0));
		modePanel.add(formatSelector, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, 
		 	GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 200, 0));
		modePanel.add(widthLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
		 	GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 20), 0, 0));
		modePanel.add(widthField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
		  	GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 200, 0));
		modePanel.add(heightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
		  	GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 20), 0, 0));
		modePanel.add(heightField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
		 	GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 200, 0));		
		
		
		JLabel historyLabel = new JLabel();
		historyLabel.setText("History: ");
		JPanel historyPanel = new JPanel(new GridBagLayout());
		historyPanel.setBorder(BorderFactory.createEtchedBorder());
		saveToFileCheckBox = new JCheckBox("Save to file", settings.getSaveCommentsToFile());
		saveToFileCheckBox.setSelected(settings.getSaveCommentsToFile());
		copyToClipboardCheckBox = new JCheckBox("Copy to clipboard", settings.getSaveCommentToClipboard());
		copyToClipboardCheckBox.setSelected(settings.getSaveCommentToClipboard());
		historyPanel.add(saveToFileCheckBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 200, 0));
		historyPanel.add(copyToClipboardCheckBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 200, 0));

        //buttons
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagramSettings = null;
                closeDialog();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
        
        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagramSettings = new DiagramExportSettings((GraphicFormat) formatSelector.getSelectedItem(),
                        Integer.parseInt(widthField.getText()),
                        Integer.parseInt(heightField.getText()),
                        auto.isSelected());
               	if (saveToFileCheckBox.isSelected()) {
					diagramSettings.setSaveCommentsToFile(true);
				}
				if (copyToClipboardCheckBox.isSelected()) {
					diagramSettings.setSaveCommentToClipboard(true);
				}  
                closeDialog();
            }
        });
        getRootPane().setDefaultButton(okButton);
		
        JPanel buttonPanel = new JPanel();
        GridBagLayout buttonLayout = new GridBagLayout();
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.add(cancelButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));
        buttonPanel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));
	
		GridBagLayout gridBagLayout = new GridBagLayout();
	   this.getContentPane().setLayout(gridBagLayout);
	   this.getContentPane().add(modeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			   , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(-70, 5, 0, 15), 0, 0));
		this.getContentPane().add(modePanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
					   , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 200, 0));
	   this.getContentPane().add(historyLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
			   , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 15), 0, 0));
	   this.getContentPane().add(historyPanel, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
			   , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 200, 0));
	   this.getContentPane().add(buttonPanel, new GridBagConstraints(0, 5, 4, 1, 1.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 1, 0));
	   this.getContentPane().add(new JPanel(), new GridBagConstraints(0, 6, 5, 1, 1.0, 1.0
				, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 1));
       pack();
    }

    protected void closeDialog() {
        DiagramExportSettingsDialog.dialog.setVisible(false);
        ConfigurationManager.storePlacement("DiagramExportSettingsDialog", this);
    }

    public void actionPerformed(ActionEvent a) {
        Object source = a.getSource();
        if ((source == manual) || (source == auto)) {
            this.formatSelector.setEnabled(manual.isSelected());
            this.widthField.setEnabled(manual.isSelected());
            this.heightField.setEnabled(manual.isSelected());
            this.formatLabel.setEnabled(manual.isSelected());
            this.widthLabel.setEnabled(manual.isSelected());
			this.heightLabel.setEnabled(manual.isSelected());
        }
    }

    /**
     * Sets up the dialog.
     */
    public static void initialize(Component comp, DiagramExportSettings settings) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        dialog = new DiagramExportSettingsDialog(frame, settings);
    }

    /**
     * Show the initialized dialog.
     *
     * The argument should be null if you want the dialog to come up in
     * the center of the screen.  Otherwise, the argument should be the
     * component on top of which the dialog should appear.
     */
    public static DiagramExportSettings showDialog(Component comp) {
        if (dialog != null) {
            dialog.setLocationRelativeTo(comp);
            ConfigurationManager.restorePlacement("DiagramExportSettingsDialog", dialog,
                    new Rectangle(50, 50, 350, 160));
            dialog.setVisible(true);
        } else {
            System.err.println("DiagramExportSettingsDialog has to be initialize(..)d " +
                    "before showDialog(..) is called.");
        }
        return diagramSettings;
    }
}
