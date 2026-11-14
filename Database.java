package com.loansystem;

import java.sql.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:loan_management.db";
    private Database() {}
    public static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        return c;
    }
    public static void initialize() {
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS customers(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT, email TEXT, income REAL DEFAULT 0, credit_score INTEGER DEFAULT 0)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS loans(id INTEGER PRIMARY KEY AUTOINCREMENT, customer_name TEXT NOT NULL, category TEXT NOT NULL, amount REAL NOT NULL, interest_rate REAL NOT NULL, months INTEGER NOT NULL, status TEXT NOT NULL DEFAULT 'Pending', created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
            try (ResultSet r=s.executeQuery("SELECT COUNT(*) FROM users")) { if(r.next() && r.getInt(1)==0) {
                s.executeUpdate("INSERT INTO users(username,password,role) VALUES('admin','admin123','Administrator'),('officer','loan123','Loan Officer')");
            }}
            try (ResultSet r=s.executeQuery("SELECT COUNT(*) FROM customers")) { if(r.next() && r.getInt(1)==0) {
                s.executeUpdate("INSERT INTO customers(name,phone,email,income,credit_score) VALUES('Aarav Mehta','9876543210','aarav@example.com',65000,750),('Diya Sharma','9876501234','diya@example.com',52000,720)");
            }}
            try (ResultSet r=s.executeQuery("SELECT COUNT(*) FROM loans")) { if(r.next() && r.getInt(1)==0) {
                s.executeUpdate("INSERT INTO loans(customer_name,category,amount,interest_rate,months,status) VALUES('Aarav Mehta','Home',2500000,8.5,240,'Approved'),('Diya Sharma','Education',500000,7.5,60,'Pending')");
            }}
        } catch(SQLException e) { throw new RuntimeException("Could not initialize SQLite database", e); }
    }
}
