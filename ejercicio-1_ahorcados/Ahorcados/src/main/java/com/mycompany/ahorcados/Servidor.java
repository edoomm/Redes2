package com.mycompany.ahorcados;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;



public class Servidor {
    private ServerSocket servidor;
    private Socket cliente;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ArrayList<ArrayList<String>> palabras;
    private int dificultad;
    private int intentos;
    
    private void inicializarPalabras () {
        palabras = new ArrayList<>();
        ArrayList<String> facil = new ArrayList<>();
        facil.add("perro");
        facil.add("mano");
        facil.add("reloj");
        palabras.add(facil);
        ArrayList<String> medio = new ArrayList<>();
        medio.add("licuado");
        medio.add("audifonos");
        medio.add("libreta");
        palabras.add(medio);
        ArrayList<String> dificil = new ArrayList<>();
        dificil.add("rehabilitado");
        dificil.add("eliminador");
        dificil.add("decodificador");
        palabras.add(dificil);
    }
    
    public Servidor () {
        inicializarPalabras();
    }
    
    private void obtenerDificultad () {
        try {
            DataInputStream dis = new DataInputStream(cliente.getInputStream());
            System.out.println("Esperando la dificultad elegida...");
            dificultad = dis.readInt();
            System.out.println("Dificultad elegida: " + dificultad);
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
    
    private void guardarPuntaje (String ipJugador, Cronometro tiempo, String resultado) {
        
            

            String formato = "Cliente: " + ipJugador + " " + resultado 
                        + "\nTiempo: " + tiempo.getHorasTranscurridas() + " hrs. "
                        + tiempo.getMinutosTranscurridos() + " mins. "
                        + tiempo.getSegundosTranscurridos() + " segs. " + "\n\n";
            System.out.println("Escribiendo el archivo");
        try {
            Files.write(Paths.get("registro_partidas.txt"), formato.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }    
        
        
    }
    
    private void iniciarJuego () {
        try {
            obtenerDificultad();
            intentos = 5;
            
            int indicePalabra = (int)Math.random() % palabras.get(dificultad).size();
            PalabraOculta palabraOculta = new PalabraOculta(palabras.get(dificultad).get(indicePalabra));
            Cronometro cronometro = new Cronometro();
            cronometro.iniciar();
            while ( true ) {
                Paquete paquete = new Paquete();
                paquete.setMensaje("Adivina caracter por caracter la palabra\n"
                        + "Tienes " + intentos + " intentos.\n");
                paquete.setPalabra(palabraOculta.getPalabraCodificada());
                oos.writeObject(paquete);
                System.out.println("Esperando respuesta del cliente...");
                Paquete paqueteRecibido = (Paquete)ois.readObject();
                System.out.println("Caracter ingresado: " + paqueteRecibido.getCaracter());
                if ( !palabraOculta.descubrirCaracter(paqueteRecibido.getCaracter()) ) {
                    intentos--;
                } else {
                    if ( palabraOculta.isDescubierta() ) {
                        cronometro.detener();
                        paquete = new Paquete();
                        paquete.setMensaje("FELICITACIONES!\n"
                        + "La palabra es: " + palabraOculta.getPalabra() + ".\n"
                        + "Tu tiempo fue: " + cronometro.getHorasTranscurridas() + " hrs. "
                            + cronometro.getMinutosTranscurridos() + "mins."
                            + cronometro.getSegundosTranscurridos() + "segs." + "\n" );
                        paquete.setCaracter('?');
                        paquete.setPalabra(palabraOculta.getPalabra());
                        
                        guardarPuntaje(cliente.getInetAddress().toString(), cronometro, "Ganador");
                        oos.writeObject(paquete);
                        cerrarFlujos();
                        
                        break;
                    }
                }
                if ( intentos == 0 ) {
                    cronometro.detener();
                    System.out.println("Fin del juego");
                    paquete = new Paquete();
                    paquete.setMensaje("SUERTE PARA LA PRÓXIMA!\n"
                    + "La palabra es: " + palabraOculta.getPalabra() + ".\n"
                    + "Tu tiempo fue: " + cronometro.getHorasTranscurridas() + " hrs. "
                        + cronometro.getMinutosTranscurridos() + " mins. "
                        + cronometro.getSegundosTranscurridos() + " segs. " + "\n" );
                    paquete.setCaracter('?');
                    oos.writeObject(paquete);
                    guardarPuntaje(cliente.getInetAddress().toString(), cronometro, "Perdedor");
                    
                    cerrarFlujos();
                    break;
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } 
    }
    
    public void iniciar () {
        try {
            servidor = new ServerSocket(1309);
            System.out.println("Esperando jugador...");
            cliente = servidor.accept();
            ois = new java.io.ObjectInputStream(cliente.getInputStream());
            oos = new java.io.ObjectOutputStream(cliente.getOutputStream());
            iniciarJuego();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Servidor servidor = new Servidor ();
        servidor.iniciar();
    }
}
