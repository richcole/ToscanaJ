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
import org.tockit.tupelware.source.text.TabDelimitedParser;

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
    /**
     * This introduces value identity on object tupels.
     */
    private static class ObjectTupel {
        private Object[] data;
        public ObjectTupel(Object[] data) {
            this.data = data;    
        }
        public boolean equals(Object other) {
            if(this.getClass() != other.getClass()) {
                return false;
            }
            ObjectTupel otherTupel = (ObjectTupel) other;
            if(otherTupel.data.length != this.data.length) {
                return false;
            }
            for (int i = 0; i < otherTupel.data.length; i++) {
                if(!otherTupel.data[i].equals(this.data[i])) {
                    return false;
                }
            }
            return true;
        }
        public int hashCode() {
            int hashCode = 7;
            for (int i = 0; i < this.data.length; i++) {
                Object element = this.data[i];
                hashCode = 42*hashCode + (element == null ? 0 : element.hashCode());
            }
            return hashCode;
        }
    }
    
    /**
     * Creates a conceptual schema, taking one element as the objects the rest as attributes.
     * 
     * The return value is a conceptual schema with n-1 diagrams (n = number of elements in tupels).
     * Each binary relation for the contexts is the projection of the tupels onto the dimension
     * given by the objectPosition parameter and one other.
     */
    public static ConceptualSchema scaleTupels(TupelSet tupels, int objectPosition) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());

        String[] variableNames = tupels.getVariableNames();
        for (int i = 0; i < variableNames.length; i++) {
            if(i == objectPosition) {
                continue;
            }
            schema.addDiagram(scaleTupels(tupels, new int[]{objectPosition}, new int[]{i}));
        }

        return schema;
    }

    /**
     * Creates a diagram projecting the given indices on the context.
     * 
     * The objectIndices parameter defines the objects, the attributeIndices parameter
     * the attributes. They incide iff they cooccur in a tupel.
     */    
    public static Diagram2D scaleTupels(TupelSet tupels, int[] objectIndices, int[] attributeIndices) {
        Map tupelObjectMap = new HashMap();
        Map valueAttributeMap = new HashMap();
        ContextImplementation context = new ContextImplementation("Tupels");
        context.getObjects().addAll(tupelObjectMap.values());
        for (Iterator iter = tupels.getTupels().iterator(); iter.hasNext();) {
            Object[] tupel = (Object[]) iter.next();
            ObjectTupel objectValues = selectSubset(tupel, objectIndices);
            if(tupelObjectMap.get(objectValues) == null) {
                FCAObject newObject = new FCAObjectImplementation(createCrossproductName(objectValues));
                tupelObjectMap.put(objectValues, newObject);
                context.getObjects().add(newObject);
            }
            ObjectTupel attributeValues = selectSubset(tupel, attributeIndices); 
            if(valueAttributeMap.get(attributeValues) == null) {
                Attribute newAttribute = new Attribute(createCrossproductName(attributeValues));
                valueAttributeMap.put(attributeValues, newAttribute);
                context.getAttributes().add(newAttribute);
            }
            context.getRelationImplementation().insert(tupelObjectMap.get(objectValues), valueAttributeMap.get(attributeValues));
        }
        Lattice lattice = new GantersAlgorithm().createLattice(context);
        String[] variableNames = tupels.getVariableNames();
        String diagName = createCrossproductName(selectSubset(variableNames, attributeIndices));
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, diagName, new DefaultDimensionStrategy());
        return diagram;
    }

    private static ObjectTupel selectSubset(Object[] inTupel, int[] indices) {
        Object[] retVal = new Object[indices.length];
        for (int j = 0; j < indices.length; j++) {
            retVal[j] = inTupel[indices[j]];
        }
        return new ObjectTupel(retVal);
    }
    
    private static String createCrossproductName(ObjectTupel tupel) {
        StringBuffer retVal = new StringBuffer();
        Object[] values = tupel.data;
        for (int j = 0; j < values.length; j++) {
            if(j != 0) {
                retVal.append(" x ");
            }
            retVal.append(values[j].toString());
        }
        return retVal.toString();
    }
    
    public static void main(String[] args) throws Exception {
        int objectPos;
        if(args.length == 3) {
            objectPos = Integer.parseInt(args[2]);
        } else {
            objectPos = 0;
        }
        TupelSet input = TabDelimitedParser.parseTabDelimitedTupels(new FileReader(new File(args[0])));
        ConceptualSchema result = scaleTupels(input, objectPos);
        XMLWriter.write(new File(args[1]), result);
    }
}
