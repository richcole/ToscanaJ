/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.BinaryRelation;

import java.util.Collection;

public class ViewContext implements Context {
    private CernatoModel model;
    private View view;
    private ScalingRelation relation;

    private class ScalingRelation implements BinaryRelation {
        public boolean contains(Object domainObject, Object rangeObject) {
            if(!(domainObject instanceof FCAObject)) {
                return false;
            }
            if(!(rangeObject instanceof Criterion)) {
                return false;
            }
            FCAObject fcaObject = (FCAObject) domainObject;
            Criterion criterion = (Criterion) rangeObject;
            Value relationValue = model.getContext().getRelationship(fcaObject, criterion.getProperty());
            return criterion.getValueGroup().containsValue(relationValue);
        }
    }

    public ViewContext(CernatoModel model, View view) {
        this.model = model;
        this.view = view;
        this.relation = new ScalingRelation();
    }

    public Collection getObjects() {
        return model.getContext().getObjects();
    }

    public Collection getAttributes() {
        return view.getCriteria();
    }

    public BinaryRelation getRelation() {
        return relation;
    }
}
