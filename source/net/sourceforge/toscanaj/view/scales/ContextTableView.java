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
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JComponent;

import net.sourceforge.toscanaj.model.Context;

public class ContextTableView extends JComponent {
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color TABLE_CORNER_COLOR = Color.WHITE;
	private static final Color TABLE_HEADER_COLOR = Color.DARK_GRAY;
	private static final Color TABLE_CELL_COLOR = Color.LIGHT_GRAY;
	private Context context;
	private static final int TABLE_WIDTH = 100;
	private static final int TABLE_HEIGHT = 30;
	
	
	public ContextTableView(Context context) {
		super();
		this.context = context;
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
		
		int numCol = this.context.getAttributes().size() + 1;
		int numRow = this.context.getObjects().size() + 1;
		Dimension size = new Dimension(numCol * TABLE_WIDTH + 1, numRow * TABLE_HEIGHT + 1);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		
		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);
	}

	protected void drawColumnHeader(Graphics2D g2d) {
		g2d.setPaint(TABLE_CORNER_COLOR);
		drawCell(g2d, "", 0,0);
		
		g2d.setPaint(TABLE_HEADER_COLOR);
		Iterator attrIt = this.context.getAttributes().iterator();
		int col = 1;
		while(attrIt.hasNext()) {
			Object attribute = attrIt.next();
			drawCell(g2d, attribute.toString(), col * TABLE_WIDTH, 0);
			col += 1;
		}
	}

	protected void drawRow(Graphics2D g2d, Object object, int row) {
		Font font = g2d.getFont();
		g2d.setFont(font.deriveFont(Font.BOLD));		
		g2d.setPaint(TABLE_HEADER_COLOR);
		int y = row * TABLE_HEIGHT;
		drawCell(g2d, object.toString(), 0,y);
		
		g2d.setFont(font.deriveFont(Font.PLAIN));		
		g2d.setPaint(TABLE_CELL_COLOR);
		Iterator attrIt = this.context.getAttributes().iterator();
		int col = 1;
		while(attrIt.hasNext()) {
			Object attribute = attrIt.next();
			if(this.context.getRelation().contains(object,attribute)) {
				drawCell(g2d, "X", col * TABLE_WIDTH, y);
			} else {
				drawCell(g2d, "", col * TABLE_WIDTH, y);
			}
			col += 1;
		}
	}

	protected void drawCell(Graphics2D g2d, String content, int x, int y) {
		g2d.fill(new Rectangle2D.Double(x,y,TABLE_WIDTH, TABLE_HEIGHT));
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(TEXT_COLOR); 
		g2d.draw(new Rectangle2D.Double(x,y,TABLE_WIDTH, TABLE_HEIGHT));
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		g2d.drawString(content, x + TABLE_WIDTH/2 - fontMetrics.stringWidth(content)/2, 
		                        y + TABLE_HEIGHT/2 + fontMetrics.getMaxAscent()/2);
		g2d.setPaint(oldPaint);
	}
	
}
