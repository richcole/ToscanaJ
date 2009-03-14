/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramReference;

/**
 * A view for presenting the list of all diagrams.
 * 
 * This will display a list of diagrams from the DiagramHistory where all
 * diagrams which were used (normal history), are used (current view, might be
 * more than one if nesting is used) and will be used (list of diagrams for
 * zooming) are shown.
 * 
 * All three list are shown as one long list but with different fonts.
 */
public class DiagramHistoryView extends JList {
    /**
     * The cell renderer renders the diagram titles according to the position of
     * the diagram (past, current, future).
     */
    class DiagramCellRenderer extends JLabel implements ListCellRenderer {
        /**
         * Sets the diagram title on the cell and uses different fonts to
         * display it.
         */
        public Component getListCellRendererComponent(final JList list,
                final Object value, // value to display
                final int index, // cell index
                final boolean isSelected, // is the cell selected
                final boolean cellHasFocus) // the list and the cell have the
        // focus
        {
            final DiagramHistory history = (DiagramHistory) list.getModel();

            setText(value.toString());
            setOpaque(true);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (list.hasFocus()) {
                    setBorder(new javax.swing.border.LineBorder(Color.yellow));
                } else {
                    setBorder(null);
                }
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
                setBorder(null);
            }
            setEnabled(list.isEnabled());
            final Font font = list.getFont();

            final DiagramReference diagram = (DiagramReference) value;
            if (history.isInCurrent(diagram)) {
                setFont(font.deriveFont(Font.BOLD));
            } else if (history.isInFuture(diagram)) {
                setFont(font.deriveFont(Font.ITALIC));
            } else {
                setFont(font.deriveFont(Font.PLAIN));
            }
            return this;
        }
    }

    /**
     * Creates a new view for the given history.
     */
    public DiagramHistoryView(final ListModel history) {
        super(history);
        if (!(history instanceof DiagramHistory)) {
            throw new ClassCastException(
                    "This view needs a model of type net.sourceforge.toscanaj.controller.fca.DiagramHistory");
        }
        this.setCellRenderer(new DiagramCellRenderer());
    }
}
