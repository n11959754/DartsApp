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
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createClassTimeSlot(ClassTimeSlot timeSlot) {
        try {

            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO TimeSlots (classID, time, day, type, colour) VALUES (?, ?, ?, ?, ?)"
            );
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getTime());
            statement.setString(3, timeSlot.getDay());
            statement.setString(4, timeSlot.getType());
            statement.setString(5, timeSlot.getColour());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
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

    public void deleteClassTimeSlot(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TimeSlots WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<ClassTimeSlot> getTimeSlotsByClassID(int classId) {
        List<ClassTimeSlot> slots = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TimeSlots WHERE classID = ?");
            statement.setInt(1, classId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ClassTimeSlot slot = new ClassTimeSlot(
                        resultSet.getInt("classID"),
                        resultSet.getString("time"),
                        resultSet.getString("day"),
                        resultSet.getString("type")
                );
                slot.setTimeSlotID(resultSet.getInt("id"));
                slot.setColour(resultSet.getString("colour"));
                slots.add(slot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;
    }
}