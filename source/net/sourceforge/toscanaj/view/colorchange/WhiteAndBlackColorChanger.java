/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.colorchange;

import java.awt.Color;


public class WhiteAndBlackColorChanger extends AbstractColorChanger {
    public Color changeColor(Color originalColor) {
        if(originalColor.equals(Color.BLACK)) {
            return originalColor;
        } else {
            return Color.WHITE;
        }
    }
}
