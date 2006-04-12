/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.toscanaj.controller.diagram.SelectionChangedEvent;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.events.EventBroker;
import org.tockit.swing.undo.ExtendedUndoManager;

/**
 * This class paints a diagram defined by the SimpleLineDiagram class.
 *
 * @todo get rid of ChangeObserver, use EventBroker.
 */
public class DiagramView extends Canvas implements ChangeObserver {
    /**
     * The diagram to display.
     */
    private Diagram2D diagram = null;

    private ConceptInterpreter conceptInterpreter;

    private ConceptInterpretationContext conceptInterpretationContext;
    
    private ExtendedUndoManager undoManager;
    
    /**
     * Currently we don't use selection.
     */
    static final public int NO_SELECTION = -1;
    /**
     * Node displays nothing selected
     */
    static final public int NOT_SELECTED = 0;
    /**
     * Node displays the currently selected concept.
     */
    static final public int SELECTED_DIRECTLY = 1;
    /**
     * Node displays a concept in the filter of the currently selected concept.
     */
    static final public int SELECTED_FILTER = 2;
    /**
     * Node displays a concept in the ideal of the currently selected concept.
     */
    static final public int SELECTED_IDEAL = 4;

    private DiagramSchema diagramSchema;
	
	private double minimumFontSize = 0;
	
    private boolean screenTransformDirty = false;

    private LabelView.LabelFactory attributeLabelFactory = AttributeLabelView.getFactory();
    private LabelView.LabelFactory objectLabelFactory = ObjectLabelView.getFactory();

