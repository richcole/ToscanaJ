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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import net.sourceforge.toscanaj.model.cernato.CernatoObject;
import net.sourceforge.toscanaj.model.cernato.CernatoTable;
import net.sourceforge.toscanaj.model.cernato.Property;


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
		this.selectedCell = new SelectedCell(0,0);
		updateSize();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		int col = 0;
		Iterator attriIt = cernatoTable.getAttributes().iterator();
		while(attriIt.hasNext()){
			Property property = (Property) attriIt.next();
			if(selectedCell.getColumn()-1 == col){
				drawColumn(g2d, col, true, property);
			}else{
				drawColumn(g2d,col,false, property);
			}
			col+=1;
		}
		g2d.setPaint(oldPaint);
	}
	
	private void drawColumn(Graphics2D g2d , int col, boolean columnSelected,
							Property attribute) {

		Iterator objIt = cernatoTable.getObjects().iterator();
		int row = 0;
		while(objIt.hasNext()){
			CernatoObject object = (CernatoObject) objIt.next();
			if(selectedCell.getRow()-1 == row || columnSelected){
				if(cernatoTable.getRelationship(object,attribute)!=null){
					String content = cernatoTable.getRelationship(object,attribute).toString();
					drawCell(g2d,col,row,SELECTED_CELL_COLOR,content);
				}
				else{
					drawCell(g2d,col,row,SELECTED_CELL_COLOR,"");
				}
				
			}else{
				if(cernatoTable.getRelationship(object,attribute)!=null){
					String content = cernatoTable.getRelationship(object,attribute).toString();
					drawCell(g2d,col,row,CELL_COLOR,content);
				}
				else{
					drawCell(g2d,col,row,CELL_COLOR,"");
				}
			}
			row+= 1 ;
		}
	}
	
	protected void drawCell(Graphics2D g2d,int col, int row, Color cellColor,
								String content){
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(cellColor);
		g2d.fill(new Rectangle2D.Double(col*CELL_WIDTH, row*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT));
		g2d.setPaint(BORDER_COLOR);
		g2d.draw(new Rectangle2D.Double(col*CELL_WIDTH, row*CELL_HEIGHT, CELL_WIDTH, CELL_HEIGHT));
		g2d.setPaint(oldPaint);
		
		FontMetrics fontMetrics = g2d.getFontMetrics();

		g2d.drawString(content,
		(col*CELL_WIDTH) + CELL_WIDTH / 2 - fontMetrics.stringWidth(content) / 2,
		(row*CELL_HEIGHT) + CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
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
	public void update() {
		repaint();
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
		
		public SelectedCell(int row, int col){
			this.row = row;
			this.col = col;
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
