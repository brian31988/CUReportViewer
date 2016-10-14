/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author brian.marshall
 */
public class Database {

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public void connect() {
        conn = null;

        try {

            String dbURL = "jdbc:sqlserver://2k8r2e;databaseName=CUReport";
            String user = "sa";
            String pass = "OhSACanYouSee.";

            conn = DriverManager.getConnection(dbURL, user, pass);

            //conn.close();
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }

    public ResultSet executeQuery(String SQLQuery) throws SQLException {
        stmt = null;
        rs = null;

        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = stmt.executeQuery(SQLQuery);
        return rs;
    }

    public void closeconnections() throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
}
