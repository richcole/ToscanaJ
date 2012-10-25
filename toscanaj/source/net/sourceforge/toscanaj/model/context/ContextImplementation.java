/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.context;

import java.util.Iterator;
import java.util.Set;

import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;
import org.tockit.context.model.ListsContext;
import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;

/**
 * @todo hide access to collections and relation by playing man in the middle.
 */
public class ContextImplementation implements ListsContext<FCAElementImplementation, FCAElementImplementation> {
    private final ListSet<FCAElementImplementation> objects = new ListSetImplementation<FCAElementImplementation>();
    private final ListSet<FCAElementImplementation> attributes = new ListSetImplementation<FCAElementImplementation>();
    private final BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation> relation =
            new BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation>();
    private String name = null;

    public ContextImplementation() {
        // no further initialization required
    }

    public ContextImplementation(final String name) {
        this.name = name;
    }

    public Set<FCAElementImplementation> getObjects() {
        return objects;
    }

    public Set<FCAElementImplementation> getAttributes() {
        return attributes;
    }

    public BinaryRelation<FCAElementImplementation, FCAElementImplementation> getRelation() {
        return relation;
    }

    public BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation> getRelationImplementation() {
        return this.relation;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Context<FCAElementImplementation, FCAElementImplementation>
            createSum(final Context<FCAElementImplementation, FCAElementImplementation> other, final String title) {
        final ContextImplementation context = new ContextImplementation(title);
        final Set<FCAElementImplementation> newObjects = context.getObjects();
        final Set<FCAElementImplementation> newAttributes = context.getAttributes();
        final BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation> newRelation = context
                .getRelationImplementation();

        newObjects.addAll(this.getObjects());
        newObjects.addAll(other.getObjects());

        newAttributes.addAll(this.getAttributes());
        newAttributes.addAll(other.getAttributes());

        for (FCAElementImplementation object : newObjects) {
            for (FCAElementImplementation attribute : newAttributes) {
                if (this.getRelation().contains(object, attribute)
                        || other.getRelation().contains(object, attribute)) {
                    newRelation.insert(object, attribute);
                }
            }
        }
        return context;
    }

    /**
     * @todo this is not a good place, since we assume FCAElements and SQL strings here -- it will break if that is not true.
     */
    @SuppressWarnings("unchecked")
    public Context<FCAElementImplementation, FCAElementImplementation> createProduct(
            final Context<FCAElementImplementation, FCAElementImplementation> other, final String title) {
        final ContextImplementation context = new ContextImplementation(title);
        final Set<FCAElementImplementation> newObjects = context.getObjects();
        final Set<FCAElementImplementation> newAttributes = context.getAttributes();
        final BinaryRelationImplementation<FCAElementImplementation, FCAElementImplementation> newRelation = context
                .getRelationImplementation();

        newAttributes.addAll(this.getAttributes());
        newAttributes.addAll(other.getAttributes());

        for (FCAElementImplementation objectL : this.getObjects()) {
            for (FCAElementImplementation objectR : other.getObjects()) {
                final String newObjectData = "(" + objectL.getData().toString()
                        + ") AND (" + objectR.getData().toString() + ")";
                final FCAElementImplementation newObject = new FCAElementImplementation(newObjectData);
                newObjects.add(newObject);
                Iterator<FCAElementImplementation> attrIt = this.getAttributes().iterator();
                while (attrIt.hasNext()) {
                    final FCAElementImplementation attribute = attrIt.next();
                    if (this.getRelation().contains(objectL, attribute)) {
                        newRelation.insert(newObject, attribute);
                    }
                }
                attrIt = other.getAttributes().iterator();
                while (attrIt.hasNext()) {
                    final FCAElementImplementation attribute = attrIt.next();
                    if (other.getRelation().contains(objectR, attribute)) {
                        newRelation.insert(newObject, attribute);
                    }
                }
            }
        }
        return context;
    }

    public ListSet<FCAElementImplementation> getObjectList() {
        return this.objects;
    }

    public ListSet<FCAElementImplementation> getAttributeList() {
        return this.attributes;
    }

    public void updatePositionMarkers() {
        int pos = 0;
        for (final FCAElementImplementation object : this.objects) {
            object.setContextPosition(pos);
            pos++;
        }
        pos = 0;
        for (final FCAElementImplementation object : this.attributes) {
            object.setContextPosition(pos);
            pos++;
        }
    }
}
