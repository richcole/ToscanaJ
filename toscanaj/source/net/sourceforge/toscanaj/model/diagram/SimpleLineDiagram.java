/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.events.DiagramChangedEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.canvas.CanvasItem;
import org.tockit.events.EventBroker;
import org.tockit.util.IdPool;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is an abstraction of all diagram related information.
 *
 * We assume that the first node we get is the top node of the diagram. The order
 * of all other nodes and the order of the lines does not matter.
 *
 * The coordinate system given can use y-coordinates either pointing upwards (the
 * usual mathematical system) or downwards (the usual computer coordinates). The
 * first call to getNode() or getBounds() will make sure that the coordinates
 * will be pointing downwards when reading.
 */
public class SimpleLineDiagram<O,A> implements WriteableDiagram2D<O,A> {
	protected EventBroker eventBroker;
	
    /**
     * The title used for this diagram.
     */
    private String title = new String();

    /**
     * The list of nodes in the diagram.
     */
    private List<DiagramNode<O,A>> nodes = new LinkedList<DiagramNode<O,A>>();

    /**
     * The list of lines in the diagram.
     */
    private List<DiagramLine<O,A>> lines = new LinkedList<DiagramLine<O,A>>();

    /**
     * This is set to true once we determined the direction of the y-axis.
     */
    private boolean coordinateSystemChecked;

    private Element description = null;
    
    private IdPool idPool = new IdPool();
    
    private static HashMap<String, ExtraCanvasItemFactory> extraCanvasItemFactories = new HashMap<String, ExtraCanvasItemFactory>();
    private List<CanvasItem> extraCanvasItems = new ArrayList<CanvasItem>();

    /**
     * The default constructor creates a diagram with just nothing in it at all.
     */
    public SimpleLineDiagram() {
        coordinateSystemChecked = false;
    }

    public SimpleLineDiagram(Element element) throws XMLSyntaxError {
        readXML(element);
    }
    
    /**
     * A copy constructor creating a duplicate of given diagram.
     * 
     * Makes a reasonably deep copy. Does not handle subclasses and at least
     * NDimDiagram/NDimDiagramNode are not yet supported.
     * @todo add support for copying n-dim diagrams.
     * 
     * Assumptions:
     * Assuming that ConceptImplementation is used for Concept interface and
     * that the objects and attributes are FCAObject(s) and Attribute(s).
     *  
	 * When making a copy of the diagram node, copying all properties, except:
	 * - identifiers. Assumption is that if we are copying diagrams and 
	 * identifiers are unique within each diagram - it should be ok to 
	 * keep the same identifiers for nodes in a copied diagram.
     */
    public SimpleLineDiagram(Diagram2D<O,A> diagram) {
		coordinateSystemChecked = false;
		
		Map<DiagramNode<O,A>, DiagramNode<O,A>> oldToNewNodeMapping = new Hashtable<DiagramNode<O,A>, DiagramNode<O,A>>();
    	
    	this.title = diagram.getTitle();
    	
    	Iterator<DiagramNode<O,A>> diagramNodes = diagram.getNodes();
    	while (diagramNodes.hasNext()) {
			DiagramNode<O,A> curNode = diagramNodes.next();
			// we are not using DiagramNode copy constructor here for copying 
			// nodes because it doesn't offer deep copy of a Concept, using 
			// makeDiagramNodeCopy() method instead.
			DiagramNode<O,A> copiedNode = makeDiagramNodeCopy(curNode);
			this.nodes.add(copiedNode);
			oldToNewNodeMapping.put(curNode, copiedNode);
		}
		
		Iterator<DiagramLine<O, A>> lines = diagram.getLines();
		while (lines.hasNext()) {
			DiagramLine<O,A> curLine = lines.next();
			DiagramNode<O,A> copiedFromNode = oldToNewNodeMapping.get(curLine.getFromNode());
			DiagramNode<O,A> copiedToNode = oldToNewNodeMapping.get(curLine.getToNode());
			DiagramLine<O,A> copiedLine = new DiagramLine<O,A>(copiedFromNode,copiedToNode, this);
			this.lines.add(copiedLine);
			ConceptImplementation<O,A> subConcept = (ConceptImplementation<O, A>) copiedToNode.getConcept();
			ConceptImplementation<O,A> superConcept = (ConceptImplementation<O, A>) copiedFromNode.getConcept();
			subConcept.addSuperConcept(superConcept);
			superConcept.addSubConcept(subConcept);
		}
		
		Iterator<DiagramNode<O,A>> it = this.getNodes();
		while (it.hasNext()) {
			DiagramNode<O,A> curNode = it.next();
			((ConceptImplementation<O,A>) curNode.getConcept()).buildClosures();
		}
    }

