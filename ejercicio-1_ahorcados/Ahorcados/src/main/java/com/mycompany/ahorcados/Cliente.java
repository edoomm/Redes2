package com.mycompany.ahorcados;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Scanner lector;
    
    public Cliente () {
        ois = null;
        oos = null;
        lector = new Scanner(System.in);
    }
    
    private void elegirDificultad () {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Ingresa la dificultad: \n0) Facil\n1) Intermedio\n2) Dificil\n");
            int dificultad = lector.nextInt();
            dos.writeInt(dificultad);
            dos.flush();
            System.out.println(dificultad);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    private void cerrarFlujos () {
        try {
            oos.close();
            ois.close();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    private void iniciarJuego () {
        elegirDificultad();
        try {
            Paquete paquete = new Paquete();
            lector.nextLine();
            while ( true ) {
                paquete = (Paquete)ois.readObject();
                
                System.out.println(paquete.getMensaje());
                if ( paquete.getCaracter() == '?' ) {
                    break;
                }
                String palabraOculta = paquete.getPalabra();
                for ( int i = 0 ; i < palabraOculta.length() ; i++ ) {
                    System.out.print(palabraOculta.charAt(i) + " ");
                }
                System.out.println();
                //System.out.println(paquete.getPalabra());
                
                System.out.println("Ingrese el caracter que desee descubrir: ");
                String entrada = lector.nextLine();
                System.out.println("Entrada: " + entrada);
                char caracter;
                if ( entrada.length() > 0 ) {
                    caracter = entrada.charAt(0);
                } else {
                    System.out.println("No se ingresó nada");
                    caracter = '¿';
                }
                System.out.println("Enviando intento...\n");
                paquete.setCaracter(caracter);
                
                
                oos.writeObject(paquete);
                oos.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
        
    public void conectarse () {
        try {
            socket = new Socket("localhost", 1309);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            if ( socket != null )
                iniciarJuego();
            else
                System.out.println("Error al conectarse con el servidor :(");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Cliente jugador = new Cliente();
        jugador.conectarse();
    }
    
}
