/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.toscanaj.model.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;

public class DiagramToContextConverter {
	public static Context getContext(Diagram2D diagram) {
		ContextImplementation context = new ContextImplementation(diagram.getTitle());
		
		Set objects = (Set) context.getObjects();
		Set attributes = (Set) context.getAttributes();
		BinaryRelationImplementation relation = context.getRelationImplementation();
		
		Iterator nodesIt = diagram.getNodes();
		while (nodesIt.hasNext()) {
            DiagramNode node = (DiagramNode) nodesIt.next();
            Concept concept = node.getConcept();
            
            Iterator objCont = concept.getObjectContingentIterator();
            while (objCont.hasNext()) {
                Object obj = objCont.next();
                objects.add(obj);
            }
            
            Iterator attrCont = concept.getAttributeContingentIterator();
            while (attrCont.hasNext()) {
                Object attrib = attrCont.next();
                attributes.add(attrib);
                Iterator downset = concept.getDownset().iterator();
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
}
