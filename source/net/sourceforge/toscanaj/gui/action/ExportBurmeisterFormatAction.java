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
import java.io.PrintStream;

import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.writer.BurmeisterWriter;

public class ExportBurmeisterFormatAction extends ExportContextAction {
	public ExportBurmeisterFormatAction (
			Frame frame,
			ContextSource contextSource) {
		super(frame, contextSource);
	}

    public ExportBurmeisterFormatAction (
			Frame frame,
            ContextSource contextSource,
			int mnemonic,
			KeyStroke keystroke
			) {
		super(frame, contextSource, mnemonic, keystroke);
	}
	
    protected void exportFile(File selectedFile) throws FileNotFoundException {
        BurmeisterWriter.writeToBurmeisterFormat(getContextSource().getContext(), new PrintStream(selectedFile));
    }

    protected ExtensionFileFilter getFileFilter() {
        return new ExtensionFileFilter(new String[] {"cxt"}, "Formal Contexts in Burmeister format");
    }

    protected String getName() {
        return "Export Burmeister format...";
    }
}
