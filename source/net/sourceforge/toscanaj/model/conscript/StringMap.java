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
public class StringMap extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	private Hashtable map;
		
	public StringMap(URL file, String identifier, FormattedString title,
					  String remark, Hashtable specials, Hashtable map) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.map = map;
	}
	
	/**
	 * Returns the file.
	 * @return URL
	 */
	public URL getFile() {
		return file;
	}

	/**
	 * Returns the identifier.
	 * @return String
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the map.
	 * @return Hashtable
	 */
	public Hashtable getMap() {
		return map;
	}

	/**
	 * Returns the remark.
	 * @return String
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * Returns the specials.
	 * @return Hashtable
	 */
	public Hashtable getSpecials() {
		return specials;
	}

	/**
	 * Returns the title.
	 * @return FormattedString
	 */
	public FormattedString getTitle() {
		return title;
	}

	/**
	 * Sets the file.
	 * @param file The file to set
	 */
	public void setFile(URL file) {
		this.file = file;
	}

	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Sets the map.
	 * @param map The map to set
	 */
	public void setMap(Hashtable map) {
		this.map = map;
	}

	/**
	 * Sets the remark.
	 * @param remark The remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * Sets the specials.
	 * @param specials The specials to set
	 */
	public void setSpecials(Hashtable specials) {
		this.specials = specials;
	}

	/**
	 * Sets the title.
	 * @param title The title to set
	 */
	public void setTitle(FormattedString title) {
		this.title = title;
	}

}
