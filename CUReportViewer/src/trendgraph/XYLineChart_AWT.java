/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trendgraph;

import java.awt.Color; 
import java.awt.BasicStroke; 
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

public class XYLineChart_AWT extends ApplicationFrame 
{
   public XYLineChart_AWT( String applicationTitle, String chartTitle )
   {
      super(applicationTitle);
      JFreeChart xylineChart = ChartFactory.createXYLineChart(
         chartTitle ,
         "Year (YYYY)" , //X-axis
         "Total Assets ($)" , //Y-axis
         createDataset() ,
         PlotOrientation.VERTICAL ,
         true , true , false);
         
      ChartPanel chartPanel = new ChartPanel( xylineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 1000 , 800 ) );   //(x, y)
      final XYPlot plot = xylineChart.getXYPlot( );
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
      
      renderer.setSeriesPaint( 0 , Color.RED ); //can be GREEN, YELLOW, ETC.

      renderer.setSeriesStroke( 0 , new BasicStroke( 3.0f ) ); //Font size
 
      plot.setRenderer( renderer ); 
      setContentPane( chartPanel ); 
   }
   
   private XYDataset createDataset( )
   {
      final XYSeries cuName = new XYSeries( "CU Name" );          
      cuName.add( 2004 , 1000 );    //can add more points if necessary                
      cuName.add( 2016 , 10000 );          
        
      final XYSeriesCollection dataset = new XYSeriesCollection( );          
      
      dataset.addSeries( cuName );          
    
      return dataset;
   }

   public static void main( String[ ] args ) 
   {
      XYLineChart_AWT chart = new XYLineChart_AWT("Year vs. Total Assets", "CU Report");
      chart.pack( );          
      RefineryUtilities.centerFrameOnScreen( chart );          
      chart.setVisible( true ); 
   }
}
