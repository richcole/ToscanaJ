/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.diagram.SelectionChangedEvent;
import net.sourceforge.toscanaj.events.EventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.awt.*;
import java.awt.geom.*;
import java.util.Iterator;

/**
 * This class encapsulates all generic label drawing code.
 *
 * The actual classes to use are the AttributeLabelView and the ObjectLabelView
 * which are distinguished by position (above vs. below the node) and default
 * display type (list vs. number).
 */
abstract public class LabelView extends CanvasItem implements ChangeObserver, EventListener {
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
     * Gives the minimum number of display lines possible.
     *
     * @see #displayLines
     */
    protected static final int MIN_DISPLAY_LINES = 3;

    /**
     * Gives the number of display lines used on a new label.
     *
     * @see #displayLines
     */
    protected static final int DEFAULT_DISPLAY_LINES = 4;

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
     * The bounding rectangle for the label itself.
     */
    protected Rectangle2D rect = null;

    /**
     * The label information that should be drawn.
     */
    protected LabelInfo labelInfo;

    /**
     * Store the diagram view that the label belongs to.
     */
    protected DiagramView diagramView = null;

    protected NodeView nodeView;

    /**
     * The current display size in lines.
     *
     * This is the number of items currently displayed.
     */
    protected int displayLines = DEFAULT_DISPLAY_LINES;

    /**
     * The first item displayed in the list.
     *
     * This is used if the number of displayed lines is smaller than the number
     * of items to display to determine the top element in the displayed part.
     */
    protected int firstItem = 0;

    /**
     * The width reserved for the scrollbar to the right.
     */
    protected int scrollbarWidth = 0;

    /**
     * The height of a single line in the view.
     */
    protected int lineHeight = 0;

