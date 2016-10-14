/*
 * Front End for CU_Report Database
 */
package controller;

import model.Database;
import view.GUI;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Brian
 * @author Essa
 */
public class CUReportViewer {

    ResultSet rs;
    Database database;
    GUI gui;

    public CUReportViewer() throws SQLException {
        database = new Database();
        gui = new GUI();
        queryExample("Q1_2005");
    }

    public void queryExample(String tableName) throws SQLException {
        database.connect();
        rs = database.executeQuery("SELECT * FROM " + tableName);
        gui.displayResultInTable(rs);
        database.closeconnections();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        CUReportViewer test = new CUReportViewer();
    }
}
