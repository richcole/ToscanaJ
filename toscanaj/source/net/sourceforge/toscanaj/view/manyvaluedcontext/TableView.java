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
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            if (!(context instanceof WritableManyValuedContext)) {
                return false;
            }
            if (columnIndex == context.getAttributes().size()) {
                return false;
            }
            if (rowIndex == context.getObjects().size()) {
                return false;
            }
            return true;
        }

        @Override
        public Class getColumnClass(final int columnIndex) {
            return Object.class;
        }

        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (columnIndex == context.getAttributes().size()) {
                return null;
            }
            if (rowIndex == context.getObjects().size()) {
                return null;
            }
            final FCAElement object = getObjectForRow(rowIndex);
            final ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
            final Value relationship = context.getRelationship(object,
                    attribute);
            if (relationship == null) {
                return "";
            }
            return relationship.getDisplayString();
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex,
                final int columnIndex) {
            if (columnIndex == context.getAttributes().size()) {
                throw new IllegalArgumentException(
                        "Last column in table is not editable");
            }
            if (rowIndex == context.getObjects().size()) {
                throw new IllegalArgumentException(
                        "Last row in table is not editable");
            }
            final FCAElement object = getObjectForRow(rowIndex);
            final ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
            final WritableManyValuedContext writableContext = (WritableManyValuedContext) context;
            writableContext.setRelationship(object, attribute, (Value) aValue);
        }

        @Override
        public String getColumnName(final int columnIndex) {
            if (columnIndex == context.getAttributes().size()) {
                return "<Add new>";

            }
            final ManyValuedAttribute attribute = getAttributeForColumn(columnIndex);
            return attribute.getName();
        }

        private FCAElement getObjectForRow(final int rowIndex) {
            return context.getObjects().get(rowIndex);
        }

        private ManyValuedAttribute getAttributeForColumn(final int columnIndex) {
            return context.getAttributes().get(columnIndex);
        }
    }

    public TableView(final ManyValuedContext context) {
        this.context = context;
        setModel(new ContextTableModel());
        setAutoResizeMode(AUTO_RESIZE_OFF);
    }

    public void setManyValuedContext(final ManyValuedContext context) {
        this.context = context;
        // code fragment picked from JTable.setModel() -- needed to update view
        tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        final ManyValuedAttribute mvAttr = this.context.getAttributes().get(
                column);
        final Datatype type = mvAttr.getType();
        return DatatypeViewFactory.getValueCellEditor(type);
    }

    /**
     * @todo this is a hack since we don't have change notification on the many
     *       valued context, we do it through this backdoor. Not really well
     *       maintainable and not efficient either, but it gets things going...
     * 
     * @see RowHeader#updateModel()
     */
    public void updateModel() {
        tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
    }
}
