/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.NormedIntervalSource;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.canvas.CanvasItem;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * Draws a line between two points, representing a DiagramLine in the model.
 */
public class LineView extends CanvasItem {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(LineView.class);

    private static class NonRealizedConceptGroupingMode {
        // nothing to declare
    }

    public static final NonRealizedConceptGroupingMode NO_GROUPING = new NonRealizedConceptGroupingMode();
    public static final NonRealizedConceptGroupingMode COLORED_LINES_GROUPING = new NonRealizedConceptGroupingMode();
    public static final NonRealizedConceptGroupingMode CLOUDS_GROUPING = new NonRealizedConceptGroupingMode();

    private NonRealizedConceptGroupingMode groupingMode = NO_GROUPING;

    private static final double LABEL_MARGIN = 2;

    /**
     * Store the line in the model for this view.
     */
    private DiagramLine diagramLine = null;

    private final NodeView fromView;
    private final NodeView toView;

    private Color showRatioColor = new Color(204, 255, 204);
    private Color showRatioFillColor = new Color(255, 204, 204);
    private float fontSize = 6;
    private boolean dynamicLineWidth = false;
    private String labelFormat = "0.## %";
    private boolean useExtentLabels = false;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(final DiagramLine diagramLine, final NodeView fromView,
            final NodeView toView) {
        this.diagramLine = diagramLine;
        this.fromView = fromView;
        this.toView = toView;
        this.useExtentLabels = preferences.getBoolean("showExtentRatioLabels",
                false);
        this.labelFormat = preferences.get("labelFormat", this.labelFormat);
        this.showRatioColor = preferences.getColor("showExtentRatioColor",
                this.showRatioColor);
        this.showRatioFillColor = preferences.getColor(
                "showExtentRatioFillColor", this.showRatioFillColor);
        this.fontSize = preferences.getFloat("labelFontSize", 6);
        this.dynamicLineWidth = preferences.getBoolean("lineWidth", false);
        if (preferences.get("nonRealizedConceptGrouping", "").equals(
                "coloredLines")) {
            this.groupingMode = COLORED_LINES_GROUPING;
        }
        if (preferences.get("nonRealizedConceptGrouping", "").equals("clouds")) {
            this.groupingMode = CLOUDS_GROUPING;
        }
    }

