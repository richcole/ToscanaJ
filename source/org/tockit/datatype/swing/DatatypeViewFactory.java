/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.swing;

import javax.swing.CellEditor;
import javax.swing.JPanel;

import org.tockit.datatype.Datatype;


public abstract class DatatypeViewFactory {
	public static CellEditor getValueCellEditor(Datatype datatype) {
		return null;
	}
	
	public static JPanel getValueSetPanel(Datatype datatype) {
		return null;
	}
	
	public static JPanel getSubtypingPanel(Datatype datatype) {
		return null;
	}
}
