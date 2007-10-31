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

/**
 * @todo remove the objectDisplayMode out of this class, it is not really part of the interpretetation
 *       context, but an aspect of a certain view. Having it in here caused troubles with requerying
 *       since the interpreter assumed a context change whenever the view was changed and thus
 *       queried the same information again. This is by now avoided by not using the display mode
 *       in equals()/hashCode(), but this is of course only half a solution. 
 */
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
    private List nestingContexts = CollectionFactory.createDefaultList();

    /// @todo use something else than diagramHistory as first parameter -- not useful in anything but Toscana, even not
    /// in the editors
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
        retVal.nestingContexts.addAll(this.nestingContexts);
        retVal.nestingContexts.add(this);
        return retVal;
    }

    public EventBroker getEventBroker() {
        return this.eventBroker;
    }

    public void setObjectDisplayMode(boolean isContingent) {
        this.objectDisplayMode = isContingent;
        // this change is not considered to be relevant anymore, consistently with equals() and hashCode()
        // this.eventBroker.processEvent(new ConceptInterpretationContextChangedEvent(this));
    }

    public boolean getObjectDisplayMode() {
        return this.objectDisplayMode;
    }

    public void setFilterMode(boolean isContingent) {
        this.filterMode = isContingent;
        this.eventBroker.processEvent(new ConceptInterpretationContextChangedEvent(this));
    }

    public boolean getFilterMode() {
        return this.filterMode;
    }

    public DiagramHistory getDiagramHistory() {
        return this.diagramHistory;
    }

    public List getNestingConcepts() {
        return this.nestingConcepts;
    }

    public List getNestingContexts() {
        return this.nestingContexts;
    }
    
    public Concept getOutermostTopConcept(Concept concept) {
    	if(this.nestingConcepts.size() == 0) {
    		return concept.getTopConcept();
    	} else {
    		Concept outermostConcept = (Concept) this.nestingConcepts.get(0);
    		return outermostConcept.getTopConcept();
    	}
    }

    public void update(Object source) {
        this.eventBroker.processEvent(new ConceptInterpretationContextChangedEvent(this));
    }
    
    /**
     * Caution: equals(..) and hashCode() ignore the display mode.
     */
    @Override
	public boolean equals(Object other) {
    	if(other.getClass() != this.getClass()) {
    		return false;
    	}
    	ConceptInterpretationContext otherContext = (ConceptInterpretationContext) other;
		if(!otherContext.diagramHistory.equals(this.diagramHistory)) {
			return false;
		}
		if(!otherContext.nestingContexts.equals(this.nestingContexts)) {
			return false;
		}
		if(!otherContext.nestingConcepts.equals(this.nestingConcepts)) {
			return false;
		}
		if(otherContext.filterMode != this.filterMode) {
			return false;
		}
		return true;
    }
    
	/**
	 * Caution: equals(..) and hashCode() ignore the display mode.
	 */
    @Override
	public int hashCode() {
    	int result = 17;
		result = result * 37 + this.diagramHistory.hashCode();
		result = result * 37 + this.nestingContexts.hashCode();
		result = result * 37 + this.nestingConcepts.hashCode();
    	result = result * 37 + (this.filterMode ? 1 : 0);
    	return result;
    }

    public ConceptInterpretationContext getOutermostContext() {
        if(this.nestingContexts.size() == 0) {
            return this;
        } else {
            return (ConceptInterpretationContext) this.nestingContexts.get(0);
        }
    }
}
