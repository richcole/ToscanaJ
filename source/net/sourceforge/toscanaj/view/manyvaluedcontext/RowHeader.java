/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JComponent;

import net.sourceforge.toscanaj.model.context.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;



public class RowHeader extends JComponent{
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color CELL_COLOR = Color.LIGHT_GRAY;

	private ManyValuedContext context;
	
	public RowHeader(ManyValuedContext context){
		this.context = context;
		updateSize();
	}
	
	public void paintComponent(Graphics g){
		
		Graphics2D g2d = (Graphics2D)g;
		Paint oldPaint = g2d.getPaint();
		
		int row = 0;
		Iterator objectIt = this.context.getObjects().iterator();
		while(objectIt.hasNext()){
			FCAObject object = (FCAObject)objectIt.next();
			drawCell(g2d , object.toString() , 0 , row * TableView.CELL_HEIGHT);
			row += 1;
		}
		g2d.setPaint(oldPaint);
	}
	
	protected void drawCell(Graphics2D g2d, String content, int x, int y) {
		g2d.setPaint(CELL_COLOR);
		g2d.fill( new Rectangle2D.Double(x , y , TableView.CELL_WIDTH, TableView.CELL_HEIGHT));
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x , y , TableView.CELL_WIDTH, TableView.CELL_HEIGHT));
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);

		g2d.drawString(newContent,
		x + TableView.CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
		y + TableView.CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
		
	}
	
	protected String reduceStringDisplayWidth(String content, Graphics2D g2d) {
		String newContent = content;
		String tail = "...";
		int stringWidth = g2d.getFontMetrics().stringWidth(newContent);
		int tailWidth = g2d.getFontMetrics().stringWidth(tail);
		if (stringWidth > (TableView.CELL_WIDTH - 10)) {
			while ((stringWidth + tailWidth) > (TableView.CELL_WIDTH - 10)) {
				newContent = newContent.substring(0, (newContent.length() - 1));
				stringWidth = g2d.getFontMetrics().stringWidth(newContent);
			}
			newContent += tail;
		}
		return newContent;
	}
	
	public void updateSize() {
		this.setPreferredSize(calculateNewSize());
	}
	
	private Dimension calculateNewSize() {
		int numRow = context.getObjects().size() + 1;
		return new Dimension(TableView.CELL_WIDTH, numRow * TableView.CELL_HEIGHT + 1);
	}
	
	public void setManyValuedContext(ManyValuedContext context) {
		this.context = context;
		updateSize();
		validate();
	}
}
