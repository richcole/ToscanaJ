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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalType;

public class NumericalTypeDialog extends JDialog {
	
	private NumericalType type;
	private JTextField numericalNameField;
	private JTextField maxValueField, minValueField, numDecimalField;
	private NumericalTypeDialog dialog = this;
	
	public NumericalTypeDialog(PropertiesDialog dialog,AttributeType type){
		super(dialog, "Many Valued-context:Type",true);
		this.type = (NumericalType) type;
		setContentPane(createView());
		setBounds(100,100,400,250);
		show();
	}
	
	protected JPanel createView(){
		JPanel mainPane = new JPanel(new GridBagLayout());
		
		mainPane.add(createNumericalNamePane(), new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,2,0,2),
								2,2
								));
		
		mainPane.add(createTabPane(), new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.BOTH,
								new Insets(0,2,0,2),
								2,2
								));
		mainPane.add(createButtonPane(), new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(5,2,0,2),
								2,2
								));
		return mainPane;
	}

	protected JTabbedPane createTabPane() {
		JPanel mainPane = new JPanel(new GridBagLayout());
		JTabbedPane tabPane = new JTabbedPane();
		
		JLabel maxValueName = new JLabel("Maximum Values: ");
		JLabel minValueName = new JLabel("Minimum Values: ");
		JLabel numDecimalName = new JLabel("Number of Decimals: ");
		
		maxValueField = new JTextField(""+type.getMaximumValue());
		minValueField = new JTextField(""+type.getMinimumValue());
		numDecimalField = new JTextField(""+type.getNumOfDecimals());
		
		minValueField.setHorizontalAlignment(JTextField.RIGHT);
		maxValueField.setHorizontalAlignment(JTextField.RIGHT);
		numDecimalField.setHorizontalAlignment(JTextField.RIGHT);

		mainPane.add(maxValueName, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(10,10,0,10),
								2,2
								));
		
		mainPane.add(maxValueField, new GridBagConstraints(
								1,0,1,1,4,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(10,10,0,10),
								2,2
								));
								
		mainPane.add(minValueName, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0,10,0,10),
								2,2
								));
		
		mainPane.add(minValueField, new GridBagConstraints(
								1,1,1,1,4,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,10,0,10),
								2,2
								));
		mainPane.add(numDecimalName, new GridBagConstraints(
								0,2,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(0,10,10,10),
								2,2
								));
		
		mainPane.add(numDecimalField, new GridBagConstraints(
								1,2,1,1,4,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(0,10,10,10),
								2,2
								));
								
		tabPane.add("Values", mainPane);
		return tabPane;
	}
	
	protected JPanel createNumericalNamePane(){
		JPanel mainPane = new JPanel(new GridBagLayout());
		JLabel label = new JLabel("Numerical");
		numericalNameField = new JTextField (type.getName());
		
		mainPane.add(label, new GridBagConstraints(
								0,0,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE,
								new Insets(2,2,2,2),
								2,2
								));
		mainPane.add(numericalNameField, new GridBagConstraints(
								0,1,1,1,1,1,
								GridBagConstraints.NORTHWEST,
								GridBagConstraints.HORIZONTAL,
								new Insets(2,2,2,2),
								2,2
								));

		return mainPane;
	}
	
	protected JPanel createButtonPane() {
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton applyButton = new JButton("Apply");
		JButton closeButton = new JButton("Close");
	
		applyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!(minValueField.getText().equals("")) && !(maxValueField.getText().equals(""))
					&& !(numDecimalField.getText().equals(""))){
						double maxValue = 0.0;
						double minValue = 0.0;
						int decimal = 0;
						try{
							maxValue = Double.parseDouble(maxValueField.getText());
							minValue = Double.parseDouble(minValueField.getText());
							decimal = Integer.parseInt(numDecimalField.getText());
						}
						catch (NumberFormatException ef){
							JOptionPane.showMessageDialog(dialog,
							"Enter numbers only.",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
							return;
						}
						type.setMaximumValue(maxValue);
						type.setMinimumValue(minValue);
						type.setNumberOfDecimals(decimal);
					}
				dispose();
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
	
}
