package net.sourceforge.toscanaj.canvas;

import net.sourceforge.toscanaj.view.diagram.DiagramSchema;

import javax.swing.JComponent;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DrawingCanvas controls all the updating of CanvasItems contained in a DiagramView
 * ie NodeView, LineView, and LabelView
 *
 * @TODO For now the mouse events on the canvas are dealt with here, but shall be handled by
 * a separate class eventually (CanvasController)
 */

public class DrawingCanvas extends JComponent implements MouseListener, MouseMotionListener, Printable {
    /**
     * Sends a click event to a canvas item.
     *
     * @see DrawingCanvas.mouseReleaseEvent(MouseEvent)
     */
    private class CanvasItemClickTask extends TimerTask {
        /**
         * The message recipient.
         */
        private CanvasItem target;
        /**
         * The position transmitted with the message.
         */
        private Point2D point;
        /**
         * Creates a new task for sending a message.
         */
        public CanvasItemClickTask(CanvasItem target, Point2D point) {
            this.target = target;
            this.point = point;
        }
        /**
         * Sends the message.
         */
        public void run() {
            target.clicked(point);
        }
    }

    /**
     * Sends a background click event to a canvas.
     *
     * @see DrawingCanvas.mouseReleaseEvent(MouseEvent)
     */
    private class BackgroundClickTask extends TimerTask {
        /**
         * The message recipient.
         */
        private DrawingCanvas target;
        /**
         * The position transmitted with the message.
         */
        private Point2D point;
        /**
         * Creates a new task for sending a message.
         */
        public BackgroundClickTask(DrawingCanvas target, Point2D point) {
            this.target = target;
            this.point = point;
        }
        /**
         * Sends the message.
         */
        public void run() {
            target.backgroundClicked(point);
        }
    }

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
     * Stores the transformation matrix we used on the last draw event.
     *
     * This is used to map mouse positions to the canvas coordinates.
     */
    private AffineTransform transform = null;

    /**
     * Keeps the size of the canvas.
     *
     * This is done to avoid resizing while the mouse is dragged.
     */
    private Rectangle2D canvasSize = null;

    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer doubleClickTimer = null;

    /**
     * Just adds the listeners we need to work.
     */
    public DrawingCanvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Paints the canvas including all CanvasItems on it.
     */
    public void paintCanvas(Graphics2D graphics)
    {
        // fill the background
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        graphics.setPaint(diagramSchema.getBackground());
        graphics.fill(this.getCanvasSize(graphics));
        graphics.setPaint(diagramSchema.getForeground());

        // paint all items on canvas
        Iterator it = this.canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            cur.draw(graphics);
        }

