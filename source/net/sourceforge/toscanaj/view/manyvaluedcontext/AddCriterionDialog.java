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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.Criterion;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalValueGroup;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualValueGroup;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.View;

public class AddCriterionDialog extends JDialog{
		private ManyValuedAttribute attr;
		private View view;
		private JButton createButton;
		private List checkBoxList = new ArrayList();
		private JTextField minTextField,maxTextField,name;
		
		public AddCriterionDialog(ManyValuedAttribute attr, Frame parent, View view){
			super(parent,"Conditions",true);
			this.attr = attr;
			this.view = view;
			if(attr.getType() instanceof TextualType){
				TextualType textualType = (TextualType)attr.getType();
				setContentPane(createTextualTypeView(textualType));
			}
			else{
				NumericalType numericalType = (NumericalType)attr.getType();
				setContentPane(createNumericalTypeView(numericalType));
			}
			setSize(300,250);
			show();
		}
		
		protected JPanel createNumericalTypeView(final NumericalType numericalType) {
			JPanel mainPane =new JPanel(new GridBagLayout());
			JPanel namePane = new JPanel(new GridBagLayout());
			name = new JTextField();
			name.addKeyListener(getNumericalTypeViewKeyListener(numericalType));
			JLabel nameLabel = new JLabel("Name:");

			namePane.add(nameLabel, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));

