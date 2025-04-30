<h1 align="center">WEEK 6 Address Book Application</h1>

<h1 align="center">Database Connectivity</h1>

![Image of digital block with shapes](/images/apiImg.jpeg)

> [!NOTE]
AIM: Create an Address Book Application with Database persistance - SQLite Database
> This application could form the basic foundation for your CAB302 Project. There is a requirement that your application contains a database, however, the choice of database is left as a decision to be made by the group. Any database (apart from an in-memory/volatile database) is acceptable. Make sure the database you select has a JDBC driver to connect your application to the database - most databases will of course support Java.


![Screen for entering the contact details.](/images/Update_Contact_Fixed.gif)

**You are free to extend, change, redesign this simple application and modify it to your requirements.**

## Classes

1. Contact Class - Simple class for creating Contacts (people to store in address book)
2. HelloApplication - Entry point for this JavaFX Application - Boilerplate code to load initial view
3. HelloController - Logic for initial screen events
4. IContactDAO - Interface for Contact Data Access Object - Handles CRUD (Create, Read, Update, Delete operations)
5. MainController - Logic for controlling the data displayed on the view - Connects model (data) and views
6. MockContactDAO - Creates a Mock Database using the same interface as the class that will control database operations - This Mock could be useful for unit testing (**Only it implements same interface as class responsible for database logic)


## Part A: Update Application Structure
When implementing the various classes for this project we suggest using a MVC Architecture (Model View Controller) - there should be at minimum a separation of concerns with all the models in separate folder, as well as separate folder for controllers. Make sure to let IntelliJ IDEA do the heavy lifting. Create the new folders/packages in IntelliJ IDEA and drag and drop classes into appropriate packages. IntelliJ IDEA will recognise that shifting these files will require the code to be refactored to update directory structures and imports so make sure to allow IntelliJ IDEA to assist in this process.

## Part B: Add JDBC Driver for SQLite
We are using an SQLite database however you could equally use a MySQL database - that is an implementation choice. Use IntelliJ IDEA to add thje JDBC driver - you will probably need to update the "pom" file (pom.xml) in the root directory and add in the appropriate dependency for the JDBC driver. The SQLite JDBC driver we are using may not be listed in IntelliJ IDEA however you will still be able to install the latest driver which currently is stable and has no reported security vulnerabilities.

## Part C: Implement a new DAO class with SQLite
Once the JDBC driver is added a new Java class will be required to create the Database Connection. We will also create some classes to create and populate the SQLite database. You will be able to use **[DB Browser for SQLite](https://sqlitebrowser.org/)** to inspect the database, tables, and data contained within. Feel free to alter the schema and experiment with the database design.


## Part D: Implement the CRUD operations
The final step is to implement the CRUD operations in the SqliteContactDao class. We will implement the getAllContacts, getContact, updateContact, addContact, and deleteContact methods.

> [!IMPORTANT]
> SQL Injection is one of the top 10 web application vulnerabilities. In simple words, SQL Injection means injecting/inserting SQL code in a query via user-inputted data. It can occur in any applications using relational databases like Oracle, MySQL, PostgreSQL and SQL Server. To perform SQL Injection, a malicious user first tries to find a place in the application where he can embed SQL code along with data. It can be the login page of any web application or any other place. So when data embedded with SQL code is received by the application, SQL code will be executed along with the application query.
+ A malicious user can obtain unauthorized access to your application and steal data.
+ They can alter, delete data in your database and take your application down.
+ A hacker can also get control of the system on which database server is running by executing database specific system commands.
  
> [!TIP]
> Instead of concatenating username and password into the query, we provide them to query via PreparedStatement’s setter methods.

## Best Practices to avoid SQL Injection

+ Validate data before using them in the query.
+ Do not use common words as your table name or column name. For example, many applications use tbluser or tblaccount to store user data. Email, firstname, lastname are common column names.
+ Do not directly concatenate data ( received as user input) to create SQL queries.
+ Use positional parameters in the query. If you are using plain JDBC, then use PreparedStatement to execute the query.
+ Do proper code review so that no developer accidentally write unsafe SQL code.



