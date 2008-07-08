/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasDrawnEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class CanvasFeedbackMessage extends CanvasItem {
    private static final int X_MARGIN = 10;
    private static final int Y_MARGIN = 5;
    private final long endTime;
    private final long fadeInTime;
    private final long fadeOutTime;

    private final String message;
    private final Canvas canvas;
    private Rectangle2D bounds;
    private final Point2D position;

    private static final long HOLD_TIME = 3000;
    private static final long FADE_TIME = 300;

    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.PLAIN,
            20);
    private static final Color MESSAGE_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.WHITE;
    private static final Color LABEL_COLOR = Color.RED;

    private final CanvasCallbackHandler canvasCallbackHandler;

    private class CanvasCallbackHandler implements EventBrokerListener {
        public void processEvent(final Event e) {
            CanvasFeedbackMessage.this.canvas.repaint();
        }
    }

    public CanvasFeedbackMessage(final String message, final Canvas canvas,
            final Point2D pos) {
        this.message = message;
        this.canvas = canvas;

        this.fadeInTime = System.currentTimeMillis() + FADE_TIME;
        this.fadeOutTime = this.fadeInTime + HOLD_TIME;
        this.endTime = this.fadeOutTime + FADE_TIME;

        this.position = pos;

        this.canvas.addCanvasItem(this);
        this.canvasCallbackHandler = new CanvasCallbackHandler();
        this.canvas.getController().getEventBroker().subscribe(
                this.canvasCallbackHandler, CanvasDrawnEvent.class,
                Object.class);

        canvas.repaint();
    }

    @Override
    public void draw(final Graphics2D g) {
        final long time = System.currentTimeMillis();
        if (time > this.endTime) {
            cleanUp();
            return;
        }

        updateBounds(g);

        final Paint oldPaint = g.getPaint();
        final Font oldFont = g.getFont();

        double alpha = 1;
        if (time < this.fadeInTime) {
            alpha = 1 - (this.fadeInTime - time) / (double) FADE_TIME;
        } else if (time > this.fadeOutTime) {
            alpha = (this.endTime - time) / (double) FADE_TIME;
        }
        final Color textColor = createFadedColor(MESSAGE_COLOR, alpha);
        final Color borderColor = createFadedColor(BORDER_COLOR, alpha);
        final Color labelColor = createFadedColor(LABEL_COLOR, alpha);
        final Font font = getRescaledMessageFont(g);

        g.setPaint(labelColor);
        g.fill(this.bounds);
        g.setPaint(borderColor);
        g.draw(this.bounds);

        g.setPaint(textColor);
        g.setFont(font);
        g
                .drawString(
                        this.message,
                        (float) (this.bounds.getMinX() + getRescaledXMargin(g)),
                        (float) (this.bounds.getMinY() + g.getFontMetrics()
                                .getHeight()));

        g.setPaint(oldPaint);
        g.setFont(oldFont);
    }

    public Font getRescaledMessageFont(final Graphics2D g) {
        return MESSAGE_FONT.deriveFont((float) (MESSAGE_FONT.getSize2D() / g
                .getTransform().getScaleY()));
    }

    protected Color createFadedColor(final Color color, final double alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int) (alpha * 255));
    }

    private void cleanUp() {
        this.canvas.getController().getEventBroker().removeSubscriptions(
                this.canvasCallbackHandler);
        this.canvas.removeCanvasItem(this);
    }

    @Override
    public boolean containsPoint(final Point2D point) {
        return false;
    }

    @Override
    public Point2D getPosition() {
        if (this.bounds == null) {
            return this.position;
        }
        return new Point2D.Double(this.bounds.getCenterX(), this.bounds
                .getCenterY());
    }

    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D g) {
        return this.bounds;
    }

    private void updateBounds(final Graphics2D g) {
        final Font oldFont = g.getFont();
        g.setFont(getRescaledMessageFont(g));
        final double marginX = getRescaledXMargin(g);
        final double marginY = getRescaledYMargin(g);
        final double cx = this.position.getX();
        final double cy = this.position.getY();
        final double width = g.getFontMetrics().stringWidth(this.message);
        final double height = g.getFontMetrics().getHeight();
        this.bounds = new Rectangle2D.Double(cx - width / 2 - marginX, cy
                - height / 2 - marginY, width + 2 * marginX, height + 2
                * marginY);
        g.setFont(oldFont);
    }

    private double getRescaledXMargin(final Graphics2D g) {
        return X_MARGIN / g.getTransform().getScaleX();
    }

    private double getRescaledYMargin(final Graphics2D g) {
        return Y_MARGIN / g.getTransform().getScaleY();
    }
}
