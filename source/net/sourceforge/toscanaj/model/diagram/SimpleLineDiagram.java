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
public class SimpleLineDiagram implements WriteableDiagram2D {
	protected EventBroker eventBroker;
	
    /**
     * The title used for this diagram.
     */
    private String title = new String();

    /**
     * The list of nodes in the diagram.
     */
    private List nodes = new LinkedList();

    /**
     * The list of lines in the diagram.
     */
    private List lines = new LinkedList();

    /**
     * This is set to true once we determined the direction of the y-axis.
     */
    private boolean coordinateSystemChecked;

    private Element description = null;
    
    private IdPool idPool = new IdPool();
    
    private static HashMap extraCanvasItemFactories = new HashMap();
    private List extraCanvasItems = new ArrayList();

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
    public SimpleLineDiagram (Diagram2D diagram) {
		coordinateSystemChecked = false;
		
		Hashtable oldToNewNodeMapping = new Hashtable();
    	
    	this.title = diagram.getTitle();
    	
    	Iterator diagramNodes = diagram.getNodes();
    	while (diagramNodes.hasNext()) {
			DiagramNode curNode = (DiagramNode) diagramNodes.next();
			// we are not using DiagramNode copy constructor here for copying 
			// nodes because it doesn't offer deep copy of a Concept, using 
			// makeDiagramNodeCopy() method instead.
			DiagramNode copiedNode = makeDiagramNodeCopy(curNode);
			this.nodes.add(copiedNode);
			oldToNewNodeMapping.put(curNode, copiedNode);
		}
		
		Iterator diagramLines = diagram.getLines();
		while (diagramLines.hasNext()) {
			DiagramLine curLine = (DiagramLine) diagramLines.next();
			DiagramNode copiedFromNode = (DiagramNode) oldToNewNodeMapping.get(curLine.getFromNode());
			DiagramNode copiedToNode = (DiagramNode) oldToNewNodeMapping.get(curLine.getToNode());
			DiagramLine copiedLine = new DiagramLine(copiedFromNode,copiedToNode, this);
			this.lines.add(copiedLine);
			ConceptImplementation subConcept = (ConceptImplementation) copiedToNode.getConcept();
			ConceptImplementation superConcept = (ConceptImplementation) copiedFromNode.getConcept();
			subConcept.addSuperConcept(superConcept);
			superConcept.addSubConcept(subConcept);
		}
		
		Iterator it = this.getNodes();
		while (it.hasNext()) {
			DiagramNode curNode = (DiagramNode) it.next();
			((ConceptImplementation) curNode.getConcept()).buildClosures();
		}
    }

	/**
	 * Make a deep copy of the node, copying all properties, with 
	 * the following exception:
	 * - identifier: at the moment just copy the same identifier over.
	 */
    private DiagramNode makeDiagramNodeCopy (DiagramNode node) {   	

		Point2D position = (Point2D) node.getPosition().clone();
			
		Concept originalNodeConcept = node.getConcept();
		ConceptImplementation concept = new ConceptImplementation();
		Iterator attrIterator = originalNodeConcept.getAttributeContingentIterator();
		while (attrIterator.hasNext()) {
            FCAElement curAttr = (FCAElement) attrIterator.next();
			concept.addAttribute(new FCAElementImplementation(curAttr.getData(), curAttr.getDescription()));
		}
		Iterator objIterator = originalNodeConcept.getObjectContingentIterator();
		while (objIterator.hasNext()) {
			FCAElement curObj = (FCAElement) objIterator.next();
			concept.addObject(new FCAElementImplementation(curObj.getData()));
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
		DiagramNode newNode = new DiagramNode(this, newIdentifier, position, concept, attributeLabelInfo, objectLabelInfo, null);
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
        for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
            DiagramNode node = (DiagramNode) iterator.next();
            retVal.addContent(node.toXML());
        }
        for (Iterator iterator = lines.iterator(); iterator.hasNext();) {
            DiagramLine line = (DiagramLine) iterator.next();
            retVal.addContent(line.toXML());
        }
        if(!this.extraCanvasItems.isEmpty()) {
            Element extraItemElem = new Element(EXTRA_CANVAS_ITEMS_ELEMENT_NAME);
            for (Iterator iter = this.extraCanvasItems.iterator(); iter.hasNext();) {
                CanvasItem item = (CanvasItem) iter.next();
                if(item instanceof XMLizable) {
                    XMLizable xmlItem = (XMLizable) item;
                    extraItemElem.addContent(xmlItem.toXML());
                }
            }
            retVal.addContent(extraItemElem);
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        coordinateSystemChecked = true; // don't check while we still build the diagram
        XMLHelper.checkName(elem, DIAGRAM_ELEMENT_NAME);
        title = XMLHelper.getAttribute(elem, TITLE_ATTRIBUTE_NAME).getValue();
        description = elem.getChild(DESCRIPTION_ELEMENT_NAME);
        List nodeElems = elem.getChildren(DiagramNode.NODE_ELEMENT_NAME);
        for (Iterator iterator = nodeElems.iterator(); iterator.hasNext();) {
            Element diagramNode = (Element) iterator.next();
            DiagramNode newNode = createNewDiagramNode(diagramNode);
            try {
            	this.idPool.reserveId(newNode.getIdentifier()); 
            }
            catch (IllegalArgumentException e) {
            	throw new XMLSyntaxError("Node identifier '" + newNode.getIdentifier() + "' is already used in the diagram '" + title + "'", e);
            }
            nodes.add(newNode);
        }
        List lineElems = elem.getChildren(DiagramLine.DIAGRAM_LINE_ELEMENT_NAME);
        for (Iterator iterator = lineElems.iterator(); iterator.hasNext();) {
            Element diagramLine = (Element) iterator.next();
            DiagramLine line = new DiagramLine(diagramLine, this);
            lines.add(line);
            DiagramNode from = line.getFromNode();
            DiagramNode to = line.getToNode();

            // add direct neighbours to concepts
            ConceptImplementation concept1 =
                    (ConceptImplementation) from.getConcept();
            ConceptImplementation concept2 =
                    (ConceptImplementation) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        // build transitive closures for each concept
        for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
            DiagramNode node = (DiagramNode) iterator.next();
            ((ConceptImplementation) node.getConcept()).buildClosures();
        }
        coordinateSystemChecked = false;
        
        Element extraItemElem = elem.getChild(EXTRA_CANVAS_ITEMS_ELEMENT_NAME);
        if(extraItemElem != null) {
            List children = extraItemElem.getChildren();
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                Element child = (Element) iter.next();
                ExtraCanvasItemFactory factory = (ExtraCanvasItemFactory) extraCanvasItemFactories.get(child.getName());
                if (factory != null) {
	                this.extraCanvasItems.add(factory.createCanvasItem(this, child));
                }
            }
        }
    }

