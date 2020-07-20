import com.binance.client.model.market.AggregateTrade;
import org.jfree.data.xy.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;


public class DataHandler {

    MainForm mainForm;
    BinanceHandler binanceHandler;


    String marketPairName;
    public float currentPrice, lastPrice;
    public AggregateTrade lastTrade = new AggregateTrade();
    public XYSeriesCollection priceTimeDataset = new XYSeriesCollection();
    DefaultTableModel orderTableModel = new DefaultTableModel();
    DataHandler(MainForm instance,BinanceHandler bh) throws IOException {
        mainForm = instance;
        binanceHandler = bh;
        binanceHandler.dataHandler = this;

        mainForm.PopulateComboBox();
        binanceHandler.GetListeners("BTCUSDT");


        orderTableModel.addColumn("Time");
        orderTableModel.addColumn("Price");
        orderTableModel.addColumn("Qty");

        //start data update function
        Long mili = System.currentTimeMillis();
        while(true) {
            if(System.currentTimeMillis() > mili + 500) {
                marketPairName = mainForm.selectedMarketPair;
                UpdateDataVars(marketPairName);
                UpdateForm();
                mili = System.currentTimeMillis();
            }
            //
        }

    }

    void UpdateForm(){




        //set price label and change the color
        String color;
        if (currentPrice > lastPrice){
            color = "green";
        }else color = "red";
        mainForm.SetPriceLabelText(Float.toString(currentPrice),color);


        //updateChart
        HandleGraph();

        HandleCurrentOrdersTable();

    }

    void UpdateDataVars(String name) throws IOException {
        if (!name.equals(binanceHandler.selectedPair)) {
            binanceHandler.CloseListener();
            binanceHandler.GetListeners(name);
        }
        lastPrice = currentPrice;
        currentPrice = binanceHandler.currentPrice;






    }


    Date ConvertMiliSecToDate(long time){
        return new Date(time);
    }



    void HandleGraph(){
        if(priceTimeDataset.getSeriesCount()>0){
            if (mainForm.graphHandler.lineChart != null && mainForm.graphHandler.lineChart.getTitle().getText() == marketPairName&& priceTimeDataset.getSeries(0).getItemCount() >0) {
                mainForm.graphHandler.UpdateDataset(GetLastNDataPoints(GetXYSeriesFromDataset(priceTimeDataset,"Price"),30));

            } else {
                priceTimeDataset.removeAllSeries();
                binanceHandler.series.clear();
                mainForm.CreateChart(priceTimeDataset, marketPairName);

            }
        }
    }

    XYDataset GetLastNDataPoints(XYSeries data, int n){
        int numDataPoints = data.getItemCount();
        if(n>numDataPoints) n = numDataPoints;

        XYSeries nData = new XYSeries("ParsedData");

        for (int i = numDataPoints-1; i >= numDataPoints - n; i--) {

            nData.add(data.getX(i),data.getY(i));
        }
        XYSeries nRevData = new XYSeries("ReversedData");
        if(nData.getItemCount() < n) { n = nData.getItemCount();}

        n-=1;
        for (int i = n; i >=0 ; i--) {
            nRevData.add(nData.getX(i),nData.getY(i));
        }
        return  CreateXYDataset(nRevData);
    }

    XYSeriesCollection CreateXYDataset(XYSeries series){
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return  dataset;
    }

    static XYSeries GetXYSeriesFromDataset(XYSeriesCollection dataset, String seriesName){
        return dataset.getSeries(seriesName);
    }

    static public double GetStandardDev(XYDataset data){
        List<Double> values = new ArrayList();
        XYSeries series = ((XYSeriesCollection) data).getSeries(0);

        for (int i = 0; i < series.getItemCount(); i++) {
            values.add((double)series.getY(i));
        }


        double sum = 0f;
        double average = 0f;

        for (double value:values
        ) {

            sum +=value;

        }
        average = sum/values.size();
        sum =0;
        for (double value:values
        ) {
            sum += Math.pow((value - average),2);
        }
        double stdDev = Math.sqrt(sum/values.size());
        return stdDev;
    }
    static public double GetScale(XYDataset data){
        List<Double> values = new ArrayList();
        XYSeries series = ((XYSeriesCollection) data).getSeries(0);
        for (int i = 0; i < series.getItemCount(); i++) {
            values.add((double)series.getY(i));
        }
        double max = 0; double min = 99999999;
        double size = 0;
        for (double num:values
             ) {
            if(num > max) max = num;
            if(num < min) min = num;
            if(max - num > num-min) size = Math.ceil(max-num);
            else size = Math.ceil(num-min);
        }


        return size + GetStandardDev(data);
    }

    public void HandlePastOrderTable(AggregateTrade lastTrade){


        Object[] data = new Object[3];

            data[0] = ConvertMiliSecToDate(lastTrade.getTime()).toString().split(" ")[3];
            data[1] = lastTrade.getPrice();
            data[2] = lastTrade.getQty();
            orderTableModel.insertRow(0,data);

            while(orderTableModel.getRowCount() >5){
                orderTableModel.removeRow(orderTableModel.getRowCount()-1);
            }



            mainForm.SetPastOrderTable(orderTableModel);

    }

    void HandleCurrentOrdersTable(){
        DefaultTableModel table = new DefaultTableModel();
        table.addColumn("Price");
        table.addColumn("Qty");
        table.setNumRows(5);
        DefaultTableModel table2 =  new DefaultTableModel();
        table2.addColumn("Price");
        table2.addColumn("Qty");
        table2.setNumRows(5);
        SortedMap<Double,Double> askList = new TreeMap<>();
        SortedMap<Double,Double> bidList = new TreeMap<>(Comparator.reverseOrder());


        for (Map.Entry<BigDecimal, BigDecimal> entry: binanceHandler.getAsks().entrySet()) {
            askList.put(entry.getKey().doubleValue(),entry.getValue().doubleValue());
        }
        for (Map.Entry<BigDecimal, BigDecimal> entry: binanceHandler.getBids().entrySet()) {
            bidList.put(entry.getKey().doubleValue(),entry.getValue().doubleValue());
        }



        Object[][] askData = new Object[5][2];
        Object[][] bidData = new Object[5][2];

        Object[] askKeys= askList.keySet().toArray();
        Object[] askValues= askList.values().toArray();
        Object[] bidKeys= bidList.keySet().toArray();
        Object[] bidValues= bidList.values().toArray();
        int i = 4;
        for (int j = 0; j < 5; j++) {


            askData[j][0] = askKeys[i];
            askData[j][1] = askValues[i];
            bidData[j][0] = bidKeys[j];
            bidData[j][1] = bidValues[j];
        i--;
        }

        table.setDataVector(askData, new String[]{"Price", "Qty"});
        table2.setDataVector(bidData,new String[]{"Price", "Qty"});

        mainForm.SetOpenOrderTable(table,table2);
    }




}
