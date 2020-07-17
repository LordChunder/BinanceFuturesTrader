import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.market.PriceChangeTicker;

import javax.swing.*;
import java.util.List;

public class BinanceHandler {
    public RequestOptions options;
    public SyncRequestClient syncRequestClient;






    void LoginAndInitialize(String api, String secretKey){
        options =  new RequestOptions();


        syncRequestClient = SyncRequestClient.create("", "",
                options);

        if(api == "" || secretKey == ""){
            new PopupNoticiation().ShowNotification("Logged in without API");
        }


    }

    public float GetLastPrice(String name){
        List<PriceChangeTicker> price =  syncRequestClient.get24hrTickerPriceChange(name);
        return price.get(0).getLastPrice().floatValue();
    }
}
