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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import org.tockit.util.ListSet;

import net.sourceforge.toscanaj.gui.dialog.*;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;

public class ContextTableRowHeader extends JComponent implements Scrollable {
	private ContextTableEditorDialog dialog;
    /**
     * @todo remove and use object list from context instead.
     */
	private WritableFCAElement[] objects;
	
	public ContextTableRowHeader (ContextTableEditorDialog dialog) {
		super();
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
		for (int i = 0; i < this.objects.length; i++) {
			FCAElement object = this.objects[i];
			drawRow(g2d, object, i);
		}

		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);
	}

	public Dimension calculateNewSize() {
		Set objectsSet = this.dialog.getContext().getObjects();
		
		this.objects = (WritableFCAElement[]) objectsSet.toArray(new WritableFCAElement[objectsSet.size()]);
		int numRow = this.objects.length + 1;
		return new Dimension(ContextTableView.CELL_WIDTH + 1, numRow * ContextTableView.CELL_HEIGHT + 1);
	}

	protected void drawRow(Graphics2D g2d, Object object, int row) {
		Font font = g2d.getFont();
		g2d.setFont(font.deriveFont(Font.BOLD));
		g2d.setPaint(ContextTableView.TABLE_HEADER_COLOR);
		int y = row * ContextTableView.CELL_HEIGHT;
		drawCell(g2d, object.toString(), 0, y);
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

		
	public String getToolTipText(MouseEvent e) {
		String tooltipText = null;
		
		ContextTableView.Position pos = dialog.getTablePosition(e.getX(), e.getY());

		if (pos != null) {
			if (pos.getCol() == 0 && pos.getRow() != this.objects.length) {
				tooltipText = this.objects[pos.getRow()].toString();
			}
		}
		return tooltipText;
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
    
	private MouseListener createMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					final ContextTableView.Position pos =
						dialog.getTablePosition(e.getX(), e.getY());
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
                    JMenu sortMenu = new JMenu("Move before");
                    for (int i = 0; i < objects.length; i++) {
                        final WritableFCAElement object = objects[i];
                        JMenuItem menuItem = new JMenuItem(object.toString());
                        menuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                moveObject(pos.getRow(), object);
                            }
                        });
                        if(i == pos.getRow() || i == pos.getRow() + 1) {
                            menuItem.setEnabled(false);
                        }
                        sortMenu.add(menuItem);
                    }
                    popupMenu.add(sortMenu);
                    
                    if(pos.getRow() != objects.length - 1) {
                        JMenuItem menuItem = new JMenuItem("Move to end");
                        menuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                moveObject(pos.getRow(), null);
                            }
                        });
                        popupMenu.add(menuItem);
                    }
                    
                    popupMenu.add(rename);
					popupMenu.add(remove);
                    popupMenu.show(dialog.getScrollPane(), e.getX() + getX(), e.getY() + getY() + ContextTableView.CELL_HEIGHT);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					final ContextTableView.Position pos =
						dialog.getTablePosition(e.getX(), e.getY());
					if (pos == null) {
						return;
					} else {
						showPopupMenu(e, pos);
					}
				}
			}

			public void mouseClicked(MouseEvent e) {
				final ContextTableView.Position pos =
					dialog.getTablePosition(e.getX(), e.getY());
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
		Object objToRemove = this.objects[pos];
		this.dialog.getContext().getObjects().remove(objToRemove);
		this.dialog.updateView();
	}
	
	protected boolean addObject(String newObjectName){
		if (!collectionContainsString(newObjectName, this.objects)) {
			WritableFCAElement object = new FCAElementImplementation(newObjectName);
			this.dialog.getContext().getObjects().add(object);
			return true;
		} else {
			return false;
		}		
	}
	
	private void renameObject(int num) {
		String inputValue = "";
		do {
			WritableFCAElement oldObject = this.objects[num];
			InputTextDialog dialog = new InputTextDialog(this.dialog, "Rename Object", "object", oldObject.toString());
			if (!dialog.isCancelled()) {
				inputValue = dialog.getInput();
				if (!collectionContainsString(inputValue, this.objects)) {
				    ContextImplementation context = this.dialog.getContext();
                    Set objects = context.getObjects();
                    WritableFCAElement newObject = new FCAElementImplementation(inputValue);
                    objects.remove(oldObject);
                    if(objects instanceof List) {
                        List objectList = (List) objects;
                        objectList.add(num, newObject);
                    } else {
                        objects.add(newObject);
                    }
                    for (Iterator iter = context.getAttributes().iterator(); iter.hasNext(); ) {
                        Object attribute = iter.next();
                        if(context.getRelation().contains(oldObject, attribute)) {
                            context.getRelationImplementation().insert(newObject, attribute);
                            context.getRelationImplementation().remove(oldObject, attribute);
                        }
                    }
                    calculateNewSize();
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
		while (collectionContainsString(inputValue, this.objects));
		repaint();
	}
	

    private void moveObject(int from, WritableFCAElement target) {
        ListSet objectList = this.dialog.getContext().getObjectList();
        if(target != null) {
            int targetPos = objectList.indexOf(target);
            if(from < targetPos) {
                targetPos --;
            }
            Object movingObject = objectList.remove(from);
            objectList.add(targetPos, movingObject);
        } else {
            Object movingObject = objectList.remove(from);
            objectList.add(movingObject);
        }
        calculateNewSize();
        this.dialog.repaint();
    }
    
	protected boolean collectionContainsString(
		String value,
		Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			Object obj = objects[i];
			if (obj.toString().equalsIgnoreCase(value.trim())) {
				return true;
			}
		}
		return false;
	}
    
}