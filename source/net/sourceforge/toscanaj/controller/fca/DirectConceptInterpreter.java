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
import java.util.Hashtable;
import java.util.TreeSet;

///@todo this class does not allow nesting and filtering at the moment (or does it?)

public class DirectConceptInterpreter extends AbstractConceptInterperter
										implements ConceptInterpreter {
    private Hashtable contingents = new Hashtable();
    private Hashtable extents = new Hashtable();

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
			//return getContingent(concept, context).iterator();
			Hashtable contextContingents = (Hashtable) contingents.get(context);
			if (contextContingents == null) {
				contextContingents = new Hashtable();
				contingents.put(context, contextContingents);
			}
		  	Set contingent = (Set) contingents.get(concept);
			if (contingent == null) {
	            contingent = calculateContingent(concept, context);
	            contextContingents.put(concept, contingent);
			}
			return contingent.iterator();
        } else {
            //return getExtent(concept, context).iterator();
			if(context == null) {
				throw new RuntimeException("Missing context on call to getExtent(..)");
			}
			Hashtable contextExtents = (Hashtable) extents.get(context);
			if (contextExtents == null) {
				contextExtents = new Hashtable();
				extents.put(context, contextExtents);
			}
			Set extent = (Set) contextExtents.get(concept);
			if (extent == null) {
				extent = calculateExtent(concept, context);
				contextExtents.put(concept, extent);
			}
            return extent.iterator();
        }
    }

    protected int getMaximalContingentSize() {
        /// @todo implement
        return 1;
    }

    protected int calculateContingentSize (Concept concept, ConceptInterpretationContext context) {
    	Set contingent = calculateContingent(concept, context);
    	return contingent.size();
    }

    private Set calculateContingent(Concept concept, ConceptInterpretationContext context) {
		TreeSet retVal = new TreeSet();
        Iterator objectContingentIterator = concept.getObjectContingentIterator();
        while (objectContingentIterator.hasNext()) {
            Object o = objectContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private void filterObjects(final TreeSet currentSet, ConceptInterpretationContext context) {
        DiagramHistory.ConceptVisitor visitor;
        final HashSet toRemove = new HashSet();
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
                            toRemove.add(o);
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
                            toRemove.add(o);
                        }
                    }
                }
            };
        }
        context.getDiagramHistory().visitZoomedConcepts(visitor);
        for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            currentSet.remove(o);
        }
    }

    private void nestObjects(TreeSet currentSet, ConceptInterpretationContext context, boolean contingentOnly) {
        Iterator mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept concept = (Concept) mainIt.next();
            HashSet toRemove = new HashSet();
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
                    toRemove.add(o);
                }
            }
            for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
				currentSet.remove(o);
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
