package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

/**
 * A class representing a diagram node with an inner diagram.
 */
public class NestedDiagramNode extends DiagramNode {
    /**
     * Stores the inner diagram.
     */
    private Diagram2D innerDiagram;

    /**
     * Creates a new diagram node by copying the information from the given
     * other node and attaching the diagram.
     *
     * The given node determines the position in the outer diagram while the
     * given diagram is the inner diagram in a nested diagram. The scale parameter
     * determines how much the new diagram will be scaled to accomodate the inner
     * diagrams.
     *
     * If the dropAttributeLabels flag is set, the inner nodes will have no
     * attribute labels attached.
     */
    public NestedDiagramNode(DiagramNode outerNode, Diagram2D innerDiagram, float scale,
                                        boolean dropAttributeLabels ) {
        super( new Point2D.Double(outerNode.getX()*scale, outerNode.getY()*scale),
               outerNode.getConcept(),
               new LabelInfo(outerNode.getAttributeLabelInfo()), null);
        // scale attribute label position
        this.attributeLabel.setOffset(new Point2D.Double( this.attributeLabel.getOffset().getX() * scale,
                                                          this.attributeLabel.getOffset().getY() * scale ) );

        // calculate an offset that places center of the inner diagram into the middle of the node
        Rectangle2D rect = innerDiagram.getBounds();
        Point2D offset = new Point2D.Double(
                                this.getX() - rect.getX() - rect.getWidth()/2,
                                this.getY() - rect.getY() - rect.getHeight()/2 );
        SimpleLineDiagram newDiag = new SimpleLineDiagram();
        Hashtable nodeMap = new Hashtable();
        for(int i = 0; i< innerDiagram.getNumberOfNodes(); i++) {
            DiagramNode oldNode = innerDiagram.getNode(i);
            Point2D newPos = new Point2D.Double( oldNode.getX() + offset.getX(),
                                                 oldNode.getY() + offset.getY() );
            Concept newConcept = oldNode.getConcept().filterByContingent(outerNode.getConcept());
            LabelInfo newAttrLabel;
            if(dropAttributeLabels) {
                newAttrLabel = null;
            }
            else {
                newAttrLabel = new LabelInfo(oldNode.getAttributeLabelInfo());
            }
            LabelInfo newObjLabel = new LabelInfo(oldNode.getObjectLabelInfo());
            DiagramNode newNode = new DiagramNode(newPos, newConcept, newAttrLabel, newObjLabel);
            nodeMap.put(oldNode,newNode);
            newDiag.addNode(newNode);
        }
        for(int i = 0; i < innerDiagram.getNumberOfLines(); i++) {
            DiagramLine line = innerDiagram.getLine(i);
            DiagramNode from = (DiagramNode) nodeMap.get(line.getFromNode());
            DiagramNode to = (DiagramNode) nodeMap.get(line.getToNode());
            newDiag.addLine( from, to );

            // add direct neighbours in inner diagram to concepts
            AbstractConceptImplementation concept1 =
                             (AbstractConceptImplementation) from.getConcept();
            AbstractConceptImplementation concept2 =
                             (AbstractConceptImplementation) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        this.innerDiagram = newDiag;
    }

    /**
     * Returns the inner diagram of the node.
     */
    public Diagram2D getInnerDiagram() {
        return this.innerDiagram;
    }

    /**
     * Calculates the x-extension of this node.
     *
     * This is based on the bounds of the inner diagram.
     */
    public double getRadiusX() {
        Rectangle2D bounds = this.innerDiagram.getBounds();
        if( bounds.getHeight() > 2*bounds.getWidth()) {
            return bounds.getHeight()/3.4;
        }
        else {
            return bounds.getWidth()/1.7;
        }
    }

    /**
     * Calculates the y-extension of this node.
     *
     * This is based on the bounds of the inner diagram.
     */
    public double getRadiusY() {
        Rectangle2D bounds = this.innerDiagram.getBounds();
        if( bounds.getWidth() > 2*bounds.getHeight()) {
            return bounds.getWidth()/3.4;
        }
        else {
            return bounds.getHeight()/1.7;
        }
    }
}