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

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;

import org.tockit.util.ListSet;

public class ContextTableColumnHeader extends JComponent implements Scrollable {
    private final ContextTableEditorDialog dialog;
    private WritableFCAElement[] attributes;

    public ContextTableColumnHeader(final ContextTableEditorDialog dialog) {
        super();
        this.dialog = dialog;
        addMouseListener(createMouseListener());
        setVisible(true);
        ToolTipManager.sharedInstance().registerComponent(this);
        updateSize();
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        final Paint oldPaint = g2d.getPaint();
        final Font oldFont = g2d.getFont();

        g2d.setFont(oldFont.deriveFont(Font.BOLD));
        drawColumnHeader(g2d);

        g2d.setPaint(oldPaint);
        g2d.setFont(oldFont);
    }

    public Dimension calculateNewSize() {
        final Set<Object> attributesSet = this.dialog.getContext()
                .getAttributes();
        this.attributes = attributesSet
                .toArray(new WritableFCAElement[attributesSet.size()]);
        final int numCol = this.attributes.length + 1;
        return new Dimension(numCol * ContextTableView.CELL_WIDTH + 1,
                ContextTableView.CELL_HEIGHT + 1);
    }

    protected void drawColumnHeader(final Graphics2D g2d) {
        g2d.setPaint(ContextTableView.TABLE_HEADER_COLOR);
        for (int i = 0; i < this.attributes.length; i++) {
            final WritableFCAElement attribute = this.attributes[i];
            drawCell(g2d, attribute.toString(),
                    i * ContextTableView.CELL_WIDTH, 0);
        }
    }

    protected void drawCell(final Graphics2D g2d, final String content,
            final int x, final int y) {
        g2d.fill(new Rectangle2D.Double(x, y, ContextTableView.CELL_WIDTH,
                ContextTableView.CELL_HEIGHT));
        final Paint oldPaint = g2d.getPaint();
        g2d.setPaint(ContextTableView.TEXT_COLOR);
        g2d.draw(new Rectangle2D.Double(x, y, ContextTableView.CELL_WIDTH,
                ContextTableView.CELL_HEIGHT));

        final FontMetrics fontMetrics = g2d.getFontMetrics();
        final String newContent = reduceStringDisplayWidth(content, g2d);

        g2d.drawString(newContent, x + ContextTableView.CELL_WIDTH / 2
                - fontMetrics.stringWidth(newContent) / 2, y
                + ContextTableView.CELL_HEIGHT / 2 + fontMetrics.getMaxAscent()
                / 2);
        g2d.setPaint(oldPaint);
    }

    protected String reduceStringDisplayWidth(final String content,
            final Graphics2D g2d) {
        String newContent = content;
        final String tail = "...";
        int stringWidth = g2d.getFontMetrics().stringWidth(newContent);
        final int tailWidth = g2d.getFontMetrics().stringWidth(tail);
        if (stringWidth > (ContextTableView.CELL_WIDTH - 10)) {
            while ((stringWidth + tailWidth) > (ContextTableView.CELL_WIDTH - 10)) {
                newContent = newContent.substring(0, (newContent.length() - 1));
                stringWidth = g2d.getFontMetrics().stringWidth(newContent);
            }
            newContent += tail;
        }
        return newContent;
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        String tooltipText = null;

        final ContextTableView.Position pos = this.dialog.getTablePosition(e
                .getX(), e.getY());

        if (pos != null) {
            if (pos.getCol() != this.attributes.length) {
                final WritableFCAElement attr = this.attributes[pos.getCol()];
                tooltipText = attr.toString();
            }
        }
        return tooltipText;
    }

    public Dimension getPreferredScrollableViewportSize() {
        return ContextTableView.TABLE_HEADER_PREFERRED_VIEWPORT_SIZE;
    }

