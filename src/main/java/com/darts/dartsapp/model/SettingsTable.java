package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SettingsTable {

    private Connection connection;

    public SettingsTable() {
        connection = SqlConnect.getInstance();
        createTable();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS Settings ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userID INTEGER NOT NULL,"
                    + "theme VARCHAR DEFAULT 'Light',"
                    + "dateFormat VARCHAR DEFAULT 'dd/mm/yyyy',"
                    + "timeFormat VARCHAR DEFAULT '12',"
                    + "FOREIGN KEY (userID) REFERENCES Users(id)"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createSettings(Settings settings) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Settings (userID) VALUES (?)");
            statement.setInt(1, settings.getUserID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateSettings(Settings settings) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Settings SET userID = ?, theme = ?, dateFormat = ?, timeFormat = ? WHERE id = ?");
            statement.setInt(1, settings.getUserID());
            statement.setString(2, settings.getTheme());
            statement.setString(3, settings.getDateFormat());
            statement.setString(4, settings.getTimeFormat());
            statement.setInt(5, settings.getSettingsID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSettings(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Settings WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Settings getSettings(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Settings WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String theme = resultSet.getString("theme");
                String dateFormat = resultSet.getString("dateFormat");
                String timeFormat = resultSet.getString("timeFormat");
                Settings settings = new Settings(userID);
                settings.setTheme(theme);
                settings.setDateFormat(dateFormat);
                settings.setTimeFormat(timeFormat);
                settings.setSettingsID(id);
                return settings;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Settings> getAllSettings() {
        List<Settings> settings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Settings");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int userID = resultSet.getInt("userID");
                String theme = resultSet.getString("theme");
                String dateFormat = resultSet.getString("dateFormat");
                String timeFormat = resultSet.getString("timeFormat");
                Settings setting = new Settings(userID);
                setting.setTheme(theme);
                setting.setDateFormat(dateFormat);
                setting.setTimeFormat(timeFormat);
                settings.add(setting);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings;
    }

}

