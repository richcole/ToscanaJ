package net.sourceforge.toscanaj.controller.fca;
import java.lang.String;
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
    private Diagram2D diagram;

    /**
     * The concept used as filter (null for current and future diagrams).
     */
    private Concept filterConcept;

    /**
     * Initialises a new reference.
     */
    public DiagramReference(Diagram2D diagram, Concept filterConcept) {
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
     * Returns the concept used as filter (null for current and future diagrams).
     */
    public Concept getFilterConcept() {
        return this.filterConcept;
    }

    public void setZoomedConcept(Concept filterConcept) {
        this.filterConcept = filterConcept;
    }

    /**
     * Returns the diagram title for usage in a view.
     */
    public String toString() {
        return diagram.getTitle();
    }
}