/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.eventhandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JList;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.gui.DiagramOrganiser;

public class DiagramOrganiserDnDController implements DragGestureListener,
        DropTargetListener, DragSourceListener {

    private final JList sourceList;

    private final JList destinationList;

    private final DragSource newDiagramDragSource;

    private final DragSource diagramReorderDragSource;

    private final DiagramOrganiser diagramOrganiser;

    private int mode;

    private static final int HISTORY_REORDER_MODE = 0;
    private static final int ADD_DIAGRAM_MODE = 1;

    public DiagramOrganiserDnDController(final JList sourceList,
            final JList destinationList, final DiagramOrganiser diagramOrganiser) {
        this.diagramOrganiser = diagramOrganiser;

        this.destinationList = destinationList;

        this.sourceList = sourceList;

        this.newDiagramDragSource = new DragSource();
        this.newDiagramDragSource.createDefaultDragGestureRecognizer(
                this.sourceList, DnDConstants.ACTION_COPY, this);

        this.diagramReorderDragSource = new DragSource();
        this.diagramReorderDragSource.createDefaultDragGestureRecognizer(
                this.destinationList, DnDConstants.ACTION_MOVE, this);

        new DropTarget(this.destinationList, DnDConstants.ACTION_COPY_OR_MOVE,
                this);
    }

    /**
     * a drag gesture has been initiated
     */
    public void dragGestureRecognized(final DragGestureEvent event) {
        if (event.getComponent() == this.sourceList) {
            final int itemDragged = this.sourceList.locationToIndex(event
                    .getDragOrigin());
            final StringSelection transferable = new StringSelection(
                    (new Integer(itemDragged)).toString());
            event.startDrag(null, transferable, this);
            this.mode = ADD_DIAGRAM_MODE;
        } else if (event.getComponent() == this.destinationList) {
            final int itemDragged = this.destinationList.locationToIndex(event
                    .getDragOrigin());
            final DiagramHistory history = (DiagramHistory) this.destinationList
                    .getModel();
            if (!history.isInPast(itemDragged)) {
                // / @todo add specific transferable and data flavor for this
                final StringSelection transferable = new StringSelection(
                        (new Integer(itemDragged)).toString());
                this.newDiagramDragSource.startDrag(event, null, transferable,
                        this);
            }
            this.mode = HISTORY_REORDER_MODE;
        } else {
            throw new RuntimeException("Unknown drag source");
        }
    }

    public void dragEnter(final DropTargetDragEvent event) {
        // not interesting for us
    }

    /**
     * is invoked when a drag operation is going on
     * 
     */
    public void dragOver(final DropTargetDragEvent event) {
        final int itemUnder = this.destinationList.locationToIndex(event
                .getLocation());
        final DiagramHistory history = (DiagramHistory) this.destinationList
                .getModel();
        if (history.isInPast(itemUnder)) {
            event.rejectDrag();
        } else {
            if (this.mode == HISTORY_REORDER_MODE) {
                event.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            }
        }
    }

    public void dropActionChanged(final DropTargetDragEvent event) {
        // not interesting for us
    }

    /**
     * is invoked when you are exit the DropSite without dropping
     * 
     */
    public void dragExit(final DropTargetEvent event) {
        // not interesting for us
    }

    /**
     * a drop has occurred
     * 
     */
    public void drop(final DropTargetDropEvent event) {
        try {
            final Transferable transferable = event.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                final String s = (String) transferable
                        .getTransferData(DataFlavor.stringFlavor);
                final DiagramHistory history = (DiagramHistory) this.destinationList
                        .getModel();
                final int startIndex = Integer.parseInt(s);
                final int endIndex = this.destinationList.locationToIndex(event
                        .getLocation());
                if (this.mode == HISTORY_REORDER_MODE) {
                    event.acceptDrop(DnDConstants.ACTION_MOVE);
                    history.moveDiagram(startIndex, endIndex);
                } else {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    if (endIndex == -1) {
                        history.addDiagram(this.diagramOrganiser.getSchema()
                                .getDiagram(startIndex));
                    } else {
                        history.insertDiagram(endIndex, this.diagramOrganiser
                                .getSchema().getDiagram(startIndex));
                    }
                }
                event.getDropTargetContext().dropComplete(true);
            } else {
                event.rejectDrop();
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
            event.rejectDrop();
        } catch (final UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            event.rejectDrop();
        }
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has entered the DropSite
     */
    public void dragEnter(final DragSourceDragEvent event) {
        // not interesting for us
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * is currently ocurring over the DropSite
     */
    public void dragOver(final DragSourceDragEvent event) {
        // not interesting for us
    }

    /**
     * is invoked if the use modifies the current drop gesture
     */
    public void dropActionChanged(final DragSourceDragEvent event) {
        // not interesting for us
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has exited the DropSite
     */
    public void dragExit(final DragSourceEvent event) {
        // not interesting for us
    }

    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has ended
     */
    public void dragDropEnd(final DragSourceDropEvent event) {
        // not interesting for us
    }
}
