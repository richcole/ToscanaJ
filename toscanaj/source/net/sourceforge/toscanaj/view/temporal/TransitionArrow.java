/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.temporal;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.ExtraCanvasItemFactory;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import org.jdom.Element;
import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.geom.*;

public class TransitionArrow extends CanvasItem implements XMLizable {
    private static class Factory implements ExtraCanvasItemFactory {
        public CanvasItem createCanvasItem(SimpleLineDiagram diagram, Element element) throws XMLSyntaxError {
            final TransitionArrow retVal = new TransitionArrow();
            retVal.timeController = new AnimationTimeController(
                    Double.MAX_VALUE, 0, Double.MAX_VALUE, 0, 1);
            retVal.timeController.setCurrentTime(Double.MAX_VALUE / 2);
            retVal.startNode = diagram.getNode(element.getAttributeValue("from"));
            retVal.endNode = diagram.getNode(element.getAttributeValue("to"));
            // we used to have just a single offset, so we keep parsing that
            Element offsetElem = element.getChild("offset");
            if (offsetElem != null) {
                double offsetX = Double.parseDouble(offsetElem.getAttributeValue("x"));
                double offsetY = Double.parseDouble(offsetElem.getAttributeValue("y"));
                retVal.manualStartOffset = new Point2D.Double(offsetX, offsetY);
                retVal.manualEndOffset = new Point2D.Double(offsetX, offsetY);
            } else {
                offsetElem = element.getChild("startOffset");
                double offsetX = Double.parseDouble(offsetElem.getAttributeValue("x"));
                double offsetY = Double.parseDouble(offsetElem.getAttributeValue("y"));
                retVal.manualStartOffset = new Point2D.Double(offsetX, offsetY);
                offsetElem = element.getChild("endOffset");
                if(offsetElem != null) {
                    offsetX = Double.parseDouble(offsetElem.getAttributeValue("x"));
                    offsetY = Double.parseDouble(offsetElem.getAttributeValue("y"));
                } else {
                    // workaround for bug where files were saved without the endOffset element
                    offsetX = 0;
                    offsetY = 0;
                }
                retVal.manualEndOffset = new Point2D.Double(offsetX, offsetY);
            }
            Element labelOffsetElem = element.getChild("labelOffset");
            if (labelOffsetElem != null) {
                double offsetX = Double.parseDouble(offsetElem.getAttributeValue("x"));
                double offsetY = Double.parseDouble(offsetElem.getAttributeValue("y"));
                retVal.labelOffset = new Point2D.Double(offsetX, offsetY);
            }
            if (element.getAttributeValue("arrowStyle") != null) {
                retVal.style = DiagramSchema.getCurrentSchema()
                        .getArrowStyles()[XMLHelper.getIntAttribute(element,
                        "arrowStyle")];
            } else {
                // just to keep parsing older file formats, even though not
                // correct
                retVal.style = DiagramSchema.getCurrentSchema().getArrowStyles()[0];
            }
            retVal.updateShiftVector();
            retVal.calculateBounds();

            return retVal;
        }
    }

    public static void registerFactory() {
        SimpleLineDiagram.registerExtraCanvasItemFactory("transitionArrow", new Factory());
    }

    protected DiagramNode startNode;
    protected DiagramNode endNode;
    protected Rectangle2D bounds = new Rectangle2D.Double();
    protected Point2D startPoint = new Point2D.Double();
    protected Point2D endPoint = new Point2D.Double();
    protected Point2D shiftVector = new Point2D.Double();
    protected Point2D manualStartOffset = new Point2D.Double();
    protected Point2D manualEndOffset = new Point2D.Double();
    protected Point2D labelOffset = new Point2D.Double();
    protected double timePos;
    protected AnimationTimeController timeController;
    protected ArrowStyle style;

    private Shape currentShape;

