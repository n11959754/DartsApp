package com.darts.dartsapp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {

    private static Connection instance = null;

    private SqlConnect() {
        String url = "jdbc:sqlite:study.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqlConnect();
        }
        return instance;
    }
}