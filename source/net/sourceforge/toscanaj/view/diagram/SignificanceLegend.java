/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import net.sourceforge.toscanaj.controller.fca.AbstractConceptInterperter;

import org.tockit.canvas.MovableCanvasItem;


public class SignificanceLegend extends MovableCanvasItem {
    private static final String TITLE = "Significance Levels";
    private static final double MARGIN_FACTOR = 1.1;
    
    private Font font;
    private Rectangle2D bounds;
    
    public SignificanceLegend(Font font, Point2D pos) {
        this.font = font;
        this.bounds = new Rectangle2D.Double(pos.getX(), pos.getY(), 0, 0);
    }
    
    public void draw(Graphics2D g) {
        TextLayout titleLayout = new TextLayout(TITLE, this.font, g.getFontRenderContext());
        int numEntries = AbstractConceptInterperter.SIGNIFICANCE_LEVELS.length;
        TextLayout[] numberLayouts = new TextLayout[numEntries];
        double maxWidth = 0;
        double height = 0;
        for (int i = 0; i < numEntries; i++) {
            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(3);
            format.setMaximumFractionDigits(6);
            numberLayouts[i] = new TextLayout(format.format(AbstractConceptInterperter.SIGNIFICANCE_LEVELS[i]), this.font, g.getFontRenderContext());
            maxWidth = Math.max(maxWidth, numberLayouts[i].getBounds().getWidth() * MARGIN_FACTOR);
            height += (numberLayouts[i].getAscent() + numberLayouts[i].getDescent() + numberLayouts[i].getLeading()) * MARGIN_FACTOR;
        }
        
        double w = Math.max(titleLayout.getBounds().getWidth() * MARGIN_FACTOR, height);
        double h = titleLayout.getBounds().getHeight() + maxWidth;
        double x = this.bounds.getCenterX() - w/2;
        double y = this.bounds.getCenterY() - h/2;
        
        this.bounds.setRect(x, y, w, h);
        
        g.setPaint(Color.WHITE);
        g.fill(this.bounds);
        g.setPaint(Color.BLACK);
        titleLayout.draw(g, (float) (x + titleLayout.getBounds().getWidth() * (MARGIN_FACTOR - 1)/2), 
                            (float) y + titleLayout.getAscent());
        double yBelowTitle = y + titleLayout.getAscent() + titleLayout.getDescent() + titleLayout.getLeading();
        g.draw(new Line2D.Double(x, yBelowTitle, x + w, yBelowTitle));
        g.translate(x, (y+h));
        g.rotate(-Math.PI/2);
        double curX = 0;
        for (int i = 0; i < numberLayouts.length; i++) {
            TextLayout layout = numberLayouts[i];
            layout.draw(g, (float) ((MARGIN_FACTOR - 1)/2), (float) curX + layout.getAscent());
            curX += (layout.getAscent() + layout.getDescent() + layout.getLeading()) * MARGIN_FACTOR;
        }
        g.rotate(Math.PI/2);
        g.translate(-x, -(y+h));
        g.setPaint(Color.BLACK);
        g.draw(this.bounds);
    }

    public boolean containsPoint(Point2D point) {
        return this.bounds.contains(point);
    }

    public Point2D getPosition() {
        return new Point2D.Double(this.bounds.getCenterX(), this.bounds.getCenterY());
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return this.bounds;
    }

    public void setPosition(Point2D newPosition) {
        double px = newPosition.getX();
        double py = newPosition.getY();
        double w = this.bounds.getWidth();
        double h = this.bounds.getHeight();
        this.bounds.setRect(px - w/2, py - h/2, w, h);
    }
}
