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
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

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
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) iterator.next();
            if (displayMode == ConceptInterpretationContext.CONTINGENT) {
                addClausePart(concept.getObjectClause());
            } else {
                addClausePart(concept.getExtentClause());
            }
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
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        if (filterMode == ConceptInterpretationContext.CONTINGENT) {
            addClausePart(dbConcept.getObjectClause());
        } else {
            addClausePart(dbConcept.getExtentClause());
        }
    }

    public String getClause() {
        return this.clause;
    }

    public static String createWhereClause(DatabaseConnectedConcept forConcept, DiagramHistory filterDiagrams,
                                           List outerConcepts, boolean displayMode, boolean filterMode) {
        WhereClauseGenerator generator;
        if (displayMode == ConceptInterpretationContext.CONTINGENT) {
            generator = new WhereClauseGenerator(forConcept.getObjectClause(), filterDiagrams, outerConcepts, displayMode, filterMode);
        } else {
            generator = new WhereClauseGenerator(forConcept.getExtentClause(), filterDiagrams, outerConcepts, displayMode, filterMode);
        }
        return generator.getClause();
    }
}
