/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import net.sourceforge.toscanaj.model.cernato.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class CernatoXMLParser {
    public static CernatoModel importCernatoXMLFile(File cernatoXMLFile)
            throws FileNotFoundException, DataFormatException, JDOMException {
        FileInputStream in;
        in = new FileInputStream(cernatoXMLFile);

        SAXBuilder parser = new SAXBuilder();
        Document document = parser.build(in);

        Element rootElement = document.getRootElement();
        if (!rootElement.getName().equals("cernatodata")) {
            throw new DataFormatException("Input file is not Cernato XML export");
        }

        CernatoModel model = new CernatoModel();

        Hashtable types = new Hashtable();
        Hashtable properties = new Hashtable();
        Hashtable objects = new Hashtable();

        parseTypes(rootElement, model, types);
        parseProperties(rootElement, model, types, properties);
        parseObjects(rootElement, model, objects);
        parseRelation(rootElement, model, properties, objects);
        parseViews(rootElement, model, properties);

        return model;
    }

    private static void parseTypes(Element root, CernatoModel model, Hashtable types) throws DataFormatException {
        Element typesElement = root.getChild("types");
        if (typesElement == null) {
            throw new DataFormatException("Could not find types");
        }
        List typeElements = typesElement.getChildren();
        for (Iterator iterator = typeElements.iterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            if (element.getName().equals("type_textual")) {
                parseTextualType(element, model, types);
            } else if (element.getName().equals("type_numerical")) {
                parseNumericalType(element, model, types);
            }
        }
    }

    private static void parseTextualType(Element element, CernatoModel model, Hashtable types) throws DataFormatException {
        String id = element.getAttributeValue("id");
        if (id == null) {
            throw new DataFormatException("Type missing id");
        }
        String name = element.getChild("name").getText();
        if (name == null) {
            throw new DataFormatException("Type missing name");
        }
        TextualType newType = new TextualType(name);
        List valueGroups = element.getChildren("text_value_group");
        for (Iterator iterator = valueGroups.iterator(); iterator.hasNext();) {
            Element valueGroupElement = (Element) iterator.next();
            String groupId = valueGroupElement.getAttributeValue("id");
            String groupName = valueGroupElement.getChild("name").getText();
            TextualValueGroup group = new TextualValueGroup(newType, groupName, groupId);
            List values = valueGroupElement.getChildren("text_value");
            for (Iterator iterator2 = values.iterator(); iterator2.hasNext();) {
                Element valueElement = (Element) iterator2.next();
                group.addValue(new TextualValue(valueElement.getText()));
            }
        }
        types.put(id, newType);
        model.getTypes().add(newType);
    }

    private static void parseNumericalType(Element element, CernatoModel model, Hashtable types) throws DataFormatException {
        String id = element.getAttributeValue("id");
        if (id == null) {
            throw new DataFormatException("Type missing id");
        }
        String name = element.getChild("name").getText();
        if (name == null) {
            throw new DataFormatException("Type missing name");
        }
        NumericalType newType = new NumericalType(name);
        List valueGroups = element.getChildren("num_value_group");
        for (Iterator iterator = valueGroups.iterator(); iterator.hasNext();) {
            Element valueGroupElement = (Element) iterator.next();
            String groupId = valueGroupElement.getAttributeValue("id");
            String groupName = valueGroupElement.getChild("name").getText();
            double min = Double.parseDouble(valueGroupElement.getChild("lower_border").getText());
            boolean minIncluded = valueGroupElement.getChild("lower_border").getAttributeValue("included").equals("yes");
            double max = Double.parseDouble(valueGroupElement.getChild("upper_border").getText());
            boolean maxIncluded = valueGroupElement.getChild("upper_border").getAttributeValue("included").equals("yes");
            new NumericalValueGroup(newType, groupName, groupId, min, minIncluded, max, maxIncluded);
        }
        types.put(id, newType);
        model.getTypes().add(newType);
    }

    private static void parseProperties(Element root, CernatoModel model, Hashtable types, Hashtable properties)
            throws DataFormatException {
        Element propertiesElement = root.getChild("properties");
        if (propertiesElement == null) {
            throw new DataFormatException("Could not find properties");
        }
        List propertyElements = propertiesElement.getChildren("property");
        for (Iterator iterator = propertyElements.iterator(); iterator.hasNext();) {
            Element propElem = (Element) iterator.next();
            String id = propElem.getAttributeValue("id");
            String name = propElem.getChild("name").getText();
            String typeref = propElem.getChild("type_ref").getAttributeValue("type");
            Property property = new Property((Type) types.get(typeref), name);
            properties.put(id, property);
            model.getContext().add(property);
        }
    }

    private static void parseObjects(Element root, CernatoModel model, Hashtable objects)
            throws DataFormatException {
        Element objectsElement = root.getChild("objects");
        if (objectsElement == null) {
            throw new DataFormatException("Could not find objects");
        }
        List objectElement = objectsElement.getChildren("object");
        for (Iterator iterator = objectElement.iterator(); iterator.hasNext();) {
            Element objElem = (Element) iterator.next();
            String id = objElem.getAttributeValue("id");
            String name = objElem.getChild("name").getText();
            FCAObject object = new FCAObject(name);
            objects.put(id, object);
            model.getContext().add(object);
        }
    }

    private static void parseRelation(Element root, CernatoModel model, Hashtable properties, Hashtable objects)
            throws DataFormatException {
        Element relationElement = root.getChild("relation");
        if (relationElement == null) {
            throw new DataFormatException("Could not find relation");
        }
        List rowElems = relationElement.getChildren("row");
        for (Iterator iterator = rowElems.iterator(); iterator.hasNext();) {
            Element rowElem = (Element) iterator.next();
            String objectid = rowElem.getAttributeValue("object");
            FCAObject object = (FCAObject) objects.get(objectid);
            List cellElems = rowElem.getChildren("cell");
            for (Iterator iterator2 = cellElems.iterator(); iterator2.hasNext();) {
                Element cellElem = (Element) iterator2.next();
                String propertyid = cellElem.getAttributeValue("property");
                Property property = (Property) properties.get(propertyid);
                String content = cellElem.getText();
                if (property.getType() instanceof TextualType) {
                    model.getContext().setRelationship(object, property, new TextualValue(content));
                } else if (property.getType() instanceof NumericalType) {
                    model.getContext().setRelationship(object, property, new NumericalValue(Double.parseDouble(content)));
                }
            }
        }
    }

    private static void parseViews(Element rootElement, CernatoModel model, Hashtable properties)
            throws DataFormatException {
        Element viewsElem = rootElement.getChild("views");
        if (viewsElem == null) {
            throw new DataFormatException("Could not find views");
        }
        List viewElems = viewsElem.getChildren("view");
        for (Iterator iterator = viewElems.iterator(); iterator.hasNext();) {
            Element viewElem = (Element) iterator.next();
            String name = viewElem.getChild("name").getText();
            View view = new View(name);
            List criteriaElems = viewElem.getChildren("criterion");
            for (Iterator iterator2 = criteriaElems.iterator(); iterator2.hasNext();) {
                Element criterionElem = (Element) iterator2.next();
                Property property = (Property) properties.get(criterionElem.getChild("property_ref").
                        getAttributeValue("property"));
                ValueGroup valgroup = property.getType().getValueGroup(criterionElem.getChild("value_group_ref").
                        getAttributeValue("value_group"));
                view.addCriterion(new Criterion(property, valgroup));
            }
            model.getViews().add(view);
        }
    }
}
