/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LabelConnector extends CanvasItem {

    private LabelView labelView;
    private Line2D line;

    public LabelConnector(LabelView labelView) {
    	this.labelView = labelView;
    }

    public void draw(Graphics2D graphics) {
        this.labelView.updateBounds(graphics);
    	updateLine();
    	
        Stroke oldStroke = graphics.getStroke();
        Paint oldPaint = graphics.getPaint();
        
        float[] dashstyle = {4, 4};
        graphics.setPaint(this.labelView.getConnectorColor());
        graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 1, dashstyle, 0));
        graphics.draw(this.line);

		graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }
    
    private void updateLine() {
    	if(this.labelView.isVisible()) {
	  		this.line = new Line2D.Double(this.labelView.getConnectorStartPosition(), 
	  									   this.labelView.getConnectorEndPosition());
    	}
    }

    public boolean containsPoint(Point2D point) {
        return false;
    }

    public Rectangle2D getCanvasBounds(Graphics2D graphics) {
        this.labelView.updateBounds(graphics);
        updateLine();
        if(this.labelView.isVisible()) {
	       	return this.line.getBounds2D();
        } else {
        	return null;
        }
    }

    public Point2D getPosition() {
        if(this.labelView.isVisible()) {
            return this.labelView.getPosition();
        } else {
            return null;
        }
    }
}
