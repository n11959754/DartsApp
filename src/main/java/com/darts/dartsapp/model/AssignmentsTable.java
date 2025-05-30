package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AssignmentsTable {

    private Connection connection;

    public AssignmentsTable() {
        connection = SqlConnect.getInstance();
        createTable();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Assignments ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "classID INTEGER NOT NULL,"
                    + "time VARCHAR NOT NULL,"
                    + "day DATE NOT NULL,"
                    + "weight INTEGER NOT NULL,"
                    + "type VARCHAR NOT NULL,"
                    + "colour VARCHAR DEFAULT '#ffffff',"
                    + "FOREIGN KEY (classID) REFERENCES Class(id)"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAssignment(Assignments assignment) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Assignments (classID, time, day weight, type) VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, assignment.getClassID());
            statement.setString(2, assignment.getTime());
            statement.setString(3, assignment.getDay());
            statement.setInt(4, assignment.getWeight());
            statement.setString(5, assignment.getType());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAssignment(Assignments assignment) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Assignments SET classID = ?, time = ?, day=?, weight = ?, type = ?, colour=? WHERE id = ?");
            statement.setInt(1, assignment.getClassID());
            statement.setString(2, assignment.getTime());
            statement.setString(3, assignment.getDay());
            statement.setInt(4, assignment.getWeight());
            statement.setString(5, assignment.getType());
            statement.setString(6, assignment.getColour());
            statement.setInt(7, assignment.getAssignmentID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAssignment(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Assignments WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Assignments getAssignment(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Assignments WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("time");
                String day = resultSet.getString("day");
                int weight = resultSet.getInt("weight");
                String type = resultSet.getString("type");
                Assignments assignment = new Assignments(classID, time, day, weight, type);
                assignment.setAssignmentID(id);
                return assignment;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Assignments> getAllAssignments() {
        List<Assignments> assignments = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Assignments");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("time");
                String day = resultSet.getString("day");
                int weight = resultSet.getInt("weight");
                String type = resultSet.getString("type");
                Assignments assignment = new Assignments(classID, time, day, weight, type);
                assignment.setAssignmentID(id);
                assignments.add(assignment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }

    public List<Assignments> getAssignmentsByClassID(int classId) {
        List<Assignments> assignments = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Assignments WHERE classID = ?");
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Assignments assignment = new Assignments(
                        resultSet.getInt("classID"),
                        resultSet.getString("time"),
                        resultSet.getString("day"),
                        resultSet.getInt("weight"),
                        resultSet.getString("type")
                );
                assignment.setAssignmentID(resultSet.getInt("id"));
                assignment.setColour(resultSet.getString("colour"));
                assignments.add(assignment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }

}