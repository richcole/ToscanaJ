/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 28, 2002
 * Time: 3:40:32 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.util.STD_Iterator;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseSchemaChangedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseModifiedEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class DatabaseSchema implements BrokerEventListener {

    EventBroker broker;
    List tables;

    public DatabaseSchema(EventBroker broker) {
        this.tables = new ArrayList();
        this.broker = broker;
        this.broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.broker.subscribe(this, DatabaseModifiedEvent.class, Object.class);
    }

    void addTable(Table table) {
        this.tables.add(table);
    };

    public List getTables() {
        return tables;
    }

    public void readFromDBConnection(DatabaseConnection connection) {

        for (Iterator it = connection.getTableNames().iterator(); it.hasNext();) {
            String tableName = (String) it.next();
        }

        STD_Iterator it = new STD_Iterator(connection.getTableNames());

        this.tables.clear();
        for(it.reset();!it.atEnd();it.next()) {
            String tableName = (String)it.val();
            Table  table = new Table(broker, tableName); //@todo get key name
            STD_Iterator colIt = new STD_Iterator(
                connection.getColumnNames(tableName)
            );
            for(colIt.reset(); !colIt.atEnd(); colIt.next()) {
                table.addColumn(new Column((String)colIt.val()));
            }
            addTable(table);
        }
        broker.processEvent(new DatabaseSchemaChangedEvent(this, this));
    }

    public void processEvent(Event e) {
        if ( e instanceof DatabaseConnectedEvent ) {
            DatabaseConnectedEvent event = (DatabaseConnectedEvent) e;
            readFromDBConnection(event.getConnection());
        }
        if ( e instanceof DatabaseModifiedEvent ) {
            DatabaseModifiedEvent event = (DatabaseModifiedEvent) e;
            readFromDBConnection(event.getConnection());
        }
    }

    public EventBroker getBroker() {
        return broker;
    }
}
