/*
* Created by IntelliJ IDEA.
* User: p198
* Date: Jun 23, 2002
* Time: 2:50:59 PM
* To change template for new class use
* Code Style | Class Templates options (Tools | IDE Options).
*/
package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.AnacondaModel;

public class ConnectDatabaseActivity implements SimpleActivity {

    protected AnacondaModel model;

    public ConnectDatabaseActivity(AnacondaModel model) {
        this.model = model;
    }

    public boolean doActivity() throws Exception {
        model.getDatabase().connect();
        return true;
    }
}
