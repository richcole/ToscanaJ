/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.scaling;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.tockit.events.EventBroker;
import org.tockit.tupelware.model.TupelSet;
import org.tockit.tupelware.parser.TupelParser;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAObject;
import net.sourceforge.toscanaj.model.context.FCAObjectImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.util.xmlize.XMLWriter;


public class TupelScaling {
    public static ConceptualSchema scaleWithTupelsAsObjects(TupelSet tupels) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());

        // first turn all tupels into FCAObjects
        Map tupelObjectMap = new HashMap();
        for (Iterator iter = tupels.getTupels().iterator(); iter.hasNext();) {
            Object[] tupel = (Object[]) iter.next();
            FCAObject newObject = new FCAObjectImplementation(TupelSet.toString(tupel));
            tupelObjectMap.put(tupel, newObject);
        }
        
        String[] variableNames = tupels.getVariableNames();
        for (int i = 0; i < variableNames.length; i++) {
            String varName = variableNames[i];
            Map valueAttributeMap = new HashMap();
            ContextImplementation context = new ContextImplementation("Tupels");
            context.getObjects().addAll(tupelObjectMap.values());
            for (Iterator iter = tupels.getTupels().iterator(); iter.hasNext();) {
                Object[] tupel = (Object[]) iter.next();
                if(valueAttributeMap.get(tupel[i]) == null) {
                    Attribute newAttribute = new Attribute(tupel[i]);
                    valueAttributeMap.put(tupel[i], newAttribute);
                    context.getAttributes().add(newAttribute);
                }
                context.getRelationImplementation().insert(tupelObjectMap.get(tupel), valueAttributeMap.get(tupel[i]));
            }
            Lattice lattice = new GantersAlgorithm().createLattice(context);
            Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, varName, new DefaultDimensionStrategy());
            schema.addDiagram(diagram);
        }

        return schema;
    }
    
    public static void main(String[] args) throws Exception {
        TupelSet input = TupelParser.parseTabDelimitedTupels(new FileReader(new File(args[0])));
        ConceptualSchema result = scaleWithTupelsAsObjects(input);
        XMLWriter.write(new File(args[1]), result);
    }
}
