/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 24, 2002
 * Time: 10:58:45 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.view;

import net.sourceforge.toscanaj.model.ViewListModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observer;
import java.util.Observable;
import java.awt.*;

public class ViewListModelView extends ModelView implements Observer {

    private ViewListModel viewListModel;
    private HashMap treeNodes = new HashMap();
    private HashMap viewNames = new HashMap();
    private DefaultMutableTreeNode treeRoot;
    private JTree treePane;

    void put(String viewName, DefaultMutableTreeNode node) {
        treeNodes.put(viewName, node);
        viewNames.put(node, viewNames);
    };

    DefaultMutableTreeNode get(String viewName) {
        return (DefaultMutableTreeNode) treeNodes.get(viewName);
    }

    String get(DefaultMutableTreeNode node) {
        return (String) viewNames.get(node);
    };

    void registerModelView(
            String displayName,
            String viewName,
            String parentViewName)
        {

        if (parentViewName == null) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(displayName);
            treeRoot.setAllowsChildren(true);
            treeRoot.add(childNode);
            treeNodes.put(viewName, displayName);
            treePane.expandPath(new TreePath(childNode.getPath()));
        } else {

            if (treeNodes.containsKey(parentViewName)) {
                DefaultMutableTreeNode parentNode = get(parentViewName);
                DefaultMutableTreeNode childNode;

                if (treeNodes.containsKey(viewName)) {
                    childNode = (DefaultMutableTreeNode) treeNodes.get(viewName);
                } else {
                    childNode = new DefaultMutableTreeNode(displayName);
                    put(viewName, childNode);
                }
                parentNode.add(childNode);
                parentNode.setAllowsChildren(true);
                treePane.expandPath(new TreePath(childNode.getPath()));
            }
            else {
                /// the parent node was not registered @todo
            }
        }
    }

    class TreeSelectionHandler implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)
                        e.getNewLeadSelectionPath().getLastPathComponent();
            CardLayout card = (CardLayout)rightPane.getLayout();
            card.show(rightPane, get(node));
        }
    };

    public ViewListModelView(
            JFrame frame,
            JPanel rightPane,
            JPanel leftPane,
            ViewListModel viewListModel)
    {
        super(frame, rightPane);
        setLayout(new BorderLayout());

        this.viewListModel = viewListModel;
        viewListModel.addObserver(this);

        treeRoot = new DefaultMutableTreeNode("All Views", true);
        treePane = new JTree(treeRoot);

        JScrollPane scrollPane = new JScrollPane();

        scrollPane.getViewport().add(treePane);
        add(scrollPane);

        treePane.addTreeSelectionListener(new TreeSelectionHandler());

        // create entries for all the views already in the model
        Iterator it = viewListModel.getParentMap().keySet().iterator();
        System.out.println("initialize view list model view");
        while (it.hasNext()) {
            String child = (String) it.next();
            System.out.println("register model view child=" + child);
            registerModelView(
                    viewListModel.description(child),
                    child,
                    viewListModel.parentName(child)
            );
        }
    }

    public void update(Observable o, Object arg) {

        System.out.println("Update view list model view");
        String childName = (String) arg;
        if (o == viewListModel) {
            registerModelView(
                    viewListModel.description(childName),
                    childName,
                    viewListModel.parentName(childName)
            );
        }
    }
}
