/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Calls an external program as database viewer. Ideally program called is the program
 * associated with file extention of a file we want to display. For example, if 
 * we retrieved location of html file from the database - we want default browser to 
 * display this file, or acrobat reader to be launched for pdf file.
 * 
 * At the moment this approach works best on Windows platform as it has built-in file
 * associations with the programs handling certain files. On other platforms, for now,
 * we will try to open given file with a browser since most browsers are able to 
 * forward files they can't handle to appropriate applications (if we come across
 * file type that browser doesn't know how to handle - we just give up).
 *
 * A definition for this viewer looks like this:
 * <objectView class="net.sourceforge.toscanaj.dbviewer.ShellExecuteDatabaseViewer"
 *         name="Start external viewer...">
 *     <parameter name="columnName" value="URL"/>
 * </objectView>
 *
 *
 * Note: the external program is at the moment started in the same thread, the
 * UI will block as long as the external program runs.
 *
 * Only the first object in the view will be used for a program call, the others
 * will be ignored.
 *
 * Relevant SourceForge Tracker url:
 * https://sourceforge.net/tracker/?func=detail&atid=418907&aid=630314&group_id=37081
 *  
 * @todo Handle multiple results somehow.
 */
public class ShellExecuteDatabaseViewer implements DatabaseViewer {
    private DatabaseViewerManager viewerManager = null;

    private String columnName;

    private List fieldNames = new LinkedList();

    public ShellExecuteDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager) {
        this.viewerManager = manager;

        // @todo need errorchecking on columnName here
        columnName = (String) viewerManager.getParameters().get("columnName");
        fieldNames.add(columnName);
    }

    public void showView(String whereClause) {
        String resourceLocation = "";
        try {
            List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                    viewerManager.getTableName(),
                    whereClause);
            Vector fields = (Vector) results.get(0);
            Iterator itFields = fields.iterator();
            while (itFields.hasNext()) { // we assume length(textFragements) = length(results) + 1
                String result = (String) itFields.next();
				resourceLocation += result;
            }
        } catch (DatabaseException e) {
        	/// @todo maybe we should introduce a proper DatabaseViewerException in the signature
            throw new RuntimeException("Failed to query database.", e);
        }
        try {
			BrowserLauncher.openURL(resourceLocation);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem running external viewer",e);
        }
    }
}
