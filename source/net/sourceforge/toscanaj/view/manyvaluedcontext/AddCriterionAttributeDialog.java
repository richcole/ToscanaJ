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
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.View;

public class AddCriterionAttributeDialog extends JDialog{
	private JFrame parent;
	private List manyValuedAttributeList;
	private View view;
		
	public AddCriterionAttributeDialog(Collection manyValuedAttributeList,JFrame parent,View view){
		super(parent,"Many Valued Context:Add Criteria",false);
		this.manyValuedAttributeList = (List)manyValuedAttributeList;
		this.parent = parent;
		this.view = view;
		setSize(300,150);
		setContentPane(createView());
	}

	public JPanel createView(){
		JPanel mainPane = new JPanel(new GridBagLayout());
		JLabel comboBoxLabel = new JLabel("Attributes:");
		final JComboBox comboBox = new JComboBox(manyValuedAttributeList.toArray());
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton continueButton = new JButton("Continue...");
		JButton closeButton = new JButton("Close");
		continueButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ManyValuedAttribute attr = (ManyValuedAttribute) comboBox.getSelectedItem();
				new AddCriterionDialog(attr,parent,view);
			}
		});
		
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		
		buttonPane.add(continueButton);
		buttonPane.add(closeButton);
		
		mainPane.add(comboBoxLabel, new GridBagConstraints(
						0,0,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(2,2,2,2),
						2,2
						));
						
		mainPane.add(comboBox, new GridBagConstraints(
						0,1,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(2,2,2,2),
						2,2
						));
		mainPane.add(new JPanel(), new GridBagConstraints(
						0,2,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.BOTH,
						new Insets(2,2,2,2),
						2,2
						));
						
		mainPane.add(buttonPane, new GridBagConstraints(
						0,3,1,1,1,1,
						GridBagConstraints.NORTH,
						GridBagConstraints.NONE,
						new Insets(2,2,2,2),
						2,2
						));
						
		return mainPane;
	}
}
