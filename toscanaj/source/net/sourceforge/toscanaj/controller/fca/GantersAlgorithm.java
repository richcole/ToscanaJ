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

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.lattice.LatticeImplementation;

import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.Context;

public class GantersAlgorithm implements
        LatticeGenerator<FCAElement, FCAElement> {
    private FCAElement[] objects;
    private FCAElement[] attributes;
    private BitSet[] relation;

    private Hashtable<BitSet, BitSet> intents;
    private Vector<BitSet> extents;

    public Lattice<FCAElement, FCAElement> createLattice(
            final Context<FCAElement, FCAElement> inputContext) {
        final LatticeImplementation<FCAElement, FCAElement> lattice = new LatticeImplementation<FCAElement, FCAElement>();
        this.objects = createElementArray(inputContext.getObjects());
        this.attributes = createElementArray(inputContext.getAttributes());
        this.relation = createRelation(inputContext.getRelation());

        this.intents = new Hashtable<BitSet, BitSet>();
        this.extents = new Vector<BitSet>();
        findExtents();
        createConcepts(lattice);
        connectConcepts(lattice);
        cleanContingents(lattice);
        return lattice;
    }

    /**
     * Maps the relation into a BitSet array, assuming the members "objects" and
     * "attributes" are set.
     */
    private BitSet[] createRelation(
            final BinaryRelation<FCAElement, FCAElement> inputRelation) {
        final BitSet[] retVal = new BitSet[this.objects.length];
        for (int i = 0; i < this.objects.length; i++) {
            final FCAElement object = this.objects[i];
            final BitSet derivation = new BitSet(this.attributes.length);
            for (int j = 0; j < this.attributes.length; j++) {
                final FCAElement attribute = this.attributes[j];
                if (inputRelation.contains(object, attribute)) {
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
    public FCAElement[] createElementArray(
            final Collection<FCAElement> collection) {
        final FCAElement[] retVal = new FCAElement[collection.size()];
        final HashSet<FCAElement> testSet = new HashSet<FCAElement>();
        final Iterator<FCAElement> it = collection.iterator();
        int pos = 0;
        while (it.hasNext()) {
            final FCAElement cur = it.next();
            if (testSet.contains(cur)) {
                throw new IllegalArgumentException(
                        "Context contains duplicate object or attribute");
            }
            retVal[pos] = cur;
            testSet.add(cur);
            pos++;
        }
        return retVal;
    }

    private void connectConcepts(
            final LatticeImplementation<FCAElement, FCAElement> lattice) {
        final Concept<FCAElement, FCAElement>[] concepts = lattice
                .getConcepts();
        for (final Concept<FCAElement, FCAElement> concept : concepts) {
            final ConceptImplementation concept1 = (ConceptImplementation) concept;
            for (final Concept<FCAElement, FCAElement> concept3 : concepts) {
                final ConceptImplementation concept2 = (ConceptImplementation) concept3;
                if (isSubConcept(concept2, concept1)) {
                    concept1.addSubConcept(concept2);
                    concept2.addSuperConcept(concept1);
                }
            }
        }
        for (final Concept<FCAElement, FCAElement> concept2 : concepts) {
            final ConceptImplementation concept = (ConceptImplementation) concept2;
            concept.buildClosures();
        }
    }

    private boolean isSubConcept(
            final Concept<FCAElement, FCAElement> concept1,
            final Concept<FCAElement, FCAElement> concept2) {
        // the extents are still stored as contingents
        if (concept1.getObjectContingentSize() > concept2
                .getObjectContingentSize()) {
            return false;
        }
        final HashSet<FCAElement> extent2 = new HashSet<FCAElement>();
        for (final Iterator<FCAElement> iterator = concept2
                .getObjectContingentIterator(); iterator.hasNext();) {
            final FCAElement obj = iterator.next();
            extent2.add(obj);
        }
        for (final Iterator<FCAElement> iterator = concept1
                .getObjectContingentIterator(); iterator.hasNext();) {
            final FCAElement obj = iterator.next();
            if (!extent2.contains(obj)) {
                return false;
            }
        }
        return true;
    }

    private void cleanContingents(
            final LatticeImplementation<FCAElement, FCAElement> lattice) {
        final Concept<FCAElement, FCAElement>[] concepts = lattice
                .getConcepts();
        for (final Concept<FCAElement, FCAElement> concept3 : concepts) {
            final ConceptImplementation<FCAElement, FCAElement> concept = (ConceptImplementation) concept3;
            final Collection<Concept<FCAElement, FCAElement>> downset = new HashSet<Concept<FCAElement, FCAElement>>(
                    concept.getDownset());
            downset.remove(concept);
            for (final Concept<FCAElement, FCAElement> concept2 : downset) {
                for (final Iterator<FCAElement> iterator2 = concept2
                        .getObjectContingentIterator(); iterator2.hasNext();) {
                    final FCAElement object = iterator2.next();
                    concept.removeObject(object);
                }
            }
            final Collection<Concept<FCAElement, FCAElement>> upset = new HashSet<Concept<FCAElement, FCAElement>>(
                    concept.getUpset());
            upset.remove(concept);
            for (final Concept<FCAElement, FCAElement> concept2 : upset) {
                for (final Iterator<FCAElement> iterator2 = concept2
                        .getAttributeContingentIterator(); iterator2.hasNext();) {
                    final FCAElement attribute = iterator2.next();
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
            for (int i = this.objects.length - 1; i >= 0; i--) {
                final BitSet newExtent = calculateNewExtent(extent, i);
                if (iLargerThan(newExtent, extent, i)) {
                    this.extents.add(newExtent);
                    extent = newExtent;
                    break;
                }
            }
        } while (extent.cardinality() != this.objects.length);
    }

    private void createConcepts(
            final LatticeImplementation<FCAElement, FCAElement> lattice) {
        for (final BitSet extent : this.extents) {
            final ConceptImplementation concept = new ConceptImplementation();
            for (int i = 0; i < this.objects.length; i++) {
                if (extent.get(i)) {
                    concept.addObject(this.objects[i]);
                }
            }
            final BitSet intent = this.intents.get(extent);
            for (int i = 0; i < this.attributes.length; i++) {
                if (intent.get(i)) {
                    concept.addAttribute(this.attributes[i]);
                }
            }
            lattice.addConcept(concept);
        }
    }

    private boolean iLargerThan(final BitSet largerSet,
            final BitSet smallerSet, final int i) {
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

    private BitSet calculateNewExtent(final BitSet extent, final int i) {
        final BitSet newExtent = new BitSet(this.objects.length);
        for (int j = 0; j <= i - 1; j++) {
            if (extent.get(j)) {
                newExtent.set(j);
            }
        }
        newExtent.set(i);
        return createClosure(newExtent);
    }

    private BitSet createClosure(final BitSet extent) {
        final BitSet intent = new BitSet();
        intent.set(0, this.attributes.length);
        for (int i = 0; i < this.objects.length; i++) {
            if (extent.get(i)) {
                final BitSet derivation = this.relation[i];
                for (int j = 0; j < this.attributes.length; j++) {
                    if (!derivation.get(j)) {
                        intent.clear(j);
                    }
                }
            }
        }
        final BitSet retVal = new BitSet();
        retVal.set(0, this.objects.length);
        for (int i = 0; i < this.attributes.length; i++) {
            if (intent.get(i)) {
                for (int j = 0; j < this.objects.length; j++) {
                    if (!this.relation[j].get(i)) {
                        retVal.clear(j);
                    }
                }
            }
        }
        this.intents.put(retVal, intent);
        return retVal;
    }
}
