package net.sourceforge.toscanaj.model.conscript;

import java.net.URL;
import java.util.Hashtable;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public abstract class SchemaPart {

	/**
	 * Returns the file.
	 * @return URL
	 */
	abstract public URL getFile();

	/**
	 * Returns the identifier.
	 * @return String
	 */
	abstract public String getIdentifier();
	/**
	 * Returns the remark.
	 * @return String
	 */
	abstract public String getRemark();

	/**
	 * Returns the specials.
	 * @return Hashtable
	 */
	abstract public Hashtable getSpecials();

	/**
	 * Returns the title.
	 * @return FormattedString
	 */
	abstract public FormattedString getTitle();

	/**
	 * Sets the file.
	 * @param file The file to set
	 */
	abstract public void setFile(URL file);

	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	abstract public void setIdentifier(String identifier);

	/**
	 * Sets the remark.
	 * @param remark The remark to set
	 */
	abstract public void setRemark(String remark);

	/**
	 * Sets the specials.
	 * @param specials The specials to set
	 */
	abstract public void setSpecials(Hashtable specials);

	/**
	 * Sets the title.
	 * @param title The title to set
	 */
	abstract public void setTitle(FormattedString title);
}
