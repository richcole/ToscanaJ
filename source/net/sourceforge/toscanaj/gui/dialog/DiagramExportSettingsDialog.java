/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import net.sourceforge.toscanaj.canvas.imagewriter.DiagramExportSettings;
import net.sourceforge.toscanaj.canvas.imagewriter.GraphicFormat;
import net.sourceforge.toscanaj.canvas.imagewriter.GraphicFormatRegistry;
import net.sourceforge.toscanaj.controller.ConfigurationManager;

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
    private JComboBox formatSelector;
    private JTextField widthField;
    private JTextField heightField;

    /**
     * No public instances of this dialog.
     */
    private DiagramExportSettingsDialog(Frame frame, DiagramExportSettings settings) {
        super(frame, true);
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

        JLabel formatLabel = new JLabel();
        formatLabel.setText("Format:");
        formatSelector = new JComboBox();
        Iterator it = GraphicFormatRegistry.getIterator();
        while (it.hasNext()) {
            formatSelector.addItem(it.next());
        }
        formatSelector.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        formatSelector.setSelectedItem(settings.getGraphicFormat());
        formatSelector.setEnabled(!settings.usesAutoMode());

        JLabel widthLabel = new JLabel();
        widthLabel.setText("Width:");
        widthField = new JTextField();
        widthField.setText(String.valueOf(settings.getImageWidth()));
        widthField.setEnabled(false);
        widthField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        widthField.setEnabled(!settings.usesAutoMode());

        JLabel heightLabel = new JLabel();
        heightLabel.setText("Height:");
        heightField = new JTextField();
        heightField.setText(String.valueOf(settings.getImageHeight()));
        heightField.setEnabled(false);
        heightField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        heightField.setEnabled(!settings.usesAutoMode());

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
                closeDialog();
            }
        });
        getRootPane().setDefaultButton(okButton);

        JPanel buttonPanel = new JPanel();
        GridBagLayout buttonLayout = new GridBagLayout();
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.add(cancelButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));
        buttonPanel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));

        GridBagLayout gridBagLayout = new GridBagLayout();
        this.getContentPane().setLayout(gridBagLayout);
        this.getContentPane().add(modeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(auto, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(manual, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(formatLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(formatSelector, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(widthLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(widthField, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(heightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(heightField, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(buttonPanel, new GridBagConstraints(0, 4, 4, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 0));
        this.getContentPane().add(new JPanel(), new GridBagConstraints(0, 5, 4, 1, 0.0, 1.0
                , GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
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
                    new Rectangle(50, 50, 335, 160));
            dialog.setVisible(true);
        } else {
            System.err.println("DiagramExportSettingsDialog has to be initialize(..)d " +
                    "before showDialog(..) is called.");
        }
        return diagramSettings;
    }
}