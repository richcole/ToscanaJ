/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.scaling;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.tockit.context.model.ContextImplementation;
import org.tockit.events.EventBroker;
import org.tockit.relations.model.Relation;
import org.tockit.relations.model.Tuple;
import org.tockit.tupleware.source.text.SeparatedTextParser;
import org.tockit.tupleware.util.StringMapper;
import org.tockit.tupleware.util.IdentityStringMapper;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.util.xmlize.XMLWriter;


// @todo move this code onto relational algebra code
public class TupleScaling {
    /**
     * This introduces value identity on object tuples.
     */
    private static class ObjectTuple {
        private Object[] data;
        public ObjectTuple(Object[] data) {
            this.data = data;    
        }
        @Override
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
        @Override
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
     * Each binary relation for the contexts is the projection of the tuples onto the dimension
     * given by the objectPosition parameter and one other.
     */
    public static ConceptualSchema scaleTuples(Relation<Object> tuples, int objectPosition, StringMapper nameMapper) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());

        String[] variableNames = tuples.getDimensionNames();
        for (int i = 0; i < variableNames.length; i++) {
            if(i == objectPosition) {
                continue;
            }
            schema.addDiagram(scaleTuples(tuples, new int[]{objectPosition}, new int[]{i}, nameMapper));
        }

        return schema;
    }

    /**
     * Creates a diagram projecting the given indices on the context.
     * 
     * The objectIndices parameter defines the objects, the attributeIndices parameter
     * the attributes. They incide iff they co-occur in a tuple.
     */    
    public static Diagram2D scaleTuples(Relation<Object> tuples, int[] objectIndices, int[] attributeIndices, StringMapper nameMapper) {
        Map<ObjectTuple, FCAElement> tupleObjectMap = new HashMap<ObjectTuple, FCAElement>();
        Map<ObjectTuple, FCAElement> valueAttributeMap = new HashMap<ObjectTuple, FCAElement>();
        ContextImplementation<FCAElement, FCAElement> context = new ContextImplementation<FCAElement, FCAElement>("Tuples");
        context.getObjects().addAll(tupleObjectMap.values());
        for (Iterator<Tuple<? extends Object>> iter = tuples.getTuples().iterator(); iter.hasNext();) {
            Tuple<? extends Object> tuple = iter.next();
            ObjectTuple objectValues = selectSubset(tuple.getData(), objectIndices);
            if(tupleObjectMap.get(objectValues) == null) {
                FCAElement newObject = new FCAElementImplementation(nameMapper.mapString(createCrossproductName(objectValues)));
                tupleObjectMap.put(objectValues, newObject);
                context.getObjects().add(newObject);
            }
            ObjectTuple attributeValues = selectSubset(tuple.getData(), attributeIndices); 
            if(valueAttributeMap.get(attributeValues) == null) {
                FCAElement newAttribute = new FCAElementImplementation(nameMapper.mapString(createCrossproductName(attributeValues)));
                valueAttributeMap.put(attributeValues, newAttribute);
                context.getAttributes().add(newAttribute);
            }
            context.getRelationImplementation().insert(tupleObjectMap.get(objectValues), valueAttributeMap.get(attributeValues));
        }
        Lattice lattice = new GantersAlgorithm().createLattice(context);
        String[] variableNames = tuples.getDimensionNames();
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
        Relation<Object> input = SeparatedTextParser.parseTabDelimitedTuples(new FileReader(new File(args[0])),'\t','\"','\000',true);
        ConceptualSchema result = scaleTuples(input, objectPos, new IdentityStringMapper());
        XMLWriter.write(new File(args[1]), result);
    }
}
