/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.manyvaluedcontext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContext;

public class ColumnHeader extends JComponent{
	
	private static final Color TEXT_COLOR = Color.BLACK;
	private static final Color TABLE_HEADER_COLOR = Color.LIGHT_GRAY;
	
	private ManyValuedContext context;
	
	
	public ColumnHeader(ManyValuedContext context){
		this.context = context;
		ToolTipManager.sharedInstance().registerComponent(this);
		updateSize();
	}
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		Font oldFont = g2d.getFont();
		int col = 0;
		Iterator attrIt = this.context.getAttributes().iterator();
		while(attrIt.hasNext()){
			ManyValuedAttribute attribute = (ManyValuedAttribute) attrIt.next();	
			drawAttributeNameCell(g2d,attribute.getName(),col * TableView.CELL_WIDTH,0);	
			col+=1;
		}
		g2d.setPaint(oldPaint);
		g2d.setFont(oldFont);	
	}
	
	protected void drawAttributeNameCell(Graphics2D g2d, String content, int x, int y) {
		g2d.setPaint(TABLE_HEADER_COLOR);
		g2d.fill(new Rectangle2D.Double(x, y, TableView.CELL_WIDTH, TableView.CELL_HEIGHT));
		g2d.setPaint(TEXT_COLOR);
		g2d.draw(new Rectangle2D.Double(x, y, TableView.CELL_WIDTH, TableView.CELL_HEIGHT));
	
		FontMetrics fontMetrics = g2d.getFontMetrics();
		String newContent = reduceStringDisplayWidth(content, g2d);
	
		g2d.drawString(
			newContent,
			x + TableView.CELL_WIDTH / 2 - fontMetrics.stringWidth(newContent) / 2,
			y + TableView.CELL_HEIGHT / 2 + fontMetrics.getMaxAscent() / 2);
	}
	
	protected String reduceStringDisplayWidth(String content, Graphics2D g2d) {
		String newContent = content;
		String tail = "...";
		int stringWidth = g2d.getFontMetrics().stringWidth(newContent);
		int tailWidth = g2d.getFontMetrics().stringWidth(tail);
		if (stringWidth > (TableView.CELL_WIDTH - 10)) {
			while ((stringWidth + tailWidth) > (TableView.CELL_WIDTH - 10)) {
				newContent = newContent.substring(0, (newContent.length() - 1));
				stringWidth = g2d.getFontMetrics().stringWidth(newContent);
			}
			newContent += tail;
		}
		return newContent;
	}
	
	public void updateSize() {
		this.setPreferredSize(calculateNewSize());
	}
	
	private Dimension calculateNewSize() {
		int numCol = context.getAttributes().size() + 1;
		return new Dimension(numCol * TableView.CELL_WIDTH + 1, TableView.CELL_HEIGHT);
	}
	
	public String getToolTipText(MouseEvent e) {
		
		int col = e.getX()/TableView.CELL_WIDTH;
		
		ArrayList attributeList = (ArrayList) context.getAttributes();
		ManyValuedAttribute attribute = (ManyValuedAttribute) attributeList.get(col);
		String tooltipText = attribute.getType().toString();
	
		return tooltipText;
	}

	

	public void setManyValuedContext(ManyValuedContext context) {
		this.context = context;
		validate();
	}
}
