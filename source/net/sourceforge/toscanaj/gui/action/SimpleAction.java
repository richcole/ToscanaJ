package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

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
            int mnemonic,
            KeyStroke keystroke) {
        super(frame, mnemonic, keystroke);
        add(activity);
    }

    public void add(SimpleActivity activity) {
        activityList.add(activity);
    }

    public SimpleAction(JFrame frame, SimpleActivity activity) {
        super(frame);
        add(activity);
    }

    public SimpleAction(JFrame frame) {
        super(frame);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            for (Iterator it = activityList.iterator(); it.hasNext();) {
                SimpleActivity activity = (SimpleActivity) it.next();
                activity.doActivity();
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

