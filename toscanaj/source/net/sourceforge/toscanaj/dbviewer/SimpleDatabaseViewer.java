/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import net.sourceforge.toscanaj.controller.db.DatabaseException;

/**
 * Shows an object in a simple dialog.
 * 
 * A definition for this viewer looks like this: <objectView
 * class="net.sourceforge.toscanaj.dbviewer.SimpleDatabaseViewer"
 * name="Show object..."/> <parameter name="openDelimiter" value="!!"/>
 * <parameter name="closeDelimiter" value="$$"/> <template>Name: !!name$$ Type:
 * !!type$$ Size: !!size$$</template> </objectView>
 * 
 * Here the three fields "name", "type" and "size" will be queried and the
 * template will be filled with the results and then displayed in a dialog. Note
 * that the whitespace will be copied, too -- if you format your XML in a nice
 * way you might get weird indentation in the dialog.
 * 
 * If multiple items have to be displayed, paging buttons will be used.
 */
public class SimpleDatabaseViewer extends PagingDatabaseViewer {
    private class SimpleViewPanel implements PageViewPanel {
        private JTextArea textArea;

        private final List<String> textFragments = new ArrayList<String>();

        private final List<String> fieldNames = new ArrayList<String>();

        public void showItem(final String keyValue) {
            try {
                final DatabaseViewerManager viewerManager = getManager();
                final List<String[]> results = viewerManager.getConnection()
                .executeQuery(
                        this.fieldNames,
                        viewerManager.getTableName(),
                        "WHERE " + viewerManager.getKeyName() + "='"
                        + keyValue + "'");
                assert textFragments.size() == results.size() + 1 : "Database results have to match the the available text fragments";
                final String[] fields = results.get(0);
                final Iterator<String> itText = this.textFragments.iterator();
                String output = "";
                for (final String result : fields) {
                    final String text = itText.next();
                    output += text + result;
                }
                output += itText.next();
                this.textArea.setText(output);
            } catch (final DatabaseException e) {
                this.textArea.setText("Failed to query database:\n"
                        + e.getMessage() + "\n" + e.getCause().getMessage());
            }
        }

        public Component getComponent() throws DatabaseViewerException {
            final DatabaseViewerManager viewerManager = getManager();
            final String openDelimiter = viewerManager.getParameters().get(
            "openDelimiter");
            if (openDelimiter == null) {
                throw new DatabaseViewerException("Open delimiter not defined");
            }
            final String closeDelimiter = viewerManager.getParameters().get(
            "closeDelimiter");
            if (closeDelimiter == null) {
                throw new DatabaseViewerException("Close delimiter not defined");
            }
            String template = viewerManager.getTemplateString();
            if (template == null) {
                throw new DatabaseViewerException("No template found");
            }
            while (template.indexOf(openDelimiter) != -1) {
                this.textFragments.add(template.substring(0, template
                        .indexOf(openDelimiter)));
                template = template.substring(template.indexOf(openDelimiter)
                        + openDelimiter.length());
                this.fieldNames.add(template.substring(0, template
                        .indexOf(closeDelimiter)));
                template = template.substring(template.indexOf(closeDelimiter)
                        + closeDelimiter.length());
            }
            this.textFragments.add(template);

            this.textArea = new JTextArea();
            this.textArea.setEditable(false);
            this.textArea.setBorder(BorderFactory.createBevelBorder(1));
            return this.textArea;
        }
    }

    @Override
    protected PageViewPanel createPanel() {
        return new SimpleViewPanel();
    }
}
