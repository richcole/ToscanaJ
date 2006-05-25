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
	private final static TableCellRenderer CELL_RENDERER = new DefaultTableCellRenderer()
	{
	    {
	        setOpaque(true);
	        setBorder(noFocusBorder);
	    }
	
	    public void updateUI() {
	        super.updateUI();
	        Border cell = UIManager.getBorder("TableHeader.cellBorder");
	        Border focus = UIManager.getBorder("Table.focusCellHighlightBorder");
	        Insets i = focus.getBorderInsets(this);
	
	        noFocusBorder = new BorderUIResource.CompoundBorderUIResource
	             (cell, BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right));
	    }
	
	    public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean focused, int row,
                int column) {
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
		private ManyValuedContext context;

		public RowHeaderModel(ManyValuedContext context) {
			this.context = context;
		}

		public int getColumnCount() {
			return 1;
		}
		
		public String getColumnName(int column) {
			return "";
		}
		
		public int getRowCount() {
			return this.context.getObjects().size() + 1;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
            if(rowIndex == this.context.getObjects().size()) {
                return "<Add new>";
            }
			FCAElement object = (FCAElement) this.context.getObjects().get(rowIndex);
			return object;
		}
	}

	
	public RowHeader(ManyValuedContext context){
		setManyValuedContext(context);
		LookAndFeel.installColorsAndFont(this, "TableHeader.background", 
							"TableHeader.foreground", "TableHeader.font");
		setDefaultRenderer(Object.class, CELL_RENDERER);
        updateSize();
	}
	
	public void setManyValuedContext(ManyValuedContext context) {
		setModel(new RowHeaderModel(context));
	}
	
	public void updateSize() {
		Dimension d = getPreferredScrollableViewportSize();
        d.width = getPreferredSize().width;
        setPreferredScrollableViewportSize(d);
        validate();
	}

	/**
	 * @todo this is a hack since we don't have change notification on the many valued context, we
	 *       do it through this backdoor. Not really well maintainable and not efficient either, but 
	 *       it gets things going...
	 *       
	 * @see TableView#updateModel()
	 */
	public void updateModel() {
		tableChanged(new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW));
	}
}
