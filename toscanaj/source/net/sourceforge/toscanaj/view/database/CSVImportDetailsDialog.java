/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package net.sourceforge.toscanaj.view.database;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.SQLTypeInfo;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

public class CSVImportDetailsDialog extends JDialog {

    private final String filename;
    private final DatabaseConnection connection;

    private String fieldSeparator = ";";

    private JTextField columnNameField;
    private JComboBox dataTypesComboBox;
    private JButton addColumnButton;
    private JButton removeColumnButton;
    private final DBTypesTableModel columnsTableModel = new DBTypesTableModel();
    private JTable columnsDisplayTable;

    private JTextField tableNameField;
    private JButton okButton;

    public CSVImportDetailsDialog(final Frame owner, final String filename,
            final DatabaseConnection connection) throws HeadlessException {
        super(owner, "Text File Import Details", true);
        this.filename = filename;
        this.connection = connection;

        final JLabel headingLabel = new JLabel("Table Configuration Details");

        final JPanel dbSetupPanel = buildDatabaseSetupDetailsPanel();
        final JPanel columnsSetupPanel = buildColumnSetupPanel();
        final JPanel buttonsPanel = buildButtonsPanel();

        final JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        dbSetupPanel.setBorder(BorderFactory.createEtchedBorder());
        dbSetupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(dbSetupPanel);

        columnsSetupPanel.setBorder(BorderFactory.createEtchedBorder());
        columnsSetupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(columnsSetupPanel);

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        this.getContentPane().add(headingLabel, BorderLayout.NORTH);
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    private JPanel buildDatabaseSetupDetailsPanel() {

        final JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());

        tableNameField = new JTextField(20);
        tableNameField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                updateOkButton();
            }
        });

        final JRadioButton commaSeparatorButton = new JRadioButton("comma");
        commaSeparatorButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                setFieldSeparator(";");
            }
        });

        final JRadioButton tabSeparatorButton = new JRadioButton("tab");
        tabSeparatorButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                // @todo should use something smarter here instead of tab string
                setFieldSeparator("	");
            }
        });

        final JRadioButton semicolonSeparatorButton = new JRadioButton(
                "semicolon");
        semicolonSeparatorButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                setFieldSeparator(";");
            }
        });

        final ButtonGroup separatorsButtonGroup = new ButtonGroup();

        separatorsButtonGroup.add(commaSeparatorButton);
        separatorsButtonGroup.add(tabSeparatorButton);
        separatorsButtonGroup.add(semicolonSeparatorButton);

        commaSeparatorButton.setSelected(true);

        detailsPanel.add(new JLabel("Table Name "), new GridBagConstraints(0,
                0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));
        detailsPanel.add(tableNameField, new GridBagConstraints(1, 0, 3, 1, 1,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 2, 2));

        detailsPanel.add(new JLabel("Fields Separator "),
                new GridBagConstraints(0, 1, GridBagConstraints.RELATIVE, 1, 1,
                        0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));
        detailsPanel.add(commaSeparatorButton, new GridBagConstraints(1, 1, 1,
                1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 2, 2));
        detailsPanel.add(tabSeparatorButton, new GridBagConstraints(2, 1,
                GridBagConstraints.RELATIVE, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 2, 2));
        detailsPanel.add(semicolonSeparatorButton, new GridBagConstraints(3, 1,
                GridBagConstraints.RELATIVE, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 2, 2));

        return detailsPanel;
    }

    private JPanel buildColumnSetupPanel() {
        final JPanel columnSetupPanel = new JPanel();
        columnSetupPanel.setLayout(new GridBagLayout());

        final JLabel columnNameFieldLabel = new JLabel("Column Name");
        columnNameField = new JTextField(20);
        columnNameField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                updateAddColumnButton();
            }
        });

        final JLabel dataTypeLabel = new JLabel("Data Type");
        final List<String> dbTypes = getDatabaseTypes();
        Collections.sort(dbTypes);
        final Vector<String> dbTypesVector = new Vector<String>();
        dbTypesVector.add("Choose Column Type");
        dbTypesVector.addAll(dbTypes);
        dataTypesComboBox = new JComboBox<String>(dbTypesVector);
        dataTypesComboBox.setEditable(false);
        dataTypesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                updateAddColumnButton();
            }
        });

        addColumnButton = new JButton("Add");
        addColumnButton.setEnabled(false);
        addColumnButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                columnsTableModel.addRow(columnNameField.getText(),
                        (String) dataTypesComboBox.getSelectedItem());
                columnNameField.setText("");
                dataTypesComboBox.setSelectedIndex(0);
                updateAddColumnButton();
            }
        });

        removeColumnButton = new JButton("Remove");
        removeColumnButton.setEnabled(false);
        removeColumnButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                final int selectedRow = columnsDisplayTable.getSelectedRow();
                columnsTableModel.removeRow(selectedRow);
            }
        });

        columnsDisplayTable = new JTable(columnsTableModel);
        columnsDisplayTable
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final ListSelectionModel rowSM = columnsDisplayTable
                .getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                updateRemoveColumnButton();
            }
        });
        final JScrollPane tableScrollPane = new JScrollPane(columnsDisplayTable);
        columnsDisplayTable.setPreferredScrollableViewportSize(new Dimension(
                200, 200));

        columnSetupPanel.add(columnNameFieldLabel, new GridBagConstraints(0, 0,
                1, 1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));
        columnSetupPanel.add(columnNameField, new GridBagConstraints(1, 0, 1,
                1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 2, 2));

        columnSetupPanel.add(dataTypeLabel, new GridBagConstraints(0, 1, 1, 1,
                1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 2, 2));
        columnSetupPanel.add(dataTypesComboBox, new GridBagConstraints(1, 1, 1,
                1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 2, 2));

        columnSetupPanel.add(addColumnButton, new GridBagConstraints(1, 2, 1,
                1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 2, 2));

        columnSetupPanel.add(tableScrollPane, new GridBagConstraints(2, 0, 1,
                2, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 2, 2));

        columnSetupPanel.add(removeColumnButton, new GridBagConstraints(2, 2,
                1, 1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));

        return columnSetupPanel;
    }

    private JPanel buildButtonsPanel() {
        final JPanel buttonsPanel = new JPanel();

        okButton = new JButton("Import file data");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                importData();
                System.out.println("should be importing data into table here");
                dispose();
            }
        });

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                dispose();
            }

        });

        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonsPanel.add(cancelButton);

        updateOkButton();

        return buttonsPanel;
    }

    private void setFieldSeparator(final String separator) {
        this.fieldSeparator = separator;
    }

    private void updateAddColumnButton() {
        if ((columnNameField.getText().length() > 0)
                && (dataTypesComboBox.getSelectedIndex() != 0)) {
            addColumnButton.setEnabled(true);
        } else {
            addColumnButton.setEnabled(false);
        }
        updateOkButton();
    }

    private void updateRemoveColumnButton() {
        if (columnsDisplayTable.getSelectedRow() >= 0) {
            removeColumnButton.setEnabled(true);
        } else {
            removeColumnButton.setEnabled(false);
        }
        updateOkButton();
    }

    private void updateOkButton() {
        if (tableNameField.getText().length() <= 0) {
            okButton.setEnabled(false);
            return;
        }
        if (columnsTableModel.getRowCount() <= 0) {
            okButton.setEnabled(false);
            return;
        }
        okButton.setEnabled(true);
    }

    private List<String> getDatabaseTypes() {
        final List<String> res = new ArrayList<String>();
        try {
            final Collection<SQLTypeInfo> typeNames = connection
                    .getDatabaseSupportedTypeNames();
            for (SQLTypeInfo cur : typeNames) {
                res.add(cur.getTypeName().toLowerCase());
            }
        } catch (final DatabaseException e) {
            ErrorDialog.showError(this, e,
                    "Error retrieving types supported by database");
        }
        return res;
    }

    private void importData() {
        String createTableStatement = "CREATE TEXT TABLE "
                + tableNameField.getText() + " {" + "\n";
        for (int i = 0; i < columnsTableModel.getRowCount(); i++) {
            createTableStatement = createTableStatement
                    + columnsTableModel.getValueAt(i, 0) + " "
                    + columnsTableModel.getValueAt(i, 1);
            if (i != (columnsTableModel.getRowCount() - 1)) {
                createTableStatement = createTableStatement + "," + "\n";
            }
        }
        createTableStatement += "};";

        final String importDataStatement = "SET TABLE "
                + tableNameField.getText() + " SOURCE \"" + filename + ";fs="
                + this.fieldSeparator + "\"";

        // System.out.println("statement 1: \n" + createTableStatement);
        // System.out.println("statement 2: \n" + importDataStatement);

        try {
            connection.executeUpdate(createTableStatement);
            connection.executeUpdate(importDataStatement);
        } catch (final DatabaseException e) {
            ErrorDialog.showError(this, e, "Error importing text file");
        }

    }

    private class DBTypesTableModel extends AbstractTableModel {

        final private String[] columnNames = { "Column Name", "Data Type" };
        private final List<List<String>> rows = new ArrayList<List<String>>();

        public int getRowCount() {
            return rows.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(final int columnIndex) {
            return columnNames[columnIndex];
        }

        public Object getValueAt(final int rowIndex, final int colIndex) {
            final List rowData = rows.get(rowIndex);
            return rowData.get(colIndex);
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int colIndex) {
            return true;
        }

        public void addRow(final String newColNameStr, final String colDataType) {
            final List<String> rowData = new ArrayList<String>();
            rowData.add(0, newColNameStr);
            rowData.add(1, colDataType);
            rows.add(rowData);
            fireTableDataChanged();
        }

        public void removeRow(final int rowIndex) {
            rows.remove(rowIndex);
            fireTableDataChanged();
        }
    }

}
