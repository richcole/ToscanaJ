/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.observer;

/**
 * Interface for adding ChangeObserver instances.
 *
 * Needed due to the typical Java problems.
 */
public interface ChangeObservable {
    /**
     * Method to add an observer.
     */
    public void addObserver(ChangeObserver observer);

    /**
     * Method to remove an observer.
     */
    public void removeObserver(ChangeObserver observer);
}
