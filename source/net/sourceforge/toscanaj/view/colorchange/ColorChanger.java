/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.colorchange;

import java.awt.Color;
import java.awt.Graphics2D;


public interface ColorChanger {
    Color changeColor(Color originalColor);
    Graphics2D getGraphics2D(Graphics2D original);
}
