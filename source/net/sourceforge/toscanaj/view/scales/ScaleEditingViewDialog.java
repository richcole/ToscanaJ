/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;


public class ScaleEditingViewDialog extends JDialog {
	
	public ScaleEditingViewDialog(JFrame frame, ConceptualSchema conceptualSchema,
					EventBroker eventBroker, DatabaseConnection databaseConnection){
		super(frame, "Scale Editing View");
		ScaleEditingView scaleEditingView = new ScaleEditingView (conceptualSchema, eventBroker, 
																	databaseConnection);
		setBounds(100,50,800,600);											
		setContentPane(scaleEditingView);
		setVisible(true);
		
	}

}
