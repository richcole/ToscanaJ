/*
 * Created by IntelliJ IDEA.
 * User: Serhiy Yevtushenko
 * Date: Jun 29, 2002
 * Time: 11:38:32 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OrdinalScaleEditorDialog extends JDialog {
    TableColumnPair tableColumnPair;
    boolean result;
    private JTextField titleEditor = new JTextField();

    public OrdinalScaleEditorDialog(Frame owner, TableColumnPair tableColumnPair) {
        super(owner);
        this.tableColumnPair = tableColumnPair;
    }

    public boolean execute(){
        result = false;
        setModal(true);
        setTitle("Ordinal scale editor");
        getContentPane().setLayout(new BorderLayout());

        titleEditor.setText(tableColumnPair.getColumn().getName()+" (ordinal)");
        getContentPane().add(new LabeledScrollPaneView("Title", titleEditor), BorderLayout.NORTH);



        getContentPane().add(makeButtonsPane(), BorderLayout.SOUTH);
        pack();
        show();
        return result;
    }

    public String getDiagramTitle(){
        return titleEditor.getText();
    }

    private JPanel makeButtonsPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout());

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dispose();
                result = true;
            }
        });
        buttonPane.add(okButton);
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dispose();
                result = false;
            }
        });
        buttonPane.add(cancelButton);
        return buttonPane;
    }

}
