/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 */
package net.sourceforge.toscanaj.controller.db;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.lattice.Concept;

public class WhereClauseGenerator<O, A> implements
        DiagramHistory.ConceptVisitor<O, A> {
    private static class NoClauseCreatedException extends RuntimeException {
        // nothing specific
    }

    private String clause;
    private boolean filterMode;

    private WhereClauseGenerator(final String startClause,
            final DiagramHistory filterDiagrams,
            final List<Concept<O, A>> outerConcepts, final boolean displayMode,
            final boolean filterMode) {
        try {
            createClauseStart(startClause);
            addFilterPart(filterDiagrams, filterMode);
            addNestingPart(outerConcepts, displayMode);
        } catch (final NoClauseCreatedException e) {
            this.clause = null;
        }
    }

    private void createClauseStart(final String startClause) {
        if (startClause == null) {
            throw new NoClauseCreatedException();
        }
        this.clause = "WHERE " + startClause;
    }

    private void addFilterPart(final DiagramHistory filterDiagrams,
            final boolean newFilterMode) {
        this.filterMode = newFilterMode;
        filterDiagrams.visitZoomedConcepts(this);
    }

    private void addNestingPart(final List<Concept<O, A>> outerConcepts,
            final boolean displayMode) {
        for (final Concept<O, A> concept : outerConcepts) {
            if (displayMode == ConceptInterpretationContext.CONTINGENT) {
                addClausePart(getObjectClause(concept));
            } else {
                addClausePart(getExtentClause(concept));
            }
        }
    }

    static private String getObjectClause(final Concept<?, ?> concept) {
        return createClause(concept.getObjectContingentIterator());
    }

    static private String getExtentClause(final Concept<?, ?> concept) {
        return createClause(concept.getExtentIterator());
    }

    static public String createClause(final Iterator<?> objectIterator) {
        if (!objectIterator.hasNext()) {
            return null;
        } else {
            String retVal = "(";
            for (final Iterator<?> iterator = objectIterator; iterator
                    .hasNext();) {
                final Object o = iterator.next();
                retVal += o.toString();
                if (iterator.hasNext()) {
                    retVal += " OR ";
                }
            }
            retVal += ")";
            return retVal;
        }
    }

    private void addClausePart(final String clausePart) {
        if (clausePart != null) {
            this.clause += " AND " + clausePart;
        } else {
            throw new NoClauseCreatedException();
        }
    }

    public void visitConcept(final Concept<O, A> concept) {
        if (this.filterMode == ConceptInterpretationContext.CONTINGENT) {
            addClausePart(getObjectClause(concept));
        } else {
            addClausePart(getExtentClause(concept));
        }
    }

    public String getClause() {
        return this.clause;
    }

    public static <O, A> String createWhereClause(
            final Concept<O, A> forConcept,
            final DiagramHistory filterDiagrams,
            final List<Concept<O, A>> outerConcepts, final boolean displayMode,
            final boolean filterMode) {
        WhereClauseGenerator<O, A> generator;
        if (displayMode == ConceptInterpretationContext.CONTINGENT) {
            generator = new WhereClauseGenerator<O, A>(
                    getObjectClause(forConcept), filterDiagrams, outerConcepts,
                    displayMode, filterMode);
        } else {
            generator = new WhereClauseGenerator<O, A>(
                    getExtentClause(forConcept), filterDiagrams, outerConcepts,
                    displayMode, filterMode);
        }
        return generator.getClause();
    }
}
