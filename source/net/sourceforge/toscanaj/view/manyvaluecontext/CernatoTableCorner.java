/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package net.sourceforge.toscanaj.view.manyvaluecontext;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import net.sourceforge.toscanaj.model.cernato.CernatoTable;


public class CernatoTableCorner extends JComponent{
	private CernatoTable cernatoTable;
	private static final Color CELL_COLOR = Color.LIGHT_GRAY;
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final int CELL_WIDTH = 150;
	private static final int CELL_HEIGHT = 60;
	private int selectedObj;
	private int selectedAttr;

	
	public CernatoTableCorner(CernatoTable cernatoTable){
		this.cernatoTable = cernatoTable;
		selectedObj = 0;
		selectedAttr = 0;
	}
	
	public void setSelectedCell(int selectedObj, int selectedAttr){
		this.selectedObj = selectedObj;
		this.selectedAttr = selectedAttr;
		repaint();
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		
		g2d.setPaint(CELL_COLOR);
		g2d.fill(new Rectangle2D.Double(0,0,CELL_WIDTH,CELL_HEIGHT));
		
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(0,0,CELL_WIDTH,CELL_HEIGHT));
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		String objContent = "Object "+selectedObj+" of "+cernatoTable.getObjects().size();
		String attrContent = "Attribute "+selectedAttr + " of "+cernatoTable.getAttributes().size();

		g2d.drawString(objContent,
		CELL_WIDTH / 2 - fontMetrics.stringWidth(objContent) / 2,
		CELL_HEIGHT /4 + fontMetrics.getMaxAscent()/2);
		
		g2d.drawString(attrContent,
		CELL_WIDTH / 2 - fontMetrics.stringWidth(objContent) / 2,
		CELL_HEIGHT /2 + fontMetrics.getMaxAscent());

		
		g2d.setPaint(oldPaint);
		
	}
	
	

}
