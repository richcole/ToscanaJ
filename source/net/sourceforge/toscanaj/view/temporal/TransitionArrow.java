/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.temporal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.CanvasItem;

public class TransitionArrow extends CanvasItem {
	private static final double SHORTENING_FACTOR = 0.15;
    private NodeView startNodeView;
	private NodeView endNodeView;
	private Color color;
	private static Shape arrowHead;
	private Rectangle2D bounds;
	
	static {
		arrowHead = createArrowHead();
	}

    protected static GeneralPath createArrowHead() {
        GeneralPath path = new GeneralPath();
        path.moveTo(-20,-10);
        path.lineTo(5,0);
        path.lineTo(-20,10);
        path.closePath();
        return path;
    }

    public TransitionArrow(NodeView startNodeView, NodeView endNodeView, Color color) {
    	this.startNodeView = startNodeView;
    	this.endNodeView = endNodeView;
    	this.color = color;
    	
    	calculateBounds();
    }

    public void draw(Graphics2D g) {
    	if(this.startNodeView == this.endNodeView) {
    		return;
    	}
    	
    	Paint oldPaint = g.getPaint();
    	Stroke oldStroke = g.getStroke();
    	AffineTransform oldTransform = g.getTransform();
    	
    	g.setPaint(color);
    	g.setStroke(new BasicStroke(3));
    	Line2D line = new Line2D.Double(getStartX(), getStartY(), getEndX(), getEndY()); 
    	g.draw(line);
    	g.translate(getEndX(), getEndY());
    	g.rotate(Math.atan2(getEndY() - getStartY(), getEndX() - getStartX()));
    	g.fill(arrowHead);
    	
    	g.setPaint(oldPaint);
    	g.setStroke(oldStroke);
    	g.setTransform(oldTransform);
    }

    public boolean containsPoint(Point2D point) {
        return false;
    }

    public Point2D getPosition() {
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return bounds;
    }

    protected void calculateBounds() {
        double x1 = getStartX();
        double y1 = getStartY();
        double x2 = getEndX();
        double y2 = getEndY();
        
        double x,y,width,height;
        if(x2 > x1) {
            x = x1;
            width = x2 - x1;
        } else {
            x = x2;
            width = x1 - x2;
        }
        if(y2 > y1) {
            y = y1;
            height = y2 - y1;
        } else {
            y = y2;
            height = y1 - y2;
        }
        this.bounds = new Rectangle2D.Double(x,y,width,height);
    }

    protected double getStartX() {
        double x = this.startNodeView.getPosition().getX();
        return x + SHORTENING_FACTOR * (this.endNodeView.getPosition().getX() - x);
    }

    protected double getStartY() {
        double y = this.startNodeView.getPosition().getY();
        return y + SHORTENING_FACTOR * (this.endNodeView.getPosition().getY() - y) - 10;
    }

    protected double getEndX() {
        double x = this.endNodeView.getPosition().getX();
        return x + SHORTENING_FACTOR * (this.startNodeView.getPosition().getX() - x);
    }

    protected double getEndY() {
        double y = this.endNodeView.getPosition().getY();
        return y + SHORTENING_FACTOR * (this.startNodeView.getPosition().getY() - y) - 10;
    }
}
