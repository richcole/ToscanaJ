/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.colorchange;

import java.awt.Color;
import java.awt.color.ColorSpace;


public class GrayscaleColorChanger extends AbstractColorChanger {
    public Color changeColor(Color originalColor) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        float[] grayComp = cs.fromRGB(originalColor.getRGBColorComponents(null));
        return new Color(cs, grayComp, originalColor.getAlpha()/255f);
    }
}
