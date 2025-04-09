/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

/**
 * Class to represent an Equilateral Triangle shape - contains 3 sides of equal length and
 * contains 3 vertices
 */

public class EquilateralTriangle extends Shape2D{

    private double sideLength;

    /**
    * Constructor for Equilateral Triangle  shape object
    * @param centre The centre of the Equilateral Triangle represented as a Point object
    * @param sideLength The length of each side (all same as equilateral)
    */
    public EquilateralTriangle(Point centre, double sideLength) {
        super(centre);
        this.sideLength = sideLength;
    }

    @Override
    public double getArea() {
        return Math.sqrt(3)/4 * Math.pow(sideLength,2);
    }

    @Override
    public double getParimeter() {
        return 3 * sideLength;
    }

    @Override
    public boolean containsPoint(Point point) {
        double dx = point.getXCord() - this.centre.getXCord();
        double dy = point.getYCord() - centre.getYCord();
        return (dy <= (Math.sqrt(3) * (dx + sideLength/3))) &&
                (dy <= -(Math.sqrt(3) * (dx - sideLength/3))) &&
                (dy >= -(Math.sqrt(3)/6 * sideLength));
    }

    @Override
    public Point[] getVertices() {
        double xCord = centre.getXCord();
        double yCord = centre.getYCord();
        Point[] vertices = new Point[3];
        vertices[0] = new Point (xCord, yCord + Math.sqrt(3)/3 * sideLength);
        vertices[1] = new Point (xCord - sideLength/2, yCord - Math.sqrt(3)/3 * sideLength);
        vertices[2] = new Point (xCord + sideLength/2, yCord - Math.sqrt(3)/3 * sideLength);
        return vertices;
    }



}
