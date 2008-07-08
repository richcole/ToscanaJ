/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class DatabaseSchema implements XMLizable, EventBrokerListener {

    EventBroker broker;
    List<Table> tables;
    public static final String DATABASE_SCHEMA_ELEMENT_NAME = "databaseSchema";

    public DatabaseSchema(final EventBroker broker) {
        this.tables = new ArrayList<Table>();
        this.broker = broker;
        this.broker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.broker.subscribe(this, DatabaseModifiedEvent.class, Object.class);
    }

    public DatabaseSchema(final EventBroker broker, final Element elem)
            throws XMLSyntaxError {
        this(broker);
        readXML(elem);
    }

    public Element toXML() {
        final Element retVal = new Element(DATABASE_SCHEMA_ELEMENT_NAME);
        for (final Table table : tables) {
            retVal.addContent(table.toXML());
        }
        return retVal;
    }

    public void readXML(final Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, DATABASE_SCHEMA_ELEMENT_NAME);
        final List<Element> tableElems = elem
                .getChildren(Table.TABLE_ELEMENT_NAME);
        for (final Element element : tableElems) {
            tables.add(new Table(broker, element));
        }
    }

    void addTable(final Table table) {
        this.tables.add(table);
    }

    public List<Table> getTables() {
        return tables;
    }

    public void readFromDBConnection(final DatabaseConnection connection) {
        final Iterator<String> it = connection.getTableNames().iterator();
        this.tables.clear();
        while (it.hasNext()) {
            final String tableName = it.next();
            final Table table = new Table(broker, tableName, false); // /@todo
                                                                     // get key
                                                                     // name
            for (final Column column : connection.getColumns(table)) {
                table.addColumn(column);
            }
            addTable(table);
        }
        broker.processEvent(new DatabaseSchemaChangedEvent(this, this));
    }

    public void processEvent(final Event e) {
        if (e instanceof DatabaseConnectedEvent) {
            final DatabaseConnectedEvent event = (DatabaseConnectedEvent) e;
            readFromDBConnection(event.getConnection());
        }
        if (e instanceof DatabaseModifiedEvent) {
            final DatabaseModifiedEvent event = (DatabaseModifiedEvent) e;
            readFromDBConnection(event.getConnection());
        }
    }

    public EventBroker getBroker() {
        return broker;
    }
}
