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
 * Calls an external program as database viewer.
 *
 * A definition for this viewer looks like this:
 * <objectView class="net.sourceforge.toscanaj.dbviewer.ProgramCallDatabaseViewer"
 *         name="Start external viewer..."/>
 *     <parameter name="openDelimiter" value="%"/>
 *     <parameter name="closeDelimiter" value="%"/>
 *     <parameter name="commandLine" value="browser %descriptionUrl%"/>
 * </objectView>
 *
 * In the example the program called "browser" will be started with the content
 * of the field "descriptionUrl". The delimiters define how the fields are
 * marked, they can be completely different and they are allowed to contain more
 * than one character.
 *
 * Note: the external program is at the moment started in the same thread, the
 * UI will block as long as the external program runs.
 *
 * Only the first object in the view will be used for a program call, the others
 * will be ignored.
 *
 * @todo Handle multiple results somehow.
 */
public class ProgramCallDatabaseViewer implements DatabaseViewer {
    private DatabaseViewerManager viewerManager = null;

    private List textFragments = new LinkedList();

    private List fieldNames = new LinkedList();

    public ProgramCallDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager) {
        this.viewerManager = manager;

        String openDelimiter = (String) this.viewerManager.getParameters().get("openDelimiter");
        String closeDelimiter = (String) this.viewerManager.getParameters().get("closeDelimiter");
        String commandLine = (String) this.viewerManager.getParameters().get("commandLine");
        while (commandLine.indexOf(openDelimiter) != -1) {
        	this.textFragments.add(commandLine.substring(0, commandLine.indexOf(openDelimiter)));
            commandLine = commandLine.substring(commandLine.indexOf(openDelimiter) + openDelimiter.length());
            this.fieldNames.add(commandLine.substring(0, commandLine.indexOf(closeDelimiter)));
            commandLine = commandLine.substring(commandLine.indexOf(closeDelimiter) + closeDelimiter.length());
        }
        this.textFragments.add(commandLine);
    }

    public void showView(String whereClause) throws DatabaseViewerException {
        String command = "";
        try {
            List results = this.viewerManager.getConnection().executeQuery(this.fieldNames,
            		this.viewerManager.getTableName(),
                    whereClause);
            Vector fields = (Vector) results.get(0);
            Iterator itText = this.textFragments.iterator();
            Iterator itFields = fields.iterator();
            while (itFields.hasNext()) { // we assume length(textFragements) = length(results) + 1
                String text = (String) itText.next();
                String result = (String) itFields.next();
                command += text + result;
            }
            command += (String) itText.next();
        } catch (DatabaseException e) {
        	/// @todo maybe we should introduce a proper DatabaseViewerException in the signature
            throw new DatabaseViewerException("Failed to query database.", e);
        }
        try {
            // add command shell on Win32 platforms
            String osName = System.getProperty("os.name");
            if (osName.equals("Windows NT")) {
                command = "cmd.exe /C " + command;
            } else if (osName.equals("Windows 95")) {
                command = "command.com /C " + command;
            }

            Runtime rt = Runtime.getRuntime();
            rt.exec(command);
        } catch (Exception e) {
            throw new DatabaseViewerException("There was a problem running external viewer",e);
        }
    }
}