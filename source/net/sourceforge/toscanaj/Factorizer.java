/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.SpinnerNumberModel;

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.parser.BurmeisterParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

public class Factorizer {

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
			retVal.add(createFactorizedDiagram(context, set));
		}
		return retVal;
	}

	private static void showResults(List diagrams) {
		JFrame mainWindow = new JFrame("Factorizer");
		BorderLayout layout = new BorderLayout();
		Container contentPane = mainWindow.getContentPane();
		contentPane.setLayout(layout);

		final JList listView = new JList(diagrams.toArray());
		JScrollPane scrollPane = new JScrollPane(listView);
		contentPane.add(scrollPane, BorderLayout.WEST);

		final DiagramView diagramView = new DiagramView();
		diagramView.setConceptInterpreter(new DirectConceptInterpreter());
		diagramView.setConceptInterpretationContext(new ConceptInterpretationContext(new DiagramHistory(), new EventBroker()));
		diagramView.setQuery(ListQuery.KEY_LIST_QUERY);
		diagramView.setMinimumFontSize(8.0);
		ObjectLabelView.setAllHidden(true);
		contentPane.add(diagramView, BorderLayout.CENTER);

		listView.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Diagram2D diagram = (Diagram2D) listView.getSelectedValue();
				diagramView.showDiagram(diagram);
			}
		});

		mainWindow.pack();
		mainWindow.setBounds(10,10,900,700);
		mainWindow.show();
		mainWindow.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public static Diagram2D createFactorizedDiagram(final Context context, Set set) {
		LatticeGenerator lgen = new GantersAlgorithm();
				
		Context context1 = makeContextCopy(context);
		context1.getAttributes().retainAll(set);
		Lattice lattice1 = lgen.createLattice(context1);
		Diagram2D diagram1 = NDimLayoutOperations.createDiagram(lattice1, context.getName(), new DefaultDimensionStrategy());
				
		Context context2 = makeContextCopy(context);
		context2.getAttributes().removeAll(set);
		Lattice lattice2 = lgen.createLattice(context2);
		Diagram2D diagram2 = NDimLayoutOperations.createDiagram(lattice2, context.getName(), new DefaultDimensionStrategy());
				
		WriteableDiagram2D nestedDiagram = new NestedLineDiagram(diagram1, diagram2);
		int numOuterConcepts = lattice1.getConcepts().length;
		int numInnerConcepts = lattice2.getConcepts().length;
		nestedDiagram.setTitle(set.toString() + " (" + numOuterConcepts + "*" + 
													   numInnerConcepts + "=" +
													   numOuterConcepts * numInnerConcepts + ")");
		return nestedDiagram;
	}

	protected static Context makeContextCopy(Context context) {
		ContextImplementation retVal = new ContextImplementation();
		retVal.setName(context.getName());
		retVal.getObjects().addAll(context.getObjects());
		retVal.getAttributes().addAll(context.getAttributes());
		for (Iterator objIt = retVal.getObjects().iterator(); objIt.hasNext();) {
			Object obj = (Object) objIt.next();
			for (Iterator attrIt = retVal.getAttributes().iterator(); attrIt.hasNext();) {
				Object attr = (Object) attrIt.next();
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
