/**
 * This work is marked with CC0 1.0 Universal
 */
package shapes;



import shapes.ShapeType.*;

import java.util.Scanner;

/**
 * Driver class for the program. Do not modify.
 * Main class contains main() as the entry point for the application.
 * Once all the classes have been implemented use this driver class
 * to test the output is as per expected in the Canvas documentation.
 * Option: Comment out code in this file for classes that not yet implemented
 * in order to test one class at a time --> optional approach
 */


public class Main {

    private final static String SHAPE_NUMBER = "Enter the number of shapes to create -> ";
    private final static String NUMBER_OF_POINTS = "Enter the number of points to create -> ";
    private final static String CENTRE_OF_SHAPE = "Enter the centre of shape %d in form of x,y -> ";
    private final static String POINT_ENTRY_NARRATION =  "Enter the coordinates of the point in the form x,y -> ";
    private final static String SHAPE_TYPE_NARRATION = "Select the type of shape %d to create from the following options:\n%s";
    private final static String SHAPE_MENU = "1. Equilateral Triangle\n2. Circle\n3. Rectangle\n4. Square\n\nSelection (1,2,3,4) -> ";
    private final static String TRIANGLE_SIDE = "Enter the side length of the Equilateral Triangle -> ";
    private final static String CIRCLE_RADIUS = "Enter the radius of the circle -> ";
    private final static String SQUARE_SIDE = "Enter the length of the Square side -> ";
    private final static String RECTANGLE_LENGTH = "Enter the length of the Rectangle -> ";
    private final static String RECTANGLE_WIDTH = "Enter the width of the Rectangle -> ";
    private final static String POINT_NUMBER = "Enter the number of points (Maximum of 5 points to check) -> ";
    private final static int NUMBER_OF_POINTS_TO_CHECK = 5;
    private final static String SPECIFIC_POINT_ENTRY_NARRATION =  "Enter the coordinates of point %d in the form x,y -> ";
    private final static String TRANSLATE_SHAPES = "\n\nEnter the amount to translate the shapes, in the form dx,dy -> ";




    public static void main(String[] args)     {

        System.out.println( "===========================" );
        Main program = new Main();
        Scanner userInput = new Scanner(System.in);
        int numberOfShapes=program.askIntegerInput(userInput, SHAPE_NUMBER,0, Integer.MAX_VALUE);
        Shape2D[] shapes = program.readShapes(userInput, numberOfShapes);

        int numberOfPoints = program.askIntegerInput(userInput, POINT_NUMBER,1,NUMBER_OF_POINTS_TO_CHECK);
        Point[] points = program.readPoints(userInput, numberOfPoints);

        // Print the shapes and whether they contain the points
        program.printShapesDetails(shapes, points);

        // Translate the shapes
        program.translateShapes(userInput, shapes, points);

        // Print the shapes and whether they still contain the points
        program.printShapesDetails(shapes, points);

        System.out.println( "===========================" );
    }

    /**
     * Method to invoke translation for each of the shape objects
     * @param shapes Array of all shapes
     * @param points Array of points to check
     */
    public void translateShapes(Scanner userInput, Shape2D[] shapes, Point[] points) {

        Point translate = readPoint(userInput, TRANSLATE_SHAPES);
        for (Shape2D shape : shapes) {
            shape.translate(translate.getXCord(), translate.getYCord());
        }
        printShapesDetails(shapes, points);
    }



    /**
     * Method to get dimensions/required parameters of th
     * @param numberOfShapes Number of shapes to be created
     * @return Array of shapes
     */
    public Shape2D[] readShapes(Scanner userInput, int numberOfShapes)
    {

        Shape2D[] shapes = new Shape2D[numberOfShapes];

        for (int i = 0; i < numberOfShapes; i++)
        {
            String narration = String.format(CENTRE_OF_SHAPE, i+1);

            Point centre = readPoint(userInput, narration);

            String concat_narration = String.format(SHAPE_TYPE_NARRATION , i+1 ,SHAPE_MENU);
            int choice = askIntegerInput(userInput, concat_narration,0,5);
            ShapeType st = ShapeType.from(choice);
            switch (st)
            {
                case ShapeType.TRIANGLE:
                    shapes[i] = readEquilateralTriangle(userInput, centre);
                    break;
                case ShapeType.CIRCLE:
                    shapes[i] = readCircle(userInput, centre);
                    break;
                case ShapeType.RECTANGLE:
                    shapes[i] = readRectangle(userInput, centre);
                    break;
                case ShapeType.SQUARE:
                    shapes[i] = readSquare(userInput, centre);
                    break;
                default:
                    System.out.println( "Invalid choice" );
                    break;
            }
        }
        return shapes;
    }


    /**
     * Method for getting input as a double - validated input plus given range of acceptable values
     * @param narration Prompt for the user instructing what input required
     * @param lowerLimit Lowest acceptable value
     * @param upperLimit Highest acceptable value
     * @return The double entered by user in console
     */
    public double askDoubleInput(Scanner userInput, String narration, double lowerLimit, double upperLimit) {
        boolean valid = false;
        double num =0;
        while (!valid) {
            System.out.print(narration);

            try {
                num = userInput.nextDouble();
                userInput.nextLine();
                valid = true;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input - please enter an integer\n");
            }
        }
        return num;
    }

