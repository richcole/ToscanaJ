/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.lattice.Attribute;

public class ContextTableView extends JComponent {
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color TABLE_CORNER_COLOR = Color.LIGHT_GRAY;
	private static final Color TABLE_HEADER_COLOR = Color.LIGHT_GRAY;
	private static final Color TABLE_CELL_COLOR = Color.WHITE;
	private Context context;
	private ContextTableScaleEditorDialog dialog;
	private static final int CELL_WIDTH = 150;
	private static final int CELL_HEIGHT = 30;

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

	public ContextTableView(Context context, ContextTableScaleEditorDialog dialog) {
		super();
		this.context = context;
		this.dialog = dialog;
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		Font oldFont = g2d.getFont();

		g2d.setFont(oldFont.deriveFont(Font.BOLD));
		drawColumnHeader(g2d);

		Iterator objIt = this.context.getObjects().iterator();
		int row = 1;
		while (objIt.hasNext()) {
			Object object = objIt.next();
			drawRow(g2d, object, row);
			row += 1;
		}

		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);
		dialog.setCreateButtonStatus();
	}

	public Dimension getSize() {
		int numCol = this.context.getAttributes().size() + 1;
		int numRow = this.context.getObjects().size() + 1;
		return new Dimension(numCol * CELL_WIDTH + 1, numRow * CELL_HEIGHT + 1);
	}

	public Dimension getMinimumSize() {
		return getSize();
	}

	public Dimension getPreferredSize() {
		return getSize();
	}

	public Dimension getMaximumSize() {
		return getSize();
	}

	protected void drawColumnHeader(Graphics2D g2d) {
		g2d.setPaint(TABLE_CORNER_COLOR);
		drawCell(g2d, "", 0, 0);

		g2d.setPaint(TABLE_HEADER_COLOR);
		Iterator attrIt = this.context.getAttributes().iterator();
		int col = 1;
		while (attrIt.hasNext()) {
			Object attribute = attrIt.next();
			drawCell(g2d, attribute.toString(), col * CELL_WIDTH, 0);
			col += 1;
		}
	}

	protected void drawRow(Graphics2D g2d, Object object, int row) {
		Font font = g2d.getFont();
		g2d.setFont(font.deriveFont(Font.BOLD));
		g2d.setPaint(TABLE_HEADER_COLOR);
		int y = row * CELL_HEIGHT;
		drawCell(g2d, object.toString(), 0, y);

		g2d.setFont(font.deriveFont(Font.PLAIN));
		g2d.setPaint(TABLE_CELL_COLOR);
		Iterator attrIt = this.context.getAttributes().iterator();
		int col = 1;
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
		String newContent = reduceStringDisplayWidth(content, g2d);

		g2d.drawString(
			newContent,
			x + CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
			y + CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
		g2d.setPaint(oldPaint);
	}

	protected String reduceStringDisplayWidth(String content, Graphics2D g2d) {
		String newContent = content;
		String tail = "...";
		int stringWidth = g2d.getFontMetrics().stringWidth(newContent);
		int tailWidth = g2d.getFontMetrics().stringWidth(tail);
		if (stringWidth > (CELL_WIDTH - 10)) {
			while ((stringWidth + tailWidth) > (CELL_WIDTH - 10)) {
				newContent = newContent.substring(0, (newContent.length() - 1));
				stringWidth = g2d.getFontMetrics().stringWidth(newContent);
			}
			newContent += tail;
		}
		return newContent;
	}

	public int getCellHeight() {
		return CELL_HEIGHT;
	}

	public int getCellWidth() {
		return CELL_WIDTH;
	}

	public String getToolTipText(MouseEvent e) {
		String tooltipText = null;

		ArrayList attributeArrayList = (ArrayList) context.getAttributes();
		ArrayList objectsArrayList = (ArrayList) context.getObjects();
		Position pos = getTablePosition(e.getX(), e.getY());

		if (pos != null) {
			if (pos.getCol() == 0 && pos.getRow() != 0) {
				tooltipText = (String) objectsArrayList.get(pos.getRow() - 1);
			} else if (pos.getCol() != 0 && pos.getRow() == 0) {
				Attribute attr = (Attribute) attributeArrayList.get(pos.getCol() - 1);
				tooltipText = (String) attr.getData();
			}
		}
		return tooltipText;
	}

	protected Position getTablePosition(int xLoc, int yLoc) {
		int col = xLoc / getCellWidth();
		int row = yLoc / getCellHeight();
		if ((col > this.context.getAttributes().size() )
			|| (row > this.context.getObjects().size() )) {
			return null;
		}
		return new Position(row, col);
	}
	
	public Context getModel() {
		return this.context;
	}
	
}
