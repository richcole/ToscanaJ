/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ContextImplementation;

import javax.swing.*;
import java.awt.*;

public class ContextTableScaleEditorDialog extends JDialog {
    private boolean result;

    private DatabaseConnection databaseConnection;

    private JTextField scaleTitleField;
    
    private JPanel buttonsPanel;

    public ContextTableScaleEditorDialog(Frame owner, DatabaseConnection databaseConnection) {
        super(owner);
        this.databaseConnection = databaseConnection;
        createView();
    }

    private void createView() {
        setModal(true);
        setTitle("Context Table Scale Generator");
        getContentPane().setLayout(new GridBagLayout());

        // -- title pane ---
        JPanel titlePane = new JPanel(new GridBagLayout());
        JLabel titleLabel = new JLabel("Title:");
        this.scaleTitleField = new JTextField();
        
        titlePane.add(
                titleLabel,
                new GridBagConstraints(
                        0, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        titlePane.add(
                scaleTitleField,
                new GridBagConstraints(
                        1, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

		ContextImplementation context = new ContextImplementation();
		String o1 = "one";
		String o2 = "two";
		String o3 = "three";
		String o4 = "four";
		String a1 = "Aone";
		String a2 = "Atwo";
		String a3 = "Athree alsfdjsa dlfj sadlkdjg salgdkj jkhsf";

		context.getObjects().add(o1);
		context.getObjects().add(o2);
		context.getObjects().add(o3);
		context.getObjects().add(o4);

		context.getAttributes().add(a1);
		context.getAttributes().add(a2);
		context.getAttributes().add(a3);
		
		context.getRelationImplementation().insert(o1,a1);
		context.getRelationImplementation().insert(o1,a2);
		context.getRelationImplementation().insert(o2,a2);
		context.getRelationImplementation().insert(o3,a3);
		context.getRelationImplementation().insert(o4,a3);

		ContextTableView tableView = new ContextTableView(context);
        JScrollPane scrollpane = new JScrollPane(tableView);
		
		createButtonsPanel();
		
        getContentPane().add(
                titlePane,
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        getContentPane().add(
                scrollpane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
		getContentPane().add(
				buttonsPanel,
				new GridBagConstraints(
						0, 2, 1, 1, 1, 1,
						GridBagConstraints.CENTER,
						GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5),
						0, 0
				)
		);
        pack();
    }
	
	private void createButtonsPanel(){
		buttonsPanel = new JPanel(new GridBagLayout());
		JButton addObj = new JButton("  Add Object ");	
		JButton addAttr = new JButton("  Add Attribute ");
		JButton cancel= new JButton("  Cancel ");
		JButton create = new JButton("  Create ");
				
		buttonsPanel.add(
			   addObj,
			   new GridBagConstraints(
					   0, 0, 1, 1, 1, 0,
					   GridBagConstraints.WEST,
					   GridBagConstraints.HORIZONTAL,
					   new Insets(0, 5, 5, 5),
					   0, 0
			   )
	   );
	   buttonsPanel.add(
			   addAttr,
			   new GridBagConstraints(
					   1, 0, 1, 1, 1, 0,
					   GridBagConstraints.WEST,
					   GridBagConstraints.BOTH,
					   new Insets(0, 5, 5, 5),
					   0, 0
			   )
	   );
	   buttonsPanel.add(
			  cancel,
			  new GridBagConstraints(
					  2, 0, 1, 1, 1, 0,
					  GridBagConstraints.EAST,
					  GridBagConstraints.HORIZONTAL,
					  new Insets(0, 5, 5, 5),
					  0, 0
			  )
	  );
	  buttonsPanel.add(
			  create,
			  new GridBagConstraints(
					  3, 0, 1, 1, 1, 0,
					  GridBagConstraints.EAST,
					  GridBagConstraints.BOTH,
					  new Insets(0, 5, 5, 5),
					  0, 0
			  )
	  );	   
	}
	
    public boolean execute() {
        result = false;
        show();
        return result;
    }

    public String getDiagramTitle() {
        return this.scaleTitleField.getText();
    }
}
