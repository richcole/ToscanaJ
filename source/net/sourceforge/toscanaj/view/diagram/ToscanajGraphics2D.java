package net.sourceforge.toscanaj.view.diagram;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class ToscanajGraphics2D {
    /**
     * The offset used to shift the projection in the new coordinate system.
     */
    Point2D _offset;

    /**
     * The scaling factor for the horizontal direction.
     */
    double _xScale;

    /**
     * The scaling factor for the horizontal direction.
     */
    double _yScale;

    /**
     * The default constructor just creates a scaling set with no change at all.
     */
    public ToscanajGraphics2D(){
      _offset = new Point2D.Double( 0, 0 );
      _xScale = 1;
      _yScale = 1;
    }

    private Graphics2D graphics;

    public ToscanajGraphics2D(Graphics2D graphics,  Point2D offset, double xscale, double yscale) {
        _offset = offset;
        _xScale = xscale;
        _yScale = yscale;
        this.graphics = graphics;
    }

    /**
     * Projects a point using the current scaling information.
     *
     * This includes scaling and moving it to the new offset.
     */
    public Point2D project( Point2D point )
    {
        return new Point2D.Double( projectX( point.getX() ),
                                   projectY( point.getY() ) );
    }

    /**
     * Projects a x-coordinate using the current scaling information.
     *
     * This includes scaling and moving it to the new offset.
     */
    public double projectX( double x )
    {
        return _offset.getX() + x * _xScale;
    }

    /**
     * Projects a y-coordinate using the current scaling information.
     *
     * This includes scaling and moving it to the new offset.
     */
    public double projectY( double y )
    {
        return _offset.getY() + y * _yScale;
    }

    /**
     * Projects a point applying the current scaling information in the
     * opposite direction.
     */
    public Point2D inverseProject( Point2D point )
    {
        return new Point2D.Double( inverseProjectX( point.getX() ),
                                   inverseProjectY( point.getY() ) );
    }

    /**
     * Projects an X-coordinate applying the current scaling information in the
     * opposite direction.
     */
    public double inverseProjectX( double x )
    {
        return ( x - _offset.getX() ) / _xScale;
    }

    /**
     * Projects an Y-coordinate applying the current scaling information in the
     * opposite direction.
     */
    public double inverseProjectY( double y )
    {
        return ( y - _offset.getY() ) / _yScale;
    }

    /**
     * Projects a point using the current scaling information.
     *
     * The offset is not applied.
     */
    public Point2D scale( Point2D point )
    {
        return new Point2D.Double( scaleX( point.getX() ),
                                   scaleY( point.getY() ) );
    }

    /**
     * Projects a x-coordinate using the current scaling information.
     *
     * The offset is not applied.
     */
    public double scaleX( double x )
    {
        return x * _xScale;
    }

    /**
     * Projects a y-coordinate using the current scaling information.
     *
     * The offset is not applied.
     */
    public double scaleY( double y )
    {
        return y * _yScale;
    }

    /**
     * Projects a point applying the current scaling information in the
     * opposite direction.
     *
     * The offset is not applied.
     */
    public Point2D inverseScale( Point2D point )
    {
        return new Point2D.Double( inverseScaleX( point.getX() ),
                                   inverseScaleY( point.getY() ) );
    }

    /**
     * Projects an X-coordinate applying the current scaling information in the
     * opposite direction.
     *
     * The offset is not applied.
     */
    public double inverseScaleX( double x )
    {
        return x / _xScale;
    }

    /**
     * Projects an Y-coordinate applying the current scaling information in the
     * opposite direction.
     *
     * The offset is not applied.
     */
    public double inverseScaleY( double y )
    {
        return y / _yScale;
    }

    /**
     * get current Graphics2D
     */
    public Graphics2D getGraphics2D() {
        return graphics;
    }

    /**
     * draws a DiagramLine between to DiagramNode
     */
    public void drawLine(Point2D from, Point2D to) {
        graphics.draw( new Line2D.Double(project(from), project(to) ));
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        graphics.drawLine( (int)projectX(x1), (int)projectY(y1), (int)projectX(x2), (int)projectY(y2) );
    }

    /**
     * draws a DiagramNode
     */
    public void drawEllipse2D(Point2D point, double radius) {
        graphics.fill( new Ellipse2D.Double( projectX(point.getX()) - radius, projectY(point.getY()) - radius,
                                            radius * 2, radius * 2 ) );
    }

    public void drawFilledRectangle(double x, double y, double width, double height, Color fill, Color border) {
        Rectangle2D rect = new Rectangle2D.Double( projectX(x), projectY(y), width, height );
        graphics.setPaint( fill );
        graphics.fill( rect );
        graphics.setPaint( border );
        graphics.draw( rect );
    }

    public void drawString(String text, double x, double y, int xFontSpacing, int yFontSpacing) {
        graphics.drawString( text, (int)projectX(x) + xFontSpacing, (int)projectY(y) + yFontSpacing);
    }

    public void setStroke(Stroke stroke) {
        graphics.setStroke(stroke);
    }

  /*public abstract  void addRenderingHints(Map hints);
  public abstract  void clip(Shape s);

  public void draw3DRect(int x, int y, int width, int height, boolean raised){}
  public abstract  void drawGlyphVector(GlyphVector g, float x, float y);
  public abstract  void drawImage(BufferedImage img, BufferedImageOp op, int x, int y);
  public abstract  boolean drawImage(Image img, AffineTransform xform, ImageObserver obs);
  public abstract  void drawRenderableImage(RenderableImage img, AffineTransform xform);
  public abstract  void drawRenderedImage(RenderedImage img, AffineTransform xform);
  public abstract  void drawString(AttributedCharacterIterator iterator, float x, float y);
  public abstract  void drawString(AttributedCharacterIterator iterator, int x, int y);
  public abstract  void drawString(String s, float x, float y);
  public abstract  void drawString(String str, int x, int y);
  public abstract  void fill(Shape s);
  public void fill3DRect(int x, int y, int width, int height, boolean raised){}
  public abstract  Color getBackground();
  public abstract  Composite getComposite();
  public abstract  GraphicsConfiguration getDeviceConfiguration();
  public abstract  FontRenderContext getFontRenderContext();
  public abstract  Paint getPaint();
  public abstract  Object getRenderingHint(RenderingHints.Key hintKey);
  public abstract  RenderingHints getRenderingHints();
  public abstract  Stroke getStroke();
  public abstract  AffineTransform getTransform();
  public abstract  boolean hit(Rectangle rect, Shape s, boolean onStroke);
  public abstract  void rotate(double theta);
  public abstract  void rotate(double theta, double x, double y);
  public abstract  void scale(double sx, double sy);
  public abstract  void setBackground(Color color);
  public abstract  void setComposite(Composite comp);
  public abstract  void setPaint(Paint paint);
  public abstract  void setRenderingHint(RenderingHints.Key hintKey, Object hintValue);
  public abstract  void setRenderingHints(Map hints);
  public abstract  void setStroke(Stroke s);
  public abstract  void setTransform(AffineTransform Tx);
  public abstract  void shear(double shx, double shy);
  public abstract  void transform(AffineTransform Tx);
  public abstract  void translate(double tx, double ty);
  public abstract  void translate(int x, int y);*/
}