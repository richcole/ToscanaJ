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
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.gui.ToscanaJMainPanel;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import org.jdom.Element;
import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class BackgroundPopupMenuHandler implements EventBrokerListener {
    private final DiagramView diagramView;
    private final ToscanaJMainPanel mainPanel;
    private JPopupMenu menu;
    private JMenuItem goBackOneDiagramItem;
    private JMenu showDiagramDescriptionMenu;
    private JMenuItem showAnalysisHistory;
    private JMenuItem showInnerDiagramDescription;
    private JMenuItem showOuterDiagramDescription;
    private JMenu changeLabelMenu;
    private final DiagramController diagContr = DiagramController
            .getController();
    private JMenuItem showCurrentDiagramDescription;
    private JMenuItem flatDiagramItem;
    private JMenuItem nestedDiagramItem;

    public BackgroundPopupMenuHandler(final DiagramView diagramView,
            final EventBroker eventBroker, final ToscanaJMainPanel mainPanel) {
        this.diagramView = diagramView;
        this.mainPanel = mainPanel;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class,
                CanvasBackground.class);
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

        assert itemEvent.getItem() instanceof CanvasBackground : getClass()
                .getName()
                + " has to be subscribed to events from CanvasBackground only";

        openPopupMenu(itemEvent.getAWTPosition());
    }

    protected void openPopupMenu(final Point2D screenPosition) {
        this.menu = new JPopupMenu();
        this.goBackOneDiagramItem = new JMenuItem("Go back one diagram");
        this.menu.add(this.goBackOneDiagramItem);
        this.goBackOneDiagramItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                DiagramController.getController().back();
                updateStatus();
            }
        });
        this.menu.add(new JSeparator());

        this.showDiagramDescriptionMenu = new JMenu("Show Diagram Description");
        this.showCurrentDiagramDescription = new JMenuItem("Current Diagram");

        makeShowDiagramDescriptionMenu();
        this.menu.add(this.showDiagramDescriptionMenu);

        this.showAnalysisHistory = new JMenuItem("Show Analysis History");
        this.menu.add(this.showAnalysisHistory);
        this.showAnalysisHistory.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                BackgroundPopupMenuHandler.this.mainPanel
                        .showDiagramContextDescription();
            }
        });

        this.menu.add(new JSeparator());

        if (this.diagContr.getDiagramHistory().getNestingLevel() == 0) {
            this.nestedDiagramItem = new JMenuItem("Nested Diagram");
            this.nestedDiagramItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    BackgroundPopupMenuHandler.this.diagContr
                            .setNestingLevel(1);
                }
            });
            this.menu.add(this.nestedDiagramItem);
        } else {
            this.flatDiagramItem = new JMenuItem("Flat Diagram");
            this.flatDiagramItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    BackgroundPopupMenuHandler.this.diagContr
                            .setNestingLevel(0);
                }
            });

            this.menu.add(this.flatDiagramItem);
        }
        makeChangeObjectLabel();

        updateStatus();
        this.menu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }

    protected void updateStatus() {

        this.goBackOneDiagramItem.setEnabled(DiagramController.getController()
                .undoIsPossible());

        if (this.diagramView.getDiagram() == null) {
            this.goBackOneDiagramItem.setEnabled(false);
            this.showDiagramDescriptionMenu.setEnabled(false);
            this.showAnalysisHistory.setEnabled(false);
            this.changeLabelMenu.setEnabled(false);
            this.nestedDiagramItem.setEnabled(false);
        }
    }

    protected void makeChangeObjectLabel() {
        final ButtonGroup labelContentGroup = new ButtonGroup();
        this.changeLabelMenu = new JMenu("Change All Object Labels");
        final ConceptualSchema conceptualSchema = this.mainPanel
                .getConceptualSchema();
        if (conceptualSchema != null) {
            final Iterator<Query> it = conceptualSchema.getQueries().iterator();
            if (it.hasNext()) {
                int count = 0;
                while (it.hasNext()) {
                    final Query query = it.next();
                    count++;
                    final String name = query.getName();
                    final JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(
                            name);
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            BackgroundPopupMenuHandler.this.diagramView
                                    .setQuery(query);
                        }
                    });
                    labelContentGroup.add(menuItem);
                    this.changeLabelMenu.add(menuItem);
                    if (query == ObjectLabelView.getDefaultQuery()) {
                        menuItem.setSelected(true);
                    }
                }
            }
        }
        this.menu.add(this.changeLabelMenu);
    }

    protected void makeShowDiagramDescriptionMenu() {
        final Diagram2D curDiag = this.diagContr.getCurrentDiagram();
        if (curDiag != null) {
            boolean outerDiagramEnabled = false;
            boolean showAboutDiagramComponents;
            boolean innerDiagramEnabled = false;
            if (this.diagContr.getDiagramHistory().getNumberOfCurrentDiagrams() == 1) {
                final Element diagDesc = curDiag.getDescription();
                showAboutDiagramComponents = diagDesc != null;
            } else {
                final Diagram2D outerDiagram = this.diagContr
                        .getDiagramHistory().getCurrentDiagram(0);
                final Element outerDiagDesc = outerDiagram.getDescription();
                final Diagram2D innerDiagram = this.diagContr
                        .getDiagramHistory().getCurrentDiagram(1);
                final Element innerDiagDesc = innerDiagram.getDescription();
                showAboutDiagramComponents = (outerDiagDesc != null)
                        || (innerDiagDesc != null);
                outerDiagramEnabled = outerDiagDesc != null;
                innerDiagramEnabled = innerDiagDesc != null;
            }
            this.showDiagramDescriptionMenu
                    .setEnabled(showAboutDiagramComponents);
            if (this.diagContr.getDiagramHistory().getNestingLevel() == 0) {
                this.showCurrentDiagramDescription = new JMenuItem(
                        "Current Diagram");
                this.showCurrentDiagramDescription
                        .addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                DescriptionViewer
                                        .show(
                                                BackgroundPopupMenuHandler.this.mainPanel,
                                                DiagramController
                                                        .getController()
                                                        .getCurrentDiagram()
                                                        .getDescription());
                            }
                        });
                this.showDiagramDescriptionMenu
                        .add(this.showCurrentDiagramDescription);
            } else if (innerDiagramEnabled && outerDiagramEnabled) {
                this.showOuterDiagramDescription = new JMenuItem(
                        "Outer Diagram");
                this.showInnerDiagramDescription = new JMenuItem(
                        "Inner Diagram");

                this.showOuterDiagramDescription
                        .addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                final Diagram2D outerDiagram = BackgroundPopupMenuHandler.this.diagContr
                                        .getDiagramHistory().getCurrentDiagram(
                                                0);
                                final Element outerDiagDesc = outerDiagram
                                        .getDescription();
                                DescriptionViewer
                                        .show(
                                                BackgroundPopupMenuHandler.this.mainPanel,
                                                outerDiagDesc);
                            }
                        });
                this.showInnerDiagramDescription
                        .addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                final Diagram2D innerDiagram = BackgroundPopupMenuHandler.this.diagContr
                                        .getDiagramHistory().getCurrentDiagram(
                                                1);
                                final Element innerDiagDesc = innerDiagram
                                        .getDescription();
                                DescriptionViewer
                                        .show(
                                                BackgroundPopupMenuHandler.this.mainPanel,
                                                innerDiagDesc);
                            }
                        });
                this.showDiagramDescriptionMenu
                        .add(this.showOuterDiagramDescription);
                this.showDiagramDescriptionMenu
                        .add(this.showInnerDiagramDescription);

            } else if (outerDiagramEnabled && !innerDiagramEnabled) {
                this.showOuterDiagramDescription = new JMenuItem(
                        "Outer Diagram");
                this.showOuterDiagramDescription
                        .addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                final Diagram2D outerDiagram = BackgroundPopupMenuHandler.this.diagContr
                                        .getDiagramHistory().getCurrentDiagram(
                                                0);
                                final Element outerDiagDesc = outerDiagram
                                        .getDescription();
                                DescriptionViewer
                                        .show(
                                                BackgroundPopupMenuHandler.this.mainPanel,
                                                outerDiagDesc);
                            }
                        });
                this.showDiagramDescriptionMenu
                        .add(this.showOuterDiagramDescription);
            } else if (innerDiagramEnabled && !outerDiagramEnabled) {
                this.showInnerDiagramDescription = new JMenuItem(
                        "Inner Diagram");
                this.showInnerDiagramDescription
                        .addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                final Diagram2D innerDiagram = BackgroundPopupMenuHandler.this.diagContr
                                        .getDiagramHistory().getCurrentDiagram(
                                                1);
                                final Element innerDiagDesc = innerDiagram
                                        .getDescription();
                                DescriptionViewer
                                        .show(
                                                BackgroundPopupMenuHandler.this.mainPanel,
                                                innerDiagDesc);
                            }
                        });
                this.showDiagramDescriptionMenu
                        .add(this.showInnerDiagramDescription);
            }
        }

        else {
            this.showDiagramDescriptionMenu.setEnabled(false);
        }
    }
}
