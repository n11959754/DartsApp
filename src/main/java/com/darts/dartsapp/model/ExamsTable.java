package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExamsTable {

    private Connection connection;

    public ExamsTable() {
        connection = SqlConnect.getInstance();
        createTable();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Exams ("
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

    public void createExam(Exams exam) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Exams (classID, time, weight) VALUES (?, ?, ?)");
            statement.setInt(1, exam.getClassID());
            statement.setString(2, exam.getTime());
            statement.setInt(3, exam.getWeight());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateExam(Exams exam) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Exams SET classID = ?, time = ?, weight = ? WHERE id = ?");
            statement.setInt(1, exam.getClassID());
            statement.setString(2, exam.getTime());
            statement.setInt(3, exam.getWeight());
            statement.setInt(4, exam.getExamID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteExam(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Exams WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Exams getExam(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Exams WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("lectureTime");
                int weight = resultSet.getInt("weight");
                Exams exam = new Exams(classID, time, weight);
                exam.setExamID(id);
                return exam;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Exams> getAllExams() {
        List<Exams> exams = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM TimeSlots");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                int classID = resultSet.getInt("classID");
                String time = resultSet.getString("time");
                int weight = resultSet.getInt("weight");
                Exams exam = new Exams(classID, time, weight);
                exam.setExamID(id);
                exams.add(exam);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return exams;
    }
}
