/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.temporal;

import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.temporal.ArrowLabelView;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class ArrowEditingLabelViewPopupMenuHandler implements EventBrokerListener {
    private final DiagramView diagramView;

    public ArrowEditingLabelViewPopupMenuHandler(final DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(final Event e) {
        CanvasItemEventWithPosition itemEvent;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (final ClassCastException e1) {
            throw new RuntimeException(getClass().getName()
                            + " has to be subscribed to CanvasItemEventWithPositions only");
        }
        ArrowLabelView labelView;
        try {
            labelView = (ArrowLabelView) itemEvent.getSubject();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final ArrowLabelView labelView, final Point2D screenPosition) {
        final DiagramView parent = this.diagramView;
        JMenuItem menuItem;
        menuItem = new JMenuItem("Change text...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String newText = JOptionPane.showInputDialog(parent, "Enter text for label", labelView.getText());
                if(newText != null) {
                    labelView.setText(newText);
                    diagramView.repaint();
                }
            }
        });

        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(menuItem);
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }
}
