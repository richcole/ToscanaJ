package net.sourceforge.toscanaj.dbviewer;

/**
 * The plug-in API for database viewers.
 *
 * This interface offers a simple way to add database viewers into ToscanaJ.
 * The viewer and its options will be defined in the CSX file by giving a
 * <viewer> element directly in the <conceptualSchema>. Multiple viewers
 * can be given, although at the time of writing only the first one will
 * be used.
 *
 * The viewer element has to attributes: class and name. The class attribute
 * tells ToscanaJ which class to load, which has to be an implementation of
 * this interface. The name is for display purposes: it will be used in menus
 * and other GUI elements.
 *
 * There are two types of children of <viewer>: there can be one or more
 * <parameter>s and there can be one <template>. A parameter has to attributes:
 * name and value. They will be available to the plugin as string to string mapping.
 * The template can be used in two ways: either it does just contain some text
 * as used e.g. by the SimpleDatabaseViewer. The advantage of using the template
 * element and not a parameter is that you can have multiple lines. Another way
 * to use the template is to give a full XML branch. This can be queried by the
 * plugin using JDOM.
 *
 * The DatabaseViewer interface is very simple to implement. Just write a default
 * constructor, some initialization code in initialize(DatabaseViewerManager)
 * which should at least store the manager. You can throw a
 * DatabaseViewerInitializationException if something fails here. There is a version
 * of this exception taking a String and another Exception, this way you can pass
 * additional information to the outside, e.g. if you can not open a file, give a
 * message like "Could not open file." and the IOException or whatever you got,
 * this way the details of the original problem (message and stack trace) can be
 * retrieved.
 *
 * Afterwards you have to implement
 * showObject(String), which gets the ID of the object to display. All other information
 * can be get from DatabaseViewerManager.
 *
 * Methods you might want to use from DatabaseViewerManager when implementing a viewer:
 * - getParameters(): returns a Dictionary mapping the parameter names to the values, e.g.
 *                    getParameters().get("myParam") returns the value of the parameter
 *                    called "myParam" or null if there was no such parameter found in the
 *                    CSX file
 * - getTemplateString(): returns the text content (text node in the DOM) of the <template>
 *                        element. Useful if you want to use simple, non-hierarchical templates.
 *                        Sometimes it is easier to use only parameters, esp. if you need only
 *                        a string with one line.
 * - getTemplate(): returns the full <template> element as org.jdom.Element. This way you can
 *                  define more complex templates, e.g. using HTML or XForms.
 * - getConnection(): returns the database connection as net.sourceforge.controller.db.DBConnection.
 *                    You can instantly call queries on this one, typically you will use the method
 *                    executeQuery(List, String, String) which takes a list of field names to query
 *                    (Strings), the name of the table to query and a where clause.
 * - getDatabaseInfo(): gives the DatabaseInfo object describing the database schema. Most relevant
 *                      here are the methods getTableName() and getKey() which return the table
 *                      used for standard queries and the key used within it.
 * - getParentWindow(): gives you a java.awt.Frame which can be used as parent for dialogs (the main
 *                      window for ToscanaJ). Can be null.
 *
 * A typical query will look like this:
 * // we assume fieldNames contains all column names whose values we want to get
 * // manager holds the DatabaseViewerManager
 * // objectKey is the parameter from showObject(String)
 * DatabaseInfo dbInfo = manager.getDatabaseInfo();
 * DBConncetion con = manager.getConnection();
 * String whereClause = "WHERE " + dbInfo.getKey() + "='" + objectKey + "'";
 * List results;
 * // the next command can throw net.sourceforge.toscanaj.controller.db.DatabaseException
 * try {
 *     results = con.executeQuery(fieldNames, dbInfo.getTableName(), whereClause);
 * }
 * catch(DatabaseException e) {
 *     // try to handle the problem, note that DatabaseException.getOriginal() gives the
 *     // SQL exception if the query failed -- often you will have a chance to display
 *     // the error instead of the object
 * }
 *
 * The results variable will hold a List of Vectors. Each item in the list will represent
 * a row of the database fulfilling the where clause (i.e. there should be only one if your
 * key is really a key). Each Vector will contain a number of Strings: one for each column
 * queried, in the same order as the field names in the list given. How you display the
 * results is your problem ;-)
 *   
 * Check the implementations in the net.sourceforge.toscanaj.dbviewer package for some
 * complete examples.
 */
public interface DatabaseViewer
{
    void initialize(DatabaseViewerManager manager)
        throws DatabaseViewerInitializationException;
    
    void showObject(String objectKey);
}
