/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;

import java.awt.geom.Point2D;
import java.util.*;

public abstract class NDimLayoutOperations {
    // constants for base vector calculation
    private static final double BASE_SCALE = 20;
    private static final double BASE_X_STRETCH = 2;
	private static final double BASE_X_SHEAR = 0.3;

    public static final Diagram2D createDiagram(Lattice lattice, String title,
                                                DimensionCreationStrategy dimensionStrategy) {
        Vector dimensions = dimensionStrategy.calculateDimensions(lattice);
        Vector base = createBase(dimensions.size());
        NDimDiagram diagram = new NDimDiagram(base);
        diagram.setTitle(title);
        Concept[] concepts = lattice.getConcepts();
        Hashtable nodemap = new Hashtable();
        double[] topVector = null;
        for (int i = 0; i < concepts.length; i++) {
            Concept concept = concepts[i];
            double[] ndimVector = new double[dimensions.size()];
            Iterator attributes = concept.getIntentIterator();
            while (attributes.hasNext()) {
                Attribute attribute = (Attribute) attributes.next();
                addVector(ndimVector, attribute, dimensions);
            }
            if (concept.isTop()) {
                topVector = ndimVector;
            }
            DiagramNode node = new NDimDiagramNode(diagram, String.valueOf(i), ndimVector, concept,
                    								new LabelInfo(), new LabelInfo(), null);
            nodemap.put(concept, node);
            diagram.addNode(node);
        }
        // the top node has to be at (0,0), otherwise base changes will affect the overall position of the diagram
        Iterator it = nodemap.values().iterator();
        while (it.hasNext()) {
            NDimDiagramNode node = (NDimDiagramNode) it.next();
            node.setNdimVector(substract(node.getNdimVector(), topVector));
        }
        createConnections(diagram, nodemap);
        changeBase(diagram);
        return diagram;
    }

    private static void changeBase(NDimDiagram diagram) {
    	int n = diagram.getBase().size();
    	int[] perm = new int[n];
    	for (int i = 0; i < perm.length; i++) {
			perm[i] = i;
		}
		int numVectors = n;
		Vector largeBase = createBase(numVectors);
		
		Vector bestBase = new Vector();
		double maxMinDist = -1;
		do {
			Vector curBase = new Vector();
			for (int i = 0; i < perm.length; i++) {
				int cur = perm[i];
				curBase.add(largeBase.get(cur));
			}
			diagram.setBase(curBase);
			double curDist = calculateMinimumDistance(diagram);
			if(curDist > maxMinDist) {
				maxMinDist = curDist;
				bestBase = curBase;
			}
			perm = findNextPermutation(perm, numVectors);
		} while(perm != null);
		diagram.setBase(bestBase);
	}

	private static int[] findNextPermutation(int[] currentPermutation, int numValues) {
		boolean last = true;
		for (int i = 0; i < currentPermutation.length; i++) {
			int cur = currentPermutation[i];
			if(cur != numValues - i - 1) {
				last=false;
				break;
			}
		}
		if(last) {
			return null;
		}
		int[] result = increasePermutation(currentPermutation, numValues);
		while(!isValid(result)) {
			result = increasePermutation(result, numValues);
		}
		
		return result;
	}

	private static boolean isValid(int[] testArray) {
		for (int i = 0; i < testArray.length; i++) {
			int cur = testArray[i];
			for (int j = i + 1; j < testArray.length; j++) {
				if(testArray[j] == cur) {
					return false;
				}
			}
		}
		return true;
	}

	private static int[] increasePermutation(int[] currentPermutation, int numValues) {
		int overflow = 1;
		int[] result = new int[currentPermutation.length];
		for (int i = currentPermutation.length - 1; i >= 0; i--) {
			result[i] = currentPermutation[i] + overflow;
			if(result[i] > numValues - 1) {
				result[i] = 0;
				overflow = 1;
			} else {
				overflow = 0;
			}
		}
		return result;
	}

