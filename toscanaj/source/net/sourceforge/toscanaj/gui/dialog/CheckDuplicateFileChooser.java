package net.sourceforge.toscanaj.gui.dialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
/**
 * The custom file chooser will check whether the file exists and shows the
 * appropriate warning message (if applicable)
 * 
 */
public class CheckDuplicateFileChooser extends JFileChooser {
    public CheckDuplicateFileChooser(final File selectedFile,
            final ExtensionFileFilter[] filterArray) {
        super(selectedFile);
        ExtensionFileFilter filter;
        for (final ExtensionFileFilter element : filterArray) {
            filter = element;
            if (filter != null) {
                addChoosableFileFilter(filter);
            }
        }
    }

    @Override
    public void approveSelection() {
        File selectedFile = getSelectedFile();
        if (selectedFile.getName().indexOf('.') == -1) { // check for extension
            // add default
            final FileFilter filter = getFileFilter();
            if (filter instanceof ExtensionFileFilter) {
                final ExtensionFileFilter extFileFilter = (ExtensionFileFilter) filter;
                final String[] extensions = extFileFilter.getExtensions();
                selectedFile = new File(selectedFile.getAbsolutePath() + "."
                        + extensions[0]);
                setSelectedFile(selectedFile);
            }
        }
        if (selectedFile.exists()) {
            final String warningMessage = "The file '"
                    + selectedFile.getName()
                    + "' already exists.\nDo you want to overwrite the existing file?";
            final int response = JOptionPane.showOptionDialog(this,
                    warningMessage, "File Export Warning: File exists",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, new Object[] { "Yes", "No" }, "No");
            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }
        super.approveSelection();
    }
}
