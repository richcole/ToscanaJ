/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.database.Column;

import javax.swing.*;
import javax.swing.event.*;
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
    private JList dividersList;

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

        dividersList = new JList(dividersModel);
        dividersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        LabeledScrollPaneView dividerEditor = new LabeledScrollPaneView("Dividers", dividersList);

        JPanel dividerPane = new JPanel();
        dividerPane.setLayout(new BoxLayout(dividerPane, BoxLayout.Y_AXIS));
        dividerPane.add(dividerEditor);

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));

        entryPanel.add(makeAddDividerPanel());

        final JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(hasSelectedDivider());


        dividersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                removeButton.setEnabled(e.getFirstIndex() != -1);
            }
        });
        dividersModel.addListDataListener(new ListDataListener() {
            private void updateRemoveButton() {
                removeButton.setEnabled(dividersModel.getSize() > 0 && hasSelectedDivider());
            }

            public void intervalAdded(ListDataEvent e) {
                updateRemoveButton();
            }

            public void intervalRemoved(ListDataEvent e) {
                updateRemoveButton();
            }

            public void contentsChanged(ListDataEvent e) {
                updateRemoveButton();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selected = dividersList.getSelectedIndices();
                for (int i = selected.length; --i >= 0;) {
                    removeDivider(selected[i]);
                }
            }
        });

        entryPanel.add(removeButton);


        JButton removeAllButton = makeActionOnCorrectScaleButton("Remove All");
        removeAllButton.addActionListener(new ActionListener() {
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

    public void removeDivider(int i) {
        dividersModel.removeElementAt(i);
    }

    private boolean hasSelectedDivider() {
        return dividersList.getSelectedIndex() != -1;
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
        addField.setText("");
        addButton = new JButton("Add");
        addButton.setEnabled(false);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDelimiter(addField.getValue());
            }
        });

        addField.getDocument().addDocumentListener(new DocumentListener() {
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

        addField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (addField.isValid()) {
                    addDelimiter(addField.getValue());
                }
            }
        });

        addPanel.add(addField);
        addPanel.add(addButton);
        return addPanel;
    }

    private DefaultListModel dividersModel;

    public void addDelimiter(double value) {
        int i;
        for (i = 0; i < dividersModel.size(); i++) {
            final double currDivider = getDelimiter(i);
            if (value == currDivider) {
                return;
            }
            if (value < currDivider) {
                break;
            }
        }
        dividersModel.insertElementAt(new Double(value), i);
        addField.setText("");
    }

    private double getDelimiter(int i) {
        return ((Double) dividersModel.elementAt(i)).doubleValue();
    }

    public java.util.List getDividers() {
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
        return dividersModel.getSize() > 0;
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
