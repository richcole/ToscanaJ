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
import java.awt.Rectangle;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;


public class TableView extends JComponent implements Scrollable{
	
	public static final int CELL_HEIGHT = 30;
	public static final int CELL_WIDTH = 150;
	private static final Color ERROR_CELL_COLOR = new Color(255,50,50);
	private static final Color SELECTED_CELL_COLOR = new Color(204,255,255);
	private static final Color CELL_COLOR = Color.WHITE;
	private static final Color BORDER_COLOR = Color.BLACK;
	
	private ManyValuedContext context;
	private ColumnHeader colHeader;
	private RowHeader rowHeader;
	private SelectedCell selectedCell;
	
	public TableView(ManyValuedContext context,ColumnHeader colHeader, RowHeader rowHeader){
		super();
		this.context = context;
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
		Iterator attriIt = context.getAttributes().iterator();
		while(attriIt.hasNext()){
			ManyValuedAttribute property = (ManyValuedAttribute) attriIt.next();
			if(selectedCell.getColumn()-1 == col){
				drawColumn(g2d, col, true, property);
			}else{
				drawColumn(g2d,col,false, property);
			}
			col+=1;
		}
		g2d.setPaint(oldPaint);
	}
	
	protected void drawColumn(Graphics2D g2d , int col, boolean columnSelected,
										ManyValuedAttribute attribute) {

		Iterator objIt = context.getObjects().iterator();
		int row = 0;
		while(objIt.hasNext()){
			FCAObject object = (FCAObject) objIt.next();
			String content = context.getRelationship(object,attribute).toString();
			
			boolean selected = checkCellSelected(row,columnSelected);
			if(!attribute.getType().isValidValue(context.getRelationship(object,attribute))){
				drawCell(g2d,col,row,ERROR_CELL_COLOR,content);
			}
			else if(selected){
				drawCell(g2d,col,row,SELECTED_CELL_COLOR,content);
			}
			else{
				drawCell(g2d,col,row,CELL_COLOR,content);
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
	
	protected boolean checkCellSelected(int row, boolean columnSelected){
		if(selectedCell.getRow()-1 == row || columnSelected){
				return true;
		}
		return false;
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
	
	protected Dimension calculateNewSize() {
		int numCol = context.getAttributes().size();
		int numRow = context.getObjects().size();
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
