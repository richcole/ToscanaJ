/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
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