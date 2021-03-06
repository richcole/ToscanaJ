/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;

import org.tockit.context.model.Context;

public interface ScaleGenerator {
    String getScaleName();

    boolean canHandleColumns(TableColumnPair[] columns);

    Context generateScale(ConceptualSchema scheme,
            DatabaseConnection databaseConnection);
}
