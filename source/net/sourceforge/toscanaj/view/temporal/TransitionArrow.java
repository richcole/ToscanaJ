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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.MovableCanvasItem;

public class TransitionArrow extends MovableCanvasItem {
    protected NodeView startNodeView;
    protected NodeView endNodeView;
    protected Rectangle2D bounds;
    protected Point2D shiftVector = new Point2D.Double();
    protected Point2D manualOffset = new Point2D.Double();
    protected double timePos;
    protected AnimationTimeController timeController;
    protected ArrowStyle style;
	
    private Shape currentShape;
    
    public TransitionArrow(NodeView startNodeView, NodeView endNodeView, ArrowStyle style, double timePos, AnimationTimeController timeController) {
    	this.startNodeView = startNodeView;
    	this.endNodeView = endNodeView;
    	this.style = style;
    	this.timePos = timePos;
    	this.timeController = timeController;

		updateShiftVector();    	
    	calculateBounds();
    }

    public void draw(Graphics2D g) {
    	if(this.startNodeView == this.endNodeView) {
    		return;
    	}

        double startX = getStartX();
        double startY = getStartY();
        double endX = getEndX();
        double endY = getEndY();

        float headLength = (float) this.style.getHeadLength();
        float headWidth = (float) this.style.getHeadWidth();
        float length = (float) Math.sqrt((startX - endX) * (startX - endX) +
                                          (startY - endY) * (startY - endY));

        Paint paint = calculatePaint(length);
        if(paint == null) { // nothing to draw
        	this.currentShape = null;
        	return;
        }

    	updateShiftVector();
    	
    	Paint oldPaint = g.getPaint();
        
        Shape line = this.style.getStroke().createStrokedShape(new Line2D.Double(-length,0,-headLength,0));
    	
		GeneralPath arrow = new GeneralPath(line);
        arrow.moveTo(-headLength,-headWidth/2);
		arrow.lineTo(0,0);
		arrow.lineTo(-headLength,headWidth/2);
		arrow.closePath();
        
		AffineTransform shapeTransform = new AffineTransform();        
		shapeTransform.translate(this.manualOffset.getX(), this.manualOffset.getY());
		shapeTransform.translate(endX, endY);
		shapeTransform.rotate(Math.atan2(endY - startY, endX - startX));
		this.currentShape = shapeTransform.createTransformedShape(arrow);

        g.setPaint(paint);
    	g.fill(currentShape);
    	
    	g.setPaint(oldPaint);
    }
    
    protected Paint calculatePaint(float arrowLength) {
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
        Color baseColor = this.style.getColor();
        Color finalColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
                          (int) (alpha * baseColor.getAlpha()));

        return finalColor;
    }
    
    public boolean containsPoint(Point2D point) {
    	if(this.currentShape == null) {
    		return false;
    	}
    	return this.currentShape.contains(point);
    }

    public Point2D getPosition() {
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        // we need to update in case one of the nodes has moved
        // @todo try finding something better
        calculateBounds();
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
        this.bounds = new Rectangle2D.Double(x + this.manualOffset.getX(),y + this.manualOffset.getY(),width,height);
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

    /**
     * @todo start and end positions should consider node radius
     */
    protected double getStartX() {
        double x = this.startNodeView.getPosition().getX();
        return x + 
               this.style.getRelativeLength() * (this.endNodeView.getPosition().getX() - x) + 
               this.shiftVector.getX();
    }

    protected double getStartY() {
        double y = this.startNodeView.getPosition().getY();
        return y + 
               this.style.getRelativeLength() * (this.endNodeView.getPosition().getY() - y) +
               this.shiftVector.getY();
    }

    protected double getEndX() {
        double x = this.endNodeView.getPosition().getX();
        return x + 
               this.style.getRelativeLength() * (this.startNodeView.getPosition().getX() - x) +
               this.shiftVector.getX();
    }

    protected double getEndY() {
        double y = this.endNodeView.getPosition().getY();
        return y + 
               this.style.getRelativeLength() * (this.startNodeView.getPosition().getY() - y) +
               this.shiftVector.getY();
    }

    public void setPosition(Point2D newPosition) {
    	this.manualOffset.setLocation(this.manualOffset.getX() + (newPosition.getX() - getPosition().getX()), 
									  this.manualOffset.getY() + (newPosition.getY() - getPosition().getY()));
    }
}
