/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;

public interface DiagramCollection {
    int getNumberOfDiagrams();

    Diagram2D getDiagram(int number);

    Diagram2D getDiagram(String title);

    void addDiagram(Diagram2D diagram);

    void removeDiagram(int diagramIndex);
    
    void exchangeDiagrams(int index, int position);

    void replaceDiagram(Diagram2D existingDiagram, Diagram2D newDiagram);
}
