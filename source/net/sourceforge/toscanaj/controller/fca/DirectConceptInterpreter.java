/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class DirectConceptInterpreter extends AbstractConceptInterperter
										implements ConceptInterpreter {
											
    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
	        Set contingent = calculateContingent(concept, context);
			return contingent.iterator();
        } else {
			Set extent = calculateExtent(concept, context);
            return extent.iterator();
        }
    }

    protected int calculateContingentSize(Concept concept, ConceptInterpretationContext context) {
    	Set contingent = calculateContingent(concept, context);
    	return contingent.size();
    }

    private Set calculateContingent(Concept concept, ConceptInterpretationContext context) {
		HashSet retVal = new HashSet();
        Iterator objectContingentIterator = concept.getObjectContingentIterator();
        while (objectContingentIterator.hasNext()) {
            Object o = objectContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private void filterObjects(final Set currentSet, ConceptInterpretationContext context) {
        DiagramHistory.ConceptVisitor visitor;
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
                    for (Iterator iterator = currentSet.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator extentIterator = concept.getExtentIterator();
                        while (extentIterator.hasNext()) {
                            Object o2 = extentIterator.next();
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
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
                    for (Iterator iterator = currentSet.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator contingentIterator = concept.getObjectContingentIterator();
                        while (contingentIterator.hasNext()) {
                            Object o2 = contingentIterator.next();
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

    private void nestObjects(Set currentSet, ConceptInterpretationContext context, boolean contingentOnly) {
        Iterator mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept concept = (Concept) mainIt.next();
            for (Iterator iterator = currentSet.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                boolean found = false;
                Iterator objectIterator;
                if(contingentOnly) {
                	objectIterator = concept.getObjectContingentIterator();
                } else {
                	objectIterator = concept.getExtentIterator();
                }
                while (objectIterator.hasNext()) {
                    Object o2 = objectIterator.next();
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

    private Set calculateExtent(Concept concept, ConceptInterpretationContext context) {
        TreeSet retVal = new TreeSet();
        Iterator extentContingentIterator = concept.getExtentIterator();
        while (extentContingentIterator.hasNext()) {
            Object o = extentContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, false);
        filterObjects(retVal, context);
        return retVal;
    }
    
	protected Object  getObject (String value, Concept concept, ConceptInterpretationContext context) {
		return value;
	}
	
	protected Object[] handleNonDefaultQuery(Query query, Concept concept, ConceptInterpretationContext context) { 
		throw new RuntimeException("Query not supported by this class (" + this.getClass().getName() + ")");
	}    
}
