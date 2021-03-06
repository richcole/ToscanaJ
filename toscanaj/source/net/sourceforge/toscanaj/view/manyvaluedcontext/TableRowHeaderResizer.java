/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MouseInputAdapter;

/**
 * A class to resize a row header.
 * 
 * Based on
 * http://www.chka.de/swing/table/row-headers/JTableRowHeaderResizer.java
 */
public class TableRowHeaderResizer extends MouseInputAdapter implements
        Serializable, ContainerListener {
    private final JScrollPane pane;
    private JViewport viewport;
    private JTable rowHeader;
    private Component corner;
    private JTable view;

    private boolean enabled;

    public TableRowHeaderResizer(final JScrollPane pane) {
        this.pane = pane;

        this.pane.addContainerListener(this);
    }

    public void setEnabled(final boolean what) {
        if (enabled == what) {
            return;
        }

        enabled = what;

        if (enabled) {
            addListeners();
        } else {
            removeListeners();
        }
    }

    protected void addListeners() {
        if (corner != null) {
            corner.addMouseListener(this);
            corner.addMouseMotionListener(this);
        }
    }

    protected void removeListeners() {
        if (corner != null) {
            corner.removeMouseListener(this);
            corner.removeMouseMotionListener(this);
        }
    }

    protected void lookupComponents() {
        this.view = (JTable) pane.getViewport().getView();
        this.viewport = pane.getRowHeader();
        if (viewport == null) {
            this.rowHeader = null;
        } else {
            this.rowHeader = (JTable) viewport.getView();
        }
        this.corner = pane.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER);
    }

    public void componentAdded(final ContainerEvent e) {
        componentRemoved(e);
    }

    public void componentRemoved(final ContainerEvent e) {
        if (enabled) {
            removeListeners();
        }

        lookupComponents();

        if (enabled) {
            addListeners();
        }
    }

    private boolean active;

    private int startX, startWidth;

    private int minWidth, maxWidth;

    private Dimension size;

    private static final int PIXELS = 10;

    private static final Cursor RESIZE_CURSOR = Cursor
            .getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

    private Cursor oldCursor;

    @Override
    public void mouseExited(final MouseEvent e) {
        if (oldCursor != null) {
            corner.setCursor(oldCursor);
            oldCursor = null;
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (corner.getWidth() - e.getX() <= PIXELS) {
            if (oldCursor == null) {
                oldCursor = corner.getCursor();
                corner.setCursor(RESIZE_CURSOR);
            }
        } else if (oldCursor != null) {
            corner.setCursor(oldCursor);
            oldCursor = null;
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        startX = e.getX();

        startWidth = rowHeader.getWidth();

        if (startWidth - startX > PIXELS) {
            return;
        }

        active = true;

        if (oldCursor == null) {
            oldCursor = corner.getCursor();
            corner.setCursor(RESIZE_CURSOR);
        }

        minWidth = rowHeader.getMinimumSize().width;
        maxWidth = rowHeader.getMaximumSize().width;
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        active = false;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (!active) {
            return;
        }

        size = viewport.getPreferredSize();

        final int newX = e.getX();

        size.width = startWidth + newX - startX;

        if (size.width < minWidth) {
            size.width = minWidth;
        } else if (size.width > maxWidth) {
            size.width = maxWidth;
        }

        // This isn't too clean, it assumes the width bubbles up to
        // viewport.getPreferredSize().width without changes.
        rowHeader.getColumnModel().getColumn(0).setPreferredWidth(size.width);

        view.sizeColumnsToFit(-1);

        pane.revalidate();
    }
}