/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.source;

import java.io.File;

import javax.swing.JFrame;

import org.tockit.tupelware.model.TupelSet;


public interface TupelSource {
	String getMenuName();
	void show(JFrame parent, File lastLocation);
	TupelSet getTupels();
	int[] getObjectIndices();
	File getSelectedFile();
}
