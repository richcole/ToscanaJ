/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db;

import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.List;
import java.util.Iterator;

public class WhereClauseGenerator implements DiagramHistory.ConceptVisitor {
    public String createWhereClause(DatabaseConnectedConcept forConcept, DiagramHistory filterDiagrams,
                                    List outerConcepts, boolean contingentOnly) {
        boolean first = true;
        String whereClause = "WHERE ";
        if (contingentOnly) {
            if (forConcept.hasObjectClause()) {
                whereClause += forConcept.getObjectClause();
                first = false;
            }
        } else {
            // aggregate all clauses from the downset
            Iterator iter = forConcept.getDownset().iterator();
            while (iter.hasNext()) {
                DatabaseConnectedConcept otherConcept = (DatabaseConnectedConcept) iter.next();
                if (!otherConcept.hasObjectClause()) {
                    continue;
                }
                if (first) {
                    first = false;
                    whereClause += " (";
                } else {
                    whereClause += " OR ";
                }
                whereClause += otherConcept.getObjectClause();
            }
            if (!first) {
                whereClause += ") ";
            }
        }

        Iterator iter = forConcept.getFilterClauses().iterator();
        while (iter.hasNext()) {
            Object item = iter.next();
            if (first) {
                first = false;
            } else {
                whereClause += " AND ";
            }
            whereClause += item;
        }
        if (first) {
            return null; // no clause at all
        }
        whereClause += ";";
        return whereClause;
    }

    public void visitConcept(Concept concept) {
    }
}
