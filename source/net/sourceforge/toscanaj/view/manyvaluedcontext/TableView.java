/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.text.NumberFormat;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.NumberFormatter;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;

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
			return context.getRelationship(object, attribute);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			FCAElement object = getObjectForRow(rowIndex);
			ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
			WritableManyValuedContext writableContext = (WritableManyValuedContext) context;
			writableContext.setRelationship(object, attribute, (AttributeValue) aValue);
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
		AttributeType type = mvAttr.getType();
		if(type instanceof TextualType) {
			TextualType textType = (TextualType) type;
			JComboBox comp = new JComboBox(type.getValueRange());
			return new DefaultCellEditor(comp);
		}
		if(type instanceof NumericalType) {
			NumericalType numType = (NumericalType) type;
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMaximumFractionDigits(1);
			final JFormattedTextField comp = new JFormattedTextField(new NumberFormatter(format));
			return new DefaultCellEditor(comp) {
				public Object getCellEditorValue() {
					double value = Double.parseDouble(comp.getText());
					return new NumericalValue(value);
				}
			};
		}
		throw new RuntimeException("Unknown table cell type");
	}
}
