/*
 * Copyright Peter Becker (http://www.peterbecker.de).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import javax.swing.*;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Shows additional information coming from the database or external files.
 * 
 * This database viewer queries the database for additional information in form
 * of either plain text or HTML. This extra information can be either stored 
 * directly in the database or it can be accessed using URLs found in the database. 
 *
 * A definition for this viewer looks like this:
 * 
 * <objectListView class="net.sourceforge.toscanaj.dbviewer.InfoDatabaseViewer"
 *         name="Show objects..."/>
 *     <parameter name="contentColumn" value="extraInfo"/>
 *     <parameter name="contentType" value="html"/>
 * </objectListView>
 *
 * This example will query the information of the column "extraInfo" for the
 * given object and render it as HTML. If multiple objects are given paging 
 * controls will be used.
 * 
 * Another definition can look like this:
 * 
 * <objectListView class="net.sourceforge.toscanaj.dbviewer.InfoDatabaseViewer"
 *         name="Show objects..."/>
 *     <parameter name="urlColumn" value="infoURL"/>
 *     <parameter name="contentType" value="text"/>
 * </objectListView>
 *
 * Here the information is an external text file, which will be loaded into the
 * viewer. The viewer takes either a "contentColumn" parameter, which means the
 * information is in the database itself, or a "urlColumn" parameter, in which case
 * the information is loaded from an external source. This can be relative to the
 * CSX file the view is defined in.
 */
public class InfoDatabaseViewer extends PagingDatabaseViewer {
    @Override
	protected PageViewPanel createPanel() throws DatabaseViewerException {
        final JEditorPane textArea = new JEditorPane();
        textArea.setEditable(false);
        
        String contentTypeDef = getManager().getParameters().get("contentType");
        if(contentTypeDef.equalsIgnoreCase("html")) {
            textArea.setContentType("text/html");
        } else if(contentTypeDef.equalsIgnoreCase("text")) {
            textArea.setContentType("text/plain");
        } else {
            throw new DatabaseViewerException("Can not identify \"contentType\" parameter for InfoDatabaseViewer.");
        }
        
        final JScrollPane scrollPane = new JScrollPane(textArea);
        
        String urlColumnDef = getManager().getParameters().get("urlColumn");
        String contentColumnDef = getManager().getParameters().get("contentColumn");
        if(urlColumnDef != null && contentColumnDef != null) {
            throw new DatabaseViewerException("Can only handle either \"urlColumn\" or \"contentColumn\" in InfoDatabaseViewer.");
        }
        if(urlColumnDef == null && contentColumnDef == null) {
            throw new DatabaseViewerException("Either \"urlColumn\" or \"contentColumn\" required in InfoDatabaseViewer.");
        }

        final List<String> columns = new ArrayList<String>();
        if(urlColumnDef != null) {
            columns.add(urlColumnDef);
            return new PageViewPanel() {
                public void showItem(String keyValue) throws DatabaseViewerException {
                    try {
                        List<Vector<Object>> results = getManager().getConnection()
                                .executeQuery(
                                        columns,
                                        getManager().getTableName(),
                                        "WHERE " + getManager().getKeyName()
                                                + " = '" + keyValue + "';");
                        Vector fields = results.get(0);
                        String url = (String) fields.get(0);
                        URL resourceUrl = new URL(DatabaseViewerManager
                                .getBaseURL(), url);
                        textArea.setPage(resourceUrl);
                    } catch (Exception e) {
                        throw new DatabaseViewerException("Can not show view");
                    }
                }

                public Component getComponent() throws DatabaseViewerException {
                    return scrollPane;
                }
            };
        } else {
            columns.add(contentColumnDef);
            return new PageViewPanel() {
                public void showItem(String keyValue) throws DatabaseViewerException {
                    try {
                        List<Vector<Object>> results = getManager().getConnection()
                                .executeQuery(
                                        columns,
                                        getManager().getTableName(),
                                        "WHERE " + getManager().getKeyName()
                                                + " = '" + keyValue + "';");
                        Vector fields = results.get(0);
                        String content = (String) fields.get(0);
                        textArea.setText(content);
                    } catch (Exception e) {
                        throw new DatabaseViewerException("Can not show view");
                    }
                }

                public Component getComponent() throws DatabaseViewerException {
                    return scrollPane;
                }
            };
        }
    }
}
