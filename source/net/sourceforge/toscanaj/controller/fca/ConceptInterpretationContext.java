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
import java.util.Set;

import util.CollectionFactory;

/// @todo add observer pattern or event listening
public class ConceptInterpretationContext {
    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean CONTINGENT = true;

    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean EXTENT = false;

    private boolean objectDisplayMode;
    private boolean filterMode;
    private DiagramHistory diagramHistory;

    public ConceptInterpretationContext(DiagramHistory diagramHistory, boolean objectDisplayMode, boolean filterMode) {
        this.diagramHistory = diagramHistory;
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

    public DiagramHistory getDiagramHistory() {
        return this.diagramHistory;
    }
}