    /**
     * Draws the line.
     */
    @Override
    public void draw(final Graphics2D graphics) {
        final DiagramSchema diagramSchema = fromView.getDiagramView()
                .getDiagramSchema();
        final Point2D from = diagramLine.getFromPosition();
        final Point2D to = diagramLine.getToPosition();
        final Paint oldPaint = graphics.getPaint();
        final Stroke oldStroke = graphics.getStroke();

        double extentRatio;
        final DiagramView diagramView = this.fromView.getDiagramView();
        final ConceptInterpreter interpreter = diagramView
                .getConceptInterpreter();
        final ConceptInterpretationContext interpretationContext = this.fromView
                .getConceptInterpretationContext();
        final Concept upperConcept = this.fromView.getDiagramNode()
                .getConcept();
        final Concept lowerConcept = this.toView.getDiagramNode().getConcept();
        final int upperExtent = interpreter.getExtentSize(upperConcept,
                interpretationContext);
        final int lowerExtent = interpreter.getExtentSize(lowerConcept,
                interpretationContext);
        if (upperExtent == 0) {
            extentRatio = 1;
        } else {
            extentRatio = (double) lowerExtent / (double) upperExtent;
        }

        final float defaultLineWidth = diagramSchema.getDefaultLineWidth();
        final float selectionLineWidth = diagramSchema.getSelectionLineWidth();
        Color lineColor = null;
        float strokeWidth = defaultLineWidth;
        if (this.diagramLine.getFromNode().getY() > this.diagramLine
                .getToNode().getY()) {
            lineColor = Color.red;
            strokeWidth = 3 * defaultLineWidth;
        } else if (this.getSelectionState() == DiagramView.NO_SELECTION) {
            lineColor = diagramSchema.getLineColor();
            strokeWidth = defaultLineWidth;
        } else if (this.getSelectionState() == DiagramView.SELECTED_IDEAL) {
            lineColor = diagramSchema.getCircleIdealColor();
            strokeWidth = selectionLineWidth;
        } else if (this.getSelectionState() == DiagramView.SELECTED_FILTER) {
            lineColor = diagramSchema.getCircleFilterColor();
            strokeWidth = selectionLineWidth;
        } else if (this.getSelectionState() == DiagramView.NOT_SELECTED) {
            lineColor = diagramSchema.fadeOut(diagramSchema.getLineColor());
            strokeWidth = defaultLineWidth;
        }

        if (extentRatio == 1 && this.groupingMode != NO_GROUPING) {
            final NormedIntervalSource intervalSource = interpreter
                    .getIntervalSource(ConceptInterpreter.INTERVAL_TYPE_EXTENT);
            final double gradientPosition = intervalSource.getValue(
                    upperConcept, interpretationContext);
            Color fillColor = diagramSchema.getGradient().getColor(
                    gradientPosition);
            if (this.getSelectionState() == DiagramView.NOT_SELECTED) {
                fillColor = diagramSchema.fadeOut(fillColor);
                strokeWidth = defaultLineWidth;
            }

            double lineLength = this.fromView.getPosition().distance(
                    this.toView.getPosition());
            double lineWidth = this.fromView.getRadiusX();

            final AffineTransform oldTransform = graphics.getTransform();
            graphics.transform(AffineTransform.getTranslateInstance(
                    from.getX(), from.getY()));
            graphics.transform(AffineTransform.getRotateInstance(Math.atan2(to
                    .getY()
                    - from.getY(), to.getX() - from.getX())));

            if (this.groupingMode == COLORED_LINES_GROUPING) {
                final Rectangle2D line = new Rectangle2D.Double(0,
                        -lineWidth / 2, lineLength, lineWidth);
                graphics.setPaint(fillColor);
                graphics.fill(line);
                graphics.setPaint(lineColor);
                graphics.setStroke(new BasicStroke(1));
                graphics.draw(line);
            }

            if (this.groupingMode == CLOUDS_GROUPING) {
                final double extraBit = 3 * lineWidth;
                lineLength += 2 * extraBit;
                lineWidth += 2 * extraBit;
                final RoundRectangle2D cloud = new RoundRectangle2D.Double(
                        -extraBit, -lineWidth / 2, lineLength, lineWidth,
                        lineWidth, lineWidth);
                graphics.setPaint(new Color(fillColor.getRed(), fillColor
                        .getGreen(), fillColor.getBlue(), (int) (fillColor
                        .getAlpha() * 0.5)));
                graphics.fill(cloud);
                graphics.setPaint(lineColor);
                graphics.setTransform(oldTransform);
                graphics.setStroke(new BasicStroke(strokeWidth));
                graphics.draw(new Line2D.Double(from, to));
            }

            graphics.setTransform(oldTransform);
        } else {
            if (this.dynamicLineWidth) {
                strokeWidth = 7 * (float) extentRatio;
                if (strokeWidth < 0) {
                    strokeWidth = Float.MIN_VALUE;
                }
            }
            graphics.setPaint(lineColor);
            graphics.setStroke(new BasicStroke(strokeWidth));
            graphics.draw(new Line2D.Double(from, to));
        }

        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);

