package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.gui.AnacondaJMainPanel;
import net.sourceforge.toscanaj.gui.MainPanel;
import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;

public class CloseMainPanelActivity implements SimpleActivity
{
    MainPanel frame;

    public CloseMainPanelActivity(MainPanel a_frame)
    {
        frame = a_frame;
    }

    public boolean doActivity()
    {
        frame.closeMainPanel();
        return true;
    }
}

