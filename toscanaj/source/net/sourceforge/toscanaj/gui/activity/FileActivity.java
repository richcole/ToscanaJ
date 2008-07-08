/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import java.io.File;

public interface FileActivity {
    /**
     * @todo reconsider the exception, perhaps it should be more explicit.
     */
    void processFile(File file) throws Exception;

    boolean prepareToProcess() throws Exception;

    String[] getExtensions();

    String getDescription();
}
