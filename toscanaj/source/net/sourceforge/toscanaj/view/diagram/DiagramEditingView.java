/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.diagram.AttributeAdditiveNodeMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.AttributeEditingLabelViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.diagram.ChainBasedNodeMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.FilterMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.IdealMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.LabelClickEventHandler;
import net.sourceforge.toscanaj.controller.diagram.LabelDragEventHandler;
import net.sourceforge.toscanaj.controller.diagram.LabelScrollEventHandler;
import net.sourceforge.toscanaj.controller.diagram.NodeMovementEventListener;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramToContextConverter;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;
import net.sourceforge.toscanaj.view.context.ContextTableEditorDialog;

import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;
import org.tockit.swing.undo.ExtendedUndoManager;

public class DiagramEditingView extends JPanel implements EventBrokerListener {
    public static interface DiagramAction {
        public void actionPerformed(ActionEvent e, Diagram2D diagram);

        public Object getLabel();

        public boolean isEnabled();
    }

    /**
     * The factor by which the grid gets rescaled when a button is clicked.
     * 
     * This is set to the cubic root of 2, which means three clicks double/half
     * the grid distance. Whenever the grid distance is halfed, all nodes
     * formerly on a grid node are back on a grid node -- this approach thus
     * allows refining the grid while having the nodes still placed on it. A
     * factor of 2 or the square root of 2 was considered as too coarse.
     */
    private static final double GRID_SIZE_CHANGE_FACTOR = 1.2599210498948731647672106072782;
    private static final int DEFAULT_GRID_SIZE = 15;
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(DiagramEditingView.class);

    /**
     * The names used in the GUI for the manipulators.
     * 
     * This has to be the same size and order as the #NODE_MANIPULATORS array.
     */
    private static final String[] FULL_MOVEMENT_OPTION_NAMES = { "Additive",
            "Chain", "Node", "Ideal", "Filter" };

    /**
     * The manipulators offered for moving nodes.
     */
    private static final EventBrokerListener[] NODE_MANIPULATORS = {
            new AttributeAdditiveNodeMovementEventListener(),
            new ChainBasedNodeMovementEventListener(),
            new NodeMovementEventListener(), new IdealMovementEventListener(),
            new FilterMovementEventListener() };

    private ConceptualSchema conceptualSchema;
    private DefaultListModel diagramListModel;
    private final JSplitPane splitPane;
    protected DiagramView diagramView;
    private static final double ZOOM_FACTOR = 1.1;
    private JButton editContextButton;
    private DatabaseConnection databaseConnection = null;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JComboBox movementChooser;
    private JButton editDiagramDescButton;
    protected ContextTableEditorDialog contextEditingDialog;
    private JButton gridIncreaseButton;
    private JButton gridDecreaseButton;
    private JCheckBox gridEnabledCheckBox;
    private final Frame parent;
    private final LabeledPanel diagramListView;
    private DiagramAction[] extraContextMenuActions;

    public DiagramEditingView(final Frame parent,
            final ConceptualSchema conceptualSchema,
            final EventBroker eventBroker, final boolean offerConsistencyCheck) {
        super();
        this.conceptualSchema = conceptualSchema;
        this.parent = parent;

        setLayout(new BorderLayout());

        setName("DiagramEditingView");

        diagramListView = makeDiagramListView();
        final JPanel mainDiagramView = makeDiagramViewPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                diagramListView, mainDiagramView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        this.contextEditingDialog = new ContextTableEditorDialog(parent,
                this.conceptualSchema, this.databaseConnection, eventBroker,
                offerConsistencyCheck);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class,
                Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
        eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.diagramView.getController().getEventBroker().subscribe(this,
                DisplayedDiagramChangedEvent.class, Object.class);
        this.diagramView.getController().getEventBroker().subscribe(
                new AttributeEditingLabelViewPopupMenuHandler(diagramView),
                CanvasItemContextMenuRequestEvent.class,
                AttributeLabelView.class);
    }

