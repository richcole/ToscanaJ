package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.AnacondaModel;
import net.sourceforge.toscanaj.AnacondaJ;
import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;

public class AnacondaQuitActivity implements SimpleActivity
{
    AnacondaJ frame;

    public AnacondaQuitActivity(AnacondaJ a_frame)
    {
        frame = a_frame;
    }

    public boolean doActivity()
    {
        frame.closeMainPanel();
        return true;
    }
}

