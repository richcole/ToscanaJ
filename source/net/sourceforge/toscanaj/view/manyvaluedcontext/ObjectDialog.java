/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
 
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;

public class ObjectDialog extends JDialog{
	
	private JTextField objectName;
	private JButton closeButton;
	private JButton changeButton;
	private JFrame parent;
	private int index;
	private JDialog dialog = this;
	private WritableManyValuedContext context;
	
	
	public ObjectDialog(JFrame parent, WritableManyValuedContext context){
		super(parent,"Object", false);
		setResizable(false);
		createView();
		pack();
		this.parent = parent;
		this.context = context;
	}

	private void createView() {
		
		JPanel mainPane = new JPanel(new GridBagLayout());
		JPanel buttonPane = new JPanel();
		
		JLabel objectNameLabel = new JLabel("Name Of Object:");
		objectName = new JTextField();
		
		mainPane.add(objectNameLabel, new GridBagConstraints(
						0,0,1,1,0,0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(1,1,1,1),
						2,2
		));
		
		mainPane.add(objectName, new GridBagConstraints(
						0,1,1,1,1,0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(1,10,1,10),
						2,2
		));
		
		
		changeButton = new JButton("Change");
		changeButton.addActionListener(new ActionListener (){
			public void actionPerformed(ActionEvent e){
				if(!objectName.getText().equals("")){
					context.updateObject(objectName.getText(),index);
					parent.validate();
					hide();
				}
				else{
					JOptionPane.showMessageDialog(dialog,
						"Please enter a object name.",
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				hide();			
			}
		});

		buttonPane.add(changeButton);
		buttonPane.add(closeButton);
		
		mainPane.add(buttonPane,new GridBagConstraints(
						0,2,1,1,1,0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(1,1,1,1),
						2,2
		));
		setContentPane(mainPane);
	}
	
	public void setObjectName(String content){
		objectName.setText(content);		
	}
	
	public void setSelectedObjectIndex(int index){
		this.index = index;
	}

}
