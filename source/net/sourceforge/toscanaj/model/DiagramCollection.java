/*
 * Created by IntelliJ IDEA.
 * User: Serhiy Yevtushenko
 * Date: Jun 29, 2002
 * Time: 5:23:50 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;

public interface DiagramCollection {
    int getNumberOfDiagrams();

    Diagram2D getDiagram(int number);

    Diagram2D getDiagram(String title);

    void addDiagram(Diagram2D diagram);

    void removeDiagram(int diagramIndex);
}
