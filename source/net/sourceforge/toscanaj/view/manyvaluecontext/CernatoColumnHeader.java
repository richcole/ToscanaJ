/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluecontext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import net.sourceforge.toscanaj.model.cernato.CernatoTable;
import net.sourceforge.toscanaj.model.cernato.Property;



public class CernatoColumnHeader extends JComponent{
	
	private static final Color TABLE_TYPE_CELL_COLOR = Color.WHITE;
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color TABLE_HEADER_COLOR = Color.LIGHT_GRAY;
	public static final int CELL_HEIGHT= 30;
	public static final int CELL_WIDTH= 150;
	
	private List attributeList;
	private CernatoTable cernatoTable;
	
	
	public CernatoColumnHeader(CernatoTable cernatoTable){
		this.cernatoTable = cernatoTable;
		this.attributeList = (List) cernatoTable.getAttributes();
		updateSize();
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		Font oldFont = g2d.getFont();
		int col = 0;
		Iterator attrIt = attributeList.iterator();
		while(attrIt.hasNext()){
			Property attribute = (Property) attrIt.next();	
			drawAttributeNameCell(g2d,attribute.toString(),col * CELL_WIDTH,0);	
			drawAttributeTypeCell(g2d, attribute.getType().getName() , col*CELL_WIDTH,CELL_HEIGHT);
			col+=1;
		}
		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);	
	}

	protected void drawAttributeTypeCell(Graphics2D g2d,String content,int x,int y)
	 {
	 	g2d.setPaint(TABLE_TYPE_CELL_COLOR);
		g2d.fill(new Rectangle2D.Double(x,y,CELL_WIDTH,CELL_HEIGHT));
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x,y,CELL_WIDTH,CELL_HEIGHT));
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);
		
		g2d.drawString(newContent,
		x + CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
		y + CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
	}
	
	protected void drawAttributeNameCell(Graphics2D g2d, String content, int x, int y) {
		g2d.setPaint(TABLE_HEADER_COLOR);
		g2d.fill(new Rectangle2D.Double(x, y, CELL_WIDTH, CELL_HEIGHT));
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x, y, CELL_WIDTH, CELL_HEIGHT));
	
		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);
	
		g2d.drawString(
			newContent,
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
		int numCol = cernatoTable.getAttributes().size();
		return new Dimension(numCol * CELL_WIDTH, 2*CELL_HEIGHT);
	}
	
}
