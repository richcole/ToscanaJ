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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
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
import net.sourceforge.toscanaj.model.lattice.Lattice;

import org.tockit.conscript.model.AbstractScale;
import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.model.ConscriptStructure;
import org.tockit.conscript.model.DatabaseDefinition;
import org.tockit.conscript.model.FCAAttribute;
import org.tockit.conscript.model.FCAObject;
import org.tockit.conscript.model.FormalContext;
import org.tockit.conscript.model.FormattedString;
import org.tockit.conscript.model.Line;
import org.tockit.conscript.model.LineDiagram;
import org.tockit.conscript.model.Point;
import org.tockit.conscript.model.QueryMap;
import org.tockit.conscript.model.StringFormat;
import org.tockit.conscript.model.StringMap;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.DataFormatException;
import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.Context;

public class CSCImport {
    private static final int TARGET_DIAGRAM_HEIGHT = 460;
    private static final int TARGET_DIAGRAM_WIDTH = 600;

    public void importCSCFile(final File file, final ConceptualSchema schema)
    throws DataFormatException, FileNotFoundException {
        try {
            final CSCFile cscFile = CSCParser.importCSCFile(file.toURI()
                    .toURL(), null);
            // used to avoid creating diagrams for both a LINEDIAGRAM and a
            // CONCRETE_SCALE entry
            final Set<String> namesOfCreatedDiagrams = new HashSet<String>();

            final List<ConscriptStructure> lineDiagrams = cscFile
            .getLineDiagrams();
            for (final Object element : lineDiagrams) {
                final LineDiagram lineDiagram = (LineDiagram) element;
                final Map<String, String> objectLabels = new HashMap<String, String>();
                for (final FCAObject object : lineDiagram.getObjects()) {
                    objectLabels.put(object.getIdentifier(), object
                            .getDescription().getContent());
                }
                final Map<String,FormattedString> attributeLabels = new HashMap<String,FormattedString>();
                for (final FCAAttribute attribute : lineDiagram.getAttributes()) {
                    attributeLabels.put(attribute.getIdentifier(), attribute
                            .getDescription());
                }
                final String name = (lineDiagram.getTitle() != null) ? lineDiagram
                        .getTitle().getContent()
                        : lineDiagram.getName();
                        // we fake having StringMaps or QueryMaps -- very much a hack
                        final Diagram2D diagram2D = createDiagram2D(name, lineDiagram,
                                new StringMap("fake") {
                            @Override
                            public FormattedString getLabel(final String entry) {
                                return attributeLabels.get(entry);
                            }
                        }, new QueryMap("fake") {
                            @Override
                            public String getQuery(final String abstractObjectId) {
                                return objectLabels.get(abstractObjectId);
                            }
                        });
                        rescale(diagram2D);
                        schema.addDiagram(diagram2D);
                        namesOfCreatedDiagrams.add(name);
            }

            final List<ConscriptStructure> concreteScales = cscFile
            .getConcreteScales();
            for (final Object element : concreteScales) {
                final ConcreteScale scale = (ConcreteScale) element;
                final String scaleName = (scale.getTitle() != null) ? scale
                        .getTitle().getContent() : scale.getName();
                        if (!namesOfCreatedDiagrams.contains(scaleName)) {
                            final Diagram2D diagram2D = createDiagram2D(scale,
                                    scaleName);
                            rescale(diagram2D);
                            schema.addDiagram(diagram2D);
                            namesOfCreatedDiagrams.add(scaleName);
                        }
            }

            final List<ConscriptStructure> formalContexts = cscFile
            .getFormalContexts();
            for (final ConscriptStructure structure : formalContexts) {
                final FormalContext formalContext = (FormalContext) structure;
                final String name = (formalContext.getTitle() != null) ? formalContext
                        .getTitle().getContent()
                        : formalContext.getName();
                        if (!namesOfCreatedDiagrams.contains(name)) {
                            final Context<FCAObject, FCAAttribute> context = new Context<FCAObject, FCAAttribute>() {
                                public Set<FCAAttribute> getAttributes() {
                                    return new HashSet(formalContext.getAttributes());
                                }
                                public String getName() {
                                    return name;
                                }

                                public Set<FCAObject> getObjects() {
                                    return new HashSet<FCAObject>(formalContext
                                            .getObjects());
                                }

                                public BinaryRelation<FCAObject, FCAAttribute> getRelation() {
                                    return formalContext.getRelation();
                                }
                            };
                            final LatticeGenerator<FCAObject, FCAAttribute> lgen = new GantersAlgorithm<FCAObject, FCAAttribute>();
                            final Lattice<FCAObject, FCAAttribute> lattice = lgen
                            .createLattice(context);
                            final Diagram2D diagram2D = NDimLayoutOperations
                            .createDiagram(lattice, name,
                                    new DefaultDimensionStrategy<FCAAttribute>());
                            rescale(diagram2D);
                            schema.addDiagram(diagram2D);
                        }
            }

            if (!cscFile.getDatabaseDefinitions().isEmpty()) {
                // if there is at least one DB definition, we use the first for
                // the CSX
                // multiple ones are unlikely and we wouldn't know what to do
                // then anyway
                final DatabaseDefinition dbDef = (DatabaseDefinition) cscFile
                .getDatabaseDefinitions().get(0);
                final DatabaseInfo dbInfo = new DatabaseInfo();
                dbInfo.setOdbcDataSource(dbDef.getDatabaseName(), null, null);
                final Table table = new Table(dbDef.getTable(), false);
                dbInfo.setTable(table);
                dbInfo.setKey(new Column(dbDef.getPrimaryKey(), 0, table));
                schema.setDatabaseInfo(dbInfo);
            }
        } catch (final MalformedURLException e) {
            // should not happen
            throw new RuntimeException("Internal error", e);
        }
    }

