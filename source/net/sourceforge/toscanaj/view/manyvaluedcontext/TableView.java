/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
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
	private class ContextTableModel extends AbstractTableModel {
		private ManyValuedContext context;

		public ContextTableModel(ManyValuedContext context) {
			this.context = context;
		}

		public int getColumnCount() {
			return this.context.getAttributes().size();
		}

		public int getRowCount() {
			return this.context.getObjects().size();
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (this.context instanceof WritableManyValuedContext);
		}

		public Class getColumnClass(int columnIndex) {
			return Object.class;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			return this.context.getRelationship(object, attribute);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			WritableManyValuedContext writableContext = (WritableManyValuedContext) this.context;
			writableContext.setRelationship(object, attribute, (AttributeValue) aValue);
		}

		public String getColumnName(int columnIndex) {
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			return attribute.getName();
		}

		private FCAElement getObjectForRow(int rowIndex) {
			return (FCAElement) this.context.getObjects().get(rowIndex);
		}

		private ManyValuedAttribute getAttributeForColumn(int columnIndex) {
			return (ManyValuedAttribute) this.context.getAttributes().get(columnIndex);
		}
	}
	
	public TableView(ManyValuedContext context) {
		setManyValuedContext(context);
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}

	public void setManyValuedContext(ManyValuedContext context) {
		setModel(new ContextTableModel(context));
	}
}
