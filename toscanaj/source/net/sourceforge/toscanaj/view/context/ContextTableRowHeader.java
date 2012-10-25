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

import net.sourceforge.toscanaj.gui.dialog.InputTextDialog;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;

import org.tockit.util.ListSet;

public class ContextTableRowHeader extends JComponent implements Scrollable {
    private final ContextTableEditorDialog dialog;
    /**
     * @todo remove and use object list from context instead.
     */
    private FCAElementImplementation[] objects;

    public ContextTableRowHeader(final ContextTableEditorDialog dialog) {
        super();
        this.dialog = dialog;
        setVisible(true);
        addMouseListener(createMouseListener());
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
        for (int i = 0; i < this.objects.length; i++) {
            final FCAElement object = this.objects[i];
            drawRow(g2d, object, i);
        }

        g2d.setPaint(oldPaint);
        g2d.setFont(oldFont);
    }

    public Dimension calculateNewSize() {
        final Set<FCAElementImplementation> objectsSet = this.dialog.getContext().getObjects();

        this.objects = objectsSet.toArray(new FCAElementImplementation[objectsSet.size()]);
        final int numRow = this.objects.length + 1;
        return new Dimension(ContextTableView.CELL_WIDTH + 1, numRow
                * ContextTableView.CELL_HEIGHT + 1);
    }

    protected void drawRow(final Graphics2D g2d, final Object object,
            final int row) {
        final Font font = g2d.getFont();
        g2d.setFont(font.deriveFont(Font.BOLD));
        g2d.setPaint(ContextTableView.TABLE_HEADER_COLOR);
        final int y = row * ContextTableView.CELL_HEIGHT;
        drawCell(g2d, object.toString(), 0, y);
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

        final ContextTableView.Position pos = dialog.getTablePosition(e.getX(),
                e.getY());

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

    private MouseListener createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final ContextTableView.Position pos = dialog
                            .getTablePosition(e.getX(), e.getY());
                    if (pos != null) {
                        showPopupMenu(e, pos);
                    }
                }
            }

            public void showPopupMenu(final MouseEvent e,
                    final ContextTableView.Position pos) {
                final JPopupMenu popupMenu = new JPopupMenu();
                final JMenuItem rename = new JMenuItem("Rename Object");
                rename.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent ae) {
                        renameObject(pos.getRow());
                    }
                });
                final JMenuItem remove = new JMenuItem("Remove Object");
                remove.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent ae) {
                        removeObject(pos.getRow());
                    }
                });
                final JMenu sortMenu = new JMenu("Move before");
                for (int i = 0; i < objects.length; i++) {
                    final WritableFCAElement object = objects[i];
                    final JMenuItem menuItem = new JMenuItem(object.toString());
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent ae) {
                            moveObject(pos.getRow(), object);
                        }
                    });
                    if (i == pos.getRow() || i == pos.getRow() + 1) {
                        menuItem.setEnabled(false);
                    }
                    sortMenu.add(menuItem);
                }
                popupMenu.add(sortMenu);

                if (pos.getRow() != objects.length - 1) {
                    final JMenuItem menuItem = new JMenuItem("Move to end");
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent ae) {
                            moveObject(pos.getRow(), null);
                        }
                    });
                    popupMenu.add(menuItem);
                }

                popupMenu.add(rename);
                popupMenu.add(remove);
                popupMenu.show(dialog.getScrollPane(), e.getX() + getX(), e
                        .getY()
                        + getY() + ContextTableView.CELL_HEIGHT);
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final ContextTableView.Position pos = dialog
                            .getTablePosition(e.getX(), e.getY());
                    if (pos != null) {
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

                if (pos.getCol() == 0) {
                    renameObject(pos.getRow());
                }
            }
        };
    }

    private void removeObject(final int pos) {
        final FCAElementImplementation objToRemove = this.objects[pos];
        this.dialog.getContext().getObjects().remove(objToRemove);
        this.dialog.updateView();
    }

    protected boolean addObject(final String newObjectName) {
        if (!collectionContainsString(newObjectName, this.objects)) {
            final FCAElementImplementation object = new FCAElementImplementation(newObjectName);
            this.dialog.getContext().getObjects().add(object);
            return true;
        } else {
            return false;
        }
    }

    private void renameObject(final int num) {
        String inputValue;
        do {
            final FCAElementImplementation oldObject = this.objects[num];
            final InputTextDialog inputDialog = new InputTextDialog(
                    this.dialog, "Rename Object", "object", oldObject
                            .toString());
            if (!inputDialog.isCancelled()) {
                inputValue = inputDialog.getInput();
                if (!collectionContainsString(inputValue, this.objects)) {
                    final ContextImplementation context = this.dialog
                            .getContext();
                    final Set<FCAElementImplementation> objectSet = context.getObjects();
                    final FCAElementImplementation newObject = new FCAElementImplementation(inputValue);
                    objectSet.remove(oldObject);
                    if (objectSet instanceof List) {
                        final List<FCAElementImplementation> objectList = (List<FCAElementImplementation>) objectSet;
                        objectList.add(num, newObject);
                    } else {
                        objectSet.add(newObject);
                    }
                    for (final Iterator<FCAElementImplementation> iter = context.getAttributes().iterator();
                         iter.hasNext();) {
                        final FCAElementImplementation attribute = iter.next();
                        if (context.getRelation()
                                .contains(oldObject, attribute)) {
                            context.getRelationImplementation().insert(
                                    newObject, attribute);
                            context.getRelationImplementation().remove(
                                    oldObject, attribute);
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
        } while (collectionContainsString(inputValue, this.objects));
        repaint();
    }

    private void moveObject(final int from, final WritableFCAElement target) {
        final ListSet objectList = this.dialog.getContext().getObjectList();
        if (target != null) {
            int targetPos = objectList.indexOf(target);
            if (from < targetPos) {
                targetPos--;
            }
            final Object movingObject = objectList.remove(from);
            objectList.add(targetPos, movingObject);
        } else {
            final Object movingObject = objectList.remove(from);
            objectList.add(movingObject);
        }
        calculateNewSize();
        this.dialog.repaint();
    }

    protected boolean collectionContainsString(final String value,
            final Object[] values) {
        for (final Object obj : values) {
            if (obj.toString().equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

}