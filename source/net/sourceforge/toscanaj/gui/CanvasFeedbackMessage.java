/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasDrawnEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class CanvasFeedbackMessage extends CanvasItem {
	private long endTime;
    private long fadeInTime;
	private long fadeOutTime;
	
	private String message;
	private Canvas canvas;
	private Rectangle2D bounds;
	private Point2D pos;
	
	private static final long HOLD_TIME = 3000;
	private static final long FADE_TIME = 300;
	
	private static final Font MESSAGE_FONT = new Font("SansSerif", Font.PLAIN, 14);
	private static final Color MESSAGE_COLOR = Color.RED;
	
    private CanvasCallbackHandler canvasCallbackHandler;
	
	private class CanvasCallbackHandler implements EventBrokerListener {
        public void processEvent(Event e) {
        	canvas.repaint();
        }
	}
	
	public CanvasFeedbackMessage(String message, Canvas canvas, Point2D pos) {
		this.message = message;
		this.canvas = canvas;
		
	    this.fadeInTime = System.currentTimeMillis() + FADE_TIME;
		this.fadeOutTime = this.fadeInTime + HOLD_TIME;
		this.endTime = this.fadeOutTime + FADE_TIME;
		
		this.pos = pos;
		
		this.canvas.addCanvasItem(this);
        canvasCallbackHandler = new CanvasCallbackHandler();
        this.canvas.getController().getEventBroker().subscribe(
					canvasCallbackHandler, CanvasDrawnEvent.class, Object.class
		);
		
		canvas.repaint();
	}

    public void draw(Graphics2D g) {
        long time = System.currentTimeMillis();
        if(time > this.endTime) {
        	cleanUp();
        	return;
        }
        
        updateBounds(g);
        
    	Paint oldPaint = g.getPaint();
    	Font oldFont = g.getFont();
    	
    	double alpha = 1;
        if( time < this.fadeInTime ) {
            alpha = 1 - (this.fadeInTime - time) / (double) FADE_TIME;
        } else if( time > this.fadeOutTime ) {
    		alpha = (this.endTime - time) / (double) FADE_TIME;
    	}
    	Color color = new Color(MESSAGE_COLOR.getRed(), MESSAGE_COLOR.getGreen(), MESSAGE_COLOR.getBlue(), (int) (alpha * 255));
    	
        g.setPaint(color);
    	g.setFont(MESSAGE_FONT);
    	
    	g.drawString(this.message, (float)this.bounds.getX(), (float)this.bounds.getY());
    	
    	g.setPaint(oldPaint);
    	g.setFont(oldFont);
    }
    
    private void cleanUp() {
    	this.canvas.getController().getEventBroker().removeSubscriptions(this.canvasCallbackHandler);
    	this.canvas.removeCanvasItem(this);
    }

    public boolean containsPoint(Point2D point) {
        return false;
    }

    public Point2D getPosition() {
    	if(this.bounds == null) {
    		return pos;
    	}
        return new Point2D.Double(this.bounds.getCenterX(), this.bounds.getCenterY());
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return this.bounds;
    }
    
    private void updateBounds(Graphics2D g) {
    	Font oldFont = g.getFont();
    	g.setFont(MESSAGE_FONT);
        double cx = pos.getX();
        double cy = pos.getY();
        double width = g.getFontMetrics().stringWidth(this.message);
        double height = g.getFontMetrics().getHeight();
        this.bounds = new Rectangle2D.Double(cx -width/2, cy - height/2, width, height);
        g.setFont(oldFont);
    }
}
