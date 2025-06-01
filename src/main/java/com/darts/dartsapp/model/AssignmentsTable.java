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
//SQL Query to create Assignments Table if it doesnt exist
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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Assignments (classID, time, day, weight, type, colour) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setInt(1, assignment.getClassID());
            statement.setString(2, assignment.getTime());
            statement.setString(3, assignment.getDay());
            statement.setInt(4, assignment.getWeight());
            statement.setString(5, assignment.getType());
            statement.setString(6, assignment.getColour());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        //This is to Update assignment, due to time there is no way to update an assignment so this method currently does nothing.
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
    //This deletes assignments, due to time restraints this hasnt been implemented yet
    public void deleteAssignment(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Assignments WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate(); //exe SQL statement
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//Gets assignments by Class ID
    public List<Assignments> getAssignmentsByClassID(int classId) {
        List<Assignments> assignments = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Assignments WHERE classID = ?");       //Preps SQL statement
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();     //Executes the SQL
            while (resultSet.next()) {
                Assignments assignment = new Assignments(
                        resultSet.getInt("classID"),
                        resultSet.getString("time"),
                        resultSet.getString("day"),         //creates assignment objects
                        resultSet.getInt("weight"),
                        resultSet.getString("type"),
                        resultSet.getString("colour")
                );
                assignment.setAssignmentID(resultSet.getInt("id")); //sets assignment ID and Colour
                assignment.setColour(resultSet.getString("colour"));
                assignments.add(assignment);        //adds to list
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }

}