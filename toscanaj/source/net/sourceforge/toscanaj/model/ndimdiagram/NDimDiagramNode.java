/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.ndimdiagram;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * Implements a diagram node using an n-dimensional space and projection onto the plane.
 *
 * The original position is used as an offset, in case the node gets positioned directly. Theoretically
 * one could change the ndimVector instead.
 * @todo do we want that? Probably we should add at least some way to reset the
 *       offset
 */
public class NDimDiagramNode extends DiagramNode {
    private double[] ndimVector;

    public NDimDiagramNode(NDimDiagram diagram, String identifier, double[] ndimVector, Concept concept,
                           LabelInfo attributeLabel, LabelInfo objectLabel,
                           DiagramNode outerNode) {
        super(diagram, identifier, new Point2D.Double(0, 0), concept, attributeLabel, objectLabel, outerNode);
        this.ndimVector = ndimVector;
    }
    
    public NDimDiagramNode(NDimDiagram nDimDiagram, Element diagramNode) throws XMLSyntaxError {
    	super(nDimDiagram, diagramNode);
    	Element ndimPosElem = diagramNode.getChild("ndimVector");
    	List coordElems = ndimPosElem.getChildren("coordinate");
        Iterator it = coordElems.iterator();
        this.ndimVector = new double[coordElems.size()];
        int i = 0;
    	while (it.hasNext()) {
            Element coordElem = (Element) it.next();
            this.ndimVector[i] = Double.parseDouble(coordElem.getTextNormalize());
            i++;
        }
        // change full 2d position (which has been saved) into offset again
        double x = super.getPosition().getX() - this.getProjectedPosition().getX();
        double y = super.getPosition().getY() - this.getProjectedPosition().getY();
        this.position.setLocation(x,y);
    }

    public Element toXML() {
    	Element retVal = super.toXML();
    	Element nDimPosElem = new Element("ndimVector");
    	for (int i = 0; i < ndimVector.length; i++) {
            double coordinate = ndimVector[i];
            Element coordElem = new Element("coordinate");
            coordElem.addContent(String.valueOf(coordinate));
            nDimPosElem.addContent(coordElem);
        }
        retVal.addContent(nDimPosElem);
    	return retVal;
    }

    public Point2D getPosition() {
        Point2D projPos = getProjectedPosition();
        return new Point2D.Double(position.getX() + projPos.getX(), position.getY() + projPos.getY());
    }

    protected Point2D getProjectedPosition() {
        Point2D pos = new Point2D.Double(0, 0);
        NDimDiagram ndimDiagram = (NDimDiagram) this.diagram;
		Iterator baseIt = ndimDiagram.getBase().iterator();
        for (int i = 0; i < ndimVector.length; i++) {
            double v = ndimVector[i];
            Point2D baseVec = (Point2D) baseIt.next();
            pos.setLocation(pos.getX() + baseVec.getX() * v,
                    pos.getY() + baseVec.getY() * v);
        }
        return pos;
    }

    public void setPosition(double x, double y) {
        Point2D projPos = getProjectedPosition();
        this.position.setLocation(x - projPos.getX(), y - projPos.getY());
    }

    public double[] getNdimVector() {
        return ndimVector;
    }

    public void setNdimVector(double[] ndimVector) {
        this.ndimVector = ndimVector;
    }
}
