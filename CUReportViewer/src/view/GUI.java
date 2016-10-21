/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javax.xml.ws.WebServiceException;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.Database;
import org.jfree.ui.RefineryUtilities;
import trendgraph.XYLineChart_AWT;

/**
 *
 * @author Brian
 * @author Essa
 */
public final class GUI extends javax.swing.JFrame {

    Database database;
    ResultSet rs;
    String orderedBy;
    String ascOrDesc;
    JTable table;

    /**
     * Creates new form GUI
     *
     * @throws java.sql.SQLException
     */
    public GUI() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        initComponents();
        database = new Database();
        initChoiceDropDowns();
        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public void displayResultInTable(ResultSet rs) throws SQLException {

        //gets column names of the table
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        //gets data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        //create table
        table = new JTable(new DefaultTableModel(data, columnNames));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        //Resizes column headers to fit their text
        for (int i = 0; i < table.getColumnCount(); i++) {
            DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn col = colModel.getColumn(i);
            int width = 0;

            TableCellRenderer renderer = col.getHeaderRenderer();
            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false,
                    false, 0, 0);
            width = comp.getPreferredSize().width;
            col.setPreferredWidth(width + 2);
        }

        /**
         * Adds mouse click listener to results table header so the user can
         * order the table by the header they click on
         */
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                orderedBy = table.getColumnName(col);

                if (ascOrDesc == "DESC") {
                    ascOrDesc = "ASC";
                } else {
                    ascOrDesc = "DESC";
                }

