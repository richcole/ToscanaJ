/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;

import net.sourceforge.toscanaj.model.context.*;
import net.sourceforge.toscanaj.model.context.BinaryRelation;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.lattice.*;

public class GantersAlgorithm implements LatticeGenerator {
    private Context context;
    private FCAElement[] objects;
    private Hashtable intents;
    public Vector extents;

    public Lattice createLattice(Context inputContext) {
        LatticeImplementation lattice = new LatticeImplementation();
        context = inputContext;
        Collection objectCol = context.getObjects();
        objects = createObjectArray(objectCol);
        intents = new Hashtable();
        extents = new Vector();
        findExtents();
        createConcepts(lattice);
        connectConcepts(lattice);
        cleanContingents(lattice);
        return lattice;
    }

	/**
	 * This is similar to Collection.toArray(), but also checks for duplicates.
	 */
    public FCAElement[] createObjectArray(Collection objectCol) {
        FCAElement[] retVal = new FCAElement[objectCol.size()];
    	HashSet testSet = new HashSet();
    	Iterator it = objectCol.iterator();
    	int pos = 0;
    	while (it.hasNext()) {
            FCAElement cur = (FCAElement) it.next();
            if(testSet.contains(cur)) {
            	throw new IllegalArgumentException("Context '" + context.getName() + "' contains duplicate object");
            }
            retVal[pos] = cur;
            testSet.add(cur);
            pos++;
        }
        return retVal;
    }

    private void connectConcepts(LatticeImplementation lattice) {
        Concept[] concepts = lattice.getConcepts();
        for (int i = 0; i < concepts.length; i++) {
            ConceptImplementation concept1 = (ConceptImplementation) concepts[i];
            for (int j = 0; j < concepts.length; j++) {
                ConceptImplementation concept2 = (ConceptImplementation) concepts[j];
                if (isSubConcept(concept2, concept1)) {
                    concept1.addSubConcept(concept2);
                    concept2.addSuperConcept(concept1);
                }
            }
        }
        for (int i = 0; i < concepts.length; i++) {
            ConceptImplementation concept = (ConceptImplementation) concepts[i];
            concept.buildClosures();
        }
    }

    private boolean isSubConcept(Concept concept1, Concept concept2) {
        // the extents are still stored as contingents
        if (concept1.getObjectContingentSize() > concept2.getObjectContingentSize()) {
            return false;
        }
        ListSet extent2 = new ListSetImplementation();
        for (Iterator iterator = concept2.getObjectContingentIterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            extent2.add(obj);
        }
        for (Iterator iterator = concept1.getObjectContingentIterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            if (!extent2.contains(obj)) {
                return false;
            }
        }
        return true;
    }

    private void cleanContingents(LatticeImplementation lattice) {
        Concept[] concepts = lattice.getConcepts();
        for (int i = 0; i < concepts.length; i++) {
            ConceptImplementation concept = (ConceptImplementation) concepts[i];
            Collection downset = new HashSet(concept.getDownset());
            downset.remove(concept);
            for (Iterator iterator = downset.iterator(); iterator.hasNext();) {
                ConceptImplementation concept2 = (ConceptImplementation) iterator.next();
                for (Iterator iterator2 = concept2.getObjectContingentIterator(); iterator2.hasNext();) {
                    FCAElement object = (FCAElement) iterator2.next();
                    concept.removeObject(object);
                }
            }
            Collection upset = new HashSet(concept.getUpset());
            upset.remove(concept);
            for (Iterator iterator = upset.iterator(); iterator.hasNext();) {
                ConceptImplementation concept2 = (ConceptImplementation) iterator.next();
                for (Iterator iterator2 = concept2.getAttributeContingentIterator(); iterator2.hasNext();) {
                    FCAElement attribute = (FCAElement) iterator2.next();
                    concept.removeAttribute(attribute);
                }
            }
        }
    }

    private void findExtents() {
        ListSet extent = new ListSetImplementation();
        createClosure(extent);
        extents.add(extent);
        do {
            for (int i = objects.length - 1; i >= 0; i--) {
                ListSet newExtent = calculateNewExtent(extent, i);
                if (iLargerThan(newExtent, extent, i)) {
                    extents.add(newExtent);
                    extent = newExtent;
                    break;
                }
            }
        } while (extent.size() != objects.length);
    }

    private void createConcepts(LatticeImplementation lattice) {
        for (Iterator iterator = extents.iterator(); iterator.hasNext();) {
            ConceptImplementation concept = new ConceptImplementation();
            ListSet extent = (ListSet) iterator.next();
            for (Iterator extIt = extent.iterator(); extIt.hasNext();) {
                FCAElement fcaObject = (FCAElement) extIt.next();
                concept.addObject(fcaObject);
            }
            ListSet intent = (ListSet) intents.get(extent);
            for (Iterator intIt = intent.iterator(); intIt.hasNext();) {
                FCAElement attribute = (FCAElement) intIt.next();
                concept.addAttribute(attribute);
            }
            lattice.addConcept(concept);
        }
    }

    private boolean iLargerThan(ListSet largerSet, ListSet smallerSet, int i) {
        for (int j = 0; j <= i - 1; j++) {
            if (largerSet.contains(objects[j]) != smallerSet.contains(objects[j])) {
                return false;
            }
        }
        if (smallerSet.contains(objects[i])) {
            return false;
        }
        if (!largerSet.contains(objects[i])) {
            return false;
        }
        return true;
    }

    private ListSet calculateNewExtent(ListSet extent, int i) {
        ListSet newExtent = new ListSetImplementation();
        for (int j = 0; j <= i - 1; j++) {
            if (extent.contains(objects[j])) {
                newExtent.add(objects[j]);
            }
        }
        newExtent.add(objects[i]);
        createClosure(newExtent);
        return newExtent;
    }

    private void createClosure(ListSet extent) {
        Collection attributes = context.getAttributes();
        BinaryRelation relation = context.getRelation();
        ListSet intent = new ListSetImplementation(attributes);
        for (Iterator it1 = extent.iterator(); it1.hasNext();) {
            Object object = it1.next();
            ListSet unrelatedAttributes = new ListSetImplementation();
            for (Iterator it2 = intent.iterator(); it2.hasNext();) {
                Object attribute = it2.next();
                if (!relation.contains(object, attribute)) {
                    unrelatedAttributes.add(attribute);
                }
            }
            intent.removeAll(unrelatedAttributes);
        }
        extent.clear();
        extent.addAll(context.getObjects());
        for (Iterator it1 = intent.iterator(); it1.hasNext();) {
            Object attribute = it1.next();
            ListSet unrelatedObjects = new ListSetImplementation();
            for (Iterator it2 = extent.iterator(); it2.hasNext();) {
                Object object = it2.next();
                if (!relation.contains(object, attribute)) {
                    unrelatedObjects.add(object);
                }
            }
            extent.removeAll(unrelatedObjects);
        }
        intents.put(extent, intent);
    }
}