        if (this.useExtentLabels) {
            drawExtentRatio(graphics, extentRatio);
        }
    }

    private void drawExtentRatio(final Graphics2D graphics, final double ratio) {
        final Paint oldPaint = graphics.getPaint();
        final Font oldFont = graphics.getFont();
        final AffineTransform oldTransform = graphics.getTransform();

        final Point2D from = diagramLine.getFromPosition();
        final Point2D to = diagramLine.getToPosition();

        if (ratio == 1) {
            return;
        }

        final DecimalFormat format = new DecimalFormat(this.labelFormat);
        final String formattedNumber = format.format(ratio);
        final double x = (from.getX() + to.getX()) / 2;
        final double y = (from.getY() + to.getY()) / 2;

        final Font font = this.fromView.getDiagramView().getFont().deriveFont(
                fontSize);
        graphics.setFont(font);
        final FontRenderContext frc = graphics.getFontRenderContext();
        final TextLayout layout = new TextLayout(formattedNumber, font, frc);
        // should give something wider than anything we really displays
        final TextLayout longLayout = new TextLayout(format.format(88.8888),
                font, frc);

        final Rectangle2D bounds = layout.getBounds();
        final Rectangle2D longBounds = longLayout.getBounds();
        final Rectangle2D labelRectangle = new Rectangle2D.Double(x
                - longBounds.getWidth() / 2 - LABEL_MARGIN, y
                - longBounds.getHeight() / 2 - LABEL_MARGIN, longBounds
                .getWidth()
                + 2 * LABEL_MARGIN, longBounds.getHeight() + 2 * LABEL_MARGIN);

        graphics.setPaint(this.showRatioColor);
        graphics.fill(labelRectangle);
        if (this.showRatioFillColor != null) {
            drawExtentRatioFill(graphics, ratio, labelRectangle);
        }
        graphics.setPaint(Color.BLACK);
        graphics.draw(labelRectangle);
        graphics.drawString(formattedNumber,
                (float) (x - bounds.getWidth() / 2 - bounds.getX()), (float) (y
                        - bounds.getHeight() / 2 - bounds.getY()));

        graphics.setFont(oldFont);
        graphics.setTransform(oldTransform);
        graphics.setPaint(oldPaint);
    }

    private void drawExtentRatioFill(final Graphics2D graphics,
            final double ratio, final Rectangle2D labelRectangle) {
        final double filledWidth = labelRectangle.getWidth() * ratio;
        graphics.setPaint(this.showRatioFillColor);
        final Rectangle2D filledRectangle = new Rectangle2D.Double(
                labelRectangle.getX(), labelRectangle.getY(), filledWidth,
                labelRectangle.getHeight());
        graphics.fill(filledRectangle);
    }

    /**
     * Returns always false since we assume the line to have no width.
     */
    @Override
    public boolean containsPoint(final Point2D point) {
        return false;
    }

    /**
     * Calculates the rectangle around this line.
     */
    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D graphics) {
        final Point2D from = diagramLine.getFromPosition();
        final Point2D to = diagramLine.getToPosition();
        double x, y, w, h;
        if (from.getX() < to.getX()) {
            x = from.getX();
            w = to.getX() - x;
        } else {
            x = to.getX();
            w = from.getX() - x;
        }
        if (from.getY() < to.getY()) {
            y = from.getY();
            h = to.getY() - y;
        } else {
            y = to.getY();
            h = from.getY() - y;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    private int getSelectionState() {
        if (this.fromView.getSelectionState() == DiagramView.NO_SELECTION) {
            return DiagramView.NO_SELECTION;
        }
        if (this.fromView.getSelectionState() == DiagramView.NOT_SELECTED) {
            return DiagramView.NOT_SELECTED;
        }
        if (this.toView.getSelectionState() == DiagramView.NOT_SELECTED) {
            return DiagramView.NOT_SELECTED;
        }
        if (this.fromView.getSelectionState() == DiagramView.SELECTED_FILTER) {
            return DiagramView.SELECTED_FILTER;
        }
        if (this.toView.getSelectionState() == DiagramView.SELECTED_IDEAL) {
            return DiagramView.SELECTED_IDEAL;
        }
        return DiagramView.NOT_SELECTED;
    }

    @Override
    public Point2D getPosition() {
        return this.fromView.getPosition();
    }
}
