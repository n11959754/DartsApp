/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;

/**
 * Class to represent a single 2-dimensional point x,y in space where xCord is the
 * x-axis position increasing to the right and yCord is the y-axis position increasing
 * upwards - This class is complete and does not require any modification. You can further
 * enhance this class to implement other functionality however it is not required for
 * current Main driver application
 */
public class Point {

    private double xCord;
    private double yCord;

    /**
     * @param xCord - x-axis coordinate of point
     * @param yCord - y-axis coordinate of point.
     */
    public Point(double xCord, double yCord) {
        this.xCord = xCord;
        this.yCord = yCord;
    }

    /**
     * Getter for x-axis position of point object
     * @return double representing x-axis value
     */
    public double getXCord() {
        return this.xCord;
    }

    /**
     * Setter for x-axis position of point object
     * @param value for the x-axis coordinate
     */
    private void setXCord(double value) {
        this.xCord = value;
    }

    /**
     * Getter for y-axis position of point object
     * @return double representing y-axis value
     */
    public double getYCord() {
        return this.yCord;
    }

    /**
     * Setter for y-axis position of point object
     * @param value for the y-axis coordinate
     */
    private void setYCord(double value) {
        this.yCord = value;
    }

    /**
     * Moves the 2-dimensional point by x & y coordinates
     * @param moveX The amount to move the point in the x direction
     * @param moveY The amount to move the point in the y direction
     */
    public void translatePoint(double moveX, double moveY) {
        this.xCord += moveX;
        this.yCord += moveY;
    }

    /**
     * Calculates the distance between 2 points
     * @param otherPoint - the other point used to find distance with current point
     * @return double representing distance between 2 points using Pythagoras' theorem
     * using the distance = Math.sqrt((Math.pow(xCord1-xCord2),2) + (Math.pow(yCord1-yCord2),2))
     */
    public double distanceBetween(Point otherPoint) {
        double distX = this.xCord - otherPoint.getXCord();
        double distY = this.yCord - otherPoint.getYCord();
        return Math.sqrt((distX * distX) + (distY  * distY));
    }

    /**
     * Returns a string representation of the point in the form (x, y)
     * @return string representation of the point
     */
    @Override
    public String toString() {
        return String.format("(%.3f , %.3f)", this.xCord, this.yCord);
    }
}
