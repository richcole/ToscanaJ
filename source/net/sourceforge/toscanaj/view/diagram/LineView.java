package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.canvas.CanvasItem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * class DiagramLine draws a line between two points
 */
public class LineView extends CanvasItem {
    /**
     * Store the line in the model for this view.
     */
    private DiagramLine diagramLine = null;

    /**
     * The current state of selection.
     *
     * @see setSelectedConcept(Concept)
     */
    private int selectionState = NodeView.NO_SELECTION;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(DiagramLine diagramLine) {
        this.diagramLine = diagramLine;
    }

    /**
     * Draws the line.
     */
    public void draw(Graphics2D graphics) {
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        Paint oldPaint = graphics.getPaint();
        Stroke oldStroke = graphics.getStroke();
        if(this.selectionState != NodeView.NO_SELECTION) {
            if(this.selectionState == NodeView.SELECTED_IDEAL) {
                graphics.setPaint(diagramSchema.getCircleIdealColor());
                graphics.setStroke(new BasicStroke(NodeView.selectionSize));
            }
            else if(this.selectionState == NodeView.SELECTED_FILTER) {
                graphics.setPaint(diagramSchema.getCircleFilterColor());
                graphics.setStroke(new BasicStroke(NodeView.selectionSize));
            }
            else if(this.selectionState == NodeView.NOT_SELECTED) {
                Color circleColor = diagramSchema.getCircleColor();
                float rel = NodeView.fadeOut;
                circleColor = new Color( (int)(circleColor.getRed()*(1-rel) + 255*rel),
                                         (int)(circleColor.getGreen()*(1-rel) + 255*rel),
                                         (int)(circleColor.getBlue()*(1-rel) + 255*rel),
                                         (int)(circleColor.getAlpha()*(1-rel) + 255*rel) );
                graphics.setPaint(circleColor);
            }
        }
        graphics.draw(new Line2D.Double(from,to));
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
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
    public Rectangle2D getBounds(Graphics2D graphics) {
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        double x,y,w,h;
        if( from.getX() < to.getX() ) {
            x = from.getX();
            w = to.getX() - x;
        }
        else {
            x = to.getX();
            w = from.getX() - x;
        }
        if( from.getY() < to.getY() ) {
            y = from.getY();
            h = to.getY() - y;
        }
        else {
            y = to.getY();
            h = from.getY() - y;
        }
        return new Rectangle2D.Double(x,y,w,h);
    }

    /**
     * Checks if the line belongs to filter or ideal of the selected concepts
     * and marks it appropriately.
     *
     * @see NodeView.setSelectedConcept(Concept)
     */
    public void setSelectedConcept(Concept concept) {
        if(concept == null) {
            this.selectionState = NodeView.NO_SELECTION;
            return;
        }
        Concept from = this.diagramLine.getFromNode().getConcept();
        if((from == concept) || (from.hasSuperConcept(concept)) ) {
            this.selectionState = NodeView.SELECTED_IDEAL;
            return;
        }
        Concept to = this.diagramLine.getToNode().getConcept();
        if((to == concept) || (to.hasSubConcept(concept)) ) {
            this.selectionState = NodeView.SELECTED_FILTER;
            return;
        }
        this.selectionState = NodeView.NOT_SELECTED;
        return;
    }

    /**
     * Returns the information if and how the node is selected.
     *
     * @see NodeView.getSelectionState()
     */
    public int getSelectionState() {
        return this.selectionState;
    }
}