/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import net.sourceforge.toscanaj.controller.diagram.LabelClickEventHandler;
import net.sourceforge.toscanaj.controller.diagram.LabelDragEventHandler;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.Criterion;
import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WriteableFCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WriteableManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WriteableManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.View;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.ViewContext;
import net.sourceforge.toscanaj.parser.CernatoXMLParser;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import org.tockit.events.EventBroker;


public class TableViewPanel extends JFrame {

	private WriteableManyValuedContext context;
	private RowHeader rowHeader;
	private ColumnHeader colHeader;
	private TableView cernatoTableView;
	private ObjectDialog cernatoObjectDialog;
	private TableViewPanel tFrame = this;
	private JSplitPane splitPane;
	private Collection attributes;
	private DiagramView diagramView;
	private View view;
	
	public TableViewPanel(WriteableManyValuedContext context) {
		this.context = context;
		createHeaders();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,createTableView(),createDiagramView());
		splitPane.setOneTouchExpandable(true);
		setContentPane(splitPane);
	}
		
	protected JScrollPane createTableView(){
		cernatoObjectDialog = new ObjectDialog(this);

		cernatoTableView = new TableView(context, colHeader, rowHeader);
		cernatoTableView.addMouseListener(getTableViewMouseListener());
		
		JScrollPane scrollPane = new JScrollPane(cernatoTableView);
		scrollPane.setColumnHeaderView(colHeader);
		scrollPane.setRowHeaderView(rowHeader);
		
		return scrollPane;
	}
	
	public JPanel createDiagramView(){
		view = new View("Views");
		JPanel mainPane = new JPanel(new GridBagLayout());
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton newButton = new JButton ("New");
		newButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				view = new View("Views");
				showDiagram(getDiagram());
			}
		});
		
		JButton addButton = new JButton ("Add");
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				AddCriterionAttributeDialog dialog = new AddCriterionAttributeDialog(context.getAttributes(),tFrame);
				dialog.show();
			}
		});

		buttonPane.add(newButton);		
		buttonPane.add(addButton);

		diagramView = new DiagramView();
		diagramView.setQuery(ListQuery.KEY_LIST_QUERY);
		EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
		DirectConceptInterpreter interpreter = new DirectConceptInterpreter();
		ConceptInterpretationContext interpretationContext =
				new ConceptInterpretationContext(new DiagramHistory(), canvasEventBroker);
		diagramView.setConceptInterpreter(interpreter);
		diagramView.setConceptInterpretationContext(interpretationContext);
		new LabelDragEventHandler(canvasEventBroker);
		new LabelClickEventHandler(canvasEventBroker);
		diagramView.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		diagramView.setPreferredSize(new Dimension(600,400));
		
		
		mainPane.add(buttonPane, new GridBagConstraints(
							0,0,1,1,1,0,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL,
							new Insets(2,2,2,2),
							2,2));
		mainPane.add(diagramView, new GridBagConstraints(
							0,1,1,1,1,1,
							GridBagConstraints.CENTER,
							GridBagConstraints.BOTH,
							new Insets(2,2,2,2),
							2,2
							));
		
		return mainPane;
		
	}
	
	public void showDiagram(Diagram2D diagram){
		diagramView.showDiagram(diagram);
	}
	
	protected Diagram2D getDiagram(){
		ViewContext viewContext = new ViewContext(context,view);
		LatticeGenerator lgen = new GantersAlgorithm();
		Lattice lattice = lgen.createLattice(viewContext);
		Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, viewContext.getName(), new DefaultDimensionStrategy());
		
		return diagram;
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
					WriteableManyValuedAttribute property = (WriteableManyValuedAttribute)
															propertyList.get(p.getRow()-1);
					ArrayList objectList = (ArrayList) context.getObjects();
					WriteableFCAObject obj = (WriteableFCAObject)objectList.get(p.getCol()-1);
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
					WriteableManyValuedAttribute property = (WriteableManyValuedAttribute)
													propertyList.get(p.getRow()-1);
					PropertiesDialog propertiesDialog = new PropertiesDialog(tFrame,property,context);
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

	protected void showNumericInputDialog(WriteableManyValuedAttribute attribute,
												WriteableFCAObject obj) {
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
		AttributeValue[] textualValueList = attributeType.getValueRange();
		JPopupMenu menu = new JPopupMenu();
		
		if(textualValueList.length<=15){
			menu = createPopupMenu(1,textualValueList.length,textualValueList,
														property,obj);
		}
		else{
			int numOfRows = 15;
			menu = createPopupMenu(textualValueList.length/numOfRows,numOfRows,
											textualValueList,property,obj );
		}
		menu.show(this.cernatoTableView,(int)xPos,(int)yPos);
	}

	protected JPopupMenu createPopupMenu(int numOfCol,int numOfRows, AttributeValue[] textualValueList,
											final ManyValuedAttribute property, 
												final FCAObject obj) {
		JPopupMenu menu = new JPopupMenu();
		menu.setLayout(new GridLayout(numOfRows ,numOfCol));
		for(int i = 0 ; i < textualValueList.length ; i++){
			final AttributeValue textualValue = (AttributeValue) textualValueList[i];
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
	
	protected String getObjectName(int index) {
		ArrayList objectList = (ArrayList) context.getObjects();
		FCAObject object = (FCAObject) objectList.get(index);
		String objectName = object.getName();
		return objectName;
	}
	
	public WriteableManyValuedContext getContextTable(){
		return context;
	}
	
	public void update() {
		cernatoTableView.repaint();
		rowHeader.repaint();
		colHeader.repaint();
	}
				
	public Point getPoint(double x, double y){
		return new Point(x,y);
	}
	
	class Point{
		
		private int row;
		private int col;
		
		public Point(double x, double y){
			row = (int) x / TableView.CELL_WIDTH + 1 ;
			col = (int) y / TableView.CELL_HEIGHT + 1 ;
		}
		
		public int getRow(){
			return row;
		}
		public int getCol(){
			return col;
		}
	}
	public void addCriterion(Criterion c) {
		view.addCriterion(c);
		showDiagram(getDiagram());
	}
	
	public static void main(String[] args) {
		CernatoModel model = null;
		try {
			model =
				CernatoXMLParser.importCernatoXMLFile(
					new File(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (model != null) {
			WriteableManyValuedContext context = model.getContext();
			TableViewPanel c1 = new TableViewPanel(context);
			c1.setDefaultCloseOperation(EXIT_ON_CLOSE);
			c1.setSize(c1.getPreferredSize());
			c1.setVisible(true);
		}
	}
}
