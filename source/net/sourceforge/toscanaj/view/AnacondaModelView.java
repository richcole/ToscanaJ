package net.sourceforge.toscanaj.view;

import net.sourceforge.toscanaj.model.AnacondaModel;
import net.sourceforge.toscanaj.view.database.InfoView;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class AnacondaModelView extends ModelView implements Observer {

    AnacondaModel model;
    InfoView infoView;
    ViewListModelView viewListModelView;

    public AnacondaModelView(
            JFrame frame,
            AnacondaModel model,
            JPanel rightPane) {
        super(frame, rightPane);
        this.model = model;

        setLayout(new BorderLayout());

        model.getDatabase().addObserver(this);
        infoView = new InfoView(frame, rightPane, model);
        viewListModelView = new ViewListModelView(
                frame,
                rightPane,
                this,
                model.getModelViewList()
        );

        add(viewListModelView);
    }

    public void update(Observable o, Object arg) {
    };

    public boolean prepareToSave() {
        if (!infoView.prepareToSave()) {
            return false;
        }
        return true;
    };

}

;


