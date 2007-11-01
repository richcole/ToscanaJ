/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.PickColumnsOperation;


public class TupleConceptInterpreter<O,A> extends AbstractConceptInterpreter<O,A,FCAElement>
										implements XMLizable {
	private final static String OBJECT_COLUMN_ELEMENT_NAME = "objectColumn"; 
											
	private int[] objectColumns;

    public TupleConceptInterpreter(int[] objectColumns) {
        this.objectColumns = objectColumns;    
    }	
    
	public TupleConceptInterpreter(Element elem) {
		readXML(elem);
	}
											
    @Override
	public Iterator<FCAElement> getObjectSetIterator(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
    	Set<FCAElement> baseSet;
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
	        baseSet = calculateContingent(concept, context);
        } else {
			baseSet = calculateExtent(concept, context);
        }
        return projectSet(baseSet).iterator();
    }

    private Set<FCAElement> projectSet(Set<FCAElement> baseSet) {
    	if(baseSet.size() == 0) {
    		return baseSet;
    	}
    	Relation<String> relation = RelationImplementation.fromStringSet(baseSet);
    	Set<Tuple<? extends String>> projectedRelation = PickColumnsOperation.pickColumns(relation, this.objectColumns).toSet();
        Set<FCAElement> retVal = new HashSet<FCAElement>();
        for (Iterator<Tuple<? extends String>> iter = projectedRelation.iterator(); iter.hasNext(); ) {
            Tuple<? extends String> element = iter.next();
            retVal.add(new FCAElementImplementation(element));
        }
        return retVal;
    }

    @Override
	protected int calculateContingentSize(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
    	Set<FCAElement> contingent = calculateContingent(concept, context);
    	return projectSet(contingent).size();
    }

	@Override
	protected int calculateExtentSize(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
		Set<FCAElement> extent = calculateExtent(concept, context);
		return projectSet(extent).size();
	}

	private Set<FCAElement> calculateContingent(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
		Set<FCAElement> retVal = getObjectSet(concept.getObjectContingentIterator());
		nestObjects(retVal, context, true);
		filterObjects(retVal, context);
		return retVal;
	}

	private Set<FCAElement> getObjectSet(Iterator<O> objectContingentIterator) {
		Set<FCAElement> retVal = new HashSet<FCAElement>();
		while (objectContingentIterator.hasNext()) {
			Object object = Tuple.fromString(objectContingentIterator.next().toString());
			retVal.add(new FCAElementImplementation(object));
		}
		return retVal;
	}

    private void filterObjects(final Set<FCAElement> currentSet, ConceptInterpretationContext<O,A> context) {
        DiagramHistory.ConceptVisitor<O,A> visitor;
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor<O,A>() {
                public void visitConcept(Concept<O,A> concept) {
                	currentSet.retainAll(getObjectSet(concept.getExtentIterator()));
                }
            };
        } else {
            visitor = new DiagramHistory.ConceptVisitor<O,A>() {
                public void visitConcept(Concept<O,A> concept) {
					currentSet.retainAll(getObjectSet(concept.getObjectContingentIterator()));
                }
            };
        }
        context.getDiagramHistory().visitZoomedConcepts(visitor);
    }

    private void nestObjects(Set<FCAElement> currentSet, ConceptInterpretationContext<O,A> context, boolean contingentOnly) {
        Iterator<Concept<O,A>> mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept<O,A> concept = mainIt.next();
			Iterator<O> objectIterator;
			if(contingentOnly) {
				objectIterator = concept.getObjectContingentIterator();
			} else {
				objectIterator = concept.getExtentIterator();
			}
			currentSet.retainAll(getObjectSet(objectIterator));
        }
    }

	private Set<FCAElement> calculateExtent(Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
		Set<FCAElement> retVal = getObjectSet(concept.getExtentIterator());
		nestObjects(retVal, context, false);
		filterObjects(retVal, context);
		return retVal;
	}
    
	@Override
	protected FCAElement getObject(String value, Concept<O,A> concept, ConceptInterpretationContext<O,A> context) {
		return new FCAElementImplementation(value);
	}
	
	@Override
	protected FCAElement[] handleNonDefaultQuery(Query query, Concept<O,A> concept, ConceptInterpretationContext<O,A> context) { 
		throw new RuntimeException("Query not supported by this class (" + this.getClass().getName() + ")");
	}

    public Element toXML() {
    	Element retVal = new Element(ConceptInterpreter.CONCEPT_INTERPRETER_ELEMENT_NAME);
    	retVal.setAttribute(ConceptInterpreter.CONCEPT_INTERPRETER_CLASS_ATTRIBUTE, this.getClass().getName());
    	for (int i = 0; i < this.objectColumns.length; i++) {
            int col = this.objectColumns[i];
            Element objectColElem = new Element(OBJECT_COLUMN_ELEMENT_NAME);
            objectColElem.addContent("" + col);
            retVal.addContent(objectColElem);
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
	public void readXML(Element elem) {
    	List<Element> objColElems = elem.getChildren(OBJECT_COLUMN_ELEMENT_NAME);
    	this.objectColumns = new int[objColElems.size()];
    	int i = 0;
    	for (Iterator<Element> iter = objColElems.iterator(); iter.hasNext();) {
            Element objColElem = iter.next();
            int col = Integer.parseInt(objColElem.getText());
            this.objectColumns[i] = col;
            i++;
        }
    }    
}
