/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TypeImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.util.IdPool;
import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;


public class ManyValuedContextImplementation implements WritableManyValuedContext, XMLizable {

	private ListSet objects = new ListSetImplementation();
    private ListSet attributes = new ListSetImplementation();
    private Hashtable relation = new Hashtable();
	private ListSet types = new ListSetImplementation();
	
	
	public static final String MANY_VALUED_CONTEXT_ELEMENT_NAME = "manyValuedContext";
	private static final String ATTRIBUTE_ID_ATTRIBUTE_NAME = "attributeId";
	private static final String OBJECTS_ELEMENT_NAME = "objects";
	private static final String ATTRIBUTES_ELEMENT_NAME = "attributes";
	private static final String TYPES_ELEMENT_NAME = "types";
	private static final String RELATION_ELEMENT_NAME = "relation";
	private static final String ROW_ELEMENT_NAME = "object";
	private static final String OBJECT_ID_ATTRIBUTE_NAME = "objectId";
	private static final String TYPE_ID_ATTRIBUTE_NAME = "typeId";
	private static final String ROW_OBJECT_REF_ATTRIBUTE_NAME = "objectRef";
	private static final String ATTRIBUTE_VALUE_PAIR_ELEMENT_NAME = "attribute";
	private static final String ATTRIBUTE_VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME = "attributeRef";
	
