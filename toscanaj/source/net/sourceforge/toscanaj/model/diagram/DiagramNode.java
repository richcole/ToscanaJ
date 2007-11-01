/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.util.CollectionFactory;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

/**
 * Stores the information on a node in a diagram.
 *
 * This is mainly the position, the concept for the node and the information
 * on the labels attached to it.
 */
public class DiagramNode implements XMLizable {
    /**
     * The size of nodes.
     *
     * This is currently a fixed value common for all nodes, but the access is
     * already done using a method on each instance, thus allowing easy extension
     * later.
     * 
     * @todo this value should probably be one as default
     */
    private static final int DEFAULT_RADIUS = 10;
    
    private double radiusX = DEFAULT_RADIUS;
    private double radiusY = DEFAULT_RADIUS;

    private String identifier;

    /**
     * The concept the node represents.
     *
     * If this is set to null the node points to a not realised concept in the
     * diagram, i.e. the concept theoretically could exist but is not supported
     * by the current set of data.
     */
    protected Concept concept = null;

    /**
     * The position of the node.
     */
    protected Point2D position = null;

    /**
     * The layout information for the attribute label.
     */
    protected LabelInfo attributeLabelInfo;

    /**
     * The layout information for the attribute label.
     */
    protected LabelInfo objectLabelInfo;

    protected DiagramNode outerNode;
    public static final String NODE_ELEMENT_NAME = "node";
    public static final String POSITION_ELEMENT_NAME = "position";
    public static final String POSITION_X_ATTRIBUTE_NAME = "x";
    public static final String POSITION_Y_ATTRIBUTE_NAME = "y";
    public static final String ID_ATTRIBUTE_NAME = "id";
    public static final String ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME = "attributeLabelStyle";
    public static final String OBJECT_LABEL_STYLE_ELEMENT_NAME = "objectLabelStyle";

    protected WriteableDiagram2D diagram;

	public DiagramNode(WriteableDiagram2D diagram, String identifier, Point2D position, Concept concept,
                       LabelInfo attributeLabelInfo, LabelInfo objectLabelInfo,
                       DiagramNode outerNode) {
        this.diagram = diagram;
        this.identifier = identifier;
        this.position = (Point2D) position.clone();
        this.concept = concept;
        setAttributeLabelInfo(attributeLabelInfo);
        setObjectLabelInfo(objectLabelInfo);
        this.outerNode = outerNode;
    }
    
    public DiagramNode(WriteableDiagram2D diagram, String identifier, Point2D position, Concept concept,
                           LabelInfo attributeLabel, LabelInfo objectLabel,
                           DiagramNode outerNode, double radiusX, double radiusY) {
        this(diagram, identifier, position, concept, attributeLabel, objectLabel, outerNode);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
    }

    public DiagramNode(WriteableDiagram2D diagram, Element element) throws XMLSyntaxError {
		this.diagram = diagram;
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(NODE_ELEMENT_NAME);
        retVal.setAttribute(ID_ATTRIBUTE_NAME, identifier);
        Element positionElem = new Element(POSITION_ELEMENT_NAME);
        positionElem.setAttribute(POSITION_X_ATTRIBUTE_NAME, String.valueOf(getPosition().getX()));
        positionElem.setAttribute(POSITION_Y_ATTRIBUTE_NAME, String.valueOf(getPosition().getY()));
        retVal.addContent(positionElem);
        if (attributeLabelInfo != null) {
            Element attrLabelInfoElem = attributeLabelInfo.toXML();
            attrLabelInfoElem.setName(ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME);
            retVal.addContent(attrLabelInfoElem);
        }
        if (objectLabelInfo != null) {
            Element objectLabelInfoElem = objectLabelInfo.toXML();
            objectLabelInfoElem.setName(OBJECT_LABEL_STYLE_ELEMENT_NAME);
            retVal.addContent(objectLabelInfoElem);
        }
        retVal.addContent(concept.toXML());
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, NODE_ELEMENT_NAME);
        identifier = XMLHelper.getAttribute(elem, ID_ATTRIBUTE_NAME).getValue();
        Element positionElem = XMLHelper.getMandatoryChild(elem, POSITION_ELEMENT_NAME);
        position = new Point2D.Double(
                XMLHelper.getDoubleAttribute(positionElem, POSITION_X_ATTRIBUTE_NAME),
                XMLHelper.getDoubleAttribute(positionElem, POSITION_Y_ATTRIBUTE_NAME)
        );
        if (XMLHelper.contains(elem, ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME)) {
            attributeLabelInfo = new LabelInfo(elem.getChild(ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME));
        } else {
        	attributeLabelInfo = new LabelInfo();
        }
        attributeLabelInfo.setNode(this);

