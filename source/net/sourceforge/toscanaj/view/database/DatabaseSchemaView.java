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
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.events.DatabaseSchemaChangedEvent;
import net.sourceforge.toscanaj.model.events.TableChangedEvent;
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
import java.awt.event.ActionEvent;

public class DatabaseSchemaView extends JPanel implements BrokerEventListener {

    DefaultListModel unkeyedTableList;
    DefaultListModel keyedTableList;
    DefaultListModel columnsList;

    private DatabaseSchema dbScheme;
    private JList unkeyedTableListPanel;
    private JList keyedTableListPanel;
    private JList columnsPanel;

    private RemoveTableKeyAction removeTableAction;

    class RemoveTableKeyAction extends AbstractAction {

        RemoveTableKeyAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent event) {
            int[] selectedList = keyedTableListPanel.getSelectedIndices();
            for (int i = 0; i < selectedList.length; ++i) {
                KeyTableInfo info = (KeyTableInfo)
                        keyedTableList.elementAt(selectedList[i]);
                info.getTable().setKey(null);
            }
        }


    }

    class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            ListSelectionModel model;
            if (event.getSource() instanceof JList) {
                JList list = (JList) event.getSource();
                model = list.getSelectionModel();

                if (model.isSelectionEmpty()) {
                    displayTable(null);
                } else {
                    TableInfo info = (TableInfo) unkeyedTableList.elementAt(
                            model.getMinSelectionIndex());
                    displayTable(info.getTable());
                }
            }
        }
    }

    class UnkeyedTableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            ListSelectionModel model;
            if (event.getSource() instanceof JList) {
                JList list = (JList) event.getSource();
                model = list.getSelectionModel();

                removeTableAction.setEnabled(model.isSelectionEmpty());
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
                // do nothing, there is nothing to do
            } else {
                ColumnInfo columnInfo =
                        (ColumnInfo) columnsList.elementAt(
                                model.getMinSelectionIndex()
                        );
                TableInfo tableInfo =
                        (TableInfo) unkeyedTableList.elementAt(
                                unkeyedTableListPanel.getSelectedIndex()
                        );
                setObjectKey(tableInfo.getTable(), columnInfo.getColumn());
            }
        }
    }

    public void setObjectKey(Table table, Column key) {
        table.setKey(key);
    }

    private void addKeyedTable(Table table) {
        keyedTableList.addElement(new KeyTableInfo(table));
    }

    private void addUnkeyedTable(Table table) {
        unkeyedTableList.addElement(new TableInfo(table));
    }

    private void removeUnkeyedTable(Table table) {
        for (int i = 0; i < unkeyedTableList.size(); ++i) {
            TableInfo info = (TableInfo) unkeyedTableList.elementAt(i);
            if (info.getTable() == table) {
                unkeyedTableList.removeElementAt(i);
                break;
            }
        }
    }

    private void removeKeyedTable(Table table) {
        for (int i = 0; i < keyedTableList.size(); ++i) {
            KeyTableInfo info = (KeyTableInfo) keyedTableList.elementAt(i);
            if (info.getTable() == table) {
                keyedTableList.removeElementAt(i);
                break;
            }
        }
    }

    public void displayTable(Table table) {
        columnsList.clear();

        if (table != null) {
            STD_Iterator it = new STD_Iterator(table.getColumns());
            for (it.reset(); !it.atEnd(); it.next()) {
                columnsList.add(0, new ColumnInfo((Column) it.val()));
            }
        }
    };

    public DatabaseSchemaView(JFrame frame, EventBroker broker) {
        super(new GridLayout(0, 1));

        this.unkeyedTableList = new DefaultListModel();
        this.keyedTableList = new DefaultListModel();
        this.columnsList = new DefaultListModel();

        unkeyedTableListPanel = new JList(this.unkeyedTableList);
        keyedTableListPanel = new JList(this.keyedTableList);
        columnsPanel = new JList(this.columnsList);

        unkeyedTableListPanel.addListSelectionListener(
                new TableSelectionListener()
        );

        columnsPanel.addListSelectionListener(
                new ColumnSelectionListener()
        );

        this.removeTableAction = new RemoveTableKeyAction("Remove Key");
        JButton removeButton = new JButton(removeTableAction);

        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                             new LabeledScrollPaneView("Available Tables:",unkeyedTableListPanel),
                                             new LabeledScrollPaneView("Selected Keys:", keyedTableListPanel, removeButton));
        leftPane.setOneTouchExpandable(true);
        leftPane.setResizeWeight(0);

        JSplitPane splitPane;
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   leftPane,
                                   new LabeledScrollPaneView("Available Object Keys:", columnsPanel));
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        broker.subscribe(this, DatabaseSchemaChangedEvent.class, Object.class);
        broker.subscribe(this, TableChangedEvent.class, Object.class);
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

    class KeyTableInfo {
        Table table;

        public KeyTableInfo(Table table) {
            this.table = table;
        }

        public String toString() {
            if (table.getKey() == null) {
                return table.getName() + ":" + "??? No key what's the story";
            }
            return table.getName() + ":" + table.getKey().getName();
        }

        public Column getKey() {
            return table.getKey();
        }

        public Table getTable() {
            return table;
        }
    }

    public void processEvent(Event e) {

        if (e instanceof DatabaseSchemaChangedEvent) {
            DatabaseSchemaChangedEvent event = (DatabaseSchemaChangedEvent) e;
            this.dbScheme = event.getDBScheme();

            clear();

            STD_Iterator it = new STD_Iterator(dbScheme.getTables());
            for (it.reset(); !it.atEnd(); it.next()) {
                updateViewOfTable((Table) it.val());
            }
        }

        if (e instanceof TableChangedEvent) {
            TableChangedEvent event = (TableChangedEvent) e;
            Table table = event.getTable();
            updateViewOfTable(table);
        }
    }

    private void updateViewOfTable(Table table) {
        if (table.getKey() == null) {
            removeKeyedTable(table);
            addUnkeyedTable(table);
        } else {
            removeUnkeyedTable(table);
            addKeyedTable(table);
        }
    }

    private void clear() {
        this.keyedTableList.clear();
        this.unkeyedTableList.clear();
        this.columnsList.clear();
    }
}
