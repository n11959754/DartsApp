<h1 align="center">WEEK 1 PRACTICAL - OBJECT ORIENTED PROGRAMMING</h1>

<h1 align="center">Shapes2D Application</h1>

![Image of digital block with shapes](/images/AdobeStock_1169291855.jpeg)

> [!NOTE]
> The major assessment item for CAB302 is an application that solves a real world problem, written using the Java programing language. Students will form development teams of five (5) students, and will be required to incoporate Agile prinicples and values into their project management strategy. The project requirements will be dicussed in detail in the coming weeks. However, a definte requirement will be the use of Object Oriented Programming design principles.

The four pillars of OOP are:
1. Abstraction
2. Encapsulation
3. Inheritence
4. Polymorphism
  
> [!IMPORTANT]
> If you have not completed the Week 1 Readings for CAB302 on Canvas, I would strongly suggest completion of Activity 1.1 - Reading: Object Oriented Programming in Java as all of these concepts are discussed in detail with example code.<br/>

## Introduction
In this first practical we will revise OOP design principles. For some students it will reinforce their OOP knowledge/application. For others, it will hopefully identify gaps in knowledge that require additional study and attention.
The task this week is the implementation of an application that models 2 dimensional shapes, performs operations of these shape objects, and displays specific properties about these shapes. To enable implementation of the required classes a UML Class Diagram has been provided. This UML Class diagram is shown in figure 1 below. 

> [!TIP]
> If you are unfamiliar with UML Class Diagrams (UML === Unified Modelling Language) then refer to the **UML Class Diagram Essentials** on Canvas - Activity 1.3

![UML Class Diagram for implementation of Shapes 2D application.](/images/UML_Class_Diagram_shapes2D.png)
Figure 1 - UML for Shapes2D Application  

## Activity - Develop the Shapes 2D Application.
This progam models multiple different shapes - Circle, Equlateral Triangle, Recrtangle and a Square - calculates the area and perimeter of the shapes, transforms the vertives to shift the shape in space, and has some aditional functionlaity such as the ability to determine if the shape contains specific points in 2D space (contain a Point or a number of Points).
1. First you will need to clone this repository - use IntelliJ IDEA or if you are confident with Git you could do this clone at a Git bash terminal --> Clone Respository and use the HTTPS address from this repository - https://github.com/cab302-qut/CAB302_P1_Shapes_OOP.git
2. Implement the solution defined by the UML Class Diagram
3. Make sure to read all of this README file which contains some additional information which you may access here on the Repository or from the cloned repository on your desktop using IntelliJ IDEA

There are some classes that do not require any changes: **DO NOT CHANGE THE FOLLOWING 3 CLASSES**
1. **Main Class** - this is a driver program and will test your implementation to ensure that the output is correct - there are no Testing Frameworks included in this project. Testing will be covered in later tutorial.
2. **ShapeType Class** - This is a simple class to provide enumerated types for the 2D Shape Objects.
3. **Point Class** - This class models a simple cartesian coordinate in 2 Dimensional space (x,y).

## Important Notes
+ **Main** and **ShapeType** class are not shown in the UML Class diagram as they are only provided to test your implementation of the Shape2D class and subclasses.
+  The relationship between Shape2D and Point class is a dependency - Shape2D relies on the Point class as Point objects are required by the Shape2D class. The dotted line and open arrow states that Shape2D relies on Point (but Point does NOT rely on Shape2D).
+ Circle, EquilateralTriangle, and Rectangle are all subclasses of Shape2D which is an abstract class. These three (3) classes inherit from Shape2D
+ Square class is a subclass of Rectangle. Square class inherits from Rectangle.
+ All of the concrete shape classes (Circle, EquilateralTriangle, Rectangle, and Square) are examples of inheritance.

Ideally you would test your implementation in an incremental fashion using Test Driven Development (TDD) - this entails writing unit tests to ensure that the code you implmenmet is correct. As we have not yet covered TDD we will use a simple methodology to ensure the application performs as expected. Use the notes in QUT Canvas to work through the classes. All the required formulas and essential logic is described in detail. If there are any sections that are not clear, please ask your tutor(s) for guidance. 
The UML Class Diagram only describes the public methods and the instance variables required by the classes - you are welcome to add in additional private helper methods to reduce any code duplication. There should be no need to add additional public methods or any instance variables. 
This is a menu driven application and ideally you should validate all user input - this is an aspect that you can always implement later. Having the classes working correctly is far more important. In addition, there is a lot of validation already implemented for you in the **Main** class.

**The most important aim is to get your software solution working. You can always come back and refactor your code to reduce code duplication and to improve the overall structure. We recommend to do incremenetal development - work on a single class at a time starting with the easy classes and working your way to the more difficult classes. The order of classes we would recommend is:
1. **Point** class is complete however make sure to read the comments amd source code so you understand the purpose of the class.
2. **Shape2D** class would be the first class to attempt - this is an abstract class. Declare all the required private instance variables and the public getters, as well as the constructor for the class. Even though the **Shape2D** class is abstract it can have a constructor which it does. We cannot create an object from this abstract class but you can create its object by reference to it's child classes (EquilateralTriangle, Circle, Rectangle). When creating the constructors for the classes that extend the Shape2D abstract class make sure to call **_super()_** in their constructors.
3. **Circle**, **EquilateralTriangle**, and **Rectangle** would be the next three classes to complete - follow the notes in Canvas as the required formulas for area, perimeter and how to determine if a point exists within the shape is explained in detail.
4. Finally complete the **Square** class.

## Testing
Use the sample 2D Shape Program Output provided in Canvas and compare your output with the provided sample output. As previously stated make sure to build this application incrementally - build the project often and ensure that there is not a large accumulation of errors. Make sure the code essentially builds as you would expect. If you implement all the classes without some sanity checks through the development process, such as building the project successfully, then you will have a major debugging nightmare to deal with. Comparing test output with output from your implementation is **_NOT_** a robust methodology for assessment of implementation correctness. It is by no means an elegant methodology, however, until we cover Unit Testing this will be sufficient. Obviously you will need to match the inputs used in the sample application execution in order to compare output from your application and the application we implemented.

## Solutions
Once all the tutorials have been completed for the week the solution will be provided. The solution will be provided as:
1. Respository which may be cloned from GitHub
2. A video walkthrough explaining the implementation of all the classes will be provided on Canvas.

## FINAL NOTE
There are no silly questions in this unit - if there is a concept you do not understand then please ask your tutor to explain the concept for you. Our tutors are all experienced and well versed in the Java Language and will be able to assist you. If you have a question you can be sure that other students have the exact same questions. 
OOP is an essential requirement that you must master and be part of your programming repertoire. Understanding the four pillars of OOP and the advantages they offer is crucial:
+Code reusability
+Improved productivity
+Improved quality (mainly through code reuse)
+Scalability
+Improved security through data hiding
+Model real world and abstract entities and allow them to interact to solve real problems
+By using inheritance, we can eliminate redundant code and extend the use of existing classes.

OOP is an important skill to acquire and to understand, however, equally it is not suitable for all problem domains. It does take some time to master OOP and the best advice we can give to programming students is simple.....
**Write code, write code, write code, write code........** You cannot break anything so experiment with the code we provide and extend it, change it, try alternatives. Only by writing code and attempting various techniques will you learn the benefits of OOP as well as the limitations. While reading about these concepts is worthwhile, writing code, making changes and experimenting (add another shape class such as octagon and/or rhombus) is the best way to learn how OOP operates and the advantages it provides. You cannot break anything by writing code and experimenting. We learn more from our mistakes than our sucessess.
