package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.diagram.LabelObservable;
import net.sourceforge.toscanaj.MainPanel;
import net.sourceforge.toscanaj.view.diagram.LabelObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * This class encapsulates all information needed to paint a label.
 */

abstract public class LabelInfo implements LabelObservable
{
    /**
     * List of LabelObserver implementations currently observing the instance.
     */
    private Vector labelObservers = null;

    /**
     * The node the label belongs to.
     */
    private DiagramNode node;

    /**
     * The offset for the label position.
     */
    private Point2D offset;

    /**
     * The background color for the label.
     */
    private Color backgroundColor;

    /**
     * The background color for the label.
     */
    private Color textColor;

    /**
     * The alignment of the text in the label.
     */
    private int textAlignment;

    /**
     * A constant for left alignment.
     */
    public static final int ALIGNLEFT = 0;

    /**
     * A constant for center alignment.
     */
    public static final int ALIGNCENTER = 1;

    /**
     * A constant for right alignment.
     */
    public static final int ALIGNRIGHT = 2;

    /**
     * The default constructor creates a label with default settings.
     *
     * A node has to be attached to it by calling attachNode(DiagramNode).
     * The node is used for finding the position for the diagram line and to
     * access the concept with the information on the contingents (strings).
     */
    public LabelInfo()
    {
        this.offset = new Point2D.Double( 0, 0 );
        this.backgroundColor = Color.white;
        this.textColor = Color.black;
        this.textAlignment = ALIGNLEFT;
        labelObservers = new Vector();
    }

    /**
     * Attaches the node as the node belonging to the label.
     *
     * Access is package level here since this should be called from DiagramNode.
     */
    void attachNode(DiagramNode node) {
        this.node = node;
    }

    /**
     * Returns the node the label belongs to.
     */
    public DiagramNode getNode() {
        return this.node;
    }

    /**
     * Returns the number of entries in the label.
     */
    abstract public int getNumberOfEntries();

    /**
     * Returns an iterator on the entries in the label.
     */
    abstract public Iterator getEntryIterator();

    /**
     * Returns the current offset.
     */
    public Point2D getOffset()
    {
        return this.offset;
    }

    /**
     * Sets the label offset.
     *
     * The offset defines how far the label is moved from the point. The
     * bahaviour is different for attribute and object labels: the attribute
     * labels are positioned on top of a point, directly connecting to the
     * top edge if the offset is (0,0). The object labels are below the points,
     * contacting them at the bottom.
     */
    public void setOffset( Point2D offset )
    {
        this.offset = offset;
        emitChangeSignal();
    }

    /**
     * A convenience method mapping to setOffset(Point2D).
     */
    public void setOffset( double x, double y ) {
        setOffset(new Point2D.Double(x,y));
    }

    /**
     * Returns the current background color.
     */
    public Color getBackgroundColor()
    {
        return this.backgroundColor;
    }

    /**
     * Sets the background color.
     */
    public void setBackgroundColor( Color color )
    {
        this.backgroundColor = color;
        emitChangeSignal();
    }

    /**
     * Returns the current text color.
     */
    public Color getTextColor()
    {
        return this.textColor;
    }

    /**
     * Sets the text color.
     */
    public void setTextColor( Color color )
    {
        this.textColor = color;
        emitChangeSignal();
    }

    /**
     * Returns the current text alignment.
     */
    public int getTextAlignment()
    {
        return this.textAlignment;
    }

    /**
     * Sets the alignment of the text.
     */
    public void setTextAligment( int alignment )
    {
        this.textAlignment = alignment;
        emitChangeSignal();
    }

    /**
     * Method to add an observer.
     */
    public void addObserver(LabelObserver observer){
        this.labelObservers.addElement(observer);
    }

    /**
     * Notifies all observes about a change.
     */
    private void emitChangeSignal(){
        if(labelObservers != null){
            Iterator iterator = labelObservers.iterator();
            while(iterator.hasNext()) {
                ((LabelObserver)iterator.next()).labelChanged();
            }
        }
    }
}