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
import net.sourceforge.toscanaj.model.lattice.Attribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ContextTableScaleEditorDialog extends JDialog {

	private boolean result;
	private ContextImplementation context;
	private ContextTableView tableView;
	private DatabaseConnection databaseConnection;
	private static final int MINIMUM_WIDTH = 550;
	private static final int MINIMUM_HEIGHT = 500;
	private static final int DEFAULT_X_POS = 250;
	private static final int DEFAULT_Y_POS = 100;

	private JTextField scaleTitleField;
	private JButton createButton;
	private JPanel buttonsPane;
	private JPanel titlePane;
	private ContextTableScaleEditorDialog contextTableScaleEditorDialog;
	private JScrollPane scrollpane;

	public ContextTableScaleEditorDialog(
		Frame owner,
		DatabaseConnection databaseConnection) {
		super(owner);
		this.databaseConnection = databaseConnection;
		this.contextTableScaleEditorDialog = this;
		//for testing purposes
		//this.context = createDummyData();
		this.context = new ContextImplementation();
		createView();
	}

	private void createView() {
		setModal(true);
		setTitle("Context Table Scale Generator");
		ConfigurationManager.restorePlacement("ContextTableScaleEditorDialog", 
			this, new Rectangle(DEFAULT_X_POS, DEFAULT_Y_POS, MINIMUM_WIDTH, MINIMUM_HEIGHT));
		// to enforce the minimum size during resizing of the JDialog
		addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width < MINIMUM_WIDTH) width = MINIMUM_WIDTH;
				if (height < MINIMUM_HEIGHT) height = MINIMUM_HEIGHT;
				setSize(width, height);
			}
		});
		
		createTitlePane();
		tableView = new ContextTableView(context, this);
		scrollpane = new JScrollPane(tableView);
		tableView.addMouseListener(getMouseListener(tableView));
		createButtonsPane();

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(
			titlePane,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				0,
				0));
		getContentPane().add(
			scrollpane,
			new GridBagConstraints(
				0,
				1,
				1,
				1,
				1,
				1,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5),
				0,
				0));
		getContentPane().add(
			buttonsPane,
			new GridBagConstraints(
				0,
				2,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(1, 5, 5, 5),
				0,
				0));
	}

	private void createTitlePane() {
		titlePane = new JPanel(new GridBagLayout());
		JLabel titleLabel = new JLabel("Title:");
		this.scaleTitleField = new JTextField();
		
		scaleTitleField.addKeyListener( new KeyListener(){
			private void validateTextField(){
				if(scaleTitleField.getText().trim().equals("")){
					createButton.setEnabled(false);
				}else{
					createButton.setEnabled(true);
				}
			}
			public void keyTyped(KeyEvent e) {
				validateTextField();
				setCreateButtonStatus();
			}
			public void keyReleased(KeyEvent e) {
				validateTextField();
				setCreateButtonStatus();
			}
			public void keyPressed(KeyEvent e) {}			
		});
		
		titlePane.add(
			titleLabel,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				0,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				0,
				0));
		titlePane.add(
			scaleTitleField,
			new GridBagConstraints(
				1,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				0,
				0));
	}

	private void createButtonsPane() {
		buttonsPane = new JPanel(new GridBagLayout());
		JButton addObjButton = new JButton(" Add Object ");
		JButton addAttrButton = new JButton(" Add Attribute ");
		this.createButton = new JButton(" Create ");
		createButton.setEnabled((scaleTitleField.getText()!=null && 
						!scaleTitleField.getText().equals("")));
		JButton cancelButton = new JButton(" Cancel ");
		addObjButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList objList = (ArrayList) context.getObjects();
				String inputValue = "";
				do{
				inputValue = showAddOrRenameInputDialog("Add Object", "object", "");
					if (inputValue != null && !inputValue.equals("")) {
						inputValue = inputValue.trim();
						if(!objectOrAttributeIsDuplicated(inputValue, objList, null)){
							context.getObjects().add(new Attribute(inputValue));
							scrollpane.updateUI();
							inputValue = "";	
						}else{
							JOptionPane.showMessageDialog(contextTableScaleEditorDialog,"An object named '"+inputValue+"' already exist. Please enter a different name.","Object exists",JOptionPane.WARNING_MESSAGE);
						}
					}else{
						break;
					}
				}while(objectOrAttributeIsDuplicated(inputValue, objList, null));
			}
		});
		addAttrButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ArrayList attrList = (ArrayList) context.getAttributes();
				String inputValue = "";
				do{
					inputValue = showAddOrRenameInputDialog("Add Attribute", "attribute", "");
					if (inputValue != null && !inputValue.equals("")) {
						inputValue = inputValue.trim();
						if(!objectOrAttributeIsDuplicated(inputValue, null, attrList)){
							context.getAttributes().add(new Attribute(inputValue));
							scrollpane.updateUI();
							inputValue = "";	
						}else{
							JOptionPane.showMessageDialog(contextTableScaleEditorDialog,"An attribute named '"+inputValue+"' already exist. Please enter a different name.","Attribute exists",JOptionPane.WARNING_MESSAGE);
						}
					}else{
						break;
					}
				}while(objectOrAttributeIsDuplicated(inputValue, attrList, null));
			}
		});
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				result = true;
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		buttonsPane.add(
			addObjButton,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 5),
				0,
				0));
		buttonsPane.add(
			addAttrButton,
			new GridBagConstraints(
				1,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.WEST,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 5),
				0,
				0));
		buttonsPane.add(
			createButton,
			new GridBagConstraints(
				2,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 100, 0, 5),
				0,
				0));
		buttonsPane.add(
			cancelButton,
			new GridBagConstraints(
				3,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.EAST,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 0),
				0,
				0));
	}

	private void closeDialog() {
		ConfigurationManager.storePlacement("ContextTableScaleEditorDialog",this);
		dispose();
		result = false;
	}

	/**
	  * To display the dialog asking for the object or attribute input name
	  * @param title The title of the dialog
	  * @param thingToAdd The string of the element to be added, either an
	  * "object" or "attribute".
	  * @param currentTextValue The value of the current string.
	  * To be used in the formatting of the text message prompt in the JDialog
	  * @return The name of the object/ attribute
	  */
	private String showAddOrRenameInputDialog(String title, String thingToAdd, String currentTextValue) {
		String inputValue = "";
		do {
			inputValue =
				(String) JOptionPane.showInputDialog(
					contextTableScaleEditorDialog,
					"Please input the name of the " + thingToAdd + ": ",
					title,
					JOptionPane.PLAIN_MESSAGE,null,null, currentTextValue);
		} while (inputValue != null && inputValue.trim().equals(""));
		return inputValue;
	}
	
	private boolean objectOrAttributeIsDuplicated(String input, ArrayList objList, ArrayList attrList){
		boolean exists = false;
		ArrayList listToCheck = null; 
		if(objList!=null){
			//user adding/ renaming an object
			listToCheck = objList;
		} else {
			//user adding/ renaming an attribute
			listToCheck = attrList;
		}
		for(int i = 0;i< listToCheck.size();i++) {
			Object obj = listToCheck.get(i);
			if(obj.toString().equalsIgnoreCase(input.trim())){
				exists = true;
				break;
			}
		}
		return exists;
	}
	private ContextImplementation createDummyData() {
		ContextImplementation context = new ContextImplementation();
		String o1 = "Apple";
		String o2 = "Carrot";
		String o3 = "Papaya";
		String o4 = "Pineapple";
		String a1 = "Vitamin A";
		String a2 = "Vitamin C";
		String a3 = "Vitamin E";

		context.getObjects().add(o1);
		context.getObjects().add(o2);
		context.getObjects().add(o3);
		context.getObjects().add(o4);

		context.getAttributes().add(a1);
		context.getAttributes().add(a2);
		context.getAttributes().add(a3);

		context.getRelationImplementation().insert(o1, a2);
		context.getRelationImplementation().insert(o2, a2);
		context.getRelationImplementation().insert(o2, a1);
		context.getRelationImplementation().insert(o3, a2);
		context.getRelationImplementation().insert(o4, a2);

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

	public MouseListener getMouseListener(final ContextTableView tableView) {
		MouseListener mouseListener = new MouseAdapter() {
			final ArrayList attributeArrayList = (ArrayList) context.getAttributes();
			final ArrayList objectsArrayList =	(ArrayList) context.getObjects();
		
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					final ContextTableView.Position pos = tableView.getTablePosition(e.getX(), e.getY());
					if( pos == null ) {
						return;
					} else {
						if (pos.getCol() == 0 && pos.getRow()!=0){
							JPopupMenu popupMenu = new JPopupMenu();
							JMenuItem rename = new JMenuItem("Rename Object");
							rename.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									renameObjectAttribute( objectsArrayList, 
									attributeArrayList , pos.getCol() , pos.getRow());
							}});
							JMenuItem remove = new JMenuItem("Remove Object");
							remove.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									objectsArrayList.remove(pos.getRow()-1);
									scrollpane.updateUI();
							}});
							popupMenu.add(rename);
							popupMenu.add(remove);
							popupMenu.show(scrollpane, e.getX(), e.getY());
						} else if (pos.getRow() == 0 && pos.getCol()!=0) {
							JPopupMenu popupMenu = new JPopupMenu();
							JMenuItem rename = new JMenuItem("Rename Attribute");
							rename.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									renameObjectAttribute( objectsArrayList, 
									attributeArrayList , pos.getCol() , pos.getRow());
							}});
							JMenuItem remove = new JMenuItem("Remove Attribute");
							remove.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
										attributeArrayList.remove(pos.getCol()-1);
										scrollpane.updateUI();
							}});
							popupMenu.add(rename);
							popupMenu.add(remove);
							popupMenu.show(scrollpane, e.getX(), e.getY());
						}
					}
				}			
			}
			
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					final ContextTableView.Position pos = tableView.getTablePosition(e.getX(), e.getY());
					if( pos == null ) {
						return;
					} else{
						if (pos.getCol() == 0 && pos.getRow()!=0){
							JPopupMenu popupMenu = new JPopupMenu();
							JMenuItem rename = new JMenuItem("Rename Object");
							rename.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									renameObjectAttribute( objectsArrayList, 
									attributeArrayList , pos.getCol() , pos.getRow());
							}});
							JMenuItem remove = new JMenuItem("Remove Object");
							remove.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									objectsArrayList.remove(pos.getRow()-1);
									scrollpane.updateUI();
							}});
							popupMenu.add(rename);
							popupMenu.add(remove);
							popupMenu.show(scrollpane, e.getX(), e.getY());
						}
						else if(pos.getRow() == 0 && pos.getCol()!=0){
							JPopupMenu popupMenu = new JPopupMenu();
							JMenuItem rename = new JMenuItem("Rename Attribute");
							rename.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									renameObjectAttribute( objectsArrayList, 
									attributeArrayList , pos.getCol() , pos.getRow());
							}});
							JMenuItem remove = new JMenuItem("Remove Attribute");
							remove.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
										attributeArrayList.remove(pos.getCol()-1);
										scrollpane.updateUI();
							}});
							popupMenu.add(rename);
							popupMenu.add(remove);
							popupMenu.show(scrollpane, e.getX(), e.getY());
						}
					}
				}
			}
			
			public void mouseClicked(MouseEvent e) {
				final ContextTableView.Position pos = tableView.getTablePosition(e.getX(), e.getY());
				if( pos == null ) {
					return;
				}
				
				if( pos.getCol() == 0 ) {
					if( pos.getRow() != 0) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (e.getClickCount() == 2) {
								renameObjectAttribute(objectsArrayList,attributeArrayList,
								pos.getCol(),pos.getRow());
							} 
						} 
					} 
				} else {
					if( pos.getRow() == 0) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (e.getClickCount() == 2) {
								renameObjectAttribute(objectsArrayList,attributeArrayList,
								pos.getCol(),pos.getRow());
							} 
						} 
					} else {
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (e.getClickCount() == 2) {
								Attribute attribute = (Attribute) attributeArrayList.get(pos.getCol() - 1);
								Attribute object =	(Attribute) objectsArrayList.get(pos.getRow() - 1);
								changeRelationImplementation(object, attribute);
//								String attribute = (String) attributeArrayList.get(pos.getCol() - 1);
//								String object =	(String) objectsArrayList.get(pos.getRow() - 1);
//								changeRelationImplementation(object, attribute);
							}
						}
					}
				}
			}
		}; 
		return mouseListener;
	}
	
	public void renameObjectAttribute(
		ArrayList objectsArrayList,
		ArrayList attributeArrayList,
		int xP,
		int yP) {
		if (xP == 0) {
			//rename Object
			String inputValue = "";
			do{
				inputValue = showAddOrRenameInputDialog("Rename Object", "object", (String) objectsArrayList.get(yP - 1));
				if (inputValue != null && !inputValue.trim().equals("")) {
					inputValue = inputValue.trim();
					if(!objectOrAttributeIsDuplicated(inputValue, objectsArrayList, null)){
						objectsArrayList.remove(yP - 1);
						objectsArrayList.add(yP - 1, inputValue);
						scrollpane.updateUI();
						inputValue ="";
					}else{
						JOptionPane.showMessageDialog(this,"An object named '"+inputValue+"' already exist. Please enter a different name.","Object exists",JOptionPane.WARNING_MESSAGE);
					}
				}else{
					break;
				}
			}while(objectOrAttributeIsDuplicated(inputValue, objectsArrayList, null)==true);
		} else if (yP == 0) {
			String inputValue = "";
			do{
				//rename attribute
				inputValue = showAddOrRenameInputDialog("Rename Attribute", "attribute", (String) attributeArrayList.get(xP - 1));
				if (inputValue != null && !inputValue.trim().equals("")) {
					inputValue = inputValue.trim();
					if(!objectOrAttributeIsDuplicated(inputValue, null, attributeArrayList)){
						attributeArrayList.remove(xP - 1);
						attributeArrayList.add(xP - 1, inputValue);
						scrollpane.updateUI();
						inputValue = "";
					}else{
					JOptionPane.showMessageDialog(this,"An attribute named '"+inputValue+"' already exist. Please enter a different name.","Attribute exists",JOptionPane.WARNING_MESSAGE);
					}	
				}else{
					break;
				
				}
			}while(objectOrAttributeIsDuplicated(inputValue, null, attributeArrayList));
		}
	}
	public void changeRelationImplementation(Attribute object, Attribute attribute) {
		if (context.getRelationImplementation().contains(object, attribute)) {
			context.getRelationImplementation().remove(object, attribute);
		} else {
			context.getRelationImplementation().insert(object, attribute);
		}
		tableView.repaint();
	}
	/*
	 * Checks against the context whether there are any objects/attributes. If
	 * either one doesn't exist, disable the create button.
	 */ 
	protected void setCreateButtonStatus(){
		if(context.getAttributes().isEmpty() || context.getObjects().isEmpty()){
			createButton.setEnabled(false);
		}else{
			if(!scaleTitleField.getText().equals("")){
				createButton.setEnabled(true);
			}else{
				createButton.setEnabled(false);
			}
		}
	}

	public ContextImplementation getContext(){
		return context;	
	}
	
}
