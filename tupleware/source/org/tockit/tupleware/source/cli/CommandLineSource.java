/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.tupleware.gui.IndexSelectionDialog;
import org.tockit.tupleware.model.TupleSet;
import org.tockit.tupleware.source.TupleSource;
import org.tockit.tupleware.source.text.TabDelimitedParser;


public class CommandLineSource implements TupleSource {
	private int[] objectIndices;
	private TupleSet tuples;
		
	public void show(JFrame parent, File lastLocation) {
        String command = JOptionPane.showInputDialog(parent, "Please enter command to call:", 
                                                     "Query tuples", JOptionPane.QUESTION_MESSAGE);
        if(command == null) {
            return;
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
            Process proc = rt.exec(command);
            String line;
            StringBuffer out = new StringBuffer();
            BufferedReader input = new BufferedReader(
                                            new InputStreamReader(proc.getInputStream()));
            while ((line = input.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            input.close();
            int exitVal = proc.waitFor();
            if(exitVal != 0) {
                input = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                StringBuffer err = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    err.append(line);
                    err.append("\n");
                }
                input.close();
                JOptionPane.showMessageDialog(parent, err.toString(), "Execution failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.tuples = TabDelimitedParser.parseTabDelimitedTuples(new StringReader(out.toString()));

            IndexSelectionDialog dialog = new IndexSelectionDialog(parent, "Select object set", this.tuples.getVariableNames());
            dialog.show();
            this.objectIndices = dialog.getSelectedIndices();
        } catch (Exception e) {
            ErrorDialog.showError(parent, e, "Program failed");
        }
    }

    public String getMenuName() {
        return "Call external program...";
    }

    public File getSelectedFile() {
        return null;
    }

    public TupleSet getTuples() {
        return this.tuples;
    }

    public int[] getObjectIndices() {
        return this.objectIndices;
    }
}
