/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.view.colorchange;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Logger;


/**
 * @todo check if there is a shorter way to do the same by supplying a ColorSpace
 * @todo check if we handle other paints than Color somehow. At the moment we do
 *       GradientPaint, but that won't work properly with the B&W versions -- the
 *       result will still be grayscale
 */
abstract class AbstractColorChanger implements ColorChanger {
    public Graphics2D getGraphics2D(Graphics2D original) {
        return new Graphics2DWrapper(original);
    }
    
    private class Graphics2DWrapper extends Graphics2D {
        private final Graphics2D g2d;
        public Graphics2DWrapper(Graphics2D g2d) {
            this.g2d = g2d;
        }
        public void setColor(Color c) {
            g2d.setColor(changeColor(c));
        }
        public void setPaint(final Paint paint) {
            if(paint instanceof Color) {
                setColor((Color) paint);
            } else if(paint instanceof GradientPaint){
                GradientPaint gp = (GradientPaint) paint;
                g2d.setPaint(new GradientPaint(gp.getPoint1(), changeColor(gp.getColor1()), 
                                               gp.getPoint2(), changeColor(gp.getColor2()), 
                                               gp.isCyclic()));
            } else {
                g2d.setPaint(paint);
                Logger.getLogger(this.getClass().getName()).warning("Unknown paint type in ColorChanger implementation");
            }
        }
        // rest is just delegation
        public void rotate(double theta) {
            g2d.rotate(theta);
        }
        public void scale(double sx, double sy) {
            g2d.scale(sx, sy);
        }
        public void shear(double shx, double shy) {
            g2d.shear(shx, shy);
        }
        public void translate(double tx, double ty) {
            g2d.translate(tx, ty);
        }
        public void rotate(double theta, double x, double y) {
            g2d.rotate(theta, x, y);
        }
        public void translate(int x, int y) {
            g2d.translate(x, y);
        }
        public Color getBackground() {
            return g2d.getBackground();
        }
        public void setBackground(Color color) {
            g2d.setBackground(color);
        }
        public Composite getComposite() {
            return g2d.getComposite();
        }
        public void setComposite(Composite comp) {
            g2d.setComposite(comp);
        }
        public GraphicsConfiguration getDeviceConfiguration() {
            return g2d.getDeviceConfiguration();
        }
        public Paint getPaint() {
            return g2d.getPaint();
        }
        public RenderingHints getRenderingHints() {
            return g2d.getRenderingHints();
        }
        public void clip(Shape s) {
            g2d.clip(s);
        }
        public void draw(Shape s) {
            g2d.draw(s);
        }
        public void fill(Shape s) {
            g2d.fill(s);
        }
        public Stroke getStroke() {
            return g2d.getStroke();
        }
        public void setStroke(Stroke s) {
            g2d.setStroke(s);
        }
        public FontRenderContext getFontRenderContext() {
            return g2d.getFontRenderContext();
        }
        public void drawGlyphVector(GlyphVector g, float x, float y) {
            g2d.drawGlyphVector(g, x, y);
        }
        public AffineTransform getTransform() {
            return g2d.getTransform();
        }
        public void setTransform(AffineTransform Tx) {
            g2d.setTransform(Tx);
        }
        public void transform(AffineTransform Tx) {
            g2d.transform(Tx);
        }
        public void drawString(String s, float x, float y) {
            g2d.drawString(s, x, y);
        }
        public void drawString(String str, int x, int y) {
            g2d.drawString(str, x, y);
        }
        public void drawString(AttributedCharacterIterator iterator, float x, float y) {
            g2d.drawString(iterator, x, y);
        }
        public void drawString(AttributedCharacterIterator iterator, int x, int y) {
            g2d.drawString(iterator, x, y);
        }
        public void addRenderingHints(Map hints) {
            g2d.addRenderingHints(hints);
        }
        public void setRenderingHints(Map hints) {
            g2d.setRenderingHints(hints);
        }
        public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
            return g2d.hit(rect, s, onStroke);
        }
        public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
            g2d.drawRenderedImage(img, xform);
        }
        public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
            g2d.drawRenderableImage(img, xform);
        }
        public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
            g2d.drawImage(img, op, x, y);
        }
        public Object getRenderingHint(Key hintKey) {
            return getRenderingHint(hintKey);
        }
        public void setRenderingHint(Key hintKey, Object hintValue) {
            g2d.setRenderingHint(hintKey, hintValue);
        }
        public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
            return g2d.drawImage(img, xform, obs);
        }
        public void dispose() {
            g2d.dispose();
        }
        public void setPaintMode() {
            g2d.setPaintMode();
        }
        public void clearRect(int x, int y, int width, int height) {
            g2d.clearRect(x, y, width, height);
        }
        public void clipRect(int x, int y, int width, int height) {
            g2d.clipRect(x, y, width, height);
        }
        public void drawLine(int x1, int y1, int x2, int y2) {
            g2d.drawLine(x1, y1, x2, y2);
        }
        public void drawOval(int x, int y, int width, int height) {
            g2d.drawOval(x, y, width, height);
        }
        public void fillOval(int x, int y, int width, int height) {
            g2d.fillOval(x, y, width, height);
        }
        public void fillRect(int x, int y, int width, int height) {
            g2d.fillRect(x, y, width, height);
        }
        public void setClip(int x, int y, int width, int height) {
            g2d.setClip(x, y, width, height);
        }
        public void copyArea(int x, int y, int width, int height, int dx, int dy) {
            g2d.copyArea(x, y, width, height, dx, dy);
        }
        public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
            g2d.drawArc(x, y, width, height, startAngle, arcAngle);
        }
        public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
            g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        }
        public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
            g2d.fillArc(x, y, width, height, startAngle, arcAngle);
        }
        public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
            g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        }
        public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            g2d.drawPolygon(xPoints, yPoints, nPoints);
        }
        public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
            g2d.drawPolyline(xPoints, yPoints, nPoints);
        }
        public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            g2d.fillPolygon(xPoints, yPoints, nPoints);
        }
        public Color getColor() {
            return g2d.getColor();
        }
        public void setXORMode(Color c1) {
            g2d.setXORMode(c1);
        }
        public Font getFont() {
            return g2d.getFont();
        }
        public void setFont(Font font) {
            g2d.setFont(font);
        }
        public Graphics create() {
            return g2d.create();
        }
        public Rectangle getClipBounds() {
            return g2d.getClipBounds();
        }
        public Shape getClip() {
            return g2d.getClip();
        }
        public void setClip(Shape clip) {
            g2d.setClip(clip);
        }
        public FontMetrics getFontMetrics(Font f) {
            return g2d.getFontMetrics(f);
        }
        public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
            return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        }
        public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
            return g2d.drawImage(img, x, y, width, height, observer);
        }
        public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
            return g2d.drawImage(img, x, y, observer);
        }
        public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
            return g2d.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        }
        public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
            return g2d.drawImage(img, x, y, width, height, bgcolor, observer);
        }
        public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
            return g2d.drawImage(img, x, y, bgcolor, observer);
        }
    }
}
