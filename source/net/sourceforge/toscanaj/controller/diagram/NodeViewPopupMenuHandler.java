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
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
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

    public NodeViewPopupMenuHandler(DiagramView diagramView) {
        this.diagramView = diagramView;
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
		String description = "Intent (" + concept.getIntentSize() + 
                             " attributes):" + lineSeparator; 
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
		boolean oldDisplayMode = context.getObjectDisplayMode();
		description += "Extent (" + concept.getExtentSize() + 
					   " objects):" + lineSeparator; 
		context.setObjectDisplayMode(ConceptInterpretationContext.CONTINGENT);
		Iterator objContIt = interpreter.getObjectSetIterator(concept, context);
		Set objectContingent = new HashSet();
		while (objContIt.hasNext()) {
			Object object = objContIt.next();
			description += "+ " + object.toString() + lineSeparator;
			objectContingent.add(object);
		}
		context.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
		Iterator extentIt = interpreter.getObjectSetIterator(concept, context);
		while (extentIt.hasNext()) {
			Object object = extentIt.next();
			if(! objectContingent.contains(object)) {
				description += "- " + object.toString() + lineSeparator;
			}
		}
		context.setObjectDisplayMode(oldDisplayMode);
		//export to clipboard
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(description),this);
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	// For implementing copy to clipboard function. Don't have to do anything here
	}
}
