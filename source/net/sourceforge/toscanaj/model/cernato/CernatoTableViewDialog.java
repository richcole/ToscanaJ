/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;


import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class CernatoTableViewDialog extends JFrame {
	
	private CernatoTable cernatoTable;
	private CernatoRowHeader rowHeader;
	private CernatoColumnHeader colHeader;
	private CernatoTableView cernatoTableView;
	private CernatoTableCorner cernatoTableCorner;

	
	public CernatoTableViewDialog(CernatoTable cernatoTable){
		this.cernatoTable = cernatoTable;
		createHeaders();
		
		cernatoTableView = new CernatoTableView(cernatoTable, colHeader, rowHeader);
		cernatoTableView.addMouseListener(getMouseListener());
		JScrollPane scrollPane = new JScrollPane(cernatoTableView);
		scrollPane.setColumnHeaderView(colHeader);
		scrollPane.setRowHeaderView(rowHeader);
		
		cernatoTableCorner = new CernatoTableCorner(cernatoTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,cernatoTableCorner);
		setContentPane(scrollPane);
	}
	
	private MouseListener getMouseListener() {
		MouseListener mouseListener = new MouseListener (){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1){
					
					int obj = (int) (e.getPoint().getY()/CernatoTableView.CELL_HEIGHT)+1;
					int attr = (int) (e.getPoint().getX()/CernatoTableView.CELL_WIDTH)+1;
					cernatoTableCorner.setSelectedCell(obj,attr);
					
					CernatoTableView.SelectedCell selectedColumn = new CernatoTableView.SelectedCell(attr,obj);
					cernatoTableView.setSelectedColumn(selectedColumn);
					
				}
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
		};
		return mouseListener;
	}
	
	protected void createHeaders(){
		rowHeader = new CernatoRowHeader(cernatoTable);
		colHeader = new CernatoColumnHeader(cernatoTable);
		colHeader.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					System.out.println(e.getPoint().getX());
					System.out.println(e.getPoint().getY());
				}
			} 
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}

		});

		rowHeader.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					System.out.println(e.getPoint().getX());
					System.out.println(e.getPoint().getY());
				}
			} 
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
	
		});
	}
	
	protected void createTableView(){
		
	}
	
	public static void main(String[]Args){
		CernatoTable c = new CernatoTable();
		for(int i =0;i<50;i++){
		
			CernatoObject obj= new CernatoObject("Hi"+i);
			c.add(obj);
		}
		for(int r = 0 ; r<50 ; r++){
			Property p = new Property(new NumericalType("boo"+r),"BOoBoo"+r);
			c.add(p);
		}
		CernatoTableViewDialog c1 = new CernatoTableViewDialog(c);
		c1.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c1.setSize(new Dimension(800,600));
		c1.setVisible(true);
		
		}
	
	
	
}
