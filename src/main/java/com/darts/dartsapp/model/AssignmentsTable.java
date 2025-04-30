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
                    + "weight INTEGER NOT NULL,"
                    + "FOREIGN KEY (classID) REFERENCES Class(id)"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAssignment(Assignments assignment) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Assignments (classID, time, weight) VALUES (?, ?, ?)");
            statement.setInt(1, assignment.getClassID());
            statement.setString(2, assignment.getTime());
            statement.setInt(3, assignment.getWeight());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAssignment(Assignments assignment) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Assignments SET classID = ?, time = ?, weight = ? WHERE id = ?");
            statement.setInt(1, assignment.getClassID());
            statement.setString(2, assignment.getTime());
            statement.setInt(3, assignment.getWeight());
            statement.setInt(4, assignment.getAssignmentID());
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
                String time = resultSet.getString("lectureTime");
                int weight = resultSet.getInt("weight");
                Assignments assignment = new Assignments(classID, time, weight);
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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TimeSlots");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("time");
                int weight = resultSet.getInt("weight");
                Assignments assignment = new Assignments(classID, time, weight);
                assignment.setAssignmentID(id);
                assignments.add(assignment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }
}