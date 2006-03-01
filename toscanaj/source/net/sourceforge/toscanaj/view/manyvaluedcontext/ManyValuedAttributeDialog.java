/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tockit.datatype.Datatype;

import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedAttribute;

public class ManyValuedAttributeDialog extends JDialog{
	
	private JTextField nameTextField;
	private JButton cancelButton;
	private JButton editTypeButton;
	private JButton changeButton;
	private WritableManyValuedAttribute property;
	private Frame parent;
	private JComboBox typeBox;
	private ManyValuedAttributeDialog dialog = this;
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
		editTypeButton = new JButton ("Edit Type");
		editTypeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                // @todo do something here
			}
		});
		
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
		JPanel editTypeButtonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		editTypeButtonPane.add(editTypeButton);
		typePane.add(editTypeButtonPane, new GridBagConstraints(
								1,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));
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
		return typeBox;
	}
}
