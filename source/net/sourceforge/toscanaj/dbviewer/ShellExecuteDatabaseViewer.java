/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

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
 * This definition will look for the URL to open the file in the column named "URL".
 * The parameter 'columnName' is mandatory and has to point to a column which contains
 * a location for each object.
 *
 *
 * Only the first object in the view will be used for a program call, the others
 * will be ignored.
 *
 * Relevant SourceForge Tracker url:
 * http://sourceforge.net/tracker/?func=detail&atid=418907&aid=630314&group_id=37081
 *  
 * @todo Handle multiple results somehow.
 */
public class ShellExecuteDatabaseViewer implements DatabaseViewer {
    final static Logger logger = Logger.getLogger(ShellExecuteDatabaseViewer.class.getName());
    
    private DatabaseViewerManager viewerManager;

    private List fieldNames = new LinkedList();

    public ShellExecuteDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager) throws DatabaseViewerException {
        this.viewerManager = manager;

        // @todo need errorchecking on columnName here
        String columnName = (String) viewerManager.getParameters().get("columnName");
        if(columnName == null) {
            throw new DatabaseViewerException("Parameter 'columnName' not given.");
        }
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
            while (itFields.hasNext()) {
                String result = (String) itFields.next();
				resourceLocation += result;
            }
        } catch (DatabaseException e) {
        	/// @todo maybe we should introduce a proper DatabaseViewerException in the signature
            throw new RuntimeException("Failed to query database.", e);
        }
        final String finalResourceLocation = resourceLocation;
        Runnable external = new Runnable() {
            public void run() {
                try {
                    BrowserLauncher.openURL(finalResourceLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.severe("Launching external program failed: " + e.getMessage());
                }
            }
        };
        new Thread(external).run();
    }
}
