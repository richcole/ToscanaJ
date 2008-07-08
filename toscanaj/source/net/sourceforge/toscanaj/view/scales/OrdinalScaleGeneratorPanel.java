/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;

/**
 * @todo for performance reasons we should start with some extra entry like
 *       "<select a column>" instead of just taking the first column. On a large
 *       database querying the average can take a while. Another option would be
 *       querying the min/max/avg in another thread, but this would still put
 *       the load on the RDBMS, while making the code more complex.
 */
public class OrdinalScaleGeneratorPanel extends JPanel {
    boolean result;
    public static final int INTEGER = 0;
    public static final int FLOAT = 1;
    public static final int UNSUPPORTED = -1;

    private JButton addButton;
    private NumberField addField;
    private JList dividersList;
    private JComboBox typeChooser;
    private JComboBox columnChooser;
    private JLabel avgLabel;
    private JLabel minLabel;
    private JLabel maxLabel;

    private DefaultListModel dividersModel;

    private Column column;
    private final DatabaseSchema databaseSchema;
    private final DatabaseConnection connection;

    private static interface ContextGenerator {
        Context createContext(String name, List<Object> dividers, Column column);
    }

    private static abstract class SingleDimensionScaleGenerator implements
            ContextGenerator {
        public Context createContext(final String name,
                final List<Object> dividers, final Column column) {
            final ContextImplementation context = new ContextImplementation();
            context.setName(name);
            for (int i = -1; i < dividers.size(); i++) {
                final String objectData = createSQLClause(column
                        .getSqlExpression(), dividers, i);
                final WritableFCAElement object = new FCAElementImplementation(
                        objectData);
                final String attributeName = createAttributeName(dividers, i);
                context.getObjects().add(object);
                if (attributeName != null) {
                    context.getAttributes().add(
                            new FCAElementImplementation(attributeName));
                }
                final Iterator<Object> it = context.getAttributes().iterator();
                while (it.hasNext()) {
                    final WritableFCAElement attribute = (WritableFCAElement) it
                            .next();
                    context.getRelationImplementation().insert(object,
                            attribute);
                }
            }
            return context;
        }

        public String createAttributeName(final List<Object> dividers,
                final int i) {
            if (i == -1) {
                return null;
            }
            return getForwardSymbol()
                    + String.valueOf(dividers.get(getPosition(i, dividers
                            .size())));
        }

        public String createSQLClause(final String columnName,
                final List<Object> dividers, final int i) {
            if (i == -1) {
                return "("
                        + columnName
                        + getBackwardSymbol()
                        + String.valueOf(dividers.get(getPosition(0, dividers
                                .size()))) + ")";
            }
            String retVal = "("
                    + columnName
                    + getForwardSymbol()
                    + String.valueOf(dividers.get(getPosition(i, dividers
                            .size()))) + ")";
            if (i < dividers.size() - 1) {
                retVal += " AND ("
                        + columnName
                        + getBackwardSymbol()
                        + String.valueOf(dividers.get(getPosition(i + 1,
                                dividers.size()))) + ")";
            }
            return retVal;
        }

        protected abstract int getPosition(int i, int max);

        protected abstract String getForwardSymbol();

        protected abstract String getBackwardSymbol();

    }

    private static class IncreasingExclusiveGenerator extends
            SingleDimensionScaleGenerator {
        @Override
        public String toString() {
            return "increasing, exclude bounds";
        }

        @Override
        protected int getPosition(final int i, final int max) {
            return i;
        }

        @Override
        protected String getForwardSymbol() {
            return ">";
        }

        @Override
        protected String getBackwardSymbol() {
            return "<=";
        }
    }

    private static class IncreasingInclusiveGenerator extends
            SingleDimensionScaleGenerator {
        @Override
        public String toString() {
            return "increasing, include bounds";
        }

        @Override
        protected int getPosition(final int i, final int max) {
            return i;
        }

        @Override
        protected String getForwardSymbol() {
            return ">=";
        }

        @Override
        protected String getBackwardSymbol() {
            return "<";
        }
    }

    private static class DecreasingExclusiveGenerator extends
            SingleDimensionScaleGenerator {
        @Override
        public String toString() {
            return "decreasing, exclude bounds";
        }

        @Override
        protected int getPosition(final int i, final int max) {
            return max - i - 1;
        }

        @Override
        protected String getForwardSymbol() {
            return "<";
        }

        @Override
        protected String getBackwardSymbol() {
            return ">=";
        }
    }

    private static class DecreasingInclusiveGenerator extends
            SingleDimensionScaleGenerator {
        @Override
        public String toString() {
            return "decreasing, include bounds";
        }

        @Override
        protected int getPosition(final int i, final int max) {
            return max - i - 1;
        }

        @Override
        protected String getForwardSymbol() {
            return "<=";
        }

        @Override
        protected String getBackwardSymbol() {
            return ">";
        }
    }

