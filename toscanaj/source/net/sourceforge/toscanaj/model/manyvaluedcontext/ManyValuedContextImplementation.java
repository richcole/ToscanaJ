/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.datatype.Datatype;
import org.tockit.datatype.DatatypeFactory;
import org.tockit.datatype.Value;
import org.tockit.datatype.xsd.AbstractXSDDatatype;
import org.tockit.util.IdPool;
import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;

public class ManyValuedContextImplementation implements
        WritableManyValuedContext, XMLizable {
    private final ListSet objects = new ListSetImplementation();
    private final ListSet<ManyValuedAttribute> attributes = new ListSetImplementation<ManyValuedAttribute>();
    private Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>> relation = new Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>>();
    private final ListSet<Datatype> types = new ListSetImplementation<Datatype>();

    public static final String MANY_VALUED_CONTEXT_ELEMENT_NAME = "manyValuedContext";
    private static final String ATTRIBUTE_ID_ATTRIBUTE_NAME = "attributeId";
    private static final String OBJECTS_ELEMENT_NAME = "objects";
    private static final String ATTRIBUTES_ELEMENT_NAME = "attributes";
    private static final String TYPES_ELEMENT_NAME = "types";
    private static final String RELATION_ELEMENT_NAME = "relation";
    private static final String RELATIONSHIP_ELEMENT_NAME = "tuple";
    private static final String OBJECT_ID_ATTRIBUTE_NAME = "objectId";
    private static final String VALUE_OBJECT_REF_ATTRIBUTE_NAME = "objectRef";
    private static final String VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME = "attributeRef";

    static {
        AbstractXSDDatatype.registerTypeCreators();
    }

    public ManyValuedContextImplementation() {
        // nothing to do here
    }

    public ManyValuedContextImplementation(final Element elem)
            throws XMLSyntaxError {
        readXML(elem);
    }

    public void add(final FCAElement object) {
        if (object instanceof WritableFCAElement) {
            ((WritableFCAElement) object).setContextPosition(this.objects
                    .size());
        }
        if (this.objects.contains(object)) {
            return; // do not create new hashtable
        }
        this.objects.add(object);
        this.relation.put(object, new Hashtable<ManyValuedAttribute, Value>());
    }

    public void remove(final FCAElement object) {
        this.objects.remove(object);
        this.relation.remove(object);
    }

    public ListSet getObjects() {
        return ListSetImplementation.unmodifiableListSet(this.objects);
    }

    public void add(final ManyValuedAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void remove(final ManyValuedAttribute attribute) {
        this.attributes.remove(attribute);
    }

    public ListSet<ManyValuedAttribute> getAttributes() {
        return ListSetImplementation.unmodifiableListSet(this.attributes);
    }

    public void add(final Datatype type) {
        this.types.add(type);
    }

    public void remove(final Datatype type) {
        this.types.remove(type);
    }

    public ListSet<Datatype> getTypes() {
        return ListSetImplementation.unmodifiableListSet(this.types);
    }

    public void setRelationship(final FCAElement object,
            final ManyValuedAttribute attribute, final Value value) {
        final Hashtable<ManyValuedAttribute, Value> row = this.relation
                .get(object);
        if (value == null) {
            row.remove(attribute);
        } else {
            row.put(attribute, value);
        }
    }

    public Value getRelationship(final FCAElement object,
            final ManyValuedAttribute attribute) {
        final Hashtable row = this.relation.get(object);
        return (Value) row.get(attribute);
    }

    public void update() {
        final Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>> newRelation = new Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>>();
        final Set entries = this.relation.entrySet();
        final Set<Object> checkObjectNames = new HashSet<Object>();
        for (final Iterator iter = entries.iterator(); iter.hasNext();) {
            final Entry entry = (Entry) iter.next();
            final FCAElement object = (FCAElement) entry.getKey();
            if (checkObjectNames.contains(object.getData())) {
                throw new IllegalStateException(
                        "Object appears twice in object set of many-valued context.");
            }
            checkObjectNames.add(object.getData());
            final Hashtable propTable = (Hashtable) entry.getValue();
            final Hashtable<ManyValuedAttribute, Value> newPropTable = new Hashtable<ManyValuedAttribute, Value>();
            final Set propEntries = propTable.entrySet();
            final Set<String> checkPropertyNames = new HashSet<String>();
            for (final Iterator iter2 = propEntries.iterator(); iter2.hasNext();) {
                final Entry propEntry = (Entry) iter2.next();
                final ManyValuedAttribute prop = (ManyValuedAttribute) propEntry
                        .getKey();
                if (checkPropertyNames.contains(prop.getName())) {
                    throw new IllegalStateException(
                            "Attribute appears twice in attribute set of many-valued context.");
                }
                checkPropertyNames.add(prop.getName());
                final Value value = (Value) propEntry.getValue();
                newPropTable.put(prop, value);
            }
            newRelation.put(object, newPropTable);
        }
        this.relation = newRelation;
    }

    public Element toXML() {
        final Element retVal = new Element(MANY_VALUED_CONTEXT_ELEMENT_NAME);
        final IdPool oidpool = new IdPool();
        final IdPool aidpool = new IdPool();
        final Hashtable<FCAElementImplementation, String> objectIdMapping = new Hashtable<FCAElementImplementation, String>();
        final Hashtable<ManyValuedAttribute, String> attributeIdMapping = new Hashtable<ManyValuedAttribute, String>();

        final Element objectsElement = new Element(OBJECTS_ELEMENT_NAME);
        for (final Iterator iter = objects.iterator(); iter.hasNext();) {
            final FCAElementImplementation itObject = (FCAElementImplementation) iter
                    .next();
            final Element objectElement = itObject.toXML();
            final String id = oidpool.getFreeId(itObject.toString());
            objectIdMapping.put(itObject, id);
            objectElement.setAttribute(OBJECT_ID_ATTRIBUTE_NAME, id);
            objectsElement.addContent(objectElement);
        }
        retVal.addContent(objectsElement);
        final Element typesElement = new Element(TYPES_ELEMENT_NAME);
        for (final Datatype itType : types) {
            final Element typeElement = itType.toXML();
            typesElement.addContent(typeElement);
        }
        retVal.addContent(typesElement);
        final Element attributesElement = new Element(ATTRIBUTES_ELEMENT_NAME);
        for (final ManyValuedAttribute itAttribute : attributes) {
            if (!(itAttribute instanceof ManyValuedAttributeImplementation)) {
                throw new RuntimeException("Found attribute \""
                        + itAttribute.getName() + "\" not to be XMLizable.");
            }
            final Element attributeElement = ((ManyValuedAttributeImplementation) itAttribute)
                    .toXML();
            final String id = aidpool.getFreeId(itAttribute.toString());
            attributeIdMapping.put(itAttribute, id);
            attributeElement.setAttribute(ATTRIBUTE_ID_ATTRIBUTE_NAME, id);
            attributesElement.addContent(attributeElement);
        }
        retVal.addContent(attributesElement);
        final Element relationElement = new Element(RELATION_ELEMENT_NAME);
        for (final Object element : relation.entrySet()) {
            final Entry itRow = (Entry) element;
            final Hashtable values = (Hashtable) itRow.getValue();
            for (final Iterator iterator = values.entrySet().iterator(); iterator
                    .hasNext();) {
                final Entry itAttributeValue = (Entry) iterator.next();
                final ManyValuedAttributeImplementation mvAttribute = (ManyValuedAttributeImplementation) itAttributeValue
                        .getKey();
                final String attributeId = attributeIdMapping.get(mvAttribute);
                final Element valueElement = new Element(
                        RELATIONSHIP_ELEMENT_NAME);
                mvAttribute.getType().insertValue(valueElement,
                        (Value) itAttributeValue.getValue());
                valueElement.setAttribute(VALUE_OBJECT_REF_ATTRIBUTE_NAME,
                        objectIdMapping.get(itRow.getKey()));
                valueElement.setAttribute(VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME,
                        attributeId);
                relationElement.addContent(valueElement);
            }
        }
        retVal.addContent(relationElement);
        return retVal;
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        final Hashtable<String, FCAElementImplementation> objectIdMapping = new Hashtable<String, FCAElementImplementation>();
        final Hashtable<String, ManyValuedAttribute> attributeIdMapping = new Hashtable<String, ManyValuedAttribute>();
        final Hashtable<String, Datatype> typeIdMapping = new Hashtable<String, Datatype>();
        final Element objectsElement = XMLHelper.getMandatoryChild(elem,
                OBJECTS_ELEMENT_NAME);
        final Element typesElement = XMLHelper.getMandatoryChild(elem,
                TYPES_ELEMENT_NAME);
        final Element attributesElement = XMLHelper.getMandatoryChild(elem,
                ATTRIBUTES_ELEMENT_NAME);
        final Element relationElement = XMLHelper.getMandatoryChild(elem,
                RELATION_ELEMENT_NAME);

        for (final Iterator<Element> iter = objectsElement.getChildren()
                .iterator(); iter.hasNext();) {
            final Element objectElement = iter.next();
            final FCAElementImplementation newObject = new FCAElementImplementation(
                    objectElement);
            final String id = XMLHelper.getAttribute(objectElement,
                    OBJECT_ID_ATTRIBUTE_NAME).getValue();
            objectIdMapping.put(id, newObject);
            add(newObject);
        }
        for (final Iterator<Element> iter = typesElement.getChildren()
                .iterator(); iter.hasNext();) {
            final Element typeElement = iter.next();
            final Datatype newType = DatatypeFactory.readType(typeElement);
            typeIdMapping.put(newType.getName(), newType);
            types.add(newType);
        }
        for (final Iterator<Element> iter = attributesElement.getChildren()
                .iterator(); iter.hasNext();) {
            final Element attributeElement = iter.next();
            final ManyValuedAttribute newAttribute = new ManyValuedAttributeImplementation(
                    attributeElement, typeIdMapping);
            final String id = XMLHelper.getAttribute(attributeElement,
                    ATTRIBUTE_ID_ATTRIBUTE_NAME).getValue();
            attributeIdMapping.put(id, newAttribute);
            attributes.add(newAttribute);
        }
        for (final Iterator<Element> iter = relationElement.getChildren()
                .iterator(); iter.hasNext();) {
            final Element valueElement = iter.next();
            final String objectRef = XMLHelper.getAttribute(valueElement,
                    VALUE_OBJECT_REF_ATTRIBUTE_NAME).getValue();
            final FCAElementImplementation rowObject = objectIdMapping
                    .get(objectRef);
            final String attributeRef = XMLHelper.getAttribute(valueElement,
                    VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME).getValue();
            final ManyValuedAttribute tempAttribute = attributeIdMapping
                    .get(attributeRef);
            final Value tempValue = tempAttribute.getType().toValue(
                    valueElement);
            setRelationship(rowObject, tempAttribute, tempValue);
        }
    }
}
