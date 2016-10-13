/*
 * Front End for CU_Report Database
 */
package cureportviewer;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Brian
 * @author Essa
 */
public class CUReportViewer {

    ResultSet rs;
    Database database;

    public CUReportViewer() throws SQLException {
        database = new Database();
        displayNumberOfRows("DBO.Q1_2006");
    }
    
    public void displayNumberOfRows(String table) throws SQLException{
        database.connect();
        rs = database.executeQuery("SELECT * FROM " + table);
        rs.last();
        System.out.println("Number of rows: " + rs.getRow());
        database.closeconnections();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        CUReportViewer test = new CUReportViewer();
    }
}
