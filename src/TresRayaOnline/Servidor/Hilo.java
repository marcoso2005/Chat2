package TresRayaOnline.Servidor;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Hilo implements Runnable{
    private Socket cliente;
    private boolean jugador;
    private Integer [][] tablero;
    private ArrayList<Hilo> hilos;

    public Hilo(Socket cliente,boolean jugador, Integer[][] tablero,ArrayList<Hilo> hilos) {
        this.cliente = cliente;
        this.tablero = tablero;
        this.jugador = jugador;
        this.hilos = hilos;
    }

    @Override
    public void run() {
        try {
            DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
            DataInputStream posicion = new DataInputStream(cliente.getInputStream());
            salida.writeUTF("/Correcto");
            salida.writeBoolean(jugador);


            String pos;
            Integer sep [] = new Integer[2];
            boolean lleno = false;
            boolean ganar = false;
            while(!Servidor.fin){
                pos = posicion.readUTF();
                if(!pos.equals("/Fin")) {
                    if (Servidor.turno == jugador) {
                        sep[0] = Integer.parseInt(pos.split("/")[0]);
                        sep[1] = Integer.parseInt(pos.split("/")[1]);

                        if (tablero[sep[0]][sep[1]] == 0) {
                            escribir("/Correcto");
                            if (jugador) {
                                tablero[sep[0]][sep[1]] = 1;
                            } else {
                                tablero[sep[0]][sep[1]] = 2;
                            }
                            escribir(pos);
                            escribir(jugador);
                            Servidor.turno = !Servidor.turno;
                            lleno = true;

                            int numVertical;
                            int numHorizontal;
                            for (int i = 0; i < tablero.length; i++) {
                                if (tablero[i][0] != 0) {
                                    numVertical = tablero[i][0];
                                    if (numVertical == tablero[i][1] && numVertical == tablero[i][2]) {
                                        ganar = true;
                                    }
                                }
                                if (tablero[0][i] != 0) {
                                    numHorizontal = tablero[0][i];
                                    if (numHorizontal == tablero[1][i] && numHorizontal == tablero[2][i]) {
                                        ganar = true;
                                    }
                                }

                            }
                            int numDiagonal = tablero[0][0];
                            if (numDiagonal != 0 && numDiagonal == tablero[1][1] && numDiagonal == tablero[2][2]) {
                                ganar = true;
                            }
                            numDiagonal = tablero[0][2];
                            if (numDiagonal != 0 && numDiagonal == tablero[1][1] && numDiagonal == tablero[2][0]) {
                                ganar = true;
                            }

                            if (!ganar) {
                                for (int i = 0; i < tablero.length; i++) {
                                    for (int j = 0; j < tablero[i].length; j++) {
                                        if (tablero[i][j] == 0) {
                                            lleno = false;
                                        }
                                    }
                                }
                                Servidor.fin = lleno;
                            } else {
                                Servidor.fin = true;
                            }


                            if (!Servidor.fin) {
                                escribir("/Correcto");
                            } else {
                                escribir("/Fin");
                            }

                        } else {
                            escribir("/Ocupado");
                        }

                    } else {
                        salida.writeUTF("/ERRTurno");
                    }
                }


            }





             if(ganar){
                 for (Hilo h : hilos) {
                     DataOutputStream s = new DataOutputStream(h.cliente.getOutputStream());
                     if(h != this){
                        s.writeUTF("/Perder");
                     }else{
                         salida.writeUTF("/Ganar");
                     }
                 }
            }else if(lleno) {
                 escribir("/Llenar");
             }

             hilos.remove(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void escribir(String mensaje){
        try {
            DataOutputStream salida;
            for(Hilo h: hilos){
                salida = new DataOutputStream(h.cliente.getOutputStream());
                salida.writeUTF(mensaje);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void escribir(boolean mensaje){
        try {
            DataOutputStream salida;
            for(Hilo h: hilos){
                salida = new DataOutputStream(h.cliente.getOutputStream());
                salida.writeBoolean(mensaje);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
