/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Shows an object in a simple dialog.
 *
 * A definition for this viewer looks like this:
 * <objectView class="net.sourceforge.toscanaj.dbviewer.SimpleDatabaseViewer"
 *         name="Show object..."/>
 *     <parameter name="openDelimiter" value="!!"/>
 *     <parameter name="closeDelimiter" value="§§"/>
 *     <template>Name: !!name§§
 * Type: !!type§§
 * Size: !!size§§</template>
 * </objectView>
 *
 * Here the three fields "name", "type" and "size" will be queried and the
 * template will be filled with the results and then displayed in a dialog.
 * Note that the whitespace will be copied, too -- if you format your XML in
 * a nice way you might get weird indentation in the dialog.
 * 
 * If multiple items have to be displayed, paging buttons will be used.
 */
public class SimpleDatabaseViewer extends PagingDatabaseViewer {
    private JTextArea textArea;

    private List textFragments = new LinkedList();

    private List fieldNames = new LinkedList();

    protected void showItem(String keyValue) {
        try {
            DatabaseViewerManager viewerManager = getManager();
            List results = viewerManager.getConnection().executeQuery(fieldNames,
                    viewerManager.getTableName(),
                    "WHERE " + viewerManager.getKeyName() + "='" + keyValue + "'");
            Vector fields = (Vector) results.get(0);
            Iterator itText = textFragments.iterator();
            Iterator itFields = fields.iterator();
            String output = "";
            while (itFields.hasNext()) { // we assume length(textFragements) = length(results) + 1
                String text = (String) itText.next();
                String result = (String) itFields.next();
                output += text + result;
            }
            output += (String) itText.next();
            this.textArea.setText(output);
        } catch (DatabaseException e) {
            this.textArea.setText("Failed to query database:\n" + e.getMessage() + "\n" + e.getCause().getMessage());
        }
    }

    protected Component getPanel() throws DatabaseViewerInitializationException {
        DatabaseViewerManager viewerManager = getManager();
        String openDelimiter = (String) viewerManager.getParameters().get("openDelimiter");
        if (openDelimiter == null) {
            throw new DatabaseViewerInitializationException("Open delimiter not defined");
        }
        String closeDelimiter = (String) viewerManager.getParameters().get("closeDelimiter");
        if (closeDelimiter == null) {
            throw new DatabaseViewerInitializationException("Close delimiter not defined");
        }
        String template = viewerManager.getTemplateString();
        if (template == null) {
            throw new DatabaseViewerInitializationException("No template found");
        }
        while (template.indexOf(openDelimiter) != -1) {
            textFragments.add(template.substring(0, template.indexOf(openDelimiter)));
            template = template.substring(template.indexOf(openDelimiter) + openDelimiter.length());
            fieldNames.add(template.substring(0, template.indexOf(closeDelimiter)));
            template = template.substring(template.indexOf(closeDelimiter) + closeDelimiter.length());
        }
        textFragments.add(template);

        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setBorder(BorderFactory.createBevelBorder(1));
        return this.textArea;
    }
}
