/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.Vector;
import java.util.Iterator;
import java.awt.geom.Point2D;

/**
 * Implements a diagram node using an n-dimensional space and projection onto the plane.
 *
 * The original position is used as an offset, in case the node gets positioned directly. Theoretically
 * one could change the ndimVector instead.
 * @todo do we want that?
 */
public class NDimDiagramNode extends DiagramNode {
    private double[] ndimVector;
    private Vector base;

    public NDimDiagramNode(String identifier, double[] ndimVector, Concept concept,
                           LabelInfo attributeLabel, LabelInfo objectLabel,
                           DiagramNode outerNode, Vector base) {
        super(identifier, new Point2D.Double(0,0), concept, attributeLabel, objectLabel, outerNode);
        this.ndimVector = ndimVector;
        this.base = base;
    }

    public NDimDiagramNode(DiagramNode other, double[] ndimVector, Vector base) {
        super(other);
        this.ndimVector = ndimVector;
        this.base = base;
    }

    public Point2D getPosition() {
        Point2D projPos = getProjectedPosition();
        return new Point2D.Double(position.getX() + projPos.getX(), position.getY() + projPos.getY());
    }

    protected Point2D getProjectedPosition() {
        Point2D pos = new Point2D.Double(0,0);
        Iterator baseIt = base.iterator();
        for (int i = 0; i < ndimVector.length; i++) {
            double v = ndimVector[i];
            Point2D baseVec = (Point2D) baseIt.next();
            pos.setLocation(pos.getX() + baseVec.getX() * v,
                                 pos.getY() + baseVec.getY() * v);
        }
        return pos;
    }

    public void setPosition(Point2D position) {
        Point2D projPos = getProjectedPosition();
        this.position.setLocation(position.getX() - projPos.getX(), position.getY() - projPos.getY());
    }

    public double[] getNdimVector() {
        return ndimVector;
    }

    public void setNdimVector(double[] ndimVector) {
        this.ndimVector = ndimVector;
    }

    public Vector getBase() {
        return base;
    }
}
