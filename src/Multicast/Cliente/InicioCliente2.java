package Multicast.Cliente;

import TCP.Cliente.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class InicioCliente2 extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField nombre;

    public InicioCliente2() {
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
                InetAddress grupo = InetAddress.getByName("localhost"); // Dirección del grupo multicast
                int puerto = 1234;

                String accion = "/User";
                byte[] datos = accion.getBytes();
                DatagramPacket peticion = new DatagramPacket(datos, datos.length, grupo, puerto);
                sCliente.send(peticion);

                String texto = nombre.getText();
                datos = texto.getBytes();
                peticion = new DatagramPacket(datos, datos.length, grupo, puerto);
                sCliente.send(peticion);

                byte[] buffer = new byte[1024];
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
                sCliente.receive(respuesta);

                String mensaje = new String(respuesta.getData(), 0, respuesta.getLength()).trim();
                int res = Integer.parseInt(mensaje);

                if (res == -1) {
                    JOptionPane.showMessageDialog(null, "El nombre ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    System.out.println(res);
                    Cliente2 dialog = new Cliente2(sCliente,peticion,texto);
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
        InicioCliente2 dialog = new InicioCliente2();
        dialog.setDefaultCloseOperation(2);
        dialog.pack();
        dialog.setVisible(true);

    }
}