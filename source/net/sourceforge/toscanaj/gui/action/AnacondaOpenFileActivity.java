/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 22, 2002
 * Time: 3:34:55 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.AnacondaModel;
import net.sourceforge.toscanaj.model.XML_Reader;

import javax.swing.*;
import java.io.File;

public class AnacondaOpenFileActivity implements FileActivity {

    SimpleActivity activity;
    AnacondaModel model;
    JFrame        frame;

    public AnacondaOpenFileActivity(AnacondaModel a_model, JFrame a_frame)
    {
        model = a_model;
        frame = a_frame;
    }

    public void processFile(File file) throws Exception {
        new XML_Reader(file, model);
    }

    public void setPrepareActivity(SimpleActivity activity) {
        this.activity = activity;
    }

    public boolean prepareToProcess() throws Exception {
        if (activity != null) {
            return activity.doActivity();
        }
        else {
            return true;
        }
    }

}
