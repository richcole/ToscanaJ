/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $$Id$$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.SqlClauseLabelView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * It seems that in Elba context we only have one object in underlying Concept object iterator, 
 * which effects the way we are trying to change an Sql Clause. (SqlClauseLabelView 
 * is constructed by splitting an object string into a number of label entries). Hence  the 
 * following assumptions.
 * Assumptions:
 * - there is always only one object in objects iterator for a corresponding Concept.
 * - this object is always a String.
 * - a Concept implementation used here is a ConceptImplementation class
 */
public class SqlClauseEditingLabelViewPopupMenuHandler implements EventBrokerListener {
    private DiagramView diagramView;

    public SqlClauseEditingLabelViewPopupMenuHandler(DiagramView diagramView, EventBroker schemaBroker) {
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
        SqlClauseLabelView labelView = null;
        try {
            labelView = (SqlClauseLabelView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from SqlClauseLabelView only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final SqlClauseLabelView labelView, Point2D canvasPosition, Point2D screenPosition) {

		Iterator objIt = labelView.getNodeView().getDiagramNode().getConcept().getObjectContingentIterator();
		Object curObjectValue = null;
		if (objIt.hasNext()) {
			curObjectValue = objIt.next();
		}
		final Object currentValue = curObjectValue;
        final String currentValueString = curObjectValue.toString();
               
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem renameSqlClauseMenuItem = new JMenuItem("Change sql clause...");
		renameSqlClauseMenuItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				InputTextDialog dialog = new InputTextDialog(JOptionPane.getFrameForComponent(diagramView), 
															 "Change SQL Clause", "SQL clause", currentValueString);
				if (!dialog.isCancelled()) {
					String newValue = dialog.getInput();
					ConceptImplementation concept = (ConceptImplementation) labelView.getNodeView().getDiagramNode().getConcept();
					WritableFCAElement newObject = new FCAElementImplementation(newValue);
					concept.replaceObject(currentValue, newObject);
					labelView.updateEntries();
				}
			}
		});
		popupMenu.add(renameSqlClauseMenuItem);
        
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

}
