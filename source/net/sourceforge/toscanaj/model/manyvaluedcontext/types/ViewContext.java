/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sourceforge.toscanaj.model.BinaryRelation;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.Criterion;
import net.sourceforge.toscanaj.model.manyvaluedcontext.FCAObject;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;

public class ViewContext implements Context{
	private ManyValuedContext context;
	private ScalingRelation relation;
	private Collection attributes;
	private String name;
	private class ScalingRelation implements BinaryRelation {
		  public boolean contains(Object domainObject, Object rangeObject) {
			  if (!(domainObject instanceof FCAObject)) {
				  return false;
			  }
			  FCAObject fcaObject = (FCAObject) domainObject;
			  if (!(rangeObject instanceof Attribute)) {
				  return false;
			  }
			  Attribute attribute = (Attribute) rangeObject;
			  if (!(attribute.getData() instanceof Criterion)) {
				  return false;
			  }
			  Criterion criterion = (Criterion) attribute.getData();
			  AttributeValue relationValue = context.getRelationship(fcaObject, criterion.getProperty());
			  return criterion.getValueGroup().containsValue(relationValue);
		  }
	  }
	
	public ViewContext(ManyValuedContext context, View view) {
		this.context = context;
		this.relation = new ScalingRelation();
		attributes = new HashSet();
		for (Iterator iterator = view.getCriteria().iterator(); iterator.hasNext();) {
			Criterion criterion = (Criterion) iterator.next();
			attributes.add(new Attribute(criterion, null));
		}
		this.name = view.getName();
	}

	public Collection getObjects() {
		return context.getObjects();
	}

	public Collection getAttributes() {
		return attributes;
	}

	public BinaryRelation getRelation() {
		return relation;
	}

	public String getName() {
		return this.name;
	}
}
