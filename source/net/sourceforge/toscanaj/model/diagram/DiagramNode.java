package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.lattice.Concept;

import java.awt.geom.Point2D;

/**
 * Stores the information on a node in a diagram.
 *
 * This is mainly the position, the concept for the node and the information
 * on the labels attached to it.
 */
public class DiagramNode {

    /**
     * The size of nodes.
     *
     * This is currently a fixed value common for all nodes, but the access is
     * already done using a method on each instance, thus allowing easy extension
     * later.
     */
    private static final int RADIUS = 10;

    /**
     * The concept the node represents.
     *
     * If this is set to null the node points to a not realised concept in the
     * diagram, i.e. the concept theoretically could exist but is not supported
     * by the current set of data.
     */
    private Concept concept = null;

    /**
     * The position of the node.
     */
    private Point2D position = null;

    /**
     * The layout information for the attribute label.
     */
    private LabelInfo attributeLabel;

    /**
     * The layout information for the attribute label.
     */
    private LabelInfo objectLabel;

    /**
     * Construct a node for a concept at a position with two labels attached.
     *
     * The labels can be null if there is no label in this position. The concept
     * can be null if there is no concept realised for this node.
     */
    public DiagramNode(Point2D position, Concept concept, LabelInfo attributeLabel, LabelInfo objectLabel){
        this.position = position;
        this.concept = concept;
        this.attributeLabel = attributeLabel;
        if(attributeLabel != null) {
            attributeLabel.attachNode(this);
        }
        this.objectLabel = objectLabel;
        if(objectLabel != null) {
            objectLabel.attachNode(this);
        }
    }

    /**
     * A copy constructor creating a duplicate of the given node.
     */
    public DiagramNode(DiagramNode other) {
        this.position = other.position;
        this.concept = other.concept;
        this.attributeLabel = other.attributeLabel;
        if(this.attributeLabel != null) {
            this.attributeLabel.attachNode(this);
        }
        this.objectLabel = other.objectLabel;
        if(this.objectLabel != null) {
            this.objectLabel.attachNode(this);
        }
    }

    /**
     * Get the current node position.
     */
    public Point2D getPosition(){
        return position;
    }

    /**
     * Set the node position in the model space.
     */
    public void setPosition(Point2D position){
       this.position = position;
    }

    /**
     * Get the concept for this node.
     */
    public Concept getConcept(){
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
        if(this.concept.isRealised()) {
            return RADIUS;
        }
        else {
            return RADIUS/3;
        }
    }

    /**
     * Get the vertical radius used for this node.
     */
    public double getRadiusY() {
        if(this.concept.isRealised()) {
            return RADIUS;
        }
        else {
            return RADIUS/3;
        }
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
     * Debug output.
     */
    public String toString() {
        String retVal = "pos = (" + this.getX() + ", " + this.getY() +")\n";
        retVal += "size = (" + this.getRadiusX() + ", " + this.getRadiusX() +")\n";
        return retVal;
    }
}