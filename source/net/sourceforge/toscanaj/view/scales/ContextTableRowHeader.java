/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.Color;
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

import net.sourceforge.toscanaj.model.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.lattice.Attribute;

/**
 * @todo there are a number of constants in here which are the same as in other
 * parts of the context table editing components. Share them...
 *
 */
public class ContextTableRowHeader extends JComponent implements Scrollable {
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color TABLE_HEADER_COLOR = Color.LIGHT_GRAY;
	private ContextImplementation context;
	private ContextTableScaleEditorDialog dialog;
	private static final int CELL_WIDTH = 150;
	private static final int CELL_HEIGHT = 30;
	private static final Dimension PREFERRED_VIEWPORT_SIZE = new Dimension(CELL_WIDTH, CELL_HEIGHT);

	public static class Position {
		private int row;
		private int col;
		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public int getCol() {
			return col;
		}

		public int getRow() {
			return row;
		}
	}

	public ContextTableRowHeader (ContextImplementation context, ContextTableScaleEditorDialog dialog) {
		super();
		this.context = context;
		this.dialog = dialog;
		setVisible(true);
		addMouseListener(createMouseListener());
		ToolTipManager.sharedInstance().registerComponent(this);
		updateSize();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		Font oldFont = g2d.getFont();

		g2d.setFont(oldFont.deriveFont(Font.BOLD));
		Iterator objIt = this.context.getObjects().iterator();
		int row = 0;
		while (objIt.hasNext()) {
			Object object = objIt.next();
			drawRow(g2d, object, row);
			row += 1;
		}

		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);
	}

	public Dimension calculateNewSize() {
		int numCol = this.context.getAttributes().size()+1;
		int numRow = this.context.getObjects().size()+1;
		return new Dimension(numCol * CELL_WIDTH + 1, numRow * CELL_HEIGHT + 1);
	}

	protected void drawRow(Graphics2D g2d, Object object, int row) {
		Font font = g2d.getFont();
		g2d.setFont(font.deriveFont(Font.BOLD));
		g2d.setPaint(TABLE_HEADER_COLOR);
		int y = row * CELL_HEIGHT;
		drawCell(g2d, object.toString(), 0, y);
	}

	protected void drawCell(Graphics2D g2d, String content, int x, int y) {
		g2d.fill(new Rectangle2D.Double(x, y, CELL_WIDTH, CELL_HEIGHT));
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x, y, CELL_WIDTH, CELL_HEIGHT));

		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);

		g2d.drawString(
			newContent,
			x + CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
			y + CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
		g2d.setPaint(oldPaint);
	}

	protected String reduceStringDisplayWidth(String content, Graphics2D g2d) {
		String newContent = content;
		String tail = "...";
		int stringWidth = g2d.getFontMetrics().stringWidth(newContent);
		int tailWidth = g2d.getFontMetrics().stringWidth(tail);
		if (stringWidth > (CELL_WIDTH - 10)) {
			while ((stringWidth + tailWidth) > (CELL_WIDTH - 10)) {
				newContent = newContent.substring(0, (newContent.length() - 1));
				stringWidth = g2d.getFontMetrics().stringWidth(newContent);
			}
			newContent += tail;
		}
		return newContent;
	}

	public int getCellHeight() {
		return CELL_HEIGHT;
	}

	public int getCellWidth() {
		return CELL_WIDTH;
	}
		
	public String getToolTipText(MouseEvent e) {
		String tooltipText = null;
		
		ArrayList objectsArrayList = (ArrayList) context.getObjects();
		Position pos = getTablePosition(e.getX(), e.getY());

		if (pos != null) {
			if (pos.getCol() == 0 && pos.getRow() != objectsArrayList.size()) {
				tooltipText = (String) objectsArrayList.get(pos.getRow());
			}
		}
		return tooltipText;
	}

	protected Position getTablePosition(int xLoc, int yLoc) {
		int col = xLoc / getCellWidth();
		int row = yLoc / getCellHeight();
		if ((col > this.context.getAttributes().size() )
			|| (row > this.context.getObjects().size() )) {
			return null;
		}
		return new Position(row, col);
	}
	
	public ContextImplementation getModel() {
		return this.context;
	}
	
    public Dimension getPreferredScrollableViewportSize() {
    	return PREFERRED_VIEWPORT_SIZE;
    }
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    	if(orientation == SwingConstants.HORIZONTAL) {
    		return CELL_WIDTH;
    	} else {
    	    return CELL_HEIGHT;
    	}
    }
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    	// round the size in any direction down to a multiple of the cell size
        if(orientation == SwingConstants.HORIZONTAL) {
            return (visibleRect.width / CELL_WIDTH) * CELL_WIDTH;
        } else {
            return (visibleRect.height / CELL_HEIGHT) * CELL_HEIGHT;
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
    
	private MouseListener createMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					final Position pos =
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
				final Position pos) {
					JPopupMenu popupMenu = new JPopupMenu();
					JMenuItem rename = new JMenuItem("Rename Object");
					rename.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							renameObject(pos.getRow());
						}
					});
					JMenuItem remove = new JMenuItem("Remove Object");
					remove.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							removeObject(pos.getRow());
						}
					});
					popupMenu.add(rename);
					popupMenu.add(remove);
					popupMenu.show(dialog.getScrollPane(), e.getX(), e.getY());
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					final Position pos =
						getTablePosition(e.getX(), e.getY());
					if (pos == null) {
						return;
					} else {
						showPopupMenu(e, pos);
					}
				}
			}

			public void mouseClicked(MouseEvent e) {
				final Position pos =
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

				if (pos.getCol() == 0) {
						renameObject(pos.getRow());
				} 
			}
		};
		return mouseListener;
	}

	private void removeObject(int pos) {
		List objects = (List) this.context.getObjects();
		objects.remove(pos);
		repaint();
	}
	
	protected boolean addObject(String newObjectName){
		if (!collectionContainsString(newObjectName, context.getObjects())) {
			context.getObjects().add(newObjectName);
			return true;
		} else {
			return false;
		}
	}
	
	private void renameObject(int num) {
		List objectList = (List) this.context.getObjects();
		String inputValue = "";
		do {
			String oldName = (String) objectList.get(num);
			InputTextDialog dialog = new InputTextDialog(this.dialog, "Rename Object", "object", oldName);
			if (!dialog.isCancelled()) {
				inputValue = dialog.getInput();
				if (!collectionContainsString(inputValue, objectList)) {
					objectList.set(num, inputValue);
					BinaryRelationImplementation relation =
						this.context.getRelationImplementation();
					Iterator attrIt = this.context.getAttributes().iterator();
					while (attrIt.hasNext()) {
						Attribute attribute = (Attribute) attrIt.next();
						if (relation.contains(oldName, attribute)) {
							relation.remove(oldName, attribute);
							relation.insert(inputValue, attribute);
						}
					}
					repaint();
					inputValue = "";
				} else {
					JOptionPane.showMessageDialog(
						this,
						"An object named '"
							+ inputValue
							+ "' already exist. Please enter a different name.",
						"Object exists",
						JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				break;
			}	
		}
		while (collectionContainsString(inputValue, objectList));
		repaint();
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
    
}