/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;

import org.tockit.swing.preferences.ExtendedPreferences;

public class NominalScaleEditorDialog extends JDialog {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(NominalScaleEditorDialog.class);

    /**
     * This is used to figure out if multiple columns are involved and we need
     * to add the column names to the attribute labels.
     * 
     * This is a hack to get more things going, but in the end it is just
     * another sign that the nominal scale generator as it is is no good.
     */
    private static boolean multipleColumnsUsed;
    private Column columnUsed;

    private boolean result;

    private TableColumnPair selectedTableColumnPair;
    private final DatabaseConnection databaseConnection;

    private JList<ColumnValue> columnValuesListView;
    private JList<Object> attributeListView;
    private DefaultListModel<ColumnValue> columnValuesListModel;
    private DefaultListModel<Object> attributeListModel;
    @SuppressWarnings("FieldCanBeLocal")
    private JButton cancelButton;
    private JButton createButton;

    private JTextField scaleTitleField;
    private JComboBox<Object> columnChooser;
    private final DatabaseSchema databaseSchema;
    private JButton andButton;
    private JButton orButton;
    private JButton notButton;

    private static final int MINIMUM_WIDTH = 500;
    private static final int MINIMUM_HEIGHT = 300;
    private static final Rectangle DEFAULT_PLACEMENT = new Rectangle(10, 10,
            MINIMUM_WIDTH, MINIMUM_HEIGHT);

    public interface SqlFragment {
        String getAttributeLabel();

        String getSqlClause();

        String getClosedAttributeLabel();
    }

    private interface ColumnValue {
        String getValue();

        String getSqlClause(Column column);

        Font deriveFont(Font f);
    }

    private class OrdinaryColumnValue implements ColumnValue {
        private final String value;

        public OrdinaryColumnValue(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String getSqlClause(final Column column) {
            if (column.getType() == Types.VARCHAR) {
                return " = '" + this.value + "'";
            } else {
                return " = " + this.value;
            }
        }

        public Font deriveFont(final Font f) {
            return f;
        }
    }

    private class NullColumnValue implements ColumnValue {
        private final String value;

        public NullColumnValue(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String getSqlClause(final Column column) {
            return " IS " + this.value;
        }

        public Font deriveFont(final Font f) {
            return f.deriveFont(Font.ITALIC);
        }
    }

    private static class TableColumnValueTriple implements SqlFragment {
        private final TableColumnPair tableColumnPair;
        private final ColumnValue colValue;

        public TableColumnValueTriple(final TableColumnPair tableColumnPair,
                final ColumnValue colValue) {
            this.tableColumnPair = tableColumnPair;
            this.colValue = colValue;
        }

        public TableColumnPair getTableColumnPair() {
            return tableColumnPair;
        }

        public String getValue() {
            return colValue.getValue();
        }

        @Override
        public String toString() {
            return this.getSqlClause();
        }

        public String getAttributeLabel() {
            if (multipleColumnsUsed) {
                return this.tableColumnPair.getColumn().getDisplayName() + ": "
                        + this.colValue.getValue();
            } else {
                return this.colValue.getValue();
            }
        }

        public String getSqlClause() {
            return this.tableColumnPair.getSqlExpression()
                    + this.colValue.getSqlClause(tableColumnPair.getColumn());
        }

        public String getClosedAttributeLabel() {
            return this.getAttributeLabel();
        }
    }

    private static class Disjunction implements SqlFragment {
        private final SqlFragment[] sqlFragments;

        public Disjunction(final SqlFragment[] sqlFragments) {
            this.sqlFragments = sqlFragments;
        }

        public String getAttributeLabel() {
            String res = "";
            for (int i = 0; i < this.sqlFragments.length; i++) {
                final SqlFragment curFragment = sqlFragments[i];
                if (i == 0) {
                    res = curFragment.getClosedAttributeLabel();
                } else {
                    res = res + " or " + curFragment.getClosedAttributeLabel();
                }
            }
            return res;
        }

