/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.Frame;
import javax.swing.*;

public abstract class KeyboardMappedAction extends AbstractAction {

    protected Frame frame;

    /**
     * @todo check why keystroke and mnemonic don't work anymore, remove extra
     * setting code in Elba and maybe elsewhere
     */
    public KeyboardMappedAction(
            Frame frame,
            String displayName, int mnemonic,
            KeyStroke keystroke) {
        this(frame, displayName);
        this.putValue(Action.MNEMONIC_KEY, new Integer(mnemonic));
        this.putValue(Action.ACCELERATOR_KEY, keystroke);
    }

    public KeyboardMappedAction(Frame frame, String displayName) {
        super(displayName);
        this.frame = frame;
    }
}

