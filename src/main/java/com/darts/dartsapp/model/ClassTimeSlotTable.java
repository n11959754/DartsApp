package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClassTimeSlotTable {

    private Connection connection;

    public ClassTimeSlotTable() {
        connection = SqlConnect.getInstance();
        createTable();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS TimeSlots ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "classID INTEGER NOT NULL,"
                    + "time VARCHAR NOT NULL,"                              //SQL Query to create Time Slot Table
                    + "day VARCHAR NOT NULL,"
                    + "type VARCHAR NOT NULL,"
                    + "colour VARCHAR DEFAULT '#ffffff',"
                    + "FOREIGN KEY (classID) REFERENCES Class(id)"
                    + ")";
            statement.execute(query);       //executes query
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//creates a class timeslot for a class for a user
    public void createClassTimeSlot(ClassTimeSlot timeSlot) {
        try {

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO TimeSlots (classID, time, day, type, colour) VALUES (?, ?, ?, ?, ?)"       //SQL statement to add new time slot
            );
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getTime());
            statement.setString(3, timeSlot.getDay());
            statement.setString(4, timeSlot.getType());
            statement.setString(5, timeSlot.getColour());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();        //Print error
        }
    }


        //Currently no way to update class time slot so this does nothing , in future this would be implemented.
    public void updateClassTimeSlot(ClassTimeSlot timeSlot) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE TimeSlots SET classID = ?, time = ?, day = ?, type = ?, colour = ? WHERE id = ?");
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getTime());
            statement.setString(3, timeSlot.getDay());
            statement.setString(4, timeSlot.getType());
            statement.setString(5, timeSlot.getColour());
            statement.setInt(6, timeSlot.getTimeSlotID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//deletes timeslot from the timeslot table
    public void deleteClassTimeSlot(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TimeSlots WHERE id = ?");        //SQL q to remove all where classID matches
            statement.setInt(1, id);
            statement.executeUpdate();      //executes the deletion
        } catch (Exception e) {
            e.printStackTrace();        //error handling if something happens
        }

    }
//gets all timeslots that belong to a specific classID
    public List<ClassTimeSlot> getTimeSlotsByClassID(int classId) {
        List<ClassTimeSlot> slots = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TimeSlots WHERE classID = ?");     //SQL query select all from timeslots where class id matches
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();     //runs query

            //loops through all results and constructs a timeslot
            while (resultSet.next()) {
                ClassTimeSlot slot = new ClassTimeSlot(
                        resultSet.getInt("classID"),
                        resultSet.getString("time"),
                        resultSet.getString("day"),
                        resultSet.getString("type")
                );

                //sets additional fields
                slot.setTimeSlotID(resultSet.getInt("id"));
                slot.setColour(resultSet.getString("colour"));
                slots.add(slot); //adds to list
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;       //returns list of matching timeslots
    }
}