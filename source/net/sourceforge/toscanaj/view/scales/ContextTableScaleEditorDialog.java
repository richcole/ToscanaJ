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
import java.util.Collection;
import java.util.Iterator;

public class ContextTableScaleEditorDialog extends JDialog {

	private static final int MINIMUM_WIDTH = 550;
	private static final int MINIMUM_HEIGHT = 500;
	private static final int DEFAULT_X_POS = 250;
	private static final int DEFAULT_Y_POS = 100;

	private ContextTableScaleEditorDialog contextTableScaleEditorDialog;
	private ContextImplementation context;
	private ContextTableView tableView;
	private DatabaseConnection databaseConnection;

	private boolean result;
	private boolean onFirstLoad; 

	private JTextField scaleTitleField;
	private JButton createButton;
	private JPanel buttonsPane, titlePane;
	private JScrollPane scrollpane;

	public ContextTableScaleEditorDialog(
		Frame owner,
		DatabaseConnection databaseConnection) {
		super(owner,true);
		this.databaseConnection = databaseConnection;
		this.contextTableScaleEditorDialog = this;
		this.context = new ContextImplementation();
		createView();
	}

	private void createView() {
		setTitle("Context Table Scale Generator");
		ConfigurationManager.restorePlacement("ContextTableScaleEditorDialog", 
			this, new Rectangle(DEFAULT_X_POS, DEFAULT_Y_POS, MINIMUM_WIDTH, MINIMUM_HEIGHT));
		onFirstLoad = true; 
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
		createTablePane();
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

	private void getInput(){
		onFirstLoad=false;
		String title = showTextInputDialog("New Title", "title","");
		scaleTitleField.setText(title);
		showObjectInputDialog();
		showAttributeInputDialog();
	}

	private void showObjectInputDialog() throws HeadlessException {
		final JButton doneButton = new JButton("Done");
		doneButton.setMnemonic('d');
		final JButton createObjButton = new JButton("Create");
		createObjButton .setMnemonic('c');
		final JTextField newNameField = new JTextField("",20);
		newNameField.setFocusable(true);
		
		createObjButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				addObject(doneButton, newNameField);
			}
		});
		newNameField.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				doneButton.setEnabled(newNameField.getText().trim().equals(""));
				boolean createPossible = !collectionContainsString(newNameField.getText(),context.getObjects());
				createObjButton.setEnabled(createPossible);
				if(createPossible) {
					createObjButton.setToolTipText("Create a new object");
				} else {
					createObjButton.setToolTipText("An object with this name already exists");
				}
			}		
		});
		newNameField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(collectionContainsString(newNameField.getText(),context.getObjects())) {
					return;
				}
				addObject(doneButton, newNameField);
			}
		});
		
		Object[] msg = {"Enter name of object: ", newNameField};
		Object[] buttons = {createObjButton, doneButton};
		final JOptionPane optionPane = new JOptionPane(msg, 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
					null, buttons, msg[1]);
		final JDialog dialog = optionPane.createDialog(this, "Add object");
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
				
		doneButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		dialog.show();
	}

	private void showAttributeInputDialog() throws HeadlessException {
		final JButton doneButton = new JButton("Done");
		doneButton.setMnemonic('d');
		final JButton createAttrButton = new JButton("Create");
		createAttrButton .setMnemonic('c');
		final JTextField newNameField = new JTextField("",20);
		newNameField.setFocusable(true);
		createAttrButton .addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				addAttribute(doneButton, newNameField);
			}
		});
		newNameField.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				doneButton.setEnabled(newNameField.getText().trim().equals(""));
				boolean createPossible = !collectionContainsString(newNameField.getText(),context.getAttributes());
				createAttrButton.setEnabled(createPossible);
				if(createPossible) {
					createAttrButton .setToolTipText("Create a new attribute");
				} else {
					createAttrButton .setToolTipText("An attribute with this name already exists");
				}
			}		
		});
		newNameField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(collectionContainsString(newNameField.getText(),context.getAttributes())) {
					return;
				}
				addAttribute(doneButton, newNameField);
			}
		});
		
		
		Object[] msg = {"Enter name of attribute: ", newNameField};
		Object[] buttons = {createAttrButton , doneButton};
		final JOptionPane optionPane = new JOptionPane(msg, 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
					null, buttons, msg[1]);
		final JDialog dialog = optionPane.createDialog(this, "Add attribute");
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		doneButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		dialog.show();
	}

	private void addObject(final JButton doneButton, final JTextField newNameField)
		throws HeadlessException {
		if(!newNameField.getText().trim().equals("")){
			if(!collectionContainsString(newNameField.getText(),context.getObjects())){
				context.getObjects().add(newNameField.getText());
				scrollpane.updateUI();
				newNameField.setText("");
			}else{
				JOptionPane.showMessageDialog(contextTableScaleEditorDialog,
				"An object named '"+newNameField.getText()+"' already exist. Please enter a different name.",
				"Object exists",
				JOptionPane.WARNING_MESSAGE);
				newNameField.setSelectionStart(0);
				newNameField.setSelectionEnd(newNameField.getText().length());
			}
		}else{
			JOptionPane.showMessageDialog(contextTableScaleEditorDialog, 
				"Please provide an object name", "No name provided",
				JOptionPane.WARNING_MESSAGE);
		}			
		newNameField.grabFocus();	
		doneButton.setEnabled(true);
	}

	private void addAttribute(final JButton doneButton, final JTextField newNameField)
		throws HeadlessException {
		if(!newNameField.getText().trim().equals("")){
			if(!collectionContainsString(newNameField.getText(),context.getAttributes())){
				context.getAttributes().add(new Attribute(newNameField.getText()));
				scrollpane.updateUI();
				newNameField.setText("");
			}else{
				JOptionPane.showMessageDialog(contextTableScaleEditorDialog,
				"An attribute named '"+newNameField.getText()+"' already exist. Please enter a different name.",
				"Attribute exists",
				JOptionPane.WARNING_MESSAGE);
				newNameField.setSelectionStart(0);
				newNameField.setSelectionEnd(newNameField.getText().length());
			}
		}else{
			JOptionPane.showMessageDialog(contextTableScaleEditorDialog, 
				"Please provide an attribute name", "No name provided",
				JOptionPane.WARNING_MESSAGE);
		}			
		newNameField.grabFocus();	
		doneButton.setEnabled(true);
	}

	private void createTablePane() {
		tableView = new ContextTableView(context, this);
		scrollpane = new JScrollPane(tableView);
		tableView.addMouseListener(getMouseListener(tableView));
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
				showObjectInputDialog();
			}
		});
		addAttrButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAttributeInputDialog();
			}
		});
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog(true);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog(false);
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
	
	private void closeDialog(boolean result) {
		ConfigurationManager.storePlacement("ContextTableScaleEditorDialog",this);
		this.result = result;
		setVisible(false);
	}

	/**
	  * To display the dialog asking for the object or attribute input name
	  * @param title The title of the dialog
	  * @param thingToAdd The string of the element to be added, either an
	  * "object" or "attribute" during renaming or the title during creation.
	  * @param currentTextValue The value of the current string.
	  * To be used in the formatting of the text message prompt in the JDialog
	  * @return The name of the object/ attribute
	  */
	private String showTextInputDialog(String title, String thingToAdd, String currentTextValue) {
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
	
	private boolean collectionContainsString(String value, Collection collection){
		Iterator it = collection.iterator();
		while (it.hasNext()) {
			Object obj = (Object) it.next();
			if(obj.toString().equalsIgnoreCase(value.trim())){
				return true;
			}
		}
		return false;
	}

	public boolean execute() {
		show();
		return result;
	}

	public String getDiagramTitle() {
		return this.scaleTitleField.getText();
	}

	private MouseListener getMouseListener(final ContextTableView tableView) {
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
									renameItem( objectsArrayList, 
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
									renameItem( objectsArrayList, 
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
									renameItem( objectsArrayList, 
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
									renameItem( objectsArrayList, 
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
								renameItem(objectsArrayList,attributeArrayList,
								pos.getCol(),pos.getRow());
							} 
						} 
					} 
				} else {
					if( pos.getRow() == 0) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (e.getClickCount() == 2) {
								renameItem(objectsArrayList,attributeArrayList,
								pos.getCol(),pos.getRow());
							} 
						} 
					} else {
						if (e.getButton() == MouseEvent.BUTTON1) {
							if (e.getClickCount() == 2) {
								Attribute attribute = (Attribute) attributeArrayList.get(pos.getCol() - 1);
								Object object = objectsArrayList.get(pos.getRow() - 1);
								changeRelationImplementation(object, attribute);
							}
						}
					}
				}
			}
		}; 
		return mouseListener;
	}
	
	private void renameItem(
		ArrayList objectsArrayList,
		ArrayList attributeArrayList,
		int xP,
		int yP) {
		if (xP == 0) {
			//rename Object
			String inputValue = "";
			do{
				Object obj = objectsArrayList.get(yP - 1); 
				inputValue = showTextInputDialog("Rename Object", "object", obj.toString());
				if (inputValue != null && !inputValue.trim().equals("")) {
					inputValue = inputValue.trim();
					if(!collectionContainsString(inputValue, objectsArrayList)){
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
			}while(collectionContainsString(inputValue, objectsArrayList));
		} else if (yP == 0) {
			//rename attribute
			String inputValue = "";
			do{
				Attribute attr = (Attribute) attributeArrayList.get(xP - 1);	
				inputValue = showTextInputDialog("Rename Attribute", "attribute", (String) attr.getData());
				if (inputValue != null && !inputValue.trim().equals("")) {
					inputValue = inputValue.trim();
					if(!collectionContainsString(inputValue, attributeArrayList)){
						Attribute attribute = (Attribute) attributeArrayList.get(xP - 1);
						attribute.setData(inputValue);
						scrollpane.updateUI();
						inputValue = "";
					}else{
					JOptionPane.showMessageDialog(this,"An attribute named '"+inputValue+"' already exist. Please enter a different name.","Attribute exists",JOptionPane.WARNING_MESSAGE);
					}	
				}else{
					break;
				
				}
			}while(collectionContainsString(inputValue, attributeArrayList));
		}
	}
	private void changeRelationImplementation(Object object, Attribute attribute) {
		if (context.getRelationImplementation().contains(object, attribute)) {
			context.getRelationImplementation().remove(object, attribute);
		} else {
			context.getRelationImplementation().insert(object, attribute);
		}
		tableView.repaint();
	}
	/*
	 * Checks against the context whether there are any objects/attributes. If
	 * either one doesn't exist, disable the button.
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
	
	public void paint(Graphics g) {
		super.paint(g);
		if(this.scaleTitleField.getText().length() == 0 && onFirstLoad==true) {
			getInput();
		}
	}
}
