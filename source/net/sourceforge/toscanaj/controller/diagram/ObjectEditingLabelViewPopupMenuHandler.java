/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $$Id$$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;
import net.sourceforge.toscanaj.view.context.InputTextDialog;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * Assumptions:
 * - a Concept implementation used here is a ConceptImplementation class
 * - an object in the FCA sense is a String.  
 */
public class ObjectEditingLabelViewPopupMenuHandler implements EventBrokerListener {
    private DiagramView diagramView;

    public ObjectEditingLabelViewPopupMenuHandler(DiagramView diagramView, EventBroker schemaBroker) {
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
        ObjectLabelView labelView = null;
        try {
            labelView = (ObjectLabelView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final ObjectLabelView labelView, Point2D canvasPosition, Point2D screenPosition) {
        final Object object = labelView.getObjectAtPosition(canvasPosition);
		final String currentValue =  object.toString();
        
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem renameObjectMenuItem = new JMenuItem("Rename object...");
		renameObjectMenuItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				InputTextDialog dialog = new InputTextDialog(JOptionPane.getFrameForComponent(diagramView), 
															 "Rename Object", "object", currentValue);
				if (!dialog.isCancelled()) {
					String newValue = dialog.getInput();
					ConceptImplementation concept = (ConceptImplementation) labelView.getNodeView().getDiagramNode().getConcept();
					concept.replaceObject(object, newValue);
					labelView.updateEntries();
				}
			}
		});
		popupMenu.add(renameObjectMenuItem);
        
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

}
