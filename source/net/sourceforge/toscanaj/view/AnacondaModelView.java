package net.sourceforge.toscanaj.view;

import net.sourceforge.toscanaj.model.AnacondaModel;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Database;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.view.dialogs.InfoView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class AnacondaModelView extends JPanel implements Observer {

    AnacondaModel model;
    JPanel        rightPane;
    InfoView      infoView;

    public AnacondaModelView(JFrame frame, AnacondaModel model, JPanel rightPane)
    {
        super();
        this.rightPane = rightPane;
        this.model     = model;

        model.getDatabase().addObserver(this);
        infoView = new InfoView(frame, rightPane, model);
    }

    public void update(Observable o, Object arg)
    {
    };
};


