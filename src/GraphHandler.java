import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GraphHandler {
    JFreeChart lineChart;


    public void CreateLineChart(JPanel panel, String pair, DefaultCategoryDataset dataset){
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        lineChart = ChartFactory.createLineChart(
                pair,
                "Time","Price",
                dataset,
                PlotOrientation.VERTICAL,
                false,false,false);


        ChartPanel chartPanel = new ChartPanel( lineChart );

        //chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        panel.add(chartPanel);
        panel.validate();
    }

    public void UpdateDataset(DefaultCategoryDataset dataset){
        ((CategoryPlot)lineChart.getPlot()).setDataset(dataset);
    }

    public void ScaleGraph(float min, float max){
        if(min !=999999999 && max !=0) {
            lineChart.getCategoryPlot().getRangeAxis().setRange(min - min * .01f, max + max * .01f);
        }
    }

}
