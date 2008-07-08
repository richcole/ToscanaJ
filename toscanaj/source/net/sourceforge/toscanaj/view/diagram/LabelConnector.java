/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.tockit.canvas.CanvasItem;

public class LabelConnector extends CanvasItem {

    private final LabelView labelView;
    private Line2D line;

    public LabelConnector(final LabelView labelView) {
        this.labelView = labelView;
    }

    @Override
    public void draw(final Graphics2D graphics) {
        this.labelView.updateBounds(graphics);
        updateLine();

        final Stroke oldStroke = graphics.getStroke();
        final Paint oldPaint = graphics.getPaint();

        final float[] dashstyle = { 4, 4 };
        graphics.setPaint(this.labelView.getConnectorColor());
        graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 1, dashstyle, 0));
        graphics.draw(this.line);

        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    private void updateLine() {
        if (this.labelView.isVisible()) {
            this.line = new Line2D.Double(this.labelView
                    .getConnectorStartPosition(), this.labelView
                    .getConnectorEndPosition());
        }
    }

    @Override
    public boolean containsPoint(final Point2D point) {
        return false;
    }

    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D graphics) {
        this.labelView.updateBounds(graphics);
        updateLine();
        if (this.labelView.isVisible()) {
            return this.line.getBounds2D();
        } else {
            return null;
        }
    }

    @Override
    public Point2D getPosition() {
        if (this.labelView.isVisible()) {
            return this.labelView.getPosition();
        } else {
            return null;
        }
    }
}
