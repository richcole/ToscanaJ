package net.sourceforge.toscanaj.parser;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.model.lattice.MemoryMappedConcept;
import net.sourceforge.toscanaj.model.diagram.AttributeLabelInfo;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.ObjectLabelInfo;

import java.awt.Color;
import java.awt.geom.Point2D;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.adapters.DOMAdapter;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;

/**
 * This class reads a CSX file and does nothing with it except complaining.
 *
 * @TODO: make code more stable and give more error messages.
 */
public class CSXParser
{
    /**
     * Stores the schema that is created.
     *
     * This is done to easily split the parse code in separate parts.
     */
    private static ConceptualSchema _Schema;

    /**
     * The database connection is created here and attached to each concept
     * if a database is used.
     */
    private static DBConnection _DatabaseConnection = null;

    /**
     * Stores the JDOM document used.
     *
     * This is done to easily split the parse code in separate parts.
     */
    private static Document _Document;

    /**
     * This stores the objects found in the context to be reused in the
     * diagrams.
     */
    private static Hashtable _Objects;

    /**
     * This stores the attributes found in the context to be reused in the
     * diagrams.
     */
    private static Hashtable _Attributes;

    /**
     * No public constructor -- the class should not be instantiated.
     */
    private CSXParser() {
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
    public static ConceptualSchema parse( File csxFile )
            throws FileNotFoundException, IOException, DataFormatException
    {
        // open stream on file
        FileInputStream in;
        in = new FileInputStream( csxFile );

        // parse schema with Xerxes
        DOMAdapter domAdapter = new org.jdom.adapters.XercesDOMAdapter();
        org.w3c.dom.Document w3cdoc = domAdapter.getDocument( in, false );

        // create JDOM document
        DOMBuilder builder =
                        new DOMBuilder( "org.jdom.adapters.XercesDOMAdapter" );
        _Document = builder.build( w3cdoc );

        // create data structure
        _Schema = new ConceptualSchema();

        // parse the different sections
        parseDatabaseInformation();
        parseContext();
        parseDiagrams();

        return _Schema;
    }

    /**
     * Parses the database section of the file.
     */
    private static void parseDatabaseInformation()
        throws DataFormatException
    {
        // check if database should be used and fetch the data if needed
        Attribute askDB = _Document.getRootElement()
                                    .getAttribute( "askDatabase" );
        if( askDB != null )
        {
            if( askDB.getValue().compareTo("true") == 0 )
            {
                _Schema.setUseDatabase( true );
                DatabaseInfo dbInfo = new DatabaseInfo();
                _Schema.setDatabaseInformation(dbInfo);
                Element dbElem = _Document.getRootElement().getChild("database");
                if( dbElem != null ) {
                    parseDBInfo(dbInfo, dbElem);
                    _DatabaseConnection = new DBConnection(dbInfo.getSource());
                }
            }
        }
    }

    /**
     * Parses the context in the file.
     */
    private static void parseContext()
    {
        // build hashtable for objects
        List elements = _Document.getRootElement()
                                    .getChild("context").getChildren("object");
        _Objects = new Hashtable( elements.size() );

        Iterator it = elements.iterator();
        while( it.hasNext() )
        {
            Element object = (Element) it.next();
            if(object.getText().length() != 0) {
                _Objects.put( object.getAttribute( "id" ).getValue(),
                             object.getText() );
            }
        }

        // build hashtable for attributes
        elements = _Document.getRootElement()
                                .getChild("context").getChildren("attribute");
        _Attributes = new Hashtable( elements.size() );

        it = elements.iterator();
        while( it.hasNext() )
        {
            Element attribute = (Element) it.next();
            if(attribute.getText().length() != 0) {
                _Attributes.put( attribute.getAttribute( "id" ).getValue(),
                             attribute.getText() );
            }
        }
    }

    /**
     * Parses the diagrams in the file.
     */
    private static void parseDiagrams() throws DataFormatException
    {
        // find and store diagrams
        List elements = _Document.getRootElement().getChildren( "diagram" );
        Iterator it = elements.iterator();
        while( it.hasNext() )
        {
            Element diagElem = (Element) it.next();
            SimpleLineDiagram diagram = new SimpleLineDiagram();

            // set the title of the diagram
            diagram.setTitle( diagElem.getAttribute( "title" ).getValue() );

            // build a list of concepts (as points in the diagram and as
            // Hashtable for building the edges later)
            List concepts = diagElem.getChildren( "concept" );
            Hashtable nodes = new Hashtable( elements.size() );
            Iterator it2 = concepts.iterator();
            int number = 0; // for counting the points
            while( it2.hasNext() )
            {
                Element conceptElem = (Element)it2.next();

                // get the position
                Element posElem = conceptElem.getChild( "position" );
                Point2D position;
                try
                {
                    position = new Point2D.Double(
                                posElem.getAttribute( "x" ).getDoubleValue(),
                                posElem.getAttribute( "y" ).getDoubleValue() );
                }
                catch ( DataConversionException e )
                {
                    /** @TODO: give more info here */
                    throw new DataFormatException(
                          "Position of some concept does not contain double." );
                }

                // create the concept for DB or in-memory access
                Concept concept;
                if( _Schema.usesDatabase() ) {
                    concept = parseDBConcept(conceptElem);
                }
                else {
                    concept = parseInMemoryConcept(conceptElem);
                }

                // parse the label layout information if needed
                LabelInfo objLabel = new ObjectLabelInfo();
                // don't use the Concept.getObjectContingentSize() method here, it might cause DB calls
                if(conceptElem.getChild("objectContingent").getChildren("objectRef").size() != 0) {
                    Element style = conceptElem.getChild("objectContingent").
                                                getChild( "labelStyle" );
                    if( style != null )
                    {
                        parseLabelStyle(objLabel, style);
                    }
                }

                LabelInfo attrLabel = new AttributeLabelInfo();
                if(conceptElem.getChild("attributeContingent").getChildren("attributeRef").size() != 0) {
                    Element style = conceptElem.getChild( "attributeContingent" ).
                                                getChild( "labelStyle" );
                    if( style != null )
                    {
                        parseLabelStyle(attrLabel, style);
                    }
                }

                // create the node
                DiagramNode node = new DiagramNode(position,concept,attrLabel,objLabel);

                // put in into the diagram
                diagram.addNode(node);

                // store the node for later retrieval (lines)
                nodes.put( conceptElem.getAttribute( "id" ).getValue(), node );

                // increase counter (not above since it is used in some places)
                number++;
            }

            // get the edges and map them to the points
            List edges = diagElem.getChildren( "edge" );
            it2 = edges.iterator();
            while( it2.hasNext() )
            {
                Element edge = (Element) it2.next();
                DiagramNode from = (DiagramNode) nodes.get(
                                    edge.getAttribute( "from" ).getValue());
                DiagramNode to   = (DiagramNode) nodes.get(
                                    edge.getAttribute( "to" ).getValue());
                diagram.addLine( from, to );

                // add direct neighbours to concepts
                AbstractConceptImplementation concept1 =
                                 (AbstractConceptImplementation) from.getConcept();
                AbstractConceptImplementation concept2 =
                                 (AbstractConceptImplementation) to.getConcept();
                concept1.addSubConcept(concept2);
                concept2.addSuperConcept(concept1);
            }

            // build transitive closures for each concept
            for(int i = 0; i < diagram.getNumberOfNodes(); i++) {
                ((AbstractConceptImplementation) diagram.getNode(i).getConcept()).buildClosures();
            }

            _Schema.addDiagram( diagram );
        }
    }

    /**
     * Creates a concept for DB access from the information in the given
     * XML element.
     */
    private static Concept parseDBConcept(Element conceptElem) {
        // create the concept
        DatabaseConnectedConcept concept =
                    new DatabaseConnectedConcept(_Schema.getDatabaseInfo(),
                                                 _DatabaseConnection);

        // get the object contingent
        Element contElem = conceptElem.getChild( "objectContingent" );
        List contingent = contElem.getChildren( "objectRef" );
        Iterator it3 = contingent.iterator();
        String query = null;
        if( it3.hasNext() ) {
            query = "";
        }
        while( it3.hasNext() )
        {
            Element ref = (Element) it3.next();
            String objClause = (String) _Objects.get( ref.getText() );
            if(objClause != null) {
                query = query + "(" + objClause + ")";
                if( it3.hasNext() ) {
                    query = query + " OR ";
                }
            }
        }
        if( query != null && query.length() != 0 ) {
            concept.setObjectClause(query);
        }
        else {
            concept.setObjectClause(null);
        }

        // get the attribute contingent
        contElem = conceptElem.getChild( "attributeContingent" );
        contingent = contElem.getChildren( "attributeRef" );
        it3 = contingent.iterator();
        while( it3.hasNext() )
        {
            Element ref = (Element) it3.next();
            String attr = (String)_Attributes.get( ref.getText() );
            if(attr != null) {
                concept.addAttribute(attr);
            }
        }
        return concept;
    }

    /**
     * Creates a concept to be stored in memory from the information in the given
     * XML element.
     */
    private static Concept parseInMemoryConcept(Element conceptElem) {
        // create the concept
        MemoryMappedConcept concept = new MemoryMappedConcept();

        // get the object contingent
        Element contElem = conceptElem.getChild( "objectContingent" );
        List contingent = contElem.getChildren( "objectRef" );
        Iterator it3 = contingent.iterator();
        while( it3.hasNext() )
        {
            Element ref = (Element) it3.next();
            String obj = (String)_Attributes.get( ref.getText() );
            if(obj != null) {
                concept.addObject(obj);
            }
        }

        // get the attribute contingent
        contElem = conceptElem.getChild( "attributeContingent" );
        contingent = contElem.getChildren( "attributeRef" );
        it3 = contingent.iterator();
        while( it3.hasNext() )
        {
            Element ref = (Element) it3.next();
            String attr = (String)_Attributes.get( ref.getText() );
            if(attr != null) {
                concept.addAttribute(attr);
            }
        }
        return concept;
    }

    /**
     * Parses the information for a single label in a diagram.
     *
     * This method parses all the information found for a label style. The
     * JDOM element given is assumed to be a labelStyle element, the information
     * found is put into the other parameter.
     */
    private static void parseLabelStyle( LabelInfo label, Element styleElement )
                             throws DataFormatException
    {
        Element el = styleElement.getChild( "offset" );
        if( el != null )
        {
            try
            {
                label.setOffset( new Point2D.Double(
                    el.getAttribute("x").getDoubleValue(),
                    el.getAttribute("y").getDoubleValue() ) );
            }
            catch( DataConversionException e )
            {
                /** @TODO: give more info here */
                throw new DataFormatException(
                      "Offset of some label does not contain double." );
            }
        }
        el = styleElement.getChild( "textColor" );
        if( el != null )
        {
            label.setTextColor( Color.decode( el.getText() ) );
        }
        el = styleElement.getChild( "bgColor" );
        if( el != null )
        {
            label.setBackgroundColor(
                                  Color.decode( el.getText() ) );
        }
        el = styleElement.getChild( "textAlignment" );
        if( el != null )
        {
            String text = el.getText();
            if( text.compareTo( "center" ) == 0 )
            {
                label.setTextAligment( LabelInfo.ALIGNCENTER );
            }
            else if( text.compareTo( "right" ) == 0 )
            {
                label.setTextAligment( LabelInfo.ALIGNRIGHT );
            }
            else if( text.compareTo( "left" ) == 0 )
            {
                label.setTextAligment( LabelInfo.ALIGNLEFT );
            }
            else
            {
                /** @TODO: give more info here */
                throw new DataFormatException("Unknown text alignment");
            }
        }
    }

    /**
     * Parses all database related information from within the given element.
     *
     * The element is supposed to be a <database> element according to the
     * XML Schema.
     */
    private static void parseDBInfo(DatabaseInfo dbInfo, Element dbElement)
        throws DataFormatException
    {
        // try to find a DSN first
        Element elem = dbElement.getChild("dsn");
        if( elem != null ) {
            // we have DSN, set it
            dbInfo.setDSN(elem.getText());
        }
        else {
            // look for path information
            elem = dbElement.getChild("path");
            if( elem == null ) {
                // since <database> was given we expected one of the two
                throw new DataFormatException("Either <dsn> or <path> expected in <database> element");
            }
            dbInfo.setDatabaseFile(elem.getText());
        }
        // let's try to find the query
        elem = dbElement.getChild("query");
        if( elem != null ) {
            // found query
            dbInfo.setQuery(elem.getText());
        }
        else {
            // need table and key
            elem = dbElement.getChild("table");
            if( elem == null ) {
                throw new DataFormatException("Neither <query> nor <table> given for <database>");
            }
            String table = elem.getText();
            elem = dbElement.getChild("key");
            if( elem == null ) {
                throw new DataFormatException("<table> but not <key> given in <database> element");
            }
            dbInfo.setQuery(table, elem.getText());
        }
    }
}