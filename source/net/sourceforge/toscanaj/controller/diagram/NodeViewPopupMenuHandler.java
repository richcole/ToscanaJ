/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NodeViewPopupMenuHandler implements EventBrokerListener, ClipboardOwner {
    private DiagramView diagramView;

    public NodeViewPopupMenuHandler(DiagramView diagramView, EventBroker eventBroker) {
        this.diagramView = diagramView;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class, NodeView.class);
    }

    public void processEvent(Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEventWithPositions only");
        }
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(nodeView, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final NodeView nodeView, Point2D canvasPosition, Point2D screenPosition) {
        JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Export concept information");
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				copyConceptInformationIntoClipboard(nodeView);
			}
		});
		popupMenu.add(menuItem);
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

	private void copyConceptInformationIntoClipboard(NodeView nodeView) {
		String lineSeparator = System.getProperty("line.separator");
		Concept concept = nodeView.getDiagramNode().getConcept();
		ConceptInterpreter interpreter = this.diagramView.getConceptInterpreter();
		ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
	    String description = "";
		int intentSize = concept.getIntentSize();
		if(intentSize > 0) {
			if(intentSize == 1) {
			    description = "Intent (1 attribute):" + lineSeparator;
			} else {
			    description = "Intent (" + intentSize + 
			                         " attributes):" + lineSeparator;
			} 
			Iterator attrContIt = concept.getAttributeContingentIterator();
			Set attributeContingent = new HashSet();
			while (attrContIt.hasNext()) {
				Object attrib = attrContIt.next();
				description += "+ " + attrib.toString() + lineSeparator;
				attributeContingent.add(attrib);
			}
			Iterator intentIt = concept.getIntentIterator();
			while (intentIt.hasNext()) {
				Object attrib = intentIt.next();
				if(! attributeContingent.contains(attrib)) {
					description += "- " + attrib.toString() + lineSeparator;
				}
			}
	    } else {
		        description += "Empty intent" + lineSeparator;
		}

	    description += lineSeparator;

		int extentSize = interpreter.getExtentSize(concept, context);
	    if(extentSize > 0) {
	        if(extentSize == 1) {
	            description += "Extent (1 object):" + lineSeparator;
	        } else {
	            description += "Extent (" + extentSize +
	                           " objects):" + lineSeparator;
	        }
			boolean oldDisplayMode = context.getObjectDisplayMode();
		    context.setObjectDisplayMode(ConceptInterpretationContext.CONTINGENT);
			Iterator objContIt = interpreter.getObjectSetIterator(concept, context);
			Set objectContingent = new HashSet();
			while (objContIt.hasNext()) {
				Object object = objContIt.next();
				String objectName = object.toString();
                description += "+ " + objectName + lineSeparator;
			    // we have to use the name here since the objects are created everytime anew
				objectContingent.add(objectName); 
			}
			context.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
			Iterator extentIt = interpreter.getObjectSetIterator(concept, context);
			while (extentIt.hasNext()) {
				Object object = extentIt.next();
			    String objectName = object.toString();
				if(! objectContingent.contains(objectName)) {
                    description += "- " + objectName + lineSeparator;
				}
			}
			context.setObjectDisplayMode(oldDisplayMode);
	    } else {
	    	description += "Empty extent" + lineSeparator;
	    }
		
		//export to clipboard
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(description),this);
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	// For implementing copy to clipboard function. Don't have to do anything here
	}
}
