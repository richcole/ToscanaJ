package net.sourceforge.toscanaj.canvas;

import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import javax.swing.JComponent;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * DrawingCanvas controls all the updating of CanvasItems contained in a DiagramView
 * ie NodeView, LineView, and LabelView
 *
 * @TODO For now the mouse events on the canvas are dealt with here, but shall be handled by
 * a separate class eventually (CanvasController)
 */

public class DrawingCanvas extends JComponent implements MouseListener, MouseMotionListener, Printable {
    /**
     * Used to indicate that no graphic format has been set.
     */
    static public final int FORMAT_UNSET = 0;

    /**
     * Used to indicate PNG format.
     */
    static public final int FORMAT_PNG = 1;

    /**
     * Used to indicate JPG format.
     */
    static public final int FORMAT_JPG = 2;

    /**
     * Used to indicate SVG format.
     */
    static public final int FORMAT_SVG = 3;

    /**
     * A list of all canvas items to draw.
     */
    protected List canvasItems = new LinkedList();

    /**
     * Stores the drawing context used for scaling.
     *
     * This is created any time the diagram is drawn and is needed for mapping
     * a point on the screen back into the coordinate system for the diagram
     * whenever a mouse event occurs.
     */
    protected ToscanajGraphics2D graphics = null;

    /**
     * Flag to prevent label from being moved when just clicked on
     */
    private boolean dragMode = false;

    /**
     * Distance that label has to be moved to enable dragMode
     */
    private int dragMin = 5;

    /**
     * The position where the mouse was when the last event came.
     */
    private Point2D lastMousePos = null;

    /**
     * Holds the selected CanvasItem
     * that the user has clicked on with intent to move
     */
    private CanvasItem selectedCanvasItem = null;

    /**
     * Paints the CanvasItems in the diagram.
     */
    public void paintCanvasItems(ToscanajGraphics2D graphics)
    {
        // fill the background
        /// @TODO Rename this method (it draws not only the items)
        /// @TODO Make Background color configurable
        Graphics2D graphics2D = graphics.getGraphics2D();
        graphics2D.setPaint(this.getBackground());
        graphics2D.fillRect(0,0,this.getWidth(), this.getHeight());
        graphics2D.setPaint(java.awt.Color.black);

        // paint all items on canvas
        Iterator it = this.canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            cur.draw(graphics);
        }
    }

    /**
     * Implements Printable.print(Graphics, PageFormat, int).
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if(pageIndex == 0) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            // set top left corner
            graphics2D.translate(pageFormat.getImageableX(),pageFormat.getImageableY());

            // scale to just fit the page
            double xScale = pageFormat.getImageableWidth()/this.getWidth();
            double yScale = pageFormat.getImageableHeight()/this.getHeight();
            double scale;
            if(xScale < yScale) {
                scale = xScale;
            }
            else {
                scale = yScale;
            }
            graphics2D.scale(scale,scale);

            ToscanajGraphics2D tgraphics = new ToscanajGraphics2D( graphics2D,
                                                                   this.graphics.getOffset(),
                                                                   this.graphics.getXScaling(),
                                                                   this.graphics.getYScaling() );

            // paint all items on canvas
            paintCanvasItems(tgraphics);

            return PAGE_EXISTS;
        }
        else {
            return NO_SUCH_PAGE;
        }
    }

    /**
     * Not used -- mouse clicks are handled as press/release combinations.
     */
    public void mouseClicked(MouseEvent e){
    }

    /**
     * Handles mouse release events.
     *
     * Resets the diagram from dragging mode back into normal mode or calls
     * clicked() or doubleClicked() on the CanvasItem hit.
     */
    public void mouseReleased(MouseEvent e) {
        if(dragMode) {
            dragMode = false;
        }
        else {
            if(selectedCanvasItem == null) {
                if(e.getClickCount() == 1) {
                    backgroundClicked(e.getPoint());
                }
                else if(e.getClickCount() == 2) {
                    backgroundDoubleClicked(e.getPoint());
                }
                return;
            }
            if(e.getClickCount() == 1) {
                selectedCanvasItem.clicked(e.getPoint());
            }
            else if(e.getClickCount() == 2) {
                selectedCanvasItem.doubleClicked(e.getPoint());
            }
        }
        selectedCanvasItem = null;
    }

    /**
     * Not used.
     */
    public void mouseEntered(MouseEvent e) {
      //System.out.println("mouseEntered");
    }

    /**
     * Not used.
     */
    public void mouseExited(MouseEvent e) {
      //System.out.println("mouseExited");
    }

    /**
     * Handles dragging the canvas items.
     */
    public void mouseDragged(MouseEvent e) {
        if(selectedCanvasItem == null) {
            return;
        }
        if(!this.contains(e.getPoint())) {
            return;
        }
        if(!dragMode && (getDistance(lastMousePos.getX(), lastMousePos.getY(), e.getX(), e.getY()) >= dragMin)) {
            dragMode = true;
        }
        if(dragMode) {
            selectedCanvasItem.moveBy(graphics.inverseScaleX(e.getX() - lastMousePos.getX()),
                                 graphics.inverseScaleY(e.getY() - lastMousePos.getY()));
            lastMousePos = new Point2D.Double(e.getX(), e.getY());
        }
    }

    /**
     * Not used.
     */
    public void mouseMoved(MouseEvent e) {
      //System.out.println("mouseMoved");
    }

    /**
     * Finds, raises and stores the canvas item hit.
     */
    public void mousePressed(MouseEvent e) {
        ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while(it.hasPrevious()) {
            CanvasItem cur = (CanvasItem) it.previous();
            Point2D point = this.graphics.inverseProject(e.getPoint());
            if(cur.containsPoint(point)) {
                // store the CanvasItem hit
                this.selectedCanvasItem = cur;
                this.lastMousePos = e.getPoint();
                if(cur.hasAutoRaise()) {
                    // raise the item
                    it.remove();
                    this.canvasItems.add(cur);
                    // redraw the raised item (needed if it will not be moved)
                    repaint();
                }
                break;
            }
        }
    }

    /**
     * Calculates the distance between the two points.
     */
    private double getDistance(double x1, double y1, double x2, double y2){
      return Math.abs(Math.sqrt(sqr(x2 - x1) + sqr(y2 - y1)));
    }

    /**
     * Returns the square of the input.
     */
    private double sqr(double x) {
      return x * x;
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
     * Returns the graphic context used.
     */
    public ToscanajGraphics2D getToscanaGraphics() {
        return this.graphics;
    }

    /**
     * This callback will be executed when the background was clicked.
     *
     * This can be overwritten in subclasses to get effects, the default
     * implementation does nothing.
     */
    protected void backgroundClicked(Point2D point) {
    }

    /**
     * This callback will be executed when the background was double-clicked.
     *
     * This can be overwritten in subclasses to get effects, the default
     * implementation does nothing.
     */
    protected void backgroundDoubleClicked(Point2D point) {
    }
}