/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.context;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import net.sourceforge.toscanaj.gui.dialog.*;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.ContextImplementation;

public class ContextTableColumnHeader extends JComponent implements Scrollable {
	private ContextImplementation context;
	private ContextTableEditorDialog dialog;

	public ContextTableColumnHeader(ContextImplementation context, ContextTableEditorDialog dialog) {
		super();
		this.context = context;
		this.dialog = dialog;
		addMouseListener(createMouseListener());
		setVisible(true);
		ToolTipManager.sharedInstance().registerComponent(this);
		updateSize();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		Font oldFont = g2d.getFont();

		g2d.setFont(oldFont.deriveFont(Font.BOLD));
		drawColumnHeader(g2d);

		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);
	}

	public Dimension calculateNewSize() {
		int numCol = this.context.getAttributes().size()+1;
		int numRow = this.context.getObjects().size()+1;
		return new Dimension(numCol * ContextTableView.CELL_WIDTH + 1, numRow * ContextTableView.CELL_HEIGHT + 1);
	}

	protected void drawColumnHeader(Graphics2D g2d) {
		g2d.setPaint(ContextTableView.TABLE_HEADER_COLOR);
		Iterator attrIt = this.context.getAttributes().iterator();
		int col = 0;
		while (attrIt.hasNext()) {
			Object attribute = attrIt.next();
			drawCell(g2d, attribute.toString(), col * ContextTableView.CELL_WIDTH, 0);
			col += 1;
		}
	}

	protected void drawCell(Graphics2D g2d, String content, int x, int y) {
		g2d.fill(new Rectangle2D.Double(x, y, ContextTableView.CELL_WIDTH, ContextTableView.CELL_HEIGHT));
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(ContextTableView.TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x, y, ContextTableView.CELL_WIDTH, ContextTableView.CELL_HEIGHT));

		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);

		g2d.drawString(
			newContent,
			x + ContextTableView.CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
			y + ContextTableView.CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
		g2d.setPaint(oldPaint);
	}

	protected String reduceStringDisplayWidth(String content, Graphics2D g2d) {
		String newContent = content;
		String tail = "...";
		int stringWidth = g2d.getFontMetrics().stringWidth(newContent);
		int tailWidth = g2d.getFontMetrics().stringWidth(tail);
		if (stringWidth > (ContextTableView.CELL_WIDTH - 10)) {
			while ((stringWidth + tailWidth) > (ContextTableView.CELL_WIDTH - 10)) {
				newContent = newContent.substring(0, (newContent.length() - 1));
				stringWidth = g2d.getFontMetrics().stringWidth(newContent);
			}
			newContent += tail;
		}
		return newContent;
	}

	public int getCellHeight() {
		return ContextTableView.CELL_HEIGHT;
	}

	public int getCellWidth() {
		return ContextTableView.CELL_WIDTH;
	}

	public String getToolTipText(MouseEvent e) {
		String tooltipText = null;

		ArrayList attributeArrayList = (ArrayList) context.getAttributes();
		ContextTableView.Position pos = getTablePosition(e.getX(), e.getY());

		if (pos != null) {
			if (pos.getCol() != attributeArrayList.size()) {
				Attribute attr = (Attribute) attributeArrayList.get(pos.getCol());
				tooltipText = (String) attr.getData();
			}
		}
		return tooltipText;
	}

	protected ContextTableView.Position getTablePosition(int xLoc, int yLoc) {
		int col = xLoc / getCellWidth();
		int row = yLoc / getCellHeight();
		if ((col > this.context.getAttributes().size() )
			|| (row > this.context.getObjects().size() )) {
			return null;
		}
		return new ContextTableView.Position(row, col);
	}
	
	public ContextImplementation getModel() {
		return this.context;
	}
	
    public Dimension getPreferredScrollableViewportSize() {
    	return ContextTableView.TABLE_HEADER_PREFERRED_VIEWPORT_SIZE;
    }
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    	if(orientation == SwingConstants.HORIZONTAL) {
    		return ContextTableView.CELL_WIDTH;
    	} else {
    	    return ContextTableView.CELL_HEIGHT;
    	}
    }
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    	// round the size in any direction down to a multiple of the cell size
        if(orientation == SwingConstants.HORIZONTAL) {
            return (visibleRect.width / ContextTableView.CELL_WIDTH) * ContextTableView.CELL_WIDTH;
        } else {
            return (visibleRect.height / ContextTableView.CELL_HEIGHT) * ContextTableView.CELL_HEIGHT;
        }
    }
 
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }
 
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public void updateSize() {
    	this.setPreferredSize(calculateNewSize());
    }
    
    public ContextImplementation getContext() {
        return context;
    }

    public void setContext(ContextImplementation context) {
        this.context = context;
        updateSize();
    }
    
	protected boolean collectionContainsString(
		String value,
		Collection collection) {
		Iterator it = collection.iterator();
		while (it.hasNext()) {
			Object obj = (Object) it.next();
			if (obj.toString().equalsIgnoreCase(value.trim())) {
				return true;
			}
		}
		return false;
	}
	
	private void removeAttribute(int pos) {
		List attributes = (List) this.context.getAttributes();
		attributes.remove(pos);
		repaint();
	}
	
	protected boolean addAttribute(String newAttributeName) {
		if (!collectionContainsString(newAttributeName, context.getAttributes())) {
			context.getAttributes().add(new Attribute(newAttributeName));
			return true;
		} else {
			return false;
		}
	}

	private MouseListener createMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					final ContextTableView.Position pos =
						getTablePosition(e.getX(), e.getY());
					if (pos == null) {
						return;
					} else {
						showPopupMenu(e, pos);
					}
				}
			}

			public void showPopupMenu(
				MouseEvent e,
				final ContextTableView.Position pos) {
					JPopupMenu popupMenu = new JPopupMenu();
					JMenuItem rename = new JMenuItem("Rename Attribute");
					rename.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							renameAttribute(pos.getCol());
						}
					});
					JMenuItem remove = new JMenuItem("Remove Attribute");
					remove.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							removeAttribute(pos.getCol());
						}
					});
					popupMenu.add(rename);
					popupMenu.add(remove);
					popupMenu.show(dialog.getScrollPane(), e.getX(), e.getY());
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					final ContextTableView.Position pos =
						getTablePosition(e.getX(), e.getY());
					if (pos == null) {
						return;
					} else {
						showPopupMenu(e, pos);
					}
				}
			}

			public void mouseClicked(MouseEvent e) {
				final ContextTableView.Position pos =
					getTablePosition(e.getX(), e.getY());
				if (pos == null) {
					return;
				}
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				if (e.getClickCount() != 2) {
					return;
				}
					renameAttribute(pos.getCol());
			}
		};
		return mouseListener;
	}
	
	private void renameAttribute(int num) {
		List attributeList = (List) this.context.getAttributes();
		String inputValue = "";
		do {
			Attribute attr = (Attribute) attributeList.get(num);
			InputTextDialog dialog = new InputTextDialog(this.dialog, "Rename Attribute", "attribute", attr.toString());
			if (!dialog.isCancelled()) {
				inputValue = dialog.getInput();
				if (!collectionContainsString(inputValue, attributeList)) {
					Attribute attribute = (Attribute) attributeList.get(num);
					attribute.setData(inputValue);
					repaint();
					inputValue = "";
				} else {
					JOptionPane.showMessageDialog(
						this,
						"An attribute named '"
							+ inputValue
							+ "' already exist. Please enter a different name.",
						"Attribute exists",
						JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				break;
			}
		} while (collectionContainsString(inputValue, attributeList));
		repaint();
	}

}