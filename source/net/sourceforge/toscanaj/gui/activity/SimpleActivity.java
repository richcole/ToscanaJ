/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 22, 2002
 * Time: 3:25:39 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.gui.activity;

import java.io.File;

public interface SimpleActivity {

    /* return success if the activity succeeded, throw an exception
     * if the user should hear that something went wrong.
    */
    public boolean doActivity() throws Exception;
}