    /**
     * Creates a view for the given label information.
     */
    public LabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        this.diagramView = diagramView;
        this.nodeView = nodeView;
        this.labelInfo = label;
        this.labelInfo.addObserver(this);
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        String fontName = diagramSchema.getLabelFontName();
        int fontSize = diagramSchema.getLabelFontSize();
        this.font = new Font(fontName, Font.PLAIN, fontSize);
        updateEntries();
        diagramView.getController().getEventBroker().subscribe(this, SelectionChangedEvent.class, Object.class);
    }

    public void updateEntries() {
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
            this.displayLines = this.getNumberOfEntries();
        }
        update(this);
    }

    /**
     * Update label view as label info has change.
     *
     * This implements the callback in ChangeObserver, the parameter is the
     * object sending the event.
     */
    public void update(Object source) {
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
    public void draw(Graphics2D graphics) {
        // we draw only if we have content to draw
        if (this.displayLines == 0) {
            return;
        }

        // remember some settings to restore them later
        Paint oldPaint = graphics.getPaint();

        // set the font we want -- don't get too small
        if (graphics.getTransform().getScaleX() * this.font.getSize() < MinFontSize) {
            Font newFont = new Font(this.font.getName(), this.font.getStyle(),
                    (int) (MinFontSize / graphics.getTransform().getScaleX()));
            graphics.setFont(newFont);
        } else {
            graphics.setFont(this.font);
        }

        // get the font metrics
        FontMetrics fm = graphics.getFontMetrics();

        // find the size and position
        DiagramNode node = this.labelInfo.getNode();
        double x = node.getX();
        double y = node.getY();
        double radius = nodeView.getRadiusY();

        // get the bounding rectangle
        this.rect = getCanvasBounds(graphics);
        double xPos = rect.getX();
        double yPos = rect.getY();
        double lw = rect.getWidth();
        double lh = rect.getHeight();
        if (getPlacement() == ABOVE) {
            y = y - radius;
        } else {
            y = y + radius;
        }

        // find colors to use
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        Color lineColor = diagramSchema.getLineColor();
        Color backgroundColor = this.labelInfo.getBackgroundColor();
        Color textColor = this.labelInfo.getTextColor();
        Color scrollbarColorDark = Color.gray;
        Color scrollbarColorLight = Color.lightGray;
        if (nodeView.getSelectionState() == DiagramView.NOT_SELECTED) {
            // lighten
            lineColor = diagramSchema.fadeOut(lineColor);
            backgroundColor = diagramSchema.fadeOut(backgroundColor);
            textColor = diagramSchema.fadeOut(textColor);
            scrollbarColorDark = diagramSchema.fadeOut(scrollbarColorDark);
            scrollbarColorLight = diagramSchema.fadeOut(scrollbarColorLight);
        }

        // draw a dashed line from the given point to the calculated
        Stroke oldStroke = graphics.getStroke();
        float[] dashstyle = {4, 4};
        graphics.setPaint(lineColor);
        graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 1, dashstyle, 0));
        graphics.draw(new Line2D.Double(x, y, xPos + lw / 2, y + this.labelInfo.getOffset().getY()));

        graphics.setStroke(oldStroke);

        // draw the label itself
        this.rect = new Rectangle2D.Double(xPos, yPos, lw, lh);
        graphics.setPaint(backgroundColor);
        graphics.fill(rect);
        graphics.setPaint(textColor);
        graphics.draw(rect);

        // draw the object names
        Iterator it = this.getEntryIterator();
        int numItem = 0;
        while (it.hasNext()) {
            String cur = it.next().toString();
            if (numItem >= this.firstItem) {
                int curPos = numItem - this.firstItem;
                if (curPos < this.displayLines) {
                    if (this.labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT) {
                        graphics.drawString(cur,
                                (float) xPos + fm.getLeading() + fm.getDescent(),
                                (float) yPos + fm.getAscent() + fm.getLeading() + curPos * fm.getHeight());
                    } else if (this.labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER) {
                        graphics.drawString(cur,
                                (float) (xPos + (fm.getLeading() / 2 + fm.getDescent() / 2 + (lw - fm.stringWidth(cur)) / 2)),
                                (float) yPos + fm.getAscent() + fm.getLeading() + curPos * fm.getHeight());
                    } else if (this.labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT) {
                        graphics.drawString(cur,
                                (float) (xPos + (-fm.getLeading() - fm.getDescent() + lw - fm.stringWidth(cur))),
                                (float) yPos + fm.getAscent() + fm.getLeading() + curPos * fm.getHeight());
                    } else {
                        throw new RuntimeException("Unknown label alignment.");
                    }
                }
            }
            numItem++;
        }
        int numItems = this.getNumberOfEntries();
        // draw the scroller elements when needed
        if (numItems > MIN_DISPLAY_LINES) {
            // first a line separating the scrollbar from the rest
            graphics.draw(new Line2D.Double(xPos + lw - this.scrollbarWidth, yPos,
                    xPos + lw - this.scrollbarWidth, yPos + lh));
            // calculate a size for the scroll buttons
            double width = this.scrollbarWidth * 0.8;
            double height = this.lineHeight * 0.8;
            if (width < height) {
                height = width;
            } else {
                width = height;
            }
            // draw the upwards triangle
            /// @todo find a way to use the double coordinate system
            double xleft = xPos + lw - this.scrollbarWidth + (this.scrollbarWidth - width) / 2;
            double ytop = yPos + (this.lineHeight - height) / 2;
            int[] xpoints = new int[3];
            int[] ypoints = new int[3];
            xpoints[0] = (int) (xleft);
            ypoints[0] = (int) (ytop + height);
            xpoints[1] = (int) (xleft + width / 2);
            ypoints[1] = (int) (ytop);
            xpoints[2] = (int) (xleft + width);
            ypoints[2] = (int) (ytop + height);
            if (this.firstItem != 0) {
                graphics.setPaint(scrollbarColorDark);
            } else {
                graphics.setPaint(scrollbarColorLight);
            }
            graphics.fill(new Polygon(xpoints, ypoints, 3));
            // draw the downwards triangle
            ytop = yPos + (this.displayLines - 2) * this.lineHeight + (this.lineHeight - height) / 2;
            xpoints[0] = (int) (xleft);
            ypoints[0] = (int) (ytop);
            xpoints[1] = (int) (xleft + width / 2);
            ypoints[1] = (int) (ytop + height);
            xpoints[2] = (int) (xleft + width);
            ypoints[2] = (int) (ytop);
            if (this.firstItem + this.displayLines < numItems) {
                graphics.setPaint(scrollbarColorDark);
            } else {
                graphics.setPaint(scrollbarColorLight);
            }
            graphics.fill(new Polygon(xpoints, ypoints, 3));
            // draw the current position
            double scale = (this.lineHeight * (this.displayLines - 3)) / (double) numItems;
            width = 0.8 * width;
            xleft = xPos + lw - this.scrollbarWidth + (this.scrollbarWidth - width) / 2;
            ytop = yPos + this.lineHeight;
            graphics.setPaint(scrollbarColorLight);
            graphics.fill(new Rectangle2D.Double(xleft, ytop + this.firstItem * scale,
                    width, this.displayLines * scale));
            // draw the resize handle
            graphics.setPaint(scrollbarColorDark);
            graphics.fill(new Ellipse2D.Double(xleft,
                    yPos + (this.displayLines - 1) * this.lineHeight +
                    (this.lineHeight - height) / 2,
                    width, height));
        }

        // restore old settings
        graphics.setPaint(oldPaint);
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
    public double getWidth(FontMetrics fontMetrics) {
        if (this.getNumberOfEntries() == 0) {
            return 0;
        }

        if (this.getNumberOfEntries() > MIN_DISPLAY_LINES) {
            this.scrollbarWidth = fontMetrics.stringWidth("M");
        } else {
            this.scrollbarWidth = 0;
        }

        double result = 0;

        // find maximum width of string
        Iterator it = this.getEntryIterator();
        while (it.hasNext()) {
            String cur = it.next().toString();
            double w = fontMetrics.stringWidth(cur);
            if (w > result) {
                result = w;
            }
        }
        // add scrollbar width if needed
        result += this.scrollbarWidth;

        // add two leadings and two descents to have some spacing on the left
        // and right side
        result += 2 * fontMetrics.getLeading() + 2 * fontMetrics.getDescent();

        return result;
    }

    /**
     * Calculates the height of the label given a specific font metric.
     */
    public int getHeight(FontMetrics fontMetrics) {
        this.lineHeight = fontMetrics.getHeight();
        return this.displayLines * fontMetrics.getHeight();
    }

    /**
     * Returns true whenever the point is in the bounding rectangle.
     */
    public boolean containsPoint(Point2D point) {
        if (this.rect == null) {
            return false;
        }
        return this.rect.contains(point);
    }

    /**
     * Moves or resizes the label, depending on where the drag event occured.
     */
    public void processDragEvent(Point2D from, Point2D to) {
        if ((from.getX() >= this.rect.getMaxX() - this.scrollbarWidth)) {
            // we have a click on the scrollbar, calculate the line hit
            int lineHit = (int) ((from.getY() - this.rect.getY()) / this.lineHeight);
            if (lineHit == this.displayLines - 1) { // it is on the resize handle
                // calculate the line hit by the new mouse position
                int newLine = (int) ((to.getY() - this.rect.getY()) / this.lineHeight);
                // check if it is above/below
                if (newLine > lineHit) {
                    if (this.displayLines < this.getNumberOfEntries()) {
                        this.displayLines++;
                        if (this.firstItem + this.displayLines >
                                this.getNumberOfEntries()) {
                            this.firstItem--;
                        }
                        notifyObservers();
                    }
                } else if (newLine < lineHit) {
                    if (this.displayLines > MIN_DISPLAY_LINES) {
                        this.displayLines--;
                        notifyObservers();
                    }
                }
            } // otherwise do nothing
        } else {
            // move
            double deltaX = to.getX() - from.getX();
            double deltaY = to.getY() - from.getY();
            this.labelInfo.setOffset(this.labelInfo.getOffset().getX() + deltaX,
                    this.labelInfo.getOffset().getY() + deltaY);
        }
    }

    /**
     * Handles scrolling of the items.
     */
    public void processClickEvent(Point2D pos) {
        if (pos.getX() < this.rect.getMaxX() - this.scrollbarWidth) {
            // not a click on the scrollbar
            return;
        }
        // calculate the line hit
        int lineHit = (int) ((pos.getY() - this.rect.getY()) / this.lineHeight);
        if (lineHit == 0) {
            // scroll up
            if (this.firstItem != 0) {
                this.firstItem--;
            }
            this.notifyObservers();
        } else if (lineHit == this.displayLines - 2) {
            // scroll down
            if (this.firstItem != this.getNumberOfEntries() -
                    this.displayLines) {
                this.firstItem++;
            }
            this.notifyObservers();
        }
    }

    /**
     * Calculates the rectangle around label.
     */
    public Rectangle2D getCanvasBounds(Graphics2D graphics) {
        // get the font metrics
        FontMetrics fm = graphics.getFontMetrics();

        // find the size and position
        DiagramNode node = this.labelInfo.getNode();
        double x = node.getX();
        double y = node.getY();
        double lw = getWidth(fm);
        double lh = getHeight(fm);
        double xPos = x - lw / 2 + this.labelInfo.getOffset().getX();
        double radius = nodeView.getRadiusY();
        double yPos;
        if (getPlacement() == ABOVE) {
            y = y - radius;
            yPos = y - lh + this.labelInfo.getOffset().getY();
        } else {
            y = y + radius;
            yPos = y + this.labelInfo.getOffset().getY();
        }
        return new Rectangle2D.Double(xPos, yPos, lw, lh);
    }

    /**
     * Calculates which item was hit.
     *
     * This is the number of the entry, not on the screen but in the full list,
     * i.e. the offset from scrolling is used. If no item was hit (e.g. a click
     * on the scrollbar or the position is not on the label), -1 will be returned.
     */
    public int getItemAtPosition(Point2D pos) {
        if (pos.getX() > this.rect.getMaxX() - this.scrollbarWidth) {
            // a click on the scrollbar or to the right
            return -1;
        }
        if (pos.getX() < this.rect.getMinX()) {
            // a click to the left of the label
            return -1;
        }
        if (pos.getY() > this.rect.getMaxY()) {
            // a click below the label
            return -1;
        }
        if (pos.getY() < this.rect.getMinY()) {
            // a click above the label
            return -1;
        }
        int lineHit = (int) ((pos.getY() - this.rect.getY()) / this.lineHeight);
        return lineHit + this.firstItem;
    }

    abstract protected boolean highlightedInIdeal();

    abstract protected boolean highlightedInFilter();

    abstract protected int getNumberOfEntries();

    abstract protected Iterator getEntryIterator();

    private void nodeSelectionChanged() {
        if (highlightedInFilter() &&
                ((nodeView.getSelectionState() == DiagramView.SELECTED_FILTER) ||
                (nodeView.getSelectionState() == DiagramView.SELECTED_DIRECTLY))
        ) {
            this.diagramView.raiseItem(this);
        }

        if (highlightedInIdeal() &&
                ((nodeView.getSelectionState() == DiagramView.SELECTED_IDEAL) ||
                (nodeView.getSelectionState() == DiagramView.SELECTED_DIRECTLY))
        ) {
            this.diagramView.raiseItem(this);
        }
    }

    public void processEvent(Event e) {
        nodeSelectionChanged();
    }
}
