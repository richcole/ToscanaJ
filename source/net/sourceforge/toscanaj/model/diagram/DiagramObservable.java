package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.view.diagram.DiagramObserver;

/**
 * Abstract class for model
 */

public interface DiagramObservable{
  /**
   * Method to add observer
   */
  public void addObserver(DiagramObserver diagramObserver);

  public void emitChangeSignal();
}