package net.sourceforge.toscanaj.observer;

/**
 * Interface for adding ChangeObserver instances.
 *
 * Needed due to the typical Java problems.
 */
public interface ChangeObservable{
    /**
     * Method to add an observer.
     */
    public void addObserver(ChangeObserver observer);

    /**
     * Method to remove an observer.
     */
    public void removeObserver(ChangeObserver observer);
}