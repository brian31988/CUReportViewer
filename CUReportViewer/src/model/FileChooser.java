/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Dimension;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author brian.marshall
 */
public class FileChooser extends JPanel {

    JFileChooser chooser;
    String path;

    public FileChooser() {
        String fileName = JOptionPane.showInputDialog("Choose a name for your save file");
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Location to Save");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            path = chooser.getSelectedFile().getPath() + "/" + fileName;
        } else {
            System.out.println("No Selection ");
        }
    }

    public String getPath() {
        return path;
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }
}
