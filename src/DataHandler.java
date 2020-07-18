import org.jfree.data.category.DefaultCategoryDataset;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    MainForm mainForm;
    BinanceHandler binanceHandler;


    String marketPairName;
    public float currentPrice, lastPrice;

    public DefaultCategoryDataset dataset;

    DataHandler(MainForm instance,BinanceHandler bh) throws IOException {
        mainForm = instance;
        binanceHandler = bh;
        binanceHandler.dataHandler = this;
        dataset = new DefaultCategoryDataset();

        mainForm.PopulateComboBox();
        binanceHandler.GetListeners("BTCUSDT");


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
    }

    void UpdateDataVars(String name) throws IOException {
        if (!name.equals(binanceHandler.selectedPair)) {
            binanceHandler.CloseListener();
            dataset.clear();
            binanceHandler.maxValue = 0; binanceHandler.minValue = 999999999;
            binanceHandler.GetListeners(name);
        }
        lastPrice = currentPrice;
        currentPrice = binanceHandler.currentPrice;


        //updateChart


        if (mainForm.graphHandler.lineChart != null && mainForm.graphHandler.lineChart.getTitle().getText() == marketPairName&& dataset.getColumnCount() >0) {
            mainForm.graphHandler.UpdateDataset(GetLastNDataPoints(dataset,30));
            Double[] scale = GetGraphScale(dataset);
            mainForm.graphHandler.ScaleGraph(scale[0],scale[1]);
        } else {
            mainForm.CreateChart(dataset, marketPairName);

        }

    }


    String ParseTime(Timestamp time){
        String timeString = time.toString();
        timeString = timeString.split(" ")[1];
        timeString = timeString.split("\\.")[0];

        return timeString;
    }


    DefaultCategoryDataset GetLastNDataPoints(DefaultCategoryDataset data, int n){
        int numDataPoints = data.getColumnCount();
        if(n>numDataPoints) n = numDataPoints;

        DefaultCategoryDataset nData = new DefaultCategoryDataset();
        for (int i = numDataPoints-1; i >= numDataPoints - n; i--) {

            nData.addValue(data.getValue(0,i),"Price",data.getColumnKey(i));
        }
        DefaultCategoryDataset nRevData = new DefaultCategoryDataset();
        if(nData.getColumnCount() < n) { n = nData.getColumnCount();}

        n-=1;
        for (int i = n; i >=0 ; i--) {
            nRevData.addValue(nData.getValue(0,i),"Price",nData.getColumnKey(i));
        }
        return  nRevData;
    }
    Double[] GetGraphScale(DefaultCategoryDataset data){
        Double[] scale = {0.0,0.0};
        List<Double> values = new ArrayList<Double>();

        for (int i = 0; i < data.getColumnCount(); i++) {
            values.add((Double)(data.getValue(0,i)));
        }

        Double last = 0.0;
        Double average = 0.0;
        Double sum = 0.0;
        for (Double value:values
             ) {
            if(value > last) scale[1] = value;
            if(value < last) scale[0] = value;
            sum +=value;
            last = value;
        }
        average = sum/values.size();
        sum =0.0;
        for (Double value:values
           ) {
            sum += Math.pow((value - average),2);
        }
        Double stdDev = Math.sqrt(sum/values.size());
        stdDev/=100;
        scale[0] = scale[0] - scale[0]*stdDev;  scale[1] = scale[1] + scale[1]*stdDev;
        return scale;
    }




}
