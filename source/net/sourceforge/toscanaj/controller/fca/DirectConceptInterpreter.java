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
        /// @todo implement
        return 0;
    }

    public double getRelativeExtentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        /// @todo implement
        return 0;
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
