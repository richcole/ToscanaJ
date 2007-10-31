/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseSchemaChangedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.events.TableChangedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

public class DatabaseSchemaView extends JPanel implements EventBrokerListener {

	private DatabaseSchema dbScheme;
	private JComboBox tableComboBox;
	private JComboBox columnComboBox;

	private void displayTable(Table table) {
		this.columnComboBox.removeAllItems();

		if (table != null) {
            for (Iterator<Column> it = table.getColumns().iterator(); it.hasNext(); ) {
                Column column = it.next();
                columnComboBox.addItem(new ColumnInfo(column));
            }
		}
	}

	public DatabaseSchemaView(EventBroker broker) {
		super(new GridBagLayout());

		this.tableComboBox = new JComboBox();
        this.tableComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                displayTable(getTable());
            }
        });
		this.columnComboBox = new JComboBox();
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.weightx = 1;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.insets = new Insets(5,5,5,5);
        
        GridBagConstraints comboBoxConstraints = new GridBagConstraints();
        comboBoxConstraints.gridx = 0;
        comboBoxConstraints.anchor = GridBagConstraints.WEST;
        comboBoxConstraints.weightx = 1;
        comboBoxConstraints.fill = GridBagConstraints.HORIZONTAL;
        comboBoxConstraints.insets = new Insets(5,20,5,5);
        
        GridBagConstraints fillConstraints = new GridBagConstraints();
        fillConstraints.gridx = 0;
        fillConstraints.weighty = 1;

        this.add(new JLabel("Select table for objects:"), labelConstraints);
        this.add(this.tableComboBox, comboBoxConstraints);
        this.add(new JLabel("Select column for objects:"), labelConstraints);
        this.add(this.columnComboBox, comboBoxConstraints);
        this.add(new JPanel(), fillConstraints);

		broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
		broker.subscribe(this, DatabaseSchemaChangedEvent.class, Object.class);
		broker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
		broker.subscribe(this, ConceptualSchemaLoadedEvent.class, Object.class);
		broker.subscribe(this, TableChangedEvent.class, Object.class);
	}

	private class TableInfo {
		Table table;

		public TableInfo(Table table) {
			this.table = table;
		}

		@Override
		public String toString() {
			return table.getDisplayName();
		}

		public Table getTable() {
			return table;
		}
	}

	private class ColumnInfo {
		Column column;

		public ColumnInfo(Column column) {
			this.column = column;
		}

		@Override
		public String toString() {
			return column.getDisplayName()
				+ ": "
				+ SQLTypeMapper.getTypeDescription(column.getType());
		}

		public Column getColumn() {
			return column;
		}
	}

	public void processEvent(Event e) {
		if (e instanceof ConceptualSchemaChangeEvent) {
			ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
			this.dbScheme = csce.getConceptualSchema().getDatabaseSchema();
			updateTableViews();
		}

		if (e instanceof DatabaseConnectedEvent) {
			DatabaseConnectedEvent dbce = (DatabaseConnectedEvent) e;
			DatabaseConnection connection = dbce.getConnection();
			if (connection.isConnected()) {
				this.dbScheme = new DatabaseSchema(new EventBroker());
				this.dbScheme.readFromDBConnection(connection);
			}
			updateTableViews();
		}

		if (e instanceof DatabaseSchemaChangedEvent) {
			DatabaseSchemaChangedEvent event = (DatabaseSchemaChangedEvent) e;
			this.dbScheme = event.getDBScheme();
			updateTableViews();
		}

		if (e instanceof TableChangedEvent) {
			TableChangedEvent event = (TableChangedEvent) e;
			Table table = event.getTable();
			if(this.tableComboBox.getSelectedItem() == table) {
                displayTable(table);
            }
		}
	}

	protected void updateTableViews() {
		if (this.dbScheme == null) {
			return;
		}

        this.tableComboBox.removeAllItems();
        for (Iterator<Table> it = dbScheme.getTables().iterator(); it.hasNext(); ) {
            Table table = it.next();
            TableInfo tableInfo = new TableInfo(table);
            this.tableComboBox.addItem(tableInfo);
            if(this.tableComboBox.getSelectedItem() == null) {
                this.tableComboBox.setSelectedItem(tableInfo);
                displayTable(table);
            }
        }
	}

	public Table getTable() {
        TableInfo tableInfo = (TableInfo) this.tableComboBox.getSelectedItem();
        if(tableInfo == null) {
            return null;
        }
        return tableInfo.getTable();
	}

	public Column getKey() {
        ColumnInfo columnInfo = (ColumnInfo) this.columnComboBox.getSelectedItem();
        if(columnInfo == null) {
            return null;
        }
        return columnInfo.getColumn();
	}

	public void setKey(String tableName, String key) {
        for (int i = 0; i < this.tableComboBox.getItemCount(); i++) {
            Table table = ((TableInfo) this.tableComboBox.getItemAt(i)).getTable();
            if (table.getDisplayName().equals(tableName)) {
                this.tableComboBox.setSelectedIndex(i);
                for (int j = 0; j < this.columnComboBox.getItemCount(); j++) {
                    Column col = ((ColumnInfo) this.columnComboBox.getItemAt(j)).getColumn();
                    if (col.getDisplayName().equals(key)) {
                        this.columnComboBox.setSelectedIndex(j);
                        table.setKey(col);
                    }
                }
            }
        }
	}
}