	/**
	 * Make a deep copy of the node, copying all properties, with 
	 * the following exception:
	 * - identifier: at the moment just copy the same identifier over.
	 * 
	 * @todo this code is specific for FCAElements in a way that is not typesafe
	 */
    @SuppressWarnings("unchecked")
	private DiagramNode<O,A> makeDiagramNodeCopy (DiagramNode<O,A> node) {   	

		Point2D position = (Point2D) node.getPosition().clone();
			
		Concept<O,A> originalNodeConcept = node.getConcept();
		ConceptImplementation<O,A> concept = new ConceptImplementation<O,A>();
		Iterator<A> attrIterator = originalNodeConcept.getAttributeContingentIterator();
		while (attrIterator.hasNext()) {
            FCAElement curAttr = (FCAElement) attrIterator.next();
			concept.addAttribute((A) new FCAElementImplementation(curAttr.getData(), curAttr.getDescription()));
		}
		Iterator<O> objIterator = originalNodeConcept.getObjectContingentIterator();
		while (objIterator.hasNext()) {
			FCAElement curObj = (FCAElement) objIterator.next();
			concept.addObject((O) new FCAElementImplementation(curObj.getData()));
		}
		
		LabelInfo attributeLabelInfo = new LabelInfo(node.getAttributeLabelInfo());
		LabelInfo objectLabelInfo = new LabelInfo(node.getObjectLabelInfo());

		String identifier = node.getIdentifier();
		String newIdentifier = identifier;
		try  {
			this.idPool.reserveId(identifier);
		}
		catch (IllegalArgumentException e) {
			newIdentifier = this.idPool.getFreeId();
		}
		DiagramNode<O,A> newNode = new DiagramNode<O,A>(this, newIdentifier, position, concept, attributeLabelInfo, objectLabelInfo, null);
		attributeLabelInfo.setNode(newNode);
		objectLabelInfo.setNode(newNode);
		return newNode;
    }

