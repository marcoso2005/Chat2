package TresRayaOnline.Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
    public static boolean turno = true;
    static boolean fin = false;
    public static void main(String[] args) {
        try {
            ArrayList<Hilo> hilos = new ArrayList<>();
            ServerSocket servidor = new ServerSocket(22222);

            while(true){
                fin = false;
                Integer [][] tablero = new Integer[3][3];
                for (int i = 0; i < tablero.length; i++) {
                    for (int j = 0; j < tablero[i].length; j++) {
                        tablero[i][j] = 0;
                    }
                }

                Socket cliente = servidor.accept();
                Hilo hi = new Hilo(cliente,true,tablero,hilos);
                hilos.add(hi);
                Thread hilo = new Thread(hi);
                hilo.start();
                cliente = servidor.accept();
                Hilo h = new Hilo(cliente,false,tablero,hilos);
                hilos.add(h);

                h.run();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
