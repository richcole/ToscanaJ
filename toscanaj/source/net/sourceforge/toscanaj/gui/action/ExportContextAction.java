/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import org.tockit.context.model.Context;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * @todo it would be better to fold the two subtypes of this into one, just
 *       offering two file types for export. We could also propose a sensible
 *       first name (context name + extension).
 */
public abstract class ExportContextAction extends KeyboardMappedAction {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(ExportContextAction.class);
    private static final String CONFIGURATION_LAST_EXPORT_FILE_ENTRY = "lastContextExport";

    public interface ContextSource {
        Context getContext();
    }

    private final ContextSource contextSource;

    public ExportContextAction(final Frame frame,
            final ContextSource contextSource) {
        super(frame, "dummy");
        putValue(Action.NAME, getName());
        this.contextSource = contextSource;
    }

    public ExportContextAction(final Frame frame,
            final ContextSource contextSource, final int mnemonic,
            final KeyStroke keystroke) {
        super(frame, "dummy", mnemonic, keystroke);
        putValue(Action.NAME, getName());
        this.contextSource = contextSource;
    }

    public void actionPerformed(final ActionEvent e) {
        try {
            exportContext();
        } catch (final FileNotFoundException e1) {
            ErrorDialog.showError(null, e1, "Can not save file");
        }
    }

    private void exportContext() throws FileNotFoundException {
        final String lastExportLocation = preferences.get(
                CONFIGURATION_LAST_EXPORT_FILE_ENTRY, null);
        JFileChooser saveDialog;
        if (lastExportLocation == null) {
            saveDialog = new JFileChooser();
        } else {
            // find parent dir, otherwise the file dialog might display the
            // default location if
            // the last export file was deleted
            final File lastExportDir = new File(lastExportLocation)
                    .getParentFile();
            saveDialog = new JFileChooser(lastExportDir);
        }

        saveDialog.setFileFilter(getFileFilter());

        final int rv = saveDialog.showSaveDialog(this.frame);
        if (rv == JFileChooser.APPROVE_OPTION) {
            File selectedFile = saveDialog.getSelectedFile();
            final ExtensionFileFilter extFileFilter = (ExtensionFileFilter) saveDialog
                    .getFileFilter();

            if (selectedFile.getName().indexOf('.') == -1) {
                final String[] extensions = extFileFilter.getExtensions();
                selectedFile = new File(selectedFile.getAbsolutePath() + "."
                        + extensions[0]);
            }

            preferences.put(CONFIGURATION_LAST_EXPORT_FILE_ENTRY, selectedFile
                    .getAbsolutePath());
            exportFile(selectedFile);
        }
    }

    protected ContextSource getContextSource() {
        return this.contextSource;
    }

    protected abstract void exportFile(File selectedFile)
            throws FileNotFoundException;

    protected abstract ExtensionFileFilter getFileFilter();

    protected abstract String getName();
}
