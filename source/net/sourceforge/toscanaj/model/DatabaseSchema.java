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
import java.sql.Types;

import org.jdom.Element;

public class DatabaseSchema implements XML_Serializable, BrokerEventListener {

    EventBroker broker;
    List tables;
    private static final String DATABASE_SCHEMA_ELEMENT_NAME = "databaseSchema";

    public DatabaseSchema(EventBroker broker) {
        this.tables = new ArrayList();
        this.broker = broker;
        this.broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.broker.subscribe(this, DatabaseModifiedEvent.class, Object.class);
    }

    public Element toXML() {
        Element retVal = new Element(DATABASE_SCHEMA_ELEMENT_NAME);
        for (Iterator iterator = tables.iterator(); iterator.hasNext();) {
            Table table = (Table) iterator.next();
            retVal.addContent(table.toXML());
        }
        return retVal;
    }

    public void readXML(Element elem) throws XML_SyntaxError {
        throw new XML_SyntaxError("Not yet implemented");
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
                connection.getColumns(tableName)
            );
            for(colIt.reset(); !colIt.atEnd(); colIt.next()) {
                table.addColumn((Column) colIt.val());
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
