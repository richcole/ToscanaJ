package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class SimpleAction extends KeyboardMappedAction {

    protected ArrayList activityList = new ArrayList();

    public SimpleAction(
            JFrame frame,
            SimpleActivity activity,
            String displayName, int mnemonic,
            KeyStroke keystroke) {
        super(frame, displayName, mnemonic, keystroke);
        add(activity);
    }

    public void add(SimpleActivity activity) {
        activityList.add(activity);
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
            for (Iterator it = activityList.iterator(); it.hasNext();) {
                SimpleActivity activity = (SimpleActivity) it.next();
                if ( ! activity.doActivity() ) {
                    break;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

