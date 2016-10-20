/*
 * Front End for CU_Report Database
 */
package controller;


import model.Database;
import view.GUI;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Brian
 * @author Essa
 */
public class CUReportViewer {

    GUI gui;

    public CUReportViewer() throws SQLException{
        try {
            gui = new GUI();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CUReportViewer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(CUReportViewer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(CUReportViewer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(CUReportViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        CUReportViewer test = new CUReportViewer();
    }
}
