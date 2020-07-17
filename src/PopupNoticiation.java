import javax.swing.*;
import java.awt.event.*;

public class PopupNoticiation extends JDialog {
    private JPanel contentPane;
    private JLabel textLabel;
    private JButton buttonCancel;

    private PopupNoticiation instance;

    public PopupNoticiation() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        instance = this;

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
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void ShowNotification(String text){
        textLabel.setText(text);
        instance.pack();
        instance.setSize(250 ,150);
        instance.setLocationRelativeTo(null);
        instance.setVisible(true);



    }
}
