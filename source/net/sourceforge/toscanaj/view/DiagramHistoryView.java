package net.sourceforge.toscanaj.view;

import net.sourceforge.toscanaj.model.DiagramHistory;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 * A view for presenting the list of all diagrams.
 *
 * This will display a list of diagrams from the DiagramHistory where all
 * diagrams which were used (normal history), are used (current view, might be more
 * than one if nesting is used) and will be used (list of diagrams for zooming)
 * are shown.
 *
 * All three list are shown as one long list but with different fonts.
 */
public class DiagramHistoryView extends JList {
    /**
     * The cell renderer renders the diagram titles according to the position
     * of the diagram (past, current, future).
     */
    class DiagramCellRenderer extends JLabel implements ListCellRenderer {
        /**
         * Sets the diagram title on the cell and uses different fonts to
         * display it.
         */
        public Component getListCellRendererComponent(
            JList list,
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // the list and the cell have the focus
        {
            Diagram2D diagram = (Diagram2D)value;
            DiagramHistory history = (DiagramHistory) list.getModel();

            String s = diagram.getTitle();
            setText(s);
            if(isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                 setBackground(list.getBackground());
                 setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            Font font = list.getFont();
            if(history.isInCurrent(diagram)) {
                setFont(font.deriveFont(Font.BOLD));
            }
            else if(history.isInFuture(diagram)) {
                setFont(font.deriveFont(Font.ITALIC));
            }
            else {
                setFont(font);
            }
            return this;
        }
    }

    /**
     * Creates a new view for the given history.
     */
    public DiagramHistoryView(ListModel history) {
        super(history);
        if(!(history instanceof DiagramHistory)) {
            throw new ClassCastException("This view needs a model of type DiagramHistory");
        }
        this.setCellRenderer(new DiagramCellRenderer());
    }
}