
import com.binance.api.client.*;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;

import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.client.*;

import com.binance.client.model.market.AggregateTrade;
import com.binance.client.model.market.OrderBook;
import org.jfree.data.xy.XYSeries;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class BinanceHandler {
    public RequestOptions options;
    public SyncRequestClient syncRequestClient;
    BinanceApiWebSocketClient client;
    DataHandler dataHandler;

    public XYSeries series = new XYSeries("Price");
    private static final String BIDS  = "BIDS";
    private static final String ASKS  = "ASKS";

    private long lastUpdateId;

    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;
    public Map<Long, AggregateTrade> aggTradesCache;


    public float currentPrice = 0;
    public Timestamp time = new Timestamp(System.currentTimeMillis());

    String selectedPair = "BTCUSDT";
    Closeable aggTradeClosable,candleStickClosable, orderBookClosable;
    OrderBook orderBook;
    void LoginAndInitialize(String api, String secretKey){
        options =  new RequestOptions();


        client = BinanceApiClientFactory.newInstance(api,secretKey).newWebSocketClient();

        syncRequestClient = SyncRequestClient.create(api, secretKey,
                options);



        if(api == "" || secretKey == ""){
            new PopupNoticiation().ShowNotification("Logged in without API");
        } else {
            try {
                new PopupNoticiation().ShowNotification("Logged in");
            } catch (Exception e) {
                new PopupNoticiation().ShowNotification("ERROR WITH API RESTART");
                e.printStackTrace();
            }
        }



    }

    void HandleDepthCache(String symbol){
        orderBook = syncRequestClient.getOrderBook(symbol.toLowerCase(),10);

        depthCache = new HashMap<>();
        lastUpdateId = orderBook.getLastUpdateId();

        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
        for (com.binance.client.model.market.OrderBookEntry ask : orderBook.getAsks()) {
            asks.put(ask.getPrice(), ask.getQty());
        }
        depthCache.put(ASKS, asks);

        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
        for (com.binance.client.model.market.OrderBookEntry bid : orderBook.getBids()) {
            bids.put(bid.getPrice(), bid.getQty());
        }
        depthCache.put(BIDS, bids);
    }
    void HandleAggTradeCache(String symbol){
        BinanceApiRestClient client = BinanceApiClientFactory.newInstance().newRestClient();
        System.out.println(symbol);
        List<AggregateTrade> aggTrades = syncRequestClient.getAggregateTrades(symbol,null,null,null,null);

        this.aggTradesCache = new HashMap<>();
        for (AggregateTrade aggTrade : aggTrades) {
            aggTradesCache.put(aggTrade.getId(), aggTrade);
        }
    }

    public void CloseListener() throws IOException {

            orderBookClosable.close();
            aggTradeClosable.close();
    }

    public void GetListeners(String name) {
        System.out.println("Stream made for "+name);
        selectedPair = name;

        HandleDepthCache(name.toLowerCase());
        HandleAggTradeCache(name);
        aggTradeClosable = client.onAggTradeEvent(name.toLowerCase(), new BinanceApiCallback<>() {
           @Override
           public void onResponse(final AggTradeEvent response) {
               currentPrice = Float.parseFloat(response.getPrice());
               time = new Timestamp(response.getTradeTime());

               series.add(time.getTime(),currentPrice);
               dataHandler.priceTimeDataset = dataHandler.CreateXYDataset(series);


               Long aggregatedTradeId = response.getAggregatedTradeId();
               AggregateTrade updateAggTrade = aggTradesCache.get(aggregatedTradeId);
               if (updateAggTrade == null) {
                   // new agg trade
                   updateAggTrade = new AggregateTrade();
               }
               updateAggTrade.setId(aggregatedTradeId);
               updateAggTrade.setTime(response.getTradeTime());
               updateAggTrade.setPrice(new BigDecimal(response.getPrice()));
               updateAggTrade.setQty(new BigDecimal(response.getQuantity()));
               updateAggTrade.setFirstId(response.getFirstBreakdownTradeId());
               updateAggTrade.setLastId(response.getLastBreakdownTradeId());
               updateAggTrade.setIsBuyerMaker(response.isBuyerMaker());

               // Store the updated agg trade in the cache
               dataHandler.HandlePastOrderTable(updateAggTrade);
               aggTradesCache.put(aggregatedTradeId, updateAggTrade);

           }

           @Override
           public void onFailure(final Throwable cause) {
               System.err.println("Web socket failed");
               cause.printStackTrace(System.err);
           }
       });


      orderBookClosable=  client.onDepthEvent(name.toLowerCase(), (DepthEvent response) -> {
          if (response.getFinalUpdateId() > lastUpdateId) {
              lastUpdateId = response.getFinalUpdateId();
              updateOrderBook(getAsks(), response.getAsks());
              updateOrderBook(getBids(), response.getBids());

          }

        });


       /*candleStickClosable = client.onCandlestickEvent(name.toLowerCase(), CandlestickInterval.ONE_MINUTE, new BinanceApiCallback<>() {

           @Override
           public void onResponse(CandlestickEvent response) {
               System.out.println(response.toString());
           }


       });
        */
    }

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return depthCache.get(ASKS);
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return depthCache.get(BIDS);
    }
    private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this level
                lastOrderBookEntries.remove(price);
            } else {
                lastOrderBookEntries.put(price, qty);
            }
        }
    }



}
