/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
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


public class TupleConceptInterpreter extends AbstractConceptInterperter
										implements ConceptInterpreter, XMLizable {
	private final static String OBJECT_COLUMN_ELEMENT_NAME = "objectColumn"; 
											
	private int[] objectColumns;
	
	public TupleConceptInterpreter(Element elem) throws XMLSyntaxError {
		readXML(elem);
	}
											
    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
    	Set baseSet;
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
	        baseSet = calculateContingent(concept, context);
        } else {
			baseSet = calculateExtent(concept, context);
        }
        return projectSet(baseSet).iterator();
    }

    private Set projectSet(Set baseSet) {
    	if(baseSet.size() == 0) {
    		return baseSet;
    	}
    	Relation relation = RelationImplementation.fromSet(baseSet);
    	return PickColumnsOperation.pickColumns(relation, this.objectColumns).toSet();
    }

    protected int calculateContingentSize(Concept concept, ConceptInterpretationContext context) {
    	Set contingent = calculateContingent(concept, context);
    	return projectSet(contingent).size();
    }

	protected int calculateExtentSize(Concept concept, ConceptInterpretationContext context) {
		Set extent = calculateExtent(concept, context);
		return projectSet(extent).size();
	}

	private Set calculateContingent(Concept concept, ConceptInterpretationContext context) {
		Set retVal = getObjectSet(concept.getObjectContingentIterator());
		nestObjects(retVal, context, true);
		filterObjects(retVal, context);
		return retVal;
	}

	private Set getObjectSet(Iterator objectContingentIterator) {
		Set retVal = new HashSet();
		while (objectContingentIterator.hasNext()) {
			Object object = Tuple.fromString(objectContingentIterator.next().toString());
			retVal.add(object);
		}
		return retVal;
	}

    private void filterObjects(final Set currentSet, ConceptInterpretationContext context) {
        DiagramHistory.ConceptVisitor visitor;
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
                	currentSet.retainAll(getObjectSet(concept.getExtentIterator()));
                }
            };
        } else {
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
					currentSet.retainAll(getObjectSet(concept.getObjectContingentIterator()));
                }
            };
        }
        context.getDiagramHistory().visitZoomedConcepts(visitor);
    }

    private void nestObjects(Set currentSet, ConceptInterpretationContext context, boolean contingentOnly) {
        Iterator mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept concept = (Concept) mainIt.next();
			Iterator objectIterator;
			if(contingentOnly) {
				objectIterator = concept.getObjectContingentIterator();
			} else {
				objectIterator = concept.getExtentIterator();
			}
			currentSet.retainAll(getObjectSet(objectIterator));
        }
    }

	private Set calculateExtent(Concept concept, ConceptInterpretationContext context) {
		Set retVal = getObjectSet(concept.getExtentIterator());
		nestObjects(retVal, context, false);
		filterObjects(retVal, context);
		return retVal;
	}
    
	protected Object getObject(String value, Concept concept, ConceptInterpretationContext context) {
		return value;
	}
	
	protected Object[] handleNonDefaultQuery(Query query, Concept concept, ConceptInterpretationContext context) { 
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

    public void readXML(Element elem) throws XMLSyntaxError {
    	List objColElems = elem.getChildren(OBJECT_COLUMN_ELEMENT_NAME);
    	this.objectColumns = new int[objColElems.size()];
    	int i = 0;
    	for (Iterator iter = objColElems.iterator(); iter.hasNext();) {
            Element objColElem = (Element) iter.next();
            int col = Integer.parseInt(objColElem.getText());
            this.objectColumns[i] = col;
            i++;
        }
    }    
}
