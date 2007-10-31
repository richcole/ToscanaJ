/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.writer.ObjectAttributeListWriter;

public class ExportOALFormatAction extends ExportContextAction {
    public ExportOALFormatAction (
            Frame frame,
            ContextSource contextSource) {
        super(frame, contextSource);
    }

    public ExportOALFormatAction (
            Frame frame,
            ContextSource contextSource,
            int mnemonic,
            KeyStroke keystroke
            ) {
        super(frame, contextSource, mnemonic, keystroke);
    }
    
    @Override
	protected void exportFile(File selectedFile) throws FileNotFoundException {
        ObjectAttributeListWriter.writeObjectAttributeList(getContextSource().getContext(), new PrintStream(new FileOutputStream(selectedFile)));
    }

    @Override
	protected ExtensionFileFilter getFileFilter() {
        return new ExtensionFileFilter(new String[] {"oal"}, "Formal Contexts as object-attribute lists");
    }

    @Override
	protected String getName() {
        return "Export OAL format...";
    }
}
