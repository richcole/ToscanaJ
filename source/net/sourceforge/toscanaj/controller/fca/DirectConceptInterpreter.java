/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.Concept;
import java.util.Iterator;
import java.util.List;

///@todo this class does not allow nesting and filtering at the moment
public class DirectConceptInterpreter implements ConceptInterpreter {
    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        if( context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            return concept.getObjectContingentIterator();
        }
        else {
            return concept.getExtentIterator();
        }
    }

    public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
        if( context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            return concept.getObjectContingentSize();
        }
        else {
            return concept.getExtentSize();
        }
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
            if(nesting.size() != 0) {
                // go outermost
                compareConcept = (Concept) nesting.get(0);
                compareContext = new ConceptInterpretationContext(context.getDiagramHistory(), context.getEventBroker());
            }
            else {
                compareConcept = concept;
                compareContext = context;
            }
            while (!compareConcept.isTop()) {
                Concept other = compareConcept;
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
        if(extent == ConceptInterpretationContext.EXTENT) {
            return concept.getExtentSize();
        }
        else {
            return concept.getObjectContingentSize();
        }
    }

    public boolean isRealized(Concept concept, ConceptInterpretationContext context) {
        /// @todo there might be some reuse with the same method from the DB version
        int extentSize = concept.getExtentSize();
        for (Iterator iterator = concept.getDownset().iterator(); iterator.hasNext();) {
            Concept other = (Concept) iterator.next();
            if (other == concept) {
                continue;
            }
            int otherExtentSize = other.getExtentSize();
            if (otherExtentSize == extentSize) {
                return false;
            }
        }
        /// @todo check lower neighbours along the outer diagram, too
        return true;
    }
}
