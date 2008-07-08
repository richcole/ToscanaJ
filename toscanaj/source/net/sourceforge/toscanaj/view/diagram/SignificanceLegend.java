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

    private final Font font;
    private final Rectangle2D bounds;
    private final Gradient gradient;

    public SignificanceLegend(final Font font, final Point2D pos,
            final Gradient gradient) {
        this.font = font;
        this.gradient = gradient;
        this.bounds = new Rectangle2D.Double(pos.getX(), pos.getY(), 0, 0);
    }

    @Override
    public void draw(final Graphics2D g) {
        final TextLayout titleLayout = new TextLayout(TITLE, this.font, g
                .getFontRenderContext());
        final int numEntries = AbstractConceptInterpreter.SIGNIFICANCE_LEVELS.length;
        final TextLayout[] numberLayouts = new TextLayout[numEntries];
        double maxWidth = 0;
        double height = 0;
        for (int i = 0; i < numEntries; i++) {
            final NumberFormat format = NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(3);
            format.setMaximumFractionDigits(6);
            numberLayouts[i] = new TextLayout(format
                    .format(AbstractConceptInterpreter.SIGNIFICANCE_LEVELS[i]),
                    this.font, g.getFontRenderContext());
            maxWidth = Math.max(maxWidth, numberLayouts[i].getVisibleAdvance());
            height += (numberLayouts[i].getAscent()
                    + numberLayouts[i].getDescent() + numberLayouts[i]
                    .getLeading())
                    * MARGIN_FACTOR;
        }
        final double titleHeight = (titleLayout.getAscent()
                + titleLayout.getDescent() + titleLayout.getLeading())
                * MARGIN_FACTOR;
        final double titleWidth = titleLayout.getAdvance() * MARGIN_FACTOR;

        final double w = Math.max(titleWidth, height);
        final double h = titleHeight + maxWidth * MARGIN_FACTOR;
        final double x = this.bounds.getX();
        final double y = this.bounds.getY();

        this.bounds.setRect(x, y, w, h);

        g.setPaint(Color.WHITE);
        g.fill(this.bounds);
        g.setPaint(Color.BLACK);
        titleLayout
                .draw(g, (float) (x + titleLayout.getAdvance()
                        * (MARGIN_FACTOR - 1) / 2), (float) y
                        + titleLayout.getAscent());
        final double yBelowTitle = y + titleHeight;
        g.translate(x, (y + h));
        g.rotate(-Math.PI / 2);
        for (int i = 0; i < numberLayouts.length; i++) {
            final TextLayout layout = numberLayouts[i];
            final double curY = i * w / numEntries;
            final double curHeight = w / numEntries;
            final double curWidth = maxWidth * MARGIN_FACTOR;
            final double gradPos = (i + 1) / (double) numEntries;
            g.setPaint(gradient.getColor(0.5 - 0.5 * gradPos));
            g.fill(new Rectangle2D.Double(0, curY, curWidth / 2, curHeight));
            g.setPaint(gradient.getColor(0.5 + 0.5 * gradPos));
            g.fill(new Rectangle2D.Double(curWidth / 2, curY, curWidth / 2,
                    curHeight));
            g.setPaint(Color.BLACK);
            layout.draw(g, (float) (maxWidth * (MARGIN_FACTOR - 1) / 2),
                    (float) curY + layout.getAscent());
        }
        g.rotate(Math.PI / 2);
        g.translate(-x, -(y + h));
        g.setPaint(Color.BLACK);
        g.draw(new Line2D.Double(x, yBelowTitle, x + w, yBelowTitle));
        g.draw(this.bounds);
    }

    @Override
    public boolean containsPoint(final Point2D point) {
        return this.bounds.contains(point);
    }

    @Override
    public Point2D getPosition() {
        return new Point2D.Double(this.bounds.getX(), this.bounds.getY());
    }

    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D g) {
        return this.bounds;
    }

    @Override
    public void setPosition(final Point2D newPosition) {
        final double px = newPosition.getX();
        final double py = newPosition.getY();
        final double w = this.bounds.getWidth();
        final double h = this.bounds.getHeight();
        this.bounds.setRect(px, py, w, h);
    }
}