    public TransitionArrow(DiagramNode startNode, DiagramNode endNode, ArrowStyle style,
            double timePos, AnimationTimeController timeController) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.style = style;
        this.timePos = timePos;
        this.timeController = timeController;

        updateShiftVector();
        calculateBounds();
    }

    private TransitionArrow() {
        // should be created through factory
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.startNode == this.endNode) {
            return;
        }

        calculateBounds();

        final double startX = this.startPoint.getX();
        final double startY = this.startPoint.getY();
        final double endX = this.endPoint.getX();
        final double endY = this.endPoint.getY();

        final Paint paint = calculatePaint(this.style.getColor());
        if (paint == null) { // nothing to draw
            this.currentShape = null;
            return;
        }

        updateShiftVector();

        final Paint oldPaint = g.getPaint();
        final Stroke oldStroke = g.getStroke();

        final float length = (float) this.startPoint.distance(this.endPoint);
        final Shape arrow = getArrowShape(this.style, length);

        final AffineTransform shapeTransform = new AffineTransform();
        shapeTransform.translate(endX, endY);
        shapeTransform.rotate(Math.atan2(endY - startY, endX - startX));
        this.currentShape = shapeTransform.createTransformedShape(arrow);

        g.setPaint(paint);
        g.fill(this.currentShape);
        if (this.style.getBorderWidth() != 0) {
            g.setStroke(new BasicStroke(this.style.getBorderWidth()));
            g.setPaint(calculatePaint(Color.BLACK));
            g.draw(this.currentShape);
        }

        g.setStroke(oldStroke);
        g.setPaint(oldPaint);
    }

    public static Shape getArrowShape(final ArrowStyle style,
            final double length) {
        final float headLength = (float) style.getHeadLength();
        final float headWidth = (float) style.getHeadWidth();

        // @todo figure out why things don't match up -- at the moment there is
        // only this hack to fixz
        // the major issues
        Shape line = style.getStroke().createStrokedShape(
                new Line2D.Double(-length, 0, -headLength, 0));
        final double diff = headLength + line.getBounds().getMaxX();
        if (diff > 0) {
            line = style.getStroke().createStrokedShape(
                    new Line2D.Double(-length, 0, -headLength - diff, 0));
        }

        final GeneralPath arrow = new GeneralPath(line);
        arrow.moveTo(-headLength, -headWidth / 2);
        arrow.lineTo(0, 0);
        arrow.lineTo(-headLength, headWidth / 2);
        arrow.closePath();
        return arrow;
    }

    protected Paint calculatePaint(final Color baseColor) {
        final AnimationTimeController controller = this.timeController;

        final double timeOffset = controller.getCurrentTime() - this.timePos;
        double alpha;
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

        return new Color(baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                (int) (alpha * baseColor.getAlpha())
        );
    }

    @Override
    public boolean containsPoint(final Point2D point) {
        return this.currentShape != null && this.currentShape.contains(point);
    }

    @Override
    public Point2D getPosition() {
        return new Point2D.Double(this.bounds.getCenterX(), this.bounds
                .getCenterY());
    }

    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D g) {
        // we need to update in case one of the nodes has moved
        // @todo try finding something better
        calculateBounds();
        return this.bounds;
    }

    protected void calculateBounds() {
        double startX = this.startNode.getPosition().getX()
                + this.shiftVector.getX() + this.manualStartOffset.getX();
        double startY = this.startNode.getPosition().getY()
                + this.shiftVector.getY() + this.manualStartOffset.getY();
        double endX = this.endNode.getPosition().getX()
                + this.shiftVector.getX() + this.manualEndOffset.getX();
        double endY = this.endNode.getPosition().getY()
                + this.shiftVector.getY() + this.manualEndOffset.getY();

        final double dx = endX - startX;
        final double dy = endY - startY;

        if (dx == 0) {
            startY += this.startNode.getRadiusY() * signum(dy);
            endY -= this.endNode.getRadiusY() * signum(dy);
        } else {
            final double angle = Math.atan2(dy, dx);
            startX += this.startNode.getRadiusX() * Math.cos(angle);
            startY += this.startNode.getRadiusY() * Math.sin(angle);
            endX -= this.endNode.getRadiusX() * Math.cos(angle);
            endY -= this.endNode.getRadiusY() * Math.sin(angle);
        }

        this.startPoint.setLocation(startX, startY);
        this.endPoint.setLocation(endX, endY);

        double x, y, width, height;
        if (endX > startX) {
            x = startX;
            width = endX - startX;
        } else {
            x = endX;
            width = startX - endX;
        }
        if (endY > startY) {
            y = startY;
            height = endY - startY;
        } else {
            y = endY;
            height = startY - endY;
        }

        this.bounds.setFrame(x, y, width, height);
    }

    private double signum(final double dy) {
        return dy > 0 ? 1 : -1;
    }

    private void updateShiftVector() {
        final double xDiff = this.endNode.getPosition().getX()
                - this.startNode.getPosition().getX();
        final double yDiff = this.endNode.getPosition().getY()
                - this.startNode.getPosition().getY();
        final double shiftDist = 10.0;
        final double factor = shiftDist
                / Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        this.shiftVector = new Point2D.Double(yDiff * factor, -xDiff * factor);
    }

    public boolean pointIsInHeadArea(final Point2D point) {
        return point.distance(this.endPoint) < this.startPoint
                .distance(this.endPoint) / 4;
    }

    public boolean pointIsInTailArea(final Point2D point) {
        return point.distance(this.startPoint) < this.endPoint
                .distance(this.startPoint) / 4;
    }

    public void shiftPosition(final double dx, final double dy) {
        shiftStartPoint(dx, dy);
        shiftEndPoint(dx, dy);
    }

    public void shiftStartPoint(final double dx, final double dy) {
        this.manualStartOffset.setLocation(this.manualStartOffset.getX() + dx,
                this.manualStartOffset.getY() + dy);
    }

    public void shiftEndPoint(final double dx, final double dy) {
        this.manualEndOffset.setLocation(this.manualEndOffset.getX() + dx,
                this.manualEndOffset.getY() + dy);
    }

    public Point2D getLabelOffset() {
        return labelOffset;
    }

    public void setLabelOffset(Point2D labelOffset) {
        this.labelOffset = labelOffset;
    }

    public void setLabelOffset(double x, double y) {
        setLabelOffset(new Point2D.Double(x, y));
    }

    public Element toXML() {
        final Element result = new Element(getTagName());
        result.setAttribute("from", this.startNode.getIdentifier());
        result.setAttribute("to", this.endNode.getIdentifier());
        Element offsetElem = new Element("startOffset");
        offsetElem.setAttribute("x", String.valueOf(this.manualStartOffset.getX()));
        offsetElem.setAttribute("y", String.valueOf(this.manualStartOffset.getY()));
        result.addContent(offsetElem);
        offsetElem = new Element("endOffset");
        offsetElem.setAttribute("x", String.valueOf(this.manualEndOffset.getX()));
        offsetElem.setAttribute("y", String.valueOf(this.manualEndOffset.getY()));
        result.addContent(offsetElem);
        offsetElem = new Element("labelOffset");
        offsetElem.setAttribute("x", String.valueOf(this.labelOffset.getX()));
        offsetElem.setAttribute("y", String.valueOf(this.labelOffset.getY()));
        result.addContent(offsetElem);
        for (int i = 0; i < DiagramSchema.getCurrentSchema().getArrowStyles().length; i++) {
            if (this.style == DiagramSchema.getCurrentSchema().getArrowStyles()[i]) {
                result.setAttribute("arrowStyle", String.valueOf(i));
            }
        }
        return result;
    }

    protected String getTagName() {
        return "transitionArrow";
    }

    public void readXML(final Element elem) {
        // done in Factory -- can't be done without access to diagram
    }
}
