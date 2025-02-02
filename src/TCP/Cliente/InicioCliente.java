package TCP.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class InicioCliente extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField nombre;
    private JButton buttonCancel;

    public InicioCliente() {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void onOK() {
        if (!nombre.getText().isEmpty()) {
            try {
                Socket sCliente = new Socket("localhost", 22222);
                DataOutputStream dos = new DataOutputStream(sCliente.getOutputStream());
                DataInputStream dis = new DataInputStream(sCliente.getInputStream());


                dos.writeUTF(nombre.getText());


                int res = dis.readInt();


                if(res == -1){
                    JOptionPane.showMessageDialog(null, "El nombre ya esta en uso", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    Cliente dialog = new Cliente(sCliente);
                    dialog.setDefaultCloseOperation(2);
                    dialog.pack();
                    dialog.setVisible(true);
                    new Thread(dialog).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ingrese un nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        InicioCliente dialog = new InicioCliente();
        dialog.setDefaultCloseOperation(2);
        dialog.pack();
        dialog.setVisible(true);

    }
}
