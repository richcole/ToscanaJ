/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

/**
 * This class encapsulates all information needed to paint a label.
 */

public class LabelInfo implements XML_Serializable, ChangeObservable {
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
    private static final String LABEL_INFO_ELEMENT_NAME = "labelStyle";
    private static final String OFFSET_ELEMENT_NAME = "offset";
    private static final String OFFSET_X_ATTRIBUTE_NAME = "x";
    private static final String OFFSET_Y_ATTRIBUTE_NAME = "y";
    private static final String TEXT_COLOR_ELEMENT_NAME = "textColor";
    private static final String BACKGROUND_COLOR_ELEMENT_NAME = "backgroundColor";
    private static final String TEXT_ALIGNMENT_ELEMENT_NAME = "textAlignment";
    private static final String TEXT_ALIGNMENT_LEFT_CONTENT = "left";
    private static final String TEXT_ALIGNMENT_CENTER_CONTENT = "center";
    private static final String TEXT_ALIGNMENT_RIGHT_CONTENT = "right";

    /**
     * The default constructor creates a label with default settings.
     *
     * A node has to be attached to it by calling attachNode(DiagramNode).
     * The node is used for finding the position for the diagram line and to
     * access the concept with the information on the contingents (strings).
     */
    public LabelInfo() {
        this.offset = new Point2D.Double(0, 0);
        this.backgroundColor = Color.white;
        this.textColor = Color.black;
        this.textAlignment = ALIGNLEFT;
        labelObservers = new Vector();
    }

    /**
     * The copy constructor makes a deep copy without the observers.
     */
    public LabelInfo(LabelInfo other) {
        this.offset = (Point2D) other.offset.clone();
        this.backgroundColor = new Color(other.backgroundColor.getRed(),
                other.backgroundColor.getGreen(),
                other.backgroundColor.getBlue(),
                other.backgroundColor.getAlpha());
        this.textColor = new Color(other.textColor.getRed(),
                other.textColor.getGreen(),
                other.textColor.getBlue(),
                other.textColor.getAlpha());
        this.textAlignment = other.textAlignment;
        labelObservers = new Vector();
    }

    public LabelInfo(Element element) throws XML_SyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(LABEL_INFO_ELEMENT_NAME);
        Element offsetElem = new Element(OFFSET_ELEMENT_NAME);
        offsetElem.setAttribute(OFFSET_X_ATTRIBUTE_NAME, String.valueOf(offset.getX()));
        offsetElem.setAttribute(OFFSET_Y_ATTRIBUTE_NAME, String.valueOf(offset.getY()));
        retVal.addContent(offsetElem);
        Element backgroundColorElem = new Element(BACKGROUND_COLOR_ELEMENT_NAME);
        backgroundColorElem.addContent("#" + Integer.toHexString(backgroundColor.getRGB()));
        retVal.addContent(backgroundColorElem);
        Element textColorElem = new Element(TEXT_COLOR_ELEMENT_NAME);
        textColorElem.addContent("#" + Integer.toHexString(textColor.getRGB()));
        retVal.addContent(textColorElem);
        Element textAlignmentElem = new Element(TEXT_ALIGNMENT_ELEMENT_NAME);
        switch(textAlignment) {
            case ALIGNLEFT:
                textAlignmentElem.addContent(TEXT_ALIGNMENT_LEFT_CONTENT);
                break;
            case ALIGNCENTER:
                textAlignmentElem.addContent(TEXT_ALIGNMENT_CENTER_CONTENT);
                break;
            case ALIGNRIGHT:
                textAlignmentElem.addContent(TEXT_ALIGNMENT_RIGHT_CONTENT);
                break;
        }
        retVal.addContent(textAlignmentElem);
        return retVal;
    }

    public void readXML(Element elem) throws XML_SyntaxError {
        throw new XML_SyntaxError("Not yet implemented");
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
     * Returns the current offset.
     */
    public Point2D getOffset() {
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
    public void setOffset(Point2D offset) {
        this.offset = offset;
        emitChangeSignal();
    }

    /**
     * A convenience method mapping to setOffset(Point2D).
     */
    public void setOffset(double x, double y) {
        setOffset(new Point2D.Double(x, y));
    }

    /**
     * Returns the current background color.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * Sets the background color.
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        emitChangeSignal();
    }

    /**
     * Returns the current text color.
     */
    public Color getTextColor() {
        return this.textColor;
    }

    /**
     * Sets the text color.
     */
    public void setTextColor(Color color) {
        this.textColor = color;
        emitChangeSignal();
    }

    /**
     * Returns the current text alignment.
     */
    public int getTextAlignment() {
        return this.textAlignment;
    }

    /**
     * Sets the alignment of the text.
     */
    public void setTextAligment(int alignment) {
        this.textAlignment = alignment;
        emitChangeSignal();
    }

    /**
     * Method to add an observer.
     */
    public void addObserver(ChangeObserver observer) {
        this.labelObservers.addElement(observer);
    }

    /**
     * Method to remove an observer.
     */
    public void removeObserver(ChangeObserver observer) {
        this.labelObservers.remove(observer);
    }

    /**
     * Notifies all observes about a change.
     */
    private void emitChangeSignal() {
        if (labelObservers != null) {
            Iterator iterator = labelObservers.iterator();
            while (iterator.hasNext()) {
                ((ChangeObserver) iterator.next()).update(this);
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