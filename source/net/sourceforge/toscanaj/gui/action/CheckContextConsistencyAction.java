/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.jdom.Element;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.view.context.ContextConsistencyChecker;

/// @todo rename class once we figured out how to call this
public class CheckContextConsistencyAction extends AbstractAction implements EventBrokerListener {
	private ConceptualSchema conceptualSchema;
	private DatabaseConnection databaseConnection;
	private Frame parent;

	public CheckContextConsistencyAction(ConceptualSchema conceptualSchema, 
										DatabaseConnection databaseConnection, 
										Frame parent,
										EventBroker eventBroker) {
		super("Check Consistency With Database");

		this.conceptualSchema = conceptualSchema;
		this.databaseConnection = databaseConnection;
		this.parent = parent;
		setActionState();
		eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
		eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);	
	}

	public void actionPerformed(ActionEvent event) {
		try {
			Hashtable allProblems = new Hashtable();
			Iterator it = conceptualSchema.getDiagramsIterator();
			while (it.hasNext()) {
				Diagram2D curDiagram = (Diagram2D) it.next();
				List curPoblems = ContextConsistencyChecker.checkConsistency(this.conceptualSchema, curDiagram, this.databaseConnection, this.parent);
				if (!curPoblems.isEmpty()) {
					allProblems.put(curDiagram.getTitle(), curPoblems);
				}
			}
			
			// give feedback
			if (allProblems.isEmpty()) {
				JOptionPane.showMessageDialog(
					this.parent,
					"No problems found",
					"Objects correct",
					JOptionPane.INFORMATION_MESSAGE);
			} else {
				// show problems
				Element problemDescription = new Element("description");
				Element htmlElement = new Element("html");
				htmlElement.addContent(
					new Element("title").addContent("Consistency problems"));
				problemDescription.addContent(htmlElement);
				Element body = new Element("body");
				htmlElement.addContent(body);
				body.addContent(new Element("h1").addContent("Problems found:"));
				
				Enumeration e = allProblems.keys();
				while (e.hasMoreElements()) {
					String diagramTitle = (String) e.nextElement();
					List problems = (List) allProblems.get(diagramTitle);
					body.addContent(new Element("h3").addContent("Diagram '" + diagramTitle + "'"));
					Iterator problemsIterator = problems.iterator();
					while (problemsIterator.hasNext()) {
						String problem = (String) problemsIterator.next(); 
						body.addContent(new Element("pre").addContent(problem));
					}
				}
				
				Frame frame = JOptionPane.getFrameForComponent(this.parent);
				DescriptionViewer.show(frame, problemDescription);
			}												
		}
		catch (Exception e ) {
			ErrorDialog.showError(parent, e, "Error checking consistency", "Couldn't check database consistency");
		}
		
	}

	public void processEvent(Event event) {
		if(event instanceof DatabaseConnectedEvent) {
			DatabaseConnectedEvent dbConEv = (DatabaseConnectedEvent) event;
			this.databaseConnection = dbConEv.getConnection();
		}
		if(event instanceof ConceptualSchemaChangeEvent) {
			ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
			if (event instanceof NewConceptualSchemaEvent) {
				this.conceptualSchema = changeEvent.getConceptualSchema();
			}
		}
		setActionState();
		
	}
	
	private void setActionState () {
		if(this.databaseConnection == null) {
			setEnabled(false);		
			return;
		} 
		else if (!this.databaseConnection.isConnected()) {
			setEnabled(false);
			return;
		}
		if (this.conceptualSchema == null) {
			setEnabled(false);
			return;
		}
		else {
			if (conceptualSchema.getNumberOfDiagrams() == 0) {
				setEnabled(false);
				return;
			}
		}
		setEnabled(true);		
	}	

}
