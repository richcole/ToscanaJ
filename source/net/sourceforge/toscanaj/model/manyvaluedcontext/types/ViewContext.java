/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.manyvaluedcontext.types;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import net.sourceforge.toscanaj.model.context.BinaryRelation;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.Criterion;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;

public class ViewContext implements Context{
	private ManyValuedContext context;
	private ScalingRelation relation;
	private Set attributes;
	private String name;
	private class ScalingRelation implements BinaryRelation {
		  public boolean contains(Object domainObject, Object rangeObject) {
			  if (!(domainObject instanceof FCAElement)) {
				  return false;
			  }
			  FCAElement fcaObject = (FCAElement) domainObject;
			  if (!(rangeObject instanceof FCAElement)) {
				  return false;
			  }
              FCAElement attribute = (FCAElement) rangeObject;
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
			FCAElementImplementation attribute = new FCAElementImplementation(criterion, null);
            attribute.setContextPosition(attributes.size());
            attributes.add(attribute);
		}
		this.name = view.getName();
	}

	public Set getObjects() {
		return context.getObjects();
	}

	public Set getAttributes() {
		return attributes;
	}

	public BinaryRelation getRelation() {
		return relation;
	}

	public String getName() {
		return this.name;
	}
}