        public String getSqlClause() {
            String res = "";
            for (int i = 0; i < this.sqlFragments.length; i++) {
                final SqlFragment curFragment = sqlFragments[i];
                if (i == 0) {
                    res = "(" + curFragment.getSqlClause() + ")";
                } else {
                    res = res + " OR (" + curFragment.getSqlClause() + ")";
                }
            }
            return res;
        }

        @Override
        public String toString() {
            return getSqlClause();
        }

        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

    private static class Conjunction implements SqlFragment {
        private final SqlFragment[] sqlFragments;

        public Conjunction(final SqlFragment[] sqlFragments) {
            this.sqlFragments = sqlFragments;
        }

        public String getAttributeLabel() {
            String res = "";
            for (int i = 0; i < this.sqlFragments.length; i++) {
                final SqlFragment curFragment = sqlFragments[i];
                if (i == 0) {
                    res = curFragment.getClosedAttributeLabel();
                } else {
                    res = res + " and " + curFragment.getClosedAttributeLabel();
                }
            }
            return res;
        }

        public String getSqlClause() {
            String res = "";
            for (int i = 0; i < this.sqlFragments.length; i++) {
                final SqlFragment curSqlFragment = sqlFragments[i];
                if (i == 0) {
                    res = "(" + curSqlFragment.getSqlClause() + ")";
                } else {
                    res = res + " AND (" + curSqlFragment.getSqlClause() + ")";
                }
            }
            return res;
        }

        @Override
        public String toString() {
            return getSqlClause();
        }

        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

    private static class Negation implements SqlFragment {
        private final SqlFragment sqlFragment;

        public Negation(final SqlFragment sqlFragment) {
            this.sqlFragment = sqlFragment;
        }

        public String getAttributeLabel() {
            return "NOT " + this.sqlFragment.getClosedAttributeLabel();
        }

        public String getSqlClause() {
            return "NOT (" + this.sqlFragment.getSqlClause() + ")";
        }

        @Override
        public String toString() {
            return getSqlClause();
        }

        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

    private class ColumnValuesListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value, final int index, final boolean isSelected,
                final boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            final ColumnValue colValue = (ColumnValue) value;
            setText(colValue.getValue());
            setFont(colValue.deriveFont(list.getFont()));
            return this;
        }

    }

    public NominalScaleEditorDialog(final Frame owner,
            final DatabaseConnection databaseConnection,
            final DatabaseSchema databaseSchema) {
        super(owner);
        this.databaseConnection = databaseConnection;
        this.databaseSchema = databaseSchema;

        multipleColumnsUsed = false;
        this.columnUsed = null;

        preferences.restoreWindowPlacement(this, DEFAULT_PLACEMENT);
        // to enforce the minimum size during resizing of the JDialog
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                if (width < MINIMUM_WIDTH) {
                    width = MINIMUM_WIDTH;
                }
                if (height < MINIMUM_HEIGHT) {
                    height = MINIMUM_HEIGHT;
                }
                setSize(width, height);
            }

            @Override
            public void componentShown(final ComponentEvent e) {
                componentResized(e);
            }
        });

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
        this.scaleTitleField.addKeyListener(new KeyAdapter() {
            private void validateTextField() {
                if (scaleTitleField.getText().trim().equals("")) {
                    createButton.setEnabled(false);
                } else {
                    createButton.setEnabled(true);
                }
            }

            @Override
            public void keyTyped(final KeyEvent e) {
                validateTextField();
                setCreateButtonStatus();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                validateTextField();
                setCreateButtonStatus();
            }
        });
        final JPanel titlePane = new JPanel(new GridBagLayout());
        titlePane.add(new Label("Scale Title: "), new GridBagConstraints(0, 0,
                1, 1, 0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));

        titlePane.add(scaleTitleField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 2, 2));

        getContentPane().add(
                titlePane,
                new GridBagConstraints(0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
                        0, 0));

