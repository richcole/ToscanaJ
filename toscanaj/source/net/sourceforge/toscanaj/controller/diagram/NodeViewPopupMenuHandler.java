/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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

public class NodeViewPopupMenuHandler implements EventBrokerListener,
        ClipboardOwner {
    private final DiagramView diagramView;

    public NodeViewPopupMenuHandler(final DiagramView diagramView,
            final EventBroker eventBroker) {
        this.diagramView = diagramView;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class,
                NodeView.class);
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
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getItem();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(nodeView, itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final NodeView nodeView,
            final Point2D screenPosition) {
        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem menuItem = new JMenuItem("Export concept information");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                copyConceptInformationIntoClipboard(nodeView);
            }
        });
        popupMenu.add(menuItem);
        popupMenu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }

    private void copyConceptInformationIntoClipboard(final NodeView nodeView) {
        final String lineSeparator = System.getProperty("line.separator");
        final Concept concept = nodeView.getDiagramNode().getConcept();
        final ConceptInterpreter interpreter = this.diagramView
                .getConceptInterpreter();
        final ConceptInterpretationContext context = nodeView
                .getConceptInterpretationContext();
        String description = "";
        final int intentSize = concept.getIntentSize();
        if (intentSize > 0) {
            if (intentSize == 1) {
                description = "Intent (1 attribute):" + lineSeparator;
            } else {
                description = "Intent (" + intentSize + " attributes):"
                        + lineSeparator;
            }
            final Iterator attrContIt = concept
                    .getAttributeContingentIterator();
            final Set<Object> attributeContingent = new HashSet<Object>();
            while (attrContIt.hasNext()) {
                final Object attrib = attrContIt.next();
                description += "+ " + attrib.toString() + lineSeparator;
                attributeContingent.add(attrib);
            }
            final Iterator intentIt = concept.getIntentIterator();
            while (intentIt.hasNext()) {
                final Object attrib = intentIt.next();
                if (!attributeContingent.contains(attrib)) {
                    description += "- " + attrib.toString() + lineSeparator;
                }
            }
        } else {
            description += "Empty intent" + lineSeparator;
        }

        description += lineSeparator;

        final int extentSize = interpreter.getExtentSize(concept, context);
        if (extentSize > 0) {
            if (extentSize == 1) {
                description += "Extent (1 object):" + lineSeparator;
            } else {
                description += "Extent (" + extentSize + " objects):"
                        + lineSeparator;
            }
            final boolean oldDisplayMode = context.getObjectDisplayMode();
            context
                    .setObjectDisplayMode(ConceptInterpretationContext.CONTINGENT);
            final Iterator objContIt = interpreter.getObjectSetIterator(
                    concept, context);
            final Set<String> objectContingent = new HashSet<String>();
            while (objContIt.hasNext()) {
                final Object object = objContIt.next();
                final String objectName = object.toString();
                description += "+ " + objectName + lineSeparator;
                // we have to use the name here since the objects are created
                // everytime anew
                objectContingent.add(objectName);
            }
            context.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
            final Iterator extentIt = interpreter.getObjectSetIterator(concept,
                    context);
            while (extentIt.hasNext()) {
                final Object object = extentIt.next();
                final String objectName = object.toString();
                if (!objectContingent.contains(objectName)) {
                    description += "- " + objectName + lineSeparator;
                }
            }
            context.setObjectDisplayMode(oldDisplayMode);
        } else {
            description += "Empty extent" + lineSeparator;
        }

        // export to clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(description), this);
    }

    public void lostOwnership(final Clipboard clipboard,
            final Transferable contents) {
        // For implementing copy to clipboard function. Don't have to do
        // anything here
    }
}
