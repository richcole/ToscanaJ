/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.database.Column;
import org.apache.batik.ext.swing.GridBagConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

public class NominalScaleEditorDialog extends JDialog {
    private boolean result;

    private Column column;
    private DatabaseConnection databaseConnection;

    private JList columnValuesListView;
    private JList attributeListView;
    private DefaultListModel columnValuesListModel;
    private DefaultListModel attributeListModel;

    private JTextField scaleTitleField;

    public NominalScaleEditorDialog(Frame owner, Column column, DatabaseConnection databaseConnection) {
        super(owner);
        this.column = column;
        this.databaseConnection = databaseConnection;

        createControls();
        fillControls();

        result = false;
    }

    private void createControls() {
        setModal(true);
        setTitle("Nominal Scale Generator");
        getContentPane().setLayout(new GridBagLayout());

        // -- title pane ---
        this.scaleTitleField = new JTextField();
        scaleTitleField.setText(column.getName() + " (nominal)");
        JPanel titlePane = new JPanel(new GridLayout(1, 0));
        titlePane.add(new Label("Scale Title"));
        titlePane.add(scaleTitleField);
        getContentPane().add(
                titlePane,
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstants.CENTER,
                        GridBagConstants.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

        JPanel tablePane = new JPanel(new GridBagLayout());
        this.columnValuesListModel = new DefaultListModel();
        this.columnValuesListView = new JList(columnValuesListModel);
        this.columnValuesListView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getClickCount() == 2) {
                    addValuesToSelection();
                }
            }
        });
        JPanel moveButtonPane = new JPanel(new GridLayout(2,1));
        JButton addButton = new JButton(">");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addValuesToSelection();
            }
        });
        JButton removeButton = new JButton("<");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeValuesFromSelection();
            }
        });
        moveButtonPane.add(addButton);
        moveButtonPane.add(removeButton);
        this.attributeListModel = new DefaultListModel();
        this.attributeListView = new JList(attributeListModel);
        this.attributeListView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getClickCount() == 2) {
                    removeValuesFromSelection();
                }
            }
        });
        tablePane.add(new LabeledScrollPaneView("Available Values", this.columnValuesListView),
            new GridBagConstraints(
                    0, 0, 1, 1, 1, 1,
                    GridBagConstants.CENTER,
                    GridBagConstants.BOTH,
                    new Insets(5, 5, 5, 5),
                    0, 0
            )
        );
        tablePane.add(moveButtonPane,
                new GridBagConstraints(
                        1, 0, 1, 1, 0, 0,
                        GridBagConstants.CENTER,
                        GridBagConstants.NONE,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.add(new LabeledScrollPaneView("Selected Attributes", this.attributeListView),
                new GridBagConstraints(
                        2, 0, 1, 1, 1, 1,
                        GridBagConstants.CENTER,
                        GridBagConstants.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.setBorder(BorderFactory.createEtchedBorder());
        getContentPane().add(
                tablePane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1, 1,
                        GridBagConstants.CENTER,
                        GridBagConstants.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                result = false;
                hide();
            }
        });
        JButton createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                result = true;
                hide();
            }
        });
        buttonPane.add(cancelButton);
        buttonPane.add(createButton);
        getContentPane().add(
                buttonPane,
                new GridBagConstraints(
                        0, 2, 1, 1, 1, 0,
                        GridBagConstants.CENTER,
                        GridBagConstants.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        pack();
    }

    private void addValuesToSelection() {
        for (int i = this.columnValuesListView.getSelectedValues().length - 1; i>=0; i--) {
            Object o = this.columnValuesListView.getSelectedValues()[i];
            this.columnValuesListModel.removeElement(o);
            this.attributeListModel.addElement(o);
        }
    }

    private void removeValuesFromSelection() {
        for (int i = this.attributeListView.getSelectedValues().length - 1; i>=0; i--) {
            Object o = this.attributeListView.getSelectedValues()[i];
            this.attributeListModel.removeElement(o);
            this.columnValuesListModel.addElement(o);
        }
    }

    private void fillControls() {
        // --- get a list of the values in column
        java.util.List resultSet = null;
        try {
            String query = "SELECT DISTINCT " + column.getName() + " FROM " +
                    column.getTable().getName() + ";";
            resultSet = databaseConnection.queryColumn(query, 1);
        } catch (DatabaseException e) {
        }

        for (Iterator it = resultSet.iterator(); it.hasNext();) {
            this.columnValuesListModel.addElement((String) it.next());
        }
    }

    public boolean execute() {
        result = false;
        show();
        return result;
    }

    public Object[] getValues() {
        return this.attributeListModel.toArray();
    }

    public String getDiagramTitle() {
        return this.scaleTitleField.getText();
    }
}
