/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.tests;

import java.util.Collection;

import javax.swing.*;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.events.EventBroker;

public class DividesContextTester {

    public static void main(final String[] args) throws Exception {
        final ContextImplementation context = new ContextImplementation();
        final Collection<FCAElementImplementation> objects = context.getObjects();
        final Collection<FCAElementImplementation> finalAttributes = context.getAttributes();
        final BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation> relation =
                context.getRelationImplementation();

        int max;
        if (args.length == 0) {
            max = 100;
        } else {
            max = Integer.parseInt(args[0]);
        }
        System.out.println("Generating 'divides'-lattice for numbers up to " + max);

        final FCAElementImplementation[] attributes = new FCAElementImplementation[max];
        for (int i = 1; i <= max; i++) {
            attributes[i - 1] = new FCAElementImplementation(i);
            finalAttributes.add(attributes[i - 1]);
        }

        for (int i = 1; i <= max; i++) {
            final FCAElementImplementation object = new FCAElementImplementation(i);
            objects.add(object);
            for (int j = 1; j <= max; j++) {
                if (i % j == 0) {
                    relation.insert(object, attributes[j - 1]);
                }
            }
        }

        final LatticeGenerator<FCAElementImplementation, FCAElementImplementation> lGen =
                new GantersAlgorithm<FCAElementImplementation, FCAElementImplementation>();

        long startMillis = System.currentTimeMillis();
        final Lattice<FCAElementImplementation, FCAElementImplementation> lattice = lGen.createLattice(context);
        System.out.println("Lattice generation: "
                + (System.currentTimeMillis() - startMillis) + " ms");

        startMillis = System.currentTimeMillis();
        final Diagram2D<FCAElementImplementation, FCAElementImplementation> diagram = NDimLayoutOperations
                .createDiagram(lattice, "test", new DefaultDimensionStrategy<FCAElementImplementation>());
        System.out.println("Diagram layout: "
                + (System.currentTimeMillis() - startMillis) + " ms");

        final EventBroker broker = new EventBroker();
        final ConceptualSchema schema = new ConceptualSchema(broker);
        schema.addDiagram(diagram);

        final JFrame mainPanel = new JFrame();
        final DiagramEditingView edView = new DiagramEditingView(mainPanel,
                schema, broker, false);
        mainPanel.setContentPane(edView);
        mainPanel.setBounds(10, 10, 700, 500);
        mainPanel.setVisible(true);
        mainPanel.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
