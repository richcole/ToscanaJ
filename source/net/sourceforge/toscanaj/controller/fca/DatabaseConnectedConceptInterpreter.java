/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
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

    DatabaseConnection databaseConnection;

    public DatabaseConnectedConceptInterpreter(
        DatabaseConnection databaseConnection)
    {
        this.databaseConnection = databaseConnection;
    }

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        boolean displayMode = context.getObjectDisplayMode();
        if( displayMode == ConceptInterpretationContext.CONTINGENT ) {
            return dbConcept.getObjectContingentIterator();
        } else if( displayMode == ConceptInterpretationContext.EXTENT ) {
            return dbConcept.getExtentIterator();
        } else {
            throw new RuntimeException("Can't happen");
        }
    }

    public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        boolean displayMode = context.getObjectDisplayMode();
        if( displayMode == ConceptInterpretationContext.CONTINGENT ) {
            return dbConcept.getObjectContingentSize();
        } else if( displayMode == ConceptInterpretationContext.EXTENT ) {
            return dbConcept.getExtentSize();
        } else {
            throw new RuntimeException("Can't happen");
        }
    }

    public int getAttributeCount(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentSize();
    }

    public double getRelativeIntentSize(Concept concept, ConceptInterpretationContext context) {
        return concept.getIntentSizeRelative();
    }

    public double getRelativeExtentSize(Concept concept, ConceptInterpretationContext context) {
        return concept.getExtentSizeRelative();
    }
}
