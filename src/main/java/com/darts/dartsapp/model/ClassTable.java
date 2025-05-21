package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createClass(Class classes) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Class (userID, ClassName) VALUES (?, ?)");
            statement.setInt(1, classes.getUserID());
            statement.setString(2, classes.getClassName());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateClass(Class classes) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Class SET userID = ?, className = ? WHERE id = ?");
            statement.setInt(1, classes.getUserID());
            statement.setString(2, classes.getClassName());
            statement.setInt(3, classes.getClassID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteClass(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Class WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Class getClass(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Class WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String className = resultSet.getString("className");
                Class classes = new Class(userID, className);
                classes.setID(id);
                return classes;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Class> getAllClasses() {
        List<Class> classes = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Class");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int userID = resultSet.getInt("userID");
                String className = resultSet.getString("className");
                Class cls = new Class(userID, className);
                cls.setID(id);
                classes.add(cls);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public List<Units> getUnits(int id) {
        List<Units> units = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT " +
                    "c.id AS classID," +
                    "c.className," +
                    "t.id AS timeSlotID," +
                    "t.time AS timeSlotTime," +
                    "t.day," +
                    "t.type AS timeSlotType," +
                    "t.colour AS timeSlotColour," +
                    "a.id AS assignmentID," +
                    "a.time AS assignmentTime," +
                    "a.day AS assignmentDay," +
                    "a.weight AS assignmentWeight," +
                    "a.type AS assignmentType," +
                    "a.colour AS assignmentColour" +
                    "FROM Class c" +
                    "LEFT JOIN TimeSlots t ON t.classID = c.id" +
                    "LEFT JOIN Assignments a ON a.classID = c.id" +
                    "WHERE c.userID = ?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int classID = resultSet.getInt("c.id");
                String className = resultSet.getString("c.className");
                int timeSlotID = resultSet.getInt("t.id");
                String time = resultSet.getString("t.time");
                String day = resultSet.getString("t.day");
                String timeSlotType = resultSet.getString("t.type");
                String timeSlotColour = resultSet.getString("t.colour");
                int assignmentID = resultSet.getInt("a.id");
                String assignmentTime = resultSet.getString("a.time");
                String assignmentDay = resultSet.getString("a.day");
                int weight = resultSet.getInt("a.weight");
                String assignmentType = resultSet.getString("a.type");
                String assignmentColour = resultSet.getString("a.colour");
                Units unit = new Units(classID, className, timeSlotID, time, day, timeSlotType, timeSlotColour, assignmentID,assignmentTime, assignmentDay, weight, assignmentType, assignmentColour);
                units.add(unit);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return units;
    }

    public List<Class> getClassesByUser(int userId) {
        List<Class> classes = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Class WHERE userID = ?");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userID = resultSet.getInt("userID");
                String className = resultSet.getString("className");
                Class cls = new Class(userID, className);
                cls.setID(id);
                classes.add(cls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
