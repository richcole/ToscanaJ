/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.gui.activity.SimpleActivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class SimpleAction extends KeyboardMappedAction {

    protected List activityList = new ArrayList();

    public SimpleAction(
            JFrame frame,
            SimpleActivity activity,
            String displayName, int mnemonic,
            KeyStroke keystroke) {
        super(frame, displayName, mnemonic, keystroke);
        add(activity);
    }

    public void add(SimpleActivity activity) {
        this.activityList.add(activity);
    }

    public SimpleAction(JFrame frame, String displayName, SimpleActivity activity) {
        super(frame, displayName);
        add(activity);
    }

    public SimpleAction(JFrame frame, String displayName) {
        super(frame, displayName);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            for (Iterator it = this.activityList.iterator(); it.hasNext();) {
                SimpleActivity activity = (SimpleActivity) it.next();
                if (!activity.doActivity()) {
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this.frame,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }
}

