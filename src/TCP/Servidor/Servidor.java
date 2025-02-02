package TCP.Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
    public static void main(String[] args) {
        int puerto = 22222;

            try {
                ArrayList<Hilo> usuarios = new ArrayList<>();
                ServerSocket servidor = new ServerSocket(puerto);
                System.out.println("Escuchando en el puerto " + puerto);
                while (true) {
                    Socket cliente = servidor.accept();
                    System.out.println("SE CONECTO EL CLIENTE\n\n");
                    Hilo run = new Hilo(cliente,usuarios);
                    usuarios.add(run);
                    Thread hilo = new Thread(run);
                    hilo.start();
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}
