/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import org.jdom.Element;
import util.CollectionFactory;

import java.util.*;

/**
 * Implements a concept whose objects are stored in a relational database.
 */
public class DatabaseConnectedConcept extends AbstractConceptImplementation {
    /**
     * Stores the information on how to use the Database connected.
     */
    private DatabaseInfo dbInfo;

    /**
     * Stores the attributes as Java Objects.
     */
    private Set attributeContingent = new HashSet();

    /**
     * Stores the where clause for finding the object contingent in the database.
     *
     * This is only the part that comes from the current diagram, the rest is
     * stored in a separate list to avoid duplication.
     *
     * @see #filterClauses
     */
    private String objectClause = null;

    private static final String OBJECT_ELEMENT_NAME = "object";

    /**
     * The constructor always needs the DB connection and the information how
     * to use it.
     */
    public DatabaseConnectedConcept(DatabaseInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public DatabaseConnectedConcept(Element element) throws XMLSyntaxError {
        readXML(element);
    }


    /// @todo this is DB specific, but it should be changed to be more generic
    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(CONCEPT_ELEMENT_NAME, elem);
        Element objectContingentElem = XMLHelper.mustbe(OBJECT_CONTINGENT_ELEMENT_NAME, elem);
        List objects = objectContingentElem.getChildren(OBJECT_ELEMENT_NAME);
        if (objects.size() > 1) {
            throw new XMLSyntaxError("Only one object clause allowed in this version");
        }
        if (objects.size() == 1) {
            Element objElem = (Element) objects.get(0);
            this.objectClause = objElem.getText();
        } else {
            this.objectClause = null;
        }
        Element attributeContingentElem = XMLHelper.mustbe(ATTRIBUTE_CONTINGENT_ELEMENT_NAME, elem);
        List attributes = attributeContingentElem.getChildren(ATTRIBUTE_ELEMENT_NAME);
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            Element attrElem = (Element) iterator.next();
            addAttribute(new Attribute(attrElem.getText(), null));
        }
    }


    protected void fillObjectContingentElement(Element objectContingentElem) {
        if (objectClause != null) {
            Element objectElem = new Element(OBJECT_ELEMENT_NAME);
            objectElem.addContent(objectClause);
            objectContingentElem.addContent(objectElem);
        }
    }

    /**
     * Changes the information about the database connection.
     */
    public void setDatabase(DatabaseInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    /**
     * Sets the where-clause for finding the object contingent.
     */
    public void setObjectClause(String clause) {
        this.objectClause = clause;
    }

    /**
     * True if the concept has an object clause defining the contingent.
     */
    public boolean hasObjectClause() {
        return this.objectClause != null;
    }

    public String getObjectClause() {
        return this.objectClause;
    }

    /**
     * Adds an attribute to the attribute contingent.
     */
    public void addAttribute(Object attribute) {
        this.attributeContingent.add(attribute);
    }

    /**
     * Implements AbstractConceptImplementation.getAttributeContingentSize().
     */
    public int getAttributeContingentSize() {
        return this.attributeContingent.size();
    }

    /**
     * Implements AbstractConceptImplementation.getObjectContingentSize().
     */
    public int getObjectContingentSize() {
        return 1;
    }

    /**
     * Implements AbstractConceptImplementation.getAttributeContingentIterator().
     */
    public Iterator getAttributeContingentIterator() {
        return this.attributeContingent.iterator();
    }

    public Iterator getObjectContingentIterator() {
        Set objects = CollectionFactory.createDefaultSet();
        objects.add(this.objectClause);
        return objects.iterator();
    }

    public String getExtentClause() {
        String clause = "(";
        boolean first = true;
        Iterator iter = this.ideal.iterator();
        while (iter.hasNext()) {
            DatabaseConnectedConcept item = (DatabaseConnectedConcept) iter.next();
            if (item.objectClause == null) {
                continue;
            }
            if (first) {
                clause += item.objectClause;
                first = false;
            } else {
                clause += " OR " + item.objectClause;
            }
        }
        clause += ")";
        if (first) { // nothing found
            return null;
        }
        return clause;
    }
}