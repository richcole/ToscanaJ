package net.sourceforge.toscanaj.view;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;

import javax.swing.*;
import java.awt.*;

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
                Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus)    // the list and the cell have the focus
        {
            DiagramHistory history = (DiagramHistory) list.getModel();

            setText(value.toString());
            setOpaque(true);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (list.hasFocus()) {
                    setBorder(new javax.swing.border.LineBorder(java.awt.Color.yellow));
                } else {
                    setBorder(null);
                }
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                setBorder(null);
            }
            setEnabled(list.isEnabled());
            Font font = list.getFont();

            DiagramHistory.DiagramReference diagram = (DiagramHistory.DiagramReference) value;
            if (history.isInCurrent(diagram)) {
                setFont(font.deriveFont(Font.BOLD));
            } else if (history.isInFuture(diagram)) {
                setFont(font.deriveFont(Font.ITALIC));
            } else {
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
        if (!(history instanceof DiagramHistory)) {
            throw new ClassCastException("This view needs a model of type DiagramController.DiagramHistory");
        }
        this.setCellRenderer(new DiagramCellRenderer());
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}