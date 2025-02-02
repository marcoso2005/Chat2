package Multicast.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

    public class Cliente2 extends JDialog implements Runnable {
        private JPanel contentPane;
        private JTextArea Conversacion;
        private JTextArea Usuarios;
        private JTextField Mensaje;
        private JButton enviar;
        private JButton buttonOK;
        private JButton buttonCancel;
        private DatagramSocket cliente;
        private InetAddress destino;
        private String nombre;
        private static final int PUERTO_DIR = 1234;
        private static final int PUERTO_MUL = 22222;

        public Cliente2(DatagramSocket cliente, DatagramPacket peticion, String nombre) {
            setContentPane(contentPane);
            setModal(false);
            getRootPane().setDefaultButton(buttonOK);
            this.cliente = cliente;
            this.nombre = nombre;

            try {
                destino = InetAddress.getByName("230.0.0.1"); // Dirección multicast
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    metodoAlCerrar();
                }
            });

            contentPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    metodoAlCerrar();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            enviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onEnviar();
                }
            });
        }

        private void metodoAlCerrar() {
            try {
                InetAddress env = InetAddress.getByName("localhost");
                byte[] mensaje = "!F14!".getBytes();
                DatagramPacket enviar = new DatagramPacket(mensaje, mensaje.length, env, PUERTO_DIR);
                cliente.send(enviar);

                mensaje = nombre.getBytes();
                enviar = new DatagramPacket(mensaje, mensaje.length, env, PUERTO_DIR);
                cliente.send(enviar);

                cliente.close();
                dispose();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void onEnviar() {
            if (!Mensaje.getText().isEmpty()) {
                try {
                    InetAddress env = InetAddress.getByName("localhost");
                    String mensaje = Mensaje.getText();
                    DatagramPacket packet = new DatagramPacket("/Mensaje".getBytes(), "/Mensaje".length(), env, PUERTO_DIR);
                    cliente.send(packet);
                    packet = new DatagramPacket(mensaje.getBytes(), mensaje.length(), env, PUERTO_DIR);
                    cliente.send(packet);

                    Mensaje.setText("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void run() {
            try {
                MulticastSocket multi = new MulticastSocket(PUERTO_MUL);
                multi.joinGroup(destino);

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                cliente.receive(packet);
                String texto = new String(packet.getData(),0,packet.getLength());

                if(texto.equals("/Historial")){
                    while(!texto.equals("/HistorialFin")){
                        cliente.receive(packet);
                        texto = new String(packet.getData(),0,packet.getLength());
                        if(!texto.equals("/HistorialFin")){
                            Conversacion.append("\n\n" + texto);
                        }
                    }
                }


                while (true) {
                    multi.receive(packet);
                    texto = new String(packet.getData(), 0, packet.getLength());
                    if (texto.equals("/Nombres")) {
                        multi.receive(packet);
                        texto = new String(packet.getData(), 0, packet.getLength());
                        texto = texto.replace(",", "\n");
                        Usuarios.setText(texto);
                    } else {
                        Conversacion.append("\n\n" + texto);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