    protected DiagramNode createNewDiagramNode(Element diagramNode) throws XMLSyntaxError {
        return new DiagramNode(this, diagramNode);
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

    public Iterator getNodes() {
        return this.nodes.iterator();
    }

    public Iterator getLines() {
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
            DiagramNode node = (DiagramNode) this.nodes.get(i);
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
    public DiagramNode getNode(int nodeNumber) {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        return (DiagramNode) this.nodes.get(nodeNumber);
    }

    public DiagramNode getNode(String identifier) {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        for (int i = 0; i < nodes.size(); i++) {
            DiagramNode node = (DiagramNode) nodes.get(i);
            if (node.getIdentifier().equals(identifier)) {
                return node;
            }
        }
        throw new RuntimeException("No diagram node with id '" + identifier + "' found.");
    }

	public DiagramNode getNodeForConcept(Concept concept) {
		if (!coordinateSystemChecked) {
			checkCoordinateSystem();
		}
		for (Iterator nodeIt = this.nodes.iterator(); nodeIt.hasNext();) {
            DiagramNode node = (DiagramNode) nodeIt.next();
            if(node.getConcept() == concept) {
            	return node;
            }
        }
		return null;
	}

    /**
     * Implements Diagram2D.getLine(int).
     */
    public DiagramLine getLine(int lineNumber) {
        return (DiagramLine) this.lines.get(lineNumber);
    }

    /**
     * Adds a node to the diagram (at the end of the list).
     *
     * The top node of a diagram always has to be added first.
     */
    public void addNode(DiagramNode node) {
        this.nodes.add(node);
		sendChangeEvent();
    }

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPosition(int lineNumber) {
        DiagramLine line = (DiagramLine) this.lines.get(lineNumber);
        return line.getFromPosition();
    }

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPosition(int lineNumber) {
        DiagramLine line = (DiagramLine) this.lines.get(lineNumber);
        return line.getToPosition();
    }

    /**
     * Adds a line to the diagram (at the end of the list).
     */
    public void addLine(DiagramNode from, DiagramNode to) {
        this.lines.add(new DiagramLine(from, to, this));
        this.coordinateSystemChecked = false;
		sendChangeEvent();
    }

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel(int nodeNumber) {
        return ((DiagramNode) this.nodes.get(nodeNumber)).getObjectLabelInfo();
    }

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel(int nodeNumber) {
        return ((DiagramNode) this.nodes.get(nodeNumber)).getAttributeLabelInfo();
    }

    /**
     * Makes sure the y-coordinates increase in the downward direction.
     */
    protected void checkCoordinateSystem() {
        if (this.nodes.size() > 1) { // no point in checking direction otherwise
            DiagramNode highestNode = (DiagramNode) this.nodes.get(0);
            for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
                DiagramNode node = (DiagramNode) iterator.next();
                if (highestNode.getY() > node.getY()) {
                    highestNode = node;
                }
            }
            if (!highestNode.getConcept().isTop()) {
                // inverse coordinates (mirror using x-axis)
                Iterator it = this.nodes.iterator();
                while (it.hasNext()) {
                    DiagramNode node = (DiagramNode) it.next();
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
    	Iterator it = this.lines.iterator();
    	while (it.hasNext()) {
            DiagramLine line = (DiagramLine) it.next();
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
    public Concept getTopConcept() {
    	if(this.nodes == null || this.nodes.size() == 0) {
    		throw new IllegalStateException("Diagram has no nodes");
    	}
    	DiagramNode node = (DiagramNode) this.nodes.get(0);
		Concept top = node.getConcept();
		while(top.getUpset().size() > 1) {
			Iterator it = top.getUpset().iterator();
			Concept upper = (Concept) it.next();
			if(upper != top) {
				top = upper;
			} else {
				top = (Concept) it.next();
			}
		}
		return top;
    }

	/**
	 * @todo check if we have a lattice
	 */
    public Concept getBottomConcept() {
		if(this.nodes == null || this.nodes.size() == 0) {
			throw new IllegalStateException("Diagram has no nodes");
		}
		DiagramNode node = (DiagramNode) this.nodes.get(0);
		Concept bottom = node.getConcept();
		while(bottom.getDownset().size() > 1) {
			Iterator it = bottom.getDownset().iterator();
			Concept lower = (Concept) it.next();
			if(lower != bottom) {
				bottom = lower;
			} else {
				bottom = (Concept) it.next();
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
    
    public List getExtraCanvasItems() {
        return Collections.unmodifiableList(this.extraCanvasItems);
    }
}
