package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClassTable {

    private Connection connection;

    public ClassTable() {
        connection = SqlConnect.getInstance();
        createTable();                              //ensures DB Connection and table exists
    }

    private void createTable() {

        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Class ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userID INTEGER NOT NULL,"                        //SQL statement to create Class Table if it doesnt exist already
                    + "className VARCHAR NOT NULL,"
                    + "FOREIGN KEY (userID) REFERENCES Users(id)"
                    + ")";
            statement.execute(query);
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Class createClass(Class newClass) {
        String sql = "INSERT INTO Class (userID, ClassName) VALUES (?, ?)";     //SQL to add (insert) new class into DB
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, newClass.getUserID());
            statement.setString(2, newClass.getClassName());
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newClass.setID(generatedKeys.getInt(1));        //makes new ID
                    return newClass;
                } else {
                    System.err.println("Creating class failed, no ID obtained.");
                    return null;
                }
            } else {
                System.err.println("Creating class failed, no rows affected.");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //currently no way to update classes so this method does not work, in future this would be used.
    //intended to be a way to update an existing class
    public void updateClass(Class classes) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("UPDATE Class SET userID = ?, className = ? WHERE id = ?");
            statement.setInt(1, classes.getUserID());
            statement.setString(2, classes.getClassName());
            statement.setInt(3, classes.getClassID());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
// currently not in use as there is no way to delete a class, rather delete a time slot which removes it from the calandar.
    public void deleteClass(int id) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("DELETE FROM Class WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Class getClass(int id) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Class WHERE id = ?");    //SQL statement to get class from ID
            statement.setInt(1, id);
            resultSet = statement.executeQuery();       //execute query
            if (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String className = resultSet.getString("className");
                Class classes = new Class(userID, className);
                classes.setID(id); // id from parameter is the classID
                return classes;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
// gets classes by User
    public List<Class> getClassesByUser(int userId) {
        List<Class> classes = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Class WHERE userID = ?");                //sql statement to select all from class table with matching UserID
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();           //Exe query
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String className = resultSet.getString("className");

                //create class using UserID
                Class cls = new Class(userId, className);
                cls.setID(id);
                classes.add(cls);
            }
        } catch (SQLException e) {
            e.printStackTrace();            //error handling if issue with DB
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    public String getClassNameByID(int classID) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT className FROM Class WHERE id = ?");      //query DB for classname using classID
            statement.setInt(1, classID);
            ResultSet resultSet = statement.executeQuery();
            //return classname if found
            if (resultSet.next()) {
                return resultSet.getString("className");
            }
        } catch (Exception e) { //error handling for any weird errors
            e.printStackTrace();
        }
        //if not found, return
        return "Unknown";
    }

}