    /**
     * Method for getting input as an integer - validated input plus given range of acceptable values
     * @param narration Prompt for the user instructing what input required
     * @param lowerLimit Lowest acceptable value
     * @param upperLimit Highest acceptable value
     * @return The integer entered by user in console
     */
    public int askIntegerInput(Scanner userInput, String narration, int lowerLimit, int upperLimit) {
        boolean valid = false;
        int num =0;
        while (!valid) {
            System.out.print( narration );
            try {
                num = userInput.nextInt();
                userInput.nextLine();
                if ((num<=lowerLimit) || (num>=upperLimit)) {
                    System.out.printf("Invalid input - enter a number greater than %d and less than %d\n", lowerLimit, upperLimit);
                } else {
                    valid = true;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input - please enter an integer\n");
            }
        }
        return num;
    }

     /**
     * Prints the details of the shapes and whether they contain the points
     * @param shapes The shapes to print
     * @param points The points to check if contained in the shape object
     */
    public void printShapesDetails(Shape2D[] shapes, Point[] points)
    {
        int count = 1;
        for (Shape2D shape : shapes)
        {

            System.out.printf( "\nShape %d is %s\n",count, shape.getClass().getSimpleName() );
            count++;

            // Print whether the shape contains each point
            for (Point point : points)
            {
                if (shape.containsPoint( point ))
                {
                    System.out.printf( "\t%s contains the point %.2f , %.2f\n ", shape.getClass().getSimpleName(), point.getXCord(), point.getYCord());
                } else {
                    System.out.printf( "\t%s does not contain the point %.2f , %.2f\n ", shape.getClass().getSimpleName(), point.getXCord(), point.getYCord());
                }
            }
            // Print the shape's details
            if (!(shape instanceof Circle)) {
                Point[] vert = shape.getVertices();
                for (int i=0; i < vert.length; i++) {
                    System.out.printf("\tVertices %d is %.2f , %.2f\n", i+1, vert[i].getXCord(), vert[i].getYCord());
                }
            } else {
                System.out.println("\tShape is Circle and has no vertices\n");
            }
            System.out.printf( "\tArea: %.2f\n", shape.getArea() );
            System.out.printf( "\tPerimeter: %.2f\n", shape.getPerimeter() );
        }
    }


    /**
     * Reads a number of points from the user as input in the console in the form of x,y
     * (x coordinate, y coordinate)
     * @param numberOfPoints Number of times the method will call helper function to take
     *                       inputs from user from console in the form of x,y on Cartesian Plane
     * @return Array of Point objects representing the x,y coordinates entered by user
     */
    public Point[] readPoints(Scanner userInput, int numberOfPoints) {

        Point[] points = new Point[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++)
        {
           String narration = String.format(SPECIFIC_POINT_ENTRY_NARRATION, i+1);
            points[i] = readPoint(userInput , narration);
        }
        return points;
    }


    /**
     * Reads a point as input from user in form of x,y (x coordinate, y coordinate)
     * @param narration is the prompt to the user as to type of input type/format required
     * @return Point object representing point on Cartesian plane
     */
    public Point readPoint(Scanner userInput, String narration)
    {
        double x=0,y=0;
        boolean valid = false;
        while (!valid) {
            System.out.print( narration );
            String input = userInput.nextLine();
            String regex=",";
            String[] coordinates = input.split( regex,2 );
            if (coordinates.length!=2) {
                System.out.println("Invalid input - please enter point in form x,y e.g. 2.4, 6.5\n");
            } else {
                try {
                    x = Double.parseDouble( coordinates[0] );
                    y = Double.parseDouble( coordinates[1] );
                    if ((x <0) || (y<0)) {
                        System.out.println("Invalid input - points entered must be in positive cartesian plane (not less than zero\n");
                    } else {
                        valid = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format - please enter a number (int or decimal");
                }
            }

        }

        return new Point(x, y);
    }

    /**
     * Method gathers required data to create Equilateral Triangle object
     * @param centre which is the centre of the triangle
     * @return Equilateral Triangle object
     */
    public EquilateralTriangle readEquilateralTriangle(Scanner userInput, Point centre)
    {
        double side = askDoubleInput(userInput, TRIANGLE_SIDE, 1, Double.MAX_VALUE);

        return new EquilateralTriangle( centre, side );
    }

    /**
     * Method gathers required data for a Circle object
     * @param centre of the circle
     * @return Circle object
     */
    public Circle readCircle(Scanner userInput, Point centre)
    {
        double radius = askDoubleInput(userInput, CIRCLE_RADIUS, 1, Double.MAX_VALUE);

        return new Circle( centre, radius );
    }

    /**
     * Method gathers required data to create a Rectangle object
     * @param centre of the Rectable
     * @return Rectangle object
     */
    public Rectangle readRectangle(Scanner userInput, Point centre)
    {
        double width = askDoubleInput(userInput, RECTANGLE_WIDTH, 1, Double.MAX_VALUE);
        double length = askDoubleInput(userInput, RECTANGLE_LENGTH, 1, Double.MAX_VALUE);

        return new Rectangle( centre, width, length );
    }

    /**
     * Method gathers required data to create a Square object
     * @param centre of the Square
     * @return Square object
     */
    public Square readSquare(Scanner userInput, Point centre)
    {
        double length = askDoubleInput(userInput, SQUARE_SIDE, 1, Double.MAX_VALUE);

        return new Square( centre, length );
    }
}
