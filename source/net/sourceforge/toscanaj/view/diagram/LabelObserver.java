package net.sourceforge.toscanaj.view.diagram;

/**
 * Interface for listening to changes on labels.
 */
public interface LabelObserver{

    /**
     * Callback for getting notice on diagram changes.
     */
    public void labelChanged();
}