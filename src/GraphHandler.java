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
import org.jfree.data.xy.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GraphHandler {
    JFreeChart lineChart;


    public void CreateLineChart(JPanel panel, String pair, XYDataset dataset){
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        lineChart = ChartFactory.createXYLineChart(
                pair,
                "Time","Price",
                dataset,
                PlotOrientation.VERTICAL,
                false,false,false);


        ChartPanel chartPanel = new ChartPanel( lineChart );

        lineChart.getXYPlot().getDomainAxis().setTickLabelsVisible(false);
        lineChart.setBackgroundPaint(Color.getColor("BBBBBB"));
        panel.add(chartPanel);
        panel.validate();
    }

    public void UpdateDataset(XYDataset dataset){
        lineChart.getXYPlot().setDataset(dataset);

        //lineChart.getXYPlot().getRangeAxis().setFixedAutoRange(DataHandler.GetScale(dataset));
        ScaleGraph(dataset);

    }

    public void ScaleGraph(XYDataset dataset){

        List<Double> values = new ArrayList();
        XYSeries series = ((XYSeriesCollection) dataset).getSeries(0);
        if(series.getItemCount() <=1) return;
        for (int i = 0; i < series.getItemCount(); i++) {
            values.add((double)series.getY(i));
        }
        double max = 0; double min = 99999999;

        for (double num:values
        ) {

            if(num > max) max = num;
            if(num < min) min = num;

        }


        lineChart.getXYPlot().getRangeAxis().setRange(min-min*.01,max+max*.01);

    }

}
