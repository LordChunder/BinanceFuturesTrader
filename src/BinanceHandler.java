import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.api.client.*;


import java.io.Closeable;
import java.io.IOException;
import java.sql.Timestamp;

public class BinanceHandler {
    public RequestOptions options;
    public SyncRequestClient syncRequestClient;
    BinanceApiWebSocketClient client;
    DataHandler dataHandler;


    public float currentPrice = 0, maxValue = 0, minValue = 999999999 ;
    public Timestamp time = new Timestamp(System.currentTimeMillis());
    String selectedPair = "BTCUSDT";
    Closeable clientCloseable;

    void LoginAndInitialize(String api, String secretKey){
        options =  new RequestOptions();

        client = BinanceApiClientFactory.newInstance(api,secretKey).newWebSocketClient();



        syncRequestClient = SyncRequestClient.create(api, secretKey,
                options);

        if(api == "" || secretKey == ""){
            new PopupNoticiation().ShowNotification("Logged in without API");
        }


    }

    public void CloseListener() throws IOException {


            clientCloseable.close();
    }

    public void GetListeners(String name) {
        System.out.println("Stream made for "+name);
        selectedPair = name;
       clientCloseable= client.onAggTradeEvent(name.toLowerCase(), new BinanceApiCallback<AggTradeEvent>() {
            @Override
            public void onResponse(final AggTradeEvent response) {
                currentPrice= Float.parseFloat(response.getPrice());
                time = new Timestamp(response.getTradeTime());
                dataHandler.dataset.addValue(currentPrice,"Price",dataHandler.ParseTime(time));

                if(currentPrice > maxValue) maxValue=currentPrice;
                if(currentPrice < minValue) minValue = currentPrice;
            }

            @Override
            public void onFailure(final Throwable cause) {
                System.err.println("Web socket failed");
                cause.printStackTrace(System.err);
            }
        });

    }



}
