/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.attributeselection.SelectedColumnChangedEvent;
import net.sourceforge.toscanaj.gui.dialog.attributeselection.ValueSetSelector;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.view.database.SQLTypeMapper;

import java.awt.*;
import java.sql.Types;
import java.util.Iterator;

public class AttributeSelectionDialog extends JDialog implements EventBrokerListener {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(AttributeSelectionDialog.class);
    
    private static final String ID_BOOLEAN_SELECTOR = "boolean selector";
    private static final String ID_INTERVAL_SELECTOR = "interval selector";
    private static final String ID_NAME_SELECTOR = "name set selector";
    private static final String ID_UNKNOWN_TYPE_LABEL = "unknown type label";
    private static final String ID_NOTHING_SELECTED_LABEL = "no selection";

    private DatabaseConnection connection;

    private DefaultListModel tablesList;
    private DefaultListModel columnsList;

    private JList tableListView;
    private JList columnListView;
    private EventBroker eventBroker;
    private JPanel cardPanel;
    private JSplitPane listSplitPane;
    private JSplitPane mainSplitPane;
    private EventBroker internalBroker;

    class TableInfo {
        Table table;

        public TableInfo(Table table) {
            this.table = table;
        }

        public String toString() {
            return table.getSqlExpression();
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
            return column.getDisplayName() + ": " + SQLTypeMapper.getTypeDescription(column.getType());
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
    
    class ColumnSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (!(event.getSource() instanceof JList)) {
                return;
            }
            JList list = (JList) event.getSource();
            ListSelectionModel model = list.getSelectionModel();
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            if (model.isSelectionEmpty()) {
            	cardLayout.show(cardPanel, ID_NOTHING_SELECTED_LABEL);
                return;
            }
            if (model.getValueIsAdjusting()) {
                return;
            }

            ColumnInfo columnInfo =
                    (ColumnInfo) columnsList.elementAt(
                            model.getMinSelectionIndex()
                    );
            int colType = columnInfo.getColumn().getType();
            if( isTextType(colType) ) {
            	cardLayout.show(cardPanel, ID_NAME_SELECTOR);
            } else if ( isRangeType(colType) ) {
                cardLayout.show(cardPanel, ID_INTERVAL_SELECTOR);
            } else if ( isBooleanType(colType) ) {
                cardLayout.show(cardPanel, ID_BOOLEAN_SELECTOR);
            } else {
            	cardLayout.show(cardPanel, ID_UNKNOWN_TYPE_LABEL);
            }
            internalBroker.processEvent(new SelectedColumnChangedEvent(columnInfo.getColumn()));
        }
        
        private boolean isRangeType(int colType) {
        	return (colType == Types.BIGINT) ||
		            (colType == Types.CHAR) ||
		            (colType == Types.TIME) ||
		            (colType == Types.DECIMAL) ||
		            (colType == Types.DOUBLE) ||
		            (colType == Types.FLOAT) ||
		            (colType == Types.INTEGER) ||
		            (colType == Types.NUMERIC) ||
		            (colType == Types.REAL) ||
		            (colType == Types.SMALLINT) ||
		            (colType == Types.TIME) ||
		            (colType == Types.TIME) ||
		            (colType == Types.TINYINT);
        }
        
        private boolean isTextType(int colType) {
            return (colType == Types.LONGVARCHAR) ||
                    (colType == Types.VARCHAR);
        }

        private boolean isBooleanType(int colType) {
            return (colType == Types.BINARY) ||
                    (colType == Types.BIT);
        }
    }

    public void displayTable(Table table) {
        columnsList.clear();

        if (table != null) {
            Iterator it = table.getColumns().iterator();
            while(it.hasNext()) {
                Column col = (Column) it.next();
                columnsList.addElement(new ColumnInfo(col));
            }
        }
    }

    public AttributeSelectionDialog(Frame aFrame, String title, DatabaseConnection connection, EventBroker eventBroker) {
        super(aFrame, true);
        setTitle(title);
        this.connection = connection;
        this.eventBroker = eventBroker;
        init();
        eventBroker.subscribe(this, DatabaseConnectEvent.class, Object.class);
        int divPos = preferences.getInt("verticalDivider", 100);
        listSplitPane.setDividerLocation(divPos);
        divPos = preferences.getInt("horizontalDivider", 100);
        mainSplitPane.setDividerLocation(divPos);
        preferences.restoreWindowPlacement(this, new Rectangle(100, 100, 300, 200));
    }

    public void init() {
    	this.internalBroker = new EventBroker();
    	
        this.tablesList = new DefaultListModel();
        this.columnsList = new DefaultListModel();

        this.tableListView = new JList(this.tablesList);
        this.columnListView = new JList(this.columnsList);
        
        this.tableListView.addListSelectionListener(new TableSelectionListener());
        this.columnListView.addListSelectionListener(new ColumnSelectionListener());

        LabeledPanel tableView = new LabeledPanel("Tables:", tableListView);
        LabeledPanel columnView = new LabeledPanel("Columns:", columnListView);
        
        listSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tableView, columnView);

        JLabel nothingSelectedLabel = new JLabel("Please select a table and a column");
        JLabel unknownTypeLabel = new JLabel("Sorry, the type of this column\nis not supported");
        ValueSetSelector valueSetSelector = new ValueSetSelector(this.connection, this.internalBroker);
        JLabel intervalSelector = new JLabel("select interval...");
        JLabel booleanSelector = new JLabel("select binary value...");
        
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());
        cardPanel.add(nothingSelectedLabel, ID_NOTHING_SELECTED_LABEL);
        cardPanel.add(unknownTypeLabel, ID_UNKNOWN_TYPE_LABEL);
        cardPanel.add(valueSetSelector, ID_NAME_SELECTOR);
        cardPanel.add(intervalSelector, ID_INTERVAL_SELECTOR);
        cardPanel.add(booleanSelector, ID_BOOLEAN_SELECTOR);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, listSplitPane, cardPanel);
        setContentPane(mainSplitPane);
    }
    
    public void fillTableList() {
    	this.tablesList.clear();
    	
    	if(!this.connection.isConnected()) {
    		return;
    	}
    	
    	Iterator it = this.connection.getTableNames().iterator();
    	while(it.hasNext()) {
    		String tableName = (String) it.next();
    		Table table = new Table(eventBroker, tableName, false);
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
        preferences.putInt("verticalDivider", listSplitPane.getDividerLocation());
        preferences.putInt("horizontalDivider", mainSplitPane.getDividerLocation());
        preferences.storeWindowPlacement(this);
    }
}
