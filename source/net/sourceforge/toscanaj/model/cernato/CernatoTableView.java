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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class CernatoTableView extends JComponent implements Scrollable{
	
	public static final int CELL_HEIGHT = 30;
	public static final int CELL_WIDTH = 150;
	private static final Color SELECTED_CELL_COLOR = new Color(255,255,204);
	private static final Color CELL_COLOR = Color.WHITE;
	private static final Color BORDER_COLOR = Color.BLACK;
	
	private CernatoTable cernatoTable;
	private CernatoColumnHeader colHeader;
	private CernatoRowHeader rowHeader;
	private SelectedCell selectedCell;
	
	public CernatoTableView(CernatoTable cernatoTable,CernatoColumnHeader colHeader, CernatoRowHeader rowHeader){
		super();
		this.cernatoTable = cernatoTable;
		this.colHeader = colHeader;
		this.rowHeader = rowHeader;
		this.selectedCell = new SelectedCell(-1,-1);
		updateSize();
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		int col = 0;
		Iterator attriIt = cernatoTable.getAttributes().iterator();
		while(attriIt.hasNext()){
			attriIt.next();
			if(selectedCell.getColumn()-1 == col){
				drawColumn(g2d, col, true);
			}else{
				drawColumn(g2d,col,false);
			}
			col+=1;
		}
		g2d.setPaint(oldPaint);
	}
	
	private void drawColumn(Graphics2D g2d , int col, boolean columnSelected) {

		Iterator objIt = cernatoTable.getObjects().iterator();
		Paint oldPaint = g2d.getPaint();
		int row = 0;
		while(objIt.hasNext()){
			objIt.next();
			if(selectedCell.getRow()-1 == row || columnSelected){
				g2d.setPaint(SELECTED_CELL_COLOR);
				g2d.fill(new Rectangle2D.Double(col*CELL_WIDTH, row*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT));
				g2d.setPaint(BORDER_COLOR);
				g2d.draw(new Rectangle2D.Double(col*CELL_WIDTH, row*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT));
				g2d.setPaint(oldPaint);
			}else{
				g2d.setPaint(CELL_COLOR);
				g2d.fill(new Rectangle2D.Double(col*CELL_WIDTH, row*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT));
				g2d.setPaint(BORDER_COLOR);
				g2d.draw(new Rectangle2D.Double(col*CELL_WIDTH, row*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT));
				g2d.setPaint(oldPaint);
			}
			row+= 1 ;
		}
	}
	
	public void setSelectedColumn(SelectedCell col){
		selectedCell = col;
		repaint();
	}


	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,int orientation,int direction) {
		
		if(orientation == SwingConstants.HORIZONTAL) {
			 return CELL_WIDTH;
		 } else {
			 return CELL_HEIGHT;
		 }
	   
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,int orientation,int direction) {
			
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
	}
	
	private Dimension calculateNewSize() {
		int numCol = cernatoTable.getAttributes().size();
		int numRow = cernatoTable.getObjects().size();
		return new Dimension(numCol * CELL_WIDTH , numRow * CELL_HEIGHT);
	}
	
	
	public static class SelectedCell{
		public int col;
		public int row;
		
		public SelectedCell(int col, int row){
			this.col = col;
			this.row = row;
		}
		
		public void setColumn(int col){
			this.col = col;
		}
		
		public void setRow(int row){
			this.row = row;
		}
		
		public int getRow(){
			return row;
		}
		
		public int getColumn(){
			return col;
		}
	}
	

}