        if (XMLHelper.contains(elem, OBJECT_LABEL_STYLE_ELEMENT_NAME)) {
            objectLabelInfo = new LabelInfo(elem.getChild(OBJECT_LABEL_STYLE_ELEMENT_NAME));
        } else {
            objectLabelInfo = new LabelInfo();
        }
        objectLabelInfo.setNode(this);

        concept = new ConceptImplementation(
                XMLHelper.getMandatoryChild(elem, ConceptImplementation.CONCEPT_ELEMENT_NAME)
        );
    }

    public String getIdentifier() {
        return identifier;
    }

    public DiagramNode getOuterNode() {
        return outerNode;
    }

    /**
     * Returns the concept which should be used for filtering.
     *
     * This is not the concept of this node if the node is nested into
     * another node. In that case we use the concept of the outermost
     * node.
     */
    public Concept getFilterConcept() {
        if (this.outerNode == null) {
            return this.concept;
        } else {
            return this.outerNode.getFilterConcept();
        }
    }

    /**
     * A copy constructor creating a duplicate of the given node.
     *
     * This is a deep copy for position and labels but refers to the same concept.
     */
    public DiagramNode(DiagramNode other) {
        this.position = (Point2D) other.position.clone();
        this.concept = other.concept;
        if (this.attributeLabelInfo != null) {
            this.attributeLabelInfo = new LabelInfo(other.attributeLabelInfo);
            this.attributeLabelInfo.setNode(this);
        } else {
            this.attributeLabelInfo = null;
        }
        if (this.objectLabelInfo != null) {
            this.objectLabelInfo = new LabelInfo(other.objectLabelInfo);
            this.objectLabelInfo.setNode(this);
        } else {
            this.objectLabelInfo = null;
        }
    }

    /**
     * Get the current node position.
     */
    public Point2D getPosition() {
    	// return copy to make sure updates go through the event broker if required
        return (Point2D) position.clone();
    }

    /**
     * Set the node position in the model space.
     * 
     * This is just a convenience method calling setPosition(double, double).
     * Do not override to ensure behaviour is consistent.
     */
    public final void setPosition(Point2D position) {
        setPosition(position.getX(), position.getY());
    }

    /**
     * Set the node position in the model space.
     */
    public void setPosition(double x, double y) {
        this.position.setLocation(x,y);
        this.diagram.sendChangeEvent();
    }

    /**
     * Get the concept for this node.
     */
    public Concept getConcept() {
        return concept;
    }

    /**
     * Get the x coordinate in the model space.
     */
    public double getX() {
        return getPosition().getX();
    }

    /**
     * Get the y coordinate in the model space.
     */
    public double getY() {
        return getPosition().getY();
    }

    /**
     * Get the horizontal radius used for this node.
     */
    public double getRadiusX() {
        if (null == concept) {
            return 0;
        }
        return radiusX;
    }

    /**
     * Get the vertical radius used for this node.
     */
    public double getRadiusY() {
        if (null == concept) {
            return 0;
        }
        return radiusY;
    }

    /**
     * Returns the layout information for the attribute label.
     *
     * This might be null if there is no label attached.
     */
    public LabelInfo getAttributeLabelInfo() {
        return this.attributeLabelInfo;
    }

    /**
     * Returns the layout information for the object label.
     *
     * This might be null if there is no label attached.
     */
    public LabelInfo getObjectLabelInfo() {
        return this.objectLabelInfo;
    }

    /**
     * Sets the layout information for the attribute label attached.
     */
    public void setAttributeLabelInfo(LabelInfo labelInfo) {
        this.attributeLabelInfo = labelInfo;
        if (attributeLabelInfo != null) {
            attributeLabelInfo.setNode(this);
        }
		this.diagram.sendChangeEvent();
    }

    /**
     * Sets the layout information for the object label attached.
     */
    public void setObjectLabelInfo(LabelInfo labelInfo) {
        this.objectLabelInfo = labelInfo;
        if (objectLabelInfo != null) {
            objectLabelInfo.setNode(this);
        }
		this.diagram.sendChangeEvent();
    }

    /**
     * Inverts the y-coordinates of the node and the labels offsets.
     *
     * This is used when the diagram given has the y-axis pointing upwards.
     *
     * @see SimpleLineDiagram#checkCoordinateSystem()
     */
    public void invertY() {
        this.position.setLocation(this.position.getX(), -this.position.getY());
        if(this.attributeLabelInfo != null) {
	        this.attributeLabelInfo.setOffset(this.attributeLabelInfo.getOffset().getX(),
	                -this.attributeLabelInfo.getOffset().getY());
        }
        if(this.objectLabelInfo != null) {
	        this.objectLabelInfo.setOffset(this.objectLabelInfo.getOffset().getX(),
	                -this.objectLabelInfo.getOffset().getY());
        }
		this.diagram.sendChangeEvent();
    }

    /**
     * Debug output.
     */
    @Override
	public String toString() {
        String retVal = "DiagramNode:\n";
        retVal += "- Pos : (" + getX() + "," + getY() + ")\n";
        retVal += "- Size: (" + getRadiusX() + "," + getRadiusY() + ")\n";
        return retVal;
    }

    public static String getElementName() {
        return NODE_ELEMENT_NAME;
    }

    public Concept[] getConceptNestingList() {
        List<Object> conceptList = CollectionFactory.createDefaultList();
        DiagramNode node = this;
        while (node != null) {
            conceptList.add(node.getConcept());
            node = node.getOuterNode();
        }
        return conceptList.toArray(new Concept[0]);
    }

    @Override
	public boolean equals(Object obj) {
        if (!(obj instanceof DiagramNode)) {
            return false;
        }
        DiagramNode other = (DiagramNode) obj;
        // @todo a hack to avoid NPE here (after copying diagrams we 
        // get nodes without identifiers). Rethink the whole identifier thing
        if ( (this.getIdentifier() != null ) && (other.getIdentifier() != null)) {
			if (!this.getIdentifier().equals(other.getIdentifier())) {
				return false;
			}
        }
        if (!this.getPosition().equals(other.getPosition())) {
            return false;
        }

        if (this.getObjectLabelInfo() != null) {
            if (!this.getObjectLabelInfo().equals(other.getObjectLabelInfo())) {
                return false;
            }
        } else {
            if (other.getObjectLabelInfo() != null) {
                return false;
            }
        }

        if (this.getAttributeLabelInfo() != null) {
            if (!this.getAttributeLabelInfo().equals(other.getAttributeLabelInfo())) {
                return false;
            }
        } else {
            if (other.getAttributeLabelInfo() != null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns true iff the node connects to a line it does not belong to.
     */
    public boolean hasCollision() {
        Iterator<DiagramLine> lineIt = this.diagram.getLines();
        while (lineIt.hasNext()) {
            DiagramLine line = lineIt.next();
            if(line.getFromNode() == this) {
                continue;
            }
            if(line.getToNode() == this) {
                continue;
            }
            double avRadius = (this.radiusX + this.radiusY)/2;
            // call getPosition() instead of using the member since NDimDiagramNode
            // overrides the method
            if(line.calculateDistance(getPosition()) < avRadius) {
                return true;
            }
        }
        return false;
    }
}
