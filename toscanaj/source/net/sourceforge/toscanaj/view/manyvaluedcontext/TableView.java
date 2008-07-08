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
import javax.swing.table.TableCellEditor;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;

import org.tockit.datatype.Datatype;
import org.tockit.datatype.Value;
import org.tockit.datatype.swing.DatatypeViewFactory;

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
			return context.getAttributes().size() + 1;
		}

		public int getRowCount() {
			return context.getObjects().size() + 1;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
            if(!(context instanceof WritableManyValuedContext)) {
                return false;
            }
            if(columnIndex == context.getAttributes().size()) {
                return false;
            }
            if(rowIndex == context.getObjects().size()) {
                return false;
            }
			return true;
		}

		@Override
		public Class getColumnClass(int columnIndex) {
			return Object.class;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
            if(columnIndex == context.getAttributes().size()) {
                return null;
            }
            if(rowIndex == context.getObjects().size()) {
                return null;
            }
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			Value relationship = context.getRelationship(object, attribute);
			if(relationship == null) {
				return ">not set<";
			}
			return relationship.getDisplayString();
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if(columnIndex == context.getAttributes().size()) {
                throw new IllegalArgumentException("Last column in table is not editable");
            }
            if(rowIndex == context.getObjects().size()) {
                throw new IllegalArgumentException("Last row in table is not editable");
            }
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			WritableManyValuedContext writableContext = (WritableManyValuedContext) context;
			writableContext.setRelationship(object, attribute, (Value) aValue);
		}

		@Override
		public String getColumnName(int columnIndex) {
            if(columnIndex == context.getAttributes().size()) {
                return "<Add new>";
                  
            }
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			return attribute.getName();
		}

		private FCAElement getObjectForRow(int rowIndex) {
			return context.getObjects().get(rowIndex);
		}

		private ManyValuedAttribute getAttributeForColumn(int columnIndex) {
			return context.getAttributes().get(columnIndex);
		}
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
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		ManyValuedAttribute	mvAttr = this.context.getAttributes().get(column);
		Datatype type = mvAttr.getType();
        return DatatypeViewFactory.getValueCellEditor(type);
	}

	/**
	 * @todo this is a hack since we don't have change notification on the many valued context, we
	 *       do it through this backdoor. Not really well maintainable and not efficient either, but 
	 *       it gets things going...
	 *       
	 * @see RowHeader#updateModel()
	 */
	public void updateModel() {
		tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
	}
}
