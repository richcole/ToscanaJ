/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;
import org.tockit.util.ColorStringConverter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class encapsulates all information needed to paint a label.
 */

public class LabelInfo implements XMLizable, ChangeObservable {
    /**
     * List of LabelObserver implementations currently observing the instance.
     */
    private Vector labelObservers = new Vector();

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
        this(new Point2D.Double(), Color.white, Color.black, ALIGNLEFT);
    }

    private LabelInfo(Point2D offset, Color backColor, Color textColor, int alignment) {
        this.offset = offset;
        this.backgroundColor = backColor;
        this.textColor = textColor;
        this.textAlignment = alignment;
    }

    /**
     * The copy constructor makes a deep copy without the observers.
     */
    public LabelInfo(LabelInfo other) {
        this((Point2D) other.offset.clone(),
                makeColorCopy(other.backgroundColor),
                makeColorCopy(other.textColor),
                other.textAlignment
        );
        this.node = other.node;
    }

    private static Color makeColorCopy(Color color) {
        return new Color(color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getAlpha());
    }

    public LabelInfo(Element element) throws XMLSyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(LABEL_INFO_ELEMENT_NAME);
        Element offsetElem = new Element(OFFSET_ELEMENT_NAME);
        offsetElem.setAttribute(OFFSET_X_ATTRIBUTE_NAME, String.valueOf(offset.getX()));
        offsetElem.setAttribute(OFFSET_Y_ATTRIBUTE_NAME, String.valueOf(offset.getY()));
        retVal.addContent(offsetElem);
        Element backgroundColorElem = new Element(BACKGROUND_COLOR_ELEMENT_NAME);
        backgroundColorElem.addContent(ColorStringConverter.colorToString(backgroundColor));
        retVal.addContent(backgroundColorElem);
        Element textColorElem = new Element(TEXT_COLOR_ELEMENT_NAME);
        textColorElem.addContent(ColorStringConverter.colorToString(textColor));
        retVal.addContent(textColorElem);
        Element textAlignmentElem = new Element(TEXT_ALIGNMENT_ELEMENT_NAME);
        switch (textAlignment) {
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

    public void readXML(Element elem) throws XMLSyntaxError {
        if (!elem.getName().equals(LABEL_INFO_ELEMENT_NAME)) {
            if (!elem.getName().equals(DiagramNode.ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME)) {
                if (!elem.getName().equals(DiagramNode.OBJECT_LABEL_STYLE_ELEMENT_NAME)) {
                    throw new XMLSyntaxError("Expected either " +
                            LABEL_INFO_ELEMENT_NAME + " or " +
                            DiagramNode.ATTRIBUTE_LABEL_STYLE_ELEMENT_NAME + " or " +
                            DiagramNode.OBJECT_LABEL_STYLE_ELEMENT_NAME);
                }
            }
        }
        Element offsetElem = XMLHelper.getMandatoryChild(elem, OFFSET_ELEMENT_NAME);
        setOffset(
                XMLHelper.getDoubleAttribute(offsetElem, OFFSET_X_ATTRIBUTE_NAME),
                XMLHelper.getDoubleAttribute(offsetElem, OFFSET_Y_ATTRIBUTE_NAME)
        );
        Element backgroundColorElem = XMLHelper.getMandatoryChild(elem, BACKGROUND_COLOR_ELEMENT_NAME);
        setBackgroundColor(ColorStringConverter.stringToColor(backgroundColorElem.getText()));
        Element textColorElem = XMLHelper.getMandatoryChild(elem, TEXT_COLOR_ELEMENT_NAME);
        setTextColor(ColorStringConverter.stringToColor(textColorElem.getText()));

        readTextAlignment(elem);
    }

    private void readTextAlignment(Element elem) throws XMLSyntaxError {
        Element textAlignmentElem = XMLHelper.getMandatoryChild(elem, TEXT_ALIGNMENT_ELEMENT_NAME);
        String textAlignmentElemText = textAlignmentElem.getText();
        if (textAlignmentElemText.equals(TEXT_ALIGNMENT_LEFT_CONTENT)) {
            textAlignment = ALIGNLEFT;
        } else if (textAlignmentElemText.equals(TEXT_ALIGNMENT_CENTER_CONTENT)) {
            textAlignment = ALIGNCENTER;
        } else if (textAlignmentElemText.equals(TEXT_ALIGNMENT_RIGHT_CONTENT)) {
            textAlignment = ALIGNRIGHT;
        } else {
            throw new XMLSyntaxError(
                    "Unknown value " + textAlignmentElemText + " for text alignment."
            );
        }
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
    public void setTextAlignment(int alignment) {
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
        if(this.node != null) {
			this.node.diagram.sendChangeEvent();
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LabelInfo)) {
            return false;
        }
        LabelInfo that = (LabelInfo) obj;
        if (!this.getBackgroundColor().equals(that.getBackgroundColor())) {
            return false;
        }
        if (!this.getTextColor().equals(that.getTextColor())) {
            return false;
        }
        if (this.getTextAlignment() != that.getTextAlignment()) {
            return false;
        }
        if (!this.getOffset().equals(that.getOffset())) {
            return false;
        }

        return true;
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
