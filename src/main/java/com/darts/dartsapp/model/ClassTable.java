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
        createTable();
    }

    private void createTable() {

        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Class ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userID INTEGER NOT NULL,"
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
        String sql = "INSERT INTO Class (userID, ClassName) VALUES (?, ?)";
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
                    newClass.setID(generatedKeys.getInt(1));
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
            statement = connection.prepareStatement("SELECT * FROM Class WHERE id = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
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

    public List<Class> getAllClasses() {
        List<Class> classes = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Class");
            resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int userID = resultSet.getInt("userID");
                String className = resultSet.getString("className");
                Class cls = new Class(userID, className);
                cls.setID(id);
                classes.add(cls);
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
        return classes;
    }

    // Assuming Units class and TimeSlots/Assignments tables exist as per your original code.
    // This method had some SQL syntax issues (missing spaces before FROM/LEFT JOIN).
    public List<Units> getUnits(int userIdParam) { // Renamed parameter to avoid conflict with column name
        List<Units> units = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        // Corrected SQL syntax: added spaces before FROM and LEFT JOIN
        String sql = "SELECT " +
                "c.id AS classID, c.className, " + // Added comma
                "t.id AS timeSlotID, t.time AS timeSlotTime, t.day AS timeSlotDay, t.type AS timeSlotType, t.colour AS timeSlotColour, " + // Added comma, aliased day
                "a.id AS assignmentID, a.time AS assignmentTime, a.day AS assignmentDay, a.weight AS assignmentWeight, a.type AS assignmentType, a.colour AS assignmentColour " + // Removed trailing comma
                "FROM Class c " + // Added space
                "LEFT JOIN TimeSlots t ON t.classID = c.id " + // Added space
                "LEFT JOIN Assignments a ON a.classID = c.id " + // Added space
                "WHERE c.userID = ?;";

        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userIdParam);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int classID = resultSet.getInt("classID");
                String className = resultSet.getString("className");
                int timeSlotID = resultSet.getInt("timeSlotID"); // Make sure to handle if t.id is NULL from LEFT JOIN
                String time = resultSet.getString("timeSlotTime");
                String day = resultSet.getString("timeSlotDay"); // Use alias
                String timeSlotType = resultSet.getString("timeSlotType");
                String timeSlotColour = resultSet.getString("timeSlotColour");
                int assignmentID = resultSet.getInt("assignmentID"); // Make sure to handle if a.id is NULL
                String assignmentTime = resultSet.getString("assignmentTime");
                String assignmentDay = resultSet.getString("assignmentDay");
                int weight = resultSet.getInt("assignmentWeight");
                String assignmentType = resultSet.getString("assignmentType");
                String assignmentColour = resultSet.getString("assignmentColour");

                // You'll need to handle potential nulls from LEFT JOINs for non-class fields
                // For example, if a class has no timeslots, timeSlotID might be 0 or time might be null.
                // The Units constructor needs to be able to handle this.

                Units unit = new Units(classID, className, timeSlotID, time, day, timeSlotType, timeSlotColour,
                        assignmentID, assignmentTime, assignmentDay, weight, assignmentType, assignmentColour);
                units.add(unit);
            }
        } catch(SQLException e) {
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
        return units;
    }

    public List<Class> getClassesByUser(int userId) {
        List<Class> classes = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM Class WHERE userID = ?");
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                // int userID = resultSet.getInt("userID"); // userID from parameter is the same
                String className = resultSet.getString("className");
                Class cls = new Class(userId, className); // Use userId from parameter for consistency
                cls.setID(id);
                classes.add(cls);
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
        return classes;
    }
}