    protected JPanel makeDiagramViewPanel() {
        final JPanel diagramViewPanel = new JPanel(new BorderLayout());

        diagramView = new DiagramView();
        diagramView.setQuery(ListQuery.KEY_LIST_QUERY);
        final EventBroker canvasEventBroker = diagramView.getController()
                .getEventBroker();
        final DirectConceptInterpreter interpreter = new DirectConceptInterpreter();
        final ConceptInterpretationContext interpretationContext = new ConceptInterpretationContext(
                new DiagramHistory(), canvasEventBroker);
        diagramView.setConceptInterpreter(interpreter);
        diagramView.setConceptInterpretationContext(interpretationContext);
        diagramView.setGrid(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE);
        diagramView.setGridEnabled(false);
        new LabelDragEventHandler(canvasEventBroker);
        new LabelClickEventHandler(canvasEventBroker);
        new LabelScrollEventHandler(canvasEventBroker);
        diagramView.setBorder(BorderFactory
                .createBevelBorder(BevelBorder.LOWERED));
        diagramView.setUndoManager(new ExtendedUndoManager());

        final JPanel toolPanel = new JPanel(new GridBagLayout());
        toolPanel.add(createMovementManipulators(), new GridBagConstraints(0,
                0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, new Insets(1, 5, 1, 1), 2, 2));
        toolPanel.add(createGridPanel(), new GridBagConstraints(1, 0, 1, 1, 0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));
        toolPanel.add(createZoomPanel(), new GridBagConstraints(2, 0, 1, 1, 0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));

        toolPanel.add(createEditPanel(), new GridBagConstraints(3, 0, 1, 1, 0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));
        toolPanel.add(new JPanel(), new GridBagConstraints(4, 0, 1, 1, 1, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));
        diagramViewPanel.add(toolPanel, BorderLayout.NORTH);
        diagramViewPanel.add(diagramView, BorderLayout.CENTER);
        loadConfigurationSettings();
        return diagramViewPanel;
    }

