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
                    + "lectureTime VARCHAR NOT NULL,"
                    + "tutTime VARCHAR NOT NULL,"
                    + "FOREIGN KEY (classID) REFERENCES Class(id)"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createClassTimeSlot(ClassTimeSlot timeSlot) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO TimeSlots (classID, lectureTime, tutTime) VALUES (?, ?, ?)");
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getLectureTime());
            statement.setString(3, timeSlot.getTutTime());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateClassTimeSlot(ClassTimeSlot timeSlot) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE TimeSlots SET classID = ?, lectureTime = ?, tutTime = ? WHERE id = ?");
            statement.setInt(1, timeSlot.getClassID());
            statement.setString(2, timeSlot.getLectureTime());
            statement.setString(3, timeSlot.getTutTime());
            statement.setInt(4, timeSlot.getTimeSlotID());
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
                String lectureTime = resultSet.getString("lectureTime");
                String tutTime = resultSet.getString("tutTime");
                ClassTimeSlot timeSlot = new ClassTimeSlot(classID, lectureTime, tutTime);
                timeSlot.setTimeSlotID(id);
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
                String lectureTime = resultSet.getString("lectureTime");
                String tutTime = resultSet.getString("tutTime");
                ClassTimeSlot timeSlot = new ClassTimeSlot(classID, lectureTime, tutTime);
                timeSlot.setTimeSlotID(id);
                timeSlots.add(timeSlot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeSlots;
    }
}