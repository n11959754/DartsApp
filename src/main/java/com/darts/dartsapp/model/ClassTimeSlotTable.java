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
                    + "time VARCHAR NOT NULL,"
                    + "type VARCHAR NOT NULL,"
                    + "colour VARCHAR DEFAULT '#ffffff'"
                    + "FOREIGN KEY (classID) REFERENCES Class(id)"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createClassTimeSlot(ClassTimeSlot timeSlot) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO TimeSlots (classID, time, type) VALUES (?, ?, ?)");
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getTime());
            statement.setString(3, timeSlot.getType());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateClassTimeSlot(ClassTimeSlot timeSlot) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE TimeSlots SET classID = ?, time = ?, type = ?, colour = ? WHERE id = ?");
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getTime());
            statement.setString(3, timeSlot.getType());
            statement.setString(4, timeSlot.getColour());
            statement.setInt(5, timeSlot.getTimeSlotID());
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

    public ClassTimeSlot getClassTimeSlot(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TimeSlots WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("time");
                String type= resultSet.getString("type");
                String colour = resultSet.getString("colour");
                ClassTimeSlot timeSlot = new ClassTimeSlot(classID, time, type);
                timeSlot.setTimeSlotID(id);
                timeSlot.setColour(colour);
                return timeSlot;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ClassTimeSlot> getAllClassTimeSlots() {
        List<ClassTimeSlot> timeSlots = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TimeSlots");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("time");
                String type= resultSet.getString("type");
                String colour = resultSet.getString("colour");
                ClassTimeSlot timeSlot = new ClassTimeSlot(classID, time, type);
                timeSlot.setTimeSlotID(id);
                timeSlot.setColour(colour);
                timeSlots.add(timeSlot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlots;
    }
}