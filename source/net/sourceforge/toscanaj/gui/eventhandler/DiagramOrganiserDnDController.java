/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.eventhandler;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.gui.DiagramOrganiser;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import javax.swing.JList;



public class DiagramOrganiserDnDController implements 
					DragGestureListener , DropTargetListener , DragSourceListener{
						
	private JList sourceList;
	
	private JList destinationList;
	
	private DropTarget dropTarget;
	
	private DragSource newDiagramDragSource;

	private DragSource diagramReorderDragSource;
	
	private DiagramOrganiser diagramOrganiser;
	
	private int mode;
	
	private static final int HISTORY_REORDER_MODE = 0;
	private static final int ADD_DIAGRAM_MODE = 1; 					
						
	public DiagramOrganiserDnDController(JList sourceList, JList destinationList , 
					DiagramOrganiser diagramOrganiser )
	{
		this.diagramOrganiser = diagramOrganiser;
		
		this.destinationList = destinationList;
		
		this.sourceList = sourceList;
		
		this.newDiagramDragSource = new DragSource();
		this.newDiagramDragSource.createDefaultDragGestureRecognizer(
								this.sourceList,DnDConstants.ACTION_COPY ,this);
		
		this.diagramReorderDragSource = new DragSource();
		this.diagramReorderDragSource.createDefaultDragGestureRecognizer(
								this.destinationList,DnDConstants.ACTION_MOVE ,this);
		
		this.dropTarget = new DropTarget(
								this.destinationList,DnDConstants.ACTION_COPY_OR_MOVE,this);
	}
	
	
	
	 /**
     * a drag gesture has been initiated
     */
	public void dragGestureRecognized(DragGestureEvent event){
		if( event.getComponent() == this.sourceList ) {
			int itemDragged = sourceList.locationToIndex(event.getDragOrigin());
			StringSelection transferable = new StringSelection((new Integer(itemDragged)).toString());
			event.startDrag(null, transferable, this);
			this.mode = ADD_DIAGRAM_MODE;
		} else if ( event.getComponent() == this.destinationList ) {
			int itemDragged = this.destinationList.locationToIndex(event.getDragOrigin());
			DiagramHistory history = (DiagramHistory) this.destinationList.getModel();
			if (!history.isInPast(itemDragged)) {
			/// @todo add specific transferable and data flavor for this
				StringSelection transferable = new StringSelection((new Integer(itemDragged)).toString());
				newDiagramDragSource.startDrag(event, null, transferable, this);
			}	
			this.mode = HISTORY_REORDER_MODE; 
		} else {
			throw new RuntimeException("Unknown drag source");
		}
	}
	
	public void dragEnter(DropTargetDragEvent event) {
	}
	
	/**
     * is invoked when a drag operation is going on
     *
     */
	public void dragOver(DropTargetDragEvent event) {
		int itemUnder = this.destinationList.locationToIndex(event.getLocation());
		DiagramHistory history = (DiagramHistory) this.destinationList.getModel();
		if (history.isInPast(itemUnder)) {
			event.rejectDrag();
		} 
		else {
			if( this.mode == HISTORY_REORDER_MODE ) {
				event.acceptDrag(DnDConstants.ACTION_MOVE);
			} else {
				event.acceptDrag(DnDConstants.ACTION_COPY);
			}
		}
	}


	public void dropActionChanged(DropTargetDragEvent event) {
	}

	/**
     * is invoked when you are exit the DropSite without dropping
	 *
	 */
	public void dragExit(DropTargetEvent event) {
	}
	
    /**
     * a drop has occurred
     *
     */
	public void drop(DropTargetDropEvent event) {
		try {	
			Transferable transferable = event.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
				DiagramHistory history = (DiagramHistory) this.destinationList.getModel();
				int startIndex = Integer.parseInt(s);
				int endIndex = this.destinationList.locationToIndex(event.getLocation());
				if( this.mode == HISTORY_REORDER_MODE ) {
					event.acceptDrop(DnDConstants.ACTION_MOVE);
					history.moveDiagram(startIndex, endIndex);
				} else {
					event.acceptDrop(DnDConstants.ACTION_COPY);
					if( endIndex == -1 ) {
						history.addDiagram(diagramOrganiser.getSchema().getDiagram(startIndex));
					} else {
						history.insertDiagram(endIndex,diagramOrganiser.getSchema().getDiagram(startIndex));
					}
				}					
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
     * this message goes to DragSourceListener, informing it that the dragging
     * has entered the DropSite
     */
	public void dragEnter(DragSourceDragEvent event) {
	}

	/**
     * this message goes to DragSourceListener, informing it that the dragging is currently
     * ocurring over the DropSite
     */
	public void dragOver(DragSourceDragEvent event) {
	}

	/**
     * is invoked if the use modifies the current drop gesture
     */
	public void dropActionChanged(DragSourceDragEvent event) {
	}

	/**
	 * this message goes to DragSourceListener, informing it that the dragging
	 * has exited the DropSite
	*/
	public void dragExit(DragSourceEvent event) {
	}
	
    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has ended
     */
	public void dragDropEnd(DragSourceDropEvent event) {
	}
}
