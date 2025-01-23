package com.soundnest.soundnest.Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/soundnest_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Efe2002";

    public DatabaseConnection(){

    }

    public static Connection connect() throws SQLException{
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/soundnest_db", "root", "Efe2002");
    }

}
