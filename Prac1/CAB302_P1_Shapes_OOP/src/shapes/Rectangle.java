/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

public class Rectangle extends Shape2D {

    private double width;
    private double length;

    /**
     * Constructor for Rectangle shape object
     * @param centre The centre of the Rectangle represented as a Point object
     * @param width The width of rectangle
     * @param length The length of rectangle
     */
    public Rectangle(Point centre, double width, double length) {

        super(centre);
        this.width = width;
        this.length = length;
    }

    private void setWidth(double width) {
        this.width = width;
    }

    private void setLength(double length) {
        this.length = length;
    }

    public double sgetWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getParimeter() {
        return 2 * length + 2 * width;
    }

    @Override
    public boolean containsPoint(Point point) {
        return (point.getXCord() >= centre.getXCord() - width/2) &&
                (point.getXCord() <= centre.getXCord() + width/2) &&
                (point.getYCord() >= centre.getYCord() - length/2) &&
                (point.getYCord() <= centre.getYCord() + length/2);
    }

    @Override
    public Point[] getVertices() {
        Point[] vertices = new Point[4];
        vertices[0] = new Point(centre.getXCord() - width / 2,  centre.getYCord() + length / 2);
        vertices[1] = new Point(centre.getXCord() + width / 2,  centre.getYCord() + length / 2);
        vertices[2] = new Point(centre.getXCord() - width / 2,  centre.getYCord() - length / 2);
        vertices[3] = new Point(centre.getXCord() + width / 2,  centre.getYCord() - length / 2);
        return vertices;
    }

}
