/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

public class DirectConceptInterpreter<O, A> extends
AbstractConceptInterpreter<O, A, O> {

    @Override
    public Iterator<O> getObjectSetIterator(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            final List<O> contingent = calculateContingent(concept, context);
            return contingent.iterator();
        } else {
            final List<O> extent = calculateExtent(concept, context);
            return extent.iterator();
        }
    }

    @Override
    protected int calculateContingentSize(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final List<O> contingent = calculateContingent(concept, context);
        return contingent.size();
    }

    private List<O> calculateContingent(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final List<O> retVal = new ArrayList<O>();
        final Iterator<O> objectContingentIterator = concept
        .getObjectContingentIterator();
        while (objectContingentIterator.hasNext()) {
            final O o = objectContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private void filterObjects(final List<O> currentSet,
            final ConceptInterpretationContext<O, A> context) {
        DiagramHistory.ConceptVisitor<O, A> visitor;
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor<O, A>() {
                public void visitConcept(final Concept<O, A> concept) {
                    for (final Iterator<O> iterator = currentSet.iterator(); iterator
                    .hasNext();) {
                        final Object o = iterator.next();
                        boolean found = false;
                        final Iterator<O> extentIterator = concept
                        .getExtentIterator();
                        while (extentIterator.hasNext()) {
                            final O o2 = extentIterator.next();
                            if (o.equals(o2)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            iterator.remove();
                        }
                    }
                }
            };
        } else {
            visitor = new DiagramHistory.ConceptVisitor<O, A>() {
                public void visitConcept(final Concept<O, A> concept) {
                    for (final Iterator<O> iterator = currentSet.iterator(); iterator
                    .hasNext();) {
                        final O o = iterator.next();
                        boolean found = false;
                        final Iterator<O> contingentIterator = concept
                        .getObjectContingentIterator();
                        while (contingentIterator.hasNext()) {
                            final O o2 = contingentIterator.next();
                            if (o.equals(o2)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            iterator.remove();
                        }
                    }
                }
            };
        }
        context.getDiagramHistory().visitZoomedConcepts(visitor);
    }

    private void nestObjects(final List<O> currentSet,
            final ConceptInterpretationContext<O, A> context,
            final boolean contingentOnly) {
        final Iterator<Concept<O, A>> mainIt = context.getNestingConcepts()
        .iterator();
        while (mainIt.hasNext()) {
            final Concept<O, A> concept = mainIt.next();
            for (final Iterator<O> iterator = currentSet.iterator(); iterator
            .hasNext();) {
                final O o = iterator.next();
                boolean found = false;
                Iterator<O> objectIterator;
                if (contingentOnly) {
                    objectIterator = concept.getObjectContingentIterator();
                } else {
                    objectIterator = concept.getExtentIterator();
                }
                while (objectIterator.hasNext()) {
                    final O o2 = objectIterator.next();
                    if (o.equals(o2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    iterator.remove();
                }
            }
        }
    }

    private List<O> calculateExtent(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final List<O> retVal = new ArrayList<O>();
        final Iterator<O> extentIterator = concept.getExtentIterator();
        while (extentIterator.hasNext()) {
            final O o = extentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, false);
        filterObjects(retVal, context);
        return retVal;
    }

    @Override
    protected FCAElement getObject(final String value,
            final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        return new FCAElementImplementation(value);
    }

    @Override
    protected O[] handleNonDefaultQuery(final Query query,
            final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        throw new RuntimeException("Query not supported by this class ("
                + this.getClass().getName() + ")");
    }
}
