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
import java.util.Collection;
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
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.ContextImplementation;

public class ContextTableColumnHeader extends JComponent implements Scrollable {
	private ContextTableEditorDialog dialog;
	private Attribute[] attributes;

	public ContextTableColumnHeader(ContextTableEditorDialog dialog) {
		super();
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
		Set attributesSet = this.dialog.getContext().getAttributes();
		this.attributes = (Attribute[]) attributesSet.toArray(new Attribute[attributesSet.size()]);
		int numCol = this.attributes.length + 1;
		return new Dimension(numCol * ContextTableView.CELL_WIDTH + 1, ContextTableView.CELL_HEIGHT + 1);
	}

	protected void drawColumnHeader(Graphics2D g2d) {
		g2d.setPaint(ContextTableView.TABLE_HEADER_COLOR);
		for (int i = 0; i < this.attributes.length; i++) {
			Attribute attribute = this.attributes[i];
			drawCell(g2d, attribute.toString(), i * ContextTableView.CELL_WIDTH, 0);			
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

	public String getToolTipText(MouseEvent e) {
		String tooltipText = null;

		ContextTableView.Position pos = this.dialog.getTablePosition(e.getX(), e.getY());

		if (pos != null) {
			if (pos.getCol() != this.attributes.length) {
				Attribute attr =  this.attributes[pos.getCol()];
				tooltipText = attr.toString();
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
    
	protected boolean collectionContainsString(
		String value,
		Collection collection) {
		Iterator it = collection.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj.toString().equalsIgnoreCase(value.trim())) {
				return true;
			}
		}
		return false;
	}
	
	private void removeAttribute(int pos) {
		Attribute attrToRemove = this.attributes[pos];
		this.dialog.getContext().getAttributes().remove(attrToRemove);
		this.dialog.updateView();
	}
	
	protected boolean addAttribute(String newAttributeName) {
		if (!this.dialog.collectionContainsString(newAttributeName, this.attributes)) {
			this.dialog.getContext().getAttributes().add(new Attribute(newAttributeName));
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
                    
                    JMenu sortMenu = new JMenu("Move before");
                    for (int i = 0; i < attributes.length; i++) {
                        final Attribute attribute = attributes[i];
                        JMenuItem menuItem = new JMenuItem(attribute.toString());
                        menuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                moveAttribute(pos.getCol(), attribute);
                            }
                        });
                        if(i == pos.getCol() || i == pos.getCol() + 1) {
                            menuItem.setEnabled(false);
                        }
                        sortMenu.add(menuItem);
                    }
                    popupMenu.add(sortMenu);
                    
                    if(pos.getCol() != attributes.length - 1) {
                        JMenuItem menuItem = new JMenuItem("Move to end");
                        menuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                moveAttribute(pos.getCol(), null);
                            }
                        });
                        popupMenu.add(menuItem);
                    }
                    
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
					popupMenu.show(dialog.getScrollPane(), e.getX() + getX() + ContextTableView.CELL_WIDTH, e.getY() + getY());
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
					renameAttribute(pos.getCol());
			}
		};
		return mouseListener;
	}
	
	private void renameAttribute(int num) {
		String inputValue = "";
		do {
			Attribute oldAttr = this.attributes[num];
			InputTextDialog dialog = new InputTextDialog(this.dialog, "Rename Attribute", "attribute", oldAttr.toString());
			if (!dialog.isCancelled()) {
                inputValue = dialog.getInput();
                if (!this.dialog.collectionContainsString(inputValue, this.attributes)) {
                    ContextImplementation context = this.dialog.getContext();
                    Set attributes = context.getAttributes();
                    Attribute newAttr = new Attribute(inputValue);
                    attributes.remove(oldAttr);
                    if(attributes instanceof List) {
                        List attributesList = (List) attributes;
                        attributesList.add(num, newAttr);
                    } else {
                        attributes.add(newAttr);
                    }
                    for (Iterator iter = context.getObjects().iterator(); iter.hasNext(); ) {
                        Object object = iter.next();
                        if(context.getRelation().contains(object,oldAttr)) {
                            context.getRelationImplementation().insert(object,newAttr);
                            context.getRelationImplementation().remove(object,oldAttr);
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
		} while (this.dialog.collectionContainsString(inputValue, this.attributes));
		repaint();
	}

    private void moveAttribute(int from, Attribute target) {
        ListSet attributeList = this.dialog.getContext().getAttributeList();
        if(target != null) {
            int targetPos = attributeList.indexOf(target);
            if(from < targetPos) {
                targetPos --;
            }
            Object movingAttribute = attributeList.remove(from);
            attributeList.add(targetPos, movingAttribute);
        } else {
            Object movingAttribute = attributeList.remove(from);
            attributeList.add(movingAttribute);
        }
        calculateNewSize();
        this.dialog.repaint();
    }    
}