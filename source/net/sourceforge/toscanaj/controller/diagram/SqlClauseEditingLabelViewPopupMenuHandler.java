/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $$Id$$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.SqlClauseLabelView;
import net.sourceforge.toscanaj.view.scales.InputTextDialog;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;

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
		String labelContents = "";
		Iterator it = labelView.getEntryIterator();
		while (it.hasNext()) {
			String curEntry = (String) it.next();
			labelContents = labelContents + curEntry;
		}
		final String currentValue = labelContents;
        
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem renameSqlClauseMenuItem = new JMenuItem("Change sql clause...");
		renameSqlClauseMenuItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				InputTextDialog dialog = new InputTextDialog(JOptionPane.getFrameForComponent(diagramView), 
															 "Change SQL Clause", "SQL clause", currentValue);
				if (!dialog.isCancelled()) {
					String newValue = dialog.getInput();
					Iterator it = labelView.getNodeView().getDiagramNode().getConcept().getObjectContingentIterator();
					while (it.hasNext()) {
						Object cur = (Object) it.next();
						//System.out.println("cur concept = " + cur);
						
					}
					diagramView.repaint();
				}
			}
		});
		popupMenu.add(renameSqlClauseMenuItem);
        
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

}
