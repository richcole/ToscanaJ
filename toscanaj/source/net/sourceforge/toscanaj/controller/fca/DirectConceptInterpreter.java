/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DirectConceptInterpreter<O,A> extends AbstractConceptInterpreter<O,A,O> {
											
    @Override
	public Iterator<O> getObjectSetIterator(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
	        List<O> contingent = calculateContingent(concept, context);
			return contingent.iterator();
        } else {
			List<O> extent = calculateExtent(concept, context);
            return extent.iterator();
        }
    }

    @Override
	protected int calculateContingentSize(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
    	List<O> contingent = calculateContingent(concept, context);
    	return contingent.size();
    }

    private List<O> calculateContingent(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
        List<O> retVal = new ArrayList<O>();
        Iterator<O> objectContingentIterator = concept.getObjectContingentIterator();
        while (objectContingentIterator.hasNext()) {
            O o = objectContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private void filterObjects(final List<O> currentSet, ConceptInterpretationContext<O,A> context) {
        DiagramHistory.ConceptVisitor<O,A> visitor;
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor<O,A>() {
                public void visitConcept(Concept<O,A> concept) {
                    for (Iterator<O> iterator = currentSet.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator<O> extentIterator = concept.getExtentIterator();
                        while (extentIterator.hasNext()) {
                            O o2 = extentIterator.next();
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
            visitor = new DiagramHistory.ConceptVisitor<O,A>() {
                public void visitConcept(Concept<O,A> concept) {
                    for (Iterator<O> iterator = currentSet.iterator(); iterator.hasNext();) {
                        O o = iterator.next();
                        boolean found = false;
                        Iterator<O> contingentIterator = concept.getObjectContingentIterator();
                        while (contingentIterator.hasNext()) {
                            O o2 = contingentIterator.next();
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

    private void nestObjects(List<O> currentSet, ConceptInterpretationContext<O,A> context, boolean contingentOnly) {
        Iterator<Concept<O,A>> mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept<O,A> concept = mainIt.next();
            for (Iterator<O> iterator = currentSet.iterator(); iterator.hasNext();) {
                O o = iterator.next();
                boolean found = false;
                Iterator<O> objectIterator;
                if(contingentOnly) {
                	objectIterator = concept.getObjectContingentIterator();
                } else {
                	objectIterator = concept.getExtentIterator();
                }
                while (objectIterator.hasNext()) {
                    O o2 = objectIterator.next();
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

    private List<O> calculateExtent(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
        List<O> retVal = new ArrayList<O>();
        Iterator<O> extentIterator = concept.getExtentIterator();
        while (extentIterator.hasNext()) {
            O o = extentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, false);
        filterObjects(retVal, context);
        return retVal;
    }
    
	@Override
	protected FCAElement getObject(String value, Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
		return new FCAElementImplementation(value);
	}
	
	@Override
	protected FCAElement[] handleNonDefaultQuery(Query query, Concept<O,A> concept, ConceptInterpretationContext<O,A> context) { 
		throw new RuntimeException("Query not supported by this class (" + this.getClass().getName() + ")");
	}    
}
