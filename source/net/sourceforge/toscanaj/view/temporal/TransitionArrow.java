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

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.ExtraCanvasItemFactory;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;

import org.jdom.Element;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.MovableCanvasItem;

public class TransitionArrow extends MovableCanvasItem implements XMLizable {
    private static class Factory implements ExtraCanvasItemFactory {
        public CanvasItem createCanvasItem(SimpleLineDiagram diagram, Element element) throws XMLSyntaxError {
            TransitionArrow retVal = new TransitionArrow();
            retVal.timeController = new AnimationTimeController(Double.MAX_VALUE, 0, Double.MAX_VALUE, 0, 1);
            retVal.timeController.setCurrentTime(Double.MAX_VALUE/2);
            retVal.startNode = diagram.getNode(element.getAttributeValue("from"));
            retVal.endNode = diagram.getNode(element.getAttributeValue("to"));
            Element offsetElem = element.getChild("offset");
            double offsetX = Double.parseDouble(offsetElem.getAttributeValue("x"));
            double offsetY = Double.parseDouble(offsetElem.getAttributeValue("y"));
            retVal.manualOffset = new Point2D.Double(offsetX, offsetY);
            if(element.getAttributeValue("arrowStyle") != null) {
                retVal.style = DiagramSchema.getCurrentSchema().
                                    getArrowStyles()[XMLHelper.getIntAttribute(element, "arrowStyle")];
            } else {
                // just to keep parsing older file formats, even though not correct
                retVal.style = DiagramSchema.getCurrentSchema().getArrowStyles()[0];
            }
            retVal.updateShiftVector();        
            retVal.calculateBounds();
            
            return retVal;
        }
    }
    
    public static void registerFactory() {
        SimpleLineDiagram.registerExtraCanvasItemFactory("transitionArrow", new Factory());
    }
   
    protected DiagramNode startNode;
    protected DiagramNode endNode;
    protected Rectangle2D bounds = new Rectangle2D.Double();
    protected Point2D startPoint = new Point2D.Double();
    protected Point2D endPoint = new Point2D.Double();
    protected Point2D shiftVector = new Point2D.Double();
    protected Point2D manualOffset = new Point2D.Double();
    protected double timePos;
    protected AnimationTimeController timeController;
    protected ArrowStyle style;
	
    private Shape currentShape;
    
    public TransitionArrow(DiagramNode startNode, DiagramNode endNode, ArrowStyle style, double timePos, AnimationTimeController timeController) {
    	this.startNode = startNode;
    	this.endNode = endNode;
    	this.style = style;
    	this.timePos = timePos;
    	this.timeController = timeController;

		updateShiftVector();    	
    	calculateBounds();
    }

    private TransitionArrow() {
    	// should be created through factory
    }

    public void draw(Graphics2D g) {
    	if(this.startNode == this.endNode) {
    		return;
    	}

    	calculateBounds();
    	
        double startX = this.startPoint.getX();
        double startY = this.startPoint.getY();
        double endX = this.endPoint.getX();
        double endY = this.endPoint.getY();

        float length = (float) this.startPoint.distance(this.endPoint);

        Paint paint = calculatePaint(length, this.style.getColor());
        if(paint == null) { // nothing to draw
        	this.currentShape = null;
        	return;
        }

    	updateShiftVector();
    	
    	Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();
        
        Shape arrow = getArrowShape(this.style, length);
        
		AffineTransform shapeTransform = new AffineTransform();        
		shapeTransform.translate(this.manualOffset.getX(), this.manualOffset.getY());
		shapeTransform.translate(endX, endY);
		shapeTransform.rotate(Math.atan2(endY - startY, endX - startX));
		this.currentShape = shapeTransform.createTransformedShape(arrow);

        g.setPaint(paint);
        g.fill(this.currentShape);
        if(this.style.getBorderWidth() != 0) {
            g.setStroke(new BasicStroke(this.style.getBorderWidth()));
            g.setPaint(calculatePaint(length, Color.BLACK));
            g.draw(this.currentShape);
        }
    	
        g.setStroke(oldStroke);
    	g.setPaint(oldPaint);
    }
    
