/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.parser;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.tockit.conscript.model.*;
import org.tockit.conscript.model.AbstractScale;
import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.model.FCAObject;
import org.tockit.conscript.model.Line;
import org.tockit.conscript.model.LineDiagram;
import org.tockit.conscript.model.Point;
import org.tockit.conscript.model.QueryMap;
import org.tockit.conscript.model.StringMap;
import org.tockit.conscript.parser.DataFormatException;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

/**
 * @todo this is not really a parser, it should be a class using the parser
 *       instead of deriving from it.
 */
public class CSCParser extends org.tockit.conscript.parser.CSCParser {
    private static final int TARGET_DIAGRAM_HEIGHT = 460;
    private static final int TARGET_DIAGRAM_WIDTH = 600;
    
    public void importCSCFile(File file, ConceptualSchema schema) 
    								throws DataFormatException, FileNotFoundException {
        try {
            CSCFile cscFile = importCSCFile(file.toURI().toURL(), null);
            List concreteScales = cscFile.getConcreteScales();
            for (Iterator iter = concreteScales.iterator(); iter.hasNext();) {
                ConcreteScale scale = (ConcreteScale) iter.next();
                Diagram2D diagram2D = createDiagram2D(scale);
                rescale(diagram2D);
                schema.addDiagram(diagram2D);
            }
            
            if(!cscFile.getDatabaseDefinitions().isEmpty()) {
                // if there is at least one DB definition, we use the first for the CSX
                // multiple ones are unlikely and we wouldn't know what to do then anyway
                DatabaseDefinition dbDef = (DatabaseDefinition) cscFile.getDatabaseDefinitions().get(0);
                DatabaseInfo dbInfo = new DatabaseInfo();
                dbInfo.setOdbcDataSource(dbDef.getDatabaseName(), null, null);
                Table table = new Table(dbDef.getTable(), false);
                dbInfo.setTable(table);
                dbInfo.setKey(new Column(dbDef.getPrimaryKey(), 0, table));
                schema.setDatabaseInfo(dbInfo);
            }
        } catch (MalformedURLException e) {
            // should not happen
            e.printStackTrace();
            throw new RuntimeException("Internal error", e);
        }
    }

