/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.gradients;

import java.awt.Color;


public class LinearGradient implements Gradient {
	private Color fromColor;
	private Color toColor;
	
	public LinearGradient(Color from, Color to) {
		this.fromColor = from;
		this.toColor = to;
	}
	
	public Color getColor(double position) {
		if (position < 0 || position > 1) {
			throw new IllegalArgumentException("Gradient position not in [0,1]");
		}
		return new Color((int) (toColor.getRed() * position + fromColor.getRed() * (1 - position)),
				(int) (toColor.getGreen() * position + fromColor.getGreen() * (1 - position)),
				(int) (toColor.getBlue() * position + fromColor.getBlue() * (1 - position)),
				(int) (toColor.getAlpha() * position + fromColor.getAlpha() * (1 - position)));
	}
}
