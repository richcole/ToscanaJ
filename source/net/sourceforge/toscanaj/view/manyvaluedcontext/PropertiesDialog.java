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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.toscanaj.gui.SienaMainPanel;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WriteableManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;

public class PropertiesDialog extends JDialog{
	
	private JTextField propertyName;
	private JButton cancelButton;
	private JButton editTypeButton;
	private JButton changeButton;
	private WriteableManyValuedAttribute property;
	private TableViewPanel frame;
	private JComboBox typeBox;
	private PropertiesDialog dialog = this;
	private ManyValuedContext context;
	
	public PropertiesDialog(TableViewPanel frame,
										WriteableManyValuedAttribute property, 
										ManyValuedContext context,
										SienaMainPanel sienaPane){
		super(sienaPane,"Many Valued-Context:Properties",true);
		this.frame = frame;
		this.property = property;
		this.context = context;
		setContentPane(createView());
		setSize(300,200);
		show();
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
		propertyName = new JTextField(property.getName());
		
		propertyNamePane.add(propertyNameLabel,new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0,0,0,0),
								2,2
								));
		propertyNamePane.add(propertyName,new GridBagConstraints(
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
				AttributeType type = (AttributeType) typeBox.getSelectedItem();
				if(type instanceof TextualType){
					TextualTypeDialog typeDialog = new TextualTypeDialog(dialog,type);
				}
				else{
					NumericalTypeDialog typeDialog = new NumericalTypeDialog(dialog,type);
				}
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
		cancelButton = new JButton("Cancel");
		changeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				AttributeType type = (AttributeType) typeBox.getSelectedItem();
				property.setType(type);
				if(!(property.getName().equals(propertyName.getText()))){
					if(!(propertyName.getText().equals(""))){
						property.setName(propertyName.getText());
					}
				}
				frame.update();
				dispose();
			}
		});
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
			AttributeType type = (AttributeType) typeIt.next();
			typeBox.addItem(type);
			if(type.getName().equals(property.getType().getName())){
				typeBox.setSelectedItem(type);
			}	
		}
		return typeBox;
	}
}
