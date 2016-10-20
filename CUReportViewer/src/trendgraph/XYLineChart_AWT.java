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
import model.Database;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class XYLineChart_AWT extends ApplicationFrame {

    public XYLineChart_AWT(String applicationTitle, String chartTitle) throws SQLException {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Year (YYYY)", //X-axis
                "Total Assets ($)", //Y-axis
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 800));   //(x, y)
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.RED); //can be GREEN, YELLOW, ETC.

        renderer.setSeriesStroke(0, new BasicStroke(3.0f)); //Font size

        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

    private XYDataset createDataset() throws SQLException {
        Database database = new Database();
        database.connect();
        final XYSeries cuName = new XYSeries("MYCOM");
        for (int i = 2004; i < 2016; i++) {
            
            ResultSet rs = database.executeQuery("SELECT [Total Net Worth] FROM Q1_" + i + " WHERE [Credit Union Name]='MYCOM'");
            while (rs.next()){
            cuName.add(i, rs.getInt("Total Net Worth"));
            }
        }
        database.closeconnections();

        final XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(cuName);

        return dataset;
    }

    public static void main(String[] args) throws SQLException {
        XYLineChart_AWT chart = new XYLineChart_AWT("Year vs. Total Assets", "CU Report");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}
