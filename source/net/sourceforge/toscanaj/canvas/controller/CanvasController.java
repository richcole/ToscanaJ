/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.controller;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.Canvas;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.Timer;
import java.util.ListIterator;

public class CanvasController implements MouseListener, MouseMotionListener {

    private Canvas canvas;

    /**
     * Flag to prevent label from being moved when just clicked on
     */
    private boolean dragMode = false;

    /**
     * This is true if a popup might have been opened as reaction on a mouse press
     * event.
     */
    private boolean popupOpen = false;

    /**
     * Distance that label has to be moved to enable dragMode
     */
    private int dragMin = 5;

    /**
     * The position where the mouse was when the last event came.
     */
    private Point2D lastMousePos = null;

    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer doubleClickTimer = null;

    /**
     * Holds the selected CanvasItem
     * that the user has clicked on with intent to move
     */
    private CanvasItem selectedCanvasItem = null;

    public CanvasController(Canvas canvas) {
        this.canvas = canvas;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    /**
     * Not used -- mouse clicks are handled as press/release combinations.
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Handles mouse release events.
     *
     * Resets the diagram from dragging mode back into normal mode or calls
     * singleClicked() or doubleClicked() on the CanvasItem hit. If no item was hit
     * backgroundSingleClicked() or backgroundDoubleClicked() on the canvas is
     * called.
     *
     * singleClicked() will only be send if it is not a double click. In any case
     * clicked() or backgroundClicked() will be send.
     *
     * @TODO Use system double click timing instead of hard-coded 300ms
     */
    public void mouseReleased(MouseEvent e) {
        if (popupOpen) {
            popupOpen = false;
            return; // nothing to do, we react only on normal clicks
        }
        Point screenPos = e.getPoint();
        if (e.isPopupTrigger()) {
            Point2D canvasPos = canvas.getCanvasCoordinates(screenPos);
            if (selectedCanvasItem != null) {
                selectedCanvasItem.openPopupMenu(canvasPos, screenPos);
            } else {
                canvas.openBackgroundPopupMenu(canvasPos, screenPos);
            }
            popupOpen = true;
        }
        if (dragMode) {
            dragMode = false;
            dragFinished(e);
            canvas.repaint();
        } else {
            Point2D modelPos = null;
            modelPos = canvas.getCanvasCoordinates(screenPos);
            if (selectedCanvasItem == null) {
                canvas.backgroundClicked(modelPos);
                if (e.getClickCount() == 1) {
                    this.doubleClickTimer = new Timer();
                    this.doubleClickTimer.schedule(new BackgroundSingleClickTask(canvas, modelPos), 300);
                } else if (e.getClickCount() == 2) {
                    this.doubleClickTimer.cancel();
                    canvas.backgroundDoubleClicked(modelPos);
                }
                return;
            }
            selectedCanvasItem.clicked(modelPos);
            if (e.getClickCount() == 1) {
                this.doubleClickTimer = new Timer();
                this.doubleClickTimer.schedule(new CanvasItemSingleClickTask(this.selectedCanvasItem, modelPos), 300);
            } else if (e.getClickCount() == 2) {
                this.doubleClickTimer.cancel();
                selectedCanvasItem.doubleClicked(modelPos);
            }
        }
        selectedCanvasItem = null;
    }

    /**
     * Not used.
     */
    protected void dragFinished(MouseEvent e) {

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
        if (selectedCanvasItem == null) {
            return;
        }
        if (!canvas.contains(e.getPoint())) {
            return;
        }
        if (!dragMode && (lastMousePos.distance(e.getPoint()) >= dragMin)) {
            dragMode = true;
        }
        if (dragMode) {
            Point2D mousePosTr = null;
            Point2D lastMousePosTr = null;
            mousePosTr = canvas.getCanvasCoordinates(e.getPoint());
            lastMousePosTr = canvas.getCanvasCoordinates(lastMousePos);


            selectedCanvasItem.dragged(lastMousePosTr, mousePosTr);
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
        Point screenPos = e.getPoint();
        Point2D canvasPos = canvas.getCanvasCoordinates(screenPos);
        this.selectedCanvasItem = canvas.getCanvasItemAt(canvasPos);
        this.lastMousePos = screenPos;
        if (e.isPopupTrigger()) {
            if (this.selectedCanvasItem != null) {
                this.selectedCanvasItem.openPopupMenu(canvasPos, screenPos);
            } else {
                canvas.openBackgroundPopupMenu(canvasPos, screenPos);
            }
        }
        this.popupOpen = e.isPopupTrigger();
    }
}