/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.toscanaj.servlet;

import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.parser.CSXParser;

import org.tockit.events.EventBroker;

import java.io.File;
import java.net.URL;

public class GlobalVariables {
	
	private static DiagramSchema diagramSchema = null;
	private static ConceptualSchema conceptualSchema = null;
	private static DatabaseConnectedConceptInterpreter conceptInterpreter = null;
	private static String servletUrl;
	
	public static DiagramSchema getDiagramSchema() {
		return diagramSchema;
	}

	public static ConceptualSchema getConceptualSchema() {
		return conceptualSchema;
	}

	public static DatabaseConnectedConceptInterpreter 
		getConceptInterpreter() {

		return conceptInterpreter;
	}

	public static void initialize(File schemaFile, String servletUrl) {
		GlobalVariables.servletUrl = servletUrl;
		diagramSchema = DiagramSchema.getDefaultSchema();
		try {
			conceptualSchema = CSXParser.parse(new EventBroker(), schemaFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		DatabaseInfo databaseInfo = conceptualSchema.getDatabaseInfo();
		conceptInterpreter = new DatabaseConnectedConceptInterpreter(databaseInfo);

		try {
			DatabaseConnection connection =
				new DatabaseConnection(new EventBroker());
			connection.connect(databaseInfo);
			URL location =
				conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
			if (location != null) {
				connection.executeScript(location);
			}
			DatabaseConnection.setConnection(connection);
		} catch (DatabaseException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"Could not connect to database: " + e.getCause().getMessage(),
				e);
		}
	}
	
	public static boolean isInitialized() {
		return diagramSchema != null; 
	}

	public static String getServletUrl() {
		return servletUrl;
	}
}
