package net.sourceforge.toscanaj.view.dialogs;

import net.sourceforge.toscanaj.canvas.imagewriter.DiagramExportSettings;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
        ButtonGroup manualOrAuto = new ButtonGroup();

        manual = new JRadioButton("Manual");
        manual.addActionListener(this);
        manual.setSelected(!settings.usesAutoMode());
        manualOrAuto.add(manual);
        auto = new JRadioButton("Auto");
        auto.addActionListener(this);
        auto.setSelected(settings.usesAutoMode());
        manualOrAuto.add(auto);

        JLabel formatLabel = new JLabel();
        formatLabel.setText("Format:");
        formatSelector = new JComboBox();
        formatSelector.addItem("PNG");
        formatSelector.addItem("JPG");
        formatSelector.addItem("SVG");
        formatSelector.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        formatSelector.setSelectedIndex(settings.getGraphicFormat()-1);
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
                DiagramExportSettingsDialog.dialog.setVisible(false);
            }
        });

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagramSettings = new DiagramExportSettings( formatSelector.getSelectedIndex() + 1,
                                                             Integer.parseInt(widthField.getText()),
                                                             Integer.parseInt(heightField.getText()),
                                                             auto.isSelected() );
                DiagramExportSettingsDialog.dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(okButton);


        GridBagLayout gridBagLayout = new GridBagLayout();
        this.getContentPane().setLayout(gridBagLayout);
        this.getContentPane().add(manual, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 200, 0));
        this.getContentPane().add(auto, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(formatSelector, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(formatLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(widthLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(heightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(widthField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(heightField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(cancelButton, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(okButton, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 200, 0));
        pack();
    }

    public void actionPerformed(ActionEvent a){
        Object source = a.getSource();
        if( (source == manual) || (source == auto) ) {
            this.formatSelector.setEnabled(manual.isSelected());
            this.widthField.setEnabled(manual.isSelected());
            this.heightField.setEnabled(manual.isSelected());
        }
    }

    /**
     * Sets up the dialog.
     */
    public static void initialize(Component comp, DiagramExportSettings settings)
    {
        Frame frame = JOptionPane.getFrameForComponent( comp );
        dialog = new DiagramExportSettingsDialog(frame, settings);
    }

    /**
     * Show the initialized dialog.
     *
     * The argument should be null if you want the dialog to come up in
     * the center of the screen.  Otherwise, the argument should be the
     * component on top of which the dialog should appear.
     */
    public static DiagramExportSettings showDialog( Component comp )
    {
        if( dialog != null )
        {
            dialog.setLocationRelativeTo(comp);
            dialog.setVisible(true);
        }
        else
        {
            System.err.println( "DiagramExportSettingsDialog has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
        return diagramSettings;
    }
}