    public Element toXML() {
        Element retVal = new Element(DIAGRAM_ELEMENT_NAME);
        retVal.setAttribute(TITLE_ATTRIBUTE_NAME, title);
        if (description != null) {
            retVal.addContent(description.detach());
        }
        for (Iterator<DiagramNode<O,A>> iterator = nodes.iterator(); iterator.hasNext();) {
            DiagramNode<O,A> node = iterator.next();
            retVal.addContent(node.toXML());
        }
        for (Iterator<DiagramLine<O,A>> iterator = lines.iterator(); iterator.hasNext();) {
            DiagramLine<O,A> line = iterator.next();
            retVal.addContent(line.toXML());
        }
        if(!this.extraCanvasItems.isEmpty()) {
            Element extraItemElem = new Element(EXTRA_CANVAS_ITEMS_ELEMENT_NAME);
            for (Iterator<CanvasItem> iter = this.extraCanvasItems.iterator(); iter.hasNext();) {
                CanvasItem item = iter.next();
                if(item instanceof XMLizable) {
                    XMLizable xmlItem = (XMLizable) item;
                    extraItemElem.addContent(xmlItem.toXML());
                }
            }
            retVal.addContent(extraItemElem);
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
	public void readXML(Element elem) throws XMLSyntaxError {
        coordinateSystemChecked = true; // don't check while we still build the diagram
        XMLHelper.checkName(elem, DIAGRAM_ELEMENT_NAME);
        title = XMLHelper.getAttribute(elem, TITLE_ATTRIBUTE_NAME).getValue();
        description = elem.getChild(DESCRIPTION_ELEMENT_NAME);
        List<Element> nodeElems = elem.getChildren(DiagramNode.NODE_ELEMENT_NAME);
        for (Iterator<Element> iterator = nodeElems.iterator(); iterator.hasNext();) {
            Element diagramNode = iterator.next();
            DiagramNode<O,A> newNode = createNewDiagramNode(diagramNode);
            try {
            	this.idPool.reserveId(newNode.getIdentifier()); 
            }
            catch (IllegalArgumentException e) {
            	throw new XMLSyntaxError("Node identifier '" + newNode.getIdentifier() + "' is already used in the diagram '" + title + "'", e);
            }
            nodes.add(newNode);
        }
        List<Element> lineElems = elem.getChildren(DiagramLine.DIAGRAM_LINE_ELEMENT_NAME);
        for (Iterator<Element> iterator = lineElems.iterator(); iterator.hasNext();) {
            Element diagramLine = iterator.next();
            DiagramLine<O,A> line = new DiagramLine<O,A>(diagramLine, this);
            lines.add(line);
            DiagramNode<O,A> from = line.getFromNode();
            DiagramNode<O,A> to = line.getToNode();

            // add direct neighbours to concepts
            ConceptImplementation<O,A> concept1 =
                    (ConceptImplementation<O,A>) from.getConcept();
            ConceptImplementation<O,A> concept2 =
                    (ConceptImplementation<O,A>) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        // build transitive closures for each concept
        for (Iterator<DiagramNode<O,A>> iterator = nodes.iterator(); iterator.hasNext();) {
            DiagramNode<O,A> node = iterator.next();
            ((ConceptImplementation<O,A>) node.getConcept()).buildClosures();
        }
        coordinateSystemChecked = false;
        
        Element extraItemElem = elem.getChild(EXTRA_CANVAS_ITEMS_ELEMENT_NAME);
        if(extraItemElem != null) {
            List<Element> children = extraItemElem.getChildren();
            for (Iterator<Element> iter = children.iterator(); iter.hasNext(); ) {
                Element child = iter.next();
                ExtraCanvasItemFactory factory = extraCanvasItemFactories.get(child.getName());
                if (factory != null) {
	                this.extraCanvasItems.add(factory.createCanvasItem(this, child));
                }
            }
        }
    }

    protected DiagramNode<O,A> createNewDiagramNode(Element diagramNode) throws XMLSyntaxError {
        return new DiagramNode<O,A>(this, diagramNode);
    }

    /**
     * Returns the title of the diagram.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the diagram.
     */
    public void setTitle(String title) {
        this.title = title;
        sendChangeEvent();
    }

    /**
     * Returns the number of nodes in the diagram.
     */
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    public Iterator<DiagramNode<O,A>> getNodes() {
        return this.nodes.iterator();
    }

    public Iterator<DiagramLine<O,A>> getLines() {
        return this.lines.iterator();
    }

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines() {
        return this.lines.size();
    }

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds() {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (int i = 0; i < this.nodes.size(); i++) {
            DiagramNode<O,A> node = this.nodes.get(i);
            double x = node.getX();
            double y = node.getY();
            double rx = node.getRadiusX();
            double ry = node.getRadiusY();

            if (x - rx < minX) {
                minX = x - rx;
            }
            if (x + rx > maxX) {
                maxX = x + rx;
            }
            if (y - ry < minY) {
                minY = y - ry;
            }
            if (y + ry > maxY) {
                maxY = y + ry;
            }
        }
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Returns a node in the diagram.
     *
     * Numbers start with zero.
     */
    public DiagramNode<O,A> getNode(int nodeNumber) {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        return this.nodes.get(nodeNumber);
    }

    public DiagramNode<O,A> getNode(String identifier) {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        for (int i = 0; i < nodes.size(); i++) {
            DiagramNode<O,A> node = nodes.get(i);
            if (node.getIdentifier().equals(identifier)) {
                return node;
            }
        }
        throw new RuntimeException("No diagram node with id '" + identifier + "' found.");
    }

	public DiagramNode<O,A> getNodeForConcept(Concept<O,A> concept) {
		if (!coordinateSystemChecked) {
			checkCoordinateSystem();
		}
		for (Iterator<DiagramNode<O,A>> nodeIt = this.nodes.iterator(); nodeIt.hasNext();) {
            DiagramNode<O,A> node = nodeIt.next();
            if(node.getConcept() == concept) {
            	return node;
            }
        }
		return null;
	}

    /**
     * Implements Diagram2D.getLine(int).
     */
    public DiagramLine<O,A> getLine(int lineNumber) {
        return this.lines.get(lineNumber);
    }

    /**
     * Adds a node to the diagram (at the end of the list).
     *
     * The top node of a diagram always has to be added first.
     */
    public void addNode(DiagramNode<O,A> node) {
        this.nodes.add(node);
		sendChangeEvent();
    }

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPosition(int lineNumber) {
        DiagramLine<O,A> line = this.lines.get(lineNumber);
        return line.getFromPosition();
    }

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPosition(int lineNumber) {
        DiagramLine<O,A> line = this.lines.get(lineNumber);
        return line.getToPosition();
    }

    /**
     * Adds a line to the diagram (at the end of the list).
     */
    public void addLine(DiagramNode<O,A> from, DiagramNode<O,A> to) {
        this.lines.add(new DiagramLine<O,A>(from, to, this));
        this.coordinateSystemChecked = false;
		sendChangeEvent();
    }

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel(int nodeNumber) {
        return this.nodes.get(nodeNumber).getObjectLabelInfo();
    }

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel(int nodeNumber) {
        return this.nodes.get(nodeNumber).getAttributeLabelInfo();
    }

    /**
     * Makes sure the y-coordinates increase in the downward direction.
     */
    protected void checkCoordinateSystem() {
        if (this.nodes.size() > 1) { // no point in checking direction otherwise
            DiagramNode<O,A> highestNode = this.nodes.get(0);
            for (Iterator<DiagramNode<O,A>> iterator = nodes.iterator(); iterator.hasNext();) {
                DiagramNode<O,A> node = iterator.next();
                if (highestNode.getY() > node.getY()) {
                    highestNode = node;
                }
            }
            if (!highestNode.getConcept().isTop()) {
                // inverse coordinates (mirror using x-axis)
                Iterator<DiagramNode<O,A>> it = this.nodes.iterator();
                while (it.hasNext()) {
                    DiagramNode<O,A> node = it.next();
                    node.invertY();
                }
            }
        }
        this.coordinateSystemChecked = true;
    }

	/**
	 * @todo in ConceptualSchema.setDescription(Element) we clone the parameter,
	 * here we don't --> check why
	 */
    public void setDescription(Element desc) {
    	if(this.description != desc) {
			sendChangeEvent();
    	}
        this.description = desc;
    }

	public void sendChangeEvent() {
		if(this.eventBroker != null) {
			this.eventBroker.processEvent(new DiagramChangedEvent(this, this));
		}
	}

	public Element getDescription() {
        return this.description;
    }
    
    public boolean isHasseDiagram() {
    	Iterator<DiagramLine<O,A>> it = this.lines.iterator();
    	while (it.hasNext()) {
            DiagramLine<O,A> line = it.next();
    	    double deltaX = Math.abs(line.getToPosition().getX() - line.getFromPosition().getX());
    	    double deltaY = line.getToPosition().getY() - line.getFromPosition().getY();
            if(deltaY < MINIMUM_STEEPNESS * deltaX) {
            	return false;
            }
        }
        return true;
    }

	public void setEventBroker(EventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}

	/**
	 * @todo check if we have a lattice
	 */
    public Concept<O,A> getTopConcept() {
    	if(this.nodes == null || this.nodes.size() == 0) {
    		throw new IllegalStateException("Diagram has no nodes");
    	}
    	DiagramNode<O,A> node = this.nodes.get(0);
		Concept<O,A> top = node.getConcept();
		while(top.getUpset().size() > 1) {
			Iterator<Concept<O,A>> it = top.getUpset().iterator();
			Concept<O,A> upper = it.next();
			if(upper != top) {
				top = upper;
			} else {
				top = it.next();
			}
		}
		return top;
    }

	/**
	 * @todo check if we have a lattice
	 */
    public Concept<O,A> getBottomConcept() {
		if(this.nodes == null || this.nodes.size() == 0) {
			throw new IllegalStateException("Diagram has no nodes");
		}
		DiagramNode<O,A> node = this.nodes.get(0);
		Concept<O,A> bottom = node.getConcept();
		while(bottom.getDownset().size() > 1) {
			Iterator<Concept<O, A>> it = bottom.getDownset().iterator();
			Concept<O,A> lower = it.next();
			if(lower != bottom) {
				bottom = lower;
			} else {
				bottom = it.next();
			}
		}
		return bottom;
    }

    public static void registerExtraCanvasItemFactory(String tagName, ExtraCanvasItemFactory factory) {
        extraCanvasItemFactories.put(tagName, factory);
    }
    
    public void addExtraCanvasItem(CanvasItem item) {
        this.extraCanvasItems.add(item);
    }
    
    public void removeExtraCanvasItems() {
        this.extraCanvasItems.clear();
    }
    
    public List<CanvasItem> getExtraCanvasItems() {
        return Collections.unmodifiableList(this.extraCanvasItems);
    }

	public EventBroker getEventBroker() {
		return this.eventBroker;
	}
}