    private Diagram2D createDiagram2D(final ConcreteScale scale,
            final String name) {
        final AbstractScale abstractScale = scale.getAbstractScale();
        final StringMap attributeMap = scale.getAttributeMap();
        final QueryMap queryMap = scale.getQueryMap();

        // we always just take the first diagram -- multiple diagrams are hardly
        // used anywhere
        final LineDiagram diagram = abstractScale.getLineDiagrams().get(0);
        return createDiagram2D(name, diagram,
                attributeMap, queryMap);
    }

    private Diagram2D createDiagram2D(final String diagramName,
            final LineDiagram originalDiagram, final StringMap attributeMap,
            final QueryMap queryMap) {
        final SimpleLineDiagram result = new SimpleLineDiagram();
        result.setTitle(diagramName);

        final List points = originalDiagram.getPoints();
        for (final Iterator iter = points.iterator(); iter.hasNext();) {
            final Point point = (Point) iter.next();

            final String identifier = "" + point.getNumber();
            final Point2D position = new Point2D.Double(point.getX(), point
                    .getY());
            final ConceptImplementation concept = new ConceptImplementation();

            final DiagramNode node = new DiagramNode(result, identifier,
                    position, concept, null, null, null);
            result.addNode(node);
        }

        final List lines = originalDiagram.getLines();
        for (final Iterator iter = lines.iterator(); iter.hasNext();) {
            final Line line = (Line) iter.next();
            final Point from = line.getFrom();
            final Point to = line.getTo();
            final DiagramNode fromNode = result.getNode("" + from.getNumber());
            final DiagramNode toNode = result.getNode("" + to.getNumber());
            result.addLine(fromNode, toNode);

            final ConceptImplementation fromConcept = (ConceptImplementation) fromNode
            .getConcept();
            final ConceptImplementation toConcept = (ConceptImplementation) toNode
            .getConcept();
            fromConcept.addSubConcept(toNode.getConcept());
            toConcept.addSuperConcept(fromConcept);
        }

        final List objects = originalDiagram.getObjects();
        for (final Iterator iter = objects.iterator(); iter.hasNext();) {
            final FCAObject object = (FCAObject) iter.next();
            FCAElementImplementation resultObject;
            final DiagramNode node = result.getNode(""
                    + object.getPoint().getNumber());

            if (queryMap.getQuery(object.getIdentifier()) != null) {
                resultObject = new FCAElementImplementation(queryMap
                        .getQuery(object.getIdentifier()));
            } else {
                resultObject = new FCAElementImplementation(object
                        .getIdentifier());
            }

            final ConceptImplementation concept = (ConceptImplementation) node
            .getConcept();
            concept.addObject(resultObject);

            final LabelInfo labelInfo = createLabelInfo(object.getDescription()
                    .getFormat());
            node.setObjectLabelInfo(labelInfo);
        }

        final List attributes = originalDiagram.getAttributes();
        for (final Iterator iter = attributes.iterator(); iter.hasNext();) {
            final FCAAttribute attribute = (FCAAttribute) iter.next();
            FCAElementImplementation resultAttribute;
            final DiagramNode node = result.getNode(""
                    + attribute.getPoint().getNumber());

            final FormattedString label = attributeMap.getLabel(attribute
                    .getIdentifier());
            if (label != null) {
                resultAttribute = new FCAElementImplementation(label
                        .getContent());
            } else {
                resultAttribute = new FCAElementImplementation(attribute
                        .getIdentifier());
            }

            final ConceptImplementation concept = (ConceptImplementation) node
            .getConcept();
            concept.addAttribute(resultAttribute);

            final LabelInfo labelInfo = createLabelInfo(attribute
                    .getDescription().getFormat());
            node.setAttributeLabelInfo(labelInfo);
        }

        for (final Iterator<DiagramNode> iter = result.getNodes(); iter
        .hasNext();) {
            final DiagramNode node = iter.next();
            final ConceptImplementation concept = (ConceptImplementation) node
            .getConcept();
            concept.buildClosures();
        }
        return result;
    }

