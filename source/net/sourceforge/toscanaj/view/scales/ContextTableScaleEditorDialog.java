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
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Attribute;

import javax.swing.*;

import org.jdom.Element;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @todo introduced OderedContext interface or something similar to get rid of
 * the casts knowing about the internals of the ContextImplementation
 */
public class ContextTableScaleEditorDialog extends JDialog implements EventBrokerListener {

	private static final String CONFIGURATION_SECTION_NAME = "ContextTableEditorDialog";
    private static final int MINIMUM_WIDTH = 700;
	private static final int MINIMUM_HEIGHT = 500;
	private static final int DEFAULT_X_POS = 50;
	private static final int DEFAULT_Y_POS = 100;

	private ContextTableScaleEditorDialog contextTableScaleEditorDialog;
	private ConceptualSchema conceptualSchema;
	private ContextImplementation context;
	private ContextTableView tableView;
	private DatabaseConnection databaseConnection;

	private boolean result;
	private boolean onFirstLoad; 

	private JTextField scaleTitleField;
	private JButton createButton;
	private JPanel buttonsPane, titlePane;
	private JScrollPane scrollpane;
    private JButton checkConsistencyButton;

    public ContextTableScaleEditorDialog(Frame owner, ConceptualSchema conceptualSchema, DatabaseConnection databaseConnection,
    									  EventBroker eventBroker) {
    	this(owner, conceptualSchema, databaseConnection, new ContextImplementation(), eventBroker);
    }

    public ContextTableScaleEditorDialog(Frame owner, ConceptualSchema conceptualSchema, 
    									  DatabaseConnection databaseConnection, ContextImplementation context,
    									  EventBroker eventBroker) {
        super(owner,true);
        this.conceptualSchema = conceptualSchema;
        this.databaseConnection = databaseConnection;
        this.contextTableScaleEditorDialog = this;
        this.context = context;
        
        createView();
        
        eventBroker.subscribe(this, ConceptualSchemaLoadedEvent.class, Object.class);
    	eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
    	eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
    }

	private void createView() {
		setTitle("Context Table");
		ConfigurationManager.restorePlacement(CONFIGURATION_SECTION_NAME, 
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
			public void componentShown(ComponentEvent e) {
				componentResized(e);
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
		String title = showTextInputDialog("New Title", "context","");
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
				updateView();
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

	private void updateView() {
	    this.tableView.updateSize();
		this.tableView.revalidate();
		this.tableView.repaint();
	}

	private void addAttribute(final JButton doneButton, final JTextField newNameField)
		throws HeadlessException {
		if(!newNameField.getText().trim().equals("")){
			if(!collectionContainsString(newNameField.getText(),context.getAttributes())){
				context.getAttributes().add(new Attribute(newNameField.getText()));
				updateView();
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
		if(this.context.getName() != null) {
			this.scaleTitleField.setText(this.context.getName());
		}
		
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
		JButton addObjButton = new JButton(" Add Objects ");
		JButton addAttrButton = new JButton(" Add Attributes ");
        checkConsistencyButton = new JButton(" Check Consistency... ");
		checkConsistencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkConsistency();
			}
		});
		checkConsistencyButton.setEnabled(false);
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
			checkConsistencyButton,
			new GridBagConstraints(
				2,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 50, 0, 5),
				0,
				0));
		buttonsPane.add(
			createButton,
			new GridBagConstraints(
				3,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 50, 0, 5),
				0,
				0));
		buttonsPane.add(
			cancelButton,
			new GridBagConstraints(
				4,
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
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME,this);
		this.context.setName(this.scaleTitleField.getText());
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
	
	private void removeObject(int pos) {
		List objects = (List) this.context.getObjects();
		objects.remove(pos);
	    updateView();
	}

    private void removeAttribute(int pos) {
        List attributes = (List) this.context.getAttributes();
        attributes.remove(pos);
        updateView();
    }

	private MouseListener getMouseListener(final ContextTableView tableView) {
		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					final ContextTableView.Position pos = tableView.getTablePosition(e.getX(), e.getY());
					if( pos == null ) {
						return;
					} else {
                        showPopupMenu(e, pos);
					}
				}			
			}

