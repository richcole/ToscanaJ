/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.Context;

import net.sourceforge.toscanaj.model.context.*;
import net.sourceforge.toscanaj.model.lattice.*;

public class GantersAlgorithm implements LatticeGenerator {
    private FCAElement[] objects;
	private FCAElement[] attributes;
    private BitSet[] relation;
    
    private Hashtable intents;
    private Vector extents;

    public Lattice createLattice(Context inputContext) {
        LatticeImplementation lattice = new LatticeImplementation();
        this.objects = createElementArray(inputContext.getObjects());
        this.attributes = createElementArray(inputContext.getAttributes());
        this.relation = createRelation(inputContext.getRelation());
        
        this.intents = new Hashtable();
        this.extents = new Vector();
        findExtents();
        createConcepts(lattice);
        connectConcepts(lattice);
        cleanContingents(lattice);
        return lattice;
    }

    /**
     * Maps the relation into a BitSet array, assuming the members "objects"
     * and "attributes" are set.
     */
	private BitSet[] createRelation(BinaryRelation relation) {
        BitSet[] retVal = new BitSet[this.objects.length];
        for (int i = 0; i < this.objects.length; i++) {
			FCAElement object = this.objects[i];
			BitSet derivation = new BitSet(this.attributes.length);
            for (int j = 0; j < this.attributes.length; j++) {
				FCAElement attribute = this.attributes[j];
				if(relation.contains(object, attribute)) {
					derivation.set(j);
                }
			}
            retVal[i] = derivation;
		}
		return retVal;
	}

	/**
	 * This is similar to Collection.toArray(), but also checks for duplicates.
	 */
    public FCAElement[] createElementArray(Collection collection) {
        FCAElement[] retVal = new FCAElement[collection.size()];
    	HashSet testSet = new HashSet();
    	Iterator it = collection.iterator();
    	int pos = 0;
    	while (it.hasNext()) {
            FCAElement cur = (FCAElement) it.next();
            if(testSet.contains(cur)) {
            	throw new IllegalArgumentException("Context contains duplicate object or attribute");
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
        HashSet extent2 = new HashSet();
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
        BitSet extent = new BitSet(this.objects.length);
        extent = createClosure(extent);
        this.extents.add(extent);
        do {
            for (int i = objects.length - 1; i >= 0; i--) {
                BitSet newExtent = calculateNewExtent(extent, i);
                if (iLargerThan(newExtent, extent, i)) {
                    this.extents.add(newExtent);
                    extent = newExtent;
                    break;
                }
            }
        } while (extent.cardinality() != objects.length);
    }

    private void createConcepts(LatticeImplementation lattice) {
        for (Iterator iterator = extents.iterator(); iterator.hasNext();) {
            ConceptImplementation concept = new ConceptImplementation();
            BitSet extent = (BitSet) iterator.next();
            for (int i = 0; i < this.objects.length; i++) {
                if(extent.get(i)) {
                	concept.addObject(this.objects[i]);
                }
			}
            BitSet intent = (BitSet) this.intents.get(extent);
            for (int i = 0; i < this.attributes.length; i++) {
                if(intent.get(i)) {
                	concept.addAttribute(this.attributes[i]);
                }
            }
            lattice.addConcept(concept);
        }
    }

    private boolean iLargerThan(BitSet largerSet, BitSet smallerSet, int i) {
        for (int j = 0; j <= i - 1; j++) {
            if (largerSet.get(j) != smallerSet.get(j)) {
                return false;
            }
        }
        if (smallerSet.get(i)) {
            return false;
        }
        if (!largerSet.get(i)) {
            return false;
        }
        return true;
    }

    private BitSet calculateNewExtent(BitSet extent, int i) {
        BitSet newExtent = new BitSet(this.objects.length);
        for (int j = 0; j <= i - 1; j++) {
            if (extent.get(j)) {
                newExtent.set(j);
            }
        }
        newExtent.set(i);
        return createClosure(newExtent);
    }

    private BitSet createClosure(BitSet extent) {
        BitSet intent = new BitSet();
        intent.set(0, this.attributes.length);
        for (int i = 0; i < this.objects.length; i++) {
            if(extent.get(i)) {
                BitSet derivation = this.relation[i];
    			for (int j = 0; j < this.attributes.length; j++) {
    				if(!derivation.get(j)) {
    					intent.clear(j);
                    }
    			}
            }
		}
        BitSet retVal = new BitSet();
        retVal.set(0, this.objects.length);
        for (int i = 0; i < this.attributes.length; i++) {
            if(intent.get(i)) {
        	    for (int j = 0; j < this.objects.length; j++) {
    				if(!this.relation[j].get(i)) {
    					retVal.clear(j);
                    }
    			}
            }
		}
        intents.put(retVal, intent);
        return retVal;
    }
}
