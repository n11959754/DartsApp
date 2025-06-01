package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TasksTable {

    private Connection connection;

    public TasksTable() {
        connection = SqlConnect.getInstance();
        createTable();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Tasks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "assignmentID INTEGER NOT NULL,"
                    + "details VARCHAR NOT NULL,"
                    + "duration INTEGER NOT NULL,"
                    + "FOREIGN KEY (assignmentID) REFERENCES Assignments(id)"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTask(Tasks task) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Tasks (assignmentID, details, duration) VALUES (?, ?, ?)");
            statement.setInt(1, task.getAssignmentID());
            statement.setString(2, task.getDetails());
            statement.setInt(3, task.getDuration());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTasks(Tasks task) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Tasks SET assignmentID = ?, details = ?, duration = ? WHERE id = ?");
            statement.setInt(1, task.getAssignmentID());
            statement.setString(2, task.getDetails());
            statement.setInt(3, task.getDuration());
            statement.setInt(4, task.getID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Tasks WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Tasks getTask(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Tasks WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int assignmentID = resultSet.getInt("assignmentID");
                String details = resultSet.getString("details");
                int duration = resultSet.getInt("duration");
                Tasks task = new Tasks(assignmentID, details, duration);
                return task;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Tasks> getAllTasks(int id) {
        List<Tasks> tasks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Tasks WHERE assignmentID = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int assignmentID = resultSet.getInt("assignmentID");
                String details = resultSet.getString("details");
                int duration = resultSet.getInt("duration");
                Tasks task = new Tasks(assignmentID, details, duration);
                tasks.add(task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Tasks> getAllTasks() {
        List<Tasks> tasks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Tasks");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int assignmentID = resultSet.getInt("assignmentID");
                String details = resultSet.getString("details");
                int duration = resultSet.getInt("duration");
                Tasks task = new Tasks(assignmentID, details, duration);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }


    public List<Tasks> getTasksByUserID(int userId) {
        List<Tasks> tasks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT t.* FROM Tasks t " +
                            "JOIN Assignments a ON t.assignmentID = a.id " +
                            "JOIN Class c ON a.classID = c.id " +
                            "WHERE c.userID = ?"
            );
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int assignmentID = resultSet.getInt("assignmentID");
                String details = resultSet.getString("details");
                int duration = resultSet.getInt("duration");
                Tasks task = new Tasks(assignmentID, details, duration);
                task.setID(resultSet.getInt("id"));
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }




}
