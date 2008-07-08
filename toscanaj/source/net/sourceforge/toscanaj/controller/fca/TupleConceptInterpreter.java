/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.PickColumnsOperation;

public class TupleConceptInterpreter<O, A> extends
        AbstractConceptInterpreter<O, A, FCAElement> implements XMLizable {
    private final static String OBJECT_COLUMN_ELEMENT_NAME = "objectColumn";

    private int[] objectColumns;

    public TupleConceptInterpreter(final int[] objectColumns) {
        this.objectColumns = objectColumns;
    }

    public TupleConceptInterpreter(final Element elem) {
        readXML(elem);
    }

    @Override
    public Iterator<FCAElement> getObjectSetIterator(
            final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        Set<FCAElement> baseSet;
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            baseSet = calculateContingent(concept, context);
        } else {
            baseSet = calculateExtent(concept, context);
        }
        return projectSet(baseSet).iterator();
    }

    private Set<FCAElement> projectSet(final Set<FCAElement> baseSet) {
        if (baseSet.size() == 0) {
            return baseSet;
        }
        final Relation<String> relation = RelationImplementation
                .fromStringSet(baseSet);
        final Set<Tuple<? extends String>> projectedRelation = PickColumnsOperation
                .pickColumns(relation, this.objectColumns).toSet();
        final Set<FCAElement> retVal = new HashSet<FCAElement>();
        for (final Tuple<? extends String> element : projectedRelation) {
            retVal.add(new FCAElementImplementation(element));
        }
        return retVal;
    }

    @Override
    protected int calculateContingentSize(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final Set<FCAElement> contingent = calculateContingent(concept, context);
        return projectSet(contingent).size();
    }

    @Override
    protected int calculateExtentSize(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final Set<FCAElement> extent = calculateExtent(concept, context);
        return projectSet(extent).size();
    }

    private Set<FCAElement> calculateContingent(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final Set<FCAElement> retVal = getObjectSet(concept
                .getObjectContingentIterator());
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private Set<FCAElement> getObjectSet(
            final Iterator<O> objectContingentIterator) {
        final Set<FCAElement> retVal = new HashSet<FCAElement>();
        while (objectContingentIterator.hasNext()) {
            final Object object = Tuple.fromString(objectContingentIterator
                    .next().toString());
            retVal.add(new FCAElementImplementation(object));
        }
        return retVal;
    }

    private void filterObjects(final Set<FCAElement> currentSet,
            final ConceptInterpretationContext<O, A> context) {
        DiagramHistory.ConceptVisitor<O, A> visitor;
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor<O, A>() {
                public void visitConcept(final Concept<O, A> concept) {
                    currentSet.retainAll(getObjectSet(concept
                            .getExtentIterator()));
                }
            };
        } else {
            visitor = new DiagramHistory.ConceptVisitor<O, A>() {
                public void visitConcept(final Concept<O, A> concept) {
                    currentSet.retainAll(getObjectSet(concept
                            .getObjectContingentIterator()));
                }
            };
        }
        context.getDiagramHistory().visitZoomedConcepts(visitor);
    }

    private void nestObjects(final Set<FCAElement> currentSet,
            final ConceptInterpretationContext<O, A> context,
            final boolean contingentOnly) {
        final Iterator<Concept<O, A>> mainIt = context.getNestingConcepts()
                .iterator();
        while (mainIt.hasNext()) {
            final Concept<O, A> concept = mainIt.next();
            Iterator<O> objectIterator;
            if (contingentOnly) {
                objectIterator = concept.getObjectContingentIterator();
            } else {
                objectIterator = concept.getExtentIterator();
            }
            currentSet.retainAll(getObjectSet(objectIterator));
        }
    }

    private Set<FCAElement> calculateExtent(final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        final Set<FCAElement> retVal = getObjectSet(concept.getExtentIterator());
        nestObjects(retVal, context, false);
        filterObjects(retVal, context);
        return retVal;
    }

    @Override
    protected FCAElement getObject(final String value,
            final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        return new FCAElementImplementation(value);
    }

    @Override
    protected FCAElement[] handleNonDefaultQuery(final Query query,
            final Concept<O, A> concept,
            final ConceptInterpretationContext<O, A> context) {
        throw new RuntimeException("Query not supported by this class ("
                + this.getClass().getName() + ")");
    }

    public Element toXML() {
        final Element retVal = new Element(
                ConceptInterpreter.CONCEPT_INTERPRETER_ELEMENT_NAME);
        retVal.setAttribute(
                ConceptInterpreter.CONCEPT_INTERPRETER_CLASS_ATTRIBUTE, this
                        .getClass().getName());
        for (final int col : this.objectColumns) {
            final Element objectColElem = new Element(
                    OBJECT_COLUMN_ELEMENT_NAME);
            objectColElem.addContent("" + col);
            retVal.addContent(objectColElem);
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    public void readXML(final Element elem) {
        final List<Element> objColElems = elem
                .getChildren(OBJECT_COLUMN_ELEMENT_NAME);
        this.objectColumns = new int[objColElems.size()];
        int i = 0;
        for (final Element objColElem : objColElems) {
            final int col = Integer.parseInt(objColElem.getText());
            this.objectColumns[i] = col;
            i++;
        }
    }
}