    /**
     * @todo there is another case, which mirrors this but puts the equals on
     *       the other direction ==> implement.
     */
    private static abstract class InterordinalGenerator implements
            ContextGenerator {
        public Context createContext(final String name,
                final List<Object> dividers, final Column column) {
            final ContextImplementation context = new ContextImplementation();
            context.setName(name);
            final int numDiv = dividers.size();
            final WritableFCAElement[] upwardsAttributes = new WritableFCAElement[numDiv];
            final WritableFCAElement[] downwardsAttributes = new WritableFCAElement[numDiv];
            for (int i = 0; i < numDiv; i++) {
                upwardsAttributes[i] = getUpwardsAttribute(dividers, i);
                downwardsAttributes[i] = getDownwardsAttribute(dividers, i);
                context.getAttributes().add(upwardsAttributes[i]);
                context.getAttributes().add(downwardsAttributes[i]);
            }
            final BinaryRelationImplementation relation = context
                    .getRelationImplementation();
            for (int i = -1; i < numDiv; i++) {
                String clause;
                if (i == -1) {
                    clause = column.getSqlExpression() + " "
                            + downwardsAttributes[i + 1];
                } else if (i == numDiv - 1) {
                    clause = column.getSqlExpression() + " "
                            + upwardsAttributes[i];
                } else {
                    clause = column.getSqlExpression() + " "
                            + upwardsAttributes[i] + " AND "
                            + column.getSqlExpression() + " "
                            + downwardsAttributes[i + 1];
                }
                final FCAElementImplementation object = new FCAElementImplementation(
                        clause);
                for (int j = 0; j <= i; j++) {
                    relation.insert(object, upwardsAttributes[j]);
                }
                for (int j = i + 1; j < numDiv; j++) {
                    relation.insert(object, downwardsAttributes[j]);
                }
                context.getObjects().add(object);
            }
            return context;
        }

        protected abstract WritableFCAElement getUpwardsAttribute(
                List<Object> dividers, int i);

        protected abstract WritableFCAElement getDownwardsAttribute(
                List<Object> dividers, int i);
    }

    private static class Type1InterordinalGenerator extends
            InterordinalGenerator {
        @Override
        protected WritableFCAElement getUpwardsAttribute(
                final List<Object> dividers, final int i) {
            return new FCAElementImplementation(">= " + dividers.get(i));
        }

        @Override
        protected WritableFCAElement getDownwardsAttribute(
                final List<Object> dividers, final int i) {
            return new FCAElementImplementation("< " + dividers.get(i));
        }

        @Override
        public String toString() {
            return "both, increasing side includes bounds";
        }
    }

    private static class Type2InterordinalGenerator extends
            InterordinalGenerator {
        @Override
        protected WritableFCAElement getUpwardsAttribute(
                final List<Object> dividers, final int i) {
            return new FCAElementImplementation("> " + dividers.get(i));
        }

        @Override
        protected WritableFCAElement getDownwardsAttribute(
                final List<Object> dividers, final int i) {
            return new FCAElementImplementation("<= " + dividers.get(i));
        }

        @Override
        public String toString() {
            return "both, decreasing side includes bounds";
        }
    }

    public OrdinalScaleGeneratorPanel(final DatabaseSchema databaseSchema,
            final DatabaseConnection connection) {
        setSize(400, 600);
        setLocation(200, 100);
        this.databaseSchema = databaseSchema;
        this.connection = connection;
        layoutPanel();
        fillControls();
    }

    public void addDividerListListener(final ListDataListener listener) {
        this.dividersModel.addListDataListener(listener);
    }

    private void layoutPanel() {
        setLayout(new GridBagLayout());

        add(makeColumnChooserPane(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 2, 2));

        add(makeTypeOptionPane(), new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));

