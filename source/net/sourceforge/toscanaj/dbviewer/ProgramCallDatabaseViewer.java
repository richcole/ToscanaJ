/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import java.io.*;
import java.util.*;

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
 * @todo Start external program in new thread, we don't need the output anyway.
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

        String openDelimiter = (String) viewerManager.getParameters().get("openDelimiter");
        String closeDelimiter = (String) viewerManager.getParameters().get("closeDelimiter");
        String commandLine = (String) viewerManager.getParameters().get("commandLine");
        while (commandLine.indexOf(openDelimiter) != -1) {
            textFragments.add(commandLine.substring(0, commandLine.indexOf(openDelimiter)));
            commandLine = commandLine.substring(commandLine.indexOf(openDelimiter) + openDelimiter.length());
            fieldNames.add(commandLine.substring(0, commandLine.indexOf(closeDelimiter)));
            commandLine = commandLine.substring(commandLine.indexOf(closeDelimiter) + closeDelimiter.length());
        }
        textFragments.add(commandLine);
    }

    public void showView(String whereClause) {
        String command = "";
        try {
            List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                    viewerManager.getTableName(),
                    whereClause);
            Vector fields = (Vector) results.get(0);
            Iterator itText = textFragments.iterator();
            Iterator itFields = fields.iterator();
            while (itFields.hasNext()) { // we assume length(textFragements) = length(results) + 1
                String text = (String) itText.next();
                String result = (String) itFields.next();
                command += text + result;
            }
            command += (String) itText.next();
        } catch (DatabaseException e) {
            System.err.println("Failed to query database:\n" + e.getMessage() + "\n" + e.getOriginal().getMessage());
        }
        String err = "";
        String out = "";
        int exitVal;
        try {
            // add command shell on Win32 platforms
            String osName = System.getProperty("os.name");
            if (osName.equals("Windows NT")) {
                command = "cmd.exe /C " + command;
            } else if (osName.equals("Windows 95")) {
                command = "command.com /C " + command;
            }

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                err += line;
            }
            InputStream stdout = proc.getInputStream();
            isr = new InputStreamReader(stdout);
            br = new BufferedReader(isr);
            line = null;
            while ((line = br.readLine()) != null) {
                out += line;
            }
            exitVal = proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
