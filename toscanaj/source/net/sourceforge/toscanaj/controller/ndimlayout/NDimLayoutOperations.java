/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;

public abstract class NDimLayoutOperations {
    // constants for base vector calculation
    private static final double BASE_SCALE = 80;
    private static final double BASE_X_STRETCH = 1;
	private static final double BASE_X_SHEAR = 0.3;
	
	private NDimLayoutOperations(){
		// no instances
	}

    public static final<O,A> NDimDiagram<O,A> createDiagram(Lattice<O,A> lattice, String title,
                                                DimensionCreationStrategy<A> dimensionStrategy) {
        List<Dimension<A>> dimensions = dimensionStrategy.calculateDimensions(lattice);
        Vector<Point2D> base = createBase(dimensions.size());
        NDimDiagram<O,A> diagram = new NDimDiagram<O,A>(base);
        diagram.setTitle(title);
        Concept<O,A>[] concepts = lattice.getConcepts();
        Hashtable<Concept<O,A>, DiagramNode<O,A>> nodemap = new Hashtable<Concept<O,A>, DiagramNode<O,A>>();
        double[] topVector = null;
        for (int i = 0; i < concepts.length; i++) {
            Concept<O,A> concept = concepts[i];
            double[] ndimVector = new double[dimensions.size()];
            Iterator<A> attributes = concept.getIntentIterator();
            while (attributes.hasNext()) {
                A attribute = attributes.next();
                addVector(ndimVector, attribute, dimensions);
            }
            if (concept.isTop()) {
                topVector = ndimVector;
            }
            DiagramNode<O,A> node = new NDimDiagramNode<O,A>(diagram, String.valueOf(i), ndimVector, concept,
                    								new LabelInfo(), new LabelInfo(), null);
            nodemap.put(concept, node);
            diagram.addNode(node);
        }
        // the top node has to be at (0,0), otherwise base changes will affect the overall position of the diagram
        Iterator<DiagramNode<O,A>> it = nodemap.values().iterator();
        while (it.hasNext()) {
            NDimDiagramNode<O,A> node = (NDimDiagramNode<O,A>) it.next();
            node.setNdimVector(substract(node.getNdimVector(), topVector));
        }
        createConnections(diagram, nodemap);
        if(diagram.getBase().size() <= 5) {
            changeBase(diagram);
        }
        return diagram;
    }

    private static<O,A> void changeBase(NDimDiagram<O,A> diagram) {
    	int n = diagram.getBase().size();
    	int[] perm = new int[n];
    	for (int i = 0; i < perm.length; i++) {
			perm[i] = i;
		}
		int numVectors = n;
		Vector<Point2D> largeBase = createBase(numVectors);
		
		Vector<Point2D> bestBase = new Vector<Point2D>();
		double maxMinDist = -1;
		do {
			Vector<Point2D> curBase = new Vector<Point2D>();
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

	private static<O,A> double calculateMinimumDistance(NDimDiagram<O,A> diagram) {
		Iterator<DiagramLine<O,A>> lineIt = diagram.getLines();
		double minDist = Double.MAX_VALUE;
		while(lineIt.hasNext()) {
			DiagramLine<O,A> line = lineIt.next();
			Iterator<DiagramNode<O,A>> nodeIt = diagram.getNodes();
			while(nodeIt.hasNext()) {
				DiagramNode<O,A> node = nodeIt.next();
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

    /**
     * @todo this could probably use DiagramLine.calculateDistance(Point2D), which also
     * considers the end points
     */
	private static<O,A> double calculateDistanceToLine(DiagramNode<O,A> node, DiagramLine<O,A> line) {
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
     * The return value is a set of base vectors similar to the ones given in Frank Vogt's book 
     * "Formale Begriffsanalyse mit C++", page 61.
     * 
     * The calculation has been changed to have a more constant scale (the height of the middle vector
     * is roughly constant) and it is rescaled and stretched by the two constants BASE_SCALE and 
     * BASE_X_STRETCH, and sheared by BASE_X_SHEAR.
     */
    private static Vector<Point2D> createBase(int n) {
        Vector<Point2D> base = new Vector<Point2D>();
        for(int i = n - 1;i >= 0; i--) {
            double a = Math.pow(2, i);
            double b = Math.pow(2, n - i - i);
            double scale = BASE_SCALE / Math.pow(2, n - 1);
            base.add(new Point2D.Double((a - b + BASE_X_SHEAR) * BASE_X_STRETCH * scale,
                    (a + b) * scale));
        }
        return base;
    }

    private static<A> void addVector(double[] ndimVector, A attribute, List<Dimension<A>> dimensions) {
        int dimCount = 0;
        for (Iterator<Dimension<A>> it = dimensions.iterator(); it.hasNext();) {
            Dimension<A> dimension = it.next();
            if (dimension.contains(attribute)) {
                ndimVector[dimCount] += 1;
            }
            dimCount++;
        }
    }

    private static<O,A> void createConnections(SimpleLineDiagram<O,A> diagram, Hashtable<Concept<O,A>, DiagramNode<O,A>> nodemap) {
        Iterator<DiagramNode<O,A>> nodesIt = diagram.getNodes();
        while (nodesIt.hasNext()) {
            DiagramNode<O,A> node = nodesIt.next();
            Concept<O,A> concept = node.getConcept();
            Collection<Concept<O,A>> upset = new HashSet<Concept<O,A>>(concept.getUpset());
            upset.remove(concept);
            Set<Concept<O,A>> indirectSuperConcepts = new HashSet<Concept<O,A>>();
            for (Iterator<Concept<O,A>> iterator = upset.iterator(); iterator.hasNext();) {
                Concept<O,A> superconcept = iterator.next();
                Collection<Concept<O,A>> upset2 = new HashSet<Concept<O,A>>(superconcept.getUpset());
                upset2.remove(superconcept);
                indirectSuperConcepts.addAll(upset2);
            }
            upset.removeAll(indirectSuperConcepts);
            for (Iterator<Concept<O,A>> iterator = upset.iterator(); iterator.hasNext();) {
                Concept<O,A> upperNeighbour = iterator.next();
                diagram.addLine(nodemap.get(upperNeighbour), node);
            }
        }
    }
}
