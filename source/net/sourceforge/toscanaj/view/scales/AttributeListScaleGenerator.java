package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;

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

    public Context generateScale(ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        AttributeListScaleGeneratorDialog scaleDialog = new AttributeListScaleGeneratorDialog(parent);
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
		
		boolean useAllCombi = scaleDialog.getUseAllCombinations();
		for (int i = 0; i < Math.pow(2,dimensions); i++) {
			String objectData = "";
			List relatedAttributes = new ArrayList();
			for(int j = 0; j < dimensions; j++) {
				if( j != 0 ) {
					objectData += " AND ";
				}
				if( (i & (1 << j)) == 0 ) {
					objectData += " NOT ";
				} else {
					relatedAttributes.add(attributes[j]);
				}
				objectData += "(" + tableData[j][1] + ")";
			}

			WritableFCAElement object = new FCAElementImplementation(objectData);
	
			if(useAllCombi){
				context.getObjects().add(object);
				Iterator it = relatedAttributes.iterator();
				while (it.hasNext()) {
					Attribute attrib = (Attribute) it.next();
					relation.insert(object, attrib);
				}
			}else{
				try{
					int result =
					databaseConnection.queryInt(
						"SELECT count (*) FROM "
							+ scheme.getDatabaseInfo().getTable().getSqlExpression()
							+ " WHERE ( "
							+ objectData
							+ " );",
						1);
					if( result != 0 ){
						context.getObjects().add(object);
						Iterator it = relatedAttributes.iterator();
						while (it.hasNext()) {
							Attribute attrib = (Attribute) it.next();
							relation.insert(object, attrib);
						}
					}	
				}catch(DatabaseException e) {
					throw new RuntimeException(e.getCause().getMessage());
				}
			}
		}
		return context;
    }

	private String createAttributeName(Object[][] data, int i) {
		String attrLabelName = (String)data[i][0]; 
		return attrLabelName.trim();
	}
}




