/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ContextImplementation;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ContextTableScaleEditorDialog extends JDialog {
    
    private boolean result;
	private ContextImplementation context;
	private ContextTableView tableView;
    private DatabaseConnection databaseConnection;

    private JTextField scaleTitleField;
    private JPanel buttonsPane;
    private JPanel titlePane;
	private ContextTableScaleEditorDialog contextTableScaleEditorDialog;
	private JScrollPane scrollpane;
	 
    public ContextTableScaleEditorDialog(Frame owner, DatabaseConnection databaseConnection) {
        super(owner);
        this.databaseConnection = databaseConnection;
        this.contextTableScaleEditorDialog = this;
		//for testing purposes
		 this.context = createDummyData();
        createView();
    }

    private void createView() {
        setModal(true);
        setTitle("Context Table Scale Generator");
		ConfigurationManager.restorePlacement("ContextTableScaleEditorDialog", 
						this, new Rectangle(250, 100, 500, 400));
        getContentPane().setLayout(new GridBagLayout());

     	createTitlePane();
				
		tableView = new ContextTableView(context);
        scrollpane = new JScrollPane(tableView);

		scrollpane.addMouseListener(getMouseListener(context,tableView));
		
		createButtonsPane();
		
        getContentPane().add(
                titlePane,
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        getContentPane().add(
                scrollpane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
		getContentPane().add(
				buttonsPane,
				new GridBagConstraints(
						0, 2, 1, 1, 1, 0,
						GridBagConstraints.CENTER,
						GridBagConstraints.BOTH,
						new Insets(1, 5, 5, 5),
						0, 0
				)
		);
    }
	
	private void createTitlePane(){
		 titlePane = new JPanel(new GridBagLayout());
		 JLabel titleLabel = new JLabel("Title:");
		 this.scaleTitleField = new JTextField();
		 titlePane.add(
				 titleLabel,
				 new GridBagConstraints(
						 0, 0, 1, 1, 0, 0,
						 GridBagConstraints.CENTER,
						 GridBagConstraints.HORIZONTAL,
						 new Insets(5, 5, 5, 5),
						 0, 0
				 )
		 );
		 titlePane.add(
				 scaleTitleField,
				 new GridBagConstraints(
						 1, 0, 1, 1, 1, 0,
						 GridBagConstraints.CENTER,
						 GridBagConstraints.HORIZONTAL,
						 new Insets(5, 5, 5, 5),
						 0, 0
				 )
		 );
	}
	
	private void createButtonsPane(){
		buttonsPane = new JPanel(new GridBagLayout());
		JButton addObj = new JButton(" Add Object ");	
		JButton addAttr = new JButton(" Add Attribute ");
		JButton create = new JButton(" Create ");
		JButton cancel= new JButton(" Cancel ");
		
		addObj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputValue = showAddInputDialog("Add Object", "object");
				if(inputValue!=null && !inputValue.trim().equals("")){
					context.getObjects().add(inputValue);
					scrollpane.updateUI();
				}
			}
		});
		addAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String inputValue = showAddInputDialog("Add Attribute", "attribute");
				context.getAttributes().add(inputValue);
				scrollpane.updateUI();
			}
		});
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("create");
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		
		buttonsPane.add(
			   addObj,
			   new GridBagConstraints(
					   0, 0, 1, 1, 1, 0,
					   GridBagConstraints.WEST,
					   GridBagConstraints.HORIZONTAL,
					   new Insets(0, 0, 0, 5),
					   0, 0
			   )
	   );
	   buttonsPane.add(
			   addAttr,
			   new GridBagConstraints(
					   1, 0, 1, 1, 1, 0,
					   GridBagConstraints.WEST,
					   GridBagConstraints.BOTH,
					   new Insets(0, 5, 0, 5),
					   0, 0
			   )
	   );
	   buttonsPane.add(
			  create,
			  new GridBagConstraints(
					  2, 0, 1, 1, 1, 0,
					  GridBagConstraints.EAST,
					  GridBagConstraints.HORIZONTAL,
					  new Insets(0, 100, 0, 5),
					  0, 0
			  )
	  );
	  buttonsPane.add(
			  cancel,
			  new GridBagConstraints(
					  3, 0, 1, 1, 1, 0,
					  GridBagConstraints.EAST,
					  GridBagConstraints.BOTH,
					  new Insets(0, 5, 0, 0),
					  0, 0
			  )
	  );	   
	}
	
	private void closeDialog(){
		ConfigurationManager.storePlacement("ContextTableScaleEditorDialog",this);
		this.setVisible(false);
	}
	
	/**
	  * To display the dialog asking for the object or attribute input name
	  * @param title The title of the dialog
	  * @param thingToAdd The string of the element to be added, either an
	  * "object" or "attribute". To be used in the formatting of the text
	  * message prompt in the JDialog
	  * @return The name of the object/ attribute
	  */
	private String showAddInputDialog(String title, String thingToAdd){
		String inputValue = "";
		do {
			inputValue = JOptionPane.showInputDialog(
				contextTableScaleEditorDialog,
				"Please input the name of the " +
				thingToAdd+ ": ", 
				title,
				JOptionPane.PLAIN_MESSAGE);
		} while(inputValue!=null && inputValue.equals(""));
		
		return inputValue;
	}
	
	private ContextImplementation createDummyData(){
		ContextImplementation context = new ContextImplementation();
		String o1 = "one";
		String o2 = "two";
		String o3 = "three";
		String o4 = "four";
		String a1 = "Aone";
		String a2 = "Atwo";
		String a3 = "Athree";

		context.getObjects().add(o1);
		context.getObjects().add(o2);
		context.getObjects().add(o3);
		context.getObjects().add(o4);

		context.getAttributes().add(a1);
		context.getAttributes().add(a2);
		context.getAttributes().add(a3);

		context.getRelationImplementation().insert(o1,a1);
		context.getRelationImplementation().insert(o1,a2);
		context.getRelationImplementation().insert(o2,a2);
		context.getRelationImplementation().insert(o3,a3);
		context.getRelationImplementation().insert(o4,a3);
		
		return context;
	}
	
    public boolean execute() {
        result = false;
        show();
        return result;
    }

    public String getDiagramTitle() {
        return this.scaleTitleField.getText();
    }
    
	public MouseListener getMouseListener(final ContextImplementation context,
													final ContextTableView tableView) {
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					ArrayList attributeArrayList = (ArrayList) context.getAttributes();
					ArrayList objectsArrayList = (ArrayList) context.getObjects();
					//User clicks within the table
					int xPos = e.getX() - tableView.getX();
					int yPos = e.getY() - tableView.getY();
					if (yPos<= (objectsArrayList.size() + 1)* tableView.getCellHeight() && 
								xPos<= (attributeArrayList.size() + 1)* tableView.getCellWidth()) {
						//User clicks within the relation cell
						if (yPos > tableView.getCellHeight()
							&& xPos > tableView.getCellWidth()) {
							String attribute = (String)attributeArrayList.get((xPos/tableView.getCellWidth())-1);
							String object = (String)objectsArrayList.get((yPos/tableView.getCellHeight())-1);
							//Check if there is relation between the object and attribute
							if(context.getRelationImplementation().contains(object , attribute)){
								context.getRelationImplementation().remove( object , attribute);
							}else{
								context.getRelationImplementation().insert( object , attribute );
							}
							tableView.repaint();
						} 
						//User clicks on the first cell of the table
						else if (
							yPos < tableView.getCellHeight()
								&& xPos < tableView.getCellWidth()) {
						}
						//User clicks on object or attribute cell to change the name 
						else {
						}
					}
				}
			}
		};
		return mouseListener;
	}
}
