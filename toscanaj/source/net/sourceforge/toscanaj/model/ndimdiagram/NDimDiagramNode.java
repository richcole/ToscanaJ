/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.ndimdiagram;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;

import org.jdom.Element;

/**
 * Implements a diagram node using an n-dimensional space and projection onto
 * the plane.
 * 
 * The original position is used as an offset, in case the node gets positioned
 * directly. Theoretically one could change the ndimVector instead.
 * 
 * @todo do we want that? Probably we should add at least some way to reset the
 *       offset
 */
public class NDimDiagramNode<O, A> extends DiagramNode<O, A> {
    private double[] ndimVector;

    public NDimDiagramNode(final NDimDiagram<O, A> diagram,
            final String identifier, final double[] ndimVector,
            final Concept<O, A> concept, final LabelInfo attributeLabel,
            final LabelInfo objectLabel, final DiagramNode<O, A> outerNode) {
        super(diagram, identifier, new Point2D.Double(0, 0), concept,
                attributeLabel, objectLabel, outerNode);
        this.ndimVector = ndimVector;
    }

    @SuppressWarnings("unchecked")
    public NDimDiagramNode(final NDimDiagram nDimDiagram,
            final Element diagramNode) throws XMLSyntaxError {
        super(nDimDiagram, diagramNode);
        final Element ndimPosElem = diagramNode.getChild("ndimVector");
        final List<Element> coordElems = ndimPosElem.getChildren("coordinate");
        final Iterator<Element> it = coordElems.iterator();
        this.ndimVector = new double[coordElems.size()];
        int i = 0;
        while (it.hasNext()) {
            final Element coordElem = it.next();
            this.ndimVector[i] = Double.parseDouble(coordElem
                    .getTextNormalize());
            i++;
        }
        // change full 2d position (which has been saved) into offset again
        final double x = super.getPosition().getX()
                - this.getProjectedPosition().getX();
        final double y = super.getPosition().getY()
                - this.getProjectedPosition().getY();
        this.position.setLocation(x, y);
    }

    @Override
    public Element toXML() {
        final Element retVal = super.toXML();
        final Element nDimPosElem = new Element("ndimVector");
        for (final double coordinate : ndimVector) {
            final Element coordElem = new Element("coordinate");
            coordElem.addContent(String.valueOf(coordinate));
            nDimPosElem.addContent(coordElem);
        }
        retVal.addContent(nDimPosElem);
        return retVal;
    }

    @Override
    public Point2D getPosition() {
        final Point2D projPos = getProjectedPosition();
        return new Point2D.Double(position.getX() + projPos.getX(), position
                .getY()
                + projPos.getY());
    }

    protected Point2D getProjectedPosition() {
        final Point2D pos = new Point2D.Double(0, 0);
        final NDimDiagram<O, A> ndimDiagram = (NDimDiagram<O, A>) this.diagram;
        final Iterator<Point2D> baseIt = ndimDiagram.getBase().iterator();
        for (final double v : ndimVector) {
            final Point2D baseVec = baseIt.next();
            pos.setLocation(pos.getX() + baseVec.getX() * v, pos.getY()
                    + baseVec.getY() * v);
        }
        return pos;
    }

    @Override
    public void setPosition(final double x, final double y) {
        final Point2D projPos = getProjectedPosition();
        this.position.setLocation(x - projPos.getX(), y - projPos.getY());
    }

    public double[] getNdimVector() {
        return ndimVector;
    }

    public void setNdimVector(final double[] ndimVector) {
        this.ndimVector = ndimVector;
    }
}
