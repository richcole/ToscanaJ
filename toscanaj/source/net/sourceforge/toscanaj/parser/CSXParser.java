/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerException;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.tockit.events.EventBroker;

/**
 * This class reads a CSX file and does nothing with it except complaining.
 * 
 * TODO don't do this on the class-level, a parser should be instantiated as an
 * object
 */
public class CSXParser {
    /**
     * Stores the schema that is created.
     * 
     * This is done to easily split the parse code in separate parts.
     */
    private static ConceptualSchema _Schema;

    /**
     * Stores the JDOM document used.
     * 
     * This is done to easily split the parse code in separate parts.
     */
    private static Document _Document;

    /**
     * Stores the URL used as based for other files.
     */
    private static URL _BaseURL;

    /**
     * This stores the objects found in the context to be reused in the
     * diagrams.
     */
    private static Hashtable<String, FCAElementImplementation> _Objects;

    /**
     * This stores the attributes found in the context to be reused in the
     * diagrams.
     */
    private static Hashtable<String, FCAElementImplementation> _Attributes;

    /**
     * No public constructor -- the class should not be instantiated.
     */
    private CSXParser() {
        // should not be called
    }

    /**
     * Loads a CSX file.
     * 
     * Most of the parsing is split into subroutines to increase readability.
     * 
     * Currently there is not much error handling in here. E.g. the code might
     * boil out with a NullPointerException if an object definition has no ID.
     * And IDs are not yet checked for uniqueness.
     * 
     * The code might also have problems when namespaces are used.
     */
    public static ConceptualSchema parse(final EventBroker eventBroker,
            final File csxFile) throws IOException, DataFormatException,
            Exception {
        try {
            final SAXBuilder parser = new SAXBuilder();
            _Document = parser.build(csxFile);

            _BaseURL = csxFile.toURI().toURL();
            DatabaseInfo.baseURL = _BaseURL;
            DatabaseViewerManager.setBaseURL(_BaseURL);

            final Element element = _Document.getRootElement();
            if (element.getName().equals("conceptualSchema")) {
                if (element.getAttributeValue("version").equals("TJ0.6")
                        || element.getAttributeValue("version").equals("TJ1.0")
                        || element.getAttributeValue("version").equals("TJ2.0")) {
                    _Schema = new ConceptualSchema(eventBroker, element);
                    _Schema.setLocation(_BaseURL);
                } else {
                    // create data structure
                    _Schema = new ConceptualSchema(eventBroker);

                    // parse the different sections
                    parseDescription();
                    parseContext();
                    parseDiagrams();
                }
            } else {
                throw new DataFormatException(
                "Root element name is not <conceptualSchema>");
            }
            eventBroker.processEvent(new ConceptualSchemaLoadedEvent(
                    _Schema,
                    csxFile));

            _Schema.dataSaved();

            return _Schema;
        } catch (final OutOfMemoryError exc) {
            ErrorDialog.showError(null, exc, "Out of memory",
            "The system ran out of memory while opening a file.");
            return new ConceptualSchema(eventBroker);
        }
    }

    /**
     * Parses the HTML description of the schema (if available).
     */
    private static void parseDescription() {
        final Element descElem = _Document.getRootElement().getChild(
        "description");
        if (descElem != null) {
            _Schema.setDescription(descElem.detach());
        }
    }

    /**
     * Parses the database section of the file.
     */
    private static void parseDatabaseInformation(final Element contextElem)
    throws DataFormatException {
        // check if database should be used and fetch the data if needed
        final Element dbElem = contextElem.getChild("databaseConnection");
        if (dbElem == null) {
            _Schema.addQuery(AggregateQuery.COUNT_QUERY);
            _Schema.addQuery(ListQuery.KEY_LIST_QUERY);
            _Schema.addQuery(AggregateQuery.PERCENT_QUERY);
            return;
        }
        final DatabaseInfo dbInfo = new DatabaseInfo();
        parseDBInfo(dbInfo, dbElem);
        _Schema.setDatabaseInfo(dbInfo);
        final Element viewsElem = dbElem.getChild("views");
        if (viewsElem != null) {
            parseDatabaseObjectViewerSetups(viewsElem);
            parseDatabaseObjectListViewerSetups(viewsElem);
            parseDatabaseAttributeViewerSetups(viewsElem);
        }
    }

    private static void parseDatabaseObjectViewerSetups(final Element viewsElem)
    throws DataFormatException {
        final List<Element> viewerElems = viewsElem.getChildren("objectView");
        final Iterator<Element> it = viewerElems.iterator();
        while (it.hasNext()) {
            final Element viewerElem = it.next();
            try {
                new DatabaseViewerManager(viewerElem,
                        _Schema.getDatabaseInfo(), DatabaseConnection
                        .getConnection());
            } catch (final DatabaseViewerException e) {
                throw new DataFormatException(
                        "A database viewer could not be initialized.", e);
            }
        }
    }

