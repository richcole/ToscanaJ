/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;

import org.jdom.Element;

// @todo perhaps this should be an action or be used as an action:
// in ElbaMainPanel corresponding button can be enabled then when context is created.
public class ContextConsistencyChecker {
	
	public static void checkConsistency(ConceptualSchema conceptualSchema, Context context,
									DatabaseConnection databaseConnection, Component parent) {
		List problems = new ArrayList();
		DatabaseInfo dbinfo = conceptualSchema.getDatabaseInfo();
		int sumCounts = 0;
		List validClauses = new ArrayList();

		// check if all objects are WHERE clauses
		Iterator it = context.getObjects().iterator();
		while (it.hasNext()) {
			String clause = (String) it.next();
			String query =
				"SELECT count(*) FROM "
					+ dbinfo.getTable().getSqlExpression()
					+ " WHERE ("
					+ clause
					+ ");";
			try {
				sumCounts += databaseConnection.queryInt(query, 1);
				validClauses.add(clause);
			} catch (DatabaseException e) {
				problems.add(
					"Object '"
						+ clause
						+ "' is not a valid clause for the database.\n"
						+ "The database returned:\n\t"
						+ e.getCause().getMessage());
			}
		}

		// check if all conjunctions are empty
		if (problems.isEmpty()) {
			it = context.getObjects().iterator();
			while (it.hasNext()) {
				String clause = (String) it.next();
				validClauses.remove(clause);
				Iterator it2 = validClauses.iterator();
				while (it2.hasNext()) {
					String otherClause = (String) it2.next();
					String query =
						"SELECT count(*) FROM "
							+ dbinfo.getTable().getSqlExpression()
							+ " WHERE ("
							+ clause
							+ ") AND ("
							+ otherClause
							+ ");";
					try {
						int count = databaseConnection.queryInt(query, 1);
						if (count != 0) {
							problems.add(
								"Object clauses '"
									+ clause
									+ "' and '"
									+ otherClause
									+ "' overlap.");
						}
					} catch (DatabaseException e) {
						// should not happen
						ErrorDialog.showError(parent,e,"Error querying the database");
					}
				}
			}
		}

		// check if disjunction of all contingents covers the data set
		if (problems.isEmpty()) {
			// doesn't make sense if we have problems so far
			String query =
				"SELECT count(*) FROM " + dbinfo.getTable().getSqlExpression() + ";";
			try {
				int count = databaseConnection.queryInt(query, 1);
				if (count != sumCounts) {
					problems.add("Object clauses do not cover database.");
				}
			} catch (DatabaseException e) {
				// should not happen
				throw new RuntimeException("Failed to query the database.", e);
			}
		}

		// give feedback
		if (problems.isEmpty()) {
			JOptionPane.showMessageDialog(
				parent,
				"No problems found",
				"Objects correct",
				JOptionPane.INFORMATION_MESSAGE);
		} else {
			// show problems
			Iterator strIt = problems.iterator();
			Element problemDescription = new Element("description");
			Element htmlElement = new Element("html");
			htmlElement.addContent(
				new Element("title").addContent("Consistency problems"));
			problemDescription.addContent(htmlElement);
			Element body = new Element("body");
			htmlElement.addContent(body);
			body.addContent(new Element("h1").addContent("Problems found:"));
			while (strIt.hasNext()) {
				String problem = (String) strIt.next();
				body.addContent(new Element("pre").addContent(problem));
			}
			Frame frame = JOptionPane.getFrameForComponent(parent);
			DescriptionViewer.show(frame, problemDescription);
		}
	}
	

}
