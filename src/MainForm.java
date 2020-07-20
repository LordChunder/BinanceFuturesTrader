import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Properties;

public class MainForm {
    static JFrame frame;
    private JPanel mainPanel;
    private JLabel priceLabel;
    private JPanel settingsPanel;
    private JComboBox<String> marketPairComboBox;
    private JPanel graphPanel;
    private JPanel tradeDataPanel;
    private JTable askTable;
    private JTable bidTable;
    private JTable orderTable;
    GraphHandler graphHandler = new GraphHandler();

    static BinanceHandler binanceHandler = new BinanceHandler();
    static DataHandler dataHandler;

    static public MainForm instance;


    public String selectedMarketPair = "BTCUSDT";



    MainForm(){

        askTable.setBackground(Color.getColor("BBBBBB"));
        orderTable.setBackground(Color.getColor("BBBBBB"));
        bidTable.setBackground(Color.getColor("BBBBBB"));
        marketPairComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedMarketPair = marketPairComboBox.getSelectedItem().toString();

            }
        });
    }

   public void SetPriceLabelText(String text, String color){
       priceLabel.setText("<html><font color='"+color+"'>"+text+"</font></html>");
   }



    public static void main(String[] args) throws IOException {
        instance = new MainForm();
        Properties properties = new Properties();

        //get main frame
        frame = new JFrame("Binance Futures Helper");
        frame.setContentPane(instance.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(720,560);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        new LoginDialog().DisplayDialog(binanceHandler);

        new DataHandler(instance,binanceHandler);



    }

     public void PopulateComboBox(){
        String[] pairs = {"BTCUSDT","ETHUSDT"};
        marketPairComboBox.setModel(new DefaultComboBoxModel<String>(pairs));
     }

     public void SetOpenOrderTable(TableModel model, TableModel model2){
        askTable.setModel(model);
        bidTable.setModel(model2);

     }
     public  void SetPastOrderTable(TableModel model){

        orderTable.setModel(model);
     }




     public void CreateChart(XYDataset dataset, String name){
         graphHandler.CreateLineChart(graphPanel,name,dataset);

     }


}
