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
    public static ConceptualSchema scaleTupels(TupelSet tupels, int objectPosition) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());

        // first turn all tupels into FCAObjects, the attributes are done inline
        Map tupelObjectMap = new HashMap();
        for (Iterator iter = tupels.getTupels().iterator(); iter.hasNext();) {
            Object[] tupel = (Object[]) iter.next();
            FCAObject newObject = new FCAObjectImplementation(tupel[objectPosition]);
            tupelObjectMap.put(tupel[objectPosition], newObject);
        }
        
        String[] variableNames = tupels.getVariableNames();
        for (int i = 0; i < variableNames.length; i++) {
            if(i == objectPosition) {
                continue;
            }
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
                context.getRelationImplementation().insert(tupelObjectMap.get(tupel[objectPosition]), valueAttributeMap.get(tupel[i]));
            }
            Lattice lattice = new GantersAlgorithm().createLattice(context);
            Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, varName, new DefaultDimensionStrategy());
            schema.addDiagram(diagram);
        }

        return schema;
    }
    
    public static void main(String[] args) throws Exception {
        int objectPos;
        if(args.length == 3) {
            objectPos = Integer.parseInt(args[2]);
        } else {
            objectPos = 1;
        }
        TupelSet input = TupelParser.parseTabDelimitedTupels(new FileReader(new File(args[0])));
        ConceptualSchema result = scaleTupels(input, objectPos);
        XMLWriter.write(new File(args[1]), result);
    }
}
