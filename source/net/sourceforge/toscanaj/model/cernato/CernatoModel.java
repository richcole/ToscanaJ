/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.toscanaj.model.manyvaluedcontext.*;

/**
 * @todo this is pretty much a hack since the class exposes its member in a writable fashion
 */
public class CernatoModel {
    private ManyValuedContextImplementation context = new ManyValuedContextImplementation();
    private Vector types = new Vector();
    private Vector views = new Vector();
    private Hashtable valuegroups = new Hashtable();

    public CernatoModel() {
    }

    public ManyValuedContextImplementation getContext() {
        return context;
    }

    public Vector getTypes() {
        return types;
    }

    public void addValueGroup(AttributeType type, ScaleColumn valueGroup, String groupId) {
    	Hashtable vgMap = getValueGroupMap(type);
    	vgMap.put(groupId, valueGroup);
    }
    
    private Hashtable getValueGroupMap(AttributeType type) {
		Hashtable vgMap = (Hashtable) this.valuegroups.get(type);
    	if(vgMap == null) {
    		vgMap = new Hashtable();
    		this.valuegroups.put(type, vgMap);
    	}
		return vgMap;
	}

	public ScaleColumn getValueGroup(AttributeType type, String valueGroupId) {
    	Hashtable vgMap = getValueGroupMap(type);
        return (ScaleColumn) vgMap.get(valueGroupId);
    }

    public Vector getViews() {
        return views;
    }
}
