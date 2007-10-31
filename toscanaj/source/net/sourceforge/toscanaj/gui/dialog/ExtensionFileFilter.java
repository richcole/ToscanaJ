/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter {
	String[] extensions;
	String fileTypeName;

	public ExtensionFileFilter(String[] extensions, String fileTypeName) {
		this.extensions = extensions;
		this.fileTypeName = fileTypeName;
	}

	@Override
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}

			String ext = f.getName().substring(f.getName().lastIndexOf('.')+1);
			if (ext == null) {
				return false;
			}
			for (int i = 0; i < this.extensions.length; i++) {
				if (ext.toLowerCase().equals(this.extensions[i].toLowerCase())) {
					return true;				
				}
			}
		}
		return false;
	} //accept

	public String getFileTypeName() {
		return this.fileTypeName;
	} //get desc

	/**
	 * Returns the extensions.
	 */
	public String[] getExtensions() {
		return this.extensions;
	}
	
	@Override
	public String getDescription() {
		String retVal = this.fileTypeName + " (";
		for (int i = 0; i < this.extensions.length; i++) {
			retVal += "*." + this.extensions[i];
			if(i != this.extensions.length - 1) {
				retVal += ", ";
			}
		}
		retVal += ")";
		return retVal;
	}

} //myfilter
