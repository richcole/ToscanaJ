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

    public DiagramNode(Point2D position, Concept concept, 
                       LabelInfo attributeLabel, LabelInfo objectLabel,
                       DiagramNode outerNode){
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
        this.outerNode = outerNode;
    }
    
    public DiagramNode getOuterNode() {
        return outerNode;
    }

    /**
     * A copy constructor creating a duplicate of the given node.
     *
     * This is a deep copy for position and labels but refers to the same concept.
     */
    public DiagramNode(DiagramNode other) {
        this.position = (Point2D) other.position.clone();
        this.concept = other.concept;
        if(this.attributeLabel != null) {
            this.attributeLabel = new LabelInfo(other.attributeLabel);
            this.attributeLabel.attachNode(this);
        }
        else {
            this.attributeLabel = null;
        }
        if(this.objectLabel != null) {
            this.objectLabel = new LabelInfo(other.objectLabel);
            this.objectLabel.attachNode(this);
        }
        else {
            this.objectLabel = null;
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
            return RADIUS / 3;
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
            return RADIUS / 3;
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
     * Inverts the y-coordinates of the node and the labels offsets.
     *
     * This is used when the diagram given has the y-axis pointing upwards.
     *
     * @see SimpleLineDiagram.checkCoordinateSystem()
     */
    public void invertY() {
        this.position.setLocation(this.position.getX(), -this.position.getY());
        this.attributeLabel.setOffset( this.attributeLabel.getOffset().getX(),
                                       -this.attributeLabel.getOffset().getY() );
        this.objectLabel.setOffset( this.objectLabel.getOffset().getX(),
                                    -this.objectLabel.getOffset().getY() );
    }

    /**
     * Debug output.
     */
    public String toString() {
        String retVal = "DiagramNode:\n";
        retVal+= "- Pos : (" + getX() + "," + getY() + ")\n";
        retVal+= "- Size: (" + getRadiusX() + "," + getRadiusY() + ")\n";
        return retVal;
    }
}