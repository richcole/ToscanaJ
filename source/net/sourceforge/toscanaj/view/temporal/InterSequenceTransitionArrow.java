/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.temporal;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import org.jdom.Element;
import org.tockit.canvas.CanvasItem;
import org.tockit.util.ColorStringConverter;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.ExtraCanvasItemFactory;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;

public class InterSequenceTransitionArrow extends TransitionArrow {
    private static class Factory implements ExtraCanvasItemFactory {
        public CanvasItem createCanvasItem(SimpleLineDiagram diagram, Element element) {
            System.out.println("Here ISTA");
            return null;
        }
    }

    public static void registerFactory() {
        SimpleLineDiagram.registerExtraCanvasItemFactory("intersequenceTransitionArrow", new Factory());
    }

    protected Color endColor;
	
    public InterSequenceTransitionArrow(DiagramNode startNode, DiagramNode endNode, 
                                         ArrowStyle style, Color secondColor, double timePos, 
                                         AnimationTimeController timeController) {
        super(startNode, endNode, style, timePos, timeController);
    	this.endColor = secondColor;
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
        Color finalStartColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
                          (int) (alpha * baseColor.getAlpha()));
        Color finalEndColor = new Color(this.endColor.getRed(), this.endColor.getGreen(), this.endColor.getBlue(),
                          (int) (alpha * this.endColor.getAlpha()));

        return new GradientPaint(-arrowLength, 0, finalStartColor, 0, 0, finalEndColor);
    }
    
    public Element toXML() {
        Element result = super.toXML();
        result.setAttribute("endColor", ColorStringConverter.colorToString(endColor));
        return result;
    }
    
    protected String getTagName() {
        return "intersequenceTransitionArrow";
    }
}
