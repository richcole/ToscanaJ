/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato.tests;

import net.sourceforge.toscanaj.model.BinaryRelation;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.cernato.CernatoObject;
import net.sourceforge.toscanaj.model.cernato.CernatoTable;
import net.sourceforge.toscanaj.model.cernato.Property;
import net.sourceforge.toscanaj.model.cernato.View;
import net.sourceforge.toscanaj.model.cernato.ViewContext;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.Criterion;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;

public class TextDumps {
    public static final void dump(CernatoTable context, PrintStream stream) {
        Collection objects = context.getObjects();
        Collection properties = context.getAttributes();
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
            AttributeType type = property.getType();
            if (type instanceof TextualType) {
                stream.print("T\t");
            } else {
                // NumericalType numtype = (NumericalType) type;
                stream.print("N\t"); /// @todo handle digits (lost while parsing XML)
            }
        }
        stream.println();
        for (Iterator it1 = objects.iterator(); it1.hasNext();) {
            CernatoObject object = (CernatoObject) it1.next();
            stream.print(object.getName() + "\t");
            for (Iterator it2 = properties.iterator(); it2.hasNext();) {
                Property property = (Property) it2.next();
                stream.print(context.getRelationship(object, property).getDisplayString() + "\t");
            }
            stream.println();
        }
    }

    public static final void dump(CernatoModel model, View view, PrintStream stream) {
        stream.println(view.getName());
        for (int i = 0; i < view.getName().length(); i++) {
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
            CernatoObject object = (CernatoObject) it1.next();
            stream.print(object.getName() + "\t");
            for (Iterator it2 = attributes.iterator(); it2.hasNext();) {
                Criterion criterion = (Criterion) it2.next();
                if (relation.contains(object, criterion)) {
                    stream.print("X");
                }
                stream.print("\t");
            }
            stream.println();
        }
    }

    public static final void dump(Lattice lattice, PrintStream stream) {
        Concept[] concepts = lattice.getConcepts();
        for (int i = 0; i < concepts.length; i++) {
            Concept concept = concepts[i];
            stream.print(concept + "[objectContingent: {");
            Iterator extIt = concept.getObjectContingentIterator();
            while (extIt.hasNext()) {
                CernatoObject obj = (CernatoObject) extIt.next();
                stream.print(obj.getName());
                if (extIt.hasNext()) {
                    stream.print(", ");
                }
            }
            stream.print("}, attributeContingent: {");
            Iterator intIt = concept.getAttributeContingentIterator();
            while (intIt.hasNext()) {
                Attribute attribute = (Attribute) intIt.next();
                stream.print(attribute.toString());
                if (intIt.hasNext()) {
                    stream.print(", ");
                }
            }
            stream.print("}, subconcepts: {");
            Iterator subIt = concept.getDownset().iterator();
            while (subIt.hasNext()) {
                Object subConcept = subIt.next();
                stream.print(subConcept);
                if (subIt.hasNext()) {
                    stream.print(", ");
                }
            }
            stream.println("}]");
        }
    }

    /**
     * Dumps context in Burmeister format.
     */
    public static final void dump(Context context, PrintStream stream) {
        stream.println("B");
        String name = "unnamed context";
        if (context instanceof ContextImplementation) {
            ContextImplementation conImp = (ContextImplementation) context;
            if(conImp.getName() != null) {
            	name = conImp.getName();
            }
        }
        stream.println(name);

        Collection objects = context.getObjects();
        Collection attributes = context.getAttributes();
        stream.println(objects.size());
        stream.println(attributes.size());
        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            stream.println(o.toString());
        }
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            Object a = iterator.next();
            stream.println(a.toString());
        }

        BinaryRelation relation = context.getRelation();
        for (Iterator it1 = objects.iterator(); it1.hasNext();) {
            Object o = it1.next();
            for (Iterator it2 = attributes.iterator(); it2.hasNext();) {
                Object a = it2.next();
                if (relation.contains(o, a)) {
                    stream.print("x");
                } else {
                    stream.print(".");
                }
            }
            stream.println();
        }
    }
}