    public static Shape getArrowShape(ArrowStyle style, double length) {
        float headLength = (float) style.getHeadLength();
        float headWidth = (float) style.getHeadWidth();

        // @todo figure out why things don't match up -- at the moment there is only this hack to fixz
        // the major issues
        Shape line = style.getStroke().createStrokedShape(new Line2D.Double(-length,0,-headLength,0));
        double diff = headLength + line.getBounds().getMaxX();
        if(diff > 0) {
            line = style.getStroke().createStrokedShape(new Line2D.Double(-length,0,-headLength - diff,0));
        }
    	
		GeneralPath arrow = new GeneralPath(line);
        arrow.moveTo(-headLength,-headWidth/2);
		arrow.lineTo(0,0);
		arrow.lineTo(-headLength,headWidth/2);
		arrow.closePath();
        return arrow;
    }

    protected Paint calculatePaint(float arrowLength, Color baseColor) {
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
        return new Point2D.Double(this.bounds.getCenterX(), this.bounds.getCenterY());
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        // we need to update in case one of the nodes has moved
        // @todo try finding something better
        calculateBounds();
        return this.bounds;
    }

    protected void calculateBounds() {
        double startX = this.startNode.getPosition().getX() + this.shiftVector.getX();
		double startY = this.startNode.getPosition().getY() + this.shiftVector.getY();
		double endX = this.endNode.getPosition().getX() + this.shiftVector.getX();
		double endY = this.endNode.getPosition().getY() + this.shiftVector.getY();
		
		double dx = endX-startX;
		double dy = endY-startY;
		
		if(dx == 0) {
			startY += this.startNode.getRadiusY() * Math.signum(dy);
			endY -= this.endNode.getRadiusY() * Math.signum(dy);
		} else {
			double angle = Math.atan2(dy,dx);
			startX += this.startNode.getRadiusX() * Math.cos(angle);
			startY += this.startNode.getRadiusY() * Math.sin(angle);
			endX -= this.endNode.getRadiusX() * Math.cos(angle);
			endY -= this.endNode.getRadiusY() * Math.sin(angle);
		}
        
        this.startPoint.setLocation(startX, startY);
        this.endPoint.setLocation(endX, endY);
		
        double x,y,width,height;
        if(endX > startX) {
            x = startX;
            width = endX - startX;
        } else {
            x = endX;
            width = startX - endX;
        }
        if(endY > startY) {
            y = startY;
            height = endY - startY;
        } else {
            y = endY;
            height = startY - endY;
        }
        
        this.bounds.setFrame(x + this.manualOffset.getX(),y + this.manualOffset.getY(),width,height);
    }

    private void updateShiftVector() {
        double xDiff = this.endNode.getPosition().getX() -
                        this.startNode.getPosition().getX();
        double yDiff = this.endNode.getPosition().getY() -
                        this.startNode.getPosition().getY();
        double shiftDist = 10.0;
        double factor = shiftDist / Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        this.shiftVector = new Point2D.Double(yDiff * factor, -xDiff * factor);
    }

    public void setPosition(Point2D newPosition) {
    	this.manualOffset.setLocation(this.manualOffset.getX() + (newPosition.getX() - getPosition().getX()), 
									  this.manualOffset.getY() + (newPosition.getY() - getPosition().getY()));
    }

    public Element toXML() {
        Element result = new Element(getTagName());
        result.setAttribute("from", this.startNode.getIdentifier());
        result.setAttribute("to", this.endNode.getIdentifier());
        Element offsetElem = new Element("offset");
        offsetElem.setAttribute("x", String.valueOf(this.manualOffset.getX()));
        offsetElem.setAttribute("y", String.valueOf(this.manualOffset.getY()));
        result.addContent(offsetElem);
        for (int i = 0; i < DiagramSchema.getCurrentSchema().getArrowStyles().length; i++) {
            if(this.style == DiagramSchema.getCurrentSchema().getArrowStyles()[i]) {
                result.setAttribute("arrowStyle", String.valueOf(i));
            }
        }
        return result;
    }

    protected String getTagName() {
        return "transitionArrow";
    }

    public void readXML(Element elem) {
        // done in Factory -- can't be done without access to diagram
    }
}
