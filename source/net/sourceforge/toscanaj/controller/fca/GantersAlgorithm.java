/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.*;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.BinaryRelation;
import net.sourceforge.toscanaj.model.cernato.FCAObject;
import net.sourceforge.toscanaj.model.cernato.Criterion;

import java.util.*;

public class GantersAlgorithm implements LatticeGenerator {
    private Context context;
    private Object[] objects;
    private Hashtable intents;
    private Hashtable attributes;
    public Vector extents;

    public Lattice createLattice(Context inputContext) {
        LatticeImplementation lattice = new LatticeImplementation();
        context = inputContext;
        Collection objectCol = context.getObjects();
        objects = new Object[objectCol.size()];
        objectCol.toArray(objects);
        intents = new Hashtable();
        attributes = new Hashtable();
        extents = new Vector();
        findExtents();
        createConcepts(lattice);
        connectConcepts(lattice);
        cleanContingents(lattice);
        return lattice;
    }

    private void connectConcepts(LatticeImplementation lattice) {
        Concept[] concepts = lattice.getConcepts();
        for (int i = 0; i < concepts.length; i++) {
            ConceptImplementation concept1 = (ConceptImplementation) concepts[i];
            for (int j = 0; j < concepts.length; j++) {
                ConceptImplementation concept2 = (ConceptImplementation) concepts[j];
                if(isSubConcept(concept2, concept1)) {
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
        if( concept1.getObjectContingentSize() > concept2.getObjectContingentSize() ) {
            return false;
        }
        Set extent2 = new HashSet();
        for (Iterator iterator = concept2.getObjectContingentIterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            extent2.add(obj);
        }
        for (Iterator iterator = concept1.getObjectContingentIterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            if(!extent2.contains(obj)) {
                return false;
            }
        }
        return true;
    }

    private void cleanContingents(LatticeImplementation lattice) {
        Concept[] concepts = lattice.getConcepts();
        for (int i = 0; i < concepts.length; i++) {
            ConceptImplementation concept = (ConceptImplementation) concepts[i];
            Collection downset = concept.getDownset();
            downset.remove(concept);
            for (Iterator iterator = downset.iterator(); iterator.hasNext();) {
                ConceptImplementation concept2 = (ConceptImplementation) iterator.next();
                for (Iterator iterator2 = concept2.getObjectContingentIterator(); iterator2.hasNext();) {
                    Object object = iterator2.next();
                    concept.removeObject(object);
                }
            }
            Collection upset = concept.getUpset();
            upset.remove(concept);
            for (Iterator iterator = upset.iterator(); iterator.hasNext();) {
                ConceptImplementation concept2 = (ConceptImplementation) iterator.next();
                for (Iterator iterator2 = concept2.getAttributeContingentIterator(); iterator2.hasNext();) {
                    Attribute attribute = (Attribute) iterator2.next();
                    concept.removeAttribute(attribute);
                }
            }
        }
    }

    private void findExtents() {
        Set extent = new HashSet();
        createClosure(extent);
        extents.add(extent);
        do {
            for (int i = objects.length-1; i >= 0; i--) {
                Set newExtent = calculateNewExtent(extent, i);
                if(iLargerThan(newExtent, extent, i)) {
                    extents.add(newExtent);
                    extent = newExtent;
                    break;
                }
            }
        } while(extent.size() != objects.length);
    }

    private void createConcepts(LatticeImplementation lattice) {
        for (Iterator iterator = extents.iterator(); iterator.hasNext();) {
            ConceptImplementation concept = new ConceptImplementation();
            Set ext = (Set) iterator.next();
            for (Iterator intit = ext.iterator(); intit.hasNext();) {
                FCAObject fcaObject = (FCAObject) intit.next();
                concept.addObject(fcaObject);
            }
            Set intent = (Set) intents.get(ext);
            for (Iterator intit = intent.iterator(); intit.hasNext();) {
                Criterion criterion = (Criterion) intit.next();
                concept.addAttribute(getAttribute(criterion));
            }
            lattice.addConcept(concept);
        }
    }

    private Attribute getAttribute(Criterion criterion) {
        Attribute attribute = (Attribute) attributes.get(criterion);
        if(attribute == null) {
            attribute = new Attribute(criterion.getDisplayString(), null);
            attributes.put(criterion, attribute);
        }
        return attribute;
    }

    private boolean iLargerThan(Set largerSet, Set smallerSet, int i) {
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

    private Set calculateNewExtent(Set extent, int i) {
        Set newExtent = new HashSet();
        for(int j = 0; j <= i-1; j++) {
            if(extent.contains(objects[j])) {
                newExtent.add(objects[j]);
            }
        }
        newExtent.add(objects[i]);
        createClosure(newExtent);
        return newExtent;
    }

    private void createClosure(Set extent) {
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
        intents.put(extent, intent);
    }
}
