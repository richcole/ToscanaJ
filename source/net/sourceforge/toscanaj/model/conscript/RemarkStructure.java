package net.sourceforge.toscanaj.model.conscript;

import java.net.URL;
import java.util.Hashtable;
import java.util.List;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class RemarkStructure extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	private List remarks;
	
	public RemarkStructure(URL file, String identifier, FormattedString title, 
							String remark, Hashtable specials, List remarks) { 
	this.file = file;
	this.identifier = identifier;
	this.title = title;
	this.remark = remark;
	this.specials = specials;
	this.remarks = remarks;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getFile()
	 */
	public URL getFile() {
		return this.file;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getIdentifier()
	 */
	public String getIdentifier() {
		return this.identifier;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getRemark()
	 */
	public String getRemark() {
		return this.remark;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getSpecials()
	 */
	public Hashtable getSpecials() {
		return this.specials;
	}

	/**
	 * Returns the remarks.
	 * @return List
	 */
	public List getRemarks() {
		return remarks;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getTitle()
	 */
	public FormattedString getTitle() {
		return this.title;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#setFile(java.net.URL)
	 */
	public void setFile(URL file) {
		this.file = file;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String identifier) {
			this.identifier = identifier;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#setRemark(java.lang.String)
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#setSpecials(java.util.Hashtable)
	 */
	public void setSpecials(Hashtable specials) {
		this.specials = specials;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#setTitle(net.sourceforge.toscanaj.model.conscript.FormattedString)
	 */
	public void setTitle(FormattedString title) {
		this.title = title;
	}

	/**
	 * Sets the remarks.
	 * @param remarks The remarks to set
	 */
	public void setRemarks(List remarks) {
		this.remarks = remarks;
	}

}
