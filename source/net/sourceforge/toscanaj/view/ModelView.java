/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 24, 2002
 * Time: 7:07:08 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public abstract class ModelView extends JPanel {
    JPanel        rightPane;
    JFrame        frame;

    static private HashMap treeNodes;
    static private HashMap viewNames;

    public ModelView(
        JFrame frame,
        JPanel rightPane)
    {
        super();
        this.rightPane = rightPane;
        this.frame = frame;
    }

    /**
     * prepare to save is called before saving to give views a chance to propogate
     * changes to the models. The user should be asked, do you want to keep these
     * changes.
     *
     * The default behaviour is to do nothing and return true.
     *
     * @return return false if the user selects cancel.
     */
    public boolean prepareToSave()
    {
        return true;
    };

    public int askUser(String question, String activeViewName)
    {
        CardLayout layout = (CardLayout) this.rightPane.getLayout();
        layout.show(rightPane, "InfoView");
        return JOptionPane.showConfirmDialog(
                frame,
                question,
                "Question",
                JOptionPane.YES_NO_CANCEL_OPTION
        );
    };
}