            public void showPopupMenu(MouseEvent e, final ContextTableView.Position pos) {
                if (pos.getCol() == 0 && pos.getRow()!=0){
                	JPopupMenu popupMenu = new JPopupMenu();
                	JMenuItem rename = new JMenuItem("Rename Object");
                	rename.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e) {
                			renameObject( pos.getRow() - 1);
                		}
                	});
                	JMenuItem remove = new JMenuItem("Remove Object");
                	remove.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e) {
                			removeObject(pos.getRow()-1);
                		}
                	});
                	popupMenu.add(rename);
                	popupMenu.add(remove);
                	popupMenu.show(scrollpane, e.getX(), e.getY());
                } else if (pos.getRow() == 0 && pos.getCol()!=0) {
                	JPopupMenu popupMenu = new JPopupMenu();
                	JMenuItem rename = new JMenuItem("Rename Attribute");
                	rename.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e) {
                			renameAttribute(pos.getCol() - 1);
                		}
                	});
                	JMenuItem remove = new JMenuItem("Remove Attribute");
                	remove.addActionListener(new ActionListener() {
                		public void actionPerformed(ActionEvent e) {
                				removeAttribute(pos.getCol()-1);
                		}
                	});
                	popupMenu.add(rename);
                	popupMenu.add(remove);
                	popupMenu.show(scrollpane, e.getX(), e.getY());
                }
            }
			
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					final ContextTableView.Position pos = tableView.getTablePosition(e.getX(), e.getY());
					if( pos == null ) {
						return;
					} else{
					    showPopupMenu(e, pos);
					}
				}
			}
			
			public void mouseClicked(MouseEvent e) {
				final ContextTableView.Position pos = tableView.getTablePosition(e.getX(), e.getY());
				if( pos == null ) {
					return;
				}
				if(e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if(e.getClickCount() != 2) {
					return;
				}				
				
				if( pos.getCol() == 0 ) {
					if( pos.getRow() != 0) {
					    renameObject(pos.getRow() - 1);
					} 
				} else {
					if( pos.getRow() == 0) {
						renameAttribute(pos.getCol() - 1);
					} else {
						changeRelationImplementation(pos.getRow() - 1, pos.getCol() - 1);
					}
				}				
			}
		}; 
		return mouseListener;
	}
	
    private void renameObject(int num) {
        List objectList = (List) this.context.getObjects();
        String inputValue = "";
        do{
            String object = (String) objectList.get(num);
            inputValue = showTextInputDialog("Rename Object", "object", object);
            if (inputValue != null && !inputValue.trim().equals("")) {
                if(!collectionContainsString(inputValue, objectList)) {
                    objectList.set(num, inputValue);
                    updateView();
                    inputValue = "";
                } else {
                    JOptionPane.showMessageDialog(this, "An object named '" + inputValue +
                                                    "' already exist. Please enter a different name.","Object exists",
                                                    JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        } while (collectionContainsString(inputValue, objectList) );
        updateView();
    }

    private void renameAttribute(int num) {
        List attributeList = (List) this.context.getAttributes();
        String inputValue = "";
        do{
            Attribute attr = (Attribute) attributeList.get(num);
            inputValue = showTextInputDialog("Rename Attribute", "attribute", attr.toString());
            if (inputValue != null && !inputValue.trim().equals("")) {
                if(!collectionContainsString(inputValue, attributeList)) {
                    Attribute attribute = (Attribute) attributeList.get(num);
                    attribute.setData(inputValue);
                    updateView();
                    inputValue = "";
                } else {
                    JOptionPane.showMessageDialog(this, "An attribute named '" + inputValue +
                                                    "' already exist. Please enter a different name.","Attribute exists",
                                                    JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        } while (collectionContainsString(inputValue, attributeList) );
        updateView();
    }

	private void changeRelationImplementation(int objectPos, int attributePos) {
	    List objectList = (List) this.context.getObjects();
	    List attributeList = (List) this.context.getAttributes();
	    Object object = objectList.get(objectPos);
	    Attribute attribute = (Attribute) attributeList.get(attributePos);
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
	
	protected void checkConsistency() {
		List problems = new ArrayList();
		DatabaseInfo dbinfo = this.conceptualSchema.getDatabaseInfo();
		int sumCounts = 0;
		List validClauses = new ArrayList();

		// check if all objects are WHERE clauses
		Iterator it = this.context.getObjects().iterator();
		while (it.hasNext()) {
			String clause = (String) it.next();
			String query = "SELECT count(*) FROM " + dbinfo.getTableName() + 
			                  " WHERE (" + clause + ");";
			try {
				sumCounts += this.databaseConnection.queryNumber(query, 1);
				validClauses.add(clause);
			} catch (DatabaseException e) {
				problems.add("Object '" + clause + "' is not a valid clause for the database.\n" +
							 "The database returned:\n\t" + e.getCause().getMessage());
			}
		}

		// check if all conjunctions are empty
		it = this.context.getObjects().iterator();
		while (it.hasNext()) {
			String clause = (String) it.next();
			validClauses.remove(clause);
			Iterator it2 = validClauses.iterator();
			while (it2.hasNext()) {
				String otherClause = (String) it2.next();
				String query = "SELECT count(*) FROM " + dbinfo.getTableName() +
				               " WHERE (" + clause + ") AND (" + otherClause + ");";
				try {
					int count = this.databaseConnection.queryNumber(query, 1);
					if(count != 0) {
						problems.add("Object clauses '" + clause + "' and '" +
						             otherClause + "' overlap.");
					}
				} catch (DatabaseException e) {
					// should not happen
					throw new RuntimeException("Failed to query the database.", e);
				}
			}
		}

		// check if disjunction of all contingents covers the data set
		if(problems.isEmpty()) { // doesn't make sense if we have problems so far
			String query = "SELECT count(*) FROM " + dbinfo.getTableName() + ";";
			try {
				int count = this.databaseConnection.queryNumber(query, 1);
				if(count != sumCounts) {
					problems.add("Object clauses do not cover database.");
				}
			} catch (DatabaseException e) {
				// should not happen
				throw new RuntimeException("Failed to query the database.", e);
			}
		}

		// give feedback
		if(problems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No problems found","Objects correct",
			                              JOptionPane.INFORMATION_MESSAGE);
		} else {
			// show problems
			Iterator strIt = problems.iterator();
			Element problemDescription = new Element("description");
			Element htmlElement = new Element("html");
			htmlElement.addContent(new Element("title").addContent("Consistency problems"));
			problemDescription.addContent(htmlElement);
			Element body = new Element("body");
			htmlElement.addContent(body);
			body.addContent(new Element("h1").addContent("Problems found:"));
			while (strIt.hasNext()) {
				String problem = (String) strIt.next();
				body.addContent(new Element("pre").addContent(problem));
			}
			Frame frame = JOptionPane.getFrameForComponent(this);
			DescriptionViewer.show(frame,problemDescription);
		}
	}

    public void processEvent(Event e) {
    	if(e instanceof ConceptualSchemaChangeEvent) {
	    	ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
	    	this.conceptualSchema = csce.getConceptualSchema();
	    	if(this.databaseConnection == null) {
	    	    this.checkConsistencyButton.setEnabled(false);
	    	} else {
    	    	this.checkConsistencyButton.setEnabled(this.databaseConnection.isConnected());
	    	}
	    	return;
    	}
    	if(e instanceof DatabaseConnectedEvent) {
    		DatabaseConnectedEvent dbce = (DatabaseConnectedEvent) e;
    		this.databaseConnection = dbce.getConnection();
    		this.checkConsistencyButton.setEnabled(this.databaseConnection.isConnected());
    		return;
    	}
    	throw new RuntimeException("Caught event we don't know about");
    }
    
    public void setContext(ContextImplementation context) {
        this.context = context;
        this.tableView.setContext(context);
        this.scaleTitleField.setText(context.getName());
    }
}
