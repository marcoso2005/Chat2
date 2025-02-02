package UDP.Servidor;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class Servidor {
    private static HashMap<String, DatagramPacket> usuarios = new HashMap<>();

    public static void main(String[] args) {
        String historial []= new String[10];
        int pos = 0;
        try {
            DatagramSocket servidor = new DatagramSocket(22222);
            while (true) {
                byte[] cadena = new byte[1024];
                DatagramPacket peticion = new DatagramPacket(cadena, cadena.length);
                servidor.receive(peticion);
                System.out.println("Se conecto un cliente\n\n");

                String nombre = "";
                boolean dispo = true;
                String accion = new String(peticion.getData(), 0, peticion.getLength());
                if (accion.equals("/User")) {
                    servidor.receive(peticion);
                    nombre = new String(peticion.getData(), 0, peticion.getLength());
                    for (String usuario : usuarios.keySet()) {
                        if (usuario != null && usuario.equalsIgnoreCase(nombre)) {
                            dispo = false;
                        }
                    }
                }

                String texto;
                if (dispo) {
                    try {
                        if (accion.equals("/User")) {
                            String cod = "0";
                            usuarios.put(nombre,peticion);
                            DatagramPacket envio = new DatagramPacket(cod.getBytes(), 0, cod.getBytes().length, peticion.getAddress(), peticion.getPort());
                            servidor.send(envio);
                            texto = "";
                            enviarMensaje(servidor, "/Nombres");

                            for (String clave: usuarios.keySet()) {
                                texto += clave + ",";
                            }

                            enviarMensaje(servidor, texto);

                            texto = "BIENVENIDO " + nombre;
                            envio = new DatagramPacket(texto.getBytes(), 0, texto.getBytes().length, peticion.getAddress(), peticion.getPort());
                            servidor.send(envio);

                            for (int i = 0; i < historial.length; i++) {
                                if (historial[pos] != null) {
                                    envio = new DatagramPacket(historial[pos].getBytes(), 0, historial[pos].getBytes().length, peticion.getAddress(), peticion.getPort());
                                    servidor.send(envio);
                                }
                                if (pos + 1 < 10) {
                                    pos++;
                                } else {
                                    pos = 0;
                                }
                            }
                        } else if(accion.equals("!F14!")){
                            servidor.receive(peticion);
                            nombre = new String(peticion.getData(), 0, peticion.getLength());
                            usuarios.remove(nombre);
                            enviarMensaje(servidor, "/Nombres");
                            texto = "";
                            for (String clave: usuarios.keySet()) {
                                texto += clave + ",";
                            }

                            enviarMensaje(servidor, texto);
                            System.out.println("ADIOOOOS");
                        }else {
                            texto = new String(peticion.getData(), 0, peticion.getLength());
                            enviarMensaje(servidor, texto);
                            System.out.println(texto);
                            historial[pos] = texto;
                            if (pos + 1 < 10) {
                                pos++;
                            } else {
                                pos = 0;
                            }
                        }
                    } catch (EOFException ex) {
                    }
                } else {
                    String cod = "-1";
                    DatagramPacket envio = new DatagramPacket(cod.getBytes(), 0, cod.getBytes().length, peticion.getAddress(), peticion.getPort());
                    servidor.send(envio);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void enviarMensaje(DatagramSocket servidor, String texto) {
        DatagramPacket envio;

        System.out.println(usuarios.size());
        for (String clave: usuarios.keySet()) {
            try {
                envio = new DatagramPacket(texto.getBytes(), 0, texto.getBytes().length, usuarios.get(clave).getAddress(), usuarios.get(clave).getPort());
                servidor.send(envio);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