	public ManyValuedContextImplementation() {
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
        this.relation.put(object, new Hashtable());
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

    public ListSet getAttributes() {
        return ListSetImplementation.unmodifiableListSet(this.attributes);
    }
    
    public void add(AttributeType type) {
        this.types.add(type);
    }

    public void remove(AttributeType type) {
        this.types.remove(type);
    }

    public ListSet getTypes(){
    	return ListSetImplementation.unmodifiableListSet(this.types);
    }

    public void setRelationship(FCAElement object, ManyValuedAttribute attribute, AttributeValue value) {
        Hashtable row = (Hashtable) this.relation.get(object);
        row.put(attribute, value);
    }

    public AttributeValue getRelationship(FCAElement object, ManyValuedAttribute attribute) {
        Hashtable row = (Hashtable) this.relation.get(object);
        return (AttributeValue) row.get(attribute);
    }
    
    public void update(){
    	Hashtable newRelation = new Hashtable();
    	Set entries = this.relation.entrySet();
    	Set checkObjectNames = new HashSet();
    	for (Iterator iter = entries.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			FCAElement object = (FCAElement) entry.getKey();
			if (checkObjectNames.contains(object.getData())) {
				throw new IllegalStateException("Object appears twice in object set of many-valued context.");
			}
			checkObjectNames.add(object.getData());
			Hashtable propTable = (Hashtable) entry.getValue();
			Hashtable newPropTable = new Hashtable();
			Set propEntries = propTable.entrySet();
			Set checkPropertyNames = new HashSet();
			for (Iterator iter2 = propEntries.iterator(); iter2.hasNext();) {
				Entry propEntry = (Entry) iter2.next();
				ManyValuedAttribute prop = (ManyValuedAttribute) propEntry.getKey();
				if (checkPropertyNames.contains(prop.getName())){
					throw new IllegalStateException("Attribute appears twice in attribute set of many-valued context.");
				}
				checkPropertyNames.add(prop.getName());
				Object value = propEntry.getValue();
				newPropTable.put(prop,value);
			}
			newRelation.put(object,newPropTable);
		}
    	this.relation = newRelation;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#toXML()
	 */
	public Element toXML() {
		// TODO Auto-generated method stub
		Element retVal = new Element(MANY_VALUED_CONTEXT_ELEMENT_NAME);
		IdPool oidpool = new IdPool();
		IdPool aidpool = new IdPool();
		IdPool tidpool = new IdPool();
		Hashtable objectIdMapping = new Hashtable();
		Hashtable attributeIdMapping = new Hashtable();
		Hashtable typeIdMapping = new Hashtable();
		
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
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			AttributeType itType = (AttributeType) iter.next();
			if (! (itType instanceof XMLizable)){
				throw new RuntimeException("Found type \"" +
						itType.getName() + "\" not to be XMLizable.");
			}
			String id = tidpool.getFreeId(itType.toString());
			typeIdMapping.put(itType, id);
			Element typeElement = ((XMLizable) itType).toXML();
			typeElement.setAttribute(TYPE_ID_ATTRIBUTE_NAME, id);
			typesElement.addContent(typeElement);
		}
		retVal.addContent(typesElement);
		Element attributesElement = new Element(ATTRIBUTES_ELEMENT_NAME);
		for (Iterator iter = attributes.iterator(); iter.hasNext();) {
			ManyValuedAttribute itAttribute = (ManyValuedAttribute) iter.next();
			if (! (itAttribute instanceof ManyValuedAttributeImplementation)) {
				throw new RuntimeException("Found attribute \"" +
						itAttribute.getName() + "\" not to be XMLizable.");
			}
			Element attributeElement = ((ManyValuedAttributeImplementation)itAttribute).toXML(typeIdMapping);
			String id = aidpool.getFreeId(itAttribute.toString());
			attributeIdMapping.put(itAttribute, id);
			attributeElement.setAttribute(ATTRIBUTE_ID_ATTRIBUTE_NAME, id);
			attributesElement.addContent(attributeElement);
		}
		retVal.addContent(attributesElement);
		Element relationElement = new Element(RELATION_ELEMENT_NAME);
		for (Iterator iter = relation.entrySet().iterator(); iter.hasNext();) {
			Entry itRow = (Entry) iter.next();
			Element rowElement = new Element(ROW_ELEMENT_NAME);
			rowElement.setAttribute(ROW_OBJECT_REF_ATTRIBUTE_NAME, (String) objectIdMapping.get(itRow.getKey()));
			Hashtable values = (Hashtable) itRow.getValue();
			for (Iterator iterator = values.entrySet().iterator(); iterator.hasNext();) {
				Entry  itAttributeValue = (Entry) iterator.next();
				Element attributeValueElement = new Element(ATTRIBUTE_VALUE_PAIR_ELEMENT_NAME);
				ManyValuedAttributeImplementation mvAttribute = (ManyValuedAttributeImplementation) itAttributeValue.getKey();
				String attributeId = (String) attributeIdMapping.get(mvAttribute);
				attributeValueElement.setAttribute(ATTRIBUTE_VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME, attributeId);
				attributeValueElement.addContent(mvAttribute.getType().toElement((AttributeValue)itAttributeValue.getValue()));
				rowElement.addContent(attributeValueElement);
			}
			relationElement.addContent(rowElement);
		}
		retVal.addContent(relationElement);
		return retVal;
	}

	public void readXML(Element elem) throws XMLSyntaxError {
		Hashtable objectIdMapping = new Hashtable();
		Hashtable attributeIdMapping = new Hashtable();
		Hashtable typeIdMapping = new Hashtable();
		Element objectsElement = XMLHelper.getMandatoryChild(elem, OBJECTS_ELEMENT_NAME);
		Element typesElement = XMLHelper.getMandatoryChild(elem, TYPES_ELEMENT_NAME);
		Element attributesElement = XMLHelper.getMandatoryChild(elem, ATTRIBUTES_ELEMENT_NAME);
		Element relationElement = XMLHelper.getMandatoryChild(elem, RELATION_ELEMENT_NAME);

		for (Iterator iter = objectsElement.getChildren().iterator(); iter.hasNext();) {
			Element objectElement = (Element) iter.next();
			FCAElementImplementation newObject = new FCAElementImplementation(objectElement);
			String id = XMLHelper.getAttribute(objectElement, OBJECT_ID_ATTRIBUTE_NAME).getValue();
			objectIdMapping.put(id,newObject);
			add(newObject);
		}
		for (Iterator iter = typesElement.getChildren().iterator(); iter.hasNext();) {
			Element typeElement = (Element) iter.next();
			String className = XMLHelper.getAttribute(typeElement, TypeImplementation.CLASS_ATTRIBUTE_NAME).getValue();
			AttributeType newType;
			try {
				Constructor construct = Class.forName(className).getConstructor(new Class[] {Element.class});
				newType = (AttributeType) construct.newInstance(new Object[] {typeElement});
			} catch (Exception e) {
				throw new XMLSyntaxError("Initialization of attribute type \"" + className + "\" failed.", e);
			}
			String id = XMLHelper.getAttribute(typeElement, TYPE_ID_ATTRIBUTE_NAME).getValue();
			typeIdMapping.put(id, newType);
			types.add(newType);
		}
		for (Iterator iter = attributesElement.getChildren().iterator(); iter.hasNext();) {
			Element attributeElement = (Element) iter.next();
			ManyValuedAttribute newAttribute = new ManyValuedAttributeImplementation(attributeElement, typeIdMapping);
			String id = XMLHelper.getAttribute(attributeElement, ATTRIBUTE_ID_ATTRIBUTE_NAME).getValue();
			attributeIdMapping.put(id, newAttribute);
			attributes.add(newAttribute);
		}
		for (Iterator iter = relationElement.getChildren().iterator(); iter.hasNext();) {
			Element row = (Element) iter.next();
			String objectRef = XMLHelper.getAttribute(row, ROW_OBJECT_REF_ATTRIBUTE_NAME).getValue();
			FCAElementImplementation rowObject = (FCAElementImplementation) objectIdMapping.get(objectRef);
			ManyValuedAttribute tempAttribute;
			AttributeValue tempValue;
			Hashtable tempRow = new Hashtable();
			for (Iterator it = row.getChildren().iterator(); it.hasNext();) {
				Element attributeValueElement = (Element) it.next();
				String attributeRef = XMLHelper.getAttribute(attributeValueElement, ATTRIBUTE_VALUE_ATTRIBUTE_REF_ATTRIBUTE_NAME).getValue();
				tempAttribute = (ManyValuedAttribute) attributeIdMapping.get(attributeRef);
				Element valueElement = XMLHelper.getMandatoryChild(attributeValueElement, TypeImplementation.VALUE_ELEMENT_NAME);
				tempValue = tempAttribute.getType().toValue(valueElement);
				tempRow.put(tempAttribute, tempValue);
			}
			relation.put(rowObject, tempRow);
		}
	}
}

