/*
 * User: Serhiy Yevtushenko
 * Date: May 28, 2002
 * Time: 12:26:35 AM
 */
package util.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JSplitPaneWithFixedRightPane extends JSplitPane {
    ComponentListener resizeListener = new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
            constrainDividerLocation();
        }
    };
    PropertyChangeListener dividerLocationListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(evt.getPropertyName())) {
                constrainDividerLocation();
            }
        }
    };

    public JSplitPaneWithFixedRightPane(int newOrientation) {
        super(newOrientation);
        setOneTouchExpandable(true);
        addComponentListener(resizeListener);
        addPropertyChangeListener(dividerLocationListener);
    }

    public JSplitPaneWithFixedRightPane() {
        this(JSplitPane.HORIZONTAL_SPLIT);
    }

    private void constrainDividerLocation() {
        int minimalDividerLocation = calcMinimalDividerLocation();
        if (getDividerLocation() < minimalDividerLocation) {
            setDividerLocation(minimalDividerLocation);
        }
    }

    private int calcMinimalDividerLocation() {
        Dimension currentSize = getSize();
        Dimension rightPaneMaximumSize = getRightComponent().getMaximumSize();
        int minimalDividerLocation = 0;
        if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            minimalDividerLocation = currentSize.width - rightPaneMaximumSize.width - getDividerSize();
        } else {
            minimalDividerLocation = currentSize.height - rightPaneMaximumSize.height - getDividerSize();
        }
        return minimalDividerLocation;
    }
}
