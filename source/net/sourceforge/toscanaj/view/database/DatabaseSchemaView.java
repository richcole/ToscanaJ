/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 28, 2002
 * Time: 4:14:58 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.gui.PanelStackView;
import net.sourceforge.toscanaj.model.events.DatabaseSchemaChangedEvent;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.DatabaseSchema;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.util.STD_Iterator;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class DatabaseSchemaView extends JPanel implements BrokerEventListener {

    DefaultListModel availableTableList;
    DefaultListModel selectedTableList;
    DefaultListModel columnsList;

    private DatabaseSchema dbScheme;
    private JList availableTableListPanel;
    private JList selectedTableListPanel;
    private JList columnsPanel;

    class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            ListSelectionModel model;
            if (event.getSource() instanceof JList) {
                JList list = (JList) event.getSource();
                model = list.getSelectionModel();
            } else {
                return;
            }
            if (model.isSelectionEmpty()) {
                displayTable(null);
            } else {
                TableInfo info = (TableInfo) availableTableList.elementAt(
                        model.getMinSelectionIndex());
                displayTable(info.getTable());
            }
        }
    }

    class ColumnSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            ListSelectionModel model;
            if (event.getSource() instanceof JList) {
                JList list = (JList) event.getSource();
                model = list.getSelectionModel();
            } else {
                return;
            }
            if (model.isSelectionEmpty()) {
                TableInfo tableInfo =
                    (TableInfo) availableTableList.elementAt(
                        availableTableListPanel.getSelectedIndex()
                    );
                setObjectKey(tableInfo.getTable(), null);
            }
            else {
                ColumnInfo columnInfo =
                    (ColumnInfo) columnsList.elementAt(
                        model.getMinSelectionIndex()
                    );
                TableInfo tableInfo =
                    (TableInfo) availableTableList.elementAt(
                        availableTableListPanel.getSelectedIndex()
                    );
                setObjectKey(tableInfo.getTable(), columnInfo.getColumn());
            }
        }
    }

    public void setObjectKey(Table table, Column column) {
        table.setKey(column);
        if ( column == null ) {
            selectedTableList.removeElement(table);
        }
        else {
            if ( ! selectedTableList.contains(new TableInfo(table)) ) {
                selectedTableList.addElement(new TableInfo(table));
            }
        }
    }

    public void displayTable(Table table) {
        columnsList.clear();
        STD_Iterator it = new STD_Iterator(table.getColumns());
        for (it.reset(); !it.atEnd(); it.next()) {
            columnsList.add(0, new ColumnInfo((Column) it.val()));
        }
    };

    public DatabaseSchemaView(JFrame frame, EventBroker broker) {
        super(new GridLayout(0, 1));
        JPanel leftPane = new JPanel(new GridBagLayout());
        JPanel rightPane = new JPanel(new GridBagLayout());

        JScrollPane leftTopPane = new JScrollPane();
        JScrollPane leftBottomPane = new JScrollPane();
        JScrollPane rightTopPane = new JScrollPane();

        this.availableTableList = new DefaultListModel();
        this.selectedTableList = new DefaultListModel();
        this.columnsList = new DefaultListModel();

        availableTableListPanel = new JList(this.availableTableList);
        selectedTableListPanel = new JList(this.selectedTableList);
        columnsPanel = new JList(this.columnsList);

        availableTableListPanel.addListSelectionListener(
                new TableSelectionListener()
        );

        columnsPanel.addListSelectionListener(
                new ColumnSelectionListener()
        );

        leftTopPane.getViewport().add(availableTableListPanel, null);
        leftBottomPane.getViewport().add(selectedTableListPanel, null);
        rightTopPane.getViewport().add(columnsPanel, null);

        leftPane.add(new JLabel("Available Tables:"),
                new GridBagConstraints(
                        0, 0, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        leftPane.add(leftTopPane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        leftPane.add(new JLabel("Selected Tables:"),
                new GridBagConstraints(
                        0, 2, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        leftPane.add(leftBottomPane,
                new GridBagConstraints(
                        0, 3, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );

        rightPane.add(new JLabel("Select Object Column:"),
                new GridBagConstraints(
                        0, 0, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        rightPane.add(rightTopPane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        rightPane.add(new JButton("Clear Object Key"),
                new GridBagConstraints(
                        0, 2, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );

        JSplitPane splitPane;
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        broker.subscribe(this, DatabaseSchemaChangedEvent.class, Object.class);
    }

    class TableInfo {
        Table table;

        public TableInfo(Table table) {
            this.table = table;
        }

        public String toString() {
            return table.getName();
        }

        public Table getTable() {
            return table;
        }
    }

    class ColumnInfo {
        Column column;

        public ColumnInfo(Column column) {
            this.column = column;
        }

        public String toString() {
            return column.getName();
        }

        public Column getColumn() {
            return column;
        }
    }

    public void processEvent(Event e) {

        if (e instanceof DatabaseSchemaChangedEvent) {
            DatabaseSchemaChangedEvent event = (DatabaseSchemaChangedEvent) e;
            this.dbScheme = event.getDBScheme();

            clear();

            STD_Iterator it = new STD_Iterator(dbScheme.getTables());
            for (it.reset(); !it.atEnd(); it.next()) {
                Table table = (Table) it.val();
                availableTableList.add(0, new TableInfo(table));
            }
        }
    }

    private void clear() {
        this.availableTableList.clear();
        this.availableTableList.clear();
    }
}
