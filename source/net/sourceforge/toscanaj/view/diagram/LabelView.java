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
     * A constant used to set the labels to display something special (used for
     * special queries in ObjectLabelView).
     *
     * @see setDisplayType(int)
     */
    static public final int DISPLAY_SPECIAL = 2;

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
    protected int displayType = DISPLAY_LIST;

    /**
     * This is the smallest font size we accept.
     *
     * If the font size would go below this due to scaling, we will increase it
     * to this.
     */
    static private final int MinFontSize = 10;

    /**
     * Stores the font we use.
     */
    private Font font = new Font("Arial", Font.PLAIN, 10);

    /**
     * Stores if percentual distribution should be shown behind numbers.
     */
    protected boolean showPercentage = false;

    /**
     * Stores if we display contingents or extent/intent.
     *
     * If set to true we show only the attribute or object contingent (depending
     * on the given label info), otherwise it is intent or extent.
     */
    protected boolean showOnlyContingent = true;

    /**
     * The bounding rectangle for the label itself.
     */
    private Rectangle2D rect = null;

    /**
     * The label information that should be drawn.
     */
    protected LabelInfo labelInfo;

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
        this.labelInfo = label;
        this.labelInfo.addObserver(this);
    }

    /**
     * Sets the type of information displayed by the label: the number or the list
     * of objects.
     */
    public void setDisplayType(int type, boolean contingentOnly) {
        this.displayType = type;
        this.showOnlyContingent = contingentOnly;
        update(this);
    }

    /**
     * If toggled to true the label will display the percentual distribution
     * behind the number of entries.
     */
    public void setShowPercentage(boolean toggle) {
        this.showPercentage = toggle;
        update(this);
    }

    /**
     * Update label view as label info has change.
     *
     * This implements the callback in ChangeObserver, the parameter is the
     * object sending the event.
     */
    public void update(Object source){
        // we expect someone to cause us to redraw if needed, we don't do it ourself
        // maybe we are off-screen or hidden or whatever ...
        notifyObservers();
    }

    /**
     * Return Label width
     */
    public double getLabelWidth() {
      return this.rect.getWidth();
    }

    /**
     * Return Label height
     */
    public double getLabelHeight() {
      return this.rect.getHeight();
    }

    /**
     * Return label x coordinate
     */
    public double getLabelX() {
      return this.rect.getX();
    }

    /**
     * Return label y coordinate
     */
    public double getLabelY() {
      return this.rect.getY();
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
    public void draw(Graphics2D graphics)
    {
        // we draw only if we have content to draw
        if(this.labelInfo.getNumberOfEntries(this.showOnlyContingent) == 0) {
            return;
        }
        // remember some settings to restore them later
        Paint oldPaint = graphics.getPaint();

        // set the font we want -- don't get too small
        if(graphics.getTransform().getScaleX() * this.font.getSize() < MinFontSize) {
            Font newFont = new Font( this.font.getName(), this.font.getStyle(),
                                     (int)(MinFontSize / graphics.getTransform().getScaleX()) );
            graphics.setFont(newFont);
        }
        else {
            graphics.setFont(this.font);
        }

        // get the font metrics
        FontMetrics fm = graphics.getFontMetrics();

        // find the size and position
        DiagramNode node = this.labelInfo.getNode();
        double x = node.getX();
        double y = node.getY();
        double radius = node.getRadiusY();

        // get the bounding rectangle
        this.rect = getCanvasBounds(graphics);
        double xPos = rect.getX();
        double yPos = rect.getY();
        double lw = rect.getWidth();
        double lh = rect.getHeight();
        if( getPlacement() == ABOVE )
        {
            y = y - radius;
        }
        else
        {
            y = y + radius;
        }

        // draw a dashed line from the given point to the calculated
        Stroke oldStroke = graphics.getStroke();
        float[] dashstyle = { 4, 4 };
        graphics.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL, 1, dashstyle, 0 ) );
        graphics.draw( new Line2D.Double( x, y, xPos + lw/2,  y + this.labelInfo.getOffset().getY() ) );
        graphics.setStroke( oldStroke );

        // draw the label itself
        this.rect = new Rectangle2D.Double( xPos, yPos, lw, lh );
        graphics.setPaint( this.labelInfo.getBackgroundColor() );
        graphics.fill( rect );
        graphics.setPaint( this.labelInfo.getTextColor() );
        graphics.draw( rect );

        if(this.displayType == DISPLAY_LIST) {
            // draw the object names
            if( this.labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT )
            {
                Iterator it = this.labelInfo.getEntryIterator(this.showOnlyContingent);
                int j = 0;
                while(it.hasNext()) {
                    String cur = it.next().toString();
                    graphics.drawString( cur,
                                         (float)xPos + fm.getLeading() + fm.getDescent(),
                                         (float)yPos + fm.getAscent() + fm.getLeading() + j * fm.getHeight() );
                    j++;
                }
            }
            else if( this.labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER )
            {
                Iterator it = this.labelInfo.getEntryIterator(this.showOnlyContingent);
                int j = 0;
                while(it.hasNext()) {
                    String cur = it.next().toString();
                    graphics.drawString( cur,
                            (float)(xPos + (fm.getLeading()/2 + fm.getDescent()/2 + ( lw - fm.stringWidth(cur))/2 )),
                            (float)yPos + fm.getAscent() + fm.getLeading() + j * fm.getHeight() );
                    j++;
                }
            }
            else if( this.labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT )
            {
                Iterator it = this.labelInfo.getEntryIterator(this.showOnlyContingent);
                int j = 0;
                while(it.hasNext()) {
                    String cur = it.next().toString();
                    graphics.drawString( cur,
                                (float)(xPos + (-fm.getLeading() - fm.getDescent() + lw - fm.stringWidth(cur))),
                                (float)yPos + fm.getAscent() + fm.getLeading() + j * fm.getHeight() );
                    j++;
                }
            }
        }
        else {
            // draw the number
            String num = getNumberDisplay();

            if( this.labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT )
            {
                graphics.drawString( num,
                                     (float)xPos + fm.getLeading() + fm.getDescent(),
                                     (float)yPos + fm.getAscent() + fm.getLeading() );
            }
            else if( this.labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER )
            {
                graphics.drawString( num,
                        (float)(xPos + (fm.getLeading()/2 + fm.getDescent()/2 + ( lw - fm.stringWidth(num))/2 )),
                        (float)yPos + fm.getAscent() + fm.getLeading() );
            }
            else if( this.labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT )
            {
                graphics.drawString( num,
                            (float)(xPos + (-fm.getLeading() - fm.getDescent() + lw - fm.stringWidth(num))),
                            (float)yPos + fm.getAscent() + fm.getLeading() );
            }
        }

        // restore old settings
        graphics.setPaint( oldPaint );
    }

    /**
     * Returns the number to display when in number mode.
     */
    protected String getNumberDisplay() {
        String num = String.valueOf(this.labelInfo.getNumberOfEntries(this.showOnlyContingent));
        if(this.showPercentage) {
            num = num.concat(" (" + (int)(this.labelInfo.getNumberOfEntriesRelative(this.showOnlyContingent) * 100) + "%)");
        }
        return num;
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
        if(this.labelInfo.getNumberOfEntries(this.showOnlyContingent) == 0) {
            return 0;
        }

        double result = 0;

        if(this.displayType == DISPLAY_LIST) {
            // find maximum width of string
            Iterator it = this.labelInfo.getEntryIterator(this.showOnlyContingent);
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
            String num = getNumberDisplay();
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
            return this.labelInfo.getNumberOfEntries(this.showOnlyContingent) * fontMetrics.getHeight();
        }
        else {
            return fontMetrics.getHeight();
        }
    }

    /**
     * Returns true whenever the point is in the bounding rectangle.
     */
    public boolean containsPoint(Point2D point) {
        if(this.rect == null) {
            return false;
        }
        return this.rect.contains(point);
    }

    /**
     * Moves the label by the given distance.
     */
    public void moveBy(double deltaX, double deltaY) {
        this.labelInfo.setOffset(this.labelInfo.getOffset().getX() + deltaX,
                                 this.labelInfo.getOffset().getY() + deltaY );
    }

    /**
     * Switches the mode for this label between displaying number or list.
     */
    public void doubleClicked(Point2D pos) {
        if(this.displayType == LabelView.DISPLAY_LIST) {
            this.displayType = LabelView.DISPLAY_NUMBER;
        }
        else {
            this.displayType = LabelView.DISPLAY_LIST;
        }
        notifyObservers();
    }

    /**
     * Calculates the rectangle around label and its connection line.
     */
    public Rectangle2D getCanvasBounds(Graphics2D graphics) {
        // get the font metrics
        FontMetrics fm = graphics.getFontMetrics();

        // find the size and position
        DiagramNode node = this.labelInfo.getNode();
        double x = node.getX();
        double y = node.getY();
        double lw = getWidth( fm );
        double lh = getHeight( fm );
        double xPos = x - lw/2 + this.labelInfo.getOffset().getX();
        double radius = node.getRadiusY();
        double yPos;
        if( getPlacement() == ABOVE )
        {
            y = y - radius;
            yPos = y - lh + this.labelInfo.getOffset().getY();
        }
        else
        {
            y = y + radius;
            yPos = y + this.labelInfo.getOffset().getY();
        }
        return new Rectangle2D.Double(xPos, yPos, lw, lh);
    }
}