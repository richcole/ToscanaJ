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

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.view.diagram.NodeView;

public class InterSequenceTransitionArrow extends TransitionArrow {
	protected Color endColor;
	
    public InterSequenceTransitionArrow(NodeView startNodeView, NodeView endNodeView, 
                                         Color startColor, Color endColor, double timePos, 
                                         AnimationTimeController timeController) {
        super(startNodeView, endNodeView, startColor, timePos, timeController);
    	this.endColor = endColor;
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
        Color finalStartColor = new Color(this.baseColor.getRed(), this.baseColor.getGreen(), this.baseColor.getBlue(),
                          (int) (alpha * this.baseColor.getAlpha()));
        Color finalEndColor = new Color(this.endColor.getRed(), this.endColor.getGreen(), this.endColor.getBlue(),
                          (int) (alpha * this.endColor.getAlpha()));
        return new GradientPaint(-arrowLength, 0, finalStartColor, 0, 0, finalEndColor);
    }
}
