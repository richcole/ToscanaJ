package net.sourceforge.toscanaj.model.conscript;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class FormattedString {
	private String content;
	private StringFormat format;
	
	public FormattedString(String content, StringFormat format) {
		this.content = content;
		this.format = format;
	}
	
	/**
	 * Returns the content.
	 * @return String
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the format.
	 * @return StringFormat
	 */
	public StringFormat getFormat() {
		return format;
	}

	/**
	 * Sets the content.
	 * @param content The content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Sets the format.
	 * @param format The format to set
	 */
	public void setFormat(StringFormat format) {
		this.format = format;
	}

}
