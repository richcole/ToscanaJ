package net.sourceforge.toscanaj.view.dialogs;

import net.sourceforge.toscanaj.canvas.DrawingCanvas;

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
public class DiagramExportDialog extends JDialog {
    /**
     * The instance used.
     */
    static private DiagramExportDialog dialog = null;

    /**
     * Stores the graphic format set by the user.
     */
    static private int format;

    /**
     * Stores the width set by the user.
     */
    static private int width;

    /**
     * Stores the height set by the user.
     */
    static private int height;

    /**
     * No public instances of this dialog.
     */
    private DiagramExportDialog(Frame frame, int width, int height) {
        super(frame, true);

        JLabel formatLabel = new JLabel();
        formatLabel.setText("Format:");
        final JComboBox formatSelector = new JComboBox();
        formatSelector.addItem("PNG");
        formatSelector.addItem("JPG");
        formatSelector.addItem("SVG");
        formatSelector.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        JLabel widthLabel = new JLabel();
        widthLabel.setText("Width:");
        final JTextField widthField = new JTextField();
        widthField.setText(String.valueOf(width));
        widthField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        JLabel heightLabel = new JLabel();
        heightLabel.setText("Height:");
        final JTextField heightField = new JTextField();
        heightField.setText(String.valueOf(height));
        heightField.setAlignmentX(JComponent.RIGHT_ALIGNMENT);

        //buttons
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DiagramExportDialog.format = DrawingCanvas.FORMAT_UNSET;
                DiagramExportDialog.dialog.setVisible(false);
            }
        });

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DiagramExportDialog.format = formatSelector.getSelectedIndex() + 1;
                DiagramExportDialog.height = Integer.parseInt(heightField.getText());
                DiagramExportDialog.width = Integer.parseInt(widthField.getText());
                DiagramExportDialog.dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(okButton);

        GridBagLayout gridBagLayout = new GridBagLayout();
        this.getContentPane().setLayout(gridBagLayout);
        this.getContentPane().add(formatSelector, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(formatLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(widthLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(heightLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 20), 0, 0));
        this.getContentPane().add(widthField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(heightField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(cancelButton, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 200, 0));
        this.getContentPane().add(okButton, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 200, 0));

        pack();
    }
    /**
     * Sets up the dialog.
     */
    public static void initialize(Component comp, int width, int height)
    {
        Frame frame = JOptionPane.getFrameForComponent( comp );
        dialog = new DiagramExportDialog( frame, width, height );
    }

    /**
     * Show the initialized dialog.
     *
     * The argument should be null if you want the dialog to come up in
     * the center of the screen.  Otherwise, the argument should be the
     * component on top of which the dialog should appear.
     */
    public static boolean showDialog( Component comp )
    {
        if( dialog != null )
        {
            dialog.setLocationRelativeTo(comp);
            dialog.setVisible(true);
        }
        else
        {
            System.err.println( "DiagramExportDialog has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
        return format != DrawingCanvas.FORMAT_UNSET;
    }

    /**
     * Returns the format set by the user on the last showDialog() call.
     */
    static public int getImageFormat() {
        return format;
    }

    /**
     * Returns the width set by the user on the last showDialog() call.
     */
    static public int getImageWidth() {
        return width;
    }

    /**
     * Returns the height set by the user on the last showDialog() call.
     */
    static public int getImageHeight() {
        return height;
    }
}