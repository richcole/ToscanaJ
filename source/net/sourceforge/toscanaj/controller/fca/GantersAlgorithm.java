/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.lattice.LatticeImplementation;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.BinaryRelation;
import net.sourceforge.toscanaj.model.cernato.FCAObject;

import java.util.*;

public class GantersAlgorithm implements LatticeGenerator {
    public Lattice createLattice(Context context) {
        LatticeImplementation retVal = new LatticeImplementation();
        Collection objectCol = context.getObjects();
        Object[] objects = new Object[objectCol.size()];
        objectCol.toArray(objects);
        Vector extents = new Vector();
        Set extent = new HashSet();
        createClosure(context, extent);
        extents.add(extent);
        do {
            for (int i = objects.length-1; i >= 0; i--) {
                Set newExtent = calculateNewExtent(context, objects, extent, i);
                if(iLargerThan(objects, newExtent, extent, i)) {
                    extents.add(newExtent);
                    extent = newExtent;
                    for (Iterator intit = newExtent.iterator(); intit.hasNext();) {
                        FCAObject fcaObject = (FCAObject) intit.next();
                        System.out.print(fcaObject.getName() + ", ");
                    }
                    System.out.println("");
                    break;
                }
            }
        } while(extent.size() != objects.length);
        for (Iterator iterator = extents.iterator(); iterator.hasNext();) {
            Set set = (Set) iterator.next();
            for (Iterator intit = set.iterator(); intit.hasNext();) {
                FCAObject fcaObject = (FCAObject) intit.next();
                System.out.print(fcaObject.getName() + ", ");
            }
            System.out.println("");
        }
        return retVal;
    }

    private boolean iLargerThan(Object[] objects, Set largerSet, Set smallerSet, int i) {
        for(int j = 0; j <= i-1; j++) {
            if(largerSet.contains(objects[j]) != smallerSet.contains(objects[j])) {
                return false;
            }
        }
        if(smallerSet.contains(objects[i])) {
            return false;
        }
        if(!largerSet.contains(objects[i])) {
            return false;
        }
        return true;
    }

    private Set calculateNewExtent(Context context, Object[] objects, Set extent, int i) {
        Set newExtent = new HashSet();
        for(int j = 0; j <= i-1; j++) {
            if(extent.contains(objects[j])) {
                newExtent.add(objects[j]);
            }
        }
        newExtent.add(objects[i]);
        createClosure(context, newExtent);
        return newExtent;
    }

    private void createClosure(Context context, Set extent) {
        Collection attributes = context.getAttributes();
        BinaryRelation relation = context.getRelation();
        Set intent = new HashSet(attributes);
        for (Iterator it1 = extent.iterator(); it1.hasNext();) {
            Object object = it1.next();
            Set unrelatedAttributes = new HashSet();
            for (Iterator it2 = intent.iterator(); it2.hasNext();) {
                Object attribute = it2.next();
                if(!relation.contains(object, attribute)) {
                    unrelatedAttributes.add(attribute);
                }
            }
            intent.removeAll(unrelatedAttributes);
        }
        extent.clear();
        extent.addAll(context.getObjects());
        for (Iterator it1 = intent.iterator(); it1.hasNext();) {
            Object attribute = it1.next();
            Set unrelatedObjects = new HashSet();
            for (Iterator it2 = extent.iterator(); it2.hasNext();) {
                Object object = it2.next();
                if(!relation.contains(object, attribute)) {
                    unrelatedObjects.add(object);
                }
            }
            extent.removeAll(unrelatedObjects);
        }
    }
}
