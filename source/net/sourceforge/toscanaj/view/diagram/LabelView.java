/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.diagram.SelectionChangedEvent;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import org.tockit.canvas.CanvasItem;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class encapsulates all generic label drawing code.
 *
 * The actual classes to use are the AttributeLabelView and the ObjectLabelView
 * which are distinguished by position (above vs. below the node) and default
 * display type (list vs. number).
 */
abstract public class LabelView extends CanvasItem implements ChangeObserver, EventBrokerListener {
    private float textmargin;

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
     * Stores the font we use.
     */
    private Font font;

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

    protected boolean scrollbarShown = false;

    /**
     * The height of a single line in the view.
     */
    protected float lineHeight = 0;

    protected Vector observers = new Vector();
    
    protected DragMode dragMode = NOT_DRAGGING;
    
    protected static class DragMode {
    }

    protected static final DragMode NOT_DRAGGING = new DragMode();
    protected static final DragMode RESIZING = new DragMode();
    protected static final DragMode MOVING = new DragMode();
    protected static final DragMode SCROLLING = new DragMode();

	private float currentScrollBarWidth;
    
	public interface LabelFactory {
    	LabelView createLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label);
    	Class getLabelClass();
    }
    
    public static LabelFactory getFactory() {
    	return null;
    }

    /**
     * Creates a view for the given label information.
     * 
     * Protected since the factory should be used instead.
     * 
     * @see getFactory()
     */
    protected LabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        this.diagramView = diagramView;
        this.nodeView = nodeView;
        this.labelInfo = label;
        this.labelInfo.addObserver(this);
        DiagramSchema diagramSchema = diagramView.getDiagramSchema();
		this.font = diagramSchema.getLabelFont();
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

    public Point2D getPosition() {
    	if( this.rect != null ) {
        	return new Point2D.Double(this.rect.getX(), this.rect.getY());
    	} else {
    		return this.nodeView.getPosition();
    	}
    }
    
    public boolean isVisible() {
    	return this.displayLines != 0;
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
        if (!isVisible()) {
            return;
        }

        // remember some settings to restore them later
        Paint oldPaint = graphics.getPaint();
        Font oldFont = graphics.getFont();

		graphics.setFont(this.font);

        // find the size and position
        updateBounds(graphics);
        double xPos = rect.getX();
        double yPos = rect.getY();
        double lw = rect.getWidth();
        double lh = rect.getHeight();

        // find colors to use
        DiagramSchema diagramSchema = diagramView.getDiagramSchema();
        Color backgroundColor = this.labelInfo.getBackgroundColor();
        Color textColor = this.labelInfo.getTextColor();
        Color scrollbarColorDark = Color.gray;
        Color scrollbarColorLight = Color.lightGray;
        if (isFaded()) {
            // lighten
            backgroundColor = diagramSchema.fadeOut(backgroundColor);
            textColor = diagramSchema.fadeOut(textColor);
            scrollbarColorDark = diagramSchema.fadeOut(scrollbarColorDark);
            scrollbarColorLight = diagramSchema.fadeOut(scrollbarColorLight);
        }

        // draw the label itself
        graphics.setPaint(backgroundColor);
        graphics.fill(rect);
        graphics.setPaint(textColor);

		float textWidth = (float) lw;
        
        // draw the contents
 		for (int i = this.firstItem; i < this.firstItem + this.displayLines; i++) {
			TextLayout cur;
 			if(i == this.firstItem && i != 0 && !scrollbarVisible()) {
 				cur = getMoreEntriesTextLayout(this.firstItem + 1, graphics.getFontRenderContext());
			} else if(i == this.firstItem + this.displayLines - 1 && i != this.getNumberOfEntries() - 1  && !scrollbarVisible()) {
				cur = getMoreEntriesTextLayout(this.getNumberOfEntries() - i, graphics.getFontRenderContext());
			} else {
				cur = getTextLayoutForEntry(i, graphics.getFontRenderContext());
			}
            int curPos = i - this.firstItem;
			float curWidth = (float)cur.getAdvance() + 2 * this.textmargin;
        	float textX;
            if (this.labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT) {
				textX = (float)xPos;
            } else if (this.labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER) {
                textX = (float)xPos + (textWidth - curWidth) / 2;
            } else if (this.labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT) {
                textX = (float)xPos + textWidth - curWidth;
            } else {
                throw new RuntimeException("Unknown label alignment.");
            }
			textX += this.textmargin;
			float textY = (float)yPos + curPos * this.lineHeight + cur.getAscent() + (this.lineHeight - getFullHeight(cur))/2;
            cur.draw(graphics, textX, textY);
        }

        // draw the scroller elements when needed
        int numItems = this.getNumberOfEntries();
        if (scrollbarVisible()) {
            FontRenderContext fontRenderContext = graphics.getFontRenderContext();
    		TextLayout mLayout = new TextLayout("M", this.font, fontRenderContext);
    		this.currentScrollBarWidth = (float) mLayout.getBounds().getWidth();

    		// cover text under the scrollbar
            graphics.setPaint(backgroundColor);
    		graphics.fill(new Rectangle2D.Double(xPos + lw - this.currentScrollBarWidth, yPos,
                    							this.currentScrollBarWidth, lh));
    		// draw a line separating the scrollbar from the rest
            graphics.setPaint(textColor);
            graphics.draw(new Line2D.Double(xPos + lw - this.currentScrollBarWidth, yPos,
                    xPos + lw - this.currentScrollBarWidth, yPos + lh));
            // calculate a size for the scroll buttons
            double width = this.currentScrollBarWidth * 0.8;
            double height = this.lineHeight * 0.8;
            if (width < height) {
                height = width;
            } else {
                width = height;
            }
            // draw the upwards triangle
            double xleft = xPos + lw - this.currentScrollBarWidth + (this.currentScrollBarWidth - width) / 2;
            double ytop = yPos + (this.lineHeight - height) / 2;
			
            if (this.firstItem != 0) {
                graphics.setPaint(scrollbarColorDark);
            } else {
                graphics.setPaint(scrollbarColorLight);
            }
            
            GeneralPath upwardTriangle = new GeneralPath();
            upwardTriangle.moveTo((float)(xleft), (float)(ytop + height));
			upwardTriangle.lineTo((float)(xleft + width / 2), (float)(ytop));
			upwardTriangle.lineTo((float)(xleft + width), (float)(ytop + height));
			upwardTriangle.closePath();
			graphics.fill(upwardTriangle);
			
            // draw the downwards triangle
            ytop = yPos + (this.displayLines - 2) * this.lineHeight + (this.lineHeight - height) / 2;
            
            if (this.firstItem + this.displayLines < numItems) { 
                graphics.setPaint(scrollbarColorDark);
            } else {
                graphics.setPaint(scrollbarColorLight); 
            }
            
			GeneralPath downwardTriangle = new GeneralPath();
			downwardTriangle.moveTo((float)(xleft), (float)(ytop));
			downwardTriangle.lineTo((float)(xleft + width / 2), (float)(ytop + height));
			downwardTriangle.lineTo((float)(xleft + width), (float)(ytop));
			downwardTriangle.closePath();
		    graphics.fill(downwardTriangle);
            
            // draw the current position
            double scale = (this.lineHeight * (this.displayLines - 3)) / (double) numItems;
            width = 0.8 * width;
            xleft = xPos + lw - this.currentScrollBarWidth + (this.currentScrollBarWidth - width) / 2;
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
        
        // draw the frame for the label
        graphics.setPaint(textColor);
        graphics.draw(rect);

        // restore old settings
        graphics.setPaint(oldPaint);
        graphics.setFont(oldFont);
    }

    private boolean scrollbarVisible() {
    	return (this.getNumberOfEntries() > MIN_DISPLAY_LINES) && this.scrollbarShown;
    }

	public void updateBounds(Graphics2D graphics) {
		if (this.getNumberOfEntries() == 0) {
			this.rect = null;
		}

        FontRenderContext fontRenderContext = graphics.getFontRenderContext();

		// margin is something dependend on the font, but the detailed choice is a bit
		// arbitrary
		TextLayout mLayout = new TextLayout("M", this.font, fontRenderContext);
		this.textmargin = mLayout.getLeading() + mLayout.getDescent();
        
        // find the size and position
        DiagramNode node = this.labelInfo.getNode();
        double x = node.getX();
        double y = node.getY();
        this.lineHeight = 0;
        double lw = 0;
        
        // find maximum width and height of string content
        for (int i = 0; i < getNumberOfEntries(); i++) {
            TextLayout cur = getTextLayoutForEntry(i, fontRenderContext);
        	double w = cur.getAdvance();
        	if (w > lw) {
        		lw = w;
        	}
        	float h = getFullHeight(cur);
        	if (h > this.lineHeight) {
        		this.lineHeight = h;
        	}
        }
        
        // check if we have at least one of the "...more entries..." markers and make sure
        // we have enough width for them
        if(this.displayLines < getNumberOfEntries()) {
        	// first create a number with just nines and as many digits as the number of
        	// entries in the label
        	int res = getNumberOfEntries();
        	int num = 9;
        	while(res > 10) {
        		res /= 10;
        		num = num * 10 + 9;
        	}
            TextLayout layout = getMoreEntriesTextLayout(num, fontRenderContext);
            if(layout.getBounds().getWidth() > lw) {
            	lw = layout.getBounds().getWidth();
            }
        }
        
		lw += 2 * textmargin;

        double lh = this.displayLines * this.lineHeight;
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
		this.rect = new Rectangle2D.Double(xPos, yPos, lw, lh);
    }

    private float getFullHeight(TextLayout layout) {
        return layout.getLeading() + layout.getAscent() + layout.getDescent();
    }

    private TextLayout getMoreEntriesTextLayout(int num, FontRenderContext fontRenderContext) {
        Font newFont = this.font.deriveFont(Font.ITALIC, this.font.getSize() * 0.8f);
        return new TextLayout("..." + num + " more...", newFont, fontRenderContext);
    }
    
    Point2D getConnectorStartPosition() {
        DiagramNode node = this.labelInfo.getNode();
        double radius = nodeView.getRadiusY();
        double x = node.getX();
        double y = node.getY();
        if (getPlacement() == ABOVE) {
            y = y - radius;
        } else {
            y = y + radius;
        }
        return new Point2D.Double(x,y);
    }
    
    protected Point2D getConnectorEndPosition() {
    	DiagramNode node=this.labelInfo.getNode();
		double rectX = this.rect.getX();
		double y = getConnectorStartPosition().getY();
		double lw = rect.getWidth();
    	double endY = y + this.labelInfo.getOffset().getY();
		if(rectX>node.getX()){
			return new Point2D.Double(rectX, endY);
    	}
    	else if(rectX+lw < node.getX()){
			return new Point2D.Double(rectX + lw, endY);
    	}
    	else{
			return new Point2D.Double(rectX + lw / 2, endY);
    	}
    }

    protected abstract boolean isFaded();
    
    protected Color getConnectorColor() {
        DiagramSchema diagramSchema = diagramView.getDiagramSchema();
        Color lineColor = diagramSchema.getLineColor();
    	if( isFaded() ) {
    		return diagramSchema.fadeOut(lineColor);
    	} else {
    		return lineColor;
    	}
    }

    /**
     * Returns the placement of the label (above or below the node).
     *
     * Possible return values are LabelView.ABOVE or LabelView.BELOW. This is
     * used to draw the labels in their appropriate position.
     */
    protected abstract int getPlacement();

    private TextLayout getTextLayoutForEntry(int position, FontRenderContext frc) {
    	String content = getEntryAt(position).toString();
		content = content.replace('\t',' ').replace('\n',' ');
    	TextLayout retVal = new TextLayout(content, this.font, frc);
    	return retVal;
    }

    /**
     * Returns true whenever the point is in the bounding rectangle.
     */
    public boolean containsPoint(Point2D point) {
        if (this.rect == null || isVisible() == false) {
            return false;
        }
        return this.rect.contains(point);
    }

    public void processDragEvent(Point2D from, Point2D to) {
    	if(this.dragMode == NOT_DRAGGING) {
    		return;
   	    } else if(this.dragMode == RESIZING) {
    	    int lineHit = (int) ((from.getY() - this.rect.getY()) / this.lineHeight);
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
  	    } else if(this.dragMode == SCROLLING) {
    	    // User wants to drag the scrollbar
    	    double scrollbarHeight = (this.displayLines - 3) * this.lineHeight;
    	    double scrollbarYToPos = to.getY() - this.rect.getY() - this.lineHeight;
    	    double relativePos = scrollbarYToPos / scrollbarHeight;
    	    int newLinePos = (int) (relativePos * (getNumberOfEntries() - this.displayLines));
    	    this.firstItem = newLinePos;
    	    ensureFirstItemBounds();
    	    notifyObservers();
   	    } else if(this.dragMode == MOVING) {
    	    double deltaX = to.getX() - from.getX();
    	    double deltaY = to.getY() - from.getY();
    	    this.labelInfo.setOffset(this.labelInfo.getOffset().getX() + deltaX,
    	            this.labelInfo.getOffset().getY() + deltaY);
    	}
    }
    
    public void startDrag(Point2D from, Point2D to) {
        if (scrollbarVisible() && (from.getX() >= this.rect.getMaxX() - this.currentScrollBarWidth)) {
            // we have a click on the scrollbar, calculate the line hit
            int lineHit = (int) ((from.getY() - this.rect.getY()) / this.lineHeight);
            if (lineHit == this.displayLines - 1) { // it is on the resize handle
            	this.dragMode = RESIZING;
            } else if( (lineHit >= 1) && (lineHit <= this.displayLines - 3) ){
                this.dragMode = SCROLLING;
            } else {
            	this.dragMode = NOT_DRAGGING;
            }
        } else {
            this.dragMode = MOVING;
        }
    	processDragEvent(from,to);
    }
    
	/**
	 * Method ensureFirstItemBounds.
	 */
	private void ensureFirstItemBounds() {
		if(firstItem<0){
			firstItem = 0;
		}
		if( firstItem > (getNumberOfEntries() - displayLines)){
			firstItem = getNumberOfEntries() - displayLines;
		}
	}

    /**
     * Handles scrolling of the items.
     */
    public void processClickEvent(Point2D pos) {
        if (scrollbarVisible() && (pos.getX() < this.rect.getMaxX() - this.currentScrollBarWidth)) {
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
		else if( (lineHit >= 1) && (lineHit <= this.displayLines - 3) ){
        	double scrollbarHeight = (this.displayLines - 3) * this.lineHeight;
			double scale = (1d * scrollbarHeight) / getNumberOfEntries();
			//get coordinate of top pos of scroll handle
			double topPosOfScrollHandle = this.rect.getY() + this.lineHeight + 
			                               this.firstItem * scale;
        	double bottomPosOfScrollHandle = topPosOfScrollHandle + 
        	                                  this.displayLines * scale;
        	
        	if( pos.getY() < topPosOfScrollHandle){ //user clicks above scrollbar
        		this.firstItem -= this.displayLines;
        		ensureFirstItemBounds();
        		notifyObservers();
			}else if ( pos.getY() > bottomPosOfScrollHandle ){ //user clicks below scrollbar
        		this.firstItem += this.displayLines;
        		ensureFirstItemBounds();
				notifyObservers();
        	}
        } 
    }

    public Rectangle2D getCanvasBounds(Graphics2D graphics) {
    	if(this.rect == null) {
    		return null;
    	}
    	Stroke stroke = graphics.getStroke();
        updateBounds(graphics);
    	if (stroke instanceof BasicStroke) {
    		double w = ((BasicStroke) stroke).getLineWidth();
    		return new Rectangle2D.Double(this.rect.getX() - w/2, this.rect.getY() - w/2,
										   this.rect.getWidth() + w, 
										   this.rect.getHeight() + w);
    	}
		return new Rectangle2D.Double(this.rect.getX(), this.rect.getY(),
									   this.rect.getWidth(), 
									   this.rect.getHeight());
    }

    /**
     * Calculates which item was hit.
     *
     * This is the number of the entry, not on the screen but in the full list,
     * i.e. the offset from scrolling is used. If no item was hit (e.g. a click
     * on the scrollbar or the position is not on the label), -1 will be returned.
     */
    public int getIndexOfPosition(Point2D pos) {
        if (pos.getX() > this.rect.getMaxX()) {
            // a click on the right of the label
            return -1;
        }
        if (scrollbarVisible() &&(pos.getX() > this.rect.getMaxX() - this.currentScrollBarWidth)) {
            // a click on the scrollbar
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

    abstract public int getNumberOfEntries();

    abstract public Object getEntryAt(int position);

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

    public void addObserver(ChangeObserver observer) {
        this.observers.add(observer);
    }

    protected void notifyObservers() {
        for (Iterator iterator = observers.iterator(); iterator.hasNext();) {
            ChangeObserver changeObserver = (ChangeObserver) iterator.next();
            changeObserver.update(this);
        }
    }
    
    public NodeView getNodeView() {
        return nodeView;
    }

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void showScrollbar(boolean scrollbarShown) {
		this.scrollbarShown = scrollbarShown;
		this.diagramView.update(this);
	}
}