                int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
                int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();
                queryDatabase(orderedBy, ascOrDesc);
                resultsScrollPane.getHorizontalScrollBar().setValue(horizValue);
                resultsScrollPane.getVerticalScrollBar().setValue(vertValue);
            }
        });

        resultsScrollPane.setViewportView(table);
        repaint();
    }

    public void initChoiceDropDowns() throws SQLException {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2004; i <= currentYear; i++) {
            int year = i;
            yearChoiceDropdown.add(Integer.toString(year));
        }
        quarterChoiceDropdown.add("Q1");
        quarterChoiceDropdown.add("Q2");
        quarterChoiceDropdown.add("Q3");
        quarterChoiceDropdown.add("Q4");

        populateCreditUnionChoiceList();

        yearChoiceDropdown.addItemListener((ItemEvent ie) -> {
            populateCreditUnionChoiceList();
        });

        quarterChoiceDropdown.addItemListener((ItemEvent ie) -> {
            populateCreditUnionChoiceList();
        });
    }

    public void populateCreditUnionChoiceList() {
        database.connect();
        creditUnionChoiceList.removeAll();

        if (!infoTableRadioButton.isSelected()) {
            String tableName = (quarterChoiceDropdown.getSelectedItem() + "_" + yearChoiceDropdown.getSelectedItem());

            try {
                rs = database.executeQuery("SELECT DISTINCT \"Credit Union Name\" FROM " + tableName + " ORDER BY \"Credit Union Name\"");
                creditUnionLabel.setText("Populating...");
                creditUnionChoiceList.add("all");
                while (rs.next()) {
                    creditUnionChoiceList.add(rs.getString("Credit Union Name"));
                }
                creditUnionLabel.setText("CU Name");

                database.closeconnections();
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String tableName = (quarterChoiceDropdown.getSelectedItem() + "_" + yearChoiceDropdown.getSelectedItem() + "_Info");

            try {
                rs = database.executeQuery("SELECT DISTINCT \"CU_NAME\" FROM " + tableName + " ORDER BY \"CU_NAME\"");
                creditUnionLabel.setText("Populating...");
                creditUnionChoiceList.add("all");
                while (rs.next()) {
                    creditUnionChoiceList.add(rs.getString("CU_NAME"));
                }
                creditUnionLabel.setText("CU Name");

                database.closeconnections();
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void queryDatabase(String orderedBy, String ascOrDesc) {
        orderedBy = orderedBy;
        ascOrDesc = ascOrDesc;
        ascOrDescLabel.setText("IN " + ascOrDesc + " ORDER");
        orderedByLabel.setText("ORDERED BY " + orderedBy);

        String[] creditUnionNameArray = creditUnionChoiceList.getSelectedItems();

        if (creditUnionNameArray.length != 0) {

            database.connect();

            String tableName = (quarterChoiceDropdown.getSelectedItem() + "_" + yearChoiceDropdown.getSelectedItem());
            if (infoTableRadioButton.isSelected()) {
                tableName = tableName + "_Info";
            }

            try {
                if (creditUnionNameArray[0] == "all") {
                    rs = database.executeQuery("SELECT * FROM " + tableName + " ORDER BY \"" + orderedBy + "\" " + ascOrDesc);
                } else {
                    //build query
                    String query = "Select * FROM " + tableName + " WHERE ";
                    if (infoTableRadioButton.isSelected()) {
                        query = query + "[CU_NAME] in (";
                    } else {
                        query = query + "[Credit Union Name] in(";
                    }
                    for (int i = 0; i < creditUnionNameArray.length; i++) {
                        query = query + "'" + creditUnionNameArray[i] + "'";
                        if (creditUnionNameArray.length - 1 != i) {
                            query = query + ",";
                        }
                    }
                    query = query + ") " + "ORDER BY [" + orderedBy + "] " + ascOrDesc;

                    rs = database.executeQuery(query);
                }
                displayResultInTable(rs);
                database.closeconnections();
            } catch (SQLException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topMenuPanel = new javax.swing.JPanel();
        yearChoiceDropdown = new java.awt.Choice();
        quarterChoiceDropdown = new java.awt.Choice();
        searchButton = new javax.swing.JButton();
        creditUnionLabel = new java.awt.Label();
        quarterLabel = new java.awt.Label();
        yearLabel = new java.awt.Label();
        creditUnionChoiceList = new java.awt.List();
        jButton1 = new javax.swing.JButton();
        resultsLabel = new java.awt.Label();
        orderedByLabel = new java.awt.Label();
        ascOrDescLabel = new java.awt.Label();
        resultsScrollPane = new javax.swing.JScrollPane();
        infoTableRadioButton = new javax.swing.JRadioButton();
        graphButton = new javax.swing.JButton();
        graphSelectedCUFromTable = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        yearChoiceDropdown.setName(""); // NOI18N

        searchButton.setText("Table");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        creditUnionLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        creditUnionLabel.setText("Credit Union");

        quarterLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        quarterLabel.setText("Quarter");

        yearLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        yearLabel.setText("Year");

        creditUnionChoiceList.setMultipleMode(true);
        creditUnionChoiceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditUnionChoiceListActionPerformed(evt);
            }
        });

        jButton1.setText("deselect all");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        resultsLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        resultsLabel.setText("RESULTS");

        orderedByLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        orderedByLabel.setText("ORDERED BY Column Name");

        ascOrDescLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ascOrDescLabel.setText("IN Asc ORDER");

        infoTableRadioButton.setText("info table");
        infoTableRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoTableRadioButtonActionPerformed(evt);
            }
        });

        graphButton.setText("Graph");
        graphButton.setToolTipText("");
        graphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphButtonActionPerformed(evt);
            }
        });

        graphSelectedCUFromTable.setText("graph selected CUs from table");
        graphSelectedCUFromTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphSelectedCUFromTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topMenuPanelLayout = new javax.swing.GroupLayout(topMenuPanel);
        topMenuPanel.setLayout(topMenuPanelLayout);
        topMenuPanelLayout.setHorizontalGroup(
            topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topMenuPanelLayout.createSequentialGroup()
                .addGap(0, 115, Short.MAX_VALUE)
                .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(resultsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1065, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                        .addComponent(orderedByLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ascOrDescLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(155, 155, 155))
            .addGroup(topMenuPanelLayout.createSequentialGroup()
                .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                        .addGap(237, 237, 237)
                        .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(topMenuPanelLayout.createSequentialGroup()
                                .addGap(201, 201, 201)
                                .addComponent(infoTableRadioButton)
                                .addGap(96, 96, 96)
                                .addComponent(creditUnionChoiceList, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(topMenuPanelLayout.createSequentialGroup()
                                .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                                        .addGap(48, 48, 48)
                                        .addComponent(yearLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(130, 130, 130)
                                        .addComponent(quarterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                                        .addComponent(yearChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(quarterChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(125, 125, 125)
                                .addComponent(creditUnionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(topMenuPanelLayout.createSequentialGroup()
                                .addGap(362, 362, 362)
                                .addComponent(resultsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(graphButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                        .addGap(524, 524, 524)
                        .addComponent(graphSelectedCUFromTable, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        topMenuPanelLayout.setVerticalGroup(
            topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topMenuPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(searchButton)
                        .addGap(15, 15, 15)
                        .addComponent(graphButton)
                        .addGap(16, 16, 16)
                        .addComponent(jButton1))
                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(quarterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(quarterChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infoTableRadioButton))
                    .addGroup(topMenuPanelLayout.createSequentialGroup()
                        .addComponent(creditUnionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(creditUnionChoiceList, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addComponent(resultsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graphSelectedCUFromTable, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(topMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderedByLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ascOrDescLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(resultsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topMenuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topMenuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        ascOrDesc = "ASC";
        if (infoTableRadioButton.isSelected()) {
            orderedBy = "CU_NAME";
        } else {
            orderedBy = "Credit Union Name";
        }
        int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
        int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();
        queryDatabase(orderedBy, ascOrDesc);
        resultsScrollPane.getHorizontalScrollBar().setValue(horizValue);
        resultsScrollPane.getVerticalScrollBar().setValue(vertValue);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        for (int i = 0; i < creditUnionChoiceList.getItemCount(); i++) {
            creditUnionChoiceList.deselect(i);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void infoTableRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoTableRadioButtonActionPerformed
        populateCreditUnionChoiceList();
    }//GEN-LAST:event_infoTableRadioButtonActionPerformed

    private void graphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphButtonActionPerformed

        String[] creditUnionName = creditUnionChoiceList.getSelectedItems();
        graphCreditUnions(creditUnionName);
    }//GEN-LAST:event_graphButtonActionPerformed

    private void graphCreditUnions(String[] creditUnionNameArray) {
        int yearStart = Integer.parseInt(JOptionPane.showInputDialog("enter year to start at"));
        int yearEnd = Integer.parseInt(JOptionPane.showInputDialog("enter year to end at"));
        String[] choices = {"Total Net Worth", "Number of current members (not number of accounts)", "Net Income (Loss)"};
        String columnName = (String) JOptionPane.showInputDialog(null, null, "choose columnvalue to graph", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        try {
            XYLineChart_AWT chart = new XYLineChart_AWT(yearStart, yearEnd, creditUnionNameArray, columnName);
        } catch (SQLException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void creditUnionChoiceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creditUnionChoiceListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_creditUnionChoiceListActionPerformed

    private void graphSelectedCUFromTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphSelectedCUFromTableActionPerformed
        int[] row = table.getSelectedRows();
        String[] creditUnionNameArray = new String[row.length];
        if (row.length != 0) {
            for (int i = 0; i < row.length; i++) {
                creditUnionNameArray[i] = (String) table.getValueAt(row[i], 0);
            }
            graphCreditUnions(creditUnionNameArray);
        }
    }//GEN-LAST:event_graphSelectedCUFromTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label ascOrDescLabel;
    private java.awt.List creditUnionChoiceList;
    private java.awt.Label creditUnionLabel;
    private javax.swing.JButton graphButton;
    private javax.swing.JButton graphSelectedCUFromTable;
    private javax.swing.JRadioButton infoTableRadioButton;
    private javax.swing.JButton jButton1;
    private java.awt.Label orderedByLabel;
    private java.awt.Choice quarterChoiceDropdown;
    private java.awt.Label quarterLabel;
    private java.awt.Label resultsLabel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JButton searchButton;
    private javax.swing.JPanel topMenuPanel;
    private java.awt.Choice yearChoiceDropdown;
    private java.awt.Label yearLabel;
    // End of variables declaration//GEN-END:variables
}
