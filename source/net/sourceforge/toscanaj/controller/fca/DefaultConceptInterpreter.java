/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 30, 2002
 * Time: 5:20:09 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

import java.util.List;

import org.w3c.dom.Node;

public class DefaultConceptInterpreter implements ConceptInterpreter {

    List N;
    List Z;

    boolean displayMode;
    boolean filterMode;

    DatabaseConnection databaseConnection;

    public DefaultConceptInterpreter(
        boolean displayMode,
        boolean filterMode,
        DatabaseConnection databaseConnection)
    {
        this.displayMode = displayMode;
        this.filterMode = filterMode;
        this.databaseConnection = databaseConnection;
    }

    public List getObjectSet(Concept concept) {
        return null;
    }

    public List getAttributeSet(Concept concept) {
        return null;
    }

    public int getObjectCount(Concept concept) {
        return 0;
    }

    public int getAttributeCount(Concept concept) {
        return 0;
    }

    public void setDisplayMode(boolean isContingent) {
    }

    public void setFilterMode(boolean isContingent) {
    }

    public ConceptInterpreter createNestedInterpreter(Concept concept) {
        return null;
    }

    public ConceptInterpreter createFilteredInterpreter(Concept concept) {
        return null;
    }

    public float getRelativeIntentSize(Concept concept) {
        return 0;
    }

    public float getRelativeExtentSize(Concept concept) {
        return 0;
    }
}
