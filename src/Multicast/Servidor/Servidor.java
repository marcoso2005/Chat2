package Multicast.Servidor;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Servidor {
    private static final ArrayList<String> usuarios = new ArrayList<>();
    private static final String DIR = "230.0.0.1";
    private static final int PORT = 22222;

    public static void main(String[] args) {
        String[] historial = new String[10];
        int pos = 0;

        try {
            InetAddress group = InetAddress.getByName(DIR);
            MulticastSocket multi = new MulticastSocket(PORT);
            DatagramSocket servidor = new DatagramSocket(1234);


            multi.joinGroup(group);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);

                servidor.receive(peticion);
                String accion = new String(peticion.getData(), 0, peticion.getLength()).trim();
                String nombre = "";
                boolean dispo = true;

                if (accion.equals("/User")) {
                    servidor.receive(peticion);
                    nombre = new String(peticion.getData(), 0, peticion.getLength()).trim();

                    for (String usuario : usuarios) {
                        if (usuario != null && usuario.equalsIgnoreCase(nombre)) {
                            dispo = false;
                            break;
                        }
                    }
                }


                DatagramPacket envio;
                String texto;

                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (dispo) {
                    if (accion.equals("/User")) {
                        String cod = "0";
                        usuarios.add(nombre);
                        envio = new DatagramPacket(cod.getBytes(), cod.getBytes().length, peticion.getAddress(), peticion.getPort());
                        multi.send(envio);

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        String listaUsuarios = "";
                        for (String usuario : usuarios) {
                            listaUsuarios += usuario + ",";
                        }


                        envio = new DatagramPacket("/Nombres".getBytes(), "/Nombres".getBytes().length, group, PORT);
                        multi.send(envio);
                        envio = new DatagramPacket(listaUsuarios.getBytes(), listaUsuarios.getBytes().length, group, PORT);
                        multi.send(envio);

                        envio = new DatagramPacket("/Historial".getBytes(), "/Historial".getBytes().length, peticion.getAddress(), peticion.getPort());
                        servidor.send(envio);

                        System.out.println(usuarios);
                        texto = "BIENVENIDO " + nombre;
                        envio = new DatagramPacket(texto.getBytes(), texto.getBytes().length, peticion.getAddress(), peticion.getPort());
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
                        envio = new DatagramPacket("/HistorialFin".getBytes(), "/HistorialFin".getBytes().length, peticion.getAddress(), peticion.getPort());
                        servidor.send(envio);


                        for (String mensaje : historial) {
                            if (mensaje != null) {
                                envio = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length, peticion.getAddress(), peticion.getPort());
                                multi.send(envio);
                            }
                        }
                    } else if (accion.equals("!F14!")) {
                        servidor.receive(peticion);
                        nombre = new String(peticion.getData(), 0, peticion.getLength()).trim();
                        usuarios.remove(nombre);

                        String listaUsuarios = "";
                        for (String usuario : usuarios) {
                            listaUsuarios += usuario + ",";
                        }

                        envio = new DatagramPacket("/Nombres".getBytes(), "/Nombres".getBytes().length, group, PORT);
                        multi.send(envio);
                        envio = new DatagramPacket(listaUsuarios.getBytes(), listaUsuarios.getBytes().length, group, PORT);
                        multi.send(envio);


                    } else if(accion.contains("/Nombres")){

                    }else if(accion.equals("/Mensaje")){
                        servidor.receive(peticion);
                        texto = new String(peticion.getData(), 0, peticion.getLength()).trim();
                        envio = new DatagramPacket(texto.getBytes(), texto.getBytes().length, group, PORT);
                        multi.send(envio);

                        historial[pos] = texto;
                        if (pos + 1 < 10) {
                            pos++;
                        } else {
                            pos = 0;
                        }

                    }
                } else {
                    String cod = "-1";
                    envio = new DatagramPacket(cod.getBytes(), cod.getBytes().length, peticion.getAddress(), peticion.getPort());
                    multi.send(envio);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

