/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.temporal;

import net.sourceforge.toscanaj.controller.diagram.AnimationTimeController;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates all generic label drawing code.
 * <p/>
 * The actual classes to use are the AttributeLabelView and the ObjectLabelView
 * which are distinguished by position (above vs. below the node) and default
 * display type (list vs. number).
 */
// derived from view.diagram.LabelView, could probably do with some refactoring to encapsulate the similarities
public class ArrowLabelView extends CanvasItem implements ChangeObserver {
    private float textmargin;

    /**
     * Stores the font we use.
     */
    private Font font;

    /**
     * The bounding rectangle for the label itself.
     */
    protected Rectangle2D rect = null;

    /**
     * Store the diagram view that the label belongs to.
     */
    protected final DiagramView diagramView;

    protected final TransitionArrow arrow;

    protected final ArrowStyle style;

    private final String text;
    private final double timePos;
    private final AnimationTimeController timeController;

    /**
     * The height of the line in the view.
     */
    protected float lineHeight = 0;

    protected List<ChangeObserver> observers = new ArrayList<>();

    public ArrowLabelView(DiagramView diagramView, TransitionArrow arrow, ArrowStyle style, String text,
                          double timePos, AnimationTimeController timeController) {
        this.diagramView = diagramView;
        this.arrow = arrow;
        this.style = style;
        this.text = text;
        this.timePos = timePos;
        this.timeController = timeController;
        final DiagramSchema diagramSchema = diagramView.getDiagramSchema();
        this.font = diagramSchema.getLabelFont();
        update(this);
    }

    /**
     * Update label view as label info has change.
     */
    public void update(final Object source) {
        // we expect someone to cause us to redraw if needed, we don't do it ourself
        // maybe we are off-screen or hidden or whatever ...
        notifyObservers();
    }

    public double getLabelWidth() {
        return this.rect.getWidth();
    }

    public double getLabelHeight() {
        return this.rect.getHeight();
    }

    @Override
    public Point2D getPosition() {
        if (this.rect != null) {
            return new Point2D.Double(this.rect.getX(), this.rect.getY());
        } else {
            return this.arrow.getPosition();
        }
    }

    /**
     * Draws the label at the given position in the graphic context.
     * <p/>
     * The position is placed above or below the label, horizontally centered
     * plus the offset from the LabelInfo.
     * <p/>
     * The placement should be either LabelView::ABOVE or LabelView::BELOW. A
     * dashed line will be drawn from the central top/bottom point to the given
     * point.
     * <p/>
     * The scaling information is needed to scale the offset.
     */
    @Override
    public void draw(final Graphics2D graphics) {
        // remember some settings to restore them later
        final Paint oldPaint = graphics.getPaint();
        final Font oldFont = graphics.getFont();

        graphics.setFont(this.font);

        // find the size and position
        updateBounds(graphics);
        final double xPos = rect.getX();
        final double yPos = rect.getY();
        final double lw = rect.getWidth();

        Paint backgroundColor = calculatePaint(Color.WHITE);
        if (backgroundColor == null) { // nothing to draw
            return;
        }
        Paint textColor = calculatePaint(Color.BLACK);

        // draw the label itself
        graphics.setPaint(backgroundColor);
        graphics.fill(rect);
        graphics.setPaint(textColor);

        final float textWidth = (float) lw;

        TextLayout layout = new TextLayout(text, font, graphics.getFontRenderContext());
        float width = layout.getAdvance() + 2 * this.textmargin;
        float textX = (float) xPos + (textWidth - width) / 2 + textmargin;
        float textY = (float) yPos + layout.getAscent() + (this.lineHeight - getFullHeight(layout)) / 2;
        layout.draw(graphics, textX, textY);

        // draw the frame for the label
        graphics.setPaint(textColor);
        graphics.draw(rect);

        // restore old settings
        graphics.setPaint(oldPaint);
        graphics.setFont(oldFont);
    }

    public void updateBounds(final Graphics2D graphics) {
        FontRenderContext fontRenderContext = graphics.getFontRenderContext();

        // margin is something dependent on the font, but the detailed choice is a bit arbitrary
        TextLayout mLayout = new TextLayout("M", this.font, fontRenderContext);
        this.textmargin = mLayout.getLeading() + mLayout.getDescent();

        // find the size and position
        double x = arrow.getPosition().getX();
        double y = arrow.getPosition().getY();
        this.lineHeight = 0;
        double lw = 0;

        // find maximum width and height of string content
        TextLayout layout = new TextLayout(text, this.font, fontRenderContext);
        double w = layout.getAdvance();
        if (w > lw) {
            lw = w;
        }
        float h = getFullHeight(layout);
        if (h > this.lineHeight) {
            this.lineHeight = h;
        }
        lw += 2 * textmargin;

        double lh = this.lineHeight;
        this.rect = new Rectangle2D.Double(x, y, lw, lh);
    }

    private float getFullHeight(final TextLayout layout) {
        return layout.getLeading() + layout.getAscent() + layout.getDescent();
    }

    Point2D getConnectorStartPosition() {
        double x = arrow.getPosition().getX();
        double y = arrow.getPosition().getY();
        return new Point2D.Double(x, y);
    }

    protected Point2D getConnectorEndPosition() {
        final double rectX = this.rect.getX();
        final double y = getConnectorStartPosition().getY();
        final double lw = rect.getWidth();
        if (rectX > arrow.getPosition().getX()) {
            return new Point2D.Double(rectX, y);
        } else if (rectX + lw < arrow.getPosition().getX()) {
            return new Point2D.Double(rectX + lw, y);
        } else {
            return new Point2D.Double(rectX + lw / 2, y);
        }
    }

    protected Color getConnectorColor() {
        return Color.DARK_GRAY;
    }

    /**
     * Returns true whenever the point is in the bounding rectangle.
     */
    @Override
    public boolean containsPoint(final Point2D point) {
        return this.rect.contains(point);
    }

    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D graphics) {
        if (this.rect == null) {
            return null;
        }
        final Stroke stroke = graphics.getStroke();
        updateBounds(graphics);
        if (stroke instanceof BasicStroke) {
            final double w = ((BasicStroke) stroke).getLineWidth();
            return new Rectangle2D.Double(this.rect.getX() - w / 2, this.rect
                    .getY()
                    - w / 2, this.rect.getWidth() + w, this.rect.getHeight()
                    + w);
        }
        return new Rectangle2D.Double(this.rect.getX(), this.rect.getY(),
                this.rect.getWidth(), this.rect.getHeight());
    }

    public void addObserver(final ChangeObserver observer) {
        this.observers.add(observer);
    }

    protected void notifyObservers() {
        for (final ChangeObserver changeObserver : observers) {
            changeObserver.update(this);
        }
    }

    public Font getFont() {
        return font;
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    private Paint calculatePaint(final Color baseColor) {
        final AnimationTimeController controller = this.timeController;

        final double timeOffset = controller.getCurrentTime() - this.timePos;
        double alpha;
        if (timeOffset < -controller.getFadeInTime()) {
            return null;
        } else if (timeOffset < 0) {
            alpha = 1 + timeOffset / controller.getFadeInTime();
        } else if (timeOffset < controller.getVisibleTime()) {
            alpha = 1;
        } else if (timeOffset < controller.getVisibleTime()
                + controller.getFadeOutTime()) {
            alpha = 1 - (timeOffset - controller.getVisibleTime())
                    / controller.getFadeOutTime();
        } else {
            return null;
        }

        return new Color(baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
                (int) (alpha * baseColor.getAlpha())
        );
    }
}
