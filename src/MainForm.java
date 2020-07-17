import javax.swing.*;

public class MainForm {
    private JPanel mainPanel;
    public JLabel priceLabel;
    static BinanceHandler binanceHandler = new BinanceHandler();



    public static void main(String[] args) {

        JFrame frame = new JFrame("<class name>");

        frame.setContentPane(new MainForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(720,480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);



        new LoginDialog().DisplayDialog(binanceHandler);
        new DataHandler();

    }


    void UpdatePrice(){
        priceLabel.setText(binanceHandler.syncRequestClient.getMarkPrice("BTCUSDT").toString());
    }



}
