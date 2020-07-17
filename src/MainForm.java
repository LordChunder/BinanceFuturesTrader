import javax.swing.*;
import java.awt.*;

public class MainForm {
    static JFrame frame;
    private JPanel mainPanel;
    private JLabel priceLabel;
    private JPanel dataPanel;
    private JToolBar toolbar;
    static BinanceHandler binanceHandler = new BinanceHandler();

    static public MainForm instance;


    MainForm(){

    }

   public void SetPriceLableText(String text){
       priceLabel.setText(text);
   }

    public static void main(String[] args) {
        instance = new MainForm();
        frame = new JFrame("<class name>");

        frame.setContentPane(instance.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.pack();
        frame.setSize(720,480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);



        new LoginDialog().DisplayDialog(binanceHandler);
        new DataHandler(instance,binanceHandler);

    }






}
