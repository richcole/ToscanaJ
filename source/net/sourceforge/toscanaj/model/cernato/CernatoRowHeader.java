/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

public class CernatoRowHeader extends JComponent{
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color CELL_COLOR = Color.LIGHT_GRAY;
	private static final int CELL_HEIGHT = 30;
	private static final int CELL_WIDTH = 150;

	private List objectList;
	private CernatoTable cernatoTable;
	
	public CernatoRowHeader(CernatoTable cernatoTable){
		this.cernatoTable = cernatoTable;
		this.objectList = (List) cernatoTable.getObjects();
		updateSize();
	}
	
	public void paintComponent(Graphics g){
		
		Graphics2D g2d = (Graphics2D)g;
		Paint oldPaint = g2d.getPaint();
		
		int row = 0;
		Iterator objectIt = objectList.iterator();
		while(objectIt.hasNext()){
			CernatoObject object = (CernatoObject)objectIt.next();
			drawCell(g2d , object.toString() , 0 , row * CELL_HEIGHT);
			row += 1;
		}
		g2d.setPaint(oldPaint);
	}
	
	protected void drawCell(Graphics2D g2d, String content, int x, int y) {
		g2d.setPaint(CELL_COLOR);
		g2d.fill( new Rectangle2D.Double(x , y , CELL_WIDTH, CELL_HEIGHT));
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x , y , CELL_WIDTH, CELL_HEIGHT));
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);

		g2d.drawString(newContent,
		x + CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
		y + CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
		
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
	
	public void updateSize() {
		this.setPreferredSize(calculateNewSize());
	}
	
	private Dimension calculateNewSize() {
		int numRow = cernatoTable.getObjects().size();
		return new Dimension(CELL_WIDTH, numRow * CELL_HEIGHT);
	}
	
}
