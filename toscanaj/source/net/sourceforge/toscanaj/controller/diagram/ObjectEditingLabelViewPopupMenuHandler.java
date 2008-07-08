/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $$Id$$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * Assumptions: - a Concept implementation used here is a ConceptImplementation
 * class - an object in the FCA sense is a String.
 */
public class ObjectEditingLabelViewPopupMenuHandler implements
        EventBrokerListener {
    private final DiagramView diagramView;

    public ObjectEditingLabelViewPopupMenuHandler(final DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(final Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (final ClassCastException exc) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to CanvasItemEventWithPositions only");
        }
        ObjectLabelView labelView = null;
        try {
            labelView = (ObjectLabelView) itemEvent.getItem();
        } catch (final ClassCastException exc) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent
                .getAWTPosition());
    }

    public void openPopupMenu(final ObjectLabelView labelView,
            final Point2D canvasPosition, final Point2D screenPosition) {
        final JPopupMenu popupMenu = new JPopupMenu();

        final JMenu queryTypeMenu = new JMenu("Change Label");
        queryTypeMenu.add(createQueryMenuItem(labelView,
                AggregateQuery.COUNT_QUERY));
        queryTypeMenu.add(createQueryMenuItem(labelView,
                ListQuery.KEY_LIST_QUERY));
        queryTypeMenu.add(createQueryMenuItem(labelView,
                AggregateQuery.PERCENT_QUERY));
        popupMenu.add(queryTypeMenu);

        if (labelView.getQuery() instanceof ListQuery) {
            final FCAElement object = labelView
                    .getObjectAtPosition(canvasPosition);
            final String currentValue = object.toString();

            final JMenuItem renameObjectMenuItem = new JMenuItem(
                    "Rename object...");
            renameObjectMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent event) {
                    final InputTextDialog dialog = new InputTextDialog(
                            JOptionPane
                                    .getFrameForComponent(ObjectEditingLabelViewPopupMenuHandler.this.diagramView),
                            "Rename Object", "object", currentValue);
                    if (!dialog.isCancelled()) {
                        final String newValue = dialog.getInput();
                        final ConceptImplementation concept = (ConceptImplementation) labelView
                                .getNodeView().getDiagramNode().getConcept();
                        final WritableFCAElement newObject = new FCAElementImplementation(
                                newValue);
                        concept.replaceObject(object, newObject);
                        labelView.updateEntries();
                    }
                }
            });
            popupMenu.add(renameObjectMenuItem);
        }

        popupMenu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }

    private JMenuItem createQueryMenuItem(final ObjectLabelView labelView,
            final Query query) {
        final JMenuItem queryItem = new JMenuItem(query.getName());
        queryItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                labelView.setQuery(query);
            }
        });
        return queryItem;
    }

}
