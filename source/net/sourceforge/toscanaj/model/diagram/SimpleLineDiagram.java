/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.events.DiagramChangedEvent;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;
import org.tockit.events.EventBroker;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

    /**
     * The default constructor creates a diagram with just nothing in it at all.
     */
    public SimpleLineDiagram() {
        coordinateSystemChecked = false;
    }

    public SimpleLineDiagram(Element element) throws XMLSyntaxError {
        readXML(element);
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
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        coordinateSystemChecked = true; // don't check while we still build the diagram
        XMLHelper.checkName(DIAGRAM_ELEMENT_NAME, elem);
        title = XMLHelper.getAttribute(elem, TITLE_ATTRIBUTE_NAME).getValue();
        description = elem.getChild(DESCRIPTION_ELEMENT_NAME);
        List nodeElems = elem.getChildren(DiagramNode.NODE_ELEMENT_NAME);
        for (Iterator iterator = nodeElems.iterator(); iterator.hasNext();) {
            Element diagramNode = (Element) iterator.next();
            nodes.add(createNewDiagramNode(diagramNode));
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
}
