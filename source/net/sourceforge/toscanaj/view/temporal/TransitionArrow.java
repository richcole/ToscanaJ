/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.temporal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.CanvasItem;

public class TransitionArrow extends CanvasItem {
	private static final double SHORTENING_FACTOR = 0.15;
    private NodeView startNodeView;
	private NodeView endNodeView;
	private Color baseColor;
	private Rectangle2D bounds;
    private Point2D shiftVector = new Point2D.Double();
    private double timePos;
    private AnimationTimeController timeController;
	
    public TransitionArrow(NodeView startNodeView, NodeView endNodeView, Color color, double timePos, AnimationTimeController timeController) {
    	this.startNodeView = startNodeView;
    	this.endNodeView = endNodeView;
    	this.baseColor = color;
    	this.timePos = timePos;
    	this.timeController = timeController;
    	
    	calculateBounds();
    }

    public void draw(Graphics2D g) {
    	if(this.startNodeView == this.endNodeView) {
    		return;
    	}
        Paint color = calculatePaint();
        if(color == null) { // nothing to draw
        	return;
        }

    	updateShiftVector();
    	
    	Paint oldPaint = g.getPaint();
    	AffineTransform oldTransform = g.getTransform();
    	
        double startX = getStartX();
        double startY = getStartY();
        double endX = getEndX();
        double endY = getEndY();
    	float length = (float) Math.sqrt((startX - endX) * (startX - endX) +
        				       		      (startY - endY) * (startY - endY));

        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(-20,-7);
        arrow.lineTo(0,0);
        arrow.lineTo(-20,7);
        arrow.lineTo(-20,2);
        arrow.lineTo(-length,2);
        arrow.lineTo(-length,-2);
        arrow.lineTo(-20,-2);
        arrow.closePath();

        g.setPaint(color);
        g.translate(endX, endY);
        g.rotate(Math.atan2(endY - startY, endX - startX));
    	g.fill(arrow);
    	
    	g.setPaint(oldPaint);
    	g.setTransform(oldTransform);
    }
    
    private Paint calculatePaint() {
        AnimationTimeController controller = this.timeController;
        double timeOffset = controller.getCurrentTime() - this.timePos;
        double alpha = 0;
        if(timeOffset < - controller.getFadeInTime()) {
            return null;
        } else if(timeOffset < 0) {
            alpha = 1 + timeOffset / controller.getFadeInTime();
        } else if(timeOffset < controller.getVisibleTime()) {
            alpha = 1;
        } else if(timeOffset < controller.getVisibleTime() + controller.getFadeOutTime()) {
            alpha = 1 - (timeOffset - controller.getVisibleTime()) / controller.getFadeOutTime();
        } else {
            return null;
        }
        return new Color(this.baseColor.getRed(), this.baseColor.getGreen(), this.baseColor.getBlue(),
                          (int) (alpha * this.baseColor.getAlpha()));
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

    private void updateShiftVector() {
        double xDiff = this.endNodeView.getPosition().getX() -
                        this.startNodeView.getPosition().getX();
        double yDiff = this.endNodeView.getPosition().getY() -
                        this.startNodeView.getPosition().getY();
        double shiftDist = 10.0;
        double factor = shiftDist / Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        this.shiftVector = new Point2D.Double(yDiff * factor, -xDiff * factor);
    }

    protected double getStartX() {
        double x = this.startNodeView.getPosition().getX();
        return x + 
               SHORTENING_FACTOR * (this.endNodeView.getPosition().getX() - x) + 
               this.shiftVector.getX();
    }

    protected double getStartY() {
        double y = this.startNodeView.getPosition().getY();
        return y + 
               SHORTENING_FACTOR * (this.endNodeView.getPosition().getY() - y) +
               this.shiftVector.getY();
    }

    protected double getEndX() {
        double x = this.endNodeView.getPosition().getX();
        return x + 
               SHORTENING_FACTOR * (this.startNodeView.getPosition().getX() - x) +
               this.shiftVector.getX();
    }

    protected double getEndY() {
        double y = this.endNodeView.getPosition().getY();
        return y + 
               SHORTENING_FACTOR * (this.startNodeView.getPosition().getY() - y) +
               this.shiftVector.getY();
    }
}
