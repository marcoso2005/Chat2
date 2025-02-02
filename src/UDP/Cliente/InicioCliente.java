package UDP.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class InicioCliente extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField nombre;

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
                DatagramSocket sCliente = new DatagramSocket();
                InetAddress destino = InetAddress.getByName("localhost");


                byte [] datos = "/User".getBytes();

                DatagramPacket peticion = new DatagramPacket(datos, datos.length, destino, 22222);
                sCliente.send(peticion);

                String texto = nombre.getText();
                datos = texto.getBytes();
                peticion = new DatagramPacket(datos, datos.length, destino, 22222);
                sCliente.send(peticion);

                byte[] cadena = new byte[1024];
                DatagramPacket respuesta = new DatagramPacket(cadena, cadena.length);
                sCliente.receive(respuesta);
                String mensaje = new String(respuesta.getData(),0,respuesta.getLength());
                int res = Integer.parseInt(mensaje);


                if(res == -1){
                    JOptionPane.showMessageDialog(null, "El nombre ya esta en uso", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    Cliente dialog = new Cliente(sCliente,respuesta,texto);
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
