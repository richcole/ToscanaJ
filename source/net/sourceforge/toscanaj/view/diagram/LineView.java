package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
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
    private int selectionState = NodeView.NOT_SELECTED;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(DiagramLine diagramLine) {
        this.diagramLine = diagramLine;
    }

    /**
     * Draws the line.
     */
    public void draw(ToscanajGraphics2D g) {
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        Paint oldPaint = g.getGraphics2D().getPaint();
        Stroke oldStroke = g.getGraphics2D().getStroke();
        if(this.selectionState != NodeView.NOT_SELECTED) {
            g.getGraphics2D().setStroke(new BasicStroke(NodeView.selectionSize));
            if(this.selectionState == NodeView.SELECTED_IDEAL) {
                g.getGraphics2D().setPaint(NodeView.circleIdealColor);
            }
            else if(this.selectionState == NodeView.SELECTED_FILTER) {
                g.getGraphics2D().setPaint(NodeView.circleFilterColor);
            }
        }
        g.drawLine(from, to);
        g.getGraphics2D().setPaint(oldPaint);
        g.getGraphics2D().setStroke(oldStroke);
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
    public Rectangle2D getBounds(ToscanajGraphics2D g) {
        Point2D from = g.project(diagramLine.getFromPosition());
        Point2D to = g.project(diagramLine.getToPosition());
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
    /**
     * Recalculates if the node is selected and how.
     *
     * @see getSelectionState()
     */
    public void setSelectedConcept(Concept concept) {
        if(concept == null) {
            this.selectionState = NodeView.NOT_SELECTED;
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