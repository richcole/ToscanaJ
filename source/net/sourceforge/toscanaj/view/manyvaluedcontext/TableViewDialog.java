/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableFCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualValue;
import net.sourceforge.toscanaj.parser.CernatoXMLParser;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class TableViewDialog extends JFrame {

	private WritableManyValuedContext context;
	private RowHeader rowHeader;
	private ColumnHeader colHeader;
	private TableView cernatoTableView;
	private ObjectDialog cernatoObjectDialog;
	private TableViewDialog tFrame = this;
	
	public TableViewDialog(WritableManyValuedContext context) {
		super("Many-valued Context Editor");
		this.context = context;
		createHeaders();
		createView();
	}
	
	protected void createView(){
		
		cernatoObjectDialog = new ObjectDialog(this);

		cernatoTableView = new TableView(context, colHeader, rowHeader);
		cernatoTableView.addMouseListener(getTableViewMouseListener());
		JScrollPane scrollPane = new JScrollPane(cernatoTableView);
		scrollPane.setColumnHeaderView(colHeader);
		scrollPane.setRowHeaderView(rowHeader);

		setContentPane(scrollPane);
	}

	protected MouseListener getTableViewMouseListener() {
		MouseListener mouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					cernatoTableView.setSelectedColumn(new TableView.
															SelectedCell(p.getCol(), p.getRow()));
				} 
				
				else if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					ArrayList propertyList = (ArrayList)context.getAttributes();
					WritableManyValuedAttribute property = (WritableManyValuedAttribute)
															propertyList.get(p.getRow()-1);
					ArrayList objectList = (ArrayList) context.getObjects();
					WritableFCAObject obj = (WritableFCAObject)objectList.get(p.getCol()-1);
					double xPos = e.getPoint().getX();
					double yPos = e.getPoint().getY();
					
					if(property.getType() instanceof TextualType){
						showPopupMenu(xPos,yPos, property,obj);
					}
					else {
						showNumericInputDialog(property,obj);
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
		rowHeader = new RowHeader(context);
		colHeader = new ColumnHeader(context);
		colHeader.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					List propertyList = (List) context.getAttributes();
					WritableManyValuedAttribute property = (WritableManyValuedAttribute)
													propertyList.get(p.getRow()-1);
					PropertiesDialog pa = new PropertiesDialog(tFrame,property,context);
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
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					Point p = getPoint(e.getPoint().getX(), e.getPoint().getY());
					int col = p.getCol()-1;
					cernatoObjectDialog.setObjectName(getObjectName(col));
					cernatoObjectDialog.setSelectedObjectIndex(col);
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
	
	protected void showNumericInputDialog(WritableManyValuedAttribute attribute,
												WritableFCAObject obj) {
		String content = context.getRelationship(obj,attribute).toString();
		String value = (String) JOptionPane.showInputDialog(this,"Enter Value","Edit Value",
																JOptionPane.PLAIN_MESSAGE,null,null,
																content);
		if(value!=null){
			try{
				double val = Double.parseDouble(value);
				NumericalValue numericalValue = new NumericalValue(val);
				context.setRelationship(obj,attribute,numericalValue);
				update();
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(this,
							"Enter numbers only.",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
				showNumericInputDialog(attribute,obj);
			}
		}
	}
	
	protected void showPopupMenu(double xPos, double yPos, ManyValuedAttribute 
										property, FCAObject obj) {
		TextualType attributeType = (TextualType)property.getType();
		List textualValueList = attributeType.getTextualValue();
		JPopupMenu menu = new JPopupMenu();
		
		if(textualValueList.size()<=15){
			menu = createPopupMenu(textualValueList,property,obj);
		}
		else{
			menu = createPopupMenu(textualValueList.size()/15,textualValueList,property,obj );
		}
		menu.show(this.cernatoTableView,(int)xPos,(int)yPos);
	}

	protected JPopupMenu createPopupMenu(int numOfCol, List textualValueList,
											final ManyValuedAttribute property, 
												final FCAObject obj) {
		JPopupMenu menu = new JPopupMenu();
		if(numOfCol*15 <= textualValueList.size()){
			numOfCol+=1;
		}
		menu.setLayout(new GridLayout(15 ,numOfCol , 2 , 2 ));
		Iterator textualValueListIt = textualValueList.iterator();
		while(textualValueListIt.hasNext()){
			final TextualValue textualValue = (TextualValue) textualValueListIt.next();
			JMenuItem menuItem = new JMenuItem(textualValue.getDisplayString());
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					context.setRelationship(obj,property,textualValue);
					update();
				}
			});
			menu.add(menuItem);
		}
		return menu;
	}
	
	protected JPopupMenu createPopupMenu(List textualValueList, 
									final ManyValuedAttribute property,
									final FCAObject obj) {
		JPopupMenu menu = new JPopupMenu();
		Iterator textualValueListIt = textualValueList.iterator();
		while(textualValueListIt.hasNext()){
			final TextualValue textualValue = (TextualValue) textualValueListIt.next();
			JMenuItem menuItem = new JMenuItem(textualValue.getDisplayString());
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					context.setRelationship(obj,property,textualValue);
					update();
				}
			});
			menu.add(menuItem);
		}
		return menu;
	}
	
	public WritableManyValuedContext getContextTable(){
		return context;
	}
	
	protected String getObjectName(int index) {
		ArrayList objectList = (ArrayList) context.getObjects();
		FCAObject object = (FCAObject) objectList.get(index);
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
			row = (int) x / TableView.CELL_WIDTH + 1;
			col = (int) y / TableView.CELL_HEIGHT + 1;
		}
		
		public int getRow(){
			return row;
		}
		public int getCol(){
			return col;
		}
	}
	
	public static void main(String[] Args) {
		net.sourceforge.toscanaj.model.cernato.CernatoModel m = null;
		try {
			m =
				CernatoXMLParser.importCernatoXMLFile(
					new File("C:/Cernato/houses1.xml"));
		} catch (Exception e) {}
		
		if (m != null) {
			WritableManyValuedContext context = m.getContext();
			TableViewDialog c1 = new TableViewDialog(context);
			c1.setDefaultCloseOperation(EXIT_ON_CLOSE);
			c1.setSize(c1.getMaximumSize());
			c1.setVisible(true);
		}
	}
}
