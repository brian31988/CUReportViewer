/*
 * Front End for CU_Report Database
 */
package controller;


import model.Database;
import view.GUI;
import java.sql.SQLException;

/**
 *
 * @author Brian
 * @author Essa
 */
public class CUReportViewer {

    GUI gui;

    public CUReportViewer() throws SQLException{
        gui = new GUI();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        CUReportViewer test = new CUReportViewer();
    }
}
