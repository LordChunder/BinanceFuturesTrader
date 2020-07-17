import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.CandlestickInterval;

import javax.swing.*;

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

        new MainForm();
    }
}
