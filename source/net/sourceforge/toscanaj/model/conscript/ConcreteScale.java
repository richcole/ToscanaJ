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
public class ConcreteScale extends SchemaPart{
	private String[] tables;
	private String[] fields;
	private AbstractScale abstractScale;
	private QueryMap queryMap;
	private StringMap attributeMap;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	
	public ConcreteScale(URL file, String identifier, FormattedString title, 
						  String remark, Hashtable specials, String[] tables, String[] fields,
						  AbstractScale abstractScale, QueryMap queryMap, StringMap attributeMap) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.tables = tables;
		this.fields = fields;
		this.abstractScale = abstractScale;
		this.queryMap = queryMap;
		this.attributeMap = attributeMap;
	}
	
	/**
	 * Returns the abstractScale.
	 * @return AbstractScale
	 */
	public AbstractScale getAbstractScale() {
		return abstractScale;
	}

	/**
	 * Returns the attributeMap.
	 * @return StringMap
	 */
	public StringMap getAttributeMap() {
		return attributeMap;
	}

	/**
	 * Returns the fields.
	 * @return String[]
	 */
	public String[] getFields() {
		return fields;
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
	 * Returns the queryMap.
	 * @return QueryMap
	 */
	public QueryMap getQueryMap() {
		return queryMap;
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
	 * Returns the tables.
	 * @return String[]
	 */
	public String[] getTables() {
		return tables;
	}

	/**
	 * Returns the title.
	 * @return FormattedString
	 */
	public FormattedString getTitle() {
		return title;
	}

	/**
	 * Sets the abstractScale.
	 * @param abstractScale The abstractScale to set
	 */
	public void setAbstractScale(AbstractScale abstractScale) {
		this.abstractScale = abstractScale;
	}

	/**
	 * Sets the attributeMap.
	 * @param attributeMap The attributeMap to set
	 */
	public void setAttributeMap(StringMap attributeMap) {
		this.attributeMap = attributeMap;
	}

	/**
	 * Sets the fields.
	 * @param fields The fields to set
	 */
	public void setFields(String[] fields) {
		this.fields = fields;
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
	 * Sets the queryMap.
	 * @param queryMap The queryMap to set
	 */
	public void setQueryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
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
	 * Sets the tables.
	 * @param tables The tables to set
	 */
	public void setTables(String[] tables) {
		this.tables = tables;
	}

	/**
	 * Sets the title.
	 * @param title The title to set
	 */
	public void setTitle(FormattedString title) {
		this.title = title;
	}
}
