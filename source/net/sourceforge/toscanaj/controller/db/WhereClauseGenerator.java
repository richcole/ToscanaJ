/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.List;
import java.util.Iterator;

public class WhereClauseGenerator implements DiagramHistory.ConceptVisitor {
    private static class NoClauseCreatedException extends RuntimeException {
    }

    private String clause;
    private boolean filterMode;

    private WhereClauseGenerator(String startClause, DiagramHistory filterDiagrams,
                                 List outerConcepts, boolean filterMode) {
        try {
            createClauseStart(startClause);
            addFilterPart(filterMode, filterDiagrams);
            addNestingPart(outerConcepts);
            clause += ";";
        } catch (NoClauseCreatedException e) {
            clause = null;
        }
    }

    private void createClauseStart(String startClause) throws NoClauseCreatedException {
        if(startClause == null) {
            throw new NoClauseCreatedException();
        }
        this.clause = "WHERE " + startClause;
    }

    private void addFilterPart(boolean filterMode, DiagramHistory filterDiagrams) {
        this.filterMode = filterMode;
        filterDiagrams.visitZoomedConcepts(this);
    }

    private void addNestingPart(List outerConcepts) {
        boolean first = true;
        for (Iterator iterator = outerConcepts.iterator(); iterator.hasNext();) {
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) iterator.next();
            if(first) { // ignore the concept we visit itself
                first = false;
                continue;
            }
            if(concept.hasObjectClause()) {
                clause += " AND " + concept.getObjectClause();
            }
            else {
                throw new NoClauseCreatedException();
            }
        }
    }

    public void visitConcept(Concept concept) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        if(filterMode == ConceptInterpretationContext.CONTINGENT) {
            if(dbConcept.hasObjectClause()) {
                clause += " AND " + dbConcept.getObjectClause();
            }
            else {
                throw new NoClauseCreatedException();
            }
        }
        else {
            String extentClause = dbConcept.getExtentClause();
            if(extentClause != null) {
                clause += " AND " + extentClause;
            }
            else {
                throw new NoClauseCreatedException();
            }
        }
    }

    public String getClause() {
        return this.clause;
    }

    public static String createWhereClause(DatabaseConnectedConcept forConcept, DiagramHistory filterDiagrams,
                                           List outerConcepts, boolean displayMode, boolean filterMode) {
        WhereClauseGenerator generator;
        if (displayMode == ConceptInterpretationContext.CONTINGENT) {
            generator = new WhereClauseGenerator(forConcept.getObjectClause(), filterDiagrams, outerConcepts, filterMode);
        } else {
            generator = new WhereClauseGenerator(forConcept.getExtentClause(), filterDiagrams, outerConcepts, filterMode);
        }
        return generator.getClause();
    }
}
