/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas;

import net.sourceforge.toscanaj.canvas.controller.CanvasController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Canvas controls all the updating of CanvasItems contained in a DiagramView
 * ie NodeView, LineView, and LabelView
 *
 * @TODO For now the mouse events on the canvas are dealt with here, but shall be handled by
 * a separate class eventually (CanvasController)
 */

public class Canvas extends JComponent implements Printable {

    /**
     * A list of all canvas items to draw.
     */
    protected List canvasItems = new LinkedList();

    /**
     * Stores the transformation matrix we used on the last draw event.
     *
     * This is used to map mouse positions to the canvas coordinates.
     */
    private AffineTransform screenTransform = new AffineTransform();

    /**
     * Keeps the size of the canvas.
     *
     * This is done to avoid resizing while the mouse is dragged.
     */
    private Rectangle2D canvasSize = null;

    private Paint backgroundPaint = null;

    private CanvasController controller = null;

    public Canvas() {
        // for now we just attach a default controller
        this.controller = new CanvasController(this);
    }

    public CanvasController getController() {
        return controller;
    }

    /**
     * Paints the canvas including all CanvasItems on it.
     */
    public void paintCanvas(Graphics2D graphics) {
        // paint all items on canvas
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            cur.draw(graphics);
        }
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public void setScreenTransform(AffineTransform transform) {
        this.screenTransform = transform;
    }

    public AffineTransform getScreenTransform() {
        return screenTransform;
    }


    /**
     * Calculates the size of this canvas on a specific drawing context.
     *
     * This is the smallest upright rectangle that contains all canvas items.
     */
    public Rectangle2D getCanvasSize(Graphics2D graphics) {
        Iterator it = this.canvasItems.iterator();
        if (!it.hasNext()) {
            return new Rectangle2D.Double(0, 0, 0, 0);
        }
        CanvasItem cur = (CanvasItem) it.next();
        Rectangle2D retVal = cur.getCanvasBounds(graphics);
        while (it.hasNext()) {
            cur = (CanvasItem) it.next();
            retVal = retVal.createUnion(cur.getCanvasBounds(graphics));
        }
        return retVal;
    }

    /**
     * Implements Printable.print(Graphics, PageFormat, int).
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex == 0) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            Rectangle2D bounds = new Rectangle2D.Double(
                    pageFormat.getImageableX(),
                    pageFormat.getImageableY(),
                    pageFormat.getImageableWidth(),
                    pageFormat.getImageableHeight());
            AffineTransform transform = scaleToFit(graphics2D, bounds);
            graphics2D.transform(transform);
            // paint all items on canvas
            paintCanvas(graphics2D);

            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

    /**
     * Scales the graphic context in which the canvas items will be completely
     * visible in the rectangle.
     */
    public AffineTransform scaleToFit(Graphics2D graphics2D, Rectangle2D bounds) {
        this.canvasSize = this.getCanvasSize(graphics2D);

        // we need some values to do the projection -- the initial values are
        // centered and no size change. This is useful if the canvas has no
        // extent in a direction
        double xOrigin = bounds.getX() + bounds.getWidth() / 2;
        double yOrigin = bounds.getY() + bounds.getHeight() / 2;
        double xScale = 1;
        double yScale = 1;

        if (canvasSize.getWidth() != 0) {
            xScale = bounds.getWidth() / canvasSize.getWidth();
            xOrigin = bounds.getX() / xScale - canvasSize.getX();
        }
        if (canvasSize.getHeight() != 0) {
            yScale = bounds.getHeight() / canvasSize.getHeight();
            yOrigin = bounds.getY() / yScale - canvasSize.getY();
        }

        // scale proportionally, add half of the possible difference to the offset to center
        if ((canvasSize.getWidth() != 0) && (canvasSize.getHeight() != 0)) {
            if (xScale > yScale) {
                xScale = yScale;
                xOrigin = xOrigin + (bounds.getWidth() / xScale - canvasSize.getWidth()) / 2;
            } else {
                yScale = xScale;
                yOrigin = yOrigin + (bounds.getHeight() / yScale - canvasSize.getHeight()) / 2;
            }
        }
        AffineTransform transform = AffineTransform.getScaleInstance(xScale, yScale);
        transform.concatenate(AffineTransform.getTranslateInstance(xOrigin, yOrigin));
        return transform;
    }

    public CanvasItem getCanvasItemAt(Point2D point) {
        ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while (it.hasPrevious()) {
            CanvasItem cur = (CanvasItem) it.previous();
            if (cur.containsPoint(point)) {
                if (cur.hasAutoRaise()) {
                    // raise and repaint the item
                    it.remove();
                    this.canvasItems.add(cur);
                    repaint();
                }
                return cur;
            }
        }
        return null;
    }

    public Point2D getCanvasCoordinates(Point2D screenPos) {
        Point2D point = null;
        try {
            point = this.screenTransform.inverseTransform(screenPos, null);
        } catch (Exception ex) {
            //this should not happen
            ex.printStackTrace();
            throw new RuntimeException("Internal error: noninvertible transformation found.");
        }
        return point;
    }

    /**
     * Removes all canvas items from the canvas.
     */
    public void clearCanvas() {
        canvasItems.clear();
    }

    /**
     * Adds a canvas item to the canvasItem list.
     *
     */
    public void addCanvasItem(CanvasItem node) {
        this.canvasItems.add(node);
    }

    /**
     * This callback will be executed when the background was clicked.
     *
     * This can be overwritten in subclasses to get effects, the default
     * implementation does nothing. There is no distinction between single
     * and double clicks, overwrite backgroundSingleClicked(Point2D) and
     * backgroundDoubleClicked(Point2D) if you need this.
     */
    public void backgroundClicked(Point2D point) {
    }

    /**
     * This callback will be executed when the background was clicked once.
     *
     * This can be overwritten in subclasses to get effects, the default
     * implementation does nothing.
     */
    public void backgroundSingleClicked(Point2D point) {
    }

    /**
     * This callback will be executed when the background was double-clicked.
     *
     * This can be overwritten in subclasses to get effects, the default
     * implementation does nothing.
     */
    public void backgroundDoubleClicked(Point2D point) {
    }

    /**
     * A callback for showing context menus on the background.
     */
    public void openBackgroundPopupMenu(Point2D canvasPosition, Point2D screenPosition) {
    }
}
