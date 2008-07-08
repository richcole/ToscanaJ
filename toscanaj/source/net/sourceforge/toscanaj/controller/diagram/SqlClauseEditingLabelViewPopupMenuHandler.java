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
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.SqlClauseLabelView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * It seems that in Elba context we only have one object in underlying Concept
 * object iterator, which effects the way we are trying to change an Sql Clause.
 * (SqlClauseLabelView is constructed by splitting an object string into a
 * number of label entries). Hence the following assumptions. Assumptions: -
 * there is always only one object in objects iterator for a corresponding
 * Concept. - this object is always a String. - a Concept implementation used
 * here is a ConceptImplementation class
 */
public class SqlClauseEditingLabelViewPopupMenuHandler implements
EventBrokerListener {
    private final DiagramView diagramView;

    public SqlClauseEditingLabelViewPopupMenuHandler(
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
        SqlClauseLabelView labelView = null;
        try {
            labelView = (SqlClauseLabelView) itemEvent.getSubject();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                    + " has to be subscribed to events from SqlClauseLabelView only");
        }
        openPopupMenu(labelView, itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final SqlClauseLabelView labelView,
            final Point2D screenPosition) {

        final Iterator objIt = labelView.getNodeView().getDiagramNode()
        .getConcept().getObjectContingentIterator();
        FCAElement curObjectValue = null;
        if (objIt.hasNext()) {
            curObjectValue = (FCAElement) objIt.next();
        } else {
            return; // nothing to display
        }
        final FCAElement currentValue = curObjectValue;
        final String currentValueString = curObjectValue.toString();

        // create the menu
        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem renameSqlClauseMenuItem = new JMenuItem(
        "Change sql clause...");
        renameSqlClauseMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                final InputTextDialog dialog = new InputTextDialog(
                        JOptionPane
                        .getFrameForComponent(SqlClauseEditingLabelViewPopupMenuHandler.this.diagramView),
                        "Change SQL Clause", "SQL clause", currentValueString);
                if (!dialog.isCancelled()) {
                    final String newValue = dialog.getInput();
                    final ConceptImplementation concept = (ConceptImplementation) labelView
                    .getNodeView().getDiagramNode().getConcept();
                    final WritableFCAElement newObject = new FCAElementImplementation(
                            newValue);
                    concept.replaceObject(currentValue, newObject);
                    labelView.updateEntries();
                }
            }
        });
        popupMenu.add(renameSqlClauseMenuItem);

        popupMenu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }

}
