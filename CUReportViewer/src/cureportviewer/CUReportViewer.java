/*
 * Front End for CU_Report Database
 */
package cureportviewer;

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
    public static void main(String[] args) {
        System.out.println("test");
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame();
        frame.setTitle("Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hello World");
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
    }
    
}
