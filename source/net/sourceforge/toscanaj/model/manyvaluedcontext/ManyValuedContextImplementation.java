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

import net.sourceforge.toscanaj.model.context.FCAObject;
import net.sourceforge.toscanaj.model.context.FCAObjectImplementation;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.util.IdPool;
import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;


public class ManyValuedContextImplementation implements WritableManyValuedContext, XMLizable {

	private static final String ATTRIBUTE_VALUE_ATTRIBUTE_ID_ATTRIBUTE_NAME = "attributeId";
	private ListSet objects = new ListSetImplementation();
    private ListSet properties = new ListSetImplementation();
    private Hashtable relation = new Hashtable();
	private ListSet types = new ListSetImplementation();
	private static final String MANY_VALUED_CONTEXT_ELEMENT_NAME = "manyValuedContext";
	private static final String OBJECTS_ELEMENT_NAME = "objects";
	private static final String PROPERTIES_ELEMENT_NAME = "attributes";
	private static final String TYPES_ELEMENT_NAME = "types";
	private static final String RELATION_ELEMENT_NAME = "relation";
	private static final String ROW_ELEMENT_NAME = "object";
	private static final String OBJECT_ID_ATTRIBUTE_NAME = "objectId";
	private static final String ATTRIBUTE_ID_ATTRIBUTE_NAME = "attributeId";
	private static final String TYPE_ID_ATTRIBUTE_NAME = "typeId";
	private static final String ROW_OBJECT_REF_ATTRIBUTE_NAME = "objectRef";
	private static final String ATTRIBUTE_VALUE_PAIR_ELEMENT_NAME = "attribute";
	public ManyValuedContextImplementation() {
    }

    public void add(FCAObject object) {
        this.objects.add(object);
        this.relation.put(object, new Hashtable());
    }

    public void remove(FCAObject object) {
        this.objects.remove(object);
        this.relation.remove(object);
    }

    public ListSet getObjects() {
        return ListSetImplementation.unmodifiableListSet(this.objects);
    }

    public void add(ManyValuedAttribute attribute) {
        this.properties.add(attribute);
    }

    public void remove(ManyValuedAttribute attribute) {
        this.properties.remove(attribute);
    }

    public ListSet getAttributes() {
        return ListSetImplementation.unmodifiableListSet(this.properties);
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

    public void setRelationship(FCAObject object, ManyValuedAttribute attribute, AttributeValue value) {
        Hashtable row = (Hashtable) this.relation.get(object);
        row.put(attribute, value);
    }

    public AttributeValue getRelationship(FCAObject object, ManyValuedAttribute attribute) {
        Hashtable row = (Hashtable) this.relation.get(object);
        return (AttributeValue) row.get(attribute);
    }
    
    public void update(){
    	Hashtable newRelation = new Hashtable();
    	Set entries = this.relation.entrySet();
    	Set checkObjectNames = new HashSet();
    	for (Iterator iter = entries.iterator(); iter.hasNext();) {
			Entry entry = (Entry) iter.next();
			FCAObject object = (FCAObject) entry.getKey();
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
/*		private Hashtable relation = new Hashtable();
		private ListSet types = new ListSetImplementation();
*/
		IdPool oidpool = new IdPool();
		IdPool aidpool = new IdPool();
		IdPool tidpool = new IdPool();
		Hashtable objectIdMapping = new Hashtable();
		Hashtable attributeIdMapping = new Hashtable();
		Hashtable typeIdMapping = new Hashtable();
		
		Element objectsElement = new Element(OBJECTS_ELEMENT_NAME);
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			FCAObjectImplementation itObject = (FCAObjectImplementation) iter.next();
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
		Element propertiesElement = new Element(PROPERTIES_ELEMENT_NAME);
		for (Iterator iter = properties.iterator(); iter.hasNext();) {
			ManyValuedAttribute itProp = (ManyValuedAttribute) iter.next();
			if (! (itProp instanceof ManyValuedAttributeImplementation)) {
				throw new RuntimeException("Found attribute \"" +
						itProp.getName() + "\" not to be XMLizable.");
			}
			Element propertyElement = ((ManyValuedAttributeImplementation)itProp).toXML(typeIdMapping);
			String id = aidpool.getFreeId(itProp.toString());
			attributeIdMapping.put(itProp, id);
			propertyElement.setAttribute(ATTRIBUTE_ID_ATTRIBUTE_NAME, id);
			propertiesElement.addContent(propertyElement);
		}
		retVal.addContent(propertiesElement);
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
				attributeValueElement.setAttribute(ATTRIBUTE_VALUE_ATTRIBUTE_ID_ATTRIBUTE_NAME, attributeId);
				attributeValueElement.addContent(mvAttribute.getType().toElement((AttributeValue)itAttributeValue.getValue()));
				rowElement.addContent(attributeValueElement);
			}
			relationElement.addContent(rowElement);
		}
		retVal.addContent(relationElement);
		return retVal;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.toscanaj.util.xmlize.XMLizable#readXML(org.jdom.Element)
	 */
	public void readXML(Element elem) {
		// TODO Auto-generated method stub
		
	}
}

