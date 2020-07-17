import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

public class MainForm {
    static JFrame frame;
    private JPanel mainPanel;
    private JLabel priceLabel;
    private JPanel dataPanel;
    private JComboBox<String> marketPairComboBox;
    static BinanceHandler binanceHandler = new BinanceHandler();

    static public MainForm instance;


    public String selectedMarketPair = "BTCUSDT";



    MainForm(){


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

        //get main frame
        frame = new JFrame("Binance Futures Helper");
        frame.setContentPane(instance.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(720,480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);



        new LoginDialog().DisplayDialog(binanceHandler);
        new DataHandler(instance,binanceHandler);

    }

     public void PopulateComboBox(){
        String[] pairs = {"BTCUSDT","ETHUSDT"};
        marketPairComboBox.setModel(new DefaultComboBoxModel<String>(pairs));
     }

}
