/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.context.model.Context;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimNodeMovementEventListener;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.parser.BurmeisterParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

public class Factorizer {
	static class Factorization {
		Set attributes;
		Diagram2D firstFactor;
		Diagram2D secondFactor;
		Diagram2D nestedDiagram;
		
		Factorization(Context fullContext, Set factorAttributes) {
			this.attributes = factorAttributes;
			LatticeGenerator lgen = new GantersAlgorithm();
					
			Context context1 = makeContextCopy(fullContext);
			context1.getAttributes().retainAll(this.attributes);
			Lattice lattice1 = lgen.createLattice(context1);
			this.firstFactor = NDimLayoutOperations.createDiagram(lattice1, fullContext.getName(), new DefaultDimensionStrategy());
					
			Context context2 = makeContextCopy(fullContext);
			context2.getAttributes().removeAll(this.attributes);
			Lattice lattice2 = lgen.createLattice(context2);
			this.secondFactor = NDimLayoutOperations.createDiagram(lattice2, fullContext.getName(), new DefaultDimensionStrategy());

			updateNestedDiagram();
		}

		void updateNestedDiagram() {
			this.nestedDiagram = new NestedLineDiagram(this.firstFactor, this.secondFactor);
		}
		
		public String toString() {
			int numOuterConcepts = this.firstFactor.getNumberOfNodes();
			int numInnerConcepts = this.secondFactor.getNumberOfNodes();
			return this.attributes.toString() + " (" + numOuterConcepts + "*" + 
														   numInnerConcepts + "=" +
														   numOuterConcepts * numInnerConcepts + ")";
		}
	}

	public static void main(String[] args) throws FileNotFoundException, DataFormatException {
		JFileChooser fileChooser = new JFileChooser();

		JPanel numberPanel = new JPanel(new FlowLayout());
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(5,2,20,1));
		numberPanel.add(new JLabel("Factor size:"));
		numberPanel.add(spinner);
		
		fileChooser.setAccessory(numberPanel);
		
		int returnValue = fileChooser.showOpenDialog(null);
		if(returnValue != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File inputFile = fileChooser.getSelectedFile();
		ContextImplementation context = BurmeisterParser.importBurmeisterFile(inputFile);
		Set attributes = context.getAttributes();

		List subsets = findAllSubsetsOfSize(attributes, ((Integer)spinner.getModel().getValue()).intValue());
		List diagrams = createFactorizedDiagrams(context,subsets);
		showResults(diagrams);
	}

	private static List createFactorizedDiagrams(Context context, List subsets) {
		List retVal = new ArrayList();
		for (Iterator it = subsets.iterator(); it.hasNext();) {
			Set set = (Set) it.next();
			retVal.add(new Factorization(context, set));
		}
		return retVal;
	}

	private static void showResults(List diagrams) {
		JFrame mainWindow = new JFrame("Factorizer");

		final JTabbedPane mainPane = new JTabbedPane();
		final JList listView = new JList(diagrams.toArray());
		JScrollPane scrollPane = new JScrollPane(listView);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, mainPane);
		mainWindow.getContentPane().add(splitPane);

		final DiagramView firstDiagramView = new DiagramView();
		firstDiagramView.setConceptInterpreter(new DirectConceptInterpreter());
		firstDiagramView.setConceptInterpretationContext(new ConceptInterpretationContext(new DiagramHistory(), new EventBroker()));
		firstDiagramView.setQuery(AggregateQuery.COUNT_QUERY);

		final DiagramView secondDiagramView = new DiagramView();
		secondDiagramView.setConceptInterpreter(new DirectConceptInterpreter());
		secondDiagramView.setConceptInterpretationContext(new ConceptInterpretationContext(new DiagramHistory(), new EventBroker()));
		secondDiagramView.setQuery(AggregateQuery.COUNT_QUERY);

		final DiagramView nestedDiagramView = new DiagramView();
		nestedDiagramView.setConceptInterpreter(new DirectConceptInterpreter());
		nestedDiagramView.setConceptInterpretationContext(new ConceptInterpretationContext(new DiagramHistory(), new EventBroker()));
		nestedDiagramView.setQuery(AggregateQuery.COUNT_QUERY);

		mainPane.add(firstDiagramView,"Factor 1");
		mainPane.add(secondDiagramView,"Factor 2");
		mainPane.add(nestedDiagramView,"Nested");

		listView.addMouseListener(new MouseAdapter() {
			class MoveAndUpdateListener implements EventBrokerListener {
				NDimNodeMovementEventListener nodeListener = new NDimNodeMovementEventListener();
				Factorization currentFactorization;
				public void processEvent(Event e) {
					this.nodeListener.processEvent(e);
					this.currentFactorization.updateNestedDiagram();
					nestedDiagramView.showDiagram(this.currentFactorization.nestedDiagram);
				}
			}
			MoveAndUpdateListener moveAndUpdateListener = new MoveAndUpdateListener();
			public void mouseClicked(MouseEvent e) {
				final Factorization factorization = (Factorization) listView.getSelectedValue();
				this.moveAndUpdateListener.currentFactorization = factorization;
				
				firstDiagramView.showDiagram(factorization.firstFactor);
				firstDiagramView.getController().getEventBroker().subscribe(this.moveAndUpdateListener, CanvasItemDraggedEvent.class, NodeView.class);
				secondDiagramView.showDiagram(factorization.secondFactor);
				secondDiagramView.getController().getEventBroker().subscribe(this.moveAndUpdateListener, CanvasItemDraggedEvent.class, NodeView.class);
				nestedDiagramView.showDiagram(factorization.nestedDiagram);
			}
		});

		mainWindow.pack();
		mainWindow.setBounds(10,10,900,700);
		mainWindow.setVisible(true);
		mainWindow.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	protected static Context makeContextCopy(Context context) {
		ContextImplementation retVal = new ContextImplementation();
		retVal.setName(context.getName());
		retVal.getObjects().addAll(context.getObjects());
		retVal.getAttributes().addAll(context.getAttributes());
		for (Iterator objIt = retVal.getObjects().iterator(); objIt.hasNext();) {
			Object obj = objIt.next();
			for (Iterator attrIt = retVal.getAttributes().iterator(); attrIt.hasNext();) {
				Object attr = attrIt.next();
				if(context.getRelation().contains(obj,attr)) {
					retVal.getRelationImplementation().insert(obj,attr);
				}
			}
		}
		return retVal;
	}

	private static List findAllSubsetsOfSize(Set objects, int n) {
		List retVal = new ArrayList();
		if(objects.size() == n) {
			retVal.add(objects);
		} else if(n != 0) {
			Object firstObject = objects.iterator().next();
			Set rest = new HashSet(objects);
			rest.remove(firstObject);
			List smallerSets = findAllSubsetsOfSize(rest, n-1);
			for (Iterator iter = smallerSets.iterator(); iter.hasNext();) {
				Set set = (Set) iter.next();
				set.add(firstObject);
				retVal.add(set);
			}
			retVal.addAll(findAllSubsetsOfSize(rest,n));			
		}
		return retVal;
	}
}
