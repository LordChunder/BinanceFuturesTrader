import com.binance.api.client.constant.BinanceApiConstants;

import javax.swing.*;
import javax.swing.text.Style;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JCheckBox checkBox;
    private JTextField apiTextField;
    private JTextField secretKeyTextField;
    static LoginDialog dialog;
    String fileName = "user.dat";

    String apiKey = "";
    String secretKey = "";

    static BinanceHandler binanceHandler;
    public LoginDialog() {
        dialog=this;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(checkBox.isSelected()) {
                    apiTextField.setEnabled(false);
                    secretKeyTextField.setEnabled(false);
                }else {
                    apiTextField.setEnabled(true);
                    secretKeyTextField.setEnabled(true);
                }
            }
        });

        // get user data populate textboxes with api if available
        try {
            ReadUserData(GetFileInputStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!apiKey.equals("")) {
            apiTextField.setText(apiKey);
            secretKeyTextField.setText(secretKey);

             checkBox.setSelected(false);
             apiTextField.setEnabled(true);
             secretKeyTextField.setEnabled(true);
        }
    }


    private void onOK() {
        // add your code here

        //login if anon is not selected otherwise api and secret is blank
        if(!checkBox.isSelected()){
            secretKey = secretKeyTextField.getText();
            apiKey = apiTextField.getText();

            binanceHandler.LoginAndInitialize(apiKey,secretKey);
            try  {
                binanceHandler.syncRequestClient.getAccountInformation();
            } catch (Exception e) {
                new PopupNoticiation().ShowNotification("Error check API information");
                e.printStackTrace();
                return;
            }
        } else {
            secretKey = ""; apiKey = "";
            binanceHandler.LoginAndInitialize(apiKey,secretKey);
        }

        //updateuserdata
        try {
            WriteUserData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
        System.exit(0);
    }

    public static void DisplayDialog( BinanceHandler handler) {
        binanceHandler = handler;
        dialog.pack();
        dialog.setSize(480,320);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    FileInputStream GetFileInputStream(String name){
        File yourFile = new File(name);
        try {
            yourFile.createNewFile(); // if file already exists will do nothing
            return new FileInputStream(yourFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    void ReadUserData(FileInputStream fileInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        List<String> out = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            out.add(line);   // add everything to StringBuilder
        }

        for (String string:out
             ) {
            String[] split =string.split("=");
            String key = split[0];
            String value = "";
            if(split.length>1) value = split[1];
            if(key.equals("API_KEY")) apiKey = value;
            else if(key.equals("SECRET_KEY")) secretKey = value;
        }
        reader.close();
        fileInputStream.close();
    }
    void WriteUserData() throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        bw.write("API_KEY="+apiKey);
        bw.newLine();
        bw.write("SECRET_KEY="+secretKey);

        bw.close();
        fos.close();
        System.out.println("YES");
    }







}
