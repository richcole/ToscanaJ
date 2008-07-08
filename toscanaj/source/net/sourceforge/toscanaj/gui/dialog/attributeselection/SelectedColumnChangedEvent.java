/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package net.sourceforge.toscanaj.gui.dialog.attributeselection;

import net.sourceforge.toscanaj.model.database.Column;

import org.tockit.events.StandardEvent;

public class SelectedColumnChangedEvent extends StandardEvent {
    public SelectedColumnChangedEvent(final Column subject) {
        super(subject);
    }
}