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
public class ContextImplementation<O, A> implements ListsContext<O, A> {
    private final ListSet<O> objects = new ListSetImplementation<O>();
    private final ListSet<A> attributes = new ListSetImplementation<A>();
    private final BinaryRelationImplementation<O, A> relation = new BinaryRelationImplementation<O, A>();
    private String name = null;

    public ContextImplementation() {
        // no further initialization required
    }

    public ContextImplementation(final String name) {
        this.name = name;
    }

    public Set<O> getObjects() {
        return objects;
    }

    public Set<A> getAttributes() {
        return attributes;
    }

    public BinaryRelation<O, A> getRelation() {
        return relation;
    }

    public BinaryRelationImplementation<O, A> getRelationImplementation() {
        return this.relation;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Context<O, A> createSum(final Context<O, A> other, final String title) {
        final ContextImplementation<O, A> context = new ContextImplementation<O, A>(
                title);
        final Set<O> newObjects = context.getObjects();
        final Set<A> newAttributes = context.getAttributes();
        final BinaryRelationImplementation<O, A> newRelation = context
                .getRelationImplementation();

        Iterator<O> objIt = this.getObjects().iterator();
        while (objIt.hasNext()) {
            final O object = objIt.next();
            newObjects.add(object);
        }
        objIt = other.getObjects().iterator();
        while (objIt.hasNext()) {
            final O object = objIt.next();
            newObjects.add(object);
        }
        Iterator<A> attrIt = this.getAttributes().iterator();
        while (attrIt.hasNext()) {
            final A attribute = attrIt.next();
            newAttributes.add(attribute);
        }
        attrIt = other.getAttributes().iterator();
        while (attrIt.hasNext()) {
            final A attribute = attrIt.next();
            newAttributes.add(attribute);
        }
        objIt = newObjects.iterator();
        while (objIt.hasNext()) {
            final O object = objIt.next();
            attrIt = newAttributes.iterator();
            while (attrIt.hasNext()) {
                final A attribute = attrIt.next();
                if (this.getRelation().contains(object, attribute)
                        || other.getRelation().contains(object, attribute)) {
                    newRelation.insert(object, attribute);
                }
            }
        }
        return context;
    }

    /**
     * @todo this is not a good place, since we assume FCAElements and SQL
     *       strings here -- it will break if that is not true.
     */
    @SuppressWarnings("unchecked")
    public Context<O, A> createProduct(final Context<O, A> other,
            final String title) {
        final ContextImplementation<O, A> context = new ContextImplementation<O, A>(
                title);
        final Set<O> newObjects = context.getObjects();
        final Set<A> newAttributes = context.getAttributes();
        final BinaryRelationImplementation<O, A> newRelation = context
                .getRelationImplementation();

        Iterator<A> attrIt = this.getAttributes().iterator();
        while (attrIt.hasNext()) {
            final A attribute = attrIt.next();
            newAttributes.add(attribute);
        }
        attrIt = other.getAttributes().iterator();
        while (attrIt.hasNext()) {
            final A attribute = attrIt.next();
            newAttributes.add(attribute);
        }
        final Iterator<O> objIt = this.getObjects().iterator();
        while (objIt.hasNext()) {
            final FCAElement objectL = (FCAElement) objIt.next();
            final Iterator<O> objIt2 = other.getObjects().iterator();
            while (objIt2.hasNext()) {
                final FCAElement objectR = (FCAElement) objIt2.next();
                final String newObjectData = "(" + objectL.getData().toString()
                        + ") AND (" + objectR.getData().toString() + ")";
                final FCAElement newObject = new FCAElementImplementation(
                        newObjectData);
                newObjects.add((O) newObject);
                attrIt = this.getAttributes().iterator();
                while (attrIt.hasNext()) {
                    final A attribute = attrIt.next();
                    if (this.getRelation().contains((O) objectL, attribute)) {
                        newRelation.insert((O) newObject, attribute);
                    }
                }
                attrIt = other.getAttributes().iterator();
                while (attrIt.hasNext()) {
                    final A attribute = attrIt.next();
                    if (other.getRelation().contains((O) objectR, attribute)) {
                        newRelation.insert((O) newObject, attribute);
                    }
                }
            }
        }
        return context;
    }

    public ListSet<O> getObjectList() {
        return this.objects;
    }

    public ListSet<A> getAttributeList() {
        return this.attributes;
    }

    public void updatePositionMarkers() {
        int pos = 0;
        for (final O object : this.objects) {
            if (object instanceof WritableFCAElement) {
                ((WritableFCAElement) object).setContextPosition(pos);
            }
            pos++;
        }
        pos = 0;
        for (final A object : this.attributes) {
            if (object instanceof WritableFCAElement) {
                ((WritableFCAElement) object).setContextPosition(pos);
            }
            pos++;
        }
    }
}
