package net.sourceforge.toscanaj.model.conscript;

import java.net.URL;
import java.util.Hashtable;

import net.sourceforge.toscanaj.model.BinaryRelation;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class FormalContext extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	private FCAObject[] objects;
	private FCAAttribute[] attributes;
	private BinaryRelation relation;

	public FormalContext(URL file, String identifier, FormattedString title, String remark, Hashtable specials, FCAObject[] objects, FCAAttribute[] attributes, BinaryRelation relation) { 
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.objects = objects;
		this.attributes = attributes;
		this.relation = relation;
		
	}
	
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getFile()
	 */
	public URL getFile() {
		return file;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getIdentifier()
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getRemark()
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getSpecials()
	 */
	public Hashtable getSpecials() {
		return specials;
	}
	/**
	 * @see net.sourceforge.toscanaj.model.conscript.SchemaPart#getTitle()
	 */
	public FormattedString getTitle() {
		return title;
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
	 * Returns the attributes.
	 * @return FCAAttribute[]
	 */
	public FCAAttribute[] getAttributes() {
		return attributes;
	}

	/**
	 * Returns the objects.
	 * @return FCAObject[]
	 */
	public FCAObject[] getObjects() {
		return objects;
	}

	/**
	 * Returns the relation.
	 * @return BinaryRelation
	 */
	public BinaryRelation getRelation() {
		return relation;
	}

	/**
	 * Sets the attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(FCAAttribute[] attributes) {
		this.attributes = attributes;
	}

	/**
	 * Sets the objects.
	 * @param objects The objects to set
	 */
	public void setObjects(FCAObject[] objects) {
		this.objects = objects;
	}

	/**
	 * Sets the relation.
	 * @param relation The relation to set
	 */
	public void setRelation(BinaryRelation relation) {
		this.relation = relation;
	}

}
