package com.darts.dartsapp.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserTable {

    private final Connection connection;

    public UserTable() {
        connection = SqlConnect.getInstance();
        createTable();
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS Users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "userName VARCHAR NOT NULL, "
                    + "email VARCHAR NOT NULL, "                    //creates user table if it doesnt exist already
                    + "phone VARCHAR NOT NULL, "
                    + "password VARCHAR NOT NULL, "
                    + "dob TEXT"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//creates user and stores to DB
    public int createUser(User user) {
        try {
            String sql = "INSERT INTO Users (userName, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getPassword());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int userID = keys.getInt(1);    //autoincrement ID
                user.setUserID(userID); // Set it on the object
                return userID;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //updates user details using UserID
    public void updateUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE Users SET userName = ?, email = ?, phone = ?, password = ?, dob = ? WHERE id = ?")) {       //SQL statement
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPhoneNumber());
            statement.setString(4, user.getPassword());
            if (user.getBirthday() != null) {
                statement.setString(5, user.getBirthday().toString()); //checks if not null, convert date to string to be stored
            } else {
                statement.setNull(5, Types.VARCHAR);
            }
            statement.setInt(6, user.getUserID());
            statement.executeUpdate();      //exe for speciied userID
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //deletes user from DB by UserID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();       //execute deletion
            return rowsAffected > 0;        //return true if deleted, false if not
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//Gets a user by userName
    public User getUser(String userName) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE userName = ?")) { //SQL query to select all info of user with matching username
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery(); //execute
            if (resultSet.next()) {     //if user is found.....
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone");
                String password = resultSet.getString("password");
                String dobStr = resultSet.getString("dob");
                LocalDate dob = (dobStr != null) ? LocalDate.parse(dobStr) : null;

                User user = new User(userName, email, phoneNumber, password, dob);
                user.setUserID(id);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    //if not found
    }

//
//    public List<User> getAllUsers() {
//        List<User> users = new ArrayList<>();
//        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users")) {
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String userName = resultSet.getString("userName");
//                String email = resultSet.getString("email");
//                String phoneNumber = resultSet.getString("phone");
//                String password = resultSet.getString("password");
//                String dobStr = resultSet.getString("dob");
//                LocalDate dob = (dobStr != null) ? LocalDate.parse(dobStr) : null;
//
//                User user = new User(userName, email, phoneNumber, password, dob);
//                user.setUserID(id);
//                users.add(user);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return users;
//    }
}
