package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.AnacondaModel;
import net.sourceforge.toscanaj.gui.AnacondaJMainPanel;
import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;

import java.awt.event.ActionEvent;
import java.awt.*;

import javax.swing.*;

public class AnacondaNewModelActivity implements SimpleActivity
{
    AnacondaModel     model;
    JPanel            rightPane;

    public AnacondaNewModelActivity(AnacondaModel model, JPanel rightPane)
    {
        this.model = model;
        this.rightPane = rightPane;
    }

    public boolean doActivity()
    {
        DatabaseInfo info = new DatabaseInfo("","","","");
        model.getDatabase().setInfo(info);
        CardLayout layout = (CardLayout) this.rightPane.getLayout();
        layout.show(rightPane, "DatabaseConnectionInformationView");
        return true;
    }
}