	private static double calculateMinimumDistance(NDimDiagram diagram) {
		Iterator lineIt = diagram.getLines();
		double minDist = Double.MAX_VALUE;
		while(lineIt.hasNext()) {
			DiagramLine line = (DiagramLine) lineIt.next();
			Iterator nodeIt = diagram.getNodes();
			while(nodeIt.hasNext()) {
				DiagramNode node = (DiagramNode) nodeIt.next();
				if(line.getFromNode() == node) {
					continue;
				}
				if(line.getToNode() == node) {
					continue;
				}
				double distFrom = node.getPosition().distance(line.getFromPosition());
				double distTo = node.getPosition().distance(line.getToPosition());
				double lengthLine = line.getFromPosition().distance(line.getToPosition());
				double curDist;
				if(distFrom > lengthLine) {
					curDist = distTo;
				} else if(distTo > lengthLine) {
					curDist = distFrom;
				} else {
					curDist = calculateDistanceToLine(node, line);
				}
				if(curDist < minDist) {
					minDist = curDist;
				}
			}
		}
		return minDist;
	}

	private static double calculateDistanceToLine(DiagramNode node, DiagramLine line) {
		Point2D from = line.getFromPosition();
		Point2D to = line.getToPosition();
		
		double hesseX = from.getY() - to.getY();
		double hesseY = to.getX() - from.getX();
		double length = Math.sqrt(hesseX * hesseX + hesseY * hesseY);
		hesseX /= length; 
		hesseY /= length;
		
		double hesseV = from.getX() * hesseX + from.getY() * hesseY;
		double nodeV = node.getX() * hesseX + node.getY() * hesseY;
		 
		return Math.abs(hesseV - nodeV);
	}

	public static double[] substract(double[] a, double[] b) {
        double[] retVal = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            retVal[i] = a[i] - b[i];
        }
        return retVal;
    }

    /**
     * Creates a set of base vectors.
     *
     * The return value is a set of base vectors as given in Frank Vogt's book "Formale Begriffsanalyse mit C++",
     * page 61, rescaled and stretched by the two constants BASE_SCALE and BASE_X_STRETCH, and sheared by BASE_X_SHEAR.
     */
    private static Vector createBase(int n) {
        Vector base = new Vector();
        for(int i = n - 1;i >= 0; i--) {
            double a = 1 << i;
            double b = 1 << (n - i - 1);
            base.add(new Point2D.Double((a - b + BASE_X_SHEAR) * BASE_X_STRETCH * BASE_SCALE,
                    (a + b) * BASE_SCALE));
        }
        return base;
    }

    private static void addVector(double[] ndimVector, Attribute attribute, Vector dimensions) {
        int dimCount = 0;
        for (Iterator it = dimensions.iterator(); it.hasNext();) {
            Dimension dimension = (Dimension) it.next();
            if (dimension.contains(attribute)) {
                ndimVector[dimCount] += 1;
            }
            dimCount++;
        }
    }

    private static void createConnections(SimpleLineDiagram diagram, Hashtable nodemap) {
        Iterator nodesIt = diagram.getNodes();
        while (nodesIt.hasNext()) {
            DiagramNode node = (DiagramNode) nodesIt.next();
            Concept concept = node.getConcept();
            Collection upset = new HashSet(concept.getUpset());
            upset.remove(concept);
            Set indirectSuperConcepts = new HashSet();
            for (Iterator iterator = upset.iterator(); iterator.hasNext();) {
                Concept superconcept = (Concept) iterator.next();
                Collection upset2 = new HashSet(superconcept.getUpset());
                upset2.remove(superconcept);
                indirectSuperConcepts.addAll(upset2);
            }
            upset.removeAll(indirectSuperConcepts);
            for (Iterator iterator = upset.iterator(); iterator.hasNext();) {
                Concept upperNeighbour = (Concept) iterator.next();
                diagram.addLine((DiagramNode) nodemap.get(upperNeighbour), node);
            }
        }
    }
}
