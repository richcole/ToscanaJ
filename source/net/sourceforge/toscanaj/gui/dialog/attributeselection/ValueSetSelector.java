/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package net.sourceforge.toscanaj.gui.dialog.attributeselection;
import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.tockit.events.EventBrokerListener;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.sql.Expression;

public class ValueSetSelector extends JPanel implements EventBrokerListener, SQLExpressionSource {
	private DatabaseConnection connection;
    private JList valueList;
	
    public ValueSetSelector(DatabaseConnection databaseConnection, EventBroker eventBroker) {
    	this.connection = databaseConnection;
    	eventBroker.subscribe(this, SelectedColumnChangedEvent.class, Object.class);
    	init();
    }
    
    private void init() {
    	JLabel label = new JLabel("Select values:");
        valueList = new JList();
        valueList.setModel(new DefaultListModel());
    	valueList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		this.setLayout(new BorderLayout());
		
    	this.add(label, BorderLayout.NORTH);
        this.add(valueList, BorderLayout.CENTER);
    }

    public void processEvent(Event e) {
    	Column column = (Column) e.getSubject();
    	updateContents(column);
    }
    
    private void updateContents(Column column) {
		DefaultListModel model = (DefaultListModel) valueList.getModel();
		model.removeAllElements();
    	String tableName = column.getTable().getName();
    	try {
            List results = this.connection.executeQuery("SELECT DISTINCT " + column.getName() + " FROM " + tableName + ";");
            for (Iterator iter = results.iterator(); iter.hasNext();) {
                Vector result = (Vector) iter.next();
                model.addElement(result.get(0));
            }
        } catch (DatabaseException e) {
        	e.printStackTrace();
        }
    }
    
    public Expression getExpression() {
        return null;
    }
}