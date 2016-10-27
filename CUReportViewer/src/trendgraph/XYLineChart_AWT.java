/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trendgraph;

import java.awt.Color;
import java.awt.BasicStroke;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import model.Database;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class XYLineChart_AWT extends JFrame {

    private int yearStart;
    private int yearEnd;
    private String[] creditUnionName;
    private String columnName;
    
    public XYLineChart_AWT(int yearStart, int yearEnd, String[] creditUnionName, String columnName ) throws SQLException {
        super("Graph");
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.yearStart = yearStart;
        this.yearEnd = yearEnd;
        this.creditUnionName = creditUnionName;
        this.columnName = columnName;
        
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                "CU Report",
                "Year (YYYY)", //X-axis
                columnName, //Y-axis (replace with columnName
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 800));   //(x, y)
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED); //can be GREEN, YELLOW, ETC.

        renderer.setSeriesStroke(0, new BasicStroke(3.0f)); //Font size
        
        renderer.setSeriesPaint(1, Color.BLUE); //can be GREEN, YELLOW, ETC.

        renderer.setSeriesStroke(1, new BasicStroke(3.0f)); //Font size
        
        renderer.setSeriesPaint(2, Color.GREEN); //can be GREEN, YELLOW, ETC.

        renderer.setSeriesStroke(2, new BasicStroke(3.0f)); //Font size
        
        renderer.setSeriesPaint(3, Color.yellow); //can be GREEN, YELLOW, ETC.

        renderer.setSeriesStroke(3, new BasicStroke(3.0f)); //Font size

        plot.setRenderer(renderer);
        setContentPane(chartPanel);
        pack();
            RefineryUtilities.centerFrameOnScreen(this);
            setVisible(true);
    }

    private XYDataset createDataset() throws SQLException {
        Database database = new Database();
        database.connect();
        final XYSeriesCollection dataset = new XYSeriesCollection();
        //need year ranges, credit unions, and column to graph
        //cycle through all of this for each credit union name
        //replace "MYCOM" with the variable holding the credit union name
        for (int j = 0; j < creditUnionName.length; j++){
        final XYSeries cuName = new XYSeries(creditUnionName[j]);
        for (int i = yearStart; i <= yearEnd; i++) {
            int x = 1;
            creditUnionName[j] = creditUnionName[j].replace("'", "''");
            for (double y = i + .25; y <= i + 1; y += .25) {
                ResultSet rs = database.executeQuery("SELECT [" + columnName + "] FROM Q" + x + "_" + i + " WHERE [Credit Union Name]='" + creditUnionName[j] + "'");
                while (rs.next()) {
                    cuName.add(y, rs.getLong(columnName));
                }
                x += 1;
            }
        }
        try{
        dataset.addSeries(cuName);
        }catch(IllegalArgumentException ex){
            System.out.println("error: selected duplicate credit unions");
        }
        }
        database.closeconnections();
        return dataset;
    }
}
