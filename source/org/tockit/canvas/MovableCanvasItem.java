/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas;

import java.awt.geom.Point2D;

public abstract class MovableCanvasItem extends CanvasItem {
    public abstract void setPosition(Point2D newPosition);

    public void moveBy(double xDiff, double yDiff) {
        setPosition(new Point2D.Double(getPosition().getX() + xDiff, getPosition().getY() + yDiff));
    }
}