        // store affine transformation matrix for mapping mouse positions
        this.transform = graphics.getTransform();
    }

    /**
     * Calculates the size of this canvas on a specific drawing context.
     *
     * This is the smallest upright rectangle that contains all canvas items.
     */
    public Rectangle2D getCanvasSize(Graphics2D graphics) {
        Iterator it = this.canvasItems.iterator();
        if(!it.hasNext()) {
            return new Rectangle2D.Double(0,0,0,0);
        }
        CanvasItem cur = (CanvasItem) it.next();
        Rectangle2D retVal = cur.getCanvasBounds(graphics);
        while(it.hasNext()) {
            cur = (CanvasItem) it.next();
            retVal = retVal.createUnion(cur.getCanvasBounds(graphics));
        }
        return retVal;
    }

    /**
     * Implements Printable.print(Graphics, PageFormat, int).
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if(pageIndex == 0) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            Rectangle2D bounds = new Rectangle2D.Double(
                                     pageFormat.getImageableX(),
                                     pageFormat.getImageableY(),
                                     pageFormat.getImageableWidth(),
                                     pageFormat.getImageableHeight() );
            scaleToFit(graphics2D,bounds);

            // paint all items on canvas
            paintCanvas(graphics2D);

            return PAGE_EXISTS;
        }
        else {
            return NO_SUCH_PAGE;
        }
    }

    /**
     * Scales the graphic context in which the canvas items will be completely
     * visible in the rectangle.
     */
    public void scaleToFit(Graphics2D graphics2D, Rectangle2D bounds) {
        if(!dragMode) {
            // get the dimensions of the canvas
            this.canvasSize = this.getCanvasSize(graphics2D);
        }

        // we need some values to do the projection -- the initial values are
        // centered and no size change. This is useful if the canvas has no
        // extent in a direction
        double xOrigin = bounds.getX() + bounds.getWidth()/2;
        double yOrigin = bounds.getY() + bounds.getHeight()/2;
        double xScale = 1;
        double yScale = 1;

        if( canvasSize.getWidth() != 0 )
        {
            xScale = bounds.getWidth() / canvasSize.getWidth();
            xOrigin = bounds.getX()/xScale - canvasSize.getX();
        }
        if( canvasSize.getHeight() != 0 )
        {
            yScale = bounds.getHeight() / canvasSize.getHeight();
            yOrigin = bounds.getY()/yScale - canvasSize.getY();
        }

        // scale proportionally, add half of the possible difference to the offset to center
        if( (canvasSize.getWidth()!=0) && (canvasSize.getHeight()!=0) ) {
            if(xScale > yScale) {
                xScale = yScale;
                xOrigin = xOrigin + ( bounds.getWidth()/xScale - canvasSize.getWidth() ) / 2;
            }
            else {
                yScale = xScale;
                yOrigin = yOrigin + ( bounds.getHeight()/yScale - canvasSize.getHeight() ) / 2;
            }
        }

        // reposition / rescale
        graphics2D.scale(xScale, yScale);
        graphics2D.translate(xOrigin, yOrigin);
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
     * clicked() or doubleClicked() on the CanvasItem hit. If no item was hit
     * backgroundClicked() or backgroundDoubleClicked() on the canvas is
     * called.
     *
     * Clicked() will only be send if it is not a double click.
     *
     * @TODO Use system double click timing instead of hard-coded 200ms
     */
    public void mouseReleased(MouseEvent e) {
        if(dragMode) {
            dragMode = false;
            repaint();
        }
        else {
            if(selectedCanvasItem == null) {
                if(e.getClickCount() == 1) {
                    this.doubleClickTimer = new Timer();
                    this.doubleClickTimer.schedule( new BackgroundClickTask( this, e.getPoint() ),  200 );
                }
                else if(e.getClickCount() == 2) {
                    this.doubleClickTimer.cancel();
                    backgroundDoubleClicked(e.getPoint());
                }
                return;
            }
            if(e.getClickCount() == 1) {
                this.doubleClickTimer = new Timer();
                this.doubleClickTimer.schedule( new CanvasItemClickTask( this.selectedCanvasItem, e.getPoint() ),  200 );
            }
            else if(e.getClickCount() == 2) {
                this.doubleClickTimer.cancel();
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
        if(!dragMode && (lastMousePos.distance(e.getPoint()) >= dragMin)) {
            dragMode = true;
        }
        if(dragMode) {
            Point2D mousePosTr = null;
            Point2D lastMousePosTr = null;
            try {
                mousePosTr = (Point2D)this.transform.inverseTransform(e.getPoint(), null);
                lastMousePosTr = (Point2D)this.transform.inverseTransform(lastMousePos, null);
            }
            catch (Exception ex ) {
                //this should not happen
                ex.printStackTrace();
            }
            selectedCanvasItem.moveBy( mousePosTr.getX() - lastMousePosTr.getX(),
                                       mousePosTr.getY() - lastMousePosTr.getY() );
            lastMousePos = e.getPoint();
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
        Point2D point = null;
        try {
            point = (Point2D)this.transform.inverseTransform(e.getPoint(),null);
        }
        catch (Exception ex ) {
            //this should not happen
            ex.printStackTrace();
        }
        ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while(it.hasPrevious()) {
            CanvasItem cur = (CanvasItem) it.previous();
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