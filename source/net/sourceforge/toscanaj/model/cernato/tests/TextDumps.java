/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato.tests;

import net.sourceforge.toscanaj.model.cernato.*;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.BinaryRelation;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;

public class TextDumps {
    public static final void dump(ManyValuedContext context, PrintStream stream) {
        Collection objects = context.getObjects();
        Collection properties = context.getProperties();
        stream.print("\t");
        for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
            Property property = (Property) iterator.next();
            stream.print(property.getName() + "\t");
        }
        stream.println();
        stream.print("\t");
        for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
            Property property = (Property) iterator.next();
            stream.print(property.getType().getName() + "\t");
        }
        stream.println();
        stream.print("\t");
        for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
            Property property = (Property) iterator.next();
            Type type = property.getType();
            if(type instanceof TextualType) {
                stream.print("T\t");
            } else {
                // NumericalType numtype = (NumericalType) type;
                stream.print("N\t"); /// @todo handle digits (lost while parsing XML)
            }
        }
        stream.println();
        for (Iterator it1 = objects.iterator(); it1.hasNext();) {
            FCAObject object = (FCAObject) it1.next();
            stream.print(object.getName() + "\t");
            for (Iterator it2 = properties.iterator(); it2.hasNext();) {
                Property property = (Property) it2.next();
                stream.print(context.getRelationship(object, property).getDisplayString() + "\t");
            }
            stream.println();
        }
    }

    public static final void dump(CernatoModel model, View view, PrintStream stream) {
        Collection criteria = view.getCriteria();
        stream.println(view.getName());
        for(int i = 0; i<view.getName().length(); i++) {
            stream.print("=");
        }
        stream.println();

        Context context = new ViewContext(model, view);
        Collection objects = context.getObjects();
        Collection attributes = context.getAttributes();
        BinaryRelation relation = context.getRelation();

        stream.print("\t");
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            Criterion criterion = (Criterion) iterator.next();
            stream.print(criterion.getDisplayString() + "\t");
        }
        stream.println();
        for (Iterator it1 = objects.iterator(); it1.hasNext();) {
            FCAObject object = (FCAObject) it1.next();
            stream.print(object.getName() + "\t");
            for (Iterator it2 = attributes.iterator(); it2.hasNext();) {
                Criterion criterion = (Criterion) it2.next();
                if(relation.contains(object,criterion)) {
                    stream.print("X");
                }
                stream.print("\t");
            }
            stream.println();
        }
    }
}
