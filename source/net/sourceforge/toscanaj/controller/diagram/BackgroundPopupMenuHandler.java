/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
 
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.gui.ToscanaJMainPanel;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import org.jdom.Element;
import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;


public class BackgroundPopupMenuHandler implements EventBrokerListener {
	private DiagramView diagramView;
	private ToscanaJMainPanel mainPanel;
	private JPopupMenu menu;
	private JMenuItem goBackOneDiagramItem;
	private JMenu showDiagramDescriptionMenu;
	private JMenuItem showAnalysisHistory;
	private JMenuItem showInnerDiagramDescription;
	private JMenuItem showOuterDiagramDescription;
	private JMenu changeLabelMenu;
	private DiagramController diagContr = DiagramController.getController();
	private JMenuItem showCurrentDiagramDescription;
	private JMenuItem flatDiagramItem;
	private JMenuItem nestedDiagramItem;
	
	
	public BackgroundPopupMenuHandler(DiagramView diagramView, ToscanaJMainPanel mainPanel){
		this.diagramView = diagramView;
		this.mainPanel = mainPanel;
	}
	
	public void processEvent(Event e) {
		CanvasItemEventWithPosition itemEvent = null;
		try {
			itemEvent = (CanvasItemEventWithPosition) e;
		} catch (ClassCastException e1) {
			throw new RuntimeException(getClass().getName() +
					" has to be subscribed to CanvasItemEventWithPositions only");
		}
		CanvasBackground background = null;
		try{
			 background = (CanvasBackground)itemEvent.getItem();
			
		}catch(ClassCastException e1){
			throw new RuntimeException(getClass().getName() +
			" has to be subscribed to events from CanvasBackground only");
		}
		
		openPopupMenu(background, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
	}

	protected void openPopupMenu(final CanvasBackground background, Point2D canvasPosition,
	 									Point2D screenPosition) {
		menu =  new JPopupMenu();
		goBackOneDiagramItem = new JMenuItem("Go back one diagram");
		menu.add(goBackOneDiagramItem);
		goBackOneDiagramItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {	
				DiagramController.getController().back();
				updateStatus();
			}
		});
		menu.add(new JSeparator());
		
		showDiagramDescriptionMenu = new JMenu("Show Diagram Description");
		showCurrentDiagramDescription = new JMenuItem("Current Diagram");
		
		makeShowDiagramDescriptionMenu();
		menu.add(showDiagramDescriptionMenu);
	
