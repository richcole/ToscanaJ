/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class AttributeEditingLabelViewPopupMenuHandler implements EventBrokerListener {
	private DiagramView diagramView;

	public AttributeEditingLabelViewPopupMenuHandler(DiagramView diagramView, EventBroker schemaBroker) {
		this.diagramView = diagramView;
	}

	public void processEvent(Event e) {
		CanvasItemEventWithPosition itemEvent = null;
		try {
			itemEvent = (CanvasItemEventWithPosition) e;
		} catch (ClassCastException e1) {
			throw new RuntimeException(getClass().getName() +
					" has to be subscribed to CanvasItemEventWithPositions only");
		}
		AttributeLabelView labelView = null;
		try {
			labelView = (AttributeLabelView) itemEvent.getItem();
		} catch (ClassCastException e1) {
			throw new RuntimeException(getClass().getName() +
					" has to be subscribed to events from ObjectLabelViews only");
		}
		openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
	}

	public void openPopupMenu(final AttributeLabelView labelView, Point2D canvasPosition, Point2D screenPosition) {
		final WritableFCAElement attribute = (WritableFCAElement) labelView.getEntryAtPosition(canvasPosition);
		if (attribute == null) {
			return;
		}
		final DiagramView parent = this.diagramView;
		JMenuItem menuItem;
		menuItem = new JMenuItem("Description...");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLEditorDialog xmlEditorDialog = new XMLEditorDialog(
				JOptionPane.getFrameForComponent(parent),"Edit attribute description");
				xmlEditorDialog.setContent(attribute.getDescription());
				xmlEditorDialog.setVisible(true);
				attribute.setDescription(xmlEditorDialog.getContent());
			}
		});
		
		JMenuItem renameAttrMenuItem = new JMenuItem("Rename attribute...");
		final String currentValue =  attribute.getData().toString();
		renameAttrMenuItem.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				InputTextDialog dialog = new InputTextDialog(JOptionPane.getFrameForComponent(diagramView), 
														     "Rename Attribute", "attribute", currentValue);
				if (!dialog.isCancelled()) {
					String newValue = dialog.getInput();
					attribute.setData(newValue);
					diagramView.repaint();
				}
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(menuItem);
		popupMenu.add(renameAttrMenuItem);
		
		popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
	}
}
