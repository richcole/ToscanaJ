/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: XMLEditorDialog.java,v 1.2 2002/11/27 05:59:17 peterbecker Exp $
 */
package net.sourceforge.toscanaj.gui.dialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.view.database.SQLTypeMapper;

import java.awt.*;
import java.util.Iterator;

public class ColumnChooserDialog extends JDialog implements EventBrokerListener {
	private DatabaseConnection connection;

    private DefaultListModel tablesList;
    private DefaultListModel columnsList;

    private JList tableListView;
    private JList columnListView;
    private EventBroker eventBroker;

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
            return column.getName() + ": " + SQLTypeMapper.getTypeName(column.getType());
        }

        public Column getColumn() {
            return column;
        }
    }

    class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getSource() instanceof JList) {
                JList list = (JList) event.getSource();
                ListSelectionModel model = list.getSelectionModel();
                if (model.getValueIsAdjusting()) {
                    return;
                }
                if (model.isSelectionEmpty()) {
                    displayTable(null);
                } else {
                    TableInfo info = (TableInfo) tablesList.elementAt(
                            model.getMinSelectionIndex());
                    displayTable(info.getTable());
                }
            }
        }
    }

    public void displayTable(Table table) {
        columnsList.clear();

        if (table != null) {
            Iterator it = table.getColumns().iterator();
            while(it.hasNext()) {
            	Column col = (Column) it.next();
                columnsList.add(0, new ColumnInfo(col));
            }
        }
    };

    class ColumnSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (!(event.getSource() instanceof JList)) {
                return;
            }
            JList list = (JList) event.getSource();
            ListSelectionModel model = list.getSelectionModel();
            if (model.isSelectionEmpty()) {
                return;
            }
            if (model.getValueIsAdjusting()) {
                return;
            }

            ColumnInfo columnInfo =
                    (ColumnInfo) columnsList.elementAt(
                            model.getMinSelectionIndex()
                    );
            final int selectedIndex = tableListView.getSelectedIndex();
            if (selectedIndex < 0) {
                return;
            }
            TableInfo tableInfo =
                    (TableInfo) tablesList.elementAt(
                            selectedIndex
                    );
//            setObjectKey(tableInfo.getTable(), columnInfo.getColumn());
        }
    }

    public ColumnChooserDialog(Frame aFrame, String title, DatabaseConnection connection, EventBroker eventBroker) {
        super(aFrame, true);
        setTitle(title);
        this.connection = connection;
        this.eventBroker = eventBroker;
        init();
        eventBroker.subscribe(this, DatabaseConnectEvent.class, Object.class);
        ConfigurationManager.restorePlacement("ColumnChooserDialog", this,
                                              new Rectangle(100, 100, 300, 200));
    }

    public void init() {
        this.tablesList = new DefaultListModel();
        this.columnsList = new DefaultListModel();

        this.tableListView = new JList(this.tablesList);
        this.columnListView = new JList(this.columnsList);
        
        this.tableListView.addListSelectionListener(new TableSelectionListener());
        this.columnListView.addListSelectionListener(new ColumnSelectionListener());

        LabeledScrollPaneView tableView = new LabeledScrollPaneView("Tables:", tableListView);
        LabeledScrollPaneView columnView = new LabeledScrollPaneView("Columns:", columnListView);
        
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
    	
        contentPane.add(tableView, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH,
                                                    new Insets(5,5,5,5), 0, 0));
        contentPane.add(columnView, new GridBagConstraints(0, 1, 1, 1, 1, 5, GridBagConstraints.EAST, GridBagConstraints.BOTH,
                                                    new Insets(5,5,5,5), 0, 0));
    	
        pack();
    }
    
    private void fillTableList() {
    	this.tablesList.clear();
    	
    	Iterator it = this.connection.getTableNames().iterator();
    	while(it.hasNext()) {
    		String tableName = (String) it.next();
    		Table table = new Table(eventBroker, tableName);
    		Iterator itCol = this.connection.getColumns(table).iterator();
    		while(itCol.hasNext()) {
    			Column col = (Column) itCol.next();
    			table.addColumn(col);
    		}
            this.tablesList.addElement(new TableInfo(table));
    	}
    }
    
    public void processEvent(Event event) {
    	fillTableList();
    }

    public void hide() {
        super.hide();
        ConfigurationManager.storePlacement("ColumnChooserDialog", this);
    }
}
