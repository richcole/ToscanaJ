/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.context;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.gui.dialog.*;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAObject;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;

import javax.swing.*;

import org.jdom.Element;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @todo introduce OrderedContext interface or something similar to get rid of
 * the casts knowing about the internals of the ContextImplementation
 * 
 * @todo use dynamic cell widths
 * 
 * @todo avoid completely recreating the whole view each time the table changes
 */
public class ContextTableEditorDialog extends JDialog implements EventBrokerListener {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(ContextTableEditorDialog.class);
    
	private static final int MINIMUM_WIDTH = 700;
	private static final int MINIMUM_HEIGHT = 500;
	private static final int DEFAULT_X_POS = 50;
	private static final int DEFAULT_Y_POS = 100;

	private ContextTableEditorDialog contextTableScaleEditorDialog;
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
	private ContextTableColumnHeader colHeader;
	private ContextTableRowHeader rowHeader;

	public ContextTableEditorDialog(
		Frame owner,
		ConceptualSchema conceptualSchema,
		DatabaseConnection databaseConnection,
		EventBroker eventBroker) {
		this(
			owner,
			conceptualSchema,
			databaseConnection,
			new ContextImplementation(),
			eventBroker);
	}

	public ContextTableEditorDialog(
		Frame owner,
		ConceptualSchema conceptualSchema,
		DatabaseConnection databaseConnection,
		ContextImplementation context,
		EventBroker eventBroker) {
		super(owner, true);
		this.conceptualSchema = conceptualSchema;
		this.databaseConnection = databaseConnection;
		this.contextTableScaleEditorDialog = this;
		this.context = context;
		
		createView();

		eventBroker.subscribe(
			this,
			ConceptualSchemaLoadedEvent.class,
			Object.class);
		eventBroker.subscribe(
			this,
			NewConceptualSchemaEvent.class,
			Object.class);
		eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
	}

	private void createView() {
		setTitle("Context Table");
		preferences.restoreWindowPlacement(this,
                                			new Rectangle(
                                				DEFAULT_X_POS,
                                				DEFAULT_Y_POS,
                                				MINIMUM_WIDTH,
                                				MINIMUM_HEIGHT));
		onFirstLoad = true;
		// to enforce the minimum size during resizing of the JDialog
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width < MINIMUM_WIDTH)
					width = MINIMUM_WIDTH;
				if (height < MINIMUM_HEIGHT)
					height = MINIMUM_HEIGHT;
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

	private void getInput() {
		onFirstLoad = false;
		InputTextDialog dialog = new InputTextDialog(this, "New Title", "context", "");
		if (! dialog.isCancelled()) {
			String title = dialog.getInput();
			scaleTitleField.setText(title);
		}
		showObjectInputDialog();
		showAttributeInputDialog();
	}

