/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * Draws a line between two points, representing a DiagramLine in the model.
 */
public class LineView extends CanvasItem {
    private static final double LABEL_MARGIN = 2;

    /**
     * Store the line in the model for this view.
     */
    private DiagramLine diagramLine = null;

    private NodeView fromView;
    private NodeView toView;
    
    private Color showRatioColor;
    private float fontSize;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(DiagramLine diagramLine, NodeView fromView, NodeView toView) {
        this.diagramLine = diagramLine;
        this.fromView = fromView;
        this.toView = toView;
        this.showRatioColor = ConfigurationManager.fetchColor("LineView", "showExtentRatioColor", null);
        this.fontSize = ConfigurationManager.fetchFloat("LineView", "labelFontSize", 8);
    }

    /**
     * Draws the line.
     */
    public void draw(Graphics2D graphics) {
        DiagramSchema diagramSchema = fromView.getDiagramView().getDiagramSchema();
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        Paint oldPaint = graphics.getPaint();
        Stroke oldStroke = graphics.getStroke();
        int selectionLineWidth = diagramSchema.getSelectionLineWidth();
        if (this.diagramLine.getFromNode().getY() > this.diagramLine.getToNode().getY()) {
            graphics.setPaint(Color.red);
            graphics.setStroke(new BasicStroke(3));
        } else if (this.getSelectionState() == DiagramView.NO_SELECTION) {
            graphics.setPaint(diagramSchema.getLineColor());
            graphics.setStroke(new BasicStroke(1));
        } else if (this.getSelectionState() == DiagramView.SELECTED_IDEAL) {
            graphics.setPaint(diagramSchema.getCircleIdealColor());
            graphics.setStroke(new BasicStroke(selectionLineWidth));
        } else if (this.getSelectionState() == DiagramView.SELECTED_FILTER) {
            graphics.setPaint(diagramSchema.getCircleFilterColor());
            graphics.setStroke(new BasicStroke(selectionLineWidth));
        } else if (this.getSelectionState() == DiagramView.NOT_SELECTED) {
            graphics.setPaint(diagramSchema.fadeOut(diagramSchema.getLineColor()));
            graphics.setStroke(new BasicStroke(1));
        }
        graphics.draw(new Line2D.Double(from, to));
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
        
        if(this.showRatioColor != null) {
            drawExtentRatio(graphics);
        }
    }

    private void drawExtentRatio(Graphics2D graphics) {
    	Paint oldPaint = graphics.getPaint();
        Font oldFont = graphics.getFont();
        AffineTransform oldTransform = graphics.getTransform();

		DiagramView diagramView = this.fromView.getDiagramView();
		ConceptInterpreter interpreter = diagramView.getConceptInterpreter();
		ConceptInterpretationContext interpretationContext = this.fromView.getConceptInterpretationContext();

		Point2D from = diagramLine.getFromPosition();
		Point2D to = diagramLine.getToPosition();
		int startExtent = interpreter.getExtentSize(this.fromView.getDiagramNode().getConcept(),interpretationContext);
		int endExtent = interpreter.getExtentSize(this.toView.getDiagramNode().getConcept(),interpretationContext);
        double ratioInPercent;
        if(startExtent == 0) {
			ratioInPercent = 1.0;
        } else {
			ratioInPercent = (double)endExtent / (double)startExtent;
        }

		DecimalFormat format = new DecimalFormat("#.## %");
		String formattedNumber = format.format(ratioInPercent);
        double x = (from.getX() + to.getX()) / 2; 
        double y = (from.getY() + to.getY()) / 2;
        
        Font font = diagramView.getFont().deriveFont(fontSize);
        graphics.setFont(font);
		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout layout = new TextLayout(formattedNumber, font, frc);

		Rectangle2D bounds = layout.getBounds();
		Rectangle2D labelRectangle = new Rectangle2D.Double(x - bounds.getWidth()/2 - LABEL_MARGIN,
 														   y - bounds.getHeight()/2 - LABEL_MARGIN,
 														   bounds.getWidth() + 2*LABEL_MARGIN,
 														   bounds.getHeight() + 2*LABEL_MARGIN);

		graphics.setPaint(Color.BLACK);
        graphics.draw(labelRectangle);
		graphics.setPaint(this.showRatioColor);
		graphics.fill(labelRectangle);
		graphics.setPaint(Color.BLACK);
        graphics.drawString(formattedNumber, (float)(x - bounds.getWidth()/2 - bounds.getX()), (float)(y - bounds.getHeight()/2 - bounds.getY()));

        graphics.setFont(oldFont); 
        graphics.setTransform(oldTransform);
        graphics.setPaint(oldPaint);
    }

    /**
     * Returns always false since we assume the line to have no width.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }

    /**
     * Calculates the rectangle around this line.
     */
    public Rectangle2D getCanvasBounds(Graphics2D graphics) {
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        double x,y,w,h;
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

    public Point2D getPosition() {
        return this.fromView.getPosition();
    }
}
