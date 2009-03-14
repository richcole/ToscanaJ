/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class AttributeEditingLabelViewPopupMenuHandler implements
        EventBrokerListener {
    private final DiagramView diagramView;

    public AttributeEditingLabelViewPopupMenuHandler(
            final DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(final Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to CanvasItemEventWithPositions only");
        }
        AttributeLabelView labelView = null;
        try {
            labelView = (AttributeLabelView) itemEvent.getSubject();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent
                .getAWTPosition());
    }

    public void openPopupMenu(final AttributeLabelView labelView,
            final Point2D canvasPosition, final Point2D screenPosition) {
        final WritableFCAElement attribute = (WritableFCAElement) labelView
                .getEntryAtPosition(canvasPosition);
        if (attribute == null) {
            return;
        }
        final DiagramView parent = this.diagramView;
        JMenuItem menuItem;
        menuItem = new JMenuItem("Description...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final XMLEditorDialog xmlEditorDialog = new XMLEditorDialog(
                        JOptionPane.getFrameForComponent(parent),
                        "Edit attribute description");
                xmlEditorDialog.setContent(attribute.getDescription());
                xmlEditorDialog.setVisible(true);
                attribute.setDescription(xmlEditorDialog.getContent());
            }
        });

        final JMenuItem renameAttrMenuItem = new JMenuItem(
                "Rename attribute...");
        final String currentValue = attribute.getData().toString();
        renameAttrMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                final InputTextDialog dialog = new InputTextDialog(
                        JOptionPane
                                .getFrameForComponent(AttributeEditingLabelViewPopupMenuHandler.this.diagramView),
                        "Rename Attribute", "attribute", currentValue);
                if (!dialog.isCancelled()) {
                    final String newValue = dialog.getInput();
                    attribute.setData(newValue);
                    AttributeEditingLabelViewPopupMenuHandler.this.diagramView
                            .repaint();
                }
            }
        });

        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(menuItem);
        popupMenu.add(renameAttrMenuItem);

        popupMenu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }
}
