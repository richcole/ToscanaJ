/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.util.xmlize.*;
import org.jdom.Element;
import util.CollectionFactory;

import java.awt.geom.Point2D;
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
     */
    private static final int RADIUS = 10;

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
    protected LabelInfo attributeLabel;

    /**
     * The layout information for the attribute label.
     */
    protected LabelInfo objectLabel;

    protected DiagramNode outerNode;
    public static final String NODE_ELEMENT_NAME = "node";
    public static final String POSITION_ELEMENT_NAME = "position";
    public static final String POSITION_X_ATTRIBUTE_NAME = "x";
    public static final String POSITION_Y_ATTRIBUTE_NAME = "y";
    public static final String ID_ATTRIBUTE_NAME = "id";
    public static final String ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME = "attributeLabelStyle";
    public static final String OBJECT_LABEL_STYLE_ELEMENT_NAME = "objectLabelStyle";

    public DiagramNode(String identifier, Point2D position, Concept concept,
                       LabelInfo attributeLabel, LabelInfo objectLabel,
                       DiagramNode outerNode) {
        this.identifier = identifier;
        this.position = position;
        this.concept = concept;
        this.attributeLabel = attributeLabel;
        if (attributeLabel != null) {
            attributeLabel.attachNode(this);
        }
        this.objectLabel = objectLabel;
        if (objectLabel != null) {
            objectLabel.attachNode(this);
        }
        this.outerNode = outerNode;
    }

    public DiagramNode(Element element) throws XMLSyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(NODE_ELEMENT_NAME);
        retVal.setAttribute(ID_ATTRIBUTE_NAME, identifier);
        Element positionElem = new Element(POSITION_ELEMENT_NAME);
        positionElem.setAttribute(POSITION_X_ATTRIBUTE_NAME, String.valueOf(position.getX()));
        positionElem.setAttribute(POSITION_Y_ATTRIBUTE_NAME, String.valueOf(position.getY()));
        retVal.addContent(positionElem);
        if (attributeLabel != null) {
            Element attrLabelInfoElem = attributeLabel.toXML();
            attrLabelInfoElem.setName(ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME);
            retVal.addContent(attrLabelInfoElem);
        }
        if (objectLabel != null) {
            Element objectLabelInfoElem = objectLabel.toXML();
            objectLabelInfoElem.setName(OBJECT_LABEL_STYLE_ELEMENT_NAME);
            retVal.addContent(objectLabelInfoElem);
        }
        retVal.addContent(concept.toXML());
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(NODE_ELEMENT_NAME, elem);
        identifier = XMLHelper.getAttribute(elem, ID_ATTRIBUTE_NAME).getValue();
        Element positionElem = XMLHelper.mustbe(POSITION_ELEMENT_NAME, elem);
        position = new Point2D.Double(
                XMLHelper.getDoubleAttribute(positionElem, POSITION_X_ATTRIBUTE_NAME),
                XMLHelper.getDoubleAttribute(positionElem, POSITION_Y_ATTRIBUTE_NAME)
        );
        if (XMLHelper.contains(elem, ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME)) {
            attributeLabel = new LabelInfo(elem.getChild(ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME));
        }
        if (XMLHelper.contains(elem, OBJECT_LABEL_STYLE_ELEMENT_NAME)) {
            objectLabel = new LabelInfo(elem.getChild(OBJECT_LABEL_STYLE_ELEMENT_NAME));
        }
        concept = new DatabaseConnectedConcept(
                XMLHelper.mustbe(DatabaseConnectedConcept.CONCEPT_ELEMENT_NAME, elem)
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
        if (this.attributeLabel != null) {
            this.attributeLabel = new LabelInfo(other.attributeLabel);
            this.attributeLabel.attachNode(this);
        } else {
            this.attributeLabel = null;
        }
        if (this.objectLabel != null) {
            this.objectLabel = new LabelInfo(other.objectLabel);
            this.objectLabel.attachNode(this);
        } else {
            this.objectLabel = null;
        }
    }

    /**
     * Get the current node position.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Set the node position in the model space.
     */
    public void setPosition(Point2D position) {
        this.position = position;
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
        return position.getX();
    }

    /**
     * Get the y coordinate in the model space.
     */
    public double getY() {
        return position.getY();
    }

    /**
     * Get the horizontal radius used for this node.
     */
    public double getRadiusX() {
        if (null == concept) {
            return 0;
        }
        return RADIUS;
    }

    /**
     * Get the vertical radius used for this node.
     */
    public double getRadiusY() {
        if (null == concept) {
            return 0;
        }
        return RADIUS;
    }

    /**
     * Returns the layout information for the attribute label.
     *
     * This might be null if there is no label attached.
     */
    public LabelInfo getAttributeLabelInfo() {
        return this.attributeLabel;
    }

    /**
     * Returns the layout information for the object label.
     *
     * This might be null if there is no label attached.
     */
    public LabelInfo getObjectLabelInfo() {
        return this.objectLabel;
    }

    /**
     * Sets the layout information for the attribute label attached.
     */
    public void setAttributeLabelInfo(LabelInfo labelInfo) {
        this.attributeLabel = labelInfo;
    }

    /**
     * Sets the layout information for the object label attached.
     */
    public void setObjectLabelInfo(LabelInfo labelInfo) {
        this.objectLabel = labelInfo;
    }

    /**
     * Inverts the y-coordinates of the node and the labels offsets.
     *
     * This is used when the diagram given has the y-axis pointing upwards.
     *
     * @ see SimpleLineDiagram.checkCoordinateSystem()
     */
    public void invertY() {
        this.position.setLocation(this.position.getX(), -this.position.getY());
        this.attributeLabel.setOffset(this.attributeLabel.getOffset().getX(),
                -this.attributeLabel.getOffset().getY());
        this.objectLabel.setOffset(this.objectLabel.getOffset().getX(),
                -this.objectLabel.getOffset().getY());
    }

    /**
     * Debug output.
     */
    public String toString() {
        String retVal = "DiagramNode:\n";
        retVal += "- Pos : (" + getX() + "," + getY() + ")\n";
        retVal += "- Size: (" + getRadiusX() + "," + getRadiusY() + ")\n";
        return retVal;
    }

    public static String getElementName() {
        return NODE_ELEMENT_NAME;
    }

    public List getConceptNestingList() {
        List conceptList = CollectionFactory.createDefaultList();
        DiagramNode node = this;
        while (node != null) {
            conceptList.add(node.getConcept());
            node = node.getOuterNode();
        }
        return conceptList;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof DiagramNode)){
            return false;
        }
        DiagramNode other = (DiagramNode)obj;
        if(!this.getIdentifier().equals(other.getIdentifier())){
            return false;
        }
        if(!this.getPosition().equals(other.getPosition())){
            return false;
        }

        if(!this.getObjectLabelInfo().equals(other.getObjectLabelInfo())){
            return false;
        }

        if(!this.getAttributeLabelInfo().equals(other.getAttributeLabelInfo())){
            return false;
        }
        return true;
    }
}