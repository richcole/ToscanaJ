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
    /**
     * This introduces value identity on object tupels.
     */
    private static class ObjectTupel {
        private Object[] tupel;
        public ObjectTupel(Object[] tupel) {
            this.tupel = tupel;    
        }
        public boolean equals(Object other) {
            if(this.getClass() != other.getClass()) {
                return false;
            }
            ObjectTupel otherTupel = (ObjectTupel) other;
            if(otherTupel.tupel.length != this.tupel.length) {
                return false;
            }
            for (int i = 0; i < otherTupel.tupel.length; i++) {
                if(!otherTupel.tupel[i].equals(this.tupel[i])) {
                    return false;
                }
            }
            return true;
        }
        public int hashCode() {
            int hashCode = 7;
            for (int i = 0; i < this.tupel.length; i++) {
                Object element = this.tupel[i];
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
    
    /**
     * Creates a conceptual schema, taking one element as the objects the rest as attributes.
     * 
     * The return value is a conceptual schema with n diagrams (n = number of crossproducts given).
     * The objects are the values of the elements given by objectPosition, the attribute are defined
     * by the arrays in crossProducts -- for each of these an attribute set is created based on the
     * crossproduct of the elements given by the int array. The relation used is based on the 
     * projection of the tupels onto G x (M1 x M2 x ... x Mn). 
     */
    public static ConceptualSchema scaleTupels(TupelSet tupels, int objectPosition, int[][] crossProducts) {
        ConceptualSchema schema = new ConceptualSchema(new EventBroker());

        // first turn all tupels into FCAObjects, the attributes are done inline
        Map tupelObjectMap = new HashMap();
        for (Iterator iter = tupels.getTupels().iterator(); iter.hasNext();) {
            Object[] tupel = (Object[]) iter.next();
            FCAObject newObject = new FCAObjectImplementation(tupel[objectPosition]);
            tupelObjectMap.put(tupel[objectPosition], newObject);
        }
        
        String[] variableNames = tupels.getVariableNames();
        for (int i = 0; i < crossProducts.length; i++) {
            int[] crossProduct = crossProducts[i];
            Map valueAttributeMap = new HashMap();
            ContextImplementation context = new ContextImplementation("Tupels");
            context.getObjects().addAll(tupelObjectMap.values());
            for (Iterator iter = tupels.getTupels().iterator(); iter.hasNext();) {
                Object[] tupel = (Object[]) iter.next();
                Object[] values = new Object[crossProduct.length];
                for (int j = 0; j < crossProduct.length; j++) {
                    values[j] = tupel[crossProduct[j]];
                }
                ObjectTupel attributeValues = new ObjectTupel(values); 
                if(valueAttributeMap.get(attributeValues) == null) {
                    Attribute newAttribute = new Attribute(createCrossproductName(values));
                    valueAttributeMap.put(attributeValues, newAttribute);
                    context.getAttributes().add(newAttribute);
                }
                context.getRelationImplementation().insert(tupelObjectMap.get(tupel[objectPosition]), valueAttributeMap.get(attributeValues));
            }
            Lattice lattice = new GantersAlgorithm().createLattice(context);
            System.out.println(lattice.getConcepts().length);
            String diagName = createCrossproductName(variableNames, crossProduct);
            Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, diagName, new DefaultDimensionStrategy());
            schema.addDiagram(diagram);
        }

        return schema;
    }

    private static String createCrossproductName(String[] variableNames, int[] crossProduct) {
        StringBuffer retVal = new StringBuffer();
        for (int j = 0; j < crossProduct.length; j++) {
            if(j != 0) {
                retVal.append(" x ");
            }
            int index = crossProduct[j];
            retVal.append(variableNames[index]);
        }
        return retVal.toString();
    }
    
    private static String createCrossproductName(Object[] values) {
        StringBuffer retVal = new StringBuffer();
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
        TupelSet input = TupelParser.parseTabDelimitedTupels(new FileReader(new File(args[0])));
        ConceptualSchema result = scaleTupels(input, objectPos);
        // this is how to do a system with crossproducts
        //ConceptualSchema result = scaleTupels(input, 5, new int[][]{
        //    new int[]{0,7},
        //    new int[]{2,3},
        //    new int[]{0,3,7}
        //});
        XMLWriter.write(new File(args[1]), result);
    }
}
