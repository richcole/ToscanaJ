/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.Iterator;
import java.util.List;

public class WhereClauseGenerator implements DiagramHistory.ConceptVisitor {
    private static class NoClauseCreatedException extends RuntimeException {
    }

    private String clause;
    private boolean filterMode;

    private WhereClauseGenerator(String startClause, DiagramHistory filterDiagrams,
                                 List outerConcepts, boolean displayMode, boolean filterMode) {
        try {
            createClauseStart(startClause);
            addFilterPart(filterDiagrams, filterMode);
            addNestingPart(outerConcepts, displayMode);
            clause += ";";
        } catch (NoClauseCreatedException e) {
            clause = null;
        }
    }

    private void createClauseStart(String startClause) {
        if (startClause == null) {
            throw new NoClauseCreatedException();
        }
        this.clause = "WHERE " + startClause;
    }

    private void addFilterPart(DiagramHistory filterDiagrams, boolean filterMode) {
        this.filterMode = filterMode;
        filterDiagrams.visitZoomedConcepts(this);
    }

    private void addNestingPart(List outerConcepts, boolean displayMode) {
        for (Iterator iterator = outerConcepts.iterator(); iterator.hasNext();) {
            Concept concept = (Concept) iterator.next();
            if (displayMode == ConceptInterpretationContext.CONTINGENT) {
                addClausePart(getObjectClause(concept));
            } else {
                addClausePart(getExtentClause(concept));
            }
        }
    }

    static private String getObjectClause(Concept concept) {
        return createClause(concept.getObjectContingentIterator());
    }

    static private String getExtentClause(Concept concept) {
        return createClause(concept.getExtentIterator());
    }

    static private String createClause(Iterator objectIterator) {
        if (!objectIterator.hasNext()) {
            return null;
        } else {
            String retVal = "(";
            for (Iterator iterator = objectIterator; iterator.hasNext();) {
                Object o = iterator.next();
                retVal += o.toString();
                if (iterator.hasNext()) {
                    retVal += " OR ";
                }
            }
            retVal += ")";
            return retVal;
        }
    }

    private void addClausePart(String clausePart) {
        if (clausePart != null) {
            clause += " AND " + clausePart;
        } else {
            throw new NoClauseCreatedException();
        }
    }

    public void visitConcept(Concept concept) {
        if (filterMode == ConceptInterpretationContext.CONTINGENT) {
            addClausePart(getObjectClause(concept));
        } else {
            addClausePart(getExtentClause(concept));
        }
    }

    public String getClause() {
        return this.clause;
    }

    public static String createWhereClause(Concept forConcept, DiagramHistory filterDiagrams,
                                           List outerConcepts, boolean displayMode, boolean filterMode) {
        WhereClauseGenerator generator;
        if (displayMode == ConceptInterpretationContext.CONTINGENT) {
            generator = new WhereClauseGenerator(getObjectClause(forConcept), filterDiagrams, outerConcepts, displayMode, filterMode);
        } else {
            generator = new WhereClauseGenerator(getExtentClause(forConcept), filterDiagrams, outerConcepts, displayMode, filterMode);
        }
        return generator.getClause();
    }
}