		showAnalysisHistory = new JMenuItem("Show Analysis History");
		menu.add(showAnalysisHistory);
		showAnalysisHistory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				mainPanel.showDiagramContextDescription();
			}
		});
		
		menu.add(new JSeparator());
		
		if(diagContr.getDiagramHistory().getNestingLevel()==0){
			nestedDiagramItem = new JMenuItem("Nested Diagram");
			nestedDiagramItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					diagContr.setNestingLevel(1);
				}
			});
			menu.add(nestedDiagramItem);
		}
		else{
			flatDiagramItem = new JMenuItem("Flat Diagram");
			flatDiagramItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					diagContr.setNestingLevel(0);
				}
			});
			
			menu.add(flatDiagramItem);
		}
		makeChangeObjectLabel();

		updateStatus();
		menu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
	}
	
	protected void updateStatus(){
		
		goBackOneDiagramItem.setEnabled(DiagramController.getController().undoIsPossible());
		
		if(DiagramController.getController().getNumberOfObjects()==-1){
			goBackOneDiagramItem.setEnabled(false);
			showDiagramDescriptionMenu.setEnabled(false);
			showAnalysisHistory.setEnabled(false);
			changeLabelMenu.setEnabled(false);
			nestedDiagramItem.setEnabled(false);
		}
	}
	
	protected void makeChangeObjectLabel(){
		ButtonGroup labelContentGroup = new ButtonGroup();
		changeLabelMenu=new JMenu("Change All Object Labels");
		ConceptualSchema conceptualSchema = this.mainPanel.getConceptualSchema();
		if (conceptualSchema != null) {
			Iterator it = conceptualSchema.getQueries().iterator();
			if (it.hasNext()) {
				boolean first = true;
				int count = 0;
				while (it.hasNext()) {
					final Query query = (Query) it.next();
					count++;
					String name = query.getName();
					JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							diagramView.setQuery(query);
						}
					});
					labelContentGroup.add(menuItem);
					changeLabelMenu.add(menuItem);
					if (first == true) {
						first = false;
						menuItem.setSelected(true);
					}
				}
			}
		}	
		menu.add(changeLabelMenu);
	}
	
	protected void makeShowDiagramDescriptionMenu(){
		Diagram2D curDiag = diagContr.getCurrentDiagram();
		if (curDiag != null) {
			boolean outerDiagramEnabled = false;
			boolean showAboutDiagramComponents;
			boolean innerDiagramEnabled = false;
			if( diagContr.getDiagramHistory().getNumberOfCurrentDiagrams() == 1) {
				Element diagDesc = curDiag.getDescription();
				showAboutDiagramComponents = diagDesc != null;
			} else {
				Diagram2D outerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(0);
				Element outerDiagDesc = outerDiagram.getDescription();
				Diagram2D innerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(1);
				Element innerDiagDesc = innerDiagram.getDescription();
				showAboutDiagramComponents = (outerDiagDesc != null) || (innerDiagDesc != null);
				outerDiagramEnabled = outerDiagDesc != null; 
				innerDiagramEnabled = innerDiagDesc != null;
			}
			showDiagramDescriptionMenu.setEnabled(showAboutDiagramComponents);
			if(diagContr.getDiagramHistory().getNestingLevel() == 0) {
				showCurrentDiagramDescription = new JMenuItem("Current Diagram");
				showCurrentDiagramDescription.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						DescriptionViewer.show(mainPanel,DiagramController.getController().getCurrentDiagram().getDescription());
					}
				});
				showDiagramDescriptionMenu.add(showCurrentDiagramDescription);
			}		
			else if(innerDiagramEnabled && outerDiagramEnabled){
				showOuterDiagramDescription = new JMenuItem("Outer Diagram");
				showInnerDiagramDescription = new JMenuItem("Inner Diagram");
				
				showOuterDiagramDescription.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						Diagram2D outerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(0);
						Element outerDiagDesc = outerDiagram.getDescription();
						DescriptionViewer.show(mainPanel, outerDiagDesc);
					}
				});
				showInnerDiagramDescription.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						Diagram2D innerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(1);
						Element innerDiagDesc = innerDiagram.getDescription();
						DescriptionViewer.show(mainPanel, innerDiagDesc);
					}
				});
				showDiagramDescriptionMenu.add(showOuterDiagramDescription);
				showDiagramDescriptionMenu.add(showInnerDiagramDescription);
				
			}
			else if(outerDiagramEnabled && !innerDiagramEnabled){
				showOuterDiagramDescription = new JMenuItem("Outer Diagram");
				showOuterDiagramDescription.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						Diagram2D outerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(0);
						Element outerDiagDesc = outerDiagram.getDescription();
						DescriptionViewer.show(mainPanel, outerDiagDesc);
					}
				});
				showDiagramDescriptionMenu.add(showOuterDiagramDescription);
			}
			else if (innerDiagramEnabled && !outerDiagramEnabled){
				showInnerDiagramDescription = new JMenuItem("Inner Diagram");
				showInnerDiagramDescription.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						Diagram2D innerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(1);
						Element innerDiagDesc = innerDiagram.getDescription();
						DescriptionViewer.show(mainPanel, innerDiagDesc);
					}
				});
				showDiagramDescriptionMenu.add(showInnerDiagramDescription);
			}
		} 

		else {
			showDiagramDescriptionMenu.setEnabled(false);
		}	
	}
}
