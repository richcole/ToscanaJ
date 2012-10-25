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

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimNodeMovementEventListener;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.parser.BurmeisterParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.context.model.Context;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class Factorizer {
    static class Factorization {
        Set attributes;
        Diagram2D firstFactor;
        Diagram2D secondFactor;
        Diagram2D nestedDiagram;

        Factorization(final Context fullContext, final Set factorAttributes) {
            this.attributes = factorAttributes;
            final LatticeGenerator lgen = new GantersAlgorithm();

            final Context context1 = makeContextCopy(fullContext);
            context1.getAttributes().retainAll(this.attributes);
            final Lattice lattice1 = lgen.createLattice(context1);
            this.firstFactor = NDimLayoutOperations.createDiagram(lattice1,
                    fullContext.getName(), new DefaultDimensionStrategy());

            final Context context2 = makeContextCopy(fullContext);
            context2.getAttributes().removeAll(this.attributes);
            final Lattice lattice2 = lgen.createLattice(context2);
            this.secondFactor = NDimLayoutOperations.createDiagram(lattice2,
                    fullContext.getName(), new DefaultDimensionStrategy());

            updateNestedDiagram();
        }

        void updateNestedDiagram() {
            this.nestedDiagram = new NestedLineDiagram(this.firstFactor,
                    this.secondFactor);
        }

        @Override
        public String toString() {
            final int numOuterConcepts = this.firstFactor.getNumberOfNodes();
            final int numInnerConcepts = this.secondFactor.getNumberOfNodes();
            return this.attributes.toString() + " (" + numOuterConcepts + "*"
                    + numInnerConcepts + "=" + numOuterConcepts
                    * numInnerConcepts + ")";
        }
    }

    public static void main(final String[] args) throws FileNotFoundException,
            DataFormatException {
        final JFileChooser fileChooser = new JFileChooser();

        final JPanel numberPanel = new JPanel(new FlowLayout());
        final JSpinner spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(5, 2, 20, 1));
        numberPanel.add(new JLabel("Factor size:"));
        numberPanel.add(spinner);

        fileChooser.setAccessory(numberPanel);

        final int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }
        final File inputFile = fileChooser.getSelectedFile();
        final Context<FCAElementImplementation, FCAElementImplementation> context =
                BurmeisterParser.importBurmeisterFile(inputFile);
        final Set<FCAElementImplementation> attributes = context.getAttributes();

        final List<Set<FCAElementImplementation>> subsets = findAllSubsetsOfSize(attributes,
                (Integer) spinner.getModel().getValue());
        final List<Factorization> diagrams = createFactorizedDiagrams(context, subsets);
        showResults(diagrams);
    }

    private static <T extends Object> List<Factorization> createFactorizedDiagrams(
            final Context context, final List<Set<T>> subsets) {
        final List<Factorization> retVal = new ArrayList<Factorization>();
        for (final Iterator<Set<T>> it = subsets.iterator(); it.hasNext();) {
            final Set<T> set = it.next();
            retVal.add(new Factorization(context, set));
        }
        return retVal;
    }

    private static void showResults(final List<Factorization> diagrams) {
        final JFrame mainWindow = new JFrame("Factorizer");

        final JTabbedPane mainPane = new JTabbedPane();
        final JList listView = new JList(diagrams.toArray());
        final JScrollPane scrollPane = new JScrollPane(listView);
        final JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, scrollPane, mainPane);
        mainWindow.getContentPane().add(splitPane);

        final DiagramView firstDiagramView = new DiagramView();
        firstDiagramView.setConceptInterpreter(new DirectConceptInterpreter());
        firstDiagramView
                .setConceptInterpretationContext(new ConceptInterpretationContext(
                        new DiagramHistory(), new EventBroker()));
        firstDiagramView.setQuery(AggregateQuery.COUNT_QUERY);

        final DiagramView secondDiagramView = new DiagramView();
        secondDiagramView.setConceptInterpreter(new DirectConceptInterpreter());
        secondDiagramView
                .setConceptInterpretationContext(new ConceptInterpretationContext(
                        new DiagramHistory(), new EventBroker()));
        secondDiagramView.setQuery(AggregateQuery.COUNT_QUERY);

        final DiagramView nestedDiagramView = new DiagramView();
        nestedDiagramView.setConceptInterpreter(new DirectConceptInterpreter());
        nestedDiagramView
                .setConceptInterpretationContext(new ConceptInterpretationContext(
                        new DiagramHistory(), new EventBroker()));
        nestedDiagramView.setQuery(AggregateQuery.COUNT_QUERY);

        mainPane.add(firstDiagramView, "Factor 1");
        mainPane.add(secondDiagramView, "Factor 2");
        mainPane.add(nestedDiagramView, "Nested");

        listView.addMouseListener(new MouseAdapter() {
            class MoveAndUpdateListener implements EventBrokerListener {
                NDimNodeMovementEventListener nodeListener = new NDimNodeMovementEventListener();
                Factorization currentFactorization;

                public void processEvent(final Event e) {
                    this.nodeListener.processEvent(e);
                    this.currentFactorization.updateNestedDiagram();
                    nestedDiagramView
                            .showDiagram(this.currentFactorization.nestedDiagram);
                }
            }

            MoveAndUpdateListener moveAndUpdateListener = new MoveAndUpdateListener();

            @Override
            public void mouseClicked(final MouseEvent e) {
                final Factorization factorization = (Factorization) listView
                        .getSelectedValue();
                this.moveAndUpdateListener.currentFactorization = factorization;

                firstDiagramView.showDiagram(factorization.firstFactor);
                firstDiagramView.getController().getEventBroker().subscribe(
                        this.moveAndUpdateListener,
                        CanvasItemDraggedEvent.class, NodeView.class);
                secondDiagramView.showDiagram(factorization.secondFactor);
                secondDiagramView.getController().getEventBroker().subscribe(
                        this.moveAndUpdateListener,
                        CanvasItemDraggedEvent.class, NodeView.class);
                nestedDiagramView.showDiagram(factorization.nestedDiagram);
            }
        });

        mainWindow.pack();
        mainWindow.setBounds(10, 10, 900, 700);
        mainWindow.setVisible(true);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
    }

    protected static Context<FCAElementImplementation, FCAElementImplementation>
                        makeContextCopy(final Context<FCAElementImplementation, FCAElementImplementation> context) {
        final ContextImplementation retVal = new ContextImplementation();
        retVal.setName(context.getName());
        retVal.getObjects().addAll(context.getObjects());
        retVal.getAttributes().addAll(context.getAttributes());
        for (final FCAElementImplementation obj : retVal.getObjects()) {
            for (final FCAElementImplementation attr : retVal.getAttributes()) {
                if (context.getRelation().contains(obj, attr)) {
                    retVal.getRelationImplementation().insert(obj, attr);
                }
            }
        }
        return retVal;
    }

    private static <T extends Object> List<Set<T>> findAllSubsetsOfSize(
            final Set<T> objects, final int n) {
        final List<Set<T>> retVal = new ArrayList<Set<T>>();
        if (objects.size() == n) {
            retVal.add(objects);
        } else if (n != 0) {
            final T firstObject = objects.iterator().next();
            final Set<T> rest = new HashSet<T>(objects);
            rest.remove(firstObject);
            final List<Set<T>> smallerSets = findAllSubsetsOfSize(rest,
                    n - 1);
            for (final Set<T> set : smallerSets) {
                set.add(firstObject);
                retVal.add(set);
            }
            retVal.addAll(findAllSubsetsOfSize(rest, n));
        }
        return retVal;
    }
}
