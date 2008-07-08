/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.toscanaj.controller.fca.events.ConceptInterpretationContextChangedEvent;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public abstract class AbstractConceptInterpreter<Oc, A, Or> implements
        ConceptInterpreter<Oc, A, Or>, EventBrokerListener {
    private boolean showDeviation = false;
    private final Hashtable<ConceptInterpretationContext<Oc, A>, Hashtable<Concept<Oc, A>, Integer>> contingentSizes = new Hashtable<ConceptInterpretationContext<Oc, A>, Hashtable<Concept<Oc, A>, Integer>>();
    private final Hashtable<ConceptInterpretationContext<Oc, A>, Hashtable<Concept<Oc, A>, Integer>> extentSizes = new Hashtable<ConceptInterpretationContext<Oc, A>, Hashtable<Concept<Oc, A>, Integer>>();

    public static final double[] SIGNIFICANCE_LEVELS = new double[] { 0.25,
            0.20, 0.15, 0.10, 0.05, 0.025, 0.02, 0.01, 0.005, 0.0025, 0.001,
            0.0005 };

    /**
     * Chi square values come from
     * http://www.ncat.edu/~warrack/chisquaretable.pdf
     */
    public static final double[] CHI_SQUARE_CRITICAL_VALUES = new double[] {
            1.32, 1.64, 2.07, 2.71, 3.84, 5.02, 5.41, 6.63, 7.88, 9.14, 10.83,
            12.12 };

    /**
     * A good reference for the chi square stuff is:
     * 
     * Manning and Schuetze: Foundations of Natural Language Processing, 3rd
     * ed., 5.3.3
     */
    private class DeviationValuesRef {
        private final int numberOfAllObjectsInDiagram;
        private int neutralSize;
        private int outerSize;
        private final double expectedSize;

        public DeviationValuesRef(final Concept<Oc, A> concept,
                final ConceptInterpretationContext<Oc, A> context) {
            final Concept<Oc, A> nestingConcept = context.getNestingConcepts()
                    .get(0);
            if (context.getObjectDisplayMode() == ConceptInterpretationContext.EXTENT) {
                this.neutralSize = getExtentSize(concept, context
                        .getOutermostContext());
                this.outerSize = getExtentSize(nestingConcept, context
                        .getOutermostContext());
            } else {
                this.neutralSize = getObjectContingentSize(concept, context
                        .getOutermostContext());
                this.outerSize = getObjectContingentSize(nestingConcept,
                        context.getOutermostContext());
            }
            this.numberOfAllObjectsInDiagram = getExtentSize(context
                    .getOutermostTopConcept(concept), context
                    .getOutermostContext());
            this.expectedSize = this.neutralSize * this.outerSize
                    / (double) this.numberOfAllObjectsInDiagram;
        }

        public double getExpectedSize() {
            return this.expectedSize;
        }

        public int getNeutralSize() {
            return this.neutralSize;
        }

        public int getOuterSize() {
            return this.outerSize;
        }

        /**
         * This is Pearson's Chi Square test for a 2x2 matrix.
         */
        public double getChiSquare(final int actualSize) {
            return (this.numberOfAllObjectsInDiagram
                    * (this.numberOfAllObjectsInDiagram * actualSize - this.neutralSize
                            * this.outerSize) * (this.numberOfAllObjectsInDiagram
                    * actualSize - this.neutralSize * this.outerSize))
                    / (double) (this.neutralSize
                            * this.outerSize
                            * (this.numberOfAllObjectsInDiagram - this.neutralSize) * (this.numberOfAllObjectsInDiagram - this.outerSize));
        }

        /**
         * Returns the level of significance of deviation.
         * 
         * @retVal level of significance as in SIGNIFICANCE_LEVELS, -1 if none
         *         reached
         */
        public int getChiSquareLevel(final int actualSize) {
            final double chiSquare = getChiSquare(actualSize);
            int i = 0;
            while (i < CHI_SQUARE_CRITICAL_VALUES.length
                    && chiSquare > CHI_SQUARE_CRITICAL_VALUES[i]) {
                i++;
            }
            return i - 1;
        }
    }

    public abstract Iterator<Or> getObjectSetIterator(Concept<Oc, A> concept,
            ConceptInterpretationContext<Oc, A> context);

    protected abstract FCAElement getObject(String value,
            Concept<Oc, A> concept, ConceptInterpretationContext<Oc, A> context);

    protected abstract FCAElement[] handleNonDefaultQuery(Query query,
            Concept<Oc, A> concept, ConceptInterpretationContext<Oc, A> context);

    protected abstract int calculateContingentSize(Concept<Oc, A> concept,
            ConceptInterpretationContext<Oc, A> context);

    protected int calculateExtentSize(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final List<Concept<Oc, A>> outerConcepts = context.getNestingConcepts();
        if (outerConcepts.size() > 1) {
            throw new RuntimeException(
                    "multiple levels of nesting not yet supported");
        }
        if (outerConcepts.size() == 1) {
            int retVal = 0;
            final Concept<Oc, A> outerConcept = outerConcepts.get(0);
            final ConceptInterpretationContext<Oc, A> parentContext = context
                    .getNestingContexts().get(0);
            for (final Concept<Oc, A> currentOuterConcept : outerConcept
                    .getDownset()) {
                final ConceptInterpretationContext<Oc, A> currentContext = parentContext
                        .createNestedContext(currentOuterConcept);
                retVal += getLocalExtentSize(concept, currentContext);
            }
            return retVal;
        } else {
            return getLocalExtentSize(concept, context);
        }
    }

    /**
     * Calculates the extent independent of nesting.
     */
    private int getLocalExtentSize(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        int retVal = 0;
        final Iterator<Concept<Oc, A>> it = concept.getDownset().iterator();
        while (it.hasNext()) {
            final Concept<Oc, A> subconcept = it.next();
            retVal += getObjectContingentSize(subconcept, context);
        }
        return retVal;
    }

    /**
     * @todo this method and the intent one could probably be merged, including
     *       their helpers: just iterate through all concepts and join the
     *       attribute sets
     */
    public Iterator<A> getAttributeContingentIterator(
            final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final ArrayList<A> retVal = new ArrayList<A>();
        final Iterator<A> attributeContingentIterator = concept
                .getAttributeContingentIterator();
        while (attributeContingentIterator.hasNext()) {
            final A a = attributeContingentIterator.next();
            retVal.add(a);
        }
        addAttributesFromNesting(retVal, context, true);
        addAttributesFromFiltering(retVal, context, true);
        return retVal.iterator();
    }

    public Iterator<A> getIntentIterator(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final ArrayList<A> retVal = new ArrayList<A>();
        final Iterator<A> intentIterator = concept.getIntentIterator();
        while (intentIterator.hasNext()) {
            final A a = intentIterator.next();
            retVal.add(a);
        }
        addAttributesFromNesting(retVal, context, false);
        addAttributesFromFiltering(retVal, context, false);
        return retVal.iterator();
    }

    public int getObjectCount(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            return getObjectContingentSize(concept, context);
        } else {
            return getExtentSize(concept, context);
        }
    }

    /**
     * BROKEN !!!
     * 
     * @todo this won't work for nested diagrams and returns only the contingent
     * @deprecated
     */
    @Deprecated
    public int getAttributeCount(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        return concept.getAttributeContingentSize();
    }

    public int getObjectContingentSize(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final Hashtable<Concept<Oc, A>, Integer> sizes = getContingentSizesCache(context);
        final Integer cacheVal = sizes.get(concept);
        if (cacheVal != null) {
            return cacheVal.intValue();
        }
        final int count = calculateContingentSize(concept, context);
        sizes.put(concept, new Integer(count));
        return count;
    }

    public int getExtentSize(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final Hashtable<Concept<Oc, A>, Integer> sizes = getExtentSizesCache(context);
        final Integer cacheVal = sizes.get(concept);
        if (cacheVal != null) {
            return cacheVal.intValue();
        }
        final int count = calculateExtentSize(concept, context);
        sizes.put(concept, new Integer(count));
        return count;
    }

    public NormedIntervalSource<Oc, A> getIntervalSource(final IntervalType type) {
        if (type == INTERVAL_TYPE_CONTINGENT) {
            return new NormedIntervalSource<Oc, A>() {
                public double getValue(final Concept<Oc, A> concept,
                        final ConceptInterpretationContext<Oc, A> context) {
                    final int contingentSize = getObjectContingentSize(concept,
                            context);
                    if (contingentSize == 0) {
                        return 0; // avoids division by zero
                    }
                    return (double) contingentSize
                            / (double) getMaximalContingentSize();
                }
            };
        } else if (type == INTERVAL_TYPE_EXTENT) {
            return new NormedIntervalSource<Oc, A>() {
                public double getValue(final Concept<Oc, A> concept,
                        final ConceptInterpretationContext<Oc, A> context) {
                    final int extentSize = getExtentSize(concept, context);
                    if (extentSize == 0) {
                        return 0; // avoids division by zero
                    }
                    final Concept<Oc, A> compareConcept = context
                            .getOutermostTopConcept(concept);
                    final ConceptInterpretationContext<Oc, A> compareContext = context
                            .getOutermostContext();
                    return (double) extentSize
                            / (double) getExtentSize(compareConcept
                                    .getTopConcept(), compareContext);
                }
            };
        } else if (type == INTERVAL_TYPE_FIXED) {
            return new FixedValueIntervalSource<Oc, A>(1);
        } else if (type == INTERVAL_TYPE_ORTHOGONALTIY) {
            return new NormedIntervalSource<Oc, A>() {
                public double getValue(final Concept<Oc, A> concept,
                        final ConceptInterpretationContext<Oc, A> context) {
                    if (context.getNestingContexts().size() == 0) {
                        return 0.5;
                    }
                    final DeviationValuesRef deviationValues = new DeviationValuesRef(
                            concept, context);
                    final double expectedSize = deviationValues
                            .getExpectedSize();
                    final int actualSize = getObjectCount(concept, context);
                    if (actualSize == expectedSize) {
                        return 0.5;
                    }
                    if (actualSize < expectedSize) {
                        final int sigLevel = deviationValues
                                .getChiSquareLevel(actualSize);
                        return 0.5 - 0.5 * (sigLevel + 1)
                                / SIGNIFICANCE_LEVELS.length;
                    }
                    final int sigLevel = deviationValues
                            .getChiSquareLevel(actualSize);
                    return 0.5 + 0.5 * (sigLevel + 1)
                            / SIGNIFICANCE_LEVELS.length;
                }
            };
        } else {
            throw new IllegalArgumentException("Unknown interval type");
        }
    }

    public FCAElement[] executeQuery(final Query query,
            final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        if (!isRealized(concept, context) && !this.showDeviation) {
            return null;
        }
        if (query == ListQuery.KEY_LIST_QUERY) {
            final int objectCount = getObjectCount(concept, context);
            if (objectCount != 0) {
                final FCAElement[] retVal = new FCAElement[objectCount];
                final Iterator it = getObjectSetIterator(concept, context);
                int pos = 0;
                while (it.hasNext()) {
                    final FCAElement o = (FCAElement) it.next();
                    retVal[pos] = o;
                    pos++;
                }
                return retVal;
            } else {
                return null;
            }
        } else if (query == AggregateQuery.COUNT_QUERY) {
            if (this.showDeviation) {
                if (context.getNestingContexts().size() == 0) {
                    return executeObjectCountQuery(concept, context);
                }
                final DeviationValuesRef deviationValues = new DeviationValuesRef(
                        concept, context);
                final double expectedSize = deviationValues.getExpectedSize();
                final int objectCount = getObjectCount(concept, context);
                if (objectCount == expectedSize) {
                    return executeObjectCountQuery(concept, context);
                }
                final NumberFormat format = NumberFormat.getNumberInstance();
                format.setMaximumFractionDigits(1);
                final String expectedValue = "[exp: "
                        + format.format(expectedSize) + "]";
                return new FCAElement[] {
                        getObject("" + objectCount, concept, context),
                        getObject(expectedValue, concept, context) };
            } else {
                return executeObjectCountQuery(concept, context);
            }
        } else if (query == AggregateQuery.PERCENT_QUERY) {
            if (this.showDeviation) {
                if (!(context.getNestingContexts().size() == 0)) {
                    final DeviationValuesRef deviationValues = new DeviationValuesRef(
                            concept, context);
                    final double expectedSize = deviationValues
                            .getExpectedSize();
                    final int objectCount = getObjectCount(concept, context);
                    if (objectCount != expectedSize) {
                        final int fullExtent = getExtentSize(concept
                                .getTopConcept(), context);
                        final NumberFormat format = NumberFormat
                                .getPercentInstance();
                        format.setMaximumFractionDigits(2);
                        final String returnValue = format.format(objectCount
                                / (double) fullExtent);
                        final String expectedValue = "[exp: "
                                + format.format(expectedSize / fullExtent)
                                + "]";
                        return new FCAElement[] {
                                getObject(returnValue, concept, context),
                                getObject(expectedValue, concept, context) };
                    } // else fall back into normal behaviour
                } // else fall back into normal behaviour
            }
            final int objectCount = getObjectCount(concept, context);
            if (objectCount != 0) {
                final int fullExtent = getExtentSize(context
                        .getOutermostTopConcept(concept), context
                        .getOutermostContext());
                final NumberFormat format = NumberFormat.getPercentInstance();
                format.setMaximumFractionDigits(2);
                final String objectValue = format.format(objectCount
                        / (double) fullExtent);
                return new FCAElement[] { getObject(objectValue, concept,
                        context) };
            } else {
                return null;
            }
        } else {
            return handleNonDefaultQuery(query, concept, context);
        }
    }

    private FCAElement[] executeObjectCountQuery(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final int objectCount = getObjectCount(concept, context);
        if (objectCount != 0) {
            final FCAElement[] retVal = new FCAElement[1];
            retVal[0] = getObject(Integer.toString(objectCount), concept,
                    context);
            return retVal;
        } else {
            return null;
        }

    }

    /**
     * This returns the maximal contingent found up to now.
     */
    protected int getMaximalContingentSize() {
        int maxVal = 0;
        for (final Hashtable<Concept<Oc, A>, Integer> contSizes : this.contingentSizes
                .values()) {
            for (final Integer curVal : contSizes.values()) {
                if (curVal.intValue() > maxVal) {
                    maxVal = curVal.intValue();
                }
            }
        }
        return maxVal;
    }

    public boolean isRealized(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        // / @todo do check only lower neighbours
        // / @todo consider going back to creating the lattice product, this is
        // too much hacking

        // first we check the inner diagram if anything below the concept has
        // the same extent
        // size (iff any subconcept has the same extent size, the concept is not
        // realized)
        final int extentSize = getExtentSize(concept, context);
        for (final Concept<Oc, A> otherConcept : concept.getDownset()) {
            if (otherConcept == concept) {
                continue;
            }
            final int otherExtentSize = getExtentSize(otherConcept, context);
            if (otherExtentSize == extentSize) {
                return false;
            }
        }
        // the way it works for the outer diagram is a bit wild -- we assume
        // either all contingent or all
        // extents have already been calculated (which one depends on the view
        // setting), so the cache for
        // either the contingent or the extent sizes should contain all
        // interpretation contexts for the
        // lower nodes of the outer diagrams. Now we look for all whose nesting
        // concepts are subconcepts
        // of the nesting concepts we are in, then check for the extent of the
        // current concept in these
        // contexts -- they are all subconcepts of our concept along the outer
        // diagram.
        final List<Concept<Oc, A>> outerConcepts = context.getNestingConcepts();
        for (final Concept<Oc, A> outerConcept : outerConcepts) {
            for (final Concept<Oc, A> otherConcept : outerConcept.getDownset()) {
                if (otherConcept != outerConcept) {
                    for (final ConceptInterpretationContext<Oc, A> otherContext : this.contingentSizes
                            .keySet()) {
                        final List<Concept<Oc, A>> nesting = otherContext
                                .getNestingConcepts();
                        if (nesting.size() != 0) {
                            if (nesting.get(nesting.size() - 1).equals(
                                    otherConcept)) {
                                final int otherExtentSize = getExtentSize(
                                        concept, otherContext);
                                if (otherExtentSize == extentSize) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private Hashtable<Concept<Oc, A>, Integer> getContingentSizesCache(
            final ConceptInterpretationContext<Oc, A> context) {
        Hashtable<Concept<Oc, A>, Integer> retVal = this.contingentSizes
                .get(context);
        if (retVal == null) {
            retVal = new Hashtable<Concept<Oc, A>, Integer>();
            this.contingentSizes.put(context, retVal);
            // / @todo can we get around this by being smarter about the
            // hashcodes ???
            context.getEventBroker().subscribe(this,
                    ConceptInterpretationContextChangedEvent.class,
                    ConceptInterpretationContext.class);
        }
        return retVal;
    }

    public void processEvent(final Event e) {
        clearCaches((ConceptInterpretationContext<Oc, A>) e.getSubject());
    }

    private void clearCaches(final ConceptInterpretationContext<Oc, A> context) {
        this.contingentSizes.remove(context);
        this.extentSizes.remove(context);
    }

    private Hashtable<Concept<Oc, A>, Integer> getExtentSizesCache(
            final ConceptInterpretationContext<Oc, A> context) {
        Hashtable<Concept<Oc, A>, Integer> retVal = this.extentSizes
                .get(context);
        if (retVal == null) {
            retVal = new Hashtable<Concept<Oc, A>, Integer>();
            this.extentSizes.put(context, retVal);
            // / @todo can we get around this by being smarter about the
            // hashcodes ???
            context.getEventBroker().subscribe(this,
                    ConceptInterpretationContextChangedEvent.class,
                    ConceptInterpretationContext.class);
        }
        return retVal;
    }

    public void clearCache() {
        this.contingentSizes.clear();
        this.extentSizes.clear();
    }

    public void showDeviation(final boolean show) {
        this.showDeviation = show;
    }

    public boolean isVisible(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        return this.showDeviation || isRealized(concept, context);
    }

    private void addAttributesFromNesting(final List<A> currentSet,
            final ConceptInterpretationContext<Oc, A> context,
            final boolean contingentOnly) {
        final Iterator<Concept<Oc, A>> mainIt = context.getNestingConcepts()
                .iterator();
        final Set<A> attributesToAdd = new HashSet<A>();
        while (mainIt.hasNext()) {
            final Concept<Oc, A> concept = mainIt.next();
            Iterator<A> attributeIterator;
            if (contingentOnly) {
                attributeIterator = concept.getAttributeContingentIterator();
            } else {
                attributeIterator = concept.getIntentIterator();
            }
            while (attributeIterator.hasNext()) {
                attributesToAdd.add(attributeIterator.next());
            }
        }
        currentSet.addAll(attributesToAdd);
    }

    private void addAttributesFromFiltering(final List<A> currentSet,
            final ConceptInterpretationContext<Oc, A> context,
            final boolean contingentOnly) {
        final Set<A> attributesToAdd = new HashSet<A>();
        DiagramHistory.ConceptVisitor<Oc, A> visitor;
        visitor = new DiagramHistory.ConceptVisitor<Oc, A>() {
            public void visitConcept(final Concept<Oc, A> concept) {
                Iterator<A> attributeIterator;
                if (contingentOnly) {
                    attributeIterator = concept
                            .getAttributeContingentIterator();
                } else {
                    attributeIterator = concept.getIntentIterator();
                }
                while (attributeIterator.hasNext()) {
                    attributesToAdd.add(attributeIterator.next());
                }
            }
        };
        context.getDiagramHistory().visitZoomedConcepts(visitor);
        currentSet.addAll(attributesToAdd);
    }
}
