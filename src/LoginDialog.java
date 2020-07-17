import javax.swing.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox checkBox;


    String apiKey = "MVi3iQizEghuc9vDZaNWEgXBgNlJbkA3RW5rgAYIlHhhC3LM6T5Mf60115uKQ5im";
    String secretKey = "S3vaz0Cvtw3psovZ5TTDd4lsGyP5vxvuYg7tPdt7AUYTn7c7wki3axfL7Xpsd3Xf";

    static BinanceHandler binanceHandler;
    public LoginDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

            }
        });
    }

    private void onOK() {
        // add your code here
        if(checkBox.isSelected()){
            binanceHandler.LoginAndInitialize(apiKey,secretKey);
        }


        dispose();
    }

    private void onCancel() {
        // add your code here if necessary

        dispose();
    }

    public static void DisplayDialog(BinanceHandler handler) {
        binanceHandler = handler;
        LoginDialog dialog = new LoginDialog();
        dialog.pack();
        dialog.setSize(480,320);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);


    }
}
