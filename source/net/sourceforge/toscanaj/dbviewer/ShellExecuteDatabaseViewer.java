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

    //private List textFragments = new LinkedList();
    private String columnName;

    private List fieldNames = new LinkedList();

    public ShellExecuteDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager) {
        this.viewerManager = manager;

        //String openDelimiter = (String) viewerManager.getParameters().get("openDelimiter");
        //String closeDelimiter = (String) viewerManager.getParameters().get("closeDelimiter");
        
        // @todo need errorchecking on columnName here
        columnName = (String) viewerManager.getParameters().get("columnName");
        System.out.println("column name = " + columnName);
        fieldNames.add(columnName);
//        String commandLine = (String) viewerManager.getParameters().get("commandLine");
//        while (commandLine.indexOf(openDelimiter) != -1) {
//            textFragments.add(commandLine.substring(0, commandLine.indexOf(openDelimiter)));
//            commandLine = commandLine.substring(commandLine.indexOf(openDelimiter) + openDelimiter.length());
//            fieldNames.add(commandLine.substring(0, commandLine.indexOf(closeDelimiter)));
//            commandLine = commandLine.substring(commandLine.indexOf(closeDelimiter) + closeDelimiter.length());
//        }
//        textFragments.add(commandLine);
    }

    public void showView(String whereClause) {
        String command = "";
        try {
            List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                    viewerManager.getTableName(),
                    whereClause);
            Vector fields = (Vector) results.get(0);
//            Iterator itText = textFragments.iterator();
            Iterator itFields = fields.iterator();
            while (itFields.hasNext()) { // we assume length(textFragements) = length(results) + 1
//                String text = (String) itText.next();
                String result = (String) itFields.next();
//                command += text + result;
				command += result;
            }
//            command += (String) itText.next();
            System.out.println("command = " + command);
        } catch (DatabaseException e) {
        	/// @todo maybe we should introduce a proper DatabaseViewerException in the signature
            throw new RuntimeException("Failed to query database.", e);
        }
        try {
            // add command shell on Win32 platforms
            String osName = System.getProperty("os.name");
            if (osName.startsWith("Windows ")) {
            	// This command will open specified file with an appropriate
            	// application registered for it's file type. 
            	// The only issue I can see at the moment with this approach is 
            	// that if we are opening an html file - last active browser
            	// window will get reused rather then opening new browser 
            	// window (not sure if the behaviour is the same for other 
            	// file types, certainly not for .txt).
				String windowsCommand = "rundll32 url.dll,FileProtocolHandler ";
				
				// followind addition to the windowsCommand
				// opens new browser window for this, may be not what we want if 
				// we want to display word doc - in this case we will get browser 
				// window with a dialog box saying that you are downloading a word
				// document and options to open or save it. Also have to add "%22" 
				// at the end of command, otherwise we get an error.
				//windowsCommand = windowsCommand + "javascript:location.href=%22";
				
				// works for txt, pdf, xml and doc:
            	//command = windowsCommand + "file://d:/temp/test.txt";
				//command = windowsCommand + "file://d:/temp/www2003.pdf";
				//command = windowsCommand + "file://d:/temp/test.xml";
				//command = windowsCommand + "file://d:/temp/test.doc";
				//command = windowsCommand + "file://d:/temp/test.docdfsd";
				
				// doesn't work for files that are not local
				//command = windowsCommand + "http://www.dstc.edu.au/index.html";
				// works:
				//command = windowsCommand + "http://www.dstc.edu.au/";
				
				//command = command + "%22";
				
				// This approach is better since it behaves in the same manner
				// as the above approach, but also has a good working feature:
				// if there a file type is unknown - user gets a dialog
				// allowing them to choose an appropriate application.
				
				// works:
				//command = "cmd.exe /c start " + "http://www.dstc.edu.au/index.html";
				//command = "cmd.exe /c start " + "http://www.dstc.edu.au";
				// works for txt, pdf, xml and doc:
				//command = "cmd.exe /c start " + "file://d:/temp/test.txt";
				//command = "cmd.exe /c start " + "file://d:/temp/www2003.pdf";
				//command = "cmd.exe /c start " + "file://d:/temp/test.xml";
				//command = "cmd.exe /c start " + "file://d:/temp/test.doc";
				//command = "cmd.exe /c start " + "file://d:/temp/test.docdfsd";
				command = "cmd.exe /c start " + command;
            }
//            if (osName.equals("Windows NT")) {
//                command = "cmd.exe /C " + command;
//            } else if (osName.equals("Windows 95")) {
//                command = "command.com /C " + command;
//            }

            Runtime rt = Runtime.getRuntime();
            rt.exec(command);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem running external viewer",e);
        }
    }
}