    private LabelInfo createLabelInfo(final StringFormat format) {
        final LabelInfo retVal = new LabelInfo();
        retVal.setOffset(format.getOffset().getX(), -format.getOffset().getY());
        if (format.getHorizontalAlign() == StringFormat.LEFT) {
            retVal.setTextAlignment(LabelInfo.ALIGNLEFT);
        } else if (format.getHorizontalAlign() == StringFormat.H_CENTER) {
            retVal.setTextAlignment(LabelInfo.ALIGNCENTER);
        } else if (format.getHorizontalAlign() == StringFormat.RIGHT) {
            retVal.setTextAlignment(LabelInfo.ALIGNRIGHT);
        }
        return retVal;
    }

    private void rescale(final Diagram2D diagram) {
        final Rectangle2D bounds = diagram.getBounds();

        double scaleX;
        if (bounds.getWidth() == 0) {
            scaleX = Double.MAX_VALUE;
        } else {
            scaleX = TARGET_DIAGRAM_WIDTH / bounds.getWidth();
        }

        double scaleY;
        if (bounds.getHeight() == 0) {
            scaleY = Double.MAX_VALUE;
        } else {
            scaleY = TARGET_DIAGRAM_HEIGHT / bounds.getHeight();
        }

        final double scale = (scaleX < scaleY) ? scaleX : scaleY;

        final Iterator<DiagramNode> it = diagram.getNodes();
        while (it.hasNext()) {
            final DiagramNode node = it.next();
            final Point2D pos = node.getPosition();
            node.setPosition(new Point2D.Double(scale * pos.getX(), scale
                    * pos.getY()));

            final LabelInfo aLabel = node.getAttributeLabelInfo();
            if (aLabel != null) {
                final Point2D offset = aLabel.getOffset();
                aLabel.setOffset(new Point2D.Double(scale * offset.getX(),
                        scale * offset.getY()));
            }

            final LabelInfo oLabel = node.getObjectLabelInfo();
            if (oLabel != null) {
                final Point2D offset = oLabel.getOffset();
                oLabel.setOffset(new Point2D.Double(scale * offset.getX(),
                        scale * offset.getY()));
            }
        }
    }
}
