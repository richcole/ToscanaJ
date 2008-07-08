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
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.canvas.CanvasItem;
import org.tockit.util.ColorStringConverter;

public class StateRing extends CanvasItem implements XMLizable {
    private static class Factory implements ExtraCanvasItemFactory {
        public CanvasItem createCanvasItem(final SimpleLineDiagram diagram,
                final Element element) {
            System.out.println("Here SR");
            return null;
        }
    }

    public static void registerFactory() {
        SimpleLineDiagram.registerExtraCanvasItemFactory("stateRing",
                new Factory());
    }

    private static final double EXTRA_RADIUS = 2;

    private final DiagramNode node;
    private final Color baseColor;
    private final double timePos;
    private final AnimationTimeController timeController;

    public StateRing(final DiagramNode node, final Color color,
            final double timePos, final AnimationTimeController timeController) {
        this.node = node;
        this.baseColor = color;
        this.timePos = timePos;
        this.timeController = timeController;
    }

    @Override
    public void draw(final Graphics2D g) {
        final Paint color = calculatePaint();
        if (color == null) { // nothing to draw
            return;
        }

        final Paint oldPaint = g.getPaint();

        final Rectangle2D bounds = getCanvasBounds(g);
        final Ellipse2D ellipse = new Ellipse2D.Double(bounds.getX(), bounds
                .getY(), bounds.getWidth(), bounds.getHeight());

        g.setPaint(color);
        g.fill(ellipse);

        g.setPaint(oldPaint);
    }

    private Paint calculatePaint() {
        final AnimationTimeController controller = this.timeController;
        final double timeOffset = controller.getCurrentTime() - this.timePos;
        double alpha = 0;
        if (timeOffset < -controller.getFadeInTime()) {
            return null;
        } else if (timeOffset < 0) {
            alpha = 1 + timeOffset / controller.getFadeInTime();
        } else if (timeOffset < controller.getVisibleTime()) {
            alpha = 1;
        } else if (timeOffset < controller.getVisibleTime()
                + controller.getFadeOutTime()) {
            alpha = 1 - (timeOffset - controller.getVisibleTime())
                    / controller.getFadeOutTime();
        } else {
            return null;
        }
        return new Color(this.baseColor.getRed(), this.baseColor.getGreen(),
                this.baseColor.getBlue(), (int) (alpha * this.baseColor
                        .getAlpha()));
    }

    @Override
    public boolean containsPoint(final Point2D point) {
        return false;
    }

    @Override
    public Point2D getPosition() {
        return this.node.getPosition();
    }

    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D g) {
        final Point2D center = this.node.getPosition();
        final double x = center.getX();
        final double y = center.getY();
        final double rx = this.node.getRadiusX() + EXTRA_RADIUS;
        final double ry = this.node.getRadiusY() + EXTRA_RADIUS;
        return new Rectangle2D.Double(x - rx, y - ry, 2 * rx, 2 * ry);
    }

    public Element toXML() {
        final Element result = new Element("stateRing");
        result.setAttribute("nodeView", this.node.getIdentifier());
        result.setAttribute("color", ColorStringConverter
                .colorToString(this.baseColor));
        return result;
    }

    public void readXML(final Element elem) {
        // @TODO
    }
}
