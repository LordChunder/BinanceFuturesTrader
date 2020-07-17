import java.io.IOException;

public class DataHandler {

    MainForm mainForm;
    BinanceHandler binanceHandler;


    String marketPairName;
    public float currentPrice, lastPrice;

    DataHandler(MainForm instance,BinanceHandler bh) throws IOException {
        mainForm = instance;
        binanceHandler = bh;

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
        if(!name.equals(binanceHandler.selectedPair)){
            binanceHandler.CloseListener();
            binanceHandler.GetListeners(name);
        }
        lastPrice = currentPrice;
        currentPrice = binanceHandler.currentPrice;

    }






}
