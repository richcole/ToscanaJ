/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 15, 2002
 * Time: 9:14:28 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.canvas.events;

import org.tockit.canvas.CanvasItem;
import java.awt.geom.Point2D;

public class CanvasItemMouseMovementEvent extends CanvasItemEventWithPosition {
    public CanvasItemMouseMovementEvent(CanvasItem item, int modifiers,
                                   Point2D canvasPosition, Point2D awtPosition) {
        super(item, modifiers, canvasPosition, awtPosition);
    }
}
