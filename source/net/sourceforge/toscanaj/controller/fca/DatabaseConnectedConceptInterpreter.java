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
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;

import util.CollectionFactory;

public class DatabaseConnectedConceptInterpreter implements ConceptInterpreter {

    List nestingConcepts = CollectionFactory.createDefaultList();
    List filteringConcepts = CollectionFactory.createDefaultList();

    boolean displayMode;
    boolean filterMode;

    DatabaseConnection databaseConnection;

    public DatabaseConnectedConceptInterpreter(
        boolean displayMode,
        boolean filterMode,
        DatabaseConnection databaseConnection)
    {
        this.displayMode = displayMode;
        this.filterMode = filterMode;
        this.databaseConnection = databaseConnection;
    }

    public Iterator getObjectSetIterator(Concept concept) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        if( displayMode == CONTINGENT ) {
            return dbConcept.getObjectContingentIterator();
        } else if( displayMode == EXTENT ) {
            return dbConcept.getExtentIterator();
        } else {
            throw new RuntimeException("Can't happen");
        }
    }

    public Iterator getAttributeSetIterator(Concept concept) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        if( displayMode == CONTINGENT ) {
            return dbConcept.getObjectContingentSize();
        } else if( displayMode == EXTENT ) {
            return dbConcept.getExtentSize();
        } else {
            throw new RuntimeException("Can't happen");
        }
    }

    public int getAttributeCount(Concept concept) {
        return concept.getAttributeContingentSize();
    }

    public void setDisplayMode(boolean isContingent) {
        displayMode = isContingent;
    }

    public void setFilterMode(boolean isContingent) {
        filterMode = isContingent;
    }

    public ConceptInterpreter createNestedInterpreter(Concept concept) {
        return null;
    }

    public ConceptInterpreter createFilteredInterpreter(Concept concept) {
        return null;
    }

    public double getRelativeIntentSize(Concept concept) {
        return concept.getIntentSizeRelative();
    }

    public double getRelativeExtentSize(Concept concept) {
        return concept.getExtentSizeRelative();
    }
}
