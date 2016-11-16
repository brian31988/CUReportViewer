/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.Database;
import model.FileChooser;
import model.SpreadSheet;
import trendgraph.XYLineChart_AWT;

/**
 *
 * @author Brian
 * @author Essa
 */
public final class GUI extends javax.swing.JFrame {

    private Database database;
    private ResultSet rs;
    private String orderedBy;
    private String ascOrDesc;
    private JTable table;
    private boolean isInfo;

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
                table.selectAll();
                int[] row = table.getSelectedRows();
                table.clearSelection();
                String[] creditUnionName = new String[row.length];
                if (row.length != 0) {
                    for (int i = 0; i < row.length; i++) {
                        if (isInfo) {
                            creditUnionName[i] = (String) table.getValueAt(row[i], 4);
                        } else {
                            creditUnionName[i] = (String) table.getValueAt(row[i], 0);
                        }
                    }
                }
                if (ascOrDesc == "DESC") {
                    ascOrDesc = "ASC";
                } else {
                    ascOrDesc = "DESC";
                }
                int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
                int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();
                queryDatabase(orderedBy, ascOrDesc, creditUnionName);
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

        if (isInfo == false) {
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

    private void queryDatabase(String orderedBy, String ascOrDesc, String[] creditUnionNames) {
        orderedBy = orderedBy;
        ascOrDesc = ascOrDesc;
        ascOrDescLabel.setText("IN " + ascOrDesc + " ORDER");
        orderedByLabel.setText("ORDERED BY " + orderedBy);
        String[] creditUnionName = creditUnionNames;

        if (creditUnionName.length != 0) {

            database.connect();

            String tableName = (quarterChoiceDropdown.getSelectedItem() + "_" + yearChoiceDropdown.getSelectedItem());
            if (isInfo) {
                tableName = tableName + "_Info";
            }

            try {
                if (creditUnionName[0] == "all") {
                    rs = database.executeQuery("SELECT * FROM " + tableName + " ORDER BY \"" + orderedBy + "\" " + ascOrDesc);
                } else {
                    //build query
                    String query = "Select * FROM " + tableName + " WHERE ";
                    if (isInfo) {
                        query = query + "[CU_NAME] in ('";
                    } else {
                        query = query + "[Credit Union Name] in ('";
                    }
                    for (int i = 0; i < creditUnionName.length; i++) {

                        creditUnionName[i] = creditUnionName[i].replace("'", "''");

                        query = query + creditUnionName[i] + "'";

                        if (creditUnionName.length - 1 != i) {
                            query = query + ", '";
                        }

                    }
                    ;
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

        mainPanel = new javax.swing.JPanel();
        yearChoiceDropdown = new java.awt.Choice();
        quarterChoiceDropdown = new java.awt.Choice();
        getDataFromChoiceList = new javax.swing.JButton();
        getInfoFromChoiceList = new javax.swing.JButton();
        graphFromChoiceList = new javax.swing.JButton();
        deselectAll = new javax.swing.JButton();
        creditUnionLabel = new java.awt.Label();
        quarterLabel = new java.awt.Label();
        yearLabel = new java.awt.Label();
        creditUnionChoiceList = new java.awt.List();
        resultsLabel = new java.awt.Label();
        orderedByLabel = new java.awt.Label();
        ascOrDescLabel = new java.awt.Label();
        resultsScrollPane = new javax.swing.JScrollPane();
        getDataFromResults = new javax.swing.JButton();
        graphFromResults = new javax.swing.JButton();
        getInfoFromResults = new javax.swing.JButton();
        exportFileButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        yearChoiceDropdown.setName(""); // NOI18N

        getDataFromChoiceList.setText("Get Data");
        getDataFromChoiceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDataFromChoiceListActionPerformed(evt);
            }
        });

        getInfoFromChoiceList.setText("Get Info");
        getInfoFromChoiceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getInfoFromChoiceListActionPerformed(evt);
            }
        });

        graphFromChoiceList.setText("Graph");
        graphFromChoiceList.setToolTipText("");
        graphFromChoiceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphFromChoiceListActionPerformed(evt);
            }
        });

        deselectAll.setText("Deselect All");
        deselectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectAllActionPerformed(evt);
            }
        });

        creditUnionLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        creditUnionLabel.setText("Credit Union");

        quarterLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        quarterLabel.setText("Quarter");

        yearLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        yearLabel.setText("Year");

        creditUnionChoiceList.setMultipleMode(true);

        resultsLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        resultsLabel.setText("RESULTS");

        orderedByLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        orderedByLabel.setText("ORDERED BY Column Name");

        ascOrDescLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ascOrDescLabel.setText("IN Asc ORDER");

        getDataFromResults.setText("Get Data");
        getDataFromResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDataFromResultsActionPerformed(evt);
            }
        });

        graphFromResults.setText("Graph");
        graphFromResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphFromResultsActionPerformed(evt);
            }
        });

        getInfoFromResults.setText("Get Info");
        getInfoFromResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getInfoFromResultsActionPerformed(evt);
            }
        });

        exportFileButton.setText("Export to File");
        exportFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(0, 117, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(resultsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1065, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(orderedByLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ascOrDescLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(34, 34, 34)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(getDataFromResults, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(getInfoFromResults, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(graphFromResults, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(exportFileButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(47, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGap(48, 48, 48)
                                        .addComponent(yearLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(130, 130, 130)
                                        .addComponent(quarterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(yearChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(quarterChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(125, 125, 125)
                                .addComponent(creditUnionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(368, 368, 368)
                                .addComponent(creditUnionChoiceList, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(graphFromChoiceList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(getInfoFromChoiceList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(getDataFromChoiceList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deselectAll, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        .addGap(353, 353, 353))))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(594, 594, 594)
                .addComponent(resultsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(quarterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(quarterChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearChoiceDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(creditUnionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(getDataFromChoiceList)
                                .addGap(11, 11, 11)
                                .addComponent(getInfoFromChoiceList)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(graphFromChoiceList)
                                .addGap(8, 8, 8)
                                .addComponent(deselectAll))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(creditUnionChoiceList, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(resultsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderedByLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ascOrDescLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(getDataFromResults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(getInfoFromResults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(graphFromResults)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exportFileButton)))
                .addGap(35, 35, 35))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void getDataFromResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getDataFromResultsActionPerformed
        ascOrDesc = "ASC";
        orderedBy = "Credit Union Name";
        int[] row = table.getSelectedRows();
        String[] creditUnionName = new String[row.length];
        if (row.length != 0) {
            for (int i = 0; i < row.length; i++) {
                if (isInfo) {
                    creditUnionName[i] = (String) table.getValueAt(row[i], 4);
                } else {
                    creditUnionName[i] = (String) table.getValueAt(row[i], 0);
                }
            }
        }
        isInfo = false;
        int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
        int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();

        queryDatabase(orderedBy, ascOrDesc, creditUnionName);

        resultsScrollPane.getHorizontalScrollBar().setValue(horizValue);
        resultsScrollPane.getVerticalScrollBar().setValue(vertValue);
    }//GEN-LAST:event_getDataFromResultsActionPerformed

    private void getInfoFromResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getInfoFromResultsActionPerformed
        ascOrDesc = "ASC";
        orderedBy = "CU_NAME";
        int[] row = table.getSelectedRows();
        String[] creditUnionName = new String[row.length];
        if (row.length != 0) {
            for (int i = 0; i < row.length; i++) {
                if (isInfo) {
                    creditUnionName[i] = (String) table.getValueAt(row[i], 4);
                } else {
                    creditUnionName[i] = (String) table.getValueAt(row[i], 0);
                }
            }
        }
        isInfo = true;
        int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
        int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();

        queryDatabase(orderedBy, ascOrDesc, creditUnionName);

        resultsScrollPane.getHorizontalScrollBar().setValue(horizValue);
        resultsScrollPane.getVerticalScrollBar().setValue(vertValue);
    }//GEN-LAST:event_getInfoFromResultsActionPerformed

    private void exportFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportFileButtonActionPerformed
        //bring up file chooser
        FileChooser chooser = new FileChooser();

        //get chosen path and save the variable
        String path = chooser.getPath();
        path = path.replace("\\", "/");
        System.out.println(path);

        try {
            //create the file
            SpreadSheet spreadSheet = new SpreadSheet(path);
            spreadSheet.populateSpreadSheet(table);

            //write data to file
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exportFileButtonActionPerformed

    private void getDataFromChoiceListActionPerformed(java.awt.event.ActionEvent evt) {
        ascOrDesc = "ASC";
        orderedBy = "Credit Union Name";
        String[] creditUnionName = creditUnionChoiceList.getSelectedItems();
        isInfo = false;
        int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
        int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();

        queryDatabase(orderedBy, ascOrDesc, creditUnionName);

        resultsScrollPane.getHorizontalScrollBar().setValue(horizValue);
        resultsScrollPane.getVerticalScrollBar().setValue(vertValue);
    }

    private void getInfoFromChoiceListActionPerformed(java.awt.event.ActionEvent evt) {
        ascOrDesc = "ASC";
        orderedBy = "CU_NAME";
        String[] creditUnionName = creditUnionChoiceList.getSelectedItems();
        isInfo = true;
        int horizValue = resultsScrollPane.getHorizontalScrollBar().getValue();
        int vertValue = resultsScrollPane.getVerticalScrollBar().getValue();

        queryDatabase(orderedBy, ascOrDesc, creditUnionName);

        resultsScrollPane.getHorizontalScrollBar().setValue(horizValue);
        resultsScrollPane.getVerticalScrollBar().setValue(vertValue);
    }

    private void graphFromChoiceListActionPerformed(java.awt.event.ActionEvent evt) {

        String[] creditUnionName = creditUnionChoiceList.getSelectedItems();
        graphCreditUnions(creditUnionName);
    }

    private void deselectAllActionPerformed(java.awt.event.ActionEvent evt) {
        for (int i = 0; i < creditUnionChoiceList.getItemCount(); i++) {
            creditUnionChoiceList.deselect(i);
        }
    }

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

    private void graphFromResultsActionPerformed(java.awt.event.ActionEvent evt) {
        int[] row = table.getSelectedRows();
        String[] creditUnionNameArray = new String[row.length];
        if (row.length != 0) {
            for (int i = 0; i < row.length; i++) {
                if (isInfo) {
                    creditUnionNameArray[i] = (String) table.getValueAt(row[i], 4);
                } else {
                    creditUnionNameArray[i] = (String) table.getValueAt(row[i], 0);
                }
            }
            graphCreditUnions(creditUnionNameArray);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label ascOrDescLabel;
    private java.awt.List creditUnionChoiceList;
    private java.awt.Label creditUnionLabel;
    private javax.swing.JButton deselectAll;
    private javax.swing.JButton exportFileButton;
    private javax.swing.JButton getDataFromChoiceList;
    private javax.swing.JButton getDataFromResults;
    private javax.swing.JButton getInfoFromChoiceList;
    private javax.swing.JButton getInfoFromResults;
    private javax.swing.JButton graphFromChoiceList;
    private javax.swing.JButton graphFromResults;
    private javax.swing.JPanel mainPanel;
    private java.awt.Label orderedByLabel;
    private java.awt.Choice quarterChoiceDropdown;
    private java.awt.Label quarterLabel;
    private java.awt.Label resultsLabel;
    private javax.swing.JScrollPane resultsScrollPane;
    private java.awt.Choice yearChoiceDropdown;
    private java.awt.Label yearLabel;
    // End of variables declaration//GEN-END:variables
}
