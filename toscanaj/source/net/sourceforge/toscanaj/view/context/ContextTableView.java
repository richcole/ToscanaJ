/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.context;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import net.sourceforge.toscanaj.model.context.ContextImplementation;

public class ContextTableView extends JComponent implements Scrollable {
	static final Color TEXT_COLOR = Color.BLACK;
	static final Color TABLE_CELL_COLOR = Color.WHITE;
	static final Color TABLE_HEADER_COLOR = Color.LIGHT_GRAY;
	static final int CELL_WIDTH = 150;
	static final int CELL_HEIGHT = 30;

	static final Dimension TABLE_HEADER_PREFERRED_VIEWPORT_SIZE = new Dimension(CELL_WIDTH,CELL_HEIGHT);
	
	private ContextImplementation context;
	private ContextTableEditorDialog dialog;
    private static final Dimension PREFERRED_VIEWPORT_SIZE = new Dimension(6 * CELL_WIDTH, 6 * CELL_HEIGHT);

	public static class Position {
		private int row;
		private int col;
		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
	}

	public ContextTableView(ContextImplementation context, ContextTableEditorDialog dialog) {
		super();
		this.context = context;
		this.dialog = dialog;
		ToolTipManager.sharedInstance().registerComponent(this);
		updateSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		Font oldFont = g2d.getFont();

		Iterator<Object> objIt = this.context.getObjects().iterator();
		int row = 0;
		while (objIt.hasNext()) {
			Object object = objIt.next();
			drawRow(g2d, object, row);
			row += 1;
		}

		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);
		dialog.setCreateButtonStatus();
	}

	public Dimension calculateNewSize() {
		int numCol = this.context.getAttributes().size();
		int numRow = this.context.getObjects().size();
		return new Dimension(numCol * CELL_WIDTH + 1, numRow * CELL_HEIGHT + 1);
	}

	protected void drawRow(Graphics2D g2d, Object object, int row) {
		Font font = g2d.getFont();
		int y = row * CELL_HEIGHT;
		g2d.setFont(font.deriveFont(Font.PLAIN));
		g2d.setPaint(TABLE_CELL_COLOR);
		Iterator<Object> attrIt = this.context.getAttributes().iterator();
		int col = 0;
		while (attrIt.hasNext()) {
			Object attribute = attrIt.next();
			if (this.context.getRelation().contains(object, attribute)) {
				drawCell(g2d, "X", col * CELL_WIDTH, y);
			} else {
				drawCell(g2d, "", col * CELL_WIDTH, y);
			}
			col += 1;
		}
	}

	protected void drawCell(Graphics2D g2d, String content, int x, int y) {
		g2d.fill(new Rectangle2D.Double(x, y, CELL_WIDTH, CELL_HEIGHT));
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x, y, CELL_WIDTH, CELL_HEIGHT));

		FontMetrics fontMetrics = g2d.getFontMetrics();
		g2d.drawString(
			content,
			x + CELL_WIDTH / 2 - fontMetrics.stringWidth(content) / 2,
			y + CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
		g2d.setPaint(oldPaint);
	}

	protected Position getTablePosition(int xLoc, int yLoc) {
		int col = xLoc / CELL_WIDTH;
		int row = yLoc / CELL_HEIGHT;
		if ((col > this.context.getAttributes().size() )
			|| (row > this.context.getObjects().size() )) {
			return null;
		}
		return new Position(row, col);
	}
	
	public ContextImplementation getModel() {
		return this.context;
	}
	
    public Dimension getPreferredScrollableViewportSize() {
    	return PREFERRED_VIEWPORT_SIZE;
    }
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    	if(orientation == SwingConstants.HORIZONTAL) {
    		return CELL_WIDTH;
    	} else {
    	    return CELL_HEIGHT;
    	}
    }
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    	// round the size in any direction down to a multiple of the cell size
        if(orientation == SwingConstants.HORIZONTAL) {
            return (visibleRect.width / CELL_WIDTH) * CELL_WIDTH;
        } else {
            return (visibleRect.height / CELL_HEIGHT) * CELL_HEIGHT;
        }
    }
 
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }
 
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public void updateSize() {
    	this.setPreferredSize(calculateNewSize());
        invalidate();
        repaint();
    }
    
    public ContextImplementation getContext() {
        return context;
    }

    public void setContext(ContextImplementation context) {
        this.context = context;
        updateSize();
    }
}