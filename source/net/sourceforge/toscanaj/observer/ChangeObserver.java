package net.sourceforge.toscanaj.observer;

/**
 * Generic observer for simple changes.
 *
 * We can't use Observer/Observable from java.util since we don't have multiple
 * inheritance or mixin classes. This observer can be used for simple update
 * notifications without passing additional information.
 */
public interface ChangeObserver{
    /**
     * Callback for getting notice on changes.
     */
    public void update();
}