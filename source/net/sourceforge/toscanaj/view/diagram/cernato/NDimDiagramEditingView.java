/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram.cernato;

import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.model.DiagramCollection;
import net.sourceforge.toscanaj.controller.cernato.NDimNodeMovementEventListener;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NDimDiagramEditingView extends DiagramEditingView {
    protected NDimNodeMovementEventListener ndimMovementEventListener;

    public NDimDiagramEditingView(JFrame frame, DiagramCollection conceptualSchema, EventBroker eventBroker) {
        super(frame, conceptualSchema, eventBroker);
    }

    protected void createMovementManipulators(JPanel toolPanel) {
        toolPanel.add(new JLabel("Movement:"));
        final EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        ndimMovementEventListener = new NDimNodeMovementEventListener();
        canvasEventBroker.subscribe(
                ndimMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
        String[] movementNames = {"Cernato", "Node", "Ideal", "Filter"};
        JComboBox movementChooser = new JComboBox(movementNames);
        toolPanel.add(movementChooser);
        movementChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox combobox = (JComboBox) e.getSource();
                String selection = combobox.getSelectedItem().toString();
                if (selection.equals("Cernato")) {
                    canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
                    canvasEventBroker.removeSubscriptions(idealMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            ndimMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if (selection.equals("Node")) {
                    canvasEventBroker.removeSubscriptions(ndimMovementEventListener);
                    canvasEventBroker.removeSubscriptions(idealMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            nodeMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if (selection.equals("Ideal")) {
                    canvasEventBroker.removeSubscriptions(ndimMovementEventListener);
                    canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            idealMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if (selection.equals("Filter")) {
                    canvasEventBroker.removeSubscriptions(ndimMovementEventListener);
                    canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
                    canvasEventBroker.removeSubscriptions(idealMovementEventListener);
                    canvasEventBroker.subscribe(
                            filterMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                }
            }
        });
    }
}