        this.columnChooser = new JComboBox<Object>();
        this.columnChooser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cb = (JComboBox) e.getSource();
                final Object newTableColumnPair = cb.getSelectedItem();
                if (newTableColumnPair != selectedTableColumnPair
                        && newTableColumnPair instanceof TableColumnPair) {
                    selectedTableColumnPair = (TableColumnPair) newTableColumnPair;
                    final Object firstItem = cb.getItemAt(0);
                    if (!(firstItem instanceof TableColumnPair)) {
                        cb.removeItemAt(0);
                    }
                } else {
                    selectedTableColumnPair = null;
                }
                fillAvailableValueList();
            }
        });

        final JPanel tablePane = new JPanel(new GridBagLayout());
        this.columnValuesListModel = new DefaultListModel<ColumnValue>();
        this.columnValuesListView = new JList<ColumnValue>(columnValuesListModel);
        this.columnValuesListView.setCellRenderer(new ColumnValuesListRenderer());
        this.columnValuesListView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    addValuesToSelection();
                }
            }
        });

        final JPanel moveButtonPane = new JPanel(new GridLayout(2, 1));
        final JButton addButton = new JButton(">");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addValuesToSelection();
            }
        });
        final JButton removeButton = new JButton("<");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                removeValuesFromSelection();
            }
        });
        moveButtonPane.add(addButton);
        moveButtonPane.add(removeButton);
        this.attributeListModel = new DefaultListModel<Object>();
        this.attributeListView = new JList<Object>(attributeListModel);
        this.attributeListView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    removeValuesFromSelection();
                }
            }
        });

        final JPanel combinationButtonPanel = new JPanel(new GridBagLayout());
        this.andButton = new JButton("AND");
        this.andButton.setEnabled(false);
        this.andButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                createConjunction();
            }
        });

        this.orButton = new JButton("OR");
        this.orButton.setEnabled(false);
        this.orButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                createDisjunction();
            }
        });

        this.notButton = new JButton("NOT");
        this.notButton.setEnabled(false);
        this.notButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent arg0) {
                createNegation();
            }
        });

        combinationButtonPanel.add(this.andButton, new GridBagConstraints(0, 0,
                1, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        combinationButtonPanel.add(this.orButton, new GridBagConstraints(1, 0,
                1, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        combinationButtonPanel.add(this.notButton, new GridBagConstraints(2, 0,
                1, 1, 1, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        tablePane.add(new LabeledPanel("Columns", this.columnChooser, false),
                new GridBagConstraints(0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
                        0, 0));
        tablePane.add(new LabeledPanel("Available Values",
                this.columnValuesListView), new GridBagConstraints(0, 1, 1, 2,
                1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        tablePane.add(moveButtonPane, new GridBagConstraints(1, 0, 1, 3, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 5, 5), 0, 0));

        final JPanel selectedClausePane = new JPanel(new BorderLayout());
        selectedClausePane.add(new LabeledPanel("Selected Clauses",
                this.attributeListView), BorderLayout.CENTER);
        selectedClausePane.add(combinationButtonPanel, BorderLayout.SOUTH);

        tablePane.add(selectedClausePane, new GridBagConstraints(2, 0, 1, 3, 1,
                1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        tablePane.setBorder(BorderFactory.createEtchedBorder());

        getContentPane().add(
                tablePane,
                new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));

        final JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                closeDialog(false);
            }
        });
        createButton = new JButton("Create");
        createButton.setEnabled(!scaleTitleField.getText().equals("")
                && attributeListView.getModel().getSize() != 0);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                closeDialog(true);
            }
        });

        buttonPane.add(createButton);
        buttonPane.add(cancelButton);
        getContentPane().add(
                buttonPane,
                new GridBagConstraints(0, 2, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
                        0, 0));

        attributeListView.getModel().addListDataListener(
                new UpdateButtonForCorrectModelStateListDataListener(
                        createButton));
        attributeListView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                final boolean combinationPossible = attributeListView
                        .getSelectedIndices().length >= 2;
                andButton.setEnabled(combinationPossible);
                orButton.setEnabled(combinationPossible);
                final boolean onlyOneItemSelected = attributeListView
                        .getSelectedIndices().length == 1;
                notButton.setEnabled(onlyOneItemSelected);
            }
        });
        pack();
    }

    private void closeDialog(final boolean res) {
        preferences.storeWindowPlacement(this);
        result = res;
        setVisible(false);
    }

    private void addValuesToSelection() {
        for (int i = this.columnValuesListView.getSelectedValuesList().size() - 1; i >= 0; i--) {
            final ColumnValue value = this.columnValuesListView.getSelectedValuesList().get(i);
            this.attributeListModel.addElement(new TableColumnValueTriple(
                    this.selectedTableColumnPair, value));
            if (this.columnUsed == null) {
                this.columnUsed = this.selectedTableColumnPair.getColumn();
            } else if (this.columnUsed != this.selectedTableColumnPair
                    .getColumn()) {
                multipleColumnsUsed = true;
            }
        }
        fillAvailableValueList();
    }

    private void removeValuesFromSelection() {
        for (int i = this.attributeListView.getSelectedValuesList().size() - 1; i >= 0; i--) {
            final Object selectedItem = this.attributeListView.getSelectedValuesList().get(i);
            this.attributeListModel.removeElement(selectedItem);
        }
        fillAvailableValueList();
    }

    private void fillControls() {
        this.columnChooser.removeAllItems();
        this.columnChooser.addItem("<Please select a column>");
        final List<Table> tables = this.databaseSchema.getTables();
        for (final Table table : tables) {
            final List<Column> columns = table.getColumns();
            for (final Column column : columns) {
                this.columnChooser.addItem(new TableColumnPair(table, column));
            }
        }
    }

    /**
     * @todo we need caching here
     */
    private void fillAvailableValueList() {
        this.columnValuesListModel.clear();
        if (selectedTableColumnPair == null) {
            return;
        }
        try {
            final String query = "SELECT DISTINCT "
                    + selectedTableColumnPair.getSqlExpression() + " FROM "
                    + selectedTableColumnPair.getTable().getSqlExpression()
                    + ";";
            List<String> resultSet = databaseConnection.queryColumn(query, 1);
            if (!resultSet.isEmpty()) {
                if (checkIfColumnAllowsNullValues()) {
                    this.columnValuesListModel.addElement(new NullColumnValue("NULL"));
                }
            }
            for (String value : resultSet) {
                if (!valueSelected(selectedTableColumnPair, value)) {
                    this.columnValuesListModel.addElement(new OrdinaryColumnValue(value));
                }
            }
        } catch (final DatabaseException e) {
            ErrorDialog.showError(this, e, "Database query failed");
        }
    }

    private boolean valueSelected(final TableColumnPair tabCol,
            final String value) {
        for (int i = 0; i < this.attributeListModel.size(); i++) {
            if (this.attributeListModel.get(i) instanceof TableColumnValueTriple) {
                final TableColumnValueTriple tcv = (TableColumnValueTriple) this.attributeListModel.get(i);
                if (tcv.getTableColumnPair().equals(tabCol) && tcv.getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean execute() {
        result = false;
        setVisible(true);
        return result;
    }

    public Object[] getValues() {
        return this.attributeListModel.toArray();
    }

    public String getDiagramTitle() {
        return this.scaleTitleField.getText();
    }

    protected void setCreateButtonStatus() {
        if (scaleTitleField.getText().equals("")
                && attributeListView.getModel().getSize() == 0) {
            createButton.setEnabled(false);
        } else {
            if (!scaleTitleField.getText().equals("")
                    && attributeListView.getModel().getSize() != 0) {
                createButton.setEnabled(true);
            } else {
                createButton.setEnabled(false);
            }
        }
    }

    private boolean isScaleCorrect() {
        return attributeListView.getModel().getSize() > 0;
    }

    private void createConjunction() {
        final int pos = this.attributeListView.getSelectedIndices()[0];
        final List<Object> selectedValues = this.attributeListView.getSelectedValuesList();
        final SqlFragment[] selectedFragments = new SqlFragment[selectedValues.size()];
        for (int i = 0; i < selectedValues.size(); i++) {
            final Object curValue = selectedValues.get(i);
            final SqlFragment fragment = (SqlFragment) curValue;
            selectedFragments[i] = fragment;
            this.attributeListModel.removeElement(fragment);
        }
        this.attributeListModel.add(pos, new Conjunction(selectedFragments));
        fillAvailableValueList();
    }

    private void createDisjunction() {
        final int pos = this.attributeListView.getSelectedIndices()[0];
        final List<Object> selectedValues = this.attributeListView.getSelectedValuesList();
        final SqlFragment[] selectedFragments = new SqlFragment[selectedValues.size()];
        for (int i = 0; i < selectedValues.size(); i++) {
            final Object curValue = selectedValues.get(i);
            final SqlFragment fragment = (SqlFragment) curValue;
            selectedFragments[i] = fragment;
            this.attributeListModel.removeElement(fragment);
        }
        this.attributeListModel.add(pos, new Disjunction(selectedFragments));
        fillAvailableValueList();
    }

    private void createNegation() {
        final int pos = this.attributeListView.getSelectedIndex();
        final SqlFragment sqlFragment = (SqlFragment) this.attributeListView
                .getSelectedValue();
        this.attributeListModel.removeElement(sqlFragment);
        this.attributeListModel.add(pos, new Negation(sqlFragment));
        fillAvailableValueList();
    }

    private boolean checkIfColumnAllowsNullValues() throws DatabaseException {
        boolean columnAllowsNulls = true;
        final DatabaseMetaData dbMetadata = databaseConnection
                .getDatabaseMetaData();
        try {
            final String tableName = selectedTableColumnPair.getTable()
                    .getDisplayName();
            final String colName = selectedTableColumnPair.getColumn()
                    .getDisplayName();
            final ResultSet rs = dbMetadata.getColumns(null, null, tableName,
                    colName);
            while (rs.next()) {
                final ResultSetMetaData cur = rs.getMetaData();
                String curTableName = "";
                String curColName = "";
                String curIsNullable = "";
                for (int i = 1; i <= cur.getColumnCount(); i++) {
                    if (cur.getColumnName(i).equals("TABLE_NAME")) {
                        curTableName = rs.getString(i);
                    }
                    if (cur.getColumnName(i).equals("COLUMN_NAME")) {
                        curColName = rs.getString(i);
                    }
                    if (cur.getColumnName(i).equals("IS_NULLABLE")) {
                        curIsNullable = rs.getString(i);
                    }
                }
                if ((curTableName.equals(tableName))
                        && (curColName.equals(colName))) {
                    // check now if column allows NULL values. This is tricky
                    // as JDBC doesn't return very good values, here is a
                    // sniplet from
                    // javadoc:
                    // "NO" means column definitely does not allow NULL values;
                    // "YES" means the column might allow NULL values.
                    // An empty string means nobody knows.
                    if (curIsNullable.equals("NO")) {
                        columnAllowsNulls = false;
                    }
                }
            }
        } catch (final SQLException e1) {
            ErrorDialog.showError(this, e1, "Database query failed");
        }
        return columnAllowsNulls;
    }

    private class UpdateButtonForCorrectModelStateListDataListener implements
            ListDataListener {
        private final JButton actionButton;

        public UpdateButtonForCorrectModelStateListDataListener(
                final JButton button) {
            this.actionButton = button;
        }

        private void updateStateOfButtons() {
            actionButton.setEnabled(isScaleCorrect()
                    && !scaleTitleField.getText().equals(""));
        }

        public void contentsChanged(final ListDataEvent e) {
            updateStateOfButtons();
        }

        public void intervalAdded(final ListDataEvent e) {
            updateStateOfButtons();
        }

        public void intervalRemoved(final ListDataEvent e) {
            updateStateOfButtons();
        }
    }

}