        add(makeDividerPane(), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        2, 2, 2, 0), 2, 2));
    }

    private JPanel makeColumnChooserPane() {
        this.columnChooser = new JComboBox();
        this.columnChooser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cb = (JComboBox) e.getSource();
                if (column != cb.getSelectedItem()) {
                    column = ((TableColumnPair) cb.getSelectedItem())
                            .getColumn();
                    if (determineDataType(column.getType()) == FLOAT) {
                        addField.setNumberType(NumberField.FLOAT);
                    } else {
                        addField.setNumberType(NumberField.INTEGER);
                    }
                    updateRangeInfo();
                }
            }
        });

        return new LabeledPanel("Column:", this.columnChooser, false);
    }

    protected void updateRangeInfo() {
        final String tail = " FROM "
                + this.column.getTable().getSqlExpression() + ";";
        final String minQu = "SELECT min(" + this.column.getSqlExpression()
                + ")" + tail;
        final String maxQu = "SELECT max(" + this.column.getSqlExpression()
                + ")" + tail;
        final String avgQu = "SELECT avg(" + this.column.getSqlExpression()
                + ")" + tail;
        try {
            if (determineDataType(this.column.getType()) == FLOAT) {
                final double min = this.connection.queryDouble(minQu, 1);
                final double max = this.connection.queryDouble(maxQu, 1);
                final double avg = this.connection.queryDouble(avgQu, 1);
                this.minLabel.setText("Min: "
                        + createFormattedNumberString(min));
                this.maxLabel.setText("Max: "
                        + createFormattedNumberString(max));
                this.avgLabel.setText("Average: "
                        + createFormattedNumberString(avg));
            } else {
                final int min = this.connection.queryInt(minQu, 1);
                final int max = this.connection.queryInt(maxQu, 1);
                final double avg = this.connection.queryDouble(avgQu, 1);
                this.minLabel.setText("Min: "
                        + createFormattedNumberString(min));
                this.maxLabel.setText("Max: "
                        + createFormattedNumberString(max));
                this.avgLabel.setText("Average: "
                        + createFormattedNumberString(avg));
            }
        } catch (final DatabaseException e) {
            ErrorDialog.showError(this, e, "Database query failed");
            this.minLabel.setText("");
            this.maxLabel.setText("");
            this.avgLabel.setText("");
        }
    }

    private String createFormattedNumberString(final double numToRound) {
        if (numToRound < 10) {
            return new DecimalFormat(".######").format(numToRound);
        } else if (numToRound < 100) {
            return new DecimalFormat(".####").format(numToRound);
        } else {
            return new DecimalFormat(".##").format(numToRound);
        }
    }

    protected JPanel makeDividerPane() {
        dividersModel = new DefaultListModel();

        dividersList = new JList(dividersModel);
        dividersList
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        final JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(hasSelectedDivider());

        dividersList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(final ListSelectionEvent e) {
                        removeButton.setEnabled(e.getFirstIndex() != -1);
                    }
                });
        dividersModel.addListDataListener(new ListDataListener() {
            private void updateRemoveButton() {
                removeButton.setEnabled(dividersModel.getSize() > 0
                        && hasSelectedDivider());
            }

            public void intervalAdded(final ListDataEvent e) {
                updateRemoveButton();
            }

            public void intervalRemoved(final ListDataEvent e) {
                updateRemoveButton();
            }

            public void contentsChanged(final ListDataEvent e) {
                updateRemoveButton();
            }
        });

        final JPanel removeButtonPane = new JPanel(new GridBagLayout());

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int[] selected = dividersList.getSelectedIndices();
                for (int i = selected.length; --i >= 0;) {
                    removeDivider(selected[i]);
                }
            }
        });

        final JButton removeAllButton = new JButton("Remove All");
        removeAllButton.setEnabled(false);
        dividersModel.addListDataListener(new ListDataListener() {
            private void updateRemoveButton() {
                removeAllButton.setEnabled(dividersModel.size() != 0);
            }

            public void intervalAdded(final ListDataEvent e) {
                updateRemoveButton();
            }

            public void intervalRemoved(final ListDataEvent e) {
                updateRemoveButton();
            }

            public void contentsChanged(final ListDataEvent e) {
                updateRemoveButton();
            }
        });
        removeAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                removeAllDividers();
            }
        });

        removeButtonPane.add(removeButton, new GridBagConstraints(0, 0, 1, 1,
                1.0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 2, 2));
        removeButtonPane.add(removeAllButton, new GridBagConstraints(1, 0, 1,
                1, 1.0, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));

        final JPanel centerPane = new JPanel(new GridBagLayout());

        centerPane.add(makeAddDividerPanel(), new GridBagConstraints(0, 0, 1,
                2, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 0), 2, 2));
        centerPane.add(new JScrollPane(dividersList), new GridBagConstraints(1,
                0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH, new Insets(2, 2, 2, 0), 2, 2));
        centerPane.add(removeButtonPane, new GridBagConstraints(1, 1, 1, 1,
                1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));
        centerPane.setBorder(BorderFactory.createEtchedBorder());

        return new LabeledPanel("Dividers:", centerPane, false);
    }

    private void fillControls() {
        this.columnChooser.removeAllItems();
        final List<Table> tables = this.databaseSchema.getTables();
        for (final Table table : tables) {
            final List<Column> columns = table.getColumns();
            for (final Column currentColumn : columns) {
                if (determineDataType(currentColumn.getType()) != UNSUPPORTED) {
                    this.columnChooser.addItem(new TableColumnPair(table,
                            currentColumn));
                }
            }
        }
    }

    public void removeDivider(final int i) {
        dividersModel.removeElementAt(i);
    }

    private boolean hasSelectedDivider() {
        return dividersList.getSelectedIndex() != -1;
    }

    public void removeAllDividers() {
        dividersModel.removeAllElements();
    }

    private JPanel makeAddDividerPanel() {
        final JPanel addPanel = new JPanel();
        final JLabel enterValueLabel = new JLabel("Enter Value:");
        addPanel.setLayout(new GridBagLayout());
        addField = new NumberField(10, NumberField.FLOAT);
        addButton = new JButton("Add");
        addButton.setEnabled(false);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addDelimiter();
            }
        });

        addField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(final DocumentEvent e) {
                setAddButtonState();
            }

            public void removeUpdate(final DocumentEvent e) {
                setAddButtonState();
            }

            public void changedUpdate(final DocumentEvent e) {
                setAddButtonState();
            }

            private void setAddButtonState() {
                addButton.setEnabled(addField.isValid());
            }
        });

        addField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                addDelimiter();
            }
        });

        this.minLabel = new JLabel();
        this.maxLabel = new JLabel();
        this.avgLabel = new JLabel();

        addPanel.add(enterValueLabel, new GridBagConstraints(0, 0, 2, 1, 0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 2, 0, 2), 2, 2));

        addPanel.add(addField, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 0, 2), 2, 2));
        addPanel.add(addButton, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 2, 0, 2), 2, 2));
        addPanel.add(this.minLabel, new GridBagConstraints(0, 2, 2, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 0, 2), 2, 2));
        addPanel.add(this.maxLabel, new GridBagConstraints(0, 3, 2, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 0, 2), 2, 2));
        addPanel.add(this.avgLabel, new GridBagConstraints(0, 4, 2, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 2, 0, 2), 2, 2));
        return addPanel;
    }

    private void addDelimiter() {
        if (determineDataType(this.column.getType()) == FLOAT) {
            addDelimiter(addField.getDoubleValue());
        } else {
            addDelimiter(addField.getIntegerValue());
        }
    }

    public void addDelimiter(final double value) {
        int i;
        for (i = 0; i < dividersModel.size(); i++) {
            final double currDivider = ((Double) dividersModel.elementAt(i))
                    .doubleValue();
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

    public void addDelimiter(final int value) {
        int i;
        for (i = 0; i < dividersModel.size(); i++) {
            final int currDivider = ((Integer) dividersModel.elementAt(i))
                    .intValue();
            if (value == currDivider) {
                return;
            }
            if (value < currDivider) {
                break;
            }
        }
        dividersModel.insertElementAt(new Integer(value), i);
        addField.setText("");
    }

    public java.util.List<Object> getDividers() {
        return Arrays.asList(dividersModel.toArray());
    }

    private JPanel makeTypeOptionPane() {
        this.typeChooser = new JComboBox(new ContextGenerator[] {
                new IncreasingExclusiveGenerator(),
                new IncreasingInclusiveGenerator(),
                new DecreasingExclusiveGenerator(),
                new DecreasingInclusiveGenerator(),
                new Type1InterordinalGenerator(),
                new Type2InterordinalGenerator() });
        return new LabeledPanel("Type:", this.typeChooser, false);
    }

    public static int determineDataType(final int columnType) {
        switch (columnType) {
            case Types.DOUBLE:
                return OrdinalScaleGeneratorPanel.FLOAT;
            case Types.FLOAT:
                return OrdinalScaleGeneratorPanel.FLOAT;
            case Types.REAL:
                return OrdinalScaleGeneratorPanel.FLOAT;
            case Types.BIGINT:
                return OrdinalScaleGeneratorPanel.INTEGER;
            case Types.INTEGER:
                return OrdinalScaleGeneratorPanel.INTEGER;
            case Types.SMALLINT:
                return OrdinalScaleGeneratorPanel.INTEGER;
            case Types.TINYINT:
                return OrdinalScaleGeneratorPanel.INTEGER;
            default:
                return OrdinalScaleGeneratorPanel.UNSUPPORTED;
        }
    }

    public Context createContext(final String title) {
        final ContextGenerator generator = (ContextGenerator) this.typeChooser
                .getSelectedItem();
        return generator.createContext(title, getDividers(), getColumn());
    }

    protected JList getDividersList() {
        return this.dividersList;
    }

    protected Column getColumn() {
        return this.column;
    }

    protected DefaultListModel getDividersModel() {
        return this.dividersModel;
    }

    protected JComboBox getTypeChooser() {
        return this.typeChooser;
    }
}
