
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Tablero implements Serializable {
    private String      tematica;
    private String[]    palabras;
    private char[][]    tablero;
    private char[][]    tableroImprimir;
    private boolean[]   palabrasDescubiertas;
    private int palabrasRestantes;

    public Tablero(String tematica, String[] palabras, char[][] tablero, int numeroPalabras) {
        this.tematica = tematica;
        this.palabras = palabras;
        this.tablero = tablero;
        this.tableroImprimir = tablero;
        this.palabrasDescubiertas = new boolean[palabras.length];
        this.palabrasRestantes = numeroPalabras;
    }

    public Tablero() {
    }
    
    /**
     * Extrae la cadena entre inicio y fin si es que son posiciones válidas
     * y verifica que la cadena exista en la lista de palabras existentes 
     * en la sopa de letras
     * @return true si la palabra existe en la lista de palabras, false en otro caso
     */
    public boolean descubrirPalabra ( Posicion inicio, Posicion fin ) { // Cambiar el nombre
        int distanciaX = inicio.getColumna() - fin.getColumna();
        int distanciaXAbsoluta = Math.abs(distanciaX);
        int distanciaY = inicio.getFila() - fin.getFila();
        int distanciaYAbsoluta = Math.abs(distanciaY);
        
        int longitudCadena = Math.max(distanciaXAbsoluta, distanciaYAbsoluta) + 1;
        
        char[] charsCadena = new char[longitudCadena];
        
        int direccionColumna, direccionFila;
        if ( distanciaX == 0 ) {
            direccionColumna = 0;
        } else if ( distanciaX < 0 ) { // Va hacia la derecha
            direccionColumna = 1;
        } else { // Va hacia la izquierda
            direccionColumna = -1;
        }
        if ( distanciaY == 0 ) {
            direccionFila = 0;
        } else if ( distanciaY < 0 ) { // Va hacia la derecha
            direccionFila = 1;
        } else { // Va hacia la izquierda
            direccionFila = -1; 
        }
        for ( 
                int fil = inicio.getFila(), col = inicio.getColumna(), i = 0 ;
                fil < tablero.length && col < tablero.length && i < longitudCadena ;
                fil += direccionFila , col += direccionColumna, i++
            ) {
            charsCadena[i] = tablero[fil][col];
        }
        String palabraExtraida = new String(charsCadena);
        System.out.println("Palabra Extraída: "+ palabraExtraida);
        //System.out.println("Longitud encontrada: " + longitudCadena);
        boolean descubrioPalabra = existePalabra(palabraExtraida);
        if ( descubrioPalabra ) {
            palabrasRestantes--;
            for ( 
                int fil = inicio.getFila(), col = inicio.getColumna(), i = 0 ;
                fil < tablero.length && col < tablero.length && i < longitudCadena;
                fil += direccionFila , col += direccionColumna, i++
            ) {
                tableroImprimir[fil][col] = '*';
            }
        }
        return descubrioPalabra;
    }
    
    /**
     * Se verifica que una palabra esté en la lista de palabras
     * @param palabra
     * @return 
     */
    public boolean existePalabra ( String palabra ) {
        for ( int i = 0 ; i < palabras.length ; i++ ) { 
            if ( palabra.equals(palabras[i]) ) {
                palabrasDescubiertas[i] = true;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString () {
        String textoTablero = "    ";
        for ( int i = 0 ; i < tableroImprimir.length ; i++ ) { 
            if ( i < 10 )
                textoTablero += i + "  ";
            else
                textoTablero += i + " ";
        }
        textoTablero += "\n";
        for ( int i = 0 ; i < tableroImprimir.length ; i++ ) {
            if ( i < 10 )
                textoTablero += i + "  ";
            else 
                textoTablero += i + " ";
            for ( int j = 0 ; j < tableroImprimir.length ; j++ ) {
                if ( j == 0 ) {
                    textoTablero += "|" + tableroImprimir[i][j] + "  ";
                } else if ( j !=  tableroImprimir.length - 1 ) {
                    textoTablero += tableroImprimir[i][j] + "  ";
                } else {
                    if ( i < 10 )
                        textoTablero += tableroImprimir[i][j] + "|   " + i;
                    else 
                        textoTablero += tableroImprimir[i][j] + "|  " + i;
                }
            }
            textoTablero += "\n";
        }
        textoTablero += "    ";
        for ( int i = 0 ; i < tableroImprimir.length ; i++ ) { 
            if ( i < 10 )
                textoTablero += i + "  ";
            else
                textoTablero += i + " ";
        }
        textoTablero += "\n";
        return textoTablero;
    }
    
    public int palabrasFaltantes() {
        return palabrasRestantes;
    }
    
    public static void main(String[] args) {
        final String[] tematicas = {"Redes", "Geografia", "Mascotas"};
        final String[] palabrasTematica1 = {"Protocolos", "Internet"};
        GeneradorDeTablero g = new GeneradorDeTablero(tematicas[0], palabrasTematica1);
        Tablero t = g.generarTablero();
        System.out.println(t);
    }
}