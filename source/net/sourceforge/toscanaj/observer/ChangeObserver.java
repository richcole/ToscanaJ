/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.observer;

/**
 * Generic observer for simple changes.
 *
 * We can't use Observer/Observable from java.util since we don't have multiple
 * inheritance or mixin classes. This observer can be used for simple update
 * notifications without passing additional information.
 */
public interface ChangeObserver {
    /**
     * Callback for getting notice on changes.
     */
    public void update(Object source);
}