package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;

/**
 * Used to store references to diagrams, including the concept used for
 * filtering in past diagrams.
 * 
 * Beneath adding the concept reference (which is null for current and future
 * diagrams) this gives the references identity, otherwise we would get problems
 * in adding a diagram twice to the history.
 */
public class DiagramReference {
    /**
     * The diagram we refer to.
     */
    private final Diagram2D diagram;

    /**
     * The concept used as filter (null for current and future diagrams).
     */
    private Concept filterConcept;

    /**
     * Initialises a new reference.
     */
    public DiagramReference(final Diagram2D diagram, final Concept filterConcept) {
        this.diagram = diagram;
        this.filterConcept = filterConcept;
    }

    /**
     * Returns the diagram we refer to.
     */
    public Diagram2D getDiagram() {
        return this.diagram;
    }

    /**
     * Returns the concept used as filter (null for current and future
     * diagrams).
     */
    public Concept getFilterConcept() {
        return this.filterConcept;
    }

    public void setFilterConcept(final Concept filterConcept) {
        this.filterConcept = filterConcept;
    }

    /**
     * Returns the diagram title for usage in a view.
     */
    @Override
    public String toString() {
        return this.diagram.getTitle();
    }

    @Override
    public boolean equals(final Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        }
        final DiagramReference otherReference = (DiagramReference) other;
        if (otherReference.diagram != this.diagram) {
            return false;
        }
        if (otherReference.filterConcept != this.filterConcept) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.diagram.hashCode() + 47 * this.filterConcept.hashCode();
    }
}