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

import net.sourceforge.toscanaj.controller.fca.AbstractConceptInterpreter;
import net.sourceforge.toscanaj.util.gradients.Gradient;

import org.tockit.canvas.MovableCanvasItem;


public class SignificanceLegend extends MovableCanvasItem {
    private static final String TITLE = "Significance Levels";
    private static final double MARGIN_FACTOR = 1.1;
    
    private Font font;
    private Rectangle2D bounds;
    private Gradient gradient;
    
    public SignificanceLegend(Font font, Point2D pos, Gradient gradient) {
        this.font = font;
        this.gradient = gradient;
        this.bounds = new Rectangle2D.Double(pos.getX(), pos.getY(), 0, 0);
    }
    
    public void draw(Graphics2D g) {
        TextLayout titleLayout = new TextLayout(TITLE, this.font, g.getFontRenderContext());
        int numEntries = AbstractConceptInterpreter.SIGNIFICANCE_LEVELS.length;
        TextLayout[] numberLayouts = new TextLayout[numEntries];
        double maxWidth = 0;
        double height = 0;
        for (int i = 0; i < numEntries; i++) {
            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(3);
            format.setMaximumFractionDigits(6);
            numberLayouts[i] = new TextLayout(format.format(AbstractConceptInterpreter.SIGNIFICANCE_LEVELS[i]), this.font, g.getFontRenderContext());
            maxWidth = Math.max(maxWidth, numberLayouts[i].getVisibleAdvance());
            height += (numberLayouts[i].getAscent() + numberLayouts[i].getDescent() + numberLayouts[i].getLeading()) * MARGIN_FACTOR;
        }
		double titleHeight = (titleLayout.getAscent() + titleLayout.getDescent() + titleLayout.getLeading()) * MARGIN_FACTOR;
		double titleWidth = titleLayout.getAdvance() * MARGIN_FACTOR;
        
        double w = Math.max(titleWidth, height);
        double h = titleHeight + maxWidth * MARGIN_FACTOR;
        double x = this.bounds.getX();
        double y = this.bounds.getY();
        
        this.bounds.setRect(x, y, w, h);
        
        g.setPaint(Color.WHITE);
        g.fill(this.bounds);
        g.setPaint(Color.BLACK);
        titleLayout.draw(g, (float) (x + titleLayout.getAdvance() * (MARGIN_FACTOR - 1)/2), 
                            (float) y + titleLayout.getAscent());
        double yBelowTitle = y + titleHeight;
        g.translate(x, (y+h));
        g.rotate(-Math.PI/2);
        for (int i = 0; i < numberLayouts.length; i++) {
            TextLayout layout = numberLayouts[i];
			double curY = i * w / numEntries;
			double curHeight = w / numEntries;
			double curWidth = maxWidth * MARGIN_FACTOR;
			double gradPos = (i + 1) / (double)numEntries;                                        
			g.setPaint(gradient.getColor(0.5 - 0.5 * gradPos));
			g.fill(new Rectangle2D.Double(0, curY, curWidth/2, curHeight));
			g.setPaint(gradient.getColor(0.5 + 0.5 * gradPos));
			g.fill(new Rectangle2D.Double(curWidth/2, curY, curWidth/2, curHeight));
			g.setPaint(Color.BLACK);
            layout.draw(g, (float) (maxWidth * (MARGIN_FACTOR - 1)/2), (float) curY + layout.getAscent());
        }
        g.rotate(Math.PI/2);
        g.translate(-x, -(y+h));
        g.setPaint(Color.BLACK);
		g.draw(new Line2D.Double(x, yBelowTitle, x + w, yBelowTitle));
        g.draw(this.bounds);
    }

    public boolean containsPoint(Point2D point) {
        return this.bounds.contains(point);
    }

    public Point2D getPosition() {
        return new Point2D.Double(this.bounds.getX(), this.bounds.getY());
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return this.bounds;
    }

    public void setPosition(Point2D newPosition) {
        double px = newPosition.getX();
        double py = newPosition.getY();
        double w = this.bounds.getWidth();
        double h = this.bounds.getHeight();
        this.bounds.setRect(px, py, w, h);
    }
}