    private static void parseDatabaseObjectListViewerSetups(
            final Element viewsElem) throws DataFormatException {
        final List<Element> viewerElems = viewsElem
        .getChildren("objectListView");
        final Iterator<Element> it = viewerElems.iterator();
        while (it.hasNext()) {
            final Element viewerElem = it.next();
            try {
                new DatabaseViewerManager(viewerElem,
                        _Schema.getDatabaseInfo(), DatabaseConnection
                        .getConnection());
            } catch (final DatabaseViewerException e) {
                throw new DataFormatException(
                        "A database viewer could not be initialized.", e);
            }
        }
    }

    private static void parseDatabaseAttributeViewerSetups(
            final Element viewsElem) throws DataFormatException {
        final List<Element> viewerElems = viewsElem
        .getChildren("attributeView");
        final Iterator<Element> it = viewerElems.iterator();
        while (it.hasNext()) {
            final Element viewerElem = it.next();
            try {
                new DatabaseViewerManager(viewerElem,
                        _Schema.getDatabaseInfo(), DatabaseConnection
                        .getConnection());
            } catch (final DatabaseViewerException e) {
                throw new DataFormatException(
                        "A database viewer could not be initialized.", e);
            }
        }
    }

    /**
     * Parses the context in the file.
     */
    private static void parseContext() throws DataFormatException {
        final Element contextElem = _Document.getRootElement().getChild(
        "context");
        if (contextElem == null) {
            throw new DataFormatException("No <context> defined");
        }

        // parse the database connection if given
        parseDatabaseInformation(contextElem);

        // build hashtable for objects
        List<Element> elements = contextElem.getChildren("object");
        _Objects = new Hashtable<String, FCAElementImplementation>(elements
                .size());

        Iterator<Element> it = elements.iterator();
        while (it.hasNext()) {
            final Element object = it.next();
            if (object.getText().length() != 0) {
                _Objects.put(object.getAttribute("id").getValue(),
                        new FCAElementImplementation(object.getText()));
            }
        }

        // build hashtable for attributes
        elements = _Document.getRootElement().getChild("context").getChildren(
        "attribute");
        _Attributes = new Hashtable<String, FCAElementImplementation>(elements
                .size());

        it = elements.iterator();
        while (it.hasNext()) {
            final Element attribute = it.next();
            final org.jdom.Attribute name = attribute.getAttribute("name");
            if ((name != null) && (name.getValue() != null)
                    && (name.getValue().length() != 0)) {
                Element descriptionElem = attribute.getChild("description");
                if (descriptionElem != null) {
                    descriptionElem = descriptionElem.detach();
                }
                _Attributes.put(attribute.getAttribute("id").getValue(),
                        new FCAElementImplementation(name.getValue(),
                                descriptionElem));
            }
        }
    }

