/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 28, 2002
 * Time: 3:40:32 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.util.STD_Iterator;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.gui.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.gui.events.DBSchemeChangedEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class DBScheme implements BrokerEventListener {

    EventBroker broker;
    List tables;

    public DBScheme(EventBroker broker) {
        this.tables = new ArrayList();
        this.broker = broker;
        this.broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
    }

    void addTable(Table table) {
        this.tables.add(table);
    };

    public List getTables() {
        return tables;
    }

    public void readFromDBConnection(DBConnection connection) {

        for (Iterator it = connection.getTableNames().iterator(); it.hasNext();) {
            String tableName = (String) it.next();
            System.out.println("Table Name:" + tableName);
        }

        STD_Iterator it = new STD_Iterator(connection.getTableNames());

        this.tables.clear();
        for(it.reset();!it.atEnd();it.next()) {
            String tableName = (String)it.val();
            System.out.println("tableName: " + tableName);
            Table  table = new Table(tableName, ""); //@todo get key name
            STD_Iterator colIt = new STD_Iterator(
                connection.getColumnNames(tableName)
            );
            for(colIt.reset(); !it.atEnd(); it.next()) {
                table.addColumn(new Column((String)it.val()));
            }
            addTable(table);
        }
        broker.processEvent(new DBSchemeChangedEvent(this, this));
        System.out.println("DBSchemeChangedEvent...");
    }

    public void processEvent(Event e) {
        System.out.println("Processing event in DBScheme...");
        if ( e instanceof DatabaseConnectedEvent ) {
            System.out.println("   -> was a DatabaseConnectedEvent");
            DatabaseConnectedEvent event = (DatabaseConnectedEvent) e;
            readFromDBConnection(event.getConnection());
        }
    }
}
