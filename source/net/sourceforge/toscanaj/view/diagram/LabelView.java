package net.sourceforge.toscanaj.view.diagram;

import java.awt.*;
import java.awt.geom.*;

import java.util.Iterator;

import javax.swing.*;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

/**
 * This class encapsulates all label drawing code.
 */
public class LabelView extends CanvasItem implements ChangeObserver {
    /**
     * Label current width.
     */
    private double width;
    /**
     * Label current height.
     */
    private double height;
    /**
     * Label current x coordinate.
     */
    private double xPos;
    /**
     * Label current y coordinate.
     */
    private double yPos;

    /**
     * Used when the label should be drawn above the given point.
     *
     * See Draw( Graphics2D, double ,double ,int ).
     */
    public static final int ABOVE = 0;

    /**
     * Used when the label should be drawn below the given point.
     *
     * See Draw( Graphics2D, double ,double ,int ).
     */
    public static final int BELOW = 1;

    /**
     * The label information that should be drawn.
     */
    private LabelInfo _labelInfo;

    /**
     * Store the diagram view that the label belongs to.
     *
     * This is used only for message passing whenever an update is needed.
     */
    private DiagramView diagramView = null;

    /**
     * Defines how the label has to be placed in relation to the node.
     */
    private int placement;

    /**
     * Creates a view for the given label information.
     */
    public LabelView( DiagramView diagramView, int placement, LabelInfo label ) {
        this.diagramView = diagramView;
        this.placement = placement;
        _labelInfo = label;
        _labelInfo.addObserver(this);
    }

    /**
     * Update label view as label info has change
     */
    public void update(Object source){
        notifyObservers();
    }

    /**
     * Return Label width
     */
    public double getLabelWidth() {
      return width;
    }

    /**
     * Return Label height
     */
    public double getLabelHeight() {
      return height;
    }

    /**
     * Return label x coordinate
     */
    public double getLabelX() {
      return xPos;
    }

    /**
     * Return label y coordinate
     */
    public double getLabelY() {
      return yPos;
    }

    /**
     * Draws the label at the given position in the graphic context.
     *
     * The position is placed above or below the label, horizontally centered
     * plus the offset from the LabelInfo.
     *
     * The placement should be either LabelView::ABOVE or LabelView::BELOW.
     * A dashed line will be drawn from the central top/bottom point to the
     * given point.
     *
     * The scaling information is needed to scale the offset.
     */
    public void draw( ToscanajGraphics2D tg )
    {
        Graphics2D graphics = tg.getGraphics2D();

        // remember some settings to restore them later
        Paint oldPaint = graphics.getPaint();

        // get the font metrics
        FontMetrics fm = graphics.getFontMetrics();

        // find the size and position
        DiagramNode node = this._labelInfo.getNode();
        double x = node.getX();
        double y = node.getY();
        double lw = getWidth( fm );
        width = tg.inverseScaleX(lw);
        double lh = getHeight( fm );
        height = tg.inverseScaleY(lh);
        xPos = x - tg.inverseScaleX(lw/2) + _labelInfo.getOffset().getX();
        if( placement == ABOVE )
        {
            y = y - tg.inverseScaleY(node.getRadius());
            yPos = y - tg.inverseScaleY(lh) + _labelInfo.getOffset().getY();
        }
        else
        {
            y = y + tg.inverseScaleY(node.getRadius());
            yPos = y + _labelInfo.getOffset().getY();
        }
        // draw a dashed line from the given point to the calculated
        Stroke oldStroke = graphics.getStroke();
        float[] dashstyle = { 4, 4 };
        tg.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL, 1, dashstyle, 0 ) );
        tg.drawLine( x, y, xPos + tg.inverseScaleX(lw/2),  y + _labelInfo.getOffset().getY() );
        tg.setStroke( oldStroke );

        // draw the label itself
        tg.drawFilledRectangle(xPos, yPos, lw, lh, _labelInfo.getBackgroundColor(), _labelInfo.getTextColor() );

        // draw the text
        if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT )
        {
            Iterator it = _labelInfo.getEntryIterator();
            int j = 0;
            while(it.hasNext()) {
                String cur = it.next().toString();
                tg.drawString( cur ,xPos, yPos, fm.getLeading() +
                                                fm.getDescent(), fm.getAscent() +
                                                fm.getLeading() + j * fm.getHeight() );
                j++;
            }
        }
        else if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER )
        {
            Iterator it = _labelInfo.getEntryIterator();
            int j = 0;
            while(it.hasNext()) {
                String cur = it.next().toString();
                tg.drawString( cur , xPos, yPos,
                        (int)(fm.getLeading()/2 + fm.getDescent()/2 + ( lw - fm.stringWidth(cur))/2  ),
                        fm.getAscent() + fm.getLeading() + j * fm.getHeight() );
                j++;
            }
        }
        else if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT )
        {
            Iterator it = _labelInfo.getEntryIterator();
            int j = 0;
            while(it.hasNext()) {
                String cur = it.next().toString();
                tg.drawString( cur ,xPos ,yPos ,
                            (int)(-fm.getLeading() - fm.getDescent() + lw - fm.stringWidth(cur)),
                            fm.getAscent() + fm.getLeading() + j * fm.getHeight() );
                j++;
            }
        }

        // restore old settings
        graphics.setPaint( oldPaint );
    }

    /**
     * Calculates the width of the label given a specific font metric.
     *
     * The width is calculated as the maximum string width plus two times the
     * leading and the descent from the font metrics. When drawing the text the
     * horizontal position should be the left edge of the label plus one times
     * thetwo values (FontMetrics::getLeading() and FontMetrics::getDescent()).
     */
    public double getWidth( FontMetrics fontMetrics )
    {
        double result = 0;

        // find maximum width of string
        Iterator it = _labelInfo.getEntryIterator();
        while(it.hasNext()) {
            String cur = it.next().toString();
            double w = fontMetrics.stringWidth( cur );
            if( w > result )
            {
                result = w;
            }
        }

        // add two leadings and two descents to have some spacing on the left
        // and right side
        result += 2 * fontMetrics.getLeading() + 2 * fontMetrics.getDescent();

        return result;
    }

    /**
     * Calculates the height of the label given a specific font metric.
     */
    public int getHeight( FontMetrics fontMetrics )
    {
        return _labelInfo.getNumberOfEntries() * fontMetrics.getHeight();
    }

    /**
     * Returns true whenever the point is in the bounding rectangle.
     */
    public boolean containsPoint(Point2D point) {
        double x = point.getX();
        if( x < this.xPos ) {
            return false;
        }
        if( x > this.xPos + this.width ) {
            return false;
        }
        double y = point.getY();
        if( y > this.yPos ) {
            return false;
        }
        if( y < this.yPos + this.height ) {
            return false;
        }
        return true;
    }

    /**
     * Moves the label by the given distance.
     */
    public void moveBy(double deltaX, double deltaY) {
        _labelInfo.setOffset(_labelInfo.getOffset().getX() + deltaX,
                             _labelInfo.getOffset().getY() + deltaY );
    }
}