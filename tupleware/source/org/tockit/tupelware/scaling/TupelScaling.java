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
    private static class ObjectTuple {
        private Object[] data;
        public ObjectTuple(Object[] data) {
            this.data = data;    
        }
        public boolean equals(Object other) {
            if(this.getClass() != other.getClass()) {
                return false;
            }
            ObjectTuple otherTuple = (ObjectTuple) other;
            if(otherTuple.data.length != this.data.length) {
                return false;
            }
            for (int i = 0; i < otherTuple.data.length; i++) {
                if(!otherTuple.data[i].equals(this.data[i])) {
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
     * The return value is a conceptual schema with n-1 diagrams (n = number of elements in tuples).
     * Each binary relation for the contexts is the projection of the tupels onto the dimension
     * given by the objectPosition parameter and one other.
     */
    public static ConceptualSchema scaleTuples(TupelSet tuples, int objectPosition) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());

        String[] variableNames = tuples.getVariableNames();
        for (int i = 0; i < variableNames.length; i++) {
            if(i == objectPosition) {
                continue;
            }
            schema.addDiagram(scaleTuples(tuples, new int[]{objectPosition}, new int[]{i}));
        }

        return schema;
    }

    /**
     * Creates a diagram projecting the given indices on the context.
     * 
     * The objectIndices parameter defines the objects, the attributeIndices parameter
     * the attributes. They incide iff they cooccur in a tuple.
     */    
    public static Diagram2D scaleTuples(TupelSet tuples, int[] objectIndices, int[] attributeIndices) {
        Map tupleObjectMap = new HashMap();
        Map valueAttributeMap = new HashMap();
        ContextImplementation context = new ContextImplementation("Tuples");
        context.getObjects().addAll(tupleObjectMap.values());
        for (Iterator iter = tuples.getTuples().iterator(); iter.hasNext();) {
            Object[] tuple = (Object[]) iter.next();
            ObjectTuple objectValues = selectSubset(tuple, objectIndices);
            if(tupleObjectMap.get(objectValues) == null) {
                FCAObject newObject = new FCAObjectImplementation(createCrossproductName(objectValues));
                tupleObjectMap.put(objectValues, newObject);
                context.getObjects().add(newObject);
            }
            ObjectTuple attributeValues = selectSubset(tuple, attributeIndices); 
            if(valueAttributeMap.get(attributeValues) == null) {
                Attribute newAttribute = new Attribute(createCrossproductName(attributeValues));
                valueAttributeMap.put(attributeValues, newAttribute);
                context.getAttributes().add(newAttribute);
            }
            context.getRelationImplementation().insert(tupleObjectMap.get(objectValues), valueAttributeMap.get(attributeValues));
        }
        Lattice lattice = new GantersAlgorithm().createLattice(context);
        String[] variableNames = tuples.getVariableNames();
        String diagName = createCrossproductName(selectSubset(variableNames, attributeIndices));
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, diagName, new DefaultDimensionStrategy());
        return diagram;
    }

    private static ObjectTuple selectSubset(Object[] inTuple, int[] indices) {
        Object[] retVal = new Object[indices.length];
        for (int j = 0; j < indices.length; j++) {
            retVal[j] = inTuple[indices[j]];
        }
        return new ObjectTuple(retVal);
    }
    
    private static String createCrossproductName(ObjectTuple tuple) {
        StringBuffer retVal = new StringBuffer();
        Object[] values = tuple.data;
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
        TupelSet input = TabDelimitedParser.parseTabDelimitedTuples(new FileReader(new File(args[0])));
        ConceptualSchema result = scaleTuples(input, objectPos);
        XMLWriter.write(new File(args[1]), result);
    }
}
