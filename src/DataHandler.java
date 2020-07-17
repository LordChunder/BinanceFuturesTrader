import java.sql.Time;

public class DataHandler {

    MainForm mainForm;
    BinanceHandler binanceHandler;

    float lastPrice;

    DataHandler(MainForm instance,BinanceHandler bh){
        mainForm = instance;
        binanceHandler = bh;

        Long mili = System.currentTimeMillis();
        while(true) {
            if(System.currentTimeMillis() > mili + 500) {

                UpdateForm();
                mili = System.currentTimeMillis();
            }
            //
        }
    }

    void UpdateForm(){
        System.out.println("H");
        mainForm.SetPriceLableText(Float.toString(binanceHandler.GetLastPrice("BTCUSDT")));
    }



}
