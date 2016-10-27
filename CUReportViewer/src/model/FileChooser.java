/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author brian.marshall
 */
public class FileChooser extends JPanel {

    JFileChooser chooser;
    String path;

    public FileChooser() {
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
            path = chooser.getSelectedFile().getPath();
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
