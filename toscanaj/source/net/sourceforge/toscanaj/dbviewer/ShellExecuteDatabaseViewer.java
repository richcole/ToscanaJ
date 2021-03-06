/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

/**
 * Calls an external program as database viewer. Ideally program called is the
 * program associated with file extension of a file we want to display. For
 * example, if we retrieved location of html file from the database - we want
 * default browser to display this file, or acrobat reader to be launched for
 * pdf file.
 * 
 * At the moment this approach works best on Windows platform as it has built-in
 * file associations with the programs handling certain files. On other
 * platforms, for now, we will try to open given file with a browser since most
 * browsers are able to forward files they can't handle to appropriate
 * applications (if we come across file type that browser doesn't know how to
 * handle - we just give up).
 * 
 * A definition for this viewer looks like this: <objectView
 * class="net.sourceforge.toscanaj.dbviewer.ShellExecuteDatabaseViewer"
 * name="Start external viewer..."> <parameter name="columnName" value="URL"/>
 * </objectView>
 * 
 * This definition will look for the URL to open the file in the column named
 * "URL". The parameter 'columnName' is mandatory and has to point to a column
 * which contains a location for each object.
 * 
 * 
 * Only the first object in the view will be used for a program call, the others
 * will be ignored.
 * 
 * Relevant SourceForge Tracker url:
 * http://sourceforge.net/tracker/?func=detail&
 * atid=418907&aid=630314&group_id=37081
 * 
 * @todo Handle multiple results somehow.
 */
public class ShellExecuteDatabaseViewer implements DatabaseViewer {
    final static Logger logger = Logger
    .getLogger(ShellExecuteDatabaseViewer.class.getName());

    private DatabaseViewerManager viewerManager;

    private final List<String> fieldNames = new ArrayList<String>();

    public ShellExecuteDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic
        // class loading mechanism
    }

    public void initialize(final DatabaseViewerManager manager)
    throws DatabaseViewerException {
        this.viewerManager = manager;

        // @todo need errorchecking on columnName here
        final String columnName = this.viewerManager.getParameters().get(
        "columnName");
        if (columnName == null) {
            throw new DatabaseViewerException(
            "Parameter 'columnName' not given.");
        }
        this.fieldNames.add(columnName);
    }

    public void showView(final String whereClause)
    throws DatabaseViewerException {
        String resourceLocation = "";
        try {
            final List<String[]> results = this.viewerManager.getConnection()
            .executeQuery(this.fieldNames,
                    this.viewerManager.getTableName(), whereClause);
            final String[] fields = results.get(0);
            for (final String result : fields) {
                resourceLocation += result;
            }
        } catch (final DatabaseException e) {
            // / @todo maybe we should introduce a proper
            // DatabaseViewerException in the signature
            throw new DatabaseViewerException("Failed to query database.", e);
        }

        final URL baseURL = DatabaseViewerManager.getBaseURL();
        URL resourceURL;
        try {
            // first try proper URL resolving, using baseURL for relative
            // locations
            resourceURL = new URL(baseURL, resourceLocation);
        } catch (final MalformedURLException e) {
            try {
                // if that fails, assume it is a file system location
                resourceURL = new File(resourceLocation).toURI().toURL();
            } catch (final MalformedURLException e1) {
                throw new DatabaseViewerException(
                        "Can not resolve URL for item", e);
            }
        }

        final URL finalURL = resourceURL;
        final Runnable external = new Runnable() {
            public void run() {
                try {
                    BrowserLauncher.openURL(finalURL.toExternalForm());
                } catch (final IOException e) {
                    e.printStackTrace();
                    logger.severe("Launching external program failed: "
                            + e.getMessage());
                }
            }
        };
        new Thread(external).run();
    }
}
