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

import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;
import org.tockit.util.ListSet;

public class DiagramToContextConverter {
    public static Context<FCAElementImplementation, FCAElementImplementation>
                        getContext(final Diagram2D<FCAElementImplementation, FCAElementImplementation> diagram) {
        final ContextImplementation context = new ContextImplementation(diagram.getTitle());

        final ListSet<FCAElementImplementation> objects = context.getObjectList();
        final ListSet<FCAElementImplementation> attributes = context.getAttributeList();
        final BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation> relation = context
                .getRelationImplementation();

        final Iterator<DiagramNode<FCAElementImplementation, FCAElementImplementation>> nodesIt = diagram.getNodes();
        while (nodesIt.hasNext()) {
            final DiagramNode<FCAElementImplementation, FCAElementImplementation> node = nodesIt.next();
            final Concept<FCAElementImplementation, FCAElementImplementation> concept = node.getConcept();

            final Iterator<FCAElementImplementation> objCont = concept.getObjectContingentIterator();
            while (objCont.hasNext()) {
                final FCAElementImplementation obj = objCont.next();
                insertIntoList(objects, obj);
            }

            final Iterator<FCAElementImplementation> attrCont = concept
                    .getAttributeContingentIterator();
            while (attrCont.hasNext()) {
                final FCAElementImplementation attrib = attrCont.next();
                insertIntoList(attributes, attrib);
                for (Concept<FCAElementImplementation, FCAElementImplementation> subConcept : concept.getDownset()) {
                    final Iterator<FCAElementImplementation> objIt = subConcept
                            .getObjectContingentIterator();
                    while (objIt.hasNext()) {
                        final FCAElementImplementation object = objIt.next();
                        relation.insert(object, attrib);
                    }
                }
            }
        }

        return context;
    }

    /**
     * @todo the specific behaviour for Comparable could be in FCAElementImplementation separate class.
     */
    @SuppressWarnings( { "cast", "unchecked" })
    private static <T> void insertIntoList(final List<T> list, final T object) {
        if (list.contains(object)) {
            return;
        }
        int insertionPos = list.size();
        if (object instanceof Comparable) {
            // insert in order if possible
            final T compObj = (T) object;
            while (insertionPos != 0
                    && list.get(insertionPos - 1) instanceof Comparable
                    && ((Comparable<T>) list.get(insertionPos - 1))
                            .compareTo(compObj) > 0) {
                insertionPos--;
            }
        }
        list.add(insertionPos, object);
    }
}
