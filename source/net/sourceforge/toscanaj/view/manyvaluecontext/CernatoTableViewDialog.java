/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluecontext;

import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.cernato.CernatoObject;
import net.sourceforge.toscanaj.model.cernato.CernatoTable;
import net.sourceforge.toscanaj.model.cernato.Property;
import net.sourceforge.toscanaj.model.cernato.TextualType;
import net.sourceforge.toscanaj.model.cernato.TextualValue;
import net.sourceforge.toscanaj.parser.CernatoXMLParser;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class CernatoTableViewDialog extends JFrame {

	private CernatoTable cernatoTable;
	private CernatoRowHeader rowHeader;
	private CernatoColumnHeader colHeader;
	private CernatoTableView cernatoTableView;
	private CernatoTableCorner cernatoTableCorner;
	private CernatoObjectDialog cernatoObjectDialog;
	public CernatoTableViewDialog(CernatoTable cernatoTable) {
		super("Many-valued Context Editor");
		this.cernatoTable = cernatoTable;
		createHeaders();
		createView();
	}
	
	protected void createView(){
		
		cernatoObjectDialog = new CernatoObjectDialog(this);

		cernatoTableView = new CernatoTableView(cernatoTable, colHeader, rowHeader);
		cernatoTableView.addMouseListener(getTableViewMouseListener());
		JScrollPane scrollPane = new JScrollPane(cernatoTableView);
		scrollPane.setColumnHeaderView(colHeader);
		scrollPane.setRowHeaderView(rowHeader);

		cernatoTableCorner = new CernatoTableCorner(cernatoTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, cernatoTableCorner);
		setContentPane(scrollPane);
	}

	protected MouseListener getTableViewMouseListener() {
		MouseListener mouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					cernatoTableCorner.setSelectedCell(p.getCol(), p.getRow());
					cernatoTableView.setSelectedColumn(new CernatoTableView.
															SelectedCell(p.getCol(), p.getRow()));
					
				} 
				
				else if(e.getClickCount() == 2){
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					ArrayList propertyList = (ArrayList)cernatoTable.getAttributes();
					Property property = (Property)propertyList.get(p.getRow()-1);
					ArrayList objectList = (ArrayList) cernatoTable.getObjects();
					CernatoObject obj = (CernatoObject)objectList.get(p.getCol()-1);
					
					if(property.getType() instanceof TextualType){
						showPopupMenu(e.getPoint().getX(), e.getPoint().getY(), property,obj);
					}
					
					else {
						System.out.println("Numerical Type");
					}
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

	protected void createHeaders() {
		rowHeader = new CernatoRowHeader(cernatoTable);
		colHeader = new CernatoColumnHeader(cernatoTable);
		colHeader.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
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

		rowHeader.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					cernatoObjectDialog.setObjectName(getObjectName(p.getCol()-1));
					cernatoObjectDialog.setSelectedObjectIndex(p.getCol()-1);
					cernatoObjectDialog.show();
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

	public void update() {
		cernatoTableView.repaint();
		rowHeader.repaint();
		colHeader.repaint();
	}
	
	protected void showPopupMenu(double x, double y, Property property, CernatoObject obj) {
		TextualType attributeType = (TextualType)property.getType();
		List textualValueList = attributeType.getTextualValue();
		JPopupMenu menu = new JPopupMenu();
		
		if(textualValueList.size()<=15){
			menu = createPopupMenu(textualValueList,property,obj);
		}
		else{
			menu = createPopupMenu(textualValueList.size()/15,textualValueList,property,obj );
		}
		menu.show(this.cernatoTableView,(int)x,(int)y);

	}

	protected JPopupMenu createPopupMenu(int numOfCol, List textualValueList,
											final Property property, final CernatoObject obj) {
		JPopupMenu menu = new JPopupMenu();
		if(numOfCol*15 <= textualValueList.size()){
			numOfCol+=1;
		}
		menu.setLayout(new GridLayout(15 ,numOfCol , 2 , 2 ));
		for(int i = 0 ; i < textualValueList.size() ; i++){
			final TextualValue t = (TextualValue)textualValueList.get(i);
			JMenuItem menuItem = new JMenuItem(t.getDisplayString());
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					cernatoTable.setRelationship(obj,property,t);
					update();
				}
			});
			menu.add(menuItem);
		}
		return menu;
	}
	
	protected JPopupMenu createPopupMenu(List textualValueList, 
									final Property property,final CernatoObject obj) {
		JPopupMenu menu = new JPopupMenu();
		for(int i = 0 ; i < textualValueList.size() ; i++){
			final TextualValue t = (TextualValue)textualValueList.get(i);
			JMenuItem menuItem = new JMenuItem(t.getDisplayString());
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					cernatoTable.setRelationship(obj,property,t);
					update();
				}
			});
	
			menu.add(menuItem);
		}
		return menu;
	}
	
	public CernatoTable getCernatoTable(){
		return cernatoTable;
	}
	
	protected String getObjectName(int index) {
		ArrayList objectList = (ArrayList) cernatoTable.getObjects();
		CernatoObject object = (CernatoObject) objectList.get(index);
		String objectName = object.getName();
		return objectName;
	}
				
	public Point getPoint(double x, double y){
		return new Point(x,y);
	}
	
	public class Point{
		
		private int row;
		private int col;
		
		public Point(double x, double y){
			row = (int) x / CernatoTableView.CELL_WIDTH + 1;
			col = (int) y / CernatoTableView.CELL_HEIGHT + 1;
		}
		
		public int getRow(){
			return row;
		}
		public int getCol(){
			return col;
		}
	}
	
	public static void main(String[] Args) {
		CernatoModel m = null;
		try {
			m =
				CernatoXMLParser.importCernatoXMLFile(
					new File("C:/Cernato/houses.xml"));
		} catch (Exception e) {
	
		}
		if (m != null) {
			CernatoTable c = m.getContext();
	
			CernatoTableViewDialog c1 = new CernatoTableViewDialog(c);
			c1.setDefaultCloseOperation(EXIT_ON_CLOSE);
			c1.setSize(c1.getMaximumSize());
			c1.setVisible(true);
	
		}
	
	}

}
