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
    	/// @todo except for the two colors this is the same as in the parent -> refactor
        AnimationTimeController controller = this.timeController;

        double startTimeOffset = controller.getCurrentTime() - this.timePos;
        double startAlpha = 0;
        if(startTimeOffset < - controller.getFadeInTime()) {
            return null;
        } else if(startTimeOffset < 0) {
            startAlpha = 1 + startTimeOffset / controller.getFadeInTime();
        } else if(startTimeOffset < controller.getVisibleTime()) {
            startAlpha = 1;
        } else if(startTimeOffset < controller.getVisibleTime() + controller.getFadeOutTime()) {
            startAlpha = 1 - (startTimeOffset - controller.getVisibleTime()) / controller.getFadeOutTime();
        } else {
            return null;
        }
        Color finalStartColor = new Color(this.baseColor.getRed(), this.baseColor.getGreen(), this.baseColor.getBlue(),
                          (int) (startAlpha * this.baseColor.getAlpha()));

        double endTimeOffset = controller.getCurrentTime() - this.timePos;
        double endAlpha = 0;
        if(endTimeOffset < - controller.getFadeInTime()) {
            return null;
        } else if(endTimeOffset < 0) {
            endAlpha = 1 + endTimeOffset / controller.getFadeInTime();
        } else if(endTimeOffset < controller.getVisibleTime()) {
            endAlpha = 1;
        } else if(endTimeOffset < controller.getVisibleTime() + controller.getFadeOutTime()) {
            endAlpha = 1 - (endTimeOffset - controller.getVisibleTime()) / controller.getFadeOutTime();
        } else {
            return null;
        }
        Color finalEndColor = new Color(this.endColor.getRed(), this.endColor.getGreen(), this.endColor.getBlue(),
                          (int) (endAlpha * this.endColor.getAlpha()));

        return new GradientPaint(-arrowLength, 0, finalStartColor, 0, 0, finalEndColor);
    }
}
