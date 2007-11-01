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


public class ManyValuedContextImplementation implements WritableManyValuedContext, XMLizable {
    private ListSet objects = new ListSetImplementation();
    private ListSet<ManyValuedAttribute> attributes = new ListSetImplementation<ManyValuedAttribute>();
    private Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>> relation = new Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>>();
	private ListSet<Datatype> types = new ListSetImplementation<Datatype>();
	
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
	
	public ManyValuedContextImplementation(Element elem) throws XMLSyntaxError {
		readXML(elem);
	}

    public void add(FCAElement object) {
        if(object instanceof WritableFCAElement) {
            ((WritableFCAElement)object).setContextPosition(this.objects.size());
        }
        if(this.objects.contains(object)) {
            return; // do not create new hashtable
        }
        this.objects.add(object);
        this.relation.put(object, new Hashtable<ManyValuedAttribute, Value>());
    }

    public void remove(FCAElement object) {
        this.objects.remove(object);
        this.relation.remove(object);
    }

    public ListSet getObjects() {
        return ListSetImplementation.unmodifiableListSet(this.objects);
    }

    public void add(ManyValuedAttribute attribute) {
        this.attributes.add(attribute);
    }

    public void remove(ManyValuedAttribute attribute) {
        this.attributes.remove(attribute);
    }

    public ListSet<ManyValuedAttribute> getAttributes() {
        return ListSetImplementation.unmodifiableListSet(this.attributes);
    }
    
    public void add(Datatype type) {
        this.types.add(type);
    }

    public void remove(Datatype type) {
        this.types.remove(type);
    }

    public ListSet<Datatype> getTypes(){
    	return ListSetImplementation.unmodifiableListSet(this.types);
    }

    public void setRelationship(FCAElement object, ManyValuedAttribute attribute, Value value) {
        Hashtable<ManyValuedAttribute, Value> row = this.relation.get(object);
        if(value == null) {
        	row.remove(attribute);
        } else {
        	row.put(attribute, value);
        }
    }

    public Value getRelationship(FCAElement object, ManyValuedAttribute attribute) {
        Hashtable row = this.relation.get(object);
        return (Value) row.get(attribute);
    }
    
    public void update(){
    	Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>> newRelation = new Hashtable<FCAElement, Hashtable<ManyValuedAttribute, Value>>();
    	Set entries = this.relation.entrySet();
    	Set<Object> checkObjectNames = new HashSet<Object>();
    	for (Iterator iter = entries.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			FCAElement object = (FCAElement) entry.getKey();
			if (checkObjectNames.contains(object.getData())) {
				throw new IllegalStateException("Object appears twice in object set of many-valued context.");
			}
			checkObjectNames.add(object.getData());
			Hashtable propTable = (Hashtable) entry.getValue();
			Hashtable<ManyValuedAttribute, Value> newPropTable = new Hashtable<ManyValuedAttribute, Value>();
			Set propEntries = propTable.entrySet();
			Set<String> checkPropertyNames = new HashSet<String>();
			for (Iterator iter2 = propEntries.iterator(); iter2.hasNext();) {
				Entry propEntry = (Entry) iter2.next();
				ManyValuedAttribute prop = (ManyValuedAttribute) propEntry.getKey();
				if (checkPropertyNames.contains(prop.getName())){
					throw new IllegalStateException("Attribute appears twice in attribute set of many-valued context.");
				}
				checkPropertyNames.add(prop.getName());
				Value value = (Value) propEntry.getValue();
				newPropTable.put(prop,value);
			}
			newRelation.put(object,newPropTable);
		}
    	this.relation = newRelation;
    }

