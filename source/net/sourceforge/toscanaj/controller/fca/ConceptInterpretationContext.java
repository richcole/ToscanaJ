/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.List;

/// @todo add observer pattern or event listening
public class ConceptInterpretationContext {
    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean CONTINGENT = true;

    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean EXTENT = false;

    private boolean objectDisplayMode;
    private boolean filterMode;
    private List filters;
    private List nestings;

    public ConceptInterpretationContext(boolean objectDisplayMode, boolean filterMode) {
        this.objectDisplayMode = objectDisplayMode;
        this.filterMode = filterMode;
    }

    public void setObjectDisplayMode(boolean isContingent) {
        this.objectDisplayMode = isContingent;
    }

    public boolean getObjectDisplayMode() {
        return objectDisplayMode;
    }

    public void setFilterMode(boolean isContingent) {
        this.filterMode = isContingent;
    }

    public boolean getFilterMode() {
        return filterMode;
    }

    public void addFilterConcept(DatabaseConnectedConcept filterConcept) {
        this.filters.add(filterConcept);
    }

    public List getFilterConcepts() {
        return this.filters;
    }

    public void addNestingConcept(DatabaseConnectedConcept nestingConcept) {
        this.nestings.add(nestingConcept);
    }

    public List getNestingConcepts() {
        return this.nestings;
    }
}
