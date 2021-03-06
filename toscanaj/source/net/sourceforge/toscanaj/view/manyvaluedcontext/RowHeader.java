/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;

/**
 * A row header for the many-valued context view.
 * 
 * Based on http://www.chka.de/swing/table/row-headers/JTable.html
 */
public class RowHeader extends JTable {
    private final static TableCellRenderer CELL_RENDERER = new DefaultTableCellRenderer() {
        {
            setOpaque(true);
            setBorder(noFocusBorder);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            final Border cell = UIManager.getBorder("TableHeader.cellBorder");
            final Border focus = UIManager
                    .getBorder("Table.focusCellHighlightBorder");
            final Insets i = focus.getBorderInsets(this);

            noFocusBorder = new BorderUIResource.CompoundBorderUIResource(cell,
                    BorderFactory.createEmptyBorder(i.top, i.left, i.bottom,
                            i.right));
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value, final boolean selected,
                final boolean focused, final int row, final int column) {
            if (table != null) {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                setFont(table.getFont());
                setEnabled(table.isEnabled());
            } else {
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setFont(UIManager.getFont("TableHeader.font"));
                setEnabled(true);
            }

            setBorder(noFocusBorder);
            setValue(value);

            return this;
        }
    };

    private class RowHeaderModel extends AbstractTableModel {
        private final ManyValuedContext context;

        public RowHeaderModel(final ManyValuedContext context) {
            this.context = context;
        }

        public int getColumnCount() {
            return 1;
        }

        @Override
        public String getColumnName(final int column) {
            return "";
        }

        public int getRowCount() {
            return this.context.getObjects().size() + 1;
        }

        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (rowIndex == this.context.getObjects().size()) {
                return "<Add new>";
            }
            return this.context.getObjects().get(rowIndex);
        }
    }

    public RowHeader(final ManyValuedContext context) {
        setManyValuedContext(context);
        LookAndFeel.installColorsAndFont(this, "TableHeader.background",
                "TableHeader.foreground", "TableHeader.font");
        setDefaultRenderer(Object.class, CELL_RENDERER);
        updateSize();
    }

    public void setManyValuedContext(final ManyValuedContext context) {
        setModel(new RowHeaderModel(context));
    }

    public void updateSize() {
        final Dimension d = getPreferredScrollableViewportSize();
        d.width = getPreferredSize().width;
        setPreferredScrollableViewportSize(d);
        validate();
    }

    /**
     * @todo this is a hack since we don't have change notification on the many
     *       valued context, we do it through this backdoor. Not really well
     *       maintainable and not efficient either, but it gets things going...
     * 
     * @see TableView#updateModel()
     */
    public void updateModel() {
        tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
    }
}
