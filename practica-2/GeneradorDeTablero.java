import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneradorDeTablero {
    private String      tematica;
    private String[]    palabras;
    private char[][]    tablero;

    // Tamaño del tablero
    private static final int T = 16;
    // Especificadas como [fila, columna]
    private static final Posicion[] DIRECCIONES = {
        new Posicion(-1,  0), new Posicion(-1,  1), // arriba, arriba-derecha
        new Posicion( 0,  1), new Posicion( 1,  1), // derecha, derecha-abajo
        new Posicion( 1,  0), new Posicion( 1, -1), // abajo, abajo-izquierda
        new Posicion( 0, -1), new Posicion(-1, -1)  // izquierda, izquierda arriba
    };

    public GeneradorDeTablero (String tematica, String[] palabras) {
        this.tematica = tematica;
        this.palabras = palabras;
        this.tablero = new char[T][T];
    }
    
    /**
     * Genera el tablero de juego
     */
    public Tablero generarTablero () {
        // Acomodamos todas las palabras en el tablero
        for ( String palabra : palabras ) {
            acomodarPalabra(palabra);
            Tablero tablero = new Tablero(tematica, palabras, this.tablero);
            System.out.println(tablero);
        }
        rellenarPosicionesFaltantes();
        return new Tablero(tematica, palabras, tablero);
    }
    
    /**
     * Acomoda la palabra en alguna posición disponible del tablero.
     * @param palabra
     */
    private void acomodarPalabra (String palabra) {
        ArrayList<Posicion> posiciones = generarPosicionesTablero();
        while ( !posiciones.isEmpty() ) {
            Collections.shuffle(posiciones);
            Posicion actual = posiciones.get(0);
            posiciones.remove(0);
            for ( Posicion direccion : DIRECCIONES ) {
                if ( escribirPalabra(palabra, actual, direccion) ) {
                    return;
                }
            }
        }
        
    }
    
    /**
     * Escribe la palabra caracter por caracter iniciando en el inicio y con la
     * dirección indicados, modifica el tablero en caso de que se pueda escribir
     * la palabra, en otro caso el tablero no cambia.
     * @param palabra
     * @param inicio
     * @param direccion
     * @return true si pudo escribir la palabra, false en otro caso
     */
    private boolean escribirPalabra ( String palabra, Posicion inicio, Posicion direccion ) {
        for ( // Verificamos que podemos escribir la palabra sin interrupciones
                int fil = inicio.getFila(), col = inicio.getColumna(), i = 0 ;
                i < palabra.length();
                fil += direccion.getFila(), col += direccion.getColumna(), i++
            ) {
            if ( !revisarLimites(fil, col) || !revisarPosicion(palabra.charAt(i), fil, col)) {
                return false;
            }
        }
        for ( // Escribimos la palabra
                int fil = inicio.getFila(), col = inicio.getColumna(), i = 0 ;
                i < palabra.length();
                fil += direccion.getFila(), col += direccion.getColumna(), i++
            ) {
            tablero[fil][col] = palabra.charAt(i);
        }
        return true;
    }
    
    /**
    * Revisa que fila y columna estén dentro de los límites del tablero
    * @param fila
    * @param columna
    * @return true si fila y columna están dentro del tablero (indices base 0) false en otro caso
    */
    private boolean revisarLimites ( int fila, int columna ) {
        //System.out.println("Revisando limites: " + fila + " " + columna);
        return fila >= 0 && fila < T && columna >= 0 && columna < T;
    }
    
    /**
     * Verifica que se pueda escribir la letra en la posición indicada por fila y columna
     * @param letra
     * @param fila
     * @param columna
     * @return true si letra coincide con la letra en la posición o si la posición está libre, false en otro caso
     */
    private boolean revisarPosicion ( char letra, int fila, int columna ) {
        /*
        System.out.println("Revisando posicion: {" + fila + ", " + columna + "} , char: " + letra  + "tablero: " + tablero[fila][columna]);
        System.out.print("tablero[fila][columna] == '\\0000' ? : ");
        if ( tablero[fila][columna] == '\u0000' )
            System.out.println("True");
        else 
            System.out.println("False");
        */
        if ( tablero[fila][columna] == '\u0000' ) { // La posicion está vacía
            return true;  
        } else {
            if ( letra == tablero[fila][columna] )  // la letra coincide con la del tablero
                return true;
            else // la letra no coincide con la del tablero
                return false; 
        }
    }
    
    /**
     * Genera ls posiciones posibles en el tablero de T x T
     * @return lista con las posiciones posibles en el tablero de T x T
     */
    private ArrayList<Posicion> generarPosicionesTablero() {
        ArrayList<Posicion> posicionesTablero = new ArrayList();
        for ( int fila = 0 ; fila < T ; fila++ ) {
            for ( int columna = 0 ; columna < T ; columna++ ) {
                posicionesTablero.add(new Posicion(fila, columna));
            }
        }
        return posicionesTablero;
    }
    
    private void rellenarPosicionesFaltantes () {
        Random r = new Random();
        for ( int i = 0 ; i < tablero.length ; i++ )  {
            for ( int j = 0 ; j < tablero.length ; j++ ) {
                if ( tablero[i][j] == '\u0000' ) {
                    char caracterAleatorio = (char)(r.nextInt(26) + 'a');
                    tablero[i][j] = caracterAleatorio;
                }
            }
        }
    }
}