
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Tablero implements Serializable {
    private String      tematica;
    private String[]    palabras;
    private char[][]    tablero;

    public Tablero(String tematica, String[] palabras, char[][] tablero) {
        this.tematica = tematica;
        this.palabras = palabras;
        this.tablero = tablero;
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
        // TODO: Hacer verificaciones de que las posiciones sean válidas
        // TODO: Hacer bien la función xD
        int distanciaX = inicio.getColumna() - fin.getColumna();
        int distanciaXAbsoluta = Math.abs(distanciaX);
        int distanciaY = inicio.getFila() - fin.getFila();
        int distanciaYAbsoluta = Math.abs(distanciaY);
        
        int longitudCadena = Math.max(distanciaXAbsoluta, distanciaYAbsoluta);
        
        char[] charsCadena = new char[longitudCadena];
        
        int direccionColumna, direccionFila;
        if ( distanciaX == 0 ) {
            direccionColumna = 0;
        } else if ( distanciaX > 0 ) { // Va hacia la derecha
            direccionColumna = 1;
        } else { // Va hacia la izquierda
            direccionColumna = -1; 
        }
        if ( distanciaY == 0 ) {
            direccionFila = 0;
        } else if ( distanciaY > 0 ) { // Va hacia la derecha
            direccionFila = 1;
        } else { // Va hacia la izquierda
            direccionFila = -1; 
        }
        System.out.println("Direccion fila : " + direccionFila + " Direccion columna: " + direccionColumna);
        for ( 
                int fil = inicio.getFila(), col = inicio.getColumna(), i = 0 ; 
                fil < longitudCadena && col < longitudCadena && i < longitudCadena ;
                fil += direccionFila , col += direccionColumna, i++
            ) {
            charsCadena[i] = tablero[fil][col];
            System.out.print(charsCadena[i]);
        }
        System.out.println("");
        String palabraExtraida = charsCadena.toString();
        System.out.println("Palabra encontrada: " + palabraExtraida);
        return existePalabra(palabraExtraida);
    }
    
    /**
     * Se verifica que una palabra esté en la lista de palabras
     * @param palabra
     * @return 
     */
    public boolean existePalabra ( String palabra ) {
        for ( String cadena : palabras ) {
            if ( cadena.equals(palabra) ) 
                return true;
        }
        return false;
    }
    
    @Override
    public String toString () {
        String textoTablero = "";
        for ( int i = 0 ; i < tablero.length ; i++ ) {
            for ( int j = 0 ; j < tablero.length ; j++ ) {
                if ( j == 0 ) {
                    textoTablero += "|" + tablero[i][j] + " ";
                } else if ( j !=  tablero.length - 1 ) {
                    textoTablero += tablero[i][j] + " ";
                } else {
                    textoTablero += tablero[i][j] + "| ";
                }
            }
            textoTablero += "\n";
        }
        return textoTablero;
    }
    
    public static void main(String[] args) {
        final String[] tematicas = {"Redes", "Geografia", "Mascotas"};
        final String[] palabrasTematica1 = {"Protocolos", "Internet"};
        GeneradorDeTablero g = new GeneradorDeTablero(tematicas[0], palabrasTematica1);
        Tablero t = g.generarTablero();
        System.out.println(t);
        t.descubrirPalabra(new Posicion(14, 11), new Posicion(5, 11));
    }
}