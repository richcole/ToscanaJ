/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;

import org.tockit.datatype.Datatype;
import org.tockit.datatype.Value;
import org.tockit.datatype.swing.DatatypeViewFactory;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;

/**
 * A view for many-valued contexts.
 * 
 * Based on the information and code found on 
 * http://www.chka.de/swing/table/row-headers/JTable.html
 * 
 * It has been refined here and there to allow for the more complex model of the
 * many-valued context. One major change is that the table model is a wrapper of
 * the many-valued context, not a mapping.
 */
public class TableView extends JTable {
	private ManyValuedContext context;

	private class ContextTableModel extends AbstractTableModel {

		public int getColumnCount() {
			return context.getAttributes().size();
		}

		public int getRowCount() {
			return context.getObjects().size();
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (context instanceof WritableManyValuedContext);
		}

		public Class getColumnClass(int columnIndex) {
			return Object.class;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			return context.getRelationship(object, attribute).getDisplayString();
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			WritableManyValuedContext writableContext = (WritableManyValuedContext) context;
			writableContext.setRelationship(object, attribute, (Value) aValue);
		}

		public String getColumnName(int columnIndex) {
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			return attribute.getName();
		}

		private FCAElement getObjectForRow(int rowIndex) {
			return (FCAElement) context.getObjects().get(rowIndex);
		}

		private ManyValuedAttribute getAttributeForColumn(int columnIndex) {
			return (ManyValuedAttribute) context.getAttributes().get(columnIndex);
		}
	}
	
	private class ContextTableColumnModle extends DefaultTableColumnModel {
		
	}
	public TableView(ManyValuedContext context) {
		this.context = context;
		setModel(new ContextTableModel());
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}

	public void setManyValuedContext(ManyValuedContext context) {
		this.context = context;
		// code fragment picked from JTable.setModel() -- needed to update view
		tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
	}
	
	public TableCellEditor getCellEditor(int row, int column) {
		ManyValuedAttribute	mvAttr = (ManyValuedAttribute) this.context.getAttributes().get(column);
		Datatype type = mvAttr.getType();
        return DatatypeViewFactory.getValueCellEditor(type);
	}
}
