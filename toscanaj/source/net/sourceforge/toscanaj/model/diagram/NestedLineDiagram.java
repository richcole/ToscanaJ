/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.model.events.DiagramChangedEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;

/**
 * Stores a nested line diagram.
 *
 * The derivation from SimpleLineDiagram is just implementation, not design.
 *
 * @todo Create common abstract class for both types of diagrams.
 */
public class NestedLineDiagram extends SimpleLineDiagram {
    private final Diagram2D originalOuterDiagram;
	private final Diagram2D originalInnerDiagram;

	/**
     * Creates a new nested line diagram from the given two diagrams.
     *
     * The inner diagram will be nested in each node of the outer diagram.
     * The level gives the depth of nesting for this diagram, starting with one
     * for a diagram on top level.
     */
    public NestedLineDiagram(final Diagram2D outerDiagram, final Diagram2D innerDiagram) {
        this.originalOuterDiagram = outerDiagram;
		this.originalInnerDiagram = innerDiagram;
		calculateDiagram(outerDiagram, innerDiagram);
        if(outerDiagram.getEventBroker() != null) {
			outerDiagram.getEventBroker().subscribe(new EventBrokerListener(){
				public void processEvent(Event e) {
					updateOuterDiagram(outerDiagram, innerDiagram);
				}
	    	}, DiagramChangedEvent.class, Object.class);
        }
        if(innerDiagram.getEventBroker() != null) {
			innerDiagram.getEventBroker().subscribe(new EventBrokerListener(){
				public void processEvent(Event e) {
					updateInnerDiagrams(outerDiagram, (Diagram2D) e.getSubject());
				}
	    	}, DiagramChangedEvent.class, Object.class);
        }
    }

    private void calculateDiagram(Diagram2D outerDiagram, Diagram2D innerDiagram) {
		double scale = 1.2 * calculateNeededScaling(outerDiagram, innerDiagram);
        Hashtable nodeMap = new Hashtable();
        for (int i = 0; i < outerDiagram.getNumberOfNodes(); i++) {
            DiagramNode oldNode = outerDiagram.getNode(i);
            NestedDiagramNode node = new NestedDiagramNode(this, oldNode, innerDiagram, scale);
            this.addNode(node);
            nodeMap.put(oldNode, node);
        }
        for (int i = 0; i < outerDiagram.getNumberOfLines(); i++) {
            DiagramLine oldLine = outerDiagram.getLine(i);
            NestedDiagramNode from = (NestedDiagramNode) nodeMap.get(oldLine.getFromNode());
            NestedDiagramNode to = (NestedDiagramNode) nodeMap.get(oldLine.getToNode());
            this.addLine(from, to);
        }
	}

    private void updateOuterDiagram(Diagram2D outerDiagram, Diagram2D innerDiagram) {
        for (int i = 0; i < outerDiagram.getNumberOfNodes(); i++) {
            DiagramNode outerNode = outerDiagram.getNode(i);
            NestedDiagramNode node = (NestedDiagramNode) this.getNode(i);
            double outerScaleFactor = node.getOuterScaleFactor();
            node.setPosition(outerNode.getPosition().getX() * outerScaleFactor,
                             outerNode.getPosition().getY() * outerScaleFactor);
        }
	}

    private void updateInnerDiagrams(Diagram2D outerDiagram, Diagram2D innerDiagram) {
		double scale = 1.2 * calculateNeededScaling(outerDiagram, innerDiagram);
        for (int i = 0; i < getNumberOfNodes(); i++) {
            NestedDiagramNode node = (NestedDiagramNode) this.getNode(i);
            node.updateInnerDiagram(innerDiagram, scale);
        }
	}

