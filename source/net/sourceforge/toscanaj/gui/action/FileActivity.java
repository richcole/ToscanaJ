/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 22, 2002
 * Time: 3:25:39 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.gui.action;

import java.io.File;

public interface FileActivity {
/**
 *  @todo reconsider the exception, perhaps it should be more explicit.
 */
    public void processFile(File file) throws Exception;
    public boolean prepareToProcess() throws Exception;
}
