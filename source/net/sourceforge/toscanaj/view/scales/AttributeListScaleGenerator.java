package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.lattice.Attribute;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class AttributeListScaleGenerator implements ScaleGenerator{
    private Frame parent;

    public AttributeListScaleGenerator (Frame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Attribute List";
    }

    
    public boolean canHandleColumns(TableColumnPair[] columns) {
       return true;
    }

    public Context generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        Column column = columns[0].getColumn();
        String columnName = column.getName();
        AttributeListScaleGeneratorDialog scaleDialog = new AttributeListScaleGeneratorDialog(parent, scheme, databaseConnection);
        if (!scaleDialog.execute()) {
            return null;
        }
		ContextImplementation context = new ContextImplementation();
		context.setName(scaleDialog.getDiagramTitle());
		BinaryRelationImplementation relation = context.getRelationImplementation();		

		Object[][] tableData = scaleDialog.getData();
		int dimensions = tableData.length;
		Attribute[] attributes = new Attribute[dimensions];

		for (int i = 0; i < dimensions; i++) {
			String attributeName = createAttributeName(tableData, i);
			attributes[i] = new Attribute(attributeName);
			context.getAttributes().add(attributes[i]);
		}
		for (int i = 0; i < Math.pow(2,dimensions); i++) {
			String object = "";
			List relatedAttributes = new ArrayList();
			for(int j = 0; j < dimensions; j++) {
				if( j != 0 ) {
					object += " AND ";
				}
				if( (i & (1 << j)) == 0 ) {
					object += " NOT ";
				} else {
					relatedAttributes.add(attributes[j]);
				}
				object += tableData[j][1];
			}
			context.getObjects().add(object);
			Iterator it = relatedAttributes.iterator();
			while (it.hasNext()) {
				Attribute attrib = (Attribute) it.next();
				relation.insert(object, attrib);
			}
		}
		return context;
    }

	private String createAttributeName(Object[][] data, int i) {
		String attrLabelName = (String)data[i][0]; 
		return attrLabelName.trim();
	}
}



