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
import net.sourceforge.toscanaj.gui.LabeledPanel;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * @todo support joins or drop the option to select multiple table/column pairs.
 * 		 getSqlTableName() returns only the first at the moment
 * @todo add session management for the layout
 */
public class DatabaseSchemaView extends JPanel implements EventBrokerListener {

	DefaultListModel unkeyedTableList;
	DefaultListModel keyedTableList;
	DefaultListModel columnsList;

	private DatabaseSchema dbScheme;
	private JList unkeyedTableListPanel;
	private JList keyedTableListPanel;
	private JList columnsPanel;
	private JSplitPane splitPane;
	private JSplitPane leftPane;

	private RemoveTableKeyAction removeTableAction;

	class RemoveTableKeyAction extends AbstractAction {

		RemoveTableKeyAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent event) {
			int[] selectedList = keyedTableListPanel.getSelectedIndices();
			for (int i = 0; i < selectedList.length; ++i) {
				KeyTableInfo info =
					(KeyTableInfo) keyedTableList.elementAt(selectedList[i]);
				info.getTable().setKey(null);
			}
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
					TableInfo info =
						(TableInfo) unkeyedTableList.elementAt(
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
					model.getMinSelectionIndex());
			final int selectedIndex = unkeyedTableListPanel.getSelectedIndex();
			if (selectedIndex < 0) {
				return;
			}
			TableInfo tableInfo =
				(TableInfo) unkeyedTableList.elementAt(selectedIndex);
			setObjectKey(tableInfo.getTable(), columnInfo.getColumn());
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
		/// @todo remove workaround for a bug in swing ver. < 1.3rel03
		if (unkeyedTableList.size() == 0) {
			displayTable(null);
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
            for (Iterator it = table.getColumns().iterator(); it.hasNext(); ) {
                Column column = (Column) it.next();
                columnsList.addElement(new ColumnInfo(column));
            }
		}
	}

	public DatabaseSchemaView(EventBroker broker) {
		super(new GridLayout(0, 1));

		this.unkeyedTableList = new DefaultListModel();
		this.keyedTableList = new DefaultListModel();
		this.columnsList = new DefaultListModel();

		unkeyedTableListPanel = new JList(this.unkeyedTableList);
		keyedTableListPanel = new JList(this.keyedTableList);
		columnsPanel = new JList(this.columnsList);

		unkeyedTableListPanel.addListSelectionListener(
			new TableSelectionListener());

		columnsPanel.addListSelectionListener(new ColumnSelectionListener());

		this.removeTableAction = new RemoveTableKeyAction("Remove Key");
		JButton removeButton = new JButton(removeTableAction);

		leftPane =
			new JSplitPane(
				JSplitPane.VERTICAL_SPLIT,
				new LabeledPanel("Available Tables:", unkeyedTableListPanel),
				new LabeledPanel(
					"Selected Keys:",
					keyedTableListPanel,
					removeButton));
		leftPane.setDividerLocation(100);
		leftPane.setOneTouchExpandable(true);
		leftPane.setResizeWeight(0);

		splitPane =
			new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				leftPane,
				new LabeledPanel("Available Object Keys:", columnsPanel));
		splitPane.setDividerLocation(180);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0);
		add(splitPane);

		broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
		broker.subscribe(this, DatabaseSchemaChangedEvent.class, Object.class);
		broker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
		broker.subscribe(this, ConceptualSchemaLoadedEvent.class, Object.class);
		broker.subscribe(this, TableChangedEvent.class, Object.class);
	}

	class TableInfo {
		Table table;

		public TableInfo(Table table) {
			this.table = table;
		}

		public String toString() {
			return table.getDisplayName();
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
			return column.getDisplayName()
				+ ": "
				+ SQLTypeMapper.getTypeDescription(column.getType());
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
				return table.getDisplayName() + ":" + "??? No key what's the story";
			}
			return table.getDisplayName() + ":" + table.getKey().getDisplayName();
		}

		public Column getKey() {
			return table.getKey();
		}

		public Table getTable() {
			return table;
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
			updateViewOfTable(table);
		}
	}

	protected void updateTableViews() {
		clear();

		if (this.dbScheme == null) {
			return;
		}

        for (Iterator it = dbScheme.getTables().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
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

	public void setHorizontalDividerLocation(int location) {
		splitPane.setDividerLocation(location);
	}

	public int getHorizontalDividerLocation() {
		return splitPane.getDividerLocation();
	}

	public void setVerticalDividerLocation(int location) {
		leftPane.setDividerLocation(location);
	}

	public int getVerticalDividerLocation() {
		return leftPane.getDividerLocation();
	}

	public Table getTable() {
		Enumeration enum = this.keyedTableList.elements();
		while (enum.hasMoreElements()) {
			KeyTableInfo element = (KeyTableInfo) enum.nextElement();
			return element.getTable();
		}
		return null;
	}

	public Column getKey() {
		Enumeration enum = this.keyedTableList.elements();
		while (enum.hasMoreElements()) {
			KeyTableInfo element = (KeyTableInfo) enum.nextElement();
			return element.getKey();
		}
		return null;
	}

	public void setKey(String tableName, String key) {
		DefaultListModel list = this.unkeyedTableList;
		for (int i = 0; i < list.size(); i++) {
			Table table = ((TableInfo) list.get(i)).getTable();
			if (table.getDisplayName().equals(tableName)) {
				Iterator colIt = table.getColumns().iterator();
				while (colIt.hasNext()) {
					Column col = (Column) colIt.next();
					if (col.getDisplayName().equals(key)) {
						removeUnkeyedTable(table);
						table.setKey(col);
					}
				}
			}
		}
	}
}
