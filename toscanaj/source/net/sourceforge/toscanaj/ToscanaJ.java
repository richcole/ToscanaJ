/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javax.swing.JOptionPane;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.tockit.plugin.PluginLoader;

import net.sourceforge.toscanaj.gui.ToscanaJMainPanel;
import net.sourceforge.toscanaj.gui.dialog.ToscanaJPreferences;

public class ToscanaJ {
    /**
     * The version name used in the about dialog.
     */
    static public final String VersionString = "CVS Build";

    /**
     *  Main method for running the program
     */
    public static void main(String[] args) {
        loadPlugins();
        final ToscanaJMainPanel mainWindow;
        Options options = new Options();
        options.addOption("reset", false, "Resets all preferences for the current user and exit");
        options.addOption("help", false, "Show this command line summary and exit");
        CommandLineParser parser = new BasicParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(options,args);
        } catch (ParseException e) {
            showUsage(options, System.err);
            System.exit(1);
        }
        assert cl != null; // to stop Eclipse from whinging -- it thinks it is possible to get a null here
        if(cl.getArgs().length > 1) {
            showUsage(options, System.err);
            System.exit(1);
        }
        if(cl.hasOption("help")) {
            showUsage(options, System.out);
            System.exit(0);
        }
        if(cl.hasOption("reset")) {
            try {
                ToscanaJPreferences.removeSettings();
                System.out.println("User preferences reset.");
            } catch (BackingStoreException exception) {
                System.err.println("Problem encountered removing preferences:");
                exception.printStackTrace();
            }
            System.exit(0);
        }
        if (cl.getArgs().length == 1) {
            mainWindow = new ToscanaJMainPanel(cl.getArgs()[0]);
        } else {
            mainWindow = new ToscanaJMainPanel();
        }

        mainWindow.setVisible(true);
    }

    private static void showUsage(Options options, PrintStream stream) {
        stream.println("Usage:");
        stream.println("  ToscanaJ [Options] [File]");
        stream.println();
        stream.println("where [File] is one optional file to open and [Options] can be:");
        for (Iterator iter = options.getOptions().iterator(); iter.hasNext(); ) {
            Option option = (Option) iter.next();
            stream.println("  " + option.getOpt() + ": " + option.getDescription());
        }
    }

    /**
     * Loads all plugins found in the default plugin location.
     */
    public static void loadPlugins() {
        try {
            PluginLoader.loadPlugins(new File("plugins"));
        } catch (FileNotFoundException e) {
            Logger.getLogger(ToscanaJ.class.getName()).info("Could not find plugin directory -- no plugins loaded");
        }
    }
}