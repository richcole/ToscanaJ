/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source;

import java.io.File;

import javax.swing.JFrame;

import org.tockit.relations.model.Relation;


/**
 * Interface for getting Tuples via GUI.
 */
public interface TupleSource {
    /**
     * The text used in the menu.
     */
	String getMenuName();
    
    /**
     * Should pop up some dialog for the user to enter the required information.
     * 
     * The lastLocation can be used in file open dialogs. All information collected has to be
     * stored for later retrieval through the other methods of this interface.
     */
	void show(JFrame parent, File lastLocation);
    
    /**
     * Retrieves the tuples collected.
     * 
     * @pre show() has been called
     */
	Relation<Object> getTuples();
    
    /**
     * Returns the indices making up the object (as a crossproduct).
     * 
     * @pre show() has been called
     */
	int[] getObjectIndices();
    
    /**
     * Returns the file selected during the process of getting tuples if applicable.
     * 
     * @pre show() has been called
     * @return the file that was selected or null if not applicable
     */
	File getSelectedFile();
}
