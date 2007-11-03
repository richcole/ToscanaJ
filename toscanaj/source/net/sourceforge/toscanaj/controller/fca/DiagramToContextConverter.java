/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.Iterator;
import java.util.List;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;
import org.tockit.util.ListSet;

import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;

public class DiagramToContextConverter {
	public static<O,A> Context<O,A> getContext(Diagram2D<O, A> diagram) {
		ContextImplementation<O,A> context = new ContextImplementation<O,A>(diagram.getTitle());
		
		ListSet<O> objects = context.getObjectList();
		ListSet<A> attributes = context.getAttributeList();
		BinaryRelationImplementation<O,A> relation = context.getRelationImplementation();
		
		Iterator<DiagramNode<O,A>> nodesIt = diagram.getNodes();
		while (nodesIt.hasNext()) {
            DiagramNode<O,A> node = nodesIt.next();
            Concept<O,A> concept = node.getConcept();
            
            Iterator<O> objCont = concept.getObjectContingentIterator();
            while (objCont.hasNext()) {
                O obj = objCont.next();
                insertIntoList(objects, obj);
            }
            
            Iterator<A> attrCont = concept.getAttributeContingentIterator();
            while (attrCont.hasNext()) {
                A attrib = attrCont.next();
                insertIntoList(attributes, attrib);
                Iterator<Concept<O,A>> downset = concept.getDownset().iterator();
                while (downset.hasNext()) {
                    Concept<O,A> subConcept = downset.next();
                    Iterator<O> objIt = subConcept.getObjectContingentIterator();
                    while (objIt.hasNext()) {
                        O object = objIt.next();
                        relation.insert(object,attrib);
                    }
                }
            }
        }
		 
		return context;
	}

	/**
	 * @todo the specific behaviour for Comparable could be in a separate class.
	 */
    @SuppressWarnings({ "cast", "unchecked" })
	private static<T> void insertIntoList(List<T> list, T object) {
        if(list.contains(object)) {
            return;
        }
        int insertionPos = list.size();
        if(object instanceof Comparable) {
            // insert in order if possible
            T compObj = (T) object;
            while(insertionPos != 0 &&
                    list.get(insertionPos - 1) instanceof Comparable && 
                    ((Comparable<T>)list.get(insertionPos - 1)).compareTo(compObj) > 0) {
                insertionPos--;
            }
        }
        list.add(insertionPos, object);
    }
}
