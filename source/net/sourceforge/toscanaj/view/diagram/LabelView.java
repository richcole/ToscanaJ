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
 * This class encapsulates all generic label drawing code.
 *
 * The actual classes to use are the AttributeLabelView and the ObjectLabelView
 * which are distinguished by position (above vs. below the node) and default
 * display type (list vs. number).
 */
abstract public class LabelView extends CanvasItem implements ChangeObserver {
    /**
     * A constant used to set the labels to display the number of objects.
     *
     * @see setDisplayType(int)
     */
    static public final int DISPLAY_NUMBER = 0;

    /**
     * A constant used to set the labels to display the list of objects.
     *
     * @see setDisplayType(int)
     */
    static public final int DISPLAY_LIST = 1;

    /**
     * Used when the label should be drawn above the given point.
     *
     * See Draw( Graphics2D, double ,double ,int ).
     */
    static protected final int ABOVE = 0;

    /**
     * Used when the label should be drawn below the given point.
     *
     * See Draw( Graphics2D, double ,double ,int ).
     */
    static protected final int BELOW = 1;

    /**
     * Stores the type of information we want to display.
     *
     * @see setDisplayType(int)
     */
    private int displayType = DISPLAY_LIST;

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
     * Creates a view for the given label information.
     */
    public LabelView( DiagramView diagramView, LabelInfo label ) {
        this.diagramView = diagramView;
        _labelInfo = label;
        _labelInfo.addObserver(this);
    }

    /**
     * Sets the type of information displayed by the label: the number or the list
     * of objects.
     *
     * This method accepts the values DISPLAY_NUMBER and DISPLAY_LIST, everything
     * else is ignored.
     */
    public void setDisplayType(int type) {
        this.displayType = type;
        update(this);
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
        if( getPlacement() == ABOVE )
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

        if(this.displayType == DISPLAY_LIST) {
            // draw the object names
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
        }
        else {
            // draw the number
            String num = String.valueOf(_labelInfo.getNumberOfEntries());
            if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT )
            {
                tg.drawString( num ,xPos, yPos, fm.getLeading() +
                                                fm.getDescent(), fm.getAscent() +
                                                fm.getLeading() );
            }
            else if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER )
            {
                tg.drawString( num , xPos, yPos,
                        (int)(fm.getLeading()/2 + fm.getDescent()/2 + ( lw - fm.stringWidth(num))/2  ),
                        fm.getAscent() + fm.getLeading() );
            }
            else if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT )
            {
                tg.drawString( num ,xPos ,yPos ,
                            (int)(-fm.getLeading() - fm.getDescent() + lw - fm.stringWidth(num)),
                            fm.getAscent() + fm.getLeading() );
            }
        }

        // restore old settings
        graphics.setPaint( oldPaint );
    }

    /**
     * Returns the placement of the label (above or below the node).
     *
     * Possible return values are LabelView.ABOVE or LabelView.BELOW. This is
     * used to draw the labels in their appropriate position.
     */
    abstract protected int getPlacement();

    /**
     * Calculates the width of the label given a specific font metric.
     *
     * The width is calculated as the maximum string width plus two times the
     * leading and the descent from the font metrics. When drawing the text the
     * horizontal position should be the left edge of the label plus one times
     * the two values (FontMetrics::getLeading() and FontMetrics::getDescent()).
     */
    public double getWidth( FontMetrics fontMetrics )
    {
        double result = 0;

        if(this.displayType == DISPLAY_LIST) {
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
        }
        else {
            // find width of number
            String num = String.valueOf(_labelInfo.getNumberOfEntries());
            result = fontMetrics.stringWidth( num );
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
        if( this.displayType == DISPLAY_LIST ) {
            return _labelInfo.getNumberOfEntries() * fontMetrics.getHeight();
        }
        else {
            return fontMetrics.getHeight();
        }
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