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
public class AbstractScale extends SchemaPart{
	private FormalContext context;
	private LineDiagram[] lineDiagrams;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	
	public AbstractScale(URL file, String identifier, FormattedString title,
						  String remark, Hashtable specials, 
						  FormalContext context, LineDiagram[] lineDiagrams) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.context = context;
		this.lineDiagrams = lineDiagrams;
	}
		
	/**
	 * Returns the context.
	 * @return FormalContext
	 */
	public FormalContext getContext() {
		return context;
	}

	/**
	 * Returns the lineDiagrams.
	 * @return LineDiagram[]
	 */
	public LineDiagram[] getLineDiagrams() {
		return lineDiagrams;
	}

	/**
	 * Sets the context.
	 * @param context The context to set
	 */
	public void setContext(FormalContext context) {
		this.context = context;
	}

	/**
	 * Sets the lineDiagrams.
	 * @param lineDiagrams The lineDiagrams to set
	 */
	public void setLineDiagrams(LineDiagram[] lineDiagrams) {
		this.lineDiagrams = lineDiagrams;
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
