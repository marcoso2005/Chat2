package TCP.Servidor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Hilo implements Runnable {
    private String nombre;
    private Socket cliente;
    private ArrayList<Hilo> usuarios;
    private static String historial[] = new String[10];
    private static int puntero = 0;


    public Hilo(Socket cliente, ArrayList<Hilo> usuarios) {
        this.cliente = cliente;
        this.usuarios = usuarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(cliente.getInputStream());
            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            boolean dispo;
            String n = dis.readUTF();
            dispo = true;

            System.out.println(usuarios.size());
            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i).getNombre() != null && usuarios.get(i).getNombre().equalsIgnoreCase(n)) {
                    dispo = false;
                    System.out.println(usuarios.get(i).getNombre());
                }
            }

            if (dispo) {
                nombre = n;
                dos.writeInt(0);
                try {
                    BufferedReader leer = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

                    String texto = "";
                    BufferedWriter e = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
                    enviarMensaje("Nombres");

                    for (int i = 0; i < usuarios.size(); i++) {
                        texto += usuarios.get(i).getNombre() + ",";
                    }

                    enviarMensaje(texto);

                    e.write("BIENVENIDO " + nombre + "\n");
                    e.flush();

                    for (int i = 0; i < historial.length; i++){
                        if(historial[puntero] != null){
                            e.write(historial[puntero] + "\n");
                            e.flush();
                        }
                        if(puntero+1 < 10){
                            puntero++;
                        }else{
                            puntero = 0;
                        }
                    }


                    while (!texto.equals("!F14!")) {
                        texto = leer.readLine();
                        if(!texto.equals("!F14!")) {
                            enviarMensaje(texto);
                            System.out.println(puntero);
                            historial[puntero] = texto;
                            if(puntero+1 < 10){
                                puntero++;
                            }else{
                                puntero = 0;
                            }
                        }
                    }
                } catch (EOFException ex) {
                }
            } else {
                dos.writeInt(-1);
            }

            usuarios.remove(this);
            String texto = "";
            BufferedWriter e = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            enviarMensaje("Nombres");

            for (int i = 0; i < usuarios.size(); i++) {
                texto += usuarios.get(i).getNombre() + ",";
            }

            enviarMensaje(texto);
            System.out.println("Adios");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void enviarMensaje(String texto) {
        BufferedWriter e;

        for (int i = 0; i < usuarios.size(); i++) {
            try {
                e = new BufferedWriter(new OutputStreamWriter(usuarios.get(i).getCliente().getOutputStream()));
                e.write(texto + "\n");
                e.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
