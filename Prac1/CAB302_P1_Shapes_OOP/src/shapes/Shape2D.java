/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

/**
 * Shape2D is an Abstract Class and acts as a base class for all the 2D shapes
 * which includes Equilateral Triangle, Circle, Rectangle amd Square.
 * Shape2D is a shared common ancestor between the shape classes as each of these classes subclass
 * Shape2D
 */
public abstract class Shape2D {

    protected Point centre;
    private double area;
    private double perimeter;

    public Shape2D(Point centre) {
        this.centre = centre;
    }

    public void tranlate (double x, double y) {
        centre.translatePoint(x,y);
    }

    public abstract double getArea();

    public abstract double getParimeter();

    public abstract boolean containsPoint(Point point);

    public abstract Point[] getVertices();

}