    public int getScrollableUnitIncrement(final Rectangle visibleRect,
            final int orientation, final int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return ContextTableView.CELL_WIDTH;
        } else {
            return ContextTableView.CELL_HEIGHT;
        }
    }

    public int getScrollableBlockIncrement(final Rectangle visibleRect,
            final int orientation, final int direction) {
        // round the size in any direction down to a multiple of the cell size
        if (orientation == SwingConstants.HORIZONTAL) {
            return (visibleRect.width / ContextTableView.CELL_WIDTH)
                    * ContextTableView.CELL_WIDTH;
        } else {
            return (visibleRect.height / ContextTableView.CELL_HEIGHT)
                    * ContextTableView.CELL_HEIGHT;
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
        invalidate();
        repaint();
    }

    protected boolean collectionContainsString(final String value,
            final Collection<Object> collection) {
        final Iterator<Object> it = collection.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            if (obj.toString().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    private void removeAttribute(final int pos) {
        final WritableFCAElement attrToRemove = this.attributes[pos];
        this.dialog.getContext().getAttributes().remove(attrToRemove);
        this.dialog.updateView();
    }

    protected boolean addAttribute(final String newAttributeName) {
        if (!this.dialog.collectionContainsString(newAttributeName,
                this.attributes)) {
            this.dialog.getContext().getAttributes().add(
                    new FCAElementImplementation(newAttributeName));
            return true;
        } else {
            return false;
        }
    }

    private MouseListener createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final ContextTableView.Position pos = dialog
                            .getTablePosition(e.getX(), e.getY());
                    if (pos == null) {
                        return;
                    } else {
                        showPopupMenu(e, pos);
                    }
                }
            }

            public void showPopupMenu(final MouseEvent e,
                    final ContextTableView.Position pos) {
                final JPopupMenu popupMenu = new JPopupMenu();

                final JMenu sortMenu = new JMenu("Move before");
                for (int i = 0; i < attributes.length; i++) {
                    final WritableFCAElement attribute = attributes[i];
                    final JMenuItem menuItem = new JMenuItem(attribute
                            .toString());
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent ae) {
                            moveAttribute(pos.getCol(), attribute);
                        }
                    });
                    if (i == pos.getCol() || i == pos.getCol() + 1) {
                        menuItem.setEnabled(false);
                    }
                    sortMenu.add(menuItem);
                }
                popupMenu.add(sortMenu);

                if (pos.getCol() != attributes.length - 1) {
                    final JMenuItem menuItem = new JMenuItem("Move to end");
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent ae) {
                            moveAttribute(pos.getCol(), null);
                        }
                    });
                    popupMenu.add(menuItem);
                }

                final JMenuItem rename = new JMenuItem("Rename Attribute");
                rename.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent ae) {
                        renameAttribute(pos.getCol());
                    }
                });
                final JMenuItem remove = new JMenuItem("Remove Attribute");
                remove.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent ae) {
                        removeAttribute(pos.getCol());
                    }
                });
                popupMenu.add(rename);
                popupMenu.add(remove);
                popupMenu.show(dialog.getScrollPane(), e.getX() + getX()
                        + ContextTableView.CELL_WIDTH, e.getY() + getY());
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final ContextTableView.Position pos = dialog
                            .getTablePosition(e.getX(), e.getY());
                    if (pos == null) {
                        return;
                    } else {
                        showPopupMenu(e, pos);
                    }
                }
            }

            @Override
            public void mouseClicked(final MouseEvent e) {
                final ContextTableView.Position pos = dialog.getTablePosition(e
                        .getX(), e.getY());
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
    }

    private void renameAttribute(final int num) {
        String inputValue = "";
        do {
            final WritableFCAElement oldAttr = this.attributes[num];
            final InputTextDialog inputDialog = new InputTextDialog(
                    this.dialog, "Rename Attribute", "attribute", oldAttr
                            .toString());
            if (!inputDialog.isCancelled()) {
                inputValue = inputDialog.getInput();
                if (!this.dialog.collectionContainsString(inputValue,
                        this.attributes)) {
                    final ContextImplementation context = this.dialog
                            .getContext();
                    final Set<Object> attributeSet = context.getAttributes();
                    final WritableFCAElement newAttr = new FCAElementImplementation(
                            inputValue);
                    attributeSet.remove(oldAttr);
                    if (attributeSet instanceof List) {
                        final List<Object> attributesList = (List<Object>) attributeSet;
                        attributesList.add(num, newAttr);
                    } else {
                        attributeSet.add(newAttr);
                    }
                    for (final Iterator<Object> iter = context.getObjects()
                            .iterator(); iter.hasNext();) {
                        final Object object = iter.next();
                        if (context.getRelation().contains(object, oldAttr)) {
                            context.getRelationImplementation().insert(object,
                                    newAttr);
                            context.getRelationImplementation().remove(object,
                                    oldAttr);
                        }
                    }
                    calculateNewSize();
                    repaint();
                    inputValue = "";
                } else {
                    JOptionPane
                            .showMessageDialog(
                                    this,
                                    "An object named '"
                                            + inputValue
                                            + "' already exist. Please enter a different name.",
                                    "Object exists", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        } while (this.dialog.collectionContainsString(inputValue,
                this.attributes));
        repaint();
    }

    private void moveAttribute(final int from, final WritableFCAElement target) {
        final ListSet attributeList = this.dialog.getContext()
                .getAttributeList();
        if (target != null) {
            int targetPos = attributeList.indexOf(target);
            if (from < targetPos) {
                targetPos--;
            }
            final Object movingAttribute = attributeList.remove(from);
            attributeList.add(targetPos, movingAttribute);
        } else {
            final Object movingAttribute = attributeList.remove(from);
            attributeList.add(movingAttribute);
        }
        calculateNewSize();
        this.dialog.repaint();
    }
}