/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.*;

///@todo this class does not allow nesting and filtering at the moment

public class DirectConceptInterpreter implements ConceptInterpreter {
    private Hashtable contingents = new Hashtable();
    private Hashtable extents = new Hashtable();

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            return getContingent(concept, context).iterator();
        } else {
            return getExtent(concept, context).iterator();
        }
    }

    public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
        return getCount(concept, context, context.getObjectDisplayMode());
    }

    public int getAttributeCount(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentSize();
    }

    public double getRelativeObjectContingentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        int contingentSize = getCount(concept, context, ConceptInterpretationContext.CONTINGENT);
        if (reference == REFERENCE_DIAGRAM) {
            if (contingentSize == 0) {
                return 0; //avoids division by zero
            }
            return (double) contingentSize / (double) getMaximalContingentSize();
        } else {
            /// @todo implement or remove the distinction
            return 1;
        }
    }

    private int getMaximalContingentSize() {
        /// @todo implement
        return 1;
    }

    public double getRelativeExtentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
        if (extentSize == 0) {
            return 0; //avoids division by zero
        }
        if (reference == REFERENCE_DIAGRAM) {
            /// @todo add way to find top compareConcept more easily
            Concept compareConcept;
            ConceptInterpretationContext compareContext;
            List nesting = context.getNestingConcepts();
            if (nesting.size() != 0) {
                // go outermost
                compareConcept = (Concept) nesting.get(0);
                compareContext = new ConceptInterpretationContext(context.getDiagramHistory(), context.getEventBroker());
            } else {
                compareConcept = concept;
                compareContext = context;
            }
            while (!compareConcept.isTop()) {
                Concept other;
                Iterator it = compareConcept.getUpset().iterator();
                do {
                    other = (Concept) it.next();
                } while (other == compareConcept);
                compareConcept = other;
            }
            int maxExtent = getCount(compareConcept, compareContext, ConceptInterpretationContext.EXTENT);
            return (double) extentSize / (double) maxExtent;
        } else {
            /// @todo implement or remove the distinction
            return 1;
        }
    }

    private int getCount(Concept concept, ConceptInterpretationContext context, boolean extent) {
        if (extent == ConceptInterpretationContext.CONTINGENT) {
            return getContingent(concept, context).size();
        } else {
            return getExtent(concept, context).size();
        }
    }

    public boolean isRealized(Concept concept, ConceptInterpretationContext context) {
        /// @todo there might be some reuse with the same method from the DB version
        int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
        for (Iterator iterator = concept.getDownset().iterator(); iterator.hasNext();) {
            Concept other = (Concept) iterator.next();
            if (other == concept) {
                continue;
            }
            int otherExtentSize = getCount(other, context, ConceptInterpretationContext.EXTENT);
            if (otherExtentSize == extentSize) {
                return false;
            }
        }
        List outerConcepts = context.getNestingConcepts();
        for (Iterator iterator = outerConcepts.iterator(); iterator.hasNext();) {
            Concept outerConcept = (Concept) iterator.next();
            for (Iterator iterator2 = outerConcept.getDownset().iterator(); iterator2.hasNext();) {
                Concept otherConcept = (Concept) iterator2.next();
                if (otherConcept != outerConcept) {
                    for (Iterator iterator3 = this.contingents.keySet().iterator(); iterator3.hasNext();) {
                        ConceptInterpretationContext otherContext = (ConceptInterpretationContext) iterator3.next();
                        List nesting = otherContext.getNestingConcepts();
                        if (nesting.size() != 0) {
                            if (nesting.get(nesting.size() - 1).equals(otherConcept)) {
                                int otherExtentSize = getCount(concept, otherContext, ConceptInterpretationContext.EXTENT);
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

    private Collection getContingent(Concept concept, ConceptInterpretationContext context) {
        Hashtable contextContingents = (Hashtable) contingents.get(context);
        if (contextContingents == null) {
            contextContingents = new Hashtable();
            contingents.put(context, contextContingents);
        }
        Vector contingent = (Vector) contingents.get(concept);
        if (contingent == null) {
            contingent = calculateContingent(concept, context);
            contextContingents.put(concept, contingent);
        }
        return contingent;
    }

    private Vector calculateContingent(Concept concept, ConceptInterpretationContext context) {
        Vector retVal = new Vector();
        Iterator objectContingentIterator = concept.getObjectContingentIterator();
        while (objectContingentIterator.hasNext()) {
            Object o = objectContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context);
        filterObjects(retVal, context);
        return retVal;
    }

    private void filterObjects(final Vector retVal, ConceptInterpretationContext context) {
        DiagramHistory.ConceptVisitor visitor;
        final Vector toRemove = new Vector();
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
                    for (Iterator iterator = retVal.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator extentIterator = concept.getExtentIterator();
                        while (extentIterator.hasNext()) {
                            Object o2 = extentIterator.next();
                            if (o == o2) {
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
                    for (Iterator iterator = retVal.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator contingentIterator = concept.getObjectContingentIterator();
                        while (contingentIterator.hasNext()) {
                            Object o2 = contingentIterator.next();
                            if (o == o2) {
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
            retVal.remove(o);
        }
    }

    private void nestObjects(Vector retVal, ConceptInterpretationContext context) {
        Iterator mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept concept = (Concept) mainIt.next();
            Vector toRemove = new Vector();
            for (Iterator iterator = retVal.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                boolean found = false;
                Iterator contingentIterator = concept.getObjectContingentIterator();
                while (contingentIterator.hasNext()) {
                    Object o2 = contingentIterator.next();
                    if (o == o2) {
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
                retVal.remove(o);
            }
        }
    }

    private Collection getExtent(Concept concept, ConceptInterpretationContext context) {
        Hashtable contextExtents = (Hashtable) extents.get(context);
        if (contextExtents == null) {
            contextExtents = new Hashtable();
            extents.put(context, contextExtents);
        }
        Vector extent = (Vector) contextExtents.get(concept);
        if (extent == null) {
            extent = calculateExtent(concept, context);
            contextExtents.put(concept, extent);
        }
        return extent;
    }

    private Vector calculateExtent(Concept concept, ConceptInterpretationContext context) {
        Vector retVal = new Vector();
        Iterator extentContingentIterator = concept.getExtentIterator();
        while (extentContingentIterator.hasNext()) {
            Object o = extentContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context);
        filterObjects(retVal, context);
        return retVal;
    }
}
