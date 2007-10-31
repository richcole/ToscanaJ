/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseModifiedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseSchemaChangedEvent;
import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseSchema implements XMLizable, EventBrokerListener {

    EventBroker broker;
    List<Table> tables;
    public static final String DATABASE_SCHEMA_ELEMENT_NAME = "databaseSchema";

    public DatabaseSchema(EventBroker broker) {
        this.tables = new ArrayList<Table>();
        this.broker = broker;
        this.broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.broker.subscribe(this, DatabaseModifiedEvent.class, Object.class);
    }

    public DatabaseSchema(EventBroker broker, Element elem) throws XMLSyntaxError {
        this(broker);
        readXML(elem);
    }

    public Element toXML() {
        Element retVal = new Element(DATABASE_SCHEMA_ELEMENT_NAME);
        for (Iterator<Table> iterator = tables.iterator(); iterator.hasNext();) {
            Table table = iterator.next();
            retVal.addContent(table.toXML());
        }
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, DATABASE_SCHEMA_ELEMENT_NAME);
        List<Element> tableElems = elem.getChildren(Table.TABLE_ELEMENT_NAME);
        for (Iterator<Element> iterator = tableElems.iterator(); iterator.hasNext();) {
            Element element = iterator.next();
            tables.add(new Table(broker, element));
        }
    }

    void addTable(Table table) {
        this.tables.add(table);
    }

    public List<Table> getTables() {
        return tables;
    }

    public void readFromDBConnection(DatabaseConnection connection) {
        Iterator<String> it = connection.getTableNames().iterator();
        this.tables.clear();
        while (it.hasNext()) {
            String tableName = it.next();
            Table table = new Table(broker, tableName, false); ///@todo get key name
            for (Iterator<Column> colIt = connection.getColumns(table).iterator(); colIt.hasNext(); ) {
                table.addColumn(colIt.next());
            }
            addTable(table);
        }
        broker.processEvent(new DatabaseSchemaChangedEvent(this, this));
    }

    public void processEvent(Event e) {
        if (e instanceof DatabaseConnectedEvent) {
            DatabaseConnectedEvent event = (DatabaseConnectedEvent) e;
            readFromDBConnection(event.getConnection());
        }
        if (e instanceof DatabaseModifiedEvent) {
            DatabaseModifiedEvent event = (DatabaseModifiedEvent) e;
            readFromDBConnection(event.getConnection());
        }
    }

    public EventBroker getBroker() {
        return broker;
    }
}
