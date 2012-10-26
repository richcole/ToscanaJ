/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.sql.Types;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.attributeselection.SelectedColumnChangedEvent;
import net.sourceforge.toscanaj.gui.dialog.attributeselection.ValueSetSelector;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.view.database.SQLTypeMapper;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * @todo this class seems unused
 */
public class AttributeSelectionDialog extends JDialog implements
        EventBrokerListener {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(AttributeSelectionDialog.class);

    private static final String ID_BOOLEAN_SELECTOR = "boolean selector";
    private static final String ID_INTERVAL_SELECTOR = "interval selector";
    private static final String ID_NAME_SELECTOR = "name set selector";
    private static final String ID_UNKNOWN_TYPE_LABEL = "unknown type label";
    private static final String ID_NOTHING_SELECTED_LABEL = "no selection";

    private final DatabaseConnection connection;

    private DefaultListModel tablesList;
    private DefaultListModel columnsList;

    private JList tableListView;
    private JList columnListView;
    private final EventBroker eventBroker;
    private JPanel cardPanel;
    private JSplitPane listSplitPane;
    private JSplitPane mainSplitPane;
    private EventBroker internalBroker;

    class TableInfo {
        final Table table;

        public TableInfo(final Table table) {
            this.table = table;
        }

        @Override
        public String toString() {
            return this.table.getSqlExpression();
        }

        public Table getTable() {
            return this.table;
        }
    }

    class ColumnInfo {
        final Column column;

        public ColumnInfo(final Column column) {
            this.column = column;
        }

        @Override
        public String toString() {
            return this.column.getDisplayName() + ": "
                    + SQLTypeMapper.getTypeDescription(this.column.getType());
        }

        public Column getColumn() {
            return this.column;
        }
    }

    class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(final ListSelectionEvent event) {
            if (event.getSource() instanceof JList) {
                final JList list = (JList) event.getSource();
                final ListSelectionModel model = list.getSelectionModel();
                if (model.getValueIsAdjusting()) {
                    return;
                }
                if (model.isSelectionEmpty()) {
                    displayTable(null);
                } else {
                    final TableInfo info = (TableInfo) AttributeSelectionDialog.this.tablesList
                            .elementAt(model.getMinSelectionIndex());
                    displayTable(info.getTable());
                }
            }
        }
    }

    class ColumnSelectionListener implements ListSelectionListener {
        public void valueChanged(final ListSelectionEvent event) {
            if (!(event.getSource() instanceof JList)) {
                return;
            }
            final JList list = (JList) event.getSource();
            final ListSelectionModel model = list.getSelectionModel();
            final CardLayout cardLayout = (CardLayout) AttributeSelectionDialog.this.cardPanel
                    .getLayout();
            if (model.isSelectionEmpty()) {
                cardLayout.show(AttributeSelectionDialog.this.cardPanel,
                        ID_NOTHING_SELECTED_LABEL);
                return;
            }
            if (model.getValueIsAdjusting()) {
                return;
            }

            final ColumnInfo columnInfo = (ColumnInfo) AttributeSelectionDialog.this.columnsList
                    .elementAt(model.getMinSelectionIndex());
            final int colType = columnInfo.getColumn().getType();
            if (isTextType(colType)) {
                cardLayout.show(AttributeSelectionDialog.this.cardPanel,
                        ID_NAME_SELECTOR);
            } else if (isRangeType(colType)) {
                cardLayout.show(AttributeSelectionDialog.this.cardPanel,
                        ID_INTERVAL_SELECTOR);
            } else if (isBooleanType(colType)) {
                cardLayout.show(AttributeSelectionDialog.this.cardPanel,
                        ID_BOOLEAN_SELECTOR);
            } else {
                cardLayout.show(AttributeSelectionDialog.this.cardPanel,
                        ID_UNKNOWN_TYPE_LABEL);
            }
            AttributeSelectionDialog.this.internalBroker
                    .processEvent(new SelectedColumnChangedEvent(columnInfo
                            .getColumn()));
        }

        private boolean isRangeType(final int colType) {
            return (colType == Types.BIGINT) || (colType == Types.CHAR)
                    || (colType == Types.TIME) || (colType == Types.DECIMAL)
                    || (colType == Types.DOUBLE) || (colType == Types.FLOAT)
                    || (colType == Types.INTEGER) || (colType == Types.NUMERIC)
                    || (colType == Types.REAL) || (colType == Types.SMALLINT)
                    || (colType == Types.TIME) || (colType == Types.TIME)
                    || (colType == Types.TINYINT);
        }

        private boolean isTextType(final int colType) {
            return (colType == Types.LONGVARCHAR) || (colType == Types.VARCHAR);
        }

        private boolean isBooleanType(final int colType) {
            return (colType == Types.BINARY) || (colType == Types.BIT);
        }
    }

    public void displayTable(final Table table) {
        this.columnsList.clear();

        if (table != null) {
            for (Column col : table.getColumns()) {
                this.columnsList.addElement(new ColumnInfo(col));
            }
        }
    }

    public AttributeSelectionDialog(final Frame aFrame, final String title,
            final DatabaseConnection connection, final EventBroker eventBroker) {
        super(aFrame, true);
        setTitle(title);
        this.connection = connection;
        this.eventBroker = eventBroker;
        init();
        eventBroker.subscribe(this, DatabaseConnectEvent.class, Object.class);
        int divPos = preferences.getInt("verticalDivider", 100);
        this.listSplitPane.setDividerLocation(divPos);
        divPos = preferences.getInt("horizontalDivider", 100);
        this.mainSplitPane.setDividerLocation(divPos);
        preferences.restoreWindowPlacement(this, new Rectangle(100, 100, 300,
                200));
    }

    public void init() {
        this.internalBroker = new EventBroker();

        this.tablesList = new DefaultListModel();
        this.columnsList = new DefaultListModel();

        this.tableListView = new JList(this.tablesList);
        this.columnListView = new JList(this.columnsList);

        this.tableListView
                .addListSelectionListener(new TableSelectionListener());
        this.columnListView
                .addListSelectionListener(new ColumnSelectionListener());

        final LabeledPanel tableView = new LabeledPanel("Tables:",
                this.tableListView);
        final LabeledPanel columnView = new LabeledPanel("Columns:",
                this.columnListView);

        this.listSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                tableView, columnView);

        final JLabel nothingSelectedLabel = new JLabel(
                "Please select a table and a column");
        final JLabel unknownTypeLabel = new JLabel(
                "Sorry, the type of this column\nis not supported");
        final ValueSetSelector valueSetSelector = new ValueSetSelector(
                this.connection, this.internalBroker);
        final JLabel intervalSelector = new JLabel("select interval...");
        final JLabel booleanSelector = new JLabel("select binary value...");

        this.cardPanel = new JPanel();
        this.cardPanel.setLayout(new CardLayout());
        this.cardPanel.add(nothingSelectedLabel, ID_NOTHING_SELECTED_LABEL);
        this.cardPanel.add(unknownTypeLabel, ID_UNKNOWN_TYPE_LABEL);
        this.cardPanel.add(valueSetSelector, ID_NAME_SELECTOR);
        this.cardPanel.add(intervalSelector, ID_INTERVAL_SELECTOR);
        this.cardPanel.add(booleanSelector, ID_BOOLEAN_SELECTOR);

        this.mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                this.listSplitPane, this.cardPanel);
        setContentPane(this.mainSplitPane);
    }

    public void fillTableList() {
        this.tablesList.clear();

        if (!this.connection.isConnected()) {
            return;
        }

        for (String tableName : this.connection.getTableNames()) {
            final Table table = new Table(this.eventBroker, tableName, false);
            for (Column col : this.connection.getColumns(table)) {
                table.addColumn(col);
            }
            this.tablesList.addElement(new TableInfo(table));
        }
    }

    public void processEvent(final Event event) {
        fillTableList();
    }

    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            preferences.putInt("verticalDivider", this.listSplitPane
                    .getDividerLocation());
            preferences.putInt("horizontalDivider", this.mainSplitPane
                    .getDividerLocation());
            preferences.storeWindowPlacement(this);
        }
    }
}
