/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.controller.fca.events.ConceptInterpretationContextChangedEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.util.CollectionFactory;

import org.tockit.events.EventBroker;

import java.util.List;

public class ConceptInterpretationContext implements ChangeObserver {
    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean CONTINGENT = true;

    /** Constant value which may be used to set displayMode or filterMode */
    public static final boolean EXTENT = false;

    private boolean objectDisplayMode;
    private boolean filterMode;
    private DiagramHistory diagramHistory;

    /// @todo allow passing null brokers
    private EventBroker eventBroker;

    private List nestingConcepts = CollectionFactory.createDefaultList();

    /// @todo use something else than diagramHistory as first parameter -- not useful in anything but Toscana, even not
    /// in Anaconda
    public ConceptInterpretationContext(DiagramHistory diagramHistory, EventBroker eventBroker) {
        this.diagramHistory = diagramHistory;
        this.objectDisplayMode = CONTINGENT;
        this.filterMode = EXTENT;
        this.eventBroker = eventBroker;
        diagramHistory.addObserver(this);
    }

    public ConceptInterpretationContext createNestedContext(Concept nestingConcept) {
        ConceptInterpretationContext retVal = new ConceptInterpretationContext(this.diagramHistory, this.eventBroker);
        retVal.objectDisplayMode = this.objectDisplayMode;
        retVal.filterMode = this.filterMode;
        retVal.nestingConcepts.addAll(this.nestingConcepts);
        retVal.nestingConcepts.add(nestingConcept);
        return retVal;
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public void setObjectDisplayMode(boolean isContingent) {
        this.objectDisplayMode = isContingent;
        this.eventBroker.processEvent(new ConceptInterpretationContextChangedEvent(this));
    }

    public boolean getObjectDisplayMode() {
        return objectDisplayMode;
    }

    public void setFilterMode(boolean isContingent) {
        this.filterMode = isContingent;
        this.eventBroker.processEvent(new ConceptInterpretationContextChangedEvent(this));
    }

    public boolean getFilterMode() {
        return filterMode;
    }

    public DiagramHistory getDiagramHistory() {
        return this.diagramHistory;
    }

    public List getNestingConcepts() {
        return nestingConcepts;
    }

    public void update(Object source) {
        this.eventBroker.processEvent(new ConceptInterpretationContextChangedEvent(this));
    }
}