	public Element toXML() {
		Element retVal = new Element(MANY_VALUED_CONTEXT_ELEMENT_NAME);
		IdPool oidpool = new IdPool();
		IdPool aidpool = new IdPool();
		Hashtable<FCAElementImplementation, String> objectIdMapping = new Hashtable<FCAElementImplementation, String>();
		Hashtable<ManyValuedAttribute, String> attributeIdMapping = new Hashtable<ManyValuedAttribute, String>();
		
		Element objectsElement = new Element(OBJECTS_ELEMENT_NAME);
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			FCAElementImplementation itObject = (FCAElementImplementation) iter.next();
			Element objectElement = itObject.toXML();
			String id = oidpool.getFreeId(itObject.toString());
			objectIdMapping.put(itObject, id);
			objectElement.setAttribute(OBJECT_ID_ATTRIBUTE_NAME, id);
			objectsElement.addContent(objectElement);
		}
		retVal.addContent(objectsElement);
		Element typesElement = new Element(TYPES_ELEMENT_NAME);
		for (Iterator<Datatype> iter = types.iterator(); iter.hasNext();) {
            Datatype itType = iter.next();
			Element typeElement = ((XMLizable) itType).toXML();
			typesElement.addContent(typeElement);
		}
		retVal.addContent(typesElement);
		Element attributesElement = new Element(ATTRIBUTES_ELEMENT_NAME);
		for (Iterator<ManyValuedAttribute> iter = attributes.iterator(); iter.hasNext();) {
			ManyValuedAttribute itAttribute = iter.next();
			if (! (itAttribute instanceof ManyValuedAttributeImplementation)) {
				throw new RuntimeException("Found attribute \"" +
						itAttribute.getName() + "\" not to be XMLizable.");
			}
			Element attributeElement = ((ManyValuedAttributeImplementation)itAttribute).toXML();
			String id = aidpool.getFreeId(itAttribute.toString());
			attributeIdMapping.put(itAttribute, id);
			attributeElement.setAttribute(ATTRIBUTE_ID_ATTRIBUTE_NAME, id);
			attributesElement.addContent(attributeElement);
		}
		retVal.addContent(attributesElement);
		Element relationElement = new Element(RELATION_ELEMENT_NAME);
		for (Iterator iter = relation.entrySet().iterator(); iter.hasNext();) {
			Entry itRow = (Entry) iter.next();
			Hashtable values = (Hashtable) itRow.getValue();
			for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
				Entry  itAttributeValue = (Entry) iterator.next();
				ManyValuedAttributeImplementation mvAttribute = (ManyValuedAttributeImplementation) itAttributeValue.getKey();
				String attributeId = attributeIdMapping.get(mvAttribute);
				Element valueElement = new Element(RELATIONSHIP_ELEMENT_NAME); 
                mvAttribute.getType().insertValue(valueElement, (Value)itAttributeValue.getValue());
				valueElement.setAttribute(VALUE_OBJECT_REF_ATTRIBUTE_NAME, objectIdMapping.get(itRow.getKey()));
				valueElement.setAttribute(VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME, attributeId);
				relationElement.addContent(valueElement);
			}
		}
		retVal.addContent(relationElement);
		return retVal;
	}

	public void readXML(Element elem) throws XMLSyntaxError {
		Hashtable<String, FCAElementImplementation> objectIdMapping = new Hashtable<String, FCAElementImplementation>();
		Hashtable<String, ManyValuedAttribute> attributeIdMapping = new Hashtable<String, ManyValuedAttribute>();
		Hashtable<String, Datatype> typeIdMapping = new Hashtable<String, Datatype>();
		Element objectsElement = XMLHelper.getMandatoryChild(elem, OBJECTS_ELEMENT_NAME);
		Element typesElement = XMLHelper.getMandatoryChild(elem, TYPES_ELEMENT_NAME);
		Element attributesElement = XMLHelper.getMandatoryChild(elem, ATTRIBUTES_ELEMENT_NAME);
		Element relationElement = XMLHelper.getMandatoryChild(elem, RELATION_ELEMENT_NAME);

		for (Iterator<Element> iter = objectsElement.getChildren().iterator(); iter.hasNext();) {
			Element objectElement = iter.next();
			FCAElementImplementation newObject = new FCAElementImplementation(objectElement);
			String id = XMLHelper.getAttribute(objectElement, OBJECT_ID_ATTRIBUTE_NAME).getValue();
			objectIdMapping.put(id,newObject);
			add(newObject);
		}
		for (Iterator<Element> iter = typesElement.getChildren().iterator(); iter.hasNext();) {
			Element typeElement = iter.next();
			Datatype newType = DatatypeFactory.readType(typeElement);
			typeIdMapping.put(newType.getName(), newType);
			types.add(newType);
		}
		for (Iterator<Element> iter = attributesElement.getChildren().iterator(); iter.hasNext();) {
			Element attributeElement = iter.next();
			ManyValuedAttribute newAttribute = new ManyValuedAttributeImplementation(attributeElement, typeIdMapping);
			String id = XMLHelper.getAttribute(attributeElement, ATTRIBUTE_ID_ATTRIBUTE_NAME).getValue();
			attributeIdMapping.put(id, newAttribute);
			attributes.add(newAttribute);
		}
		for (Iterator<Element> iter = relationElement.getChildren().iterator(); iter.hasNext();) {
			Element valueElement = iter.next();
			String objectRef = XMLHelper.getAttribute(valueElement, VALUE_OBJECT_REF_ATTRIBUTE_NAME).getValue();
			FCAElementImplementation rowObject = objectIdMapping.get(objectRef);
            String attributeRef = XMLHelper.getAttribute(valueElement, VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME).getValue();
			ManyValuedAttribute tempAttribute = attributeIdMapping.get(attributeRef);
			Value tempValue = tempAttribute.getType().toValue(valueElement);
			setRelationship(rowObject, tempAttribute, tempValue);
		}
	}
}

