/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

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
public class DiagramHistoryView extends JList
        implements DropTargetListener, DragSourceListener, DragGestureListener {
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

    static class NoSelectionListSelectionModel extends DefaultListSelectionModel {
        // implements javax.swing.ListSelectionModel
        public boolean isSelectionEmpty() {
            return true;
        }

        public int getSelectionMode() {
            return SINGLE_SELECTION;
        }

        // implements javax.swing.ListSelectionModel
        public boolean isSelectedIndex(int index) {
            return false;
        }

        public void setLeadSelectionIndex(int leadIndex) {
            return;
        }
    }

    private DragSource dragSource = null;
    private DropTarget dropTarget = null;

    /**
     * Creates a new view for the given history.
     */
    public DiagramHistoryView(ListModel history) {
        super(history);
        if (!(history instanceof DiagramHistory)) {
            throw new ClassCastException("This view needs a model of type net.sourceforge.toscanaj.controller.fca.DiagramHistory");
        }
        this.setCellRenderer(new DiagramCellRenderer());
        this.setSelectionModel(new NoSelectionListSelectionModel());

        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    }

    public void dragEnter(DropTargetDragEvent event) {
    }

    /**
     * is invoked when you are exit the DropSite without dropping
     *
     */
    public void dragExit(DropTargetEvent event) {
    }

    /**
     * is invoked when a drag operation is going on
     *
     */
    public void dragOver(DropTargetDragEvent event) {
        int itemUnder = locationToIndex(event.getLocation());
        DiagramHistory history = (DiagramHistory) getModel();
        if (history.isInPast(itemUnder)) {
            event.rejectDrag();
        } else {
            event.acceptDrag(DnDConstants.ACTION_MOVE);
        }
    }

    /**
     * a drop has occurred
     *
     */
    public void drop(DropTargetDropEvent event) {
        try {
            Transferable transferable = event.getTransferable();

            // we accept only Strings
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_MOVE);
                String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                int startItem = Integer.parseInt(s);
                int endItem = locationToIndex(event.getLocation());
                DiagramHistory history = (DiagramHistory) getModel();
                history.moveDiagram(startItem, endItem);
                event.getDropTargetContext().dropComplete(true);
            } else {
                event.rejectDrop();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            event.rejectDrop();
        } catch (UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            event.rejectDrop();
        }
    }

    /**
     * is invoked if the use modifies the current drop gesture
     */
    public void dropActionChanged(DropTargetDragEvent event) {
    }

    /**
     * a drag gesture has been initiated
     */
    public void dragGestureRecognized(DragGestureEvent event) {
        int itemDragged = locationToIndex(event.getDragOrigin());
        DiagramHistory history = (DiagramHistory) getModel();
        if (!history.isInPast(itemDragged)) {
            /// @todo add specific transferable and data flavor for this
            StringSelection transferable = new StringSelection((new Integer(itemDragged)).toString());
            dragSource.startDrag(event, DragSource.DefaultMoveDrop, transferable, this);
        }
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has ended
     */
    public void dragDropEnd(DragSourceDropEvent event) {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has entered the DropSite
     */
    public void dragEnter(DragSourceDragEvent event) {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has exited the DropSite
     */
    public void dragExit(DragSourceEvent event) {
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging is currently
     * ocurring over the DropSite
     */
    public void dragOver(DragSourceDragEvent event) {
    }

    /**
     * is invoked when the user changes the dropAction
     */
    public void dropActionChanged(DragSourceDragEvent event) {
    }
}
