package net.sourceforge.toscanaj.dbviewer;

/**
 * The plug-in API for database report generators.
 *
 * This works very similar to the DatabaseViewer interface. The only difference is
 * that there is no showObject(String) which gets the object key, but a 
 * showReport(String) which gets a where clause determining the contingent or extent
 * that is to be displayed.
 */
public interface DatabaseReportGenerator
{
    void initialize(DatabaseReportGeneratorManager manager)
        throws DatabaseViewerInitializationException;
    
    void showReport(String whereClause);
}