    protected JPanel createEditPanel() {
        final Component component = this;
        final JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit"));
        editContextButton = new JButton("Context...");
        editContextButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    editContext();
                } catch (final Throwable t) {
                    ErrorDialog.showError(component, t,
                            "Context editing failed");
                }
            }
        });
        editContextButton.setEnabled(false);
        editPanel.add(editContextButton, new GridBagConstraints(0, 0, 1, 1, 0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        editDiagramDescButton = new JButton("Description...");
        editDiagramDescButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                editDiagramDescription();
            }
        });
        editDiagramDescButton.setEnabled(false);
        editPanel.add(editDiagramDescButton, new GridBagConstraints(1, 0, 1, 1,
                0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        return editPanel;
    }

    protected JPanel createGridPanel() {
        final JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBorder(BorderFactory.createTitledBorder("Grid"));
        gridEnabledCheckBox = new JCheckBox("use");
        gridEnabledCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean enabled = gridEnabledCheckBox.isSelected();
                gridIncreaseButton.setEnabled(enabled);
                gridDecreaseButton.setEnabled(enabled);
                diagramView.setGridEnabled(enabled);
            }
        });
        gridEnabledCheckBox.setSelected(false);
        gridEnabledCheckBox.setEnabled(false);
        gridPanel.add(gridEnabledCheckBox, new GridBagConstraints(0, 0, 1, 1,
                0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        gridIncreaseButton = new JButton("+");
        gridIncreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                changeGridSize(GRID_SIZE_CHANGE_FACTOR);
            }
        });
        gridIncreaseButton.setEnabled(false);
        gridPanel.add(gridIncreaseButton, new GridBagConstraints(1, 0, 1, 1, 0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        gridDecreaseButton = new JButton("-");
        gridDecreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                changeGridSize(1 / GRID_SIZE_CHANGE_FACTOR);
            }
        });
        gridDecreaseButton.setEnabled(false);
        gridPanel.add(gridDecreaseButton, new GridBagConstraints(2, 0, 1, 1, 0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        return gridPanel;
    }

    private void changeGridSize(final double factor) {
        final double width = diagramView.getGridCellWidth();
        final double height = diagramView.getGridCellHeight();
        diagramView.setGrid(width * factor, height * factor);
    }

    protected JPanel createZoomPanel() {
        final JPanel zoomPanel = new JPanel(new GridBagLayout());
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Rescale"));
        zoomInButton = new JButton("+");
        zoomInButton.setEnabled(false);
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                zoomIntoDiagram();
            }
        });
        zoomPanel.add(zoomInButton, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        zoomOutButton = new JButton("-");
        zoomOutButton.setEnabled(false);
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                zoomOutOfDiagram();
            }
        });
        zoomPanel.add(zoomOutButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        return zoomPanel;
    }

    protected JPanel createMovementManipulators() {
        final JPanel movementPanel = new JPanel(new GridBagLayout());
        movementPanel.setBorder(BorderFactory.createTitledBorder("Movement"));
        movementChooser = new JComboBox();
        setMovementManipulators();
        movementPanel.add(movementChooser, new GridBagConstraints(0, 0, 1, 1,
                0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 2, 2));
        movementChooser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox combobox = (JComboBox) e.getSource();
                final int selection = combobox.getSelectedIndex();
                setManipulator(NODE_MANIPULATORS[selection]);
            }
        });
        return movementPanel;
    }

    protected void setMovementManipulators() {
        final Diagram2D diagram = this.diagramView.getDiagram();
        if (diagram == null) {
            movementChooser.setEnabled(false);
            movementChooser.setPreferredSize(new Dimension(80, 25));
        } else {
            movementChooser.setEnabled(true);
            if (movementChooser.getModel().getSize() != FULL_MOVEMENT_OPTION_NAMES.length) {
                movementChooser.setModel(new DefaultComboBoxModel(
                        FULL_MOVEMENT_OPTION_NAMES));
                setManipulator(NODE_MANIPULATORS[0]);
            }
        }
    }

    protected void setManipulator(final EventBrokerListener manipulator) {
        final EventBroker canvasEventBroker = diagramView.getController()
                .getEventBroker();
        for (final EventBrokerListener listener : NODE_MANIPULATORS) {
            canvasEventBroker.removeSubscriptions(listener);
        }
        canvasEventBroker.subscribe(manipulator, CanvasItemDraggedEvent.class,
                NodeView.class);
    }

    private void zoomIntoDiagram() {
        changeZoom(1 / ZOOM_FACTOR);
    }

    private void zoomOutOfDiagram() {
        changeZoom(ZOOM_FACTOR);
    }

    private void changeZoom(final double zoomFactor) {
        final Diagram2D diagram = this.diagramView.getDiagram();
        final Iterator<DiagramNode> nodesIt = diagram.getNodes();
        while (nodesIt.hasNext()) {
            final DiagramNode diagramNode = nodesIt.next();
            final Point2D pos = diagramNode.getPosition();
            diagramNode.setPosition(new Point2D.Double(pos.getX() * zoomFactor,
                    pos.getY() * zoomFactor));
        }
        changeGridSize(zoomFactor);
        this.diagramView.requestScreenTransformUpdate();
        this.diagramView.repaint();
    }

    protected void editContext() {
        ContextImplementation context = (ContextImplementation) DiagramToContextConverter
                .getContext(this.diagramView.getDiagram());
        contextEditingDialog.setContext(context);
        if (contextEditingDialog.execute()) {
            final Map<Object, Point2D> attributeVectors = findVectorsForAttributes(this.diagramView
                    .getDiagram());
            final LatticeGenerator lgen = new GantersAlgorithm();
            context = contextEditingDialog.getContext();
            final Lattice lattice = lgen.createLattice(context);
            String contextName = context.getName();
            boolean ok;
            do {
                ok = true;
                final Diagram2D foundDiagram = this.conceptualSchema
                        .getDiagram(contextName);
                if (foundDiagram != null
                        && foundDiagram != this.diagramView.getDiagram()) {
                    ok = false;
                    final InputTextDialog inputDialog = new InputTextDialog(
                            this.parent, "Duplicate Diagram Title",
                            "diagram title", contextName, false);
                    contextName = inputDialog.getInput();
                }
            } while (!ok);

            final NDimDiagram diagram = NDimLayoutOperations.createDiagram(
                    lattice, contextName, new DefaultDimensionStrategy());
            assignOldVectors(diagram, attributeVectors);
            this.conceptualSchema.replaceDiagram(this.diagramView.getDiagram(),
                    diagram);
            this.diagramView.showDiagram(diagram);
        }
    }

    /**
     * Assigns the given vectors to the irreducible nodes having the attributes.
     * 
     * Caution: we assume that there are no dimensions that are assigned to
     * multiple irreducible nodes.
     * 
     * @param diagram
     *            the diagram to relayout
     * @param attributeVectors
     *            a map from the attribute data to vectors (Point2D)
     */
    private void assignOldVectors(final NDimDiagram diagram,
            final Map<Object, Point2D> attributeVectors) {
        final Vector<Point2D> base = diagram.getBase();
        for (final Iterator<DiagramNode> nodeIt = diagram.getNodes(); nodeIt
                .hasNext();) {
            final NDimDiagramNode node = (NDimDiagramNode) nodeIt.next();
            final Concept concept = node.getConcept();
            if (concept.isMeetIrreducible()) {
                final double[] ndimVec = node.getNdimVector();
                int dim = -1;
                for (final Iterator<DiagramLine> lineIt = diagram.getLines(); lineIt
                        .hasNext();) {
                    final DiagramLine line = lineIt.next();
                    if (line.getToNode() == node) {
                        final NDimDiagramNode parentNode = (NDimDiagramNode) line
                                .getFromNode();
                        final double[] parentVec = parentNode.getNdimVector();
                        for (int i = 0; i < parentVec.length; i++) {
                            if (parentVec[i] < ndimVec[i]) {
                                dim = i;
                                break;
                            }
                        }
                    }
                }
                assert (dim != -1);
                double projX = 0;
                double projY = 0;
                for (final Iterator attrIt = concept
                        .getAttributeContingentIterator(); attrIt.hasNext();) {
                    final FCAElement attribute = (FCAElement) attrIt.next();
                    final Point2D vector = attributeVectors.get(attribute
                            .getData());
                    if (vector != null) { // vector can be null if attribute was
                        // attached to top before
                        projX += vector.getX();
                        projY += vector.getY();
                    } else { // add some default vector
                        projX += 0;
                        projY += 50;
                    }
                }
                base.set(dim, new Point2D.Double(projX, projY));
            }
        }
    }

    private Map<Object, Point2D> findVectorsForAttributes(
            final Diagram2D diagram) {
        final Map<Object, Point2D> result = new Hashtable<Object, Point2D>();
        for (final Iterator<DiagramNode> nodeIt = diagram.getNodes(); nodeIt
                .hasNext();) {
            final DiagramNode node = nodeIt.next();
            final Concept concept = node.getConcept();
            if (concept.isMeetIrreducible()) {
                final Point2D nodePos = node.getPosition();
                Point2D parentPos = null;
                for (final Iterator<DiagramLine> lineIt = diagram.getLines(); lineIt
                        .hasNext();) {
                    final DiagramLine line = lineIt.next();
                    if (line.getToNode() == node) {
                        parentPos = line.getFromPosition();
                        break;
                    }
                }
                assert parentPos != null;
                final int contSize = concept.getAttributeContingentSize();
                final Point2D vector = new Point2D.Double(
                        (nodePos.getX() - parentPos.getX()) / contSize,
                        (nodePos.getY() - parentPos.getY()) / contSize);
                for (final Iterator attrIt = concept
                        .getAttributeContingentIterator(); attrIt.hasNext();) {
                    final FCAElement attribute = (FCAElement) attrIt.next();
                    result.put(attribute.getData(), vector);
                }
            }
        }
        return result;
    }

    protected void editDiagramDescription() {
        final WriteableDiagram2D currentDiagram = (WriteableDiagram2D) this.diagramView
                .getDiagram();
        if (currentDiagram != null) {
            final XMLEditorDialog diagramDescriptionEditor = new XMLEditorDialog(
                    null, "Diagram description editor");
            diagramDescriptionEditor
                    .setContent(currentDiagram.getDescription());
            diagramDescriptionEditor.setVisible(true);
            currentDiagram
                    .setDescription(diagramDescriptionEditor.getContent());
        }
    }

    protected LabeledPanel makeDiagramListView() {
        diagramListModel = new DefaultListModel();
        final JList listView = new JList(diagramListModel);
        final JButton upButton = new JButton("Up");
        final JButton downButton = new JButton("Down");
        final JButton removeButton = new JButton("Remove");
        final JButton duplicateButton = new JButton("Duplicate");
        final JPanel buttonsPane = new JPanel(new GridBagLayout());
        final JPanel upDownButtonPane = new JPanel(new GridBagLayout());
        upDownButtonPane.add(upButton, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));
        upDownButtonPane.add(downButton, new GridBagConstraints(1, 0, 1, 1,
                1.0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));

        buttonsPane.add(upDownButtonPane, new GridBagConstraints(0, 0, 1, 1,
                1.0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 2, 2));
        buttonsPane.add(removeButton, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1), 2, 2));
        buttonsPane.add(duplicateButton, new GridBagConstraints(0, 2, 1, 1,
                1.0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 2, 2));

        upButton.setEnabled(false);
        downButton.setEnabled(false);
        removeButton.setEnabled(false);
        duplicateButton.setEnabled(false);

        listView.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fillDiagramListView();
        listView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (listView.getSelectedIndex() == 0) {
                        upButton.setEnabled(false);
                        downButton
                                .setEnabled(listView.getSelectedIndex() != -1);
                    } else if (listView.getSelectedIndex() + 1 == listView
                            .getModel().getSize()) {
                        downButton.setEnabled(false);
                        upButton.setEnabled(listView.getSelectedIndex() != -1);
                    } else {
                        upButton.setEnabled(listView.getSelectedIndex() != -1);
                        downButton
                                .setEnabled(listView.getSelectedIndex() != -1);
                    }
                    removeButton.setEnabled(listView.getSelectedIndex() != -1);
                    duplicateButton
                            .setEnabled(listView.getSelectedIndex() != -1);
                    final int[] selections = listView.getSelectedIndices();
                    if (selections.length == 1) {
                        diagramView.showDiagram(conceptualSchema
                                .getDiagram(selections[0]));
                    } else {
                        diagramView.showDiagram(null);
                    }
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int[] selections = listView.getSelectedIndices();
                for (int i = selections.length - 1; i >= 0; i--) {
                    final int selection = selections[i];
                    conceptualSchema.removeDiagram(selection);
                }
            }
        });

        duplicateButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int index = listView.getSelectedIndex();
                if (index != -1) {
                    final Diagram2D diagram = conceptualSchema
                            .getDiagram(index);
                    final Diagram2D copiedDiagram = copyDiagram(diagram);
                    if (copiedDiagram != null) {
                        conceptualSchema.addDiagram(copiedDiagram);
                        final int pos = diagramListModel.indexOf(copiedDiagram
                                .getTitle());
                        listView.setSelectedIndex(pos);
                    }
                }
            }
        });

        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int index = listView.getSelectedIndex();
                if (index != -1) {
                    conceptualSchema.exchangeDiagrams(index, index - 1);
                    listView.setSelectedIndex(index - 1);
                }
            }
        });

        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int index = listView.getSelectedIndex();
                if (index != -1) {
                    conceptualSchema.exchangeDiagrams(index, index + 1);
                    listView.setSelectedIndex(index + 1);
                }
            }
        });

        // add context menu
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    openPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    openPopupMenu(e);
                }
            }

            private void openPopupMenu(final MouseEvent e) {
                final int indexHit = listView.locationToIndex(e.getPoint());
                if (indexHit < 0) {
                    return; // no item hit
                }

                final Object itemHit = listView.getModel().getElementAt(
                        indexHit);
                final JPopupMenu popupMenu = new JPopupMenu();

                final JMenuItem duplicateItem = new JMenuItem("Duplicate '"
                        + itemHit + "'");
                duplicateItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent ae) {
                        final Diagram2D diagram = conceptualSchema
                                .getDiagram(indexHit);
                        final Diagram2D copiedDiagram = copyDiagram(diagram);
                        if (copiedDiagram != null) {
                            conceptualSchema.addDiagram(copiedDiagram);
                            final int pos = diagramListModel
                                    .indexOf(copiedDiagram.getTitle());
                            listView.setSelectedIndex(pos);
                        }
                    }
                });
                popupMenu.add(duplicateItem);
                final JMenuItem removeItem = new JMenuItem("Remove '" + itemHit
                        + "'");
                removeItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent ae) {
                        conceptualSchema.removeDiagram(indexHit);
                    }
                });
                popupMenu.add(removeItem);

                if (DiagramEditingView.this.extraContextMenuActions != null) {
                    popupMenu.addSeparator();
                    for (final DiagramAction action : DiagramEditingView.this.extraContextMenuActions) {
                        popupMenu.add(new AbstractAction() {
                            public void actionPerformed(final ActionEvent ae) {
                                final Diagram2D diagram = conceptualSchema
                                        .getDiagram(indexHit);
                                action.actionPerformed(ae, diagram);
                            }

                            @Override
                            public Object getValue(final String key) {
                                if (Action.NAME == key) {
                                    return action.getLabel();
                                }
                                return super.getValue(key);
                            }

                            @Override
                            public boolean isEnabled() {
                                return action.isEnabled();
                            }
                        });
                    }
                }

                popupMenu.show(listView, e.getX(), e.getY());
            }
        });
        return new LabeledPanel("Diagrams:", listView, buttonsPane);
    }

    protected void fillDiagramListView() {
        diagramListModel.clear();
        for (int i = 0; i < conceptualSchema.getNumberOfDiagrams(); i++) {
            final SimpleLineDiagram diagram = (SimpleLineDiagram) conceptualSchema
                    .getDiagram(i);
            diagramListModel.addElement(diagram.getTitle());
        }
    }

    public void processEvent(final Event event) {
        if (event instanceof DisplayedDiagramChangedEvent) {
            updateToolbar();
            return;
        }
        if (event instanceof DatabaseConnectedEvent) {
            final DatabaseConnectedEvent dbConEv = (DatabaseConnectedEvent) event;
            this.databaseConnection = dbConEv.getConnection();
        }
        if (event instanceof ConceptualSchemaChangeEvent) {
            final ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
            if (event instanceof NewConceptualSchemaEvent) {
                conceptualSchema = changeEvent.getConceptualSchema();
            }
            fillDiagramListView();
            updateToolbar();
        }
    }

    private void updateToolbar() {
        final boolean diagramAvailable = this.diagramView.getDiagram() != null;
        final boolean diagramIsNested = this.diagramView.getDiagram() instanceof NestedLineDiagram;
        this.zoomInButton.setEnabled(diagramAvailable && !diagramIsNested);
        this.zoomOutButton.setEnabled(diagramAvailable && !diagramIsNested);
        this.gridIncreaseButton.setEnabled(diagramAvailable
                && this.gridEnabledCheckBox.isSelected());
        this.gridDecreaseButton.setEnabled(diagramAvailable
                && this.gridEnabledCheckBox.isSelected());
        this.gridEnabledCheckBox.setEnabled(diagramAvailable);
        this.editContextButton.setEnabled(diagramAvailable);
        this.editDiagramDescButton.setEnabled(diagramAvailable);
        if (movementChooser != null) {
            setMovementManipulators();
        }
    }

    public void setDividerLocation(final int location) {
        splitPane.setDividerLocation(location);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }

    public DiagramView getDiagramView() {
        return this.diagramView;
    }

    public void saveConfigurationSettings() {
        preferences
                .putBoolean("useGrid", this.gridEnabledCheckBox.isSelected());
        preferences.putDouble("gridCellHeight", this.diagramView
                .getGridCellHeight());
        preferences.putDouble("gridCellWidth", this.diagramView
                .getGridCellWidth());
    }

    public void loadConfigurationSettings() {
        final boolean use = preferences.getBoolean("useGrid", false);
        final double cellWidth = preferences.getDouble("gridCellWidth",
                DEFAULT_GRID_SIZE);
        final double cellHeight = preferences.getDouble("gridCellWidth",
                DEFAULT_GRID_SIZE);
        this.diagramView.setGrid(cellWidth, cellHeight);
        this.diagramView.setGridEnabled(use);
        this.gridEnabledCheckBox.setSelected(use);
        this.gridEnabledCheckBox.setEnabled(use);
    }

    /**
     * Assumptions: implementation used for Diagram2D is SimpleLineDiagram
     * implementation
     */
    private Diagram2D copyDiagram(final Diagram2D diagram) {

        // @todo error message is not helpfull here as user can't do
        // anything to rectify the situation, however, need
        // some kind of feedback in case someone forgets that we only
        // support SimpleLineDiagram copying
        if (!(diagram instanceof SimpleLineDiagram)) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "Sorry, don't know how to copy a diagram other then SimpleLineDiagram",
                            "Error copying diagram", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        String copiedDiagramTitle = createCopiedDiagramTitle(diagram.getTitle());
        Diagram2D foundDiagram = conceptualSchema
                .getDiagram(copiedDiagramTitle);
        while (foundDiagram != null) {
            copiedDiagramTitle = createCopiedDiagramTitle(foundDiagram
                    .getTitle());
            foundDiagram = conceptualSchema.getDiagram(copiedDiagramTitle);
        }

        final SimpleLineDiagram copy = new SimpleLineDiagram(diagram);
        copy.setTitle(copiedDiagramTitle);
        return copy;
    }

    private String createCopiedDiagramTitle(final String diagramTitle) {
        String copiedDiagramTitle = diagramTitle + " (" + 1 + ")";
        if (diagramTitle.lastIndexOf(")") > 0) {
            final int index1 = diagramTitle.lastIndexOf("(");
            final int index2 = diagramTitle.lastIndexOf(")");
            if ((index1 > 0) && (index2 > 0)) {
                final String numStr = diagramTitle
                        .substring(index1 + 1, index2);
                final String baseStr = diagramTitle.substring(0, index1).trim();
                try {
                    final Integer num = new Integer(numStr);
                    copiedDiagramTitle = baseStr + " (" + (num.intValue() + 1)
                            + ")";
                } catch (final NumberFormatException e) {
                    // ignore this exception because if whatever is in
                    // parenthesis
                    // is not a number - we don't want to increment it then.
                }
            }
        }
        return copiedDiagramTitle;
    }

    public void addAccessory(final Component accessory) {
        this.diagramListView.addExtraComponent(accessory);
    }

    public void setExtraContextMenuActions(
            final DiagramAction[] extraContextMenuActions) {
        this.extraContextMenuActions = extraContextMenuActions;
    }
}
