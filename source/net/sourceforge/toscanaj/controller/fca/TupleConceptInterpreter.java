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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.jdom.Element;


public class TupleConceptInterpreter extends AbstractConceptInterperter
										implements ConceptInterpreter, XMLizable {
	private final static String OBJECT_COLUMN_ELEMENT_NAME = "objectColumn"; 
											
	private int[] objectColumns;
	
	public TupleConceptInterpreter(Element elem) throws XMLSyntaxError {
		readXML(elem);
	}
											
    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
	        Set contingent = calculateContingent(concept, context);
			return contingent.iterator();
        } else {
			Set extent = calculateExtent(concept, context);
            return extent.iterator();
        }
    }

    protected int calculateContingentSize(Concept concept, ConceptInterpretationContext context) {
    	Set contingent = calculateContingent(concept, context);
    	return contingent.size();
    }

	protected int calculateExtentSize(Concept concept, ConceptInterpretationContext context) {
		Set extent = calculateExtent(concept, context);
		return extent.size();
	}

    private Set calculateContingent(Concept concept, ConceptInterpretationContext context) {
        TreeSet retVal = getObjectSet(concept.getObjectContingentIterator());
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private TreeSet getObjectSet(Iterator objectContingentIterator) {
        TreeSet retVal = new TreeSet();
        while (objectContingentIterator.hasNext()) {
            String object = getObject(objectContingentIterator.next().toString());
            retVal.add(object);
        }
        return retVal;
    }

    private String getObject(String serialForm) {
        StringTokenizer tokenizer = new StringTokenizer(serialForm, " ");
        String[] tokens = new String[tokenizer.countTokens()];
        int pos = 0;
        while(tokenizer.hasMoreTokens()) {
        	tokens[pos] = tokenizer.nextToken();
        	pos++;
        }
        String object = "";
        for (int i = 0; i < this.objectColumns.length; i++) {
        	if(i != 0) {
        		object += " ";
        	}
            object += tokens[this.objectColumns[i]];
        }
        return object;
    }

    private void filterObjects(final TreeSet currentSet, ConceptInterpretationContext context) {
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

    private void nestObjects(TreeSet currentSet, ConceptInterpretationContext context, boolean contingentOnly) {
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
        TreeSet retVal = getObjectSet(concept.getExtentIterator());
        nestObjects(retVal, context, false);
        filterObjects(retVal, context);
        return retVal;
    }
    
	protected Object getObject(String value, Concept concept, ConceptInterpretationContext context) {
		return getObject(value);
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
