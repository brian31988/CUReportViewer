/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamyframe;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;


public class JavaMyFrame extends JFrame {
 
    Label labelInfo;
    JTable jTable;
 
    /*public static void main(String[] args) {
         
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }*/
 
    private static void createAndShowGUI() {
        JavaMyFrame myFrame = new JavaMyFrame();
        myFrame.setTitle("java-buddy.blogspot.com");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.prepareUI();
        myFrame.pack();
        myFrame.setVisible(true);
    }
     
    private void prepareUI(){
         
        JPanel vPanel = new JPanel();
        vPanel.setLayout(new BoxLayout(vPanel, BoxLayout.Y_AXIS));
         
        MyChart myChart = new MyChart();
        myChart.setPreferredSize(new Dimension(450, 200));
         
        jTable = new JTable(new MyTableModel());
        jTable.getSelectionModel()
                .addListSelectionListener(new MyRowColListener());
        jTable.getColumnModel().getSelectionModel()
                .addListSelectionListener(new MyRowColListener());
 
        jTable.setFillsViewportHeight(true);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(450, 100));
        vPanel.add(jScrollPane);
 
        labelInfo = new Label();
        vPanel.add(labelInfo);
         
        Button buttonPrintAll = new Button("Print All");
        buttonPrintAll.addActionListener(new ActionListener(){
 
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println();
                for(int i=0; i<jTable.getRowCount(); i++){
                    for(int j=0; j<jTable.getColumnCount(); j++){
                        String val = String.valueOf(jTable.getValueAt(i, j));
                        System.out.print(val + "\t");
                    }
                    System.out.println();
                }
                 
                //Create ListArray for the first row
                //and update MyChart
                ArrayList<Integer> l = new ArrayList<>();
                for(int i=0; i<jTable.getColumnCount(); i++){
                    l.add((Integer)jTable.getValueAt(0, i));
                }
                myChart.updateList(l);
            }
        });
         
        getContentPane().add(myChart, BorderLayout.PAGE_START);
        getContentPane().add(vPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPrintAll, BorderLayout.PAGE_END);
    }
     
    private class MyChart extends JComponent {
        ArrayList<Integer> chartList;
         
        public void updateList(ArrayList<Integer> l){
            System.out.println("updateList()");
             
            chartList = l;
            repaint();
        }
 
        @Override
        public void paint(Graphics g) {
            System.out.println("paint()");
             
            if(chartList != null){
                paintMe(g);
            }
        }
         
        private void paintMe(Graphics g){
            Graphics2D graphics2d = (Graphics2D)g;
            graphics2d.setColor(Color.blue);
             
            int width = getWidth();
            int height = getHeight();
             
            float hDiv = (float)width/(float)(chartList.size()-1);
            float vDiv = (float)height/(float)(Collections.max(chartList));
             
            for(int i=0; i<chartList.size()-1; i++){
                 
                int value1, value2;
                if(chartList.get(i)==null){
                    value1 = 0;
                }else{
                    value1 = chartList.get(i);
                }
                if(chartList.get(i+1)==null){
                    value2 = 0;
                }else{
                    value2 = chartList.get(i+1);
                }
                 
                graphics2d.drawLine(
                        (int)(i*hDiv), 
                        height - ((int)(value1*vDiv)),
                        (int)((i+1)*hDiv), 
                        height - ((int)(value2*vDiv)));
            }
             
            graphics2d.drawRect(0, 0, width, height);
        }
         
    }
     
    private class MyRowColListener implements ListSelectionListener {
 
        @Override
        public void valueChanged(ListSelectionEvent e) {
            System.out.println("valueChanged: " + e.toString());
 
            if (!e.getValueIsAdjusting()) {
                 
                int row = jTable.getSelectedRow();
                int col = jTable.getSelectedColumn();
                 
                if(row>= 0 && col>=0){
                    int selectedItem = (int)jTable.getValueAt(row, col);
                    labelInfo.setText("MyRowListener: "
                        + row + " : " + col + " = " + selectedItem);
                }
                 
            }
        }
    }
     
    class MyTableModel extends AbstractTableModel {
        private String[] DayOfWeek = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"};
     
        private Object[][] tableData = {
            {45, 25, 10, 4, 5, 6, 7},
            {4, 3, 2, 1, 7, 6, 5},
            {12, 20, 13, 14, 11, 24, 56},
            {13, 29, 23, 24, 25, 21, 20},
            {2, 4, 6, 8, 10, 12, 14},
            {11, 21, 33, 4, 9, 5, 4}};
 
        @Override
        public int getColumnCount() {
            return DayOfWeek.length;
        }
 
        @Override
        public int getRowCount() {
            return tableData.length;
        }
 
        @Override
        public String getColumnName(int col) {
            return DayOfWeek[col];
        }
 
        @Override
        public Object getValueAt(int row, int col) {
            return tableData[row][col];
        }
 
        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
 
        @Override
        public boolean isCellEditable(int row, int col) {
            return true;
        }
 
        @Override
        public void setValueAt(Object value, int row, int col) {
            tableData[row][col] = value;
            fireTableCellUpdated(row, col);
        }
 
    }
}
