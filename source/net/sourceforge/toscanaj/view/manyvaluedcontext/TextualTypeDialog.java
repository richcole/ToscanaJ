/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualValue;

public class TextualTypeDialog extends JDialog implements DragGestureListener , 
													DropTargetListener , DragSourceListener {
	
	private JTextField typeNameField;
	private JTextField valueNameField;
	private JButton addButton;
	private JButton removeButton;
	private JButton replaceButton;
	private JList valuesList;
	private TextualType textualType;
	private DragSource dragSource;
	private JButton applyButton;
	
	public TextualTypeDialog(PropertiesDialog dialog,AttributeType type){
		super(dialog, "Many Valued-context:Type",true);
		textualType = (TextualType) type;
		setContentPane(createView());
		setBounds(100,100,500,350);
		
		this.dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this.valuesList, DnDConstants.ACTION_MOVE
													  ,this);
		show();
	}
	
	protected JPanel createView(){
		JPanel mainPane = new JPanel(new GridBagLayout());
		mainPane.add(createTypeNamePane(), new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));
								
		mainPane.add(createTabPane(), new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.BOTH,
								new Insets(2,2,2,2),
								2,2
								));
		mainPane.add(createButtonPane(), new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.BOTH,
								new Insets(2,2,0,2),
								2,2
								));
		return mainPane;
	}
	
	protected JPanel createButtonPane() {
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		applyButton = new JButton("Apply");
		JButton closeButton = new JButton("Close");
		
		applyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!textualType.getName().equals(typeNameField.getText())){
					textualType.setName(typeNameField.getText());
				}
				dispose() ;
			}
		});
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		
		buttonPane.add(applyButton);
		buttonPane.add(closeButton);
		
		return buttonPane;
	}
	
	protected JTabbedPane createTabPane() {
		JTabbedPane tabPane =  new JTabbedPane();
		tabPane.add(createValueEditingPane(),"Values");
		return tabPane;
	}

	protected JPanel createValueEditingPane() {
		JPanel valueEditingPane = new JPanel(new GridBagLayout());
		JLabel valueNameLabel = new JLabel("Value:");
		
		valueNameField = new JTextField();
		
		valueEditingPane.add(valueNameLabel, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								0,0
								));
		valueEditingPane.add(valueNameField, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,0,2),
								0,0
								));
		
		JPanel buttonPane = new JPanel(new GridBagLayout());
		addButton = new JButton("Add");
		replaceButton = new JButton("Replace");
		removeButton = new JButton("Remove");
		
		addButton.setEnabled(false);
		replaceButton.setEnabled(false);
		removeButton.setEnabled(false);
		
		removeButton.addActionListener(getRemoveButtonListener());
		addButton.addActionListener(getAddButtonActionListener());
		replaceButton.addActionListener(getReplaceButtonActionListener());
		
		buttonPane.add(addButton, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,2,0,2),
								0,0
								));
		buttonPane.add(replaceButton, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,2,0,2),
								0,0
								));
		buttonPane.add(removeButton,new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,2,0,2),
								0,0
								));
		
		valueEditingPane.add(buttonPane, new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,2,0,2),
								0,0
								));
		
		
		valuesList = new JList(textualType.getValueRange());
		JScrollPane scrollPane = new JScrollPane(valuesList);
		valuesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		valuesList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if(valuesList.getSelectedIndex()!=-1){
					valueNameField.setText(valuesList.getSelectedValue().toString());
					removeButton.setEnabled(true);
				}
				else{
					removeButton.setEnabled(false);
				}
						
			}
		});
		valueEditingPane.add(scrollPane, new GridBagConstraints(
								1,1,2,3,4,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								0,0
								));
		valueNameField.addKeyListener(getValueNameFieldKeyListener());
		
		return valueEditingPane;
	}
	
	protected ActionListener getAddButtonActionListener() {
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!valueNameField.getText().equals("")){
					TextualValue textualValue = new TextualValue(valueNameField.getText().trim());
					textualType.addValue(textualValue);
					valueNameField.setText("");
					valuesList.setListData(textualType.getValueRange());
				}
			}
		};
		return actionListener;
	}
	
	protected ActionListener getReplaceButtonActionListener() {
			ActionListener actionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(!valueNameField.getText().equals("")){
						TextualValue textualValue = new TextualValue(valueNameField.getText().trim());
						int selectedIndex = valuesList.getSelectedIndex();
						textualType.replaceValue(textualValue,selectedIndex);
						valueNameField.setText("");
						valuesList.setListData(textualType.getValueRange());
					}
				}
			};
			return actionListener;
		}
	
	protected ActionListener getRemoveButtonListener() {
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = valuesList.getSelectedIndex();
				textualType.removeValue(selectedIndex);
				removeButton.setEnabled(false);
				valueNameField.setText("");
				valuesList.setListData(textualType.getValueRange());
			}
		};
		
		return actionListener;
	}
	
	protected KeyListener getValueNameFieldKeyListener(){
		KeyListener keyListener = new KeyListener(){
			public void keyTyped(KeyEvent e) {
				keyListenerConditions();
			}
			public void keyPressed(KeyEvent e) {
				keyListenerConditions();
			}
			public void keyReleased(KeyEvent e) {
				keyListenerConditions();
			}
			protected void keyListenerConditions(){
				if((valueNameField.getText().trim()).equals("")){
					addButton.setEnabled(false);
					replaceButton.setEnabled(false);
				}

				else if(textualType.isDuplicatedValue(new 
									TextualValue(valueNameField.getText().trim()))){
					addButton.setEnabled(false);
					replaceButton.setEnabled(false);
				}

				else if(valuesList.getSelectedIndex()!= -1 && 
							!valueNameField.getText().equals
									(valuesList.getSelectedValue().toString())){
					addButton.setEnabled(true);
					replaceButton.setEnabled(true);
				}
				else if(!valueNameField.getText().equals("")){
					addButton.setEnabled(true);
				}
			}
		};
		return keyListener;
	}
	
	protected JPanel createTypeNamePane() {
		JPanel typeNamePane = new JPanel(new GridBagLayout());
		JLabel typeNameLabel = new JLabel("Name of Type: ");
		typeNameField = new JTextField(textualType.getName());
		typeNameField.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {
				if((typeNameField.getText().trim()).equals(""))
					applyButton.setEnabled(false);
			}
			public void keyPressed(KeyEvent e) {
				if((typeNameField.getText().trim()).equals(""))
					applyButton.setEnabled(false);
			}
			public void keyReleased(KeyEvent e) {
				if((typeNameField.getText().trim()).equals(""))
					applyButton.setEnabled(false);
			}
			
		});
		
		typeNamePane.add(typeNameLabel, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
		typeNamePane.add(typeNameField, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));
		return typeNamePane;
	}
	
	
	/**
	* a drag gesture has been initiated
	*/
    public void dragGestureRecognized(DragGestureEvent event){
    	int itemDragged = valuesList.locationToIndex(event.getDragOrigin());
		StringSelection transferable = new StringSelection((new Integer(itemDragged)).toString());
		event.startDrag(null, transferable, this);
    }
	
    public void dragEnter(DropTargetDragEvent event) {
    }
	
   /**
	* is invoked when a drag operation is going on
	*
	*/
    public void dragOver(DropTargetDragEvent event) {
    }


    public void dropActionChanged(DropTargetDragEvent event) {
    }

   /**
	* is invoked when you are exit the DropSite without dropping
	*
	*/
    public void dragExit(DropTargetEvent event) {
    }
	
   /**
	* a drop has occurred
	*
	*/
    public void drop(DropTargetDropEvent event) {
    	try {	
    		Transferable transferable = event.getTransferable();
			if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
				int startIndex = Integer.parseInt(s);
				int endIndex = this.valuesList.locationToIndex(event.getLocation());	
				textualType.move(startIndex, endIndex);
				valuesList.setListData(textualType.getValueRange());				
				event.getDropTargetContext().dropComplete(true);
				} else {
					event.rejectDrop();
					}
			} catch (IOException exception) {
				exception.printStackTrace();
				event.rejectDrop();
			} catch (UnsupportedFlavorException ufException) {
				ufException.printStackTrace();
				event.rejectDrop();
			}
    }

   /**
	* this message goes to DragSourceListener, informing it that the dragging
	* has entered the DropSite
	*/
    public void dragEnter(DragSourceDragEvent event) {
    }

   /**
	* this message goes to DragSourceListener, informing it that the dragging is currently
	* ocurring over the DropSite
	*/
    public void dragOver(DragSourceDragEvent event) {
    }

   /**
	* is invoked if the use modifies the current drop gesture
	*/
    public void dropActionChanged(DragSourceDragEvent event) {
    }

   /**
	* this message goes to DragSourceListener, informing it that the dragging
	* has exited the DropSite
   */
    public void dragExit(DragSourceEvent event) {
    }
	
   /**
	* this message goes to DragSourceListener, informing it that the dragging
	* has ended
	*/
    public void dragDropEnd(DragSourceDropEvent event) {
    }
 }