    private class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            requestScreenTransformUpdate();
            repaint();
        }
    }

    /**
     * Creates a new view displaying an empty diagram (i.e.&#nbsp;nothing at all).
     */
    public DiagramView() {
        super(new EventBroker());
        this.conceptInterpreter = null;
        this.conceptInterpretationContext = null;
        this.diagramSchema = DiagramSchema.getCurrentSchema();
        addComponentListener(new ResizeListener());
        getBackgroundItem().setPaint(diagramSchema.getBackgroundColor());
    }

    public ConceptInterpreter getConceptInterpreter() {
        return conceptInterpreter;
    }

    public void setConceptInterpreter(ConceptInterpreter conceptInterpreter) {
        this.conceptInterpreter = conceptInterpreter;
        /// @todo update parts, redraw
    }

    public void setConceptInterpretationContext(ConceptInterpretationContext conceptInterpretationContext) {
        this.conceptInterpretationContext = conceptInterpretationContext;
        /// @todo update parts, redraw
    }

    public void setFilterMode(boolean filterMode) {
        Iterator it = this.getCanvasItemsByType(NodeView.class).iterator();
        while (it.hasNext()) {
            NodeView cur = (NodeView) it.next();
            cur.getConceptInterpretationContext().setFilterMode(filterMode);
        }
    }

    protected void dragFinished(MouseEvent e) {
        requestScreenTransformUpdate();
    }

    public void requestScreenTransformUpdate() {
        screenTransformDirty = true;
        repaint();
    }

    /**
     * Implements ChangeObserver.update(Object) by repainting the diagram.
     */
    public void update(Object source) {
        if (source instanceof DiagramController ||
                source instanceof DiagramHistory) {
            showDiagram(DiagramController.getController().getCurrentDiagram());
        } else {
            requestScreenTransformUpdate();
        }
    }
    
    public void updateDiagram() {
        showDiagram(this.diagram, false);
    }

    protected boolean isScreenTransformDirty() {
        return screenTransformDirty;
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform oldTransform = g2d.getTransform();

        if (diagram == null) {
            return;
        }

        int margin = diagramSchema.getMargin();
        if (screenTransformDirty) {
            // find current bounds
            Rectangle2D bounds = new Rectangle2D.Double(margin, margin,
                    getWidth() - 2 * margin, getHeight() - 2 * margin);
            this.setScreenTransform(this.scaleToFit(g2d, bounds));
            makeScreenTransformClear();
            setLabelFontSizes();
        }
        g2d.transform(getScreenTransform());

        // paint all items on canvas
        paintCanvas(g2d);
        // draw diagram title in the top left corner
        g2d.setPaint(diagramSchema.getForegroundColor());
        g2d.setTransform(oldTransform);
    }

    protected void makeScreenTransformClear() {
        screenTransformDirty = false;
    }

    /**
     * Sets the given diagram as new diagram to display.
     *
     * This will automatically cause a repaint of the view.
     */
    public void showDiagram(Diagram2D newDiagram) {
        showDiagram(newDiagram, true);
    }    
    
    private void showDiagram(Diagram2D newDiagram, boolean sendEvent) {
        this.diagram = newDiagram;
        removeSubscriptions();
        clearCanvas();
        /// @todo we should have different undo managers for each diagram, for now we just forget
        /// all edits when changing diagrams
        if(this.undoManager != null) {
            this.undoManager.discardAllEdits();
        }
        if (newDiagram == null) {
            repaint();
            if(sendEvent) {
                this.getController().getEventBroker().processEvent(new DisplayedDiagramChangedEvent(this));
            }
            return;
        }
        if (getParent()!=null) {
            getParent().setCursor(
                    Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        addLayer("lines-0");
        addLayer("nodes-0");
        addLayer("connectors-0");
        addLayer("lines-1");
        addLayer("nodes-1");
        addLayer("connectors-1");
        addLayer("labels");
        addLayer("extraItems");
        try{
        	addDiagram(newDiagram, conceptInterpretationContext, 0, true);
            requestScreenTransformUpdate();
            repaint();
            if(sendEvent) {
                this.getController().getEventBroker().processEvent(new DisplayedDiagramChangedEvent(this));
            }
        } catch (Exception e) {
        	ErrorDialog.showError(this, e, "Showing diagram failed", "The selected diagram can not be shown");
        	showDiagram(null);
        }
        if (getParent()!=null) {
            getParent().setCursor(Cursor.getDefaultCursor());
        }
    }

	private void removeSubscriptions() {
		for (Iterator iterator = this.getCanvasItemsByType(LabelView.class).iterator(); iterator.hasNext();) {
			LabelView lv = (LabelView) iterator.next();
			this.getController().getEventBroker().removeSubscriptions(lv);
		}
	}

	private void setLabelFontSizes() {
		Font font = diagramSchema.getLabelFont();
		double scale = this.getScreenTransform().getScaleY();
		if( (this.minimumFontSize > 0) && 
		     	(font.getSize() * scale < this.minimumFontSize)
		   ) {
		    font = font.deriveFont((float) (this.minimumFontSize / scale));
		}
		for (Iterator iterator = this.getCanvasItemsByType(LabelView.class).iterator(); iterator.hasNext();) {
			LabelView lv = (LabelView) iterator.next();
			lv.setFont(font);
		}
	}

    public Diagram2D getDiagram() {
        return diagram;
    }

    /**
     * Adds a line diagram to the canvas.
     *
     * If the filter concept is non-null all nodes created will use this for
     * filter operations.
     */
    private void addDiagram(Diagram2D newDiagram, ConceptInterpretationContext context, int layer, boolean showAttributeLabels) {
		String lineLayerName = "lines-" + layer;
        String nodeLayerName = "nodes-" + layer;
        String labelConnectorLayerName = "connectors-" + layer;
        String labelLayerName = "labels";
        Hashtable nodeMap = new Hashtable();
        for (int i = 0; i < newDiagram.getNumberOfNodes(); i++) {
            DiagramNode node = newDiagram.getNode(i);
            NodeView nodeView = new NodeView(node, this, context);
            nodeMap.put(node, nodeView);
            addCanvasItem(nodeView, nodeLayerName);
            Concept concept = node.getConcept();
            /// @todo calling isRealized(..) has the side effect of initialising the caches in the DB connected version -- find better way
            // if the caches are not initialized, the object contingent gradient will be wrong from time to time
            /// @todo make sure there always is a concept interpreter and an interpretation context. 
            ///       At the moment both are not initialized automatically
            if (conceptInterpreter.isRealized(concept, context)) {
                if (node instanceof NestedDiagramNode) {
		            NestedDiagramNode ndNode = (NestedDiagramNode) node;
		            boolean isTopRealizedConcept = true;
		            for (Iterator iter = concept.getUpset().iterator();iter.hasNext();) {
                        Concept superConcept = (Concept) iter.next();
                        if(superConcept != concept && this.conceptInterpreter.isRealized(superConcept, this.conceptInterpretationContext)) {
                        	isTopRealizedConcept = false;
                        	break;
                        }
                    }
                    addDiagram(ndNode.getInnerDiagram(), context.createNestedContext(concept), layer + 1, isTopRealizedConcept);
                }
            }
			LabelInfo objLabelInfo = newDiagram.getObjectLabel(i);
			if (objLabelInfo != null && this.objectLabelFactory != null) {
				LabelView labelView = this.objectLabelFactory.createLabelView(this, nodeView, objLabelInfo);
				addCanvasItem(labelView, labelLayerName);
				addCanvasItem(new LabelConnector(labelView), labelConnectorLayerName);
				labelView.addObserver(this);
			}
            if(showAttributeLabels) {
	            LabelInfo attrLabelInfo = newDiagram.getAttributeLabel(i);
	            if (attrLabelInfo != null) {
	                LabelView labelView = this.attributeLabelFactory.createLabelView(this, nodeView, attrLabelInfo);
	                addCanvasItem(labelView, labelLayerName);
	                addCanvasItem(new LabelConnector(labelView), labelConnectorLayerName);
	                labelView.addObserver(this);
	            }
			}
        }
        for (int i = 0; i < newDiagram.getNumberOfLines(); i++) {
            DiagramLine dl = newDiagram.getLine(i);
            addCanvasItem(new LineView(dl, (NodeView) nodeMap.get(dl.getFromNode()), (NodeView) nodeMap.get(dl.getToNode())), lineLayerName);
        }
        if(newDiagram instanceof SimpleLineDiagram) {
            SimpleLineDiagram sld = (SimpleLineDiagram) newDiagram;
            for (Iterator iter = sld.getExtraCanvasItems().iterator(); iter.hasNext();) {
                CanvasItem item = (CanvasItem) iter.next();
                addCanvasItem(item, "extraItems");
            }
        }
    }

    public void setDisplayType(boolean contingentOnly) {
        Iterator it = this.getCanvasItemsByType(NodeView.class).iterator();
        while (it.hasNext()) {
            NodeView nv = (NodeView) it.next();
            nv.getConceptInterpretationContext().setObjectDisplayMode(contingentOnly);
        }
        updateLabelEntries();
        requestScreenTransformUpdate();
        repaint();
    }

    public void updateLabelEntries() {
        Iterator it = this.getCanvasItemsByType(LabelView.class).iterator();
        while (it.hasNext()) {
            LabelView lv = (LabelView) it.next();
            lv.updateEntries();
        }
    }

    public void setSelectedConcepts(Concept[] concepts) {
        // notify all nodes and lines
        Iterator it = this.getCanvasItemsByType(NodeView.class).iterator();
        while (it.hasNext()) {
            NodeView nv = (NodeView) it.next();
            nv.setSelectedConcepts(concepts);
        }
        getController().getEventBroker().processEvent(new SelectionChangedEvent(this));
        repaint();
    }

    /**
     * Sets all object label views to use this query when asking their concepts.
     */
    public void setQuery(Query query) {
        // update the current labels
        Iterator it = this.getCanvasItemsByType(ObjectLabelView.class).iterator();
        while (it.hasNext()) {
            ObjectLabelView lv = (ObjectLabelView) it.next();
            lv.setQuery(query);
        }
        requestScreenTransformUpdate();
        repaint();
        // set the value for new ones
        ObjectLabelView.setDefaultQuery(query);
    }

    public DiagramSchema getDiagramSchema() {
        return diagramSchema;
    }
    
    public void setDiagramSchema(DiagramSchema schema) {
    	this.diagramSchema = schema;
        getBackgroundItem().setPaint(schema.getBackgroundColor());
    	this.repaint();
    }

	public double getMinimumFontSize() {
		return minimumFontSize;
	}

	public void setMinimumFontSize(double minimumFontSize) {
		this.minimumFontSize = minimumFontSize;
		this.screenTransformDirty = true;
		repaint();
	}
	
    public LabelView.LabelFactory getAttributeLabelFactory() {
        return attributeLabelFactory;
    }

    public LabelView.LabelFactory getObjectLabelFactory() {
        return objectLabelFactory;
    }

    public void setAttributeLabelFactory(
        LabelView.LabelFactory attributeLabelFactory) {
        this.attributeLabelFactory = attributeLabelFactory;
    }

	/**
	 * This changes the object labels created for new diagrams.
	 * 
	 * If a different label factory is given, this factory will be used to create
	 * the object labels. If null is given, no object labels will be used.
	 */
    public void setObjectLabelFactory(LabelView.LabelFactory objectLabelFactory) {
        this.objectLabelFactory = objectLabelFactory;
    }

    public ConceptInterpretationContext getConceptInterpretationContext() {
        return this.conceptInterpretationContext;
    }

    public ExtendedUndoManager getUndoManager() {
        /// @todo workaround to avoid problems that occur when undo is used in nested diagrams,
        /// remove once the problems are fixed.
        if(this.diagram instanceof NestedLineDiagram) {
            return null;
        }
		return undoManager;
	}
    
	public void setUndoManager(ExtendedUndoManager undoManager) {
		this.undoManager = undoManager;
	}
}