			namePane.add(name, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));	
			
								
			Border border = BorderFactory.createTitledBorder("Range");
			JPanel criteriaPane = new JPanel(new GridBagLayout());
			JLabel rangeCondition = new JLabel("* Attribute may range from "+
							numericalType.getMinimumValue()+" to "+numericalType.getMaximumValue());
			JLabel minLabel = new JLabel("From:");
			minTextField = new JTextField();
			minTextField.addKeyListener(getNumericalTypeViewKeyListener(numericalType));
			JLabel maxLabel = new JLabel("To:");
			maxTextField = new JTextField();
			maxTextField.addKeyListener(getNumericalTypeViewKeyListener(numericalType));
			final JCheckBox minIncluded = new JCheckBox(">=");
			final JCheckBox maxIncluded = new JCheckBox("<=");
			
			criteriaPane.add(minLabel, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,5,2,2),
								2,2
								));
			criteriaPane.add(minTextField, new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,5,2,2),
								2,2
								));
			criteriaPane.add(minIncluded, new GridBagConstraints(
								1,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
			criteriaPane.add(maxLabel, new GridBagConstraints(
								0,3,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,5,2,2),
								2,2
								));
			criteriaPane.add(maxTextField, new GridBagConstraints(
								0,4,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,5,2,2),
								2,2
								));
			criteriaPane.add(maxIncluded, new GridBagConstraints(
								1,4,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));		
			criteriaPane.setBorder(border);
			
			JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			createButton = new JButton("Create");
			createButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					double fromValue = Double.parseDouble(minTextField.getText());
					double toValue = Double.parseDouble(maxTextField.getText());
					NumericalValueGroup valueGroup = new NumericalValueGroup(numericalType,
													name.getText(),"",fromValue,minIncluded.isSelected(),
													toValue,maxIncluded.isSelected());
					view.addCriterion(new Criterion(attr,valueGroup));
					dispose();
				}
			});
			createButton.setEnabled(false);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
			buttonPane.add(createButton);
			buttonPane.add(cancelButton);
									
			mainPane.add(namePane, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));
			mainPane.add(criteriaPane, new GridBagConstraints(
								0,1,1,1,1,8,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.BOTH,
								new Insets(2,2,2,2),
								2,2
								));
			mainPane.add(rangeCondition, new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,5,2,2),
								2,2
								));
			mainPane.add(buttonPane, new GridBagConstraints(
								0,3,1,1,1,1,
								GridBagConstraints.EAST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
			return mainPane;
		}
	
		protected JPanel createTextualTypeView(final TextualType textualType){
			JPanel mainPane = new JPanel(new GridBagLayout());
			
			JPanel namePane = new JPanel(new GridBagLayout());
			final JTextField nameField = new JTextField();
			
			
			JLabel nameLabel = new JLabel("Name:");
			
			namePane.add(nameLabel, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
			namePane.add(nameField, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));			
								
			mainPane.add(namePane, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));
								
			JPanel checkBoxPane = new JPanel(new GridBagLayout());
			AttributeValue[] attributeValueArray = attr.getType().getValueRange();
			final Hashtable buttonMap = new Hashtable();
			for(int i = 0 ; i< attributeValueArray.length ; i++){
				AttributeValue attributeValue = attributeValueArray[i];
				final JCheckBox box = new JCheckBox(attributeValue.getDisplayString());
				checkBoxList.add(box);
				buttonMap.put(box,attributeValue);
				checkBoxPane.add(box, new GridBagConstraints(
							0,i,1,1,1,1,
							GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE,
							new Insets(2,2,2,2),
							2,2
							));
				box.addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent e) {
						Iterator it = checkBoxList.iterator();
						boolean boxsSelected = false;
						while(it.hasNext()){
							JCheckBox box = (JCheckBox) it.next();
							if(box.isSelected()){
								boxsSelected=true;
								break;
							}
						}
						if( !boxsSelected || (nameField.getText().trim()).equals("")){
							createButton.setEnabled(false);
						}
						else if(boxsSelected && !(nameField.getText().trim()).equals("")){
							createButton.setEnabled(true);
						}
					}
				});
			}
			
			JScrollPane scrollPane = new JScrollPane(checkBoxPane);
			mainPane.add(scrollPane, new GridBagConstraints(
								0,1,1,1,1,8,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.BOTH,
								new Insets(2,2,2,2),
								2,2
								));
							
			JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			createButton = new JButton("Create");
			JButton cancelButton = new JButton("Cancel");
			createButton.setEnabled(false);
			createButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Iterator it = checkBoxList.iterator();
					TextualValueGroup selectedValues = new TextualValueGroup
										(textualType,nameField.getText(),attr.getName());
					while(it.hasNext()){
						JCheckBox box = (JCheckBox)it.next();
						if(box.isSelected()) {
							AttributeValue a = (AttributeValue)buttonMap.get(box);
							selectedValues.addValue(a);
						}
					}
					Criterion c = new Criterion(attr,selectedValues);
					view.addCriterion(c);
					dispose();
				}
			});
			
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			
			buttonPane.add(createButton);
			buttonPane.add(cancelButton);
			mainPane.add(buttonPane, new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.EAST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
			nameField.addKeyListener(new KeyListener(){
				public void keyTyped(KeyEvent e) {
					keyListenerConditions();
				}
				public void keyPressed(KeyEvent e) {
					keyListenerConditions();
				}
				public void keyReleased(KeyEvent e) {
					keyListenerConditions();
				}
				public void keyListenerConditions(){
					if((nameField.getText().trim()).equals("") || !checkBoxSelected()){
						createButton.setEnabled(false);
					}
					else if(!(nameField.getText().trim()).equals("") && checkBoxSelected()){
						createButton.setEnabled(true);
					}
				}
				
			});
			return mainPane;
		}
		
		protected boolean checkBoxSelected(){
			Iterator it = checkBoxList.iterator();
			while(it.hasNext()){
				JCheckBox box = (JCheckBox) it.next();
				if(box.isSelected()){
					return true;
				}
			}
			return false;
		}
		
		protected KeyListener getNumericalTypeViewKeyListener(final NumericalType numericalType){
			return new KeyListener(){
				public void keyTyped(KeyEvent e) {
					keyListenerConditions(numericalType);
				}
				public void keyPressed(KeyEvent e) {
					keyListenerConditions(numericalType);
				}
				public void keyReleased(KeyEvent e) {
					keyListenerConditions(numericalType);
				}
			};
		}
		
		protected void keyListenerConditions(NumericalType numericalType){
			if(blankTextFields() || !maxMinValuesWithinRange(numericalType)){
				createButton.setEnabled(false);
			}	
			else if(!blankTextFields() && maxMinValuesWithinRange(numericalType)){
				createButton.setEnabled(true);
			}
			
		}
		
		protected boolean maxMinValuesWithinRange(NumericalType numericalType) {
			if(!blankTextFields()){
				double fromValue = 0.0;
				double toValue = 0.0;
				
				try{
					fromValue = Double.parseDouble(minTextField.getText());
					toValue = Double.parseDouble(maxTextField.getText());
				}
				catch(NumberFormatException e){
					return false;
				}
				if(fromValue>=numericalType.getMinimumValue() && toValue<=numericalType.getMaximumValue()){
					if(fromValue<toValue){
					return true;
					}
				}
			}
			return false;
		}

		protected boolean blankTextFields() {
			if(minTextField.getText().equals("") || 
							maxTextField.getText().equals("") || 
										name.getText().equals("")){
				return true;
			}
			return false;
		}
}