	private void showObjectInputDialog() throws HeadlessException {
		final JButton doneButton = new JButton("Done");
		doneButton.setMnemonic('d');
		final JButton createObjButton = new JButton("Create");
		createObjButton.setMnemonic('c');
		final JTextField newNameField = new JTextField("", 20);
		newNameField.setFocusable(true);

		createObjButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addObject(doneButton, newNameField);
			}
		});
		newNameField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				doneButton.setEnabled(newNameField.getText().trim().equals(""));
				boolean createPossible =
					!rowHeader.collectionContainsString(newNameField.getText(),
						context.getObjects().toArray());
				createObjButton.setEnabled(createPossible);
				if (createPossible) {
					createObjButton.setToolTipText("Create a new object");
				} else {
					createObjButton.setToolTipText(
						"An object with this name already exists");
				}
			}
		});
		newNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rowHeader.collectionContainsString(newNameField.getText(),
					context.getObjects().toArray())) {
					return;
				}
				addObject(doneButton, newNameField);
			}
		});

		Object[] msg = { "Enter name of object: ", newNameField };
		Object[] buttons = { createObjButton, doneButton };
		final JOptionPane optionPane =
			new JOptionPane(
				msg,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null,
				buttons,
				msg[1]);
		final JDialog dialog = optionPane.createDialog(this, "Add object");
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);

		doneButton.addActionListener(new ActionListener() {
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
		createAttrButton.setMnemonic('c');
		final JTextField newNameField = new JTextField("", 20);
		newNameField.setFocusable(true);
		createAttrButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAttribute(doneButton, newNameField);
			}
		});
		newNameField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				doneButton.setEnabled(newNameField.getText().trim().equals(""));
				boolean createPossible =
					!colHeader.collectionContainsString(newNameField.getText(),
						context.getAttributes());
				createAttrButton.setEnabled(createPossible);
				if (createPossible) {
					createAttrButton.setToolTipText("Create a new attribute");
				} else {
					createAttrButton.setToolTipText(
						"An attribute with this name already exists");
				}
			}
		});
		newNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (colHeader.collectionContainsString(newNameField.getText(),
					context.getAttributes())) {
					return;
				}
				addAttribute(doneButton, newNameField);
			}
		});

		Object[] msg = { "Enter name of attribute: ", newNameField };
		Object[] buttons = { createAttrButton, doneButton };
		final JOptionPane optionPane =
			new JOptionPane(
				msg,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE,
				null,
				buttons,
				msg[1]);
		final JDialog dialog = optionPane.createDialog(this, "Add attribute");
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		dialog.show();
	}

	private void addObject(
		final JButton doneButton,
		final JTextField newNameField)
		throws HeadlessException {
		if (!newNameField.getText().trim().equals("")) {
			if(rowHeader.addObject(newNameField.getText())){
				updateView();
				newNameField.setText("");
			} else {
				JOptionPane.showMessageDialog(
					contextTableScaleEditorDialog,
					"An object named '"
						+ newNameField.getText()
						+ "' already exist. Please enter a different name.",
					"Object exists",
					JOptionPane.WARNING_MESSAGE);
				newNameField.setSelectionStart(0);
				newNameField.setSelectionEnd(newNameField.getText().length());
			}
		} else {
			JOptionPane.showMessageDialog(
				contextTableScaleEditorDialog,
				"Please provide an object name",
				"No name provided",
				JOptionPane.WARNING_MESSAGE);
		}
		newNameField.grabFocus();
		doneButton.setEnabled(true);
	}

	void updateView() {
		this.tableView.updateSize();
		this.tableView.revalidate();
		this.tableView.repaint();

		this.colHeader.updateSize();
		this.colHeader.revalidate();
		this.colHeader.repaint();

		this.rowHeader.updateSize();
		this.rowHeader.revalidate();
		this.rowHeader.repaint();
		
	}

	private void addAttribute(
		final JButton doneButton,
		final JTextField newNameField)
		throws HeadlessException {
		if (!newNameField.getText().trim().equals("")) {
			if (colHeader.addAttribute(newNameField.getText())) {
				updateView();
				newNameField.setText("");
			} else {
				JOptionPane.showMessageDialog(
					contextTableScaleEditorDialog,
					"An attribute named '"
						+ newNameField.getText()
						+ "' already exist. Please enter a different name.",
					"Attribute exists",
					JOptionPane.WARNING_MESSAGE);
				newNameField.setSelectionStart(0);
				newNameField.setSelectionEnd(newNameField.getText().length());
			}
		} else {
			JOptionPane.showMessageDialog(
				contextTableScaleEditorDialog,
				"Please provide an attribute name",
				"No name provided",
				JOptionPane.WARNING_MESSAGE);
		}
		newNameField.grabFocus();
		doneButton.setEnabled(true);
	}

	private void createTablePane() {
		tableView = new ContextTableView(context, this);
		tableView.addMouseListener(getMouseListener(tableView));
				
		this.rowHeader = new ContextTableRowHeader(this);
		this.colHeader = new ContextTableColumnHeader(this);
		
		scrollpane = new JScrollPane(tableView);
		scrollpane.setColumnHeaderView(colHeader);
		scrollpane.setRowHeaderView(rowHeader);
	}

	private void createTitlePane() {
		titlePane = new JPanel(new GridBagLayout());
		JLabel titleLabel = new JLabel("Title:");
		this.scaleTitleField = new JTextField();
		if (this.context.getName() != null) {
			this.scaleTitleField.setText(this.context.getName());
		}

		scaleTitleField.addKeyListener(new KeyListener() {
			private void validateTextField() {
				if (scaleTitleField.getText().trim().equals("")) {
					createButton.setEnabled(false);
				} else {
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
			public void keyPressed(KeyEvent e) {
			}
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
		JButton addObjButton = new JButton("Add Objects");
		JButton addAttrButton = new JButton("Add Attributes");

		this.checkConsistencyButton = new JButton("Check Consistency...");
		this.checkConsistencyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkConsistency();
			}
		});
		setCheckConsistencyButtonState();
		this.createButton = new JButton("Create");
		createButton.setEnabled(
			(scaleTitleField.getText() != null
				&& !scaleTitleField.getText().equals("")));
		JButton cancelButton = new JButton("Cancel");
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
		preferences.storeWindowPlacement(this);
		this.context.setName(this.scaleTitleField.getText());
		this.result = result;
		setVisible(false);
	}

	public boolean execute() {
		show();
		return result;
	}

	private MouseListener getMouseListener(final ContextTableView tableView) {
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				final ContextTableView.Position pos =
					tableView.getTablePosition(e.getX(), e.getY());
				if (pos == null) {
					return;
				}
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if (e.getClickCount() != 2) {
					return;
				}
				changeRelationImplementation(pos.getRow(), pos.getCol());
			}
		};
		return mouseListener;
	}

	private void changeRelationImplementation(
		int objectPos,
		int attributePos) {
			
		Set objectsSet = this.context.getObjects();
		WritableFCAObject[] objects = (WritableFCAObject[]) objectsSet.toArray(new WritableFCAObject[objectsSet.size()]);
		Set attributesSet = this.context.getAttributes();
		Attribute[] attributes = (Attribute[]) attributesSet.toArray(new Attribute[attributesSet.size()]);
		WritableFCAObject object = objects[objectPos];
		Attribute attribute = attributes[attributePos];
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
	protected void setCreateButtonStatus() {
		if (context.getAttributes().isEmpty()
			|| context.getObjects().isEmpty()) {
			createButton.setEnabled(false);
		} else {
			if (!scaleTitleField.getText().equals("")) {
				createButton.setEnabled(true);
			} else {
				createButton.setEnabled(false);
			}
		}
	}

	public ContextImplementation getContext() {
		return context;
	}

	public void paint(Graphics g) {
		super.paint(g);
		if ( onFirstLoad == true &&
					this.scaleTitleField.getText().length() == 0) {
			getInput();
		}
	}
	
	protected void checkConsistency() {
		try {
			List problems = ContextConsistencyChecker.checkConsistency(this.conceptualSchema, this.context,
														this.databaseConnection, this);
			// give feedback
			if (problems.isEmpty()) {
				JOptionPane.showMessageDialog(
					this,
					"No problems found",
					"Objects correct",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				// show problems
				Iterator strIt = problems.iterator();
				Element problemDescription = new Element("description");
				Element htmlElement = new Element("html");
				htmlElement.addContent(
					new Element("title").addContent("Consistency problems"));
				problemDescription.addContent(htmlElement);
				Element body = new Element("body");
				htmlElement.addContent(body);
				body.addContent(new Element("h1").addContent("Problems found:"));
				while (strIt.hasNext()) {
					String problem = (String) strIt.next();
					body.addContent(new Element("pre").addContent(problem));
				}
				Frame frame = JOptionPane.getFrameForComponent(this);
				DescriptionViewer.show(frame, problemDescription);
			}												
		} catch (Throwable t) {
			ErrorDialog.showError(this, t, "Internal error");
		}
	}
	
	public void processEvent(Event e) {
		if (e instanceof ConceptualSchemaChangeEvent) {
			ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
			this.conceptualSchema = csce.getConceptualSchema();
			setCheckConsistencyButtonState();
			return;
		}
		if (e instanceof DatabaseConnectedEvent) {
			DatabaseConnectedEvent dbce = (DatabaseConnectedEvent) e;
			this.databaseConnection = dbce.getConnection();
			setCheckConsistencyButtonState();
			return;
		}
		throw new RuntimeException("Caught event we don't know about");
	}

	private void setCheckConsistencyButtonState() {
		if(this.databaseConnection == null) {
			this.checkConsistencyButton.setEnabled(false);		
		} else {
			this.checkConsistencyButton.setEnabled(this.databaseConnection.isConnected());
		}
	}

	public void setContext(ContextImplementation context) {
		this.context = context;
		this.tableView.setContext(context);
		this.colHeader.updateSize();
		this.rowHeader.updateSize();
		this.scaleTitleField.setText(context.getName());
	}
	
	protected JScrollPane getScrollPane() {
		return scrollpane;
	}
	
	boolean collectionContainsString(
		String value,
		Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			Object obj = objects[i];
			if (obj.toString().equalsIgnoreCase(value.trim())) {
				return true;
			}
		}
		return false;
	}
		
	ContextTableView.Position getTablePosition(int xLoc, int yLoc) {
		return this.tableView.getTablePosition(xLoc, yLoc);
	}
	
	
}
