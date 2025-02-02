package TresRayaOnline.Cliente;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Cliente extends JDialog {
    private JPanel contentPane;
    private JButton b00;
    private JButton b01;
    private JButton b02;
    private JButton b10;
    private JButton b11;
    private JButton b12;
    private JButton b20;
    private JButton b21;
    private JButton b22;
    private JLabel juego;
    private  ImageIcon cruz;
    private  ImageIcon circulo;
    private Socket cliente;
    private boolean jugador;




    public Cliente() {
        setContentPane(contentPane);
        setModal(false);

        b00.setName("0/0");
        b01.setName("0/1");
        b02.setName("0/2");
        b10.setName("1/0");
        b11.setName("1/1");
        b12.setName("1/2");
        b20.setName("2/0");
        b21.setName("2/1");
        b22.setName("2/2");

        try {
            cliente = new Socket("localhost",22222);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cruz = new ImageIcon(getClass().getClassLoader().getResource("TresRayaOnline/Fotos/cruz.png"));
        circulo = new ImageIcon(getClass().getClassLoader().getResource("TresRayaOnline/Fotos/circulo.png"));

        b00.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b00);
            }
        });

        b01.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b01);
            }
        });
        b02.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b02);
            }
        });
        b10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b10);
            }
        });
        b11.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b11);
            }
        });
        b12.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b12);
            }
        });
        b20.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b20);
            }
        });
        b21.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b21);
            }
        });
        b22.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                click(b22);
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

    private void click(JButton b){
        try {
            DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
            salida.writeUTF(b.getName().toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Cliente dialog = new Cliente();
        dialog.pack();
        dialog.setVisible(true);
        try {
            DataInputStream entrada = new DataInputStream(dialog.cliente.getInputStream());
            DataOutputStream salida = new DataOutputStream(dialog.cliente.getOutputStream());
            String accion = entrada.readUTF();
            dialog.jugador = entrada.readBoolean();

            if(dialog.jugador){
                dialog.juego.setText("Cruces");
            }else{
                dialog.juego.setText("Circulos");
            }

            String res;
            boolean ju;
            while(!accion.equals("/Fin")){
                accion = entrada.readUTF();
                if(accion.equals("/Correcto")) {
                    res = entrada.readUTF();
                    ju = entrada.readBoolean();

                    res(res,dialog,ju);

                    accion = entrada.readUTF();
                }else if(accion.equals("/Ocupado")){
                    JOptionPane.showMessageDialog(null, "La casilla esta llena","Ocupado",JOptionPane.ERROR_MESSAGE);
                }else if(accion.equals("/ERRTurno")){
                    JOptionPane.showMessageDialog(null, "No es tu turno","Error turno",JOptionPane.ERROR_MESSAGE);
                }
            }

            accion = entrada.readUTF();

            if(accion.equals("/Ganar")){
                JOptionPane.showMessageDialog(null, "Ganaste");
            }else if(accion.equals("/Perder")){
                salida.writeUTF("/Fin");
                JOptionPane.showMessageDialog(null, "Perdiste");
            }else{
                salida.writeUTF("/Fin");
                JOptionPane.showMessageDialog(null, "Empate");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);
    }

    public static void iconos(Cliente dialog,JButton b, boolean jugador){
        if(jugador){
            b.setIcon(dialog.cruz);
        }else{
            b.setIcon(dialog.circulo);
        }
    }

    public static void res(String res,Cliente dialog,boolean ju){
        switch (res){
            case "0/0":
                iconos(dialog,dialog.b00,ju);
                break;
            case "0/1":
                iconos(dialog,dialog.b01,ju);
                break;
            case "0/2":
                iconos(dialog,dialog.b02,ju);
                break;
            case "1/0":
                iconos(dialog,dialog.b10,ju);
                break;
            case "1/1":
                iconos(dialog,dialog.b11,ju);
                break;
            case "1/2":
                iconos(dialog,dialog.b12,ju);
                break;
            case "2/0":
                iconos(dialog,dialog.b20,ju);
                break;
            case "2/1":
                iconos(dialog,dialog.b21,ju);
                break;
            case "2/2":
                iconos(dialog,dialog.b22,ju);
                break;
        }
    }
}
