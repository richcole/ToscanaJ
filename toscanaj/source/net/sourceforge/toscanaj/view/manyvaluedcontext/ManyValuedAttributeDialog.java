/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedAttribute;

import org.tockit.datatype.Datatype;

public class ManyValuedAttributeDialog extends JDialog{
	
	private JTextField nameTextField;
	private JButton cancelButton;
//	private JButton editTypeButton;
	private JButton changeButton;
	private WritableManyValuedAttribute property;
	private Frame parent;
	private JComboBox typeBox;
	private ManyValuedContext context;
	
	/**
	 * @todo this dialog could probably go with just the types instead of the whole context
	 * @todo use session management
	 * @todo either get object dialog to call show() in the constructor or remove it here
	 */
	public ManyValuedAttributeDialog(Frame parent,
										WritableManyValuedAttribute property, 
										ManyValuedContext context){
		super(parent,"Many Valued-Context:Properties",true);
		this.parent = parent;
		this.property = property;
		this.context = context;
		setContentPane(createView());
		setSize(300,200);
		setVisible(true);
	}
	
	protected JPanel createView(){
		JPanel mainPane = new JPanel(new GridBagLayout());
		mainPane.add(createPropertyNamePane(), new GridBagConstraints(
							0,0,1,1,1,1,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL,
							new Insets(2,2,2,2),
							2,2
							));
								
		mainPane.add(createTypePane(), new GridBagConstraints(
							0,1,1,1,1,1,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL,
							new Insets(2,2,2,2),
							2,2
							));
							
		mainPane.add(createButtonPane(), new GridBagConstraints(
							0,2,1,1,1,1,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL,
							new Insets(2,2,2,2),
							2,2
							));
		return mainPane;
	}

	protected JPanel createPropertyNamePane() {
		JPanel propertyNamePane = new JPanel(new GridBagLayout());
		JLabel propertyNameLabel = new JLabel ("Name of Property: ");
		nameTextField = new JTextField(property.getName());
		
		propertyNamePane.add(propertyNameLabel,new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0,0,0,0),
								2,2
								));
		propertyNamePane.add(nameTextField,new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,0,0,0),
								2,2
								));
		return propertyNamePane;
	}

	protected JPanel createTypePane() {
		JLabel typeNameLabel = new JLabel ("Type: ");
		JPanel typePane = new JPanel(new GridBagLayout());
//		editTypeButton = new JButton ("Edit Type");
//		editTypeButton.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//                // @todo do something here
//			}
//		});
		
		typePane.add(typeNameLabel,new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
		typePane.add(createTypeComboBox(),new GridBagConstraints(
								0,1,2,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));
//		JPanel editTypeButtonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		editTypeButtonPane.add(editTypeButton);
//		typePane.add(editTypeButtonPane, new GridBagConstraints(
//								1,2,1,1,1,1,
//								GridBagConstraints.NORTHWEST,
//								GridBagConstraints.HORIZONTAL,
//								new Insets(2,2,2,2),
//								2,2
//								));
		return typePane;
	}
	
	protected JPanel createButtonPane(){
		JPanel buttonPane = new JPanel(new FlowLayout());
		changeButton = new JButton("Change");
		changeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Datatype type = (Datatype) typeBox.getSelectedItem();
				property.setType(type);
				//if(!(property.getName().equals(propertyName.getText()))){
					if(!(nameTextField.getText().equals(""))){
						property.setName(nameTextField.getText());
					}
				//}
				parent.validate();
				//dispose();
				setVisible(false);
			}
		});
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		buttonPane.add(changeButton);
		buttonPane.add(cancelButton);
		
		return buttonPane;
	}
	
	protected JComboBox createTypeComboBox(){
		typeBox = new JComboBox();
		Iterator typeIt = context.getTypes().iterator();
		while(typeIt.hasNext()){
            Datatype type = (Datatype) typeIt.next();
			typeBox.addItem(type);
			if(type.getName().equals(property.getType().getName())){
				typeBox.setSelectedItem(type);
			}	
		}
		typeBox.setRenderer(new DefaultListCellRenderer(){
			/*
			 * This is a copy of the implementation of DefaultListCellRenderer from Sun JDK 1.5,
			 * the only change being the text displayed. A bit of a hack, but it seems better than
			 * wrapping all content in an extra layer just for the toString() method and we don't
			 * want to rely on every datatype implementing toString as display method.
			 */
		    @Override
			public Component getListCellRendererComponent(
		            JList list,
		    	Object value,
		            int index,
		            boolean isSelected,
		            boolean cellHasFocus)
		        {
		            setComponentOrientation(list.getComponentOrientation());
		    	if (isSelected) {
		    	    setBackground(list.getSelectionBackground());
		    	    setForeground(list.getSelectionForeground());
		    	}
		    	else {
		    	    setBackground(list.getBackground());
		    	    setForeground(list.getForeground());
		    	}

		    	setText((value == null) ? "" : ((Datatype)value).getName());

		    	setEnabled(list.isEnabled());
		    	setFont(list.getFont());

		            Border border = null;
		            if (cellHasFocus) {
		                if (isSelected) {
		                    border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
		                }
		                if (border == null) {
		                    border = UIManager.getBorder("List.focusCellHighlightBorder");
		                }
		            } else {
		                border = noFocusBorder;
		            }
		    	setBorder(border);

		    	return this;
		        }
		});
		return typeBox;
	}
}
