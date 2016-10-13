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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        
        Connection conn = null;
 
        try {
 
            String dbURL = "jdbc:sqlserver://2k8r2e;databaseName=CUReport";
            String user = "sa";
            String pass = "OhSACanYouSee.";
            
            conn = DriverManager.getConnection(dbURL, user, pass);
            
            conn.close();
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }
}
