/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.util.gradients;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class combines different gradients into a single one.
 * 
 * Each gradient gets a weight assigned, which defines the relative lengths
 * of the gradients. For example a gradient with the weight 2 will have twice
 * the length of a gradient with weight 1. Since the final interval will be
 * the normed [0,1] interval again, each gradient's length in the final
 * result will be its weight divided by the sum of all weights.
 */
public class CombinedGradient implements Gradient {
	private double totalWeights;
	private ArrayList<GradientPart> gradientParts = new ArrayList<GradientPart>();
	
	private static class GradientPart {
		Gradient gradient;
		double weight;

		GradientPart(Gradient gradient, double weight) {
			this.gradient = gradient;
			this.weight = weight;
		}

		Color getColor(double weightedPosition) {
			double position = weightedPosition / this.weight;
			return this.gradient.getColor(position);
		}
	}
	
	public CombinedGradient(Gradient firstGradient, double weight) {
		addGradientPart(firstGradient, weight);		
	}

    public void addGradientPart(Gradient gradient, double weight) {
    	this.gradientParts.add(new GradientPart(gradient, weight));
    	this.totalWeights += weight;
    }

    public Color getColor(double position) {
		if (position < 0 || position > 1) {
			throw new IllegalArgumentException("Gradient position not in [0,1]");
		}
		double curPos = 0;
		double weightedPos = position * this.totalWeights;
		for (Iterator<GradientPart> iter = this.gradientParts.iterator(); iter.hasNext();) {
			GradientPart part = iter.next();
            if(curPos + part.weight >= weightedPos) {
            	return part.getColor(weightedPos - curPos);    
            }
            curPos += part.weight;
		}
		// we shouldn't get here
		throw new IllegalArgumentException("Gradient position not in [0,1]");
    }
}
