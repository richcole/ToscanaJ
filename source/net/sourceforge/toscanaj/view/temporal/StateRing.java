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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.ExtraCanvasItemFactory;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.canvas.CanvasItem;
import org.tockit.util.ColorStringConverter;

public class StateRing extends CanvasItem implements XMLizable {
    private static class Factory implements ExtraCanvasItemFactory {
        public CanvasItem createCanvasItem(SimpleLineDiagram diagram, Element element) {
            System.out.println("Here SR");
            return null;
        }
    }
    
    static {
        SimpleLineDiagram.registerExtraCanvasItemFactory("stateRing", new Factory());
    }
   
    private DiagramNode node;
	private Color baseColor;
    private double timePos;
    private AnimationTimeController timeController;
	
    public StateRing(DiagramNode node, Color color, double timePos, AnimationTimeController timeController) {
    	this.node = node;
    	this.baseColor = color;
    	this.timePos = timePos;
    	this.timeController = timeController;
    }

    public void draw(Graphics2D g) {
        Paint color = calculatePaint();
        if(color == null) { // nothing to draw
        	return;
        }

    	Paint oldPaint = g.getPaint();

        Rectangle2D bounds = getCanvasBounds(g);
        Ellipse2D ellipse = new Ellipse2D.Double(bounds.getX(), bounds.getY(),
                                       			 bounds.getWidth(), bounds.getHeight());
    	
        g.setPaint(color);
        g.fill(ellipse);
    	
    	g.setPaint(oldPaint);
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
        return this.node.getPosition();
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        Point2D center = this.node.getPosition();
        double x = center.getX();
        double y = center.getY();
        double rx = node.getRadiusX();
        double ry = node.getRadiusY();
        return new Rectangle2D.Double(x - rx, y - ry, 2 * rx + 3, 2 * ry + 3);
    }

    public Element toXML() {
        Element result = new Element("stateRing");
        result.setAttribute("nodeView",node.getIdentifier());
        result.setAttribute("color", ColorStringConverter.colorToString(this.baseColor));
        return result;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
    }
}