	/**
     * Calculates the scaling we need to fit the inner nodes into the outer ones.
     *
     * The basic algorithm goes like this:
     * 1) minDist := minimal distance between two nodes in the outer diagram
     * 2) maxRadius := half of minDist (this radius would cause two nodes in
     *                 the outer diagram to just meet)
     * 3) possibleScaling := maxRadius/currentRadius (we can scale the radius of
     *                       the outer nodes by this without getting trouble)
     * 4) neededScaling := (innerSize/2)/currentRadius (this is what we need to fit
     *                     the inner diagram)
     * 5) additionalScaling := neededScaling/possibleScaling (we need to scale
     *                         that much more to fit the inner diagrams)
     *
     * The steps can be combined to:
     * 3) additionalScaling := innerSize/minDist
     *
     * This is of course oversimplified: we ignore the distinction between x and
     * y sizes, we ignore the distinction between ellipses and rectangles. The
     * code does more details but the idea is the same.
     * 
     * @todo handle different X and Y radii. 
     */
    protected double calculateNeededScaling(Diagram2D outerDiagram, Diagram2D innerDiagram) {
        // if we have only one node we just don't scale at all (we can make this node as
        // big as we want, if there is no node we shouldn't be called at all
        if (outerDiagram.getNumberOfNodes() < 2) {
            return 1;
        }
        // first find minimal distance between nodes in the outer diagram
        float minDist = Float.MAX_VALUE;
        // compare first n-1 nodes to all after them
        for (int i = 0; i < outerDiagram.getNumberOfNodes() - 1; i++) {
            for (int j = i + 1; j < outerDiagram.getNumberOfNodes(); j++) {
                DiagramNode node1 = outerDiagram.getNode(i);
                DiagramNode node2 = outerDiagram.getNode(j);
                float dist = (float) node1.getPosition().distance(node2.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }

        // now we know how big we could draw circles in the outer diagram
        // the scaling we still need relates to the size of the inner diagram
        Rectangle2D rect = innerDiagram.getBounds();
        if (rect.getWidth() > rect.getHeight()) {
            return (float) rect.getWidth() / minDist;
        } else {
            return (float) rect.getHeight() / minDist;
        }
    }
    
	public DiagramNode getNodeForConcept(Concept concept) {
		DiagramNode diagramNode = super.getNodeForConcept(concept);
		if (diagramNode != null) {
			return diagramNode;
		} else { // try inner diagram (via top node)
			return ((NestedDiagramNode)this.getNode(0)).getInnerDiagram().getNodeForConcept(concept);
		}
	}
    
    public String toString() {
    	return this.getTitle();
    }
    
    public Diagram2D getInnerDiagram() {
        return this.originalInnerDiagram;
    }

    public Diagram2D getOuterDiagram() {
        return this.originalOuterDiagram;
    }
    
    /**
     * Returns all nodes of all inner diagrams.
     */
    public Iterator getNodes() {
        /**
         * This class implements an iterator that iterates over all attribute
         * outer nodes and then through all nodes inside these.
         */
        class NodeIterator implements Iterator {
            /**
             * Stores the main iterator on the outer nodes.
             */
            Iterator mainIterator;

            /**
             * Stores the secondary iterator on the nodes inside the outer node.
             */
            Iterator secondaryIterator;

            /**
             * We start with the iterator of all outer nodes that we want to visit.
             */
            NodeIterator(Iterator main) {
                this.mainIterator = main;
                if (main.hasNext()) {
                    NestedDiagramNode first = (NestedDiagramNode) main.next();
                    this.secondaryIterator = first.getInnerDiagram().getNodes();
                } else {
                    this.secondaryIterator = null;
                }
            }

            public boolean hasNext() {
                if (this.secondaryIterator == null) {
                    return false;
                }
                while (!this.secondaryIterator.hasNext() && this.mainIterator.hasNext()) {
                    NestedDiagramNode next = (NestedDiagramNode) this.mainIterator.next();
                    this.secondaryIterator = next.getInnerDiagram().getNodes();
                }
                return this.secondaryIterator.hasNext();
            }

            public Object next() {
                if (this.secondaryIterator == null) {
                    throw new NoSuchElementException();
                }
                if (!this.secondaryIterator.hasNext() && !this.mainIterator.hasNext()) {
                    // we were already finished
                    throw new NoSuchElementException();
                }
                while (!this.secondaryIterator.hasNext() && this.mainIterator.hasNext()) {
                    NestedDiagramNode next = (NestedDiagramNode) this.mainIterator.next();
                    this.secondaryIterator = next.getInnerDiagram().getNodes();
                }
                return this.secondaryIterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        return new NodeIterator(super.getNodes());
    }
}