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
import net.sourceforge.toscanaj.model.Column;
import util.Assert;
import util.CollectionFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class OrdinalScaleEditorDialog extends JDialog {
    Column column;
    boolean result;
    private JTextField titleEditor = new JTextField();

    private JButton addButton;
    private DoubleNumberField addField;

    public OrdinalScaleEditorDialog(Frame owner, Column column) {
        super(owner);
        this.column = column;

        layoutDialog();
        pack();
    }

    public boolean execute() {
        result = false;
        show();
        return result;
    }

    private void layoutDialog() {
        setModal(true);
        setTitle("Ordinal scale editor");
        getContentPane().setLayout(new BorderLayout());

        titleEditor.setText(column.getName() + " (ordinal)");
        getContentPane().add(new LabeledScrollPaneView("Title", titleEditor), BorderLayout.NORTH);


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        dividersModel = new DefaultListModel();

        JList dividers = new JList(dividersModel);
        LabeledScrollPaneView dividerEditor = new LabeledScrollPaneView("Dividers", dividers);

        JPanel dividerPane = new JPanel();
        dividerPane.setLayout(new BoxLayout(dividerPane, BoxLayout.Y_AXIS));
        dividerPane.add(dividerEditor);

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));

        entryPanel.add(makeAddDividerPanel());
        entryPanel.add(new JButton("Remove"));

        JButton removeAllButton = makeActionOnCorrectScaleButton("Remove All");
        removeAllButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                removeAllDividers();
            }
        });

        entryPanel.add(removeAllButton);

        dividerPane.add(entryPanel);

        centerPanel.add(dividerPane);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(makeButtonsPane(), BorderLayout.SOUTH);
    }

    public void removeAllDividers() {
        dividersModel.removeAllElements();
    }

    private JButton makeActionOnCorrectScaleButton(final String label) {
        JButton actionButton = new JButton(label);
        dividersModel.addListDataListener(new UpdateButtonForCorrectModelStateListDataListener(actionButton));
        actionButton.setEnabled(isScaleCorrect());
        return actionButton;
    }

    private JPanel makeAddDividerPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.X_AXIS));
        addField = new DoubleNumberField(0, 10);
        addButton = new JButton("Add");

        addButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addDelimiter(addField.getValue());
            }
        });

        addField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            public void removeUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            public void changedUpdate(DocumentEvent e) {
                 processDocumentEvent(e);
            }

            private void processDocumentEvent(DocumentEvent e) {
                addButton.setEnabled(addField.isValid());
            }

        });

        addPanel.add(addField);
        addPanel.add(addButton);
        return addPanel;
    }

    private DefaultListModel dividersModel;


    public void addDelimiter(double value) {
        int i;
        for(i=0; i<dividersModel.size(); i++){
            final double currDivider = getDelimiter(i);
            if(value == currDivider){
                return;
            }
            if(value<currDivider){
                break;
            }
        }
        dividersModel.insertElementAt(new Double(value), i);
    }

    private double getDelimiter(int i) {
        return ((Double)dividersModel.elementAt(i)).doubleValue();
    }

    public java.util.List getDividers(){
        return Arrays.asList(dividersModel.toArray());
    }


    public String getDiagramTitle() {
        return titleEditor.getText();
    }

    private JPanel makeButtonsPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout());

        final JButton okButton = makeActionOnCorrectScaleButton("Ok");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                result = true;
            }
        });

        buttonPane.add(okButton);

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                result = false;
            }
        });
        buttonPane.add(cancelButton);
        return buttonPane;
    }

    private boolean isScaleCorrect() {
        return dividersModel.getSize()>0;
    }

    private class UpdateButtonForCorrectModelStateListDataListener implements ListDataListener {
        private final JButton actionButton;

        public UpdateButtonForCorrectModelStateListDataListener(JButton button) {
            this.actionButton = button;
        }

        private void updateStateOfOkButton() {
            actionButton.setEnabled(isScaleCorrect());
        }

        public void contentsChanged(ListDataEvent e) {
            updateStateOfOkButton();
        }

        public void intervalAdded(ListDataEvent e) {
            updateStateOfOkButton();
        }

        public void intervalRemoved(ListDataEvent e) {
            updateStateOfOkButton();
        }
    }


}
