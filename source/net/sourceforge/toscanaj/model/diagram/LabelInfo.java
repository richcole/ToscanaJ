package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.MainPanel;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * This class encapsulates all information needed to paint a label.
 */

abstract public class LabelInfo implements ChangeObservable
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
    private Point2D offset = null;

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
     * The copy constructor makes a deep copy without the observers.
     */
    public LabelInfo(LabelInfo other) {
        this.offset = (Point2D)other.offset.clone();
        this.backgroundColor = new Color( other.backgroundColor.getRed(),
                                          other.backgroundColor.getGreen(),
                                          other.backgroundColor.getBlue(),
                                          other.backgroundColor.getAlpha() );
        this.textColor = new Color( other.textColor.getRed(),
                                    other.textColor.getGreen(),
                                    other.textColor.getBlue(),
                                    other.textColor.getAlpha() );
        this.textAlignment = other.textAlignment;
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
     *
     * If the parameter is set to false the full set (extent or intent) will be
     * used, if set to true only the contingent is used.
     */
    abstract public int getNumberOfEntries(boolean contingentOnly);

    /**
     * Returns the number of entries in the label relative to the complete
     * number of possible entries.
     *
     * The return value will be between 0 (no entry) and 1 (every single object/
     * attribute is on this label).
     *
     * If the parameter is set to false the full set (extent or intent) will be
     * used, if set to true only the contingent is used.
     */
    abstract public double getNumberOfEntriesRelative(boolean contingentOnly);

    /**
     * Returns an iterator on the entries in the label.
     *
     * If the parameter is set to false the full set (extent or intent) will be
     * used, if set to true only the contingent is used.
     */
    abstract public Iterator getEntryIterator(boolean contingentOnly);

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
    public void addObserver(ChangeObserver observer){
        this.labelObservers.addElement(observer);
    }

    /**
     * Method to remove an observer.
     */
    public void removeObserver(ChangeObserver observer){
        this.labelObservers.remove(observer);
    }

    /**
     * Notifies all observes about a change.
     */
    private void emitChangeSignal(){
        if(labelObservers != null){
            Iterator iterator = labelObservers.iterator();
            while(iterator.hasNext()) {
                ((ChangeObserver)iterator.next()).update(this);
            }
        }
    }

    /**
     * Debug output.
     */
    public String toString() {
        String retVal = "LabelInfo:\n";
        retVal += "Offset: (" + offset.getX() + "," + offset.getY() + ")\n";
        retVal += "Align: " + textAlignment + "\n";
        retVal += "Colors: " + textColor + " on " + backgroundColor;
        return retVal;
    }
}