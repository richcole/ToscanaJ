package net.sourceforge.toscanaj.canvas;

import javax.swing.JComponent;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

/**
 * DrawingCanvas controls all the updating of CanvasItems contained in a DiagramView
 * ie NodeView, LineView, and LabelView
 *
 * @TODO For now the mouse events on the canvas are dealt with here, but shall be handled by
 * a separate class eventually (CanvasController)
 */

public class DrawingCanvas extends JComponent implements MouseListener, MouseMotionListener {

    /**
     * A list of all canvas items to draw.
     */
    protected List canvasItems;

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
        // paint all items on canvas
        Iterator it = this.canvasItems.iterator();
        //ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            cur.draw(graphics);
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
     * Handles dragging the labels.
     */
    public void mouseDragged(MouseEvent e) {
        if(selectedCanvasItem != null && (dragMode || ((getDistance(lastMousePos.getX(), lastMousePos.getY(), e.getX(), e.getY()) >= dragMin)))) {
            selectedCanvasItem.moveBy(graphics.inverseScaleX(e.getX() - lastMousePos.getX()),
                                 graphics.inverseScaleY(e.getY() - lastMousePos.getY()));
            lastMousePos = new Point2D.Double(e.getX(), e.getY());
            dragMode = true;
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
                // raise the item
                it.remove();
                this.canvasItems.add(cur);
                // redraw the raised item (needed if it will not be moved)
                repaint();
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
     * create a new list of canvasItems
     */
    public void newCanvasItemsList() {
        canvasItems = new LinkedList();
    }

    /**
     * Adds a canvas item to the canvasItem list.
     *
     */
    public void addCanvasItem(CanvasItem node) {
        this.canvasItems.add(node);
    }
}