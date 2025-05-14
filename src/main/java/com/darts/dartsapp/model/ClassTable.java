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
            PreparedStatement statement = connection.prepareStatement("UPDATE Class SET userID = ?, className = ?, WHERE id = ?");
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
}