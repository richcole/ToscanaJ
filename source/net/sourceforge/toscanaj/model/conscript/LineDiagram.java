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
public class LineDiagram extends SchemaPart{
	private TypedSize unitLength;
	private Point[] points;
	private Line[] lines;
	private FCAObject[] objects;
	private FCAAttribute[] attributes;
	private Concept[] concepts;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	
	public LineDiagram(URL file, String identifier, FormattedString title,
						String remark, Hashtable specials, TypedSize unitLength,
						Point[] points, Line[] lines, FCAObject[] objects,
						FCAAttribute[] attributes, Concept[] concepts) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.unitLength = unitLength;
		this.points = points;
		this.lines = lines;
		this.objects = objects;
		this.attributes = attributes;
		this.concepts = concepts;		
	}

	/**
	 * Returns the attributes.
	 * @return FCAAttribute[]
	 */
	public FCAAttribute[] getAttributes() {
		return attributes;
	}

	/**
	 * Returns the concepts.
	 * @return Concept[]
	 */
	public Concept[] getConcepts() {
		return concepts;
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
	 * Returns the lines.
	 * @return Line[]
	 */
	public Line[] getLines() {
		return lines;
	}

	/**
	 * Returns the objects.
	 * @return FCAObject[]
	 */
	public FCAObject[] getObjects() {
		return objects;
	}

	/**
	 * Returns the points.
	 * @return Point[]
	 */
	public Point[] getPoints() {
		return points;
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
	 * Returns the unitLength.
	 * @return TypedSize
	 */
	public TypedSize getUnitLength() {
		return unitLength;
	}

	/**
	 * Sets the attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(FCAAttribute[] attributes) {
		this.attributes = attributes;
	}

	/**
	 * Sets the concepts.
	 * @param concepts The concepts to set
	 */
	public void setConcepts(Concept[] concepts) {
		this.concepts = concepts;
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
	 * Sets the lines.
	 * @param lines The lines to set
	 */
	public void setLines(Line[] lines) {
		this.lines = lines;
	}

	/**
	 * Sets the objects.
	 * @param objects The objects to set
	 */
	public void setObjects(FCAObject[] objects) {
		this.objects = objects;
	}

	/**
	 * Sets the points.
	 * @param points The points to set
	 */
	public void setPoints(Point[] points) {
		this.points = points;
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

	/**
	 * Sets the unitLength.
	 * @param unitLength The unitLength to set
	 */
	public void setUnitLength(TypedSize unitLength) {
		this.unitLength = unitLength;
	}

}