    private Diagram2D createDiagram2D(ConcreteScale scale) {
        AbstractScale abstractScale = scale.getAbstractScale();
        StringMap attributeMap = scale.getAttributeMap();
        QueryMap queryMap = scale.getQueryMap();
        
        // we always just take the first diagram -- multiple diagrams are hardly used anywhere
        LineDiagram diagram = (LineDiagram) abstractScale.getLineDiagrams().get(0);

        SimpleLineDiagram result = new SimpleLineDiagram();
        if(scale.getTitle()!= null) {
            result.setTitle(scale.getTitle().getContent());
        } else {
            result.setTitle(scale.getName());
        }
        
        List points = diagram.getPoints();
        for (Iterator iter = points.iterator(); iter.hasNext();) {
            Point point = (Point) iter.next();

            String identifier = "" + point.getNumber();
            Point2D position = new Point2D.Double(point.getX(), point.getY());
            ConceptImplementation concept = new ConceptImplementation();
            
            DiagramNode node = new DiagramNode(result, identifier, position, concept, null, null, null);
            result.addNode(node);
        }
        
        List lines = diagram.getLines();
        for (Iterator iter = lines.iterator(); iter.hasNext();) {
            Line line = (Line) iter.next();
            Point from = line.getFrom();
            Point to = line.getTo();
            DiagramNode fromNode = result.getNode("" + from.getNumber());
            DiagramNode toNode = result.getNode("" + to.getNumber());
            result.addLine(fromNode, toNode);

            ConceptImplementation fromConcept = (ConceptImplementation) fromNode.getConcept();
            ConceptImplementation toConcept = (ConceptImplementation) toNode.getConcept();
            fromConcept.addSubConcept(toNode.getConcept());
            toConcept.addSuperConcept(fromConcept);
        }
        
        List objects = diagram.getObjects();
        for (Iterator iter = objects.iterator(); iter.hasNext();) {
            FCAObject object = (FCAObject) iter.next();
            FCAElementImplementation resultObject;
            DiagramNode node = result.getNode("" + object.getPoint().getNumber());

            if(queryMap.getQuery(object.getIdentifier()) != null) {
                resultObject = new FCAElementImplementation(queryMap.getQuery(object.getIdentifier()));
            } else {
                resultObject = new FCAElementImplementation(object.getIdentifier());
            }

            ConceptImplementation concept = (ConceptImplementation) node.getConcept();
            concept.addObject(resultObject);
            
            LabelInfo labelInfo = createLabelInfo(object.getDescription().getFormat());
            node.setObjectLabelInfo(labelInfo);
        }
        
        List attributes = diagram.getAttributes();
        for (Iterator iter = attributes.iterator(); iter.hasNext();) {
            FCAAttribute attribute = (FCAAttribute) iter.next();
            FCAElementImplementation resultAttribute;
            DiagramNode node = result.getNode("" + attribute.getPoint().getNumber());

            FormattedString label = attributeMap.getLabel(attribute.getIdentifier());
            if(label != null) {
                resultAttribute = new FCAElementImplementation(label.getContent());
            } else {
                resultAttribute = new FCAElementImplementation(attribute.getIdentifier());
            }

            ConceptImplementation concept = (ConceptImplementation) node.getConcept();
            concept.addAttribute(resultAttribute);
            
            LabelInfo labelInfo = createLabelInfo(attribute.getDescription().getFormat());
            node.setAttributeLabelInfo(labelInfo);
        }

        for (Iterator iter = result.getNodes(); iter.hasNext();) {
            DiagramNode node = (DiagramNode) iter.next();
            ConceptImplementation concept = (ConceptImplementation) node.getConcept();
            concept.buildClosures();
        }
        
        return result;
    }

    private LabelInfo createLabelInfo(StringFormat format) {
        LabelInfo retVal = new LabelInfo();
        retVal.setOffset(format.getOffset().getX(), -format.getOffset().getY());
        if(format.getHorizontalAlign() == StringFormat.LEFT) {
            retVal.setTextAlignment(LabelInfo.ALIGNLEFT);
        } else if(format.getHorizontalAlign() == StringFormat.H_CENTER) {
            retVal.setTextAlignment(LabelInfo.ALIGNCENTER);
        } else if(format.getHorizontalAlign() == StringFormat.RIGHT) {
            retVal.setTextAlignment(LabelInfo.ALIGNRIGHT);
        }
        return retVal;
    }

    private void rescale(Diagram2D diagram) {
    	Rectangle2D bounds = diagram.getBounds();
        
        double scaleX;
        if(bounds.getWidth() == 0) {
            scaleX = Double.MAX_VALUE;
        } else {
            scaleX = TARGET_DIAGRAM_WIDTH / bounds.getWidth();
        }
        
        double scaleY;
        if(bounds.getHeight() == 0) {
            scaleY = Double.MAX_VALUE;
        } else {
            scaleY = TARGET_DIAGRAM_HEIGHT / bounds.getHeight();
        }
        
        double scale = (scaleX < scaleY) ? scaleX : scaleY;
        
        Iterator it = diagram.getNodes();
        while (it.hasNext()) {
            DiagramNode node = (DiagramNode) it.next();
            Point2D pos = node.getPosition();
            node.setPosition(new Point2D.Double(scale * pos.getX(), scale * pos.getY()));

            LabelInfo aLabel = node.getAttributeLabelInfo();
            if(aLabel != null) {
                Point2D offset = aLabel.getOffset();
                aLabel.setOffset(new Point2D.Double(scale * offset.getX(), scale * offset.getY()));
            }

            LabelInfo oLabel = node.getObjectLabelInfo();
            if(oLabel != null) {
                Point2D offset = oLabel.getOffset();
                oLabel.setOffset(new Point2D.Double(scale * offset.getX(), scale * offset.getY()));
            }
        }
    }
}
