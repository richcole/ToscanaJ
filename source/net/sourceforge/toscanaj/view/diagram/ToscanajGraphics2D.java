package net.sourceforge.toscanaj.view.diagram;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Implements the scaling as typical for Toscana.
 *
 * Toscana scales only distances, not sizes. The projection information set on
 * this class will be used on the coordinates of drawn objects but they will remain
 * their sizes.
 *
 * If e.g. a circles is drawn with different scaling sizes it will appear in
 * different positions but it will not have a different radius.
 */
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

    /**
     * Stores the internal standard graphic context.
     */
    private Graphics2D graphics;

    /**
     * Creates a new Toscana graphic context based on the given graphic context.
     */
    public ToscanajGraphics2D(Graphics2D graphics,  Point2D offset, double xscale, double yscale) {
        _offset = offset;
        _xScale = xscale;
        _yScale = yscale;
        this.graphics = graphics;
    }

    /**
     * Returns the offset used when projecting.
     */
    public Point2D getOffset() {
        return this._offset;
    }

    /**
     * Returns the horizontal scaling used.
     */
    public double getXScaling() {
        return this._xScale;
    }

    /**
     * Returns the vertical scaling used.
     */
    public double getYScaling() {
        return this._yScale;
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
     * Draws a line with the current paint.
     */
    public void drawLine(Point2D from, Point2D to) {
        graphics.draw( new Line2D.Double(project(from), project(to) ));
    }

    /**
     * Draws a line with the current paint.
     */
    public void drawLine(double x1, double y1, double x2, double y2) {
        graphics.drawLine( (int)projectX(x1), (int)projectY(y1), (int)projectX(x2), (int)projectY(y2) );
    }

    /**
     * Draws a circle around the given point with different fill and border paints.
     */
    public void drawCircle(Point2D center, double radius, Paint fill, Paint border) {
        Paint oldPaint = graphics.getPaint();
        Ellipse2D circle = new Ellipse2D.Double( projectX(center.getX()) - radius,
                                                 projectY(center.getY()) - radius,
                                                 radius * 2, radius * 2 );
        graphics.setPaint(fill);
        graphics.fill(circle);
        graphics.setPaint(border);
        graphics.draw(circle);
        graphics.setPaint(oldPaint);
    }

    /**
     * Draws a rectangle with different fill and border Paints.
     */
    public void drawFilledRectangle(double x, double y, double width, double height, Paint fill, Paint border) {
        Paint oldPaint = graphics.getPaint();
        Rectangle2D rect = new Rectangle2D.Double( projectX(x), projectY(y), width, height );
        graphics.setPaint( fill );
        graphics.fill( rect );
        graphics.setPaint( border );
        graphics.draw( rect );
        graphics.setPaint(oldPaint);
    }

    /**
     * Draws a string with the current paint.
     *
     * The margin is used to get an offset in target coordinates, while the position
     * itself is assumed to be in model coordinates.
     *
     * @see Graphics2D.drawString(String, int, int)
     */
    public void drawString(String text, double x, double y, int leftMargin, int topMargin) {
        graphics.drawString( text, (int)projectX(x) + leftMargin, (int)projectY(y) + topMargin);
    }

    public void setStroke(Stroke stroke) {
        graphics.setStroke(stroke);
    }
}