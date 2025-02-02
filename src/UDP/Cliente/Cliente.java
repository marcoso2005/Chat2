package UDP.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Cliente extends JDialog implements Runnable{
        private JPanel contentPane;
        private JTextArea Conversacion;
        private JTextArea Usuarios;
        private JTextField Mensaje;
        private JButton enviar;
        private JButton buttonOK;
        private JButton buttonCancel;
        private DatagramSocket cliente;
        private DatagramPacket peticion;
        InetAddress destino;
        String nombre;

    public Cliente(DatagramSocket cliente, DatagramPacket peticion,String nombre) {
            setContentPane(contentPane);
            setModal(false);
            getRootPane().setDefaultButton(buttonOK);
            this.peticion = peticion;
            this.nombre = nombre;
        try {
            destino = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

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
            enviar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onEnviar();
                }
            });

            this.cliente = cliente;





            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    metodoAlCerrar();
                }
            });
        }

        private void metodoAlCerrar(){
            try {
                byte mensaje[] = "!F14!".getBytes();
                DatagramPacket enviar = new DatagramPacket(mensaje,0,mensaje.length,destino,22222);
                cliente.send(enviar);

                enviar = new DatagramPacket(nombre.getBytes(),0,nombre.length(),destino,22222);
                cliente.send(enviar);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void onEnviar(){

            if(!Mensaje.getText().isEmpty()){
                try {
                    byte mensaje[] = Mensaje.getText().getBytes();
                    DatagramPacket enviar = new DatagramPacket(mensaje,0,mensaje.length,destino,22222);
                    cliente.send(enviar);
                    Mensaje.setText("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void onOK() {

            dispose();
        }

        private void onCancel() {
            // add your code here if necessary
            dispose();
        }


        @Override
        public void run() {
            String texto;
            try {
                while(true){
                    cliente.receive(peticion);
                    texto = new String(peticion.getData(),0,peticion.getLength());

                    if(texto.equalsIgnoreCase("/Nombres")){
                        cliente.receive(peticion);
                        texto = new String(peticion.getData(),0,peticion.getLength());
                        texto = texto.replace(",","\n");
                        Usuarios.setText(texto);
                    }else{
                        Conversacion.setText(Conversacion.getText() + "\n\n" + texto);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


