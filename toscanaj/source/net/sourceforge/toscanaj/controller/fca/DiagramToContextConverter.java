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
	public static Context getContext(Diagram2D diagram) {
		ContextImplementation context = new ContextImplementation(diagram.getTitle());
		
		ListSet objects = context.getObjectList();
		ListSet attributes = context.getAttributeList();
		BinaryRelationImplementation relation = context.getRelationImplementation();
		
		Iterator<DiagramNode> nodesIt = diagram.getNodes();
		while (nodesIt.hasNext()) {
            DiagramNode node = nodesIt.next();
            Concept concept = node.getConcept();
            
            Iterator objCont = concept.getObjectContingentIterator();
            while (objCont.hasNext()) {
                Object obj = objCont.next();
                insertIntoList(objects, obj);
            }
            
            Iterator attrCont = concept.getAttributeContingentIterator();
            while (attrCont.hasNext()) {
                Object attrib = attrCont.next();
                insertIntoList(attributes, attrib);
                Iterator<Object> downset = concept.getDownset().iterator();
                while (downset.hasNext()) {
                    Concept subConcept = (Concept) downset.next();
                    Iterator objIt = subConcept.getObjectContingentIterator();
                    while (objIt.hasNext()) {
                        Object object = objIt.next();
                        relation.insert(object,attrib);
                    }
                }
            }
        }
		 
		return context;
	}

    private static void insertIntoList(List<Object> list, Object object) {
        if(list.contains(object)) {
            return;
        }
        int insertionPos = list.size();
        if(object instanceof Comparable) {
            // insert in order if possible
            Comparable compObj = (Comparable) object;
            while(insertionPos != 0 &&
                    list.get(insertionPos - 1) instanceof Comparable && 
                    ((Comparable<Comparable>)list.get(insertionPos - 1)).compareTo(compObj) > 0) {
                insertionPos--;
            }
        }
        list.add(insertionPos, object);
    }
}
