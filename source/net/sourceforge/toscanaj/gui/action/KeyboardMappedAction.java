package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public abstract class KeyboardMappedAction extends AbstractAction {

    protected JFrame frame;
    /**
     *  @note
     *     If you don't want to specify mnemonics
     *     then use the other constructor.
     * @todo if you want another conmbination then write another constructor.
     */
    public KeyboardMappedAction(
            JFrame frame,
            String displayName, int mnemonic,
            KeyStroke keystroke)
    {
        this(frame, displayName);
        this.putValue(Action.MNEMONIC_KEY, new Integer(mnemonic));
        this.putValue(Action.ACCELERATOR_KEY, keystroke);
    }

    public KeyboardMappedAction(JFrame frame, String displayName)
    {
        super(displayName);
        this.frame = frame;
    }
}

