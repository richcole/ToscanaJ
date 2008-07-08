/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.activity.SimpleActivity;

public class SimpleAction extends KeyboardMappedAction {

    protected List<SimpleActivity> activityList = new ArrayList<SimpleActivity>();

    public SimpleAction(final JFrame frame, final SimpleActivity activity,
            final String displayName, final int mnemonic,
            final KeyStroke keystroke) {
        super(frame, displayName, mnemonic, keystroke);
        add(activity);
    }

    public void add(final SimpleActivity activity) {
        this.activityList.add(activity);
    }

    public SimpleAction(final JFrame frame, final String displayName,
            final SimpleActivity activity) {
        super(frame, displayName);
        add(activity);
    }

    public SimpleAction(final JFrame frame, final String displayName) {
        super(frame, displayName);
    }

    public void actionPerformed(final ActionEvent e) {
        try {
            for (final SimpleActivity activity : this.activityList) {
                if (!activity.doActivity()) {
                    break;
                }
            }
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this.frame, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
