/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.tests;
import java.util.Collection;

import javax.swing.JFrame;

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

public class DividesContextTester {

    public static void main(String[] args) throws Exception {
    	ContextImplementation context = new ContextImplementation();
    	Collection objects = context.getObjects();
    	Collection finalAttributes = context.getAttributes();
		BinaryRelationImplementation relation = context.getRelationImplementation();
		
		int max;
		if(args.length == 0) {
			max = 100;
		} else {
			max = Integer.parseInt(args[0]);
		}
		System.out.println("Generating 'divides'-lattice for numbers up to " + max);
		
        FCAElement[] attributes = new FCAElement[max];
        for(int i = 1; i<=max; i++) {
            attributes[i-1] = new FCAElementImplementation(new Integer(i));
            finalAttributes.add(attributes[i-1]);
        }
		
        for(int i = 1; i<=max; i++) {
        	Object object = new Integer(i);
        	objects.add(object);
            for(int j = 1; j <=max; j++) {
            	if(i%j == 0) {
            		relation.insert(object, attributes[j-1]);
            	}
            }
        }
        
        LatticeGenerator lGen = new GantersAlgorithm();
        
        long startMillis = System.currentTimeMillis();
        Lattice lattice = lGen.createLattice(context);
        System.out.println("Lattice generation: " + (System.currentTimeMillis() - startMillis) + " ms");
        
        startMillis = System.currentTimeMillis();
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, "test", new DefaultDimensionStrategy());
        System.out.println("Diagram layout: " + (System.currentTimeMillis() - startMillis) + " ms");
        
        EventBroker broker = new EventBroker();
        ConceptualSchema schema = new ConceptualSchema(broker);
        schema.addDiagram(diagram);
        
        JFrame mainPanel = new JFrame();
        DiagramEditingView edView = new DiagramEditingView(mainPanel, schema, broker);
        mainPanel.setContentPane(edView);
        mainPanel.setBounds(10,10,700,500);
        mainPanel.setVisible(true);
    }
}
