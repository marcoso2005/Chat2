package TCP.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Cliente extends JDialog implements Runnable{
    private JPanel contentPane;
    private JTextArea Conversacion;
    private JTextArea Usuarios;
    private JTextField Mensaje;
    private JButton enviar;
    private JButton buttonOK;
    private JButton buttonCancel;
    private Socket cliente;
    private BufferedWriter escribir;
    private BufferedReader leer;
    public Cliente(Socket cliente) {
        setContentPane(contentPane);
        setModal(false);
        getRootPane().setDefaultButton(buttonOK);



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
        try {
            escribir = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            leer = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }





        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                metodoAlCerrar();
            }
        });
    }

    private void metodoAlCerrar(){
        try {
            escribir.write("!F14!" + "\n");
            escribir.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onEnviar(){
        if(!Mensaje.getText().isEmpty()){
            try {
                escribir.write(Mensaje.getText() + "\n");
                escribir.flush();
                Mensaje.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    @Override
    public void run() {
        try {
            while(true){
                String texto = leer.readLine();

                if(texto.equalsIgnoreCase("Nombres")){
                    texto = leer.readLine();
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