    /**
     * Parses the diagrams in the file.
     */
    private static void parseDiagrams() throws DataFormatException {
        // find and store diagrams
        final List<Element> elements = _Document.getRootElement().getChildren(
        "diagram");
        final Iterator<Element> it = elements.iterator();
        while (it.hasNext()) {
            final Element diagElem = it.next();
            final SimpleLineDiagram diagram = new SimpleLineDiagram();

            // set the title of the diagram
            diagram.setTitle(diagElem.getAttribute("title").getValue());

            final Element descElem = diagElem.getChild("description");
            if (descElem != null) {
                diagram.setDescription(descElem.detach());
            }

            // build a list of concepts (as points in the diagram and as
            // Hashtable for building the edges later)
            final List<Element> concepts = diagElem.getChildren("concept");
            final Hashtable<String, DiagramNode> nodes = new Hashtable<String, DiagramNode>(
                    elements.size());
            Iterator<Element> it2 = concepts.iterator();
            while (it2.hasNext()) {
                final Element conceptElem = it2.next();

                // get the position
                final Element posElem = conceptElem.getChild("position");
                Point2D position;
                try {
                    position = new Point2D.Double(posElem.getAttribute("x")
                            .getDoubleValue(), posElem.getAttribute("y")
                            .getDoubleValue());
                } catch (final DataConversionException e) {
                    throw new DataFormatException(
                    "Position of some concept does not contain double.");
                }

                // create the concept
                final Concept concept = parseConcept(conceptElem);

                // parse the label layout information if needed
                final LabelInfo objLabel = new LabelInfo();
                // don't use the Concept.getObjectContingentSize() method here,
                // it might cause DB calls
                if (conceptElem.getChild("objectContingent").getChildren(
                "objectRef").size() != 0) {
                    final Element style = conceptElem.getChild(
                    "objectContingent").getChild("labelStyle");
                    if (style != null) {
                        parseLabelStyle(objLabel, style);
                    }
                }

                final LabelInfo attrLabel = new LabelInfo();
                if (conceptElem.getChild("attributeContingent").getChildren(
                "attributeRef").size() != 0) {
                    final Element style = conceptElem.getChild(
                    "attributeContingent").getChild("labelStyle");
                    if (style != null) {
                        parseLabelStyle(attrLabel, style);
                    }
                }
                final String identifier = conceptElem.getAttribute("id")
                .getValue();

                // create the node
                final DiagramNode node = new DiagramNode(diagram, identifier,
                        position, concept, attrLabel, objLabel, null);

                // put in into the diagram
                diagram.addNode(node);

                // store the node for later retrieval (lines)
                nodes.put(identifier, node);
            }

            // get the edges and map them to the points
            final List<Element> edges = diagElem.getChildren("edge");
            it2 = edges.iterator();
            while (it2.hasNext()) {
                final Element edge = it2.next();
                final DiagramNode from = nodes.get(edge.getAttribute("from")
                        .getValue());
                final DiagramNode to = nodes.get(edge.getAttribute("to")
                        .getValue());
                diagram.addLine(from, to);

                // add direct neighbours to concepts
                final ConceptImplementation concept1 = (ConceptImplementation) from
                .getConcept();
                final ConceptImplementation concept2 = (ConceptImplementation) to
                .getConcept();
                concept1.addSubConcept(concept2);
                concept2.addSuperConcept(concept1);
            }

            // build transitive closures for each concept
            for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
                ((ConceptImplementation) diagram.getNode(i).getConcept())
                .buildClosures();
            }

            _Schema.addDiagram(diagram);
        }
    }

    /**
     * Creates a concept for DB access from the information in the given XML
     * element.
     */
    private static Concept parseConcept(final Element conceptElem) {
        // create the concept
        final ConceptImplementation concept = new ConceptImplementation();

        // get the object contingent
        Element contElem = conceptElem.getChild("objectContingent");
        List<Element> contingent = contElem.getChildren("objectRef");
        Iterator<Element> iterator = contingent.iterator();
        while (iterator.hasNext()) {
            final Element ref = iterator.next();
            final FCAElement object = _Objects.get(ref.getText());
            if (object != null) {
                concept.addObject(object);
            }
        }

        // get the attribute contingent
        contElem = conceptElem.getChild("attributeContingent");
        contingent = contElem.getChildren("attributeRef");
        iterator = contingent.iterator();
        while (iterator.hasNext()) {
            final Element ref = iterator.next();
            final FCAElement attr = _Attributes.get(ref.getText());
            if (attr != null) {
                concept.addAttribute(attr);
            }
        }
        return concept;
    }

    /**
     * Parses the information for a single label in a diagram.
     * 
     * This method parses all the information found for a label style. The JDOM
     * element given is assumed to be a labelStyle element, the information
     * found is put into the other parameter.
     */
    private static void parseLabelStyle(final LabelInfo label,
            final Element styleElement) throws DataFormatException {
        Element el = styleElement.getChild("offset");
        if (el != null) {
            try {
                label.setOffset(new Point2D.Double(el.getAttribute("x")
                        .getDoubleValue(), el.getAttribute("y")
                        .getDoubleValue()));
            } catch (final DataConversionException e) {
                throw new DataFormatException(
                "Offset of some label does not contain double.");
            }
        }
        el = styleElement.getChild("textColor");
        if (el != null) {
            label.setTextColor(Color.decode(el.getText()));
        }
        el = styleElement.getChild("bgColor");
        if (el != null) {
            label.setBackgroundColor(Color.decode(el.getText()));
        }
        el = styleElement.getChild("textAlignment");
        if (el != null) {
            final String text = el.getText();
            if (text.compareTo("center") == 0) {
                label.setTextAlignment(LabelInfo.ALIGNCENTER);
            } else if (text.compareTo("right") == 0) {
                label.setTextAlignment(LabelInfo.ALIGNRIGHT);
            } else if (text.compareTo("left") == 0) {
                label.setTextAlignment(LabelInfo.ALIGNLEFT);
            } else {
                throw new DataFormatException("Unknown text alignment");
            }
        }
    }

    /**
     * Parses all database related information from within the given element.
     * 
     * The element is supposed to be a <database> element according to the XML
     * Schema.
     */
    private static void parseDBInfo(final DatabaseInfo dbInfo,
            final Element dbElement) throws DataFormatException {
        // try to find the different possible information
        final Element urlElem = dbElement.getChild("url");
        final Element embedElem = dbElement.getChild("embed");
        if ((urlElem == null) && (embedElem == null)) {
            throw new DataFormatException(
            "Either <url> or <embed> expected in <databaseConnection> element.");
        }
        if ((urlElem != null) && (embedElem != null)) {
            throw new DataFormatException(
            "Only one of <url> and <embed> expected in <databaseConnection> element.");
        }

        String url;
        String driver;
        String username;
        String password;
        String embeddedDBlocation;
        if (urlElem != null) {
            url = urlElem.getTextNormalize();
            driver = urlElem.getAttributeValue("driver");
            username = urlElem.getAttributeValue("user");
            password = urlElem.getAttributeValue("password");
            embeddedDBlocation = null;
        } else {
            assert embedElem != null; // either one should be set as checked
            // above
            final DatabaseInfo tmpInfo = DatabaseInfo.getEmbeddedDatabaseInfo();
            url = tmpInfo.getURL();
            driver = tmpInfo.getDriverClass();
            username = tmpInfo.getUserName();
            password = tmpInfo.getPassword();
            embeddedDBlocation = embedElem.getAttributeValue("url");
            dbInfo.setEmbeddedSQLLocation(embeddedDBlocation);
        }

        dbInfo.setUrl(url);
        if (driver != null) {
            // try to load the driver
            try {
                Class.forName(driver);
            } catch (final ClassNotFoundException e) {
                throw new DataFormatException("Could not load class \""
                        + driver + "\" as database driver.");
            }
        }
        dbInfo.setDriverClass(driver);
        dbInfo.setUserName(username);
        dbInfo.setPassword(password);

        // let's try to find the query
        Element elem = dbElement.getChild("table");
        if (elem == null) {
            throw new DataFormatException(
            "No <table> given for <databaseConnection>");
        }
        dbInfo.setTable(new Table(elem.getText(), false));
        elem = dbElement.getChild("key");
        if (elem == null) {
            throw new DataFormatException(
            "<table> but not <key> given in <databaseConnection> element");
        }
        final String keyName = elem.getText();
        dbInfo.setKey(new Column(keyName, Types.VARCHAR, dbInfo.getTable()));
        // check for additional queries
        final Element queryElem = dbElement.getChild("queries");
        if (queryElem == null || queryElem.getAttribute("dropDefaults") == null
                || queryElem.getAttributeValue("dropDefaults").equals("false")) {
            // add default queries
            Query query = dbInfo.createAggregateQuery("Number of Objects", "");
            query.insertQueryColumn("Count", "0", null, "count(*)", false);
            _Schema.addQuery(query);
            query = dbInfo.createListQuery("List of Objects", "", false);
            query.insertQueryColumn("Object Name", null, null, keyName, false);
            _Schema.addQuery(query);
        }
        if (queryElem != null) {
            Iterator<Element> it = queryElem.getChildren("listQuery")
            .iterator();
            while (it.hasNext()) {
                final Element cur = it.next();
                final String name = cur.getAttributeValue("name");
                final String header = cur.getAttributeValue("head");
                final String distinct = cur.getAttributeValue("distinct");
                final boolean isDistinct = (distinct != null)
                && (distinct.equals("true"));
                final Query query = dbInfo.createListQuery(name, header,
                        isDistinct);
                final Iterator<Element> it2 = cur.getChildren("column")
                .iterator();
                while (it2.hasNext()) {
                    final Element curCol = it2.next();
                    final String colName = curCol.getAttributeValue("name");
                    final String format = curCol.getAttributeValue("format");
                    final String separator = curCol
                    .getAttributeValue("separator");
                    final String sql = curCol.getText();
                    query.insertQueryColumn(colName, format, separator, sql,
                            false);
                }
                _Schema.addQuery(query);
            }
            it = queryElem.getChildren("aggregateQuery").iterator();
            while (it.hasNext()) {
                final Element cur = it.next();
                final String name = cur.getAttributeValue("name");
                final String header = cur.getAttributeValue("header");
                final Query query = dbInfo.createAggregateQuery(name, header);
                final Iterator<Element> it2 = cur.getChildren("column")
                .iterator();
                while (it2.hasNext()) {
                    final Element curCol = it2.next();
                    final String colName = curCol.getAttributeValue("name");
                    final String format = curCol.getAttributeValue("format");
                    final String separator = curCol
                    .getAttributeValue("separator");
                    final String sql = curCol.getText();
                    query.insertQueryColumn(colName, format, separator, sql,
                            false);
                }
                _Schema.addQuery(query);
            }
        }
    }